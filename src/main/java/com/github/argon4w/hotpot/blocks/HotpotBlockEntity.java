package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class HotpotBlockEntity extends BlockEntity implements IHotpotNeighborFinder {
    private boolean shouldSendContentUpdate = true;
    private boolean shouldUpdateSoupWaterLevel = false;

    private final NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty").get());
    private IHotpotSoup soup = HotpotDefinitions.HOTPOT_SOUP_TYPES.get("Empty").get();
    private int time = 0;
    private float waterLevel = 0f;
    public float renderedWaterLevel = -1f;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    private boolean tryFindEmptyContent(int hitSection, BlockPosWithLevel selfPos, TriConsumer<Integer, HotpotBlockEntity, BlockPosWithLevel> consumer) {
        return tryFindNeighbor(
                10, selfPos,
                (neighborFinder, pos) -> neighborFinder instanceof HotpotBlockEntity hotpotBlockEntity && hotpotBlockEntity.contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent),
                (neighborFinder, pos) -> {
                    int section = getContentSection(hitSection);
                    HotpotBlockEntity hotpotBlockEntity = (HotpotBlockEntity) neighborFinder;

                    if (hotpotBlockEntity.contents.get(section) instanceof HotpotEmptyContent) {
                        consumer.accept(section, hotpotBlockEntity, pos);
                    } else {
                        for (int i = 0; i < hotpotBlockEntity.contents.size(); i++) {
                            if (hotpotBlockEntity.contents.get(i) instanceof HotpotEmptyContent) {
                                consumer.accept(i, hotpotBlockEntity, pos);
                                break;
                            }
                        }
                    }

                    return true;
                }, (neighborFinder, pos) -> false);
    }

    public boolean tryPlaceContentViaInteraction(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        Optional<IHotpotContent> content = soup.interact(hitSection, player, hand, itemStack, this, selfPos);

        return content.isPresent() && tryFindEmptyContent(hitSection, selfPos, (section, hotpotBlockEntity, pos) -> {
            hotpotBlockEntity.placeContent(section, content.get(), pos);
        });
    }

    public boolean tryPlaceContent(int hitSection, IHotpotContent content, BlockPosWithLevel selfPos) {
        return tryFindEmptyContent(hitSection, selfPos, (section, hotpotBlockEntity, pos) -> hotpotBlockEntity.placeContent(section, content, pos));
    }

    private void placeContent(int section, IHotpotContent content, BlockPosWithLevel pos) {
        Optional<IHotpotContent> remappedContent = soup.remapContent(content, this, pos);
        contents.set(section, remappedContent.orElseGet(HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty")));

        HotpotDefinitions.HOTPOT_SOUP_MATCHES.forEach(((predicate, supplier) -> {
            if (predicate.test(this, pos)) {
                setSoup(supplier.get(this, pos), pos);
            }
        }));

        markDataChanged();
    }

    private void updateNeighborSoupWaterLevel(BlockPosWithLevel selfPos) {
        if (shouldUpdateSoupWaterLevel) return;

        AtomicReference<Float> totalWaterLevel = new AtomicReference<>(0f);
        LinkedList<BlockPosWithLevel> neighborsWithSameSoup = new LinkedList<>();

        tryFindNeighbor(
                -1, selfPos,
                (neighborFinder, pos) -> {
                    if (neighborFinder instanceof HotpotBlockEntity hotpotBlockEntity && isSameSoup(selfPos, pos) && !hotpotBlockEntity.shouldUpdateSoupWaterLevel) {
                        hotpotBlockEntity.shouldUpdateSoupWaterLevel = true;
                        hotpotBlockEntity.soup.tick(hotpotBlockEntity, pos);

                        totalWaterLevel.updateAndGet(v -> v + hotpotBlockEntity.soup.getWaterLevel(hotpotBlockEntity, pos) + hotpotBlockEntity.soup.getOverflowWaterLevel(hotpotBlockEntity, pos));
                        neighborsWithSameSoup.add(pos);

                        hotpotBlockEntity.soup.discardOverflowWaterLevel(hotpotBlockEntity, pos);
                    }

                    return false;
                },
                (neighborFinder, pos) -> 0f,
                (neighborFinder, pos) -> 0f
        );

        float averageWaterLevel = totalWaterLevel.get() / neighborsWithSameSoup.size();

        for (BlockPosWithLevel neighbor : neighborsWithSameSoup) {
            if (neighbor.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity) {
                hotpotBlockEntity.soup.setWaterLevel(hotpotBlockEntity, neighbor, Math.max(0f, Math.min(1f,averageWaterLevel)));
            }
        }
    }

    public void takeOutContent(int hitSection, BlockPosWithLevel pos) {
        int contentSection = getContentSection(hitSection);
        IHotpotContent content = contents.get(contentSection);

        if (!(content instanceof HotpotEmptyContent)) {
            soup.takeOutContent(content.takeOut(this, pos), this, pos);
            contents.set(contentSection, HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty").get());
            markDataChanged();
        }
    }

    public int getContentSection(int hitSection) {
        double sectionSize = (360f / 8f);
        double degree =  (time / 20f / 60f) * 360f + sectionSize / 2f;

        int rootSection = (int) Math.floor((degree % 360f) / sectionSize);
        int contentSection = hitSection - rootSection;

        return contentSection < 0 ? 8 + contentSection : contentSection;
    }

    public void onRemove(BlockPosWithLevel pos) {
        for (int i = 0; i < contents.size(); i ++) {
            IHotpotContent content = contents.get(i);

            soup.takeOutContent(content.takeOut(this, pos), this, pos);
            contents.set(i, HotpotDefinitions.HOTPOT_CONTENT_TYPES.get("Empty").get());
        }
    }

    public void setSoup(IHotpotSoup soup, BlockPosWithLevel pos) {
        this.soup = soup;
        markDataChanged();
        pos.markAndNotifyBlock();
    }

    private void saveContents(CompoundTag compoundTag) {
        ListTag list = new ListTag();

        for(int i = 0; i < contents.size(); ++i) {
            IHotpotContent content = contents.get(i);
            CompoundTag tag = new CompoundTag();

            tag.putString("Type", content.getID());
            tag.putByte("Slot", (byte) i);

            list.add(content.save(tag));
        }

        compoundTag.put("Contents", list);
    }

    private void saveSoup(CompoundTag compoundTag) {
        CompoundTag soupTag = new CompoundTag();
        soupTag.putString("Type", soup.getID());
        compoundTag.put("Soup", soup.save(soupTag));
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        time = compoundTag.contains("Time", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Time") : 0;
        waterLevel = compoundTag.contains("WaterLevel", Tag.TAG_FLOAT) ?compoundTag.getFloat("WaterLevel") : 0f;

        CompoundTag soupTag;
        if (compoundTag.contains("Soup", Tag.TAG_COMPOUND) && (soupTag = compoundTag.getCompound("Soup")).contains("Type", Tag.TAG_STRING)) {
            String type = soupTag.getString("Type");
            IHotpotSoup soup = HotpotDefinitions.HOTPOT_SOUP_TYPES.get(type).get();

            if (soup.isValid(soupTag)) {
                soup.load(soupTag);
                this.soup = soup;
            } else {
                this.soup = HotpotDefinitions.HOTPOT_SOUP_TYPES.get("Empty").get();
            }
        }

        if (compoundTag.contains("Contents")) {
            contents.clear();

            ListTag list = compoundTag.getList("Contents", Tag.TAG_COMPOUND);

            for(int i = 0; i < list.size(); ++i) {
                CompoundTag tag = list.getCompound(i);

                if (!tag.contains("Slot", Tag.TAG_BYTE) || !tag.contains("Type", Tag.TAG_STRING)) {
                    continue;
                }

                int slot = tag.getByte("Slot") & 255;
                String type = tag.getString("Type");
                Supplier<IHotpotContent> supplier = HotpotDefinitions.HOTPOT_CONTENT_TYPES.get(type);

                if (slot >= 0 && slot < contents.size() && supplier != null) {
                    IHotpotContent content = supplier.get();

                    if (content.isValid(tag)) {
                        content.load(tag);
                        contents.set(slot, content);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.putInt("Time", time);
        compoundTag.putFloat("WaterLevel", waterLevel);

        saveSoup(compoundTag);
        saveContents(compoundTag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (entity) -> {
            CompoundTag compoundTag = new CompoundTag();

            compoundTag.putInt("Time", time);
            compoundTag.putFloat("WaterLevel", waterLevel);

            if (shouldSendContentUpdate) {
                saveSoup(compoundTag);
                saveContents(compoundTag);

                shouldSendContentUpdate = false;
            }

            return compoundTag;
        });
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();

        compoundTag.putInt("Time", time);
        compoundTag.putFloat("WaterLevel", waterLevel);

        saveSoup(compoundTag);
        saveContents(compoundTag);

        return compoundTag;
    }

    public IHotpotSoup getSoup() {
        return soup;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public NonNullList<IHotpotContent> getContents() {
        return contents;
    }

    public void markShouldSendContentUpdate() {
        shouldSendContentUpdate = true;
    }

    public void markDataChanged() {
        markShouldSendContentUpdate();
        setChanged();
    }

    public int getTime() {
        return time;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity blockEntity) {
        BlockPosWithLevel selfPos = new BlockPosWithLevel(level, pos);

        blockEntity.time ++;
        blockEntity.updateNeighborSoupWaterLevel(selfPos);
        blockEntity.shouldUpdateSoupWaterLevel = false;
        blockEntity.waterLevel = blockEntity.soup.getWaterLevel(blockEntity, selfPos);

        float tickSpeed = blockEntity.soup.getContentTickSpeed(blockEntity, selfPos);

        if (tickSpeed < 1) {
            int inverseTickSpeed = (int) (1 / tickSpeed);

            if (blockEntity.time % inverseTickSpeed == 0) {
                for (IHotpotContent content : blockEntity.contents) {
                    blockEntity.shouldSendContentUpdate |= content.tick(blockEntity, selfPos);
                }
            }
        } else {
            for (int i = 0; i < Math.round(tickSpeed); i ++) {
                for (IHotpotContent content : blockEntity.contents) {
                    blockEntity.shouldSendContentUpdate |= content.tick(blockEntity, selfPos);
                }
            }
        }

        level.sendBlockUpdated(pos, state, state, 3);
        blockEntity.setChanged();
    }

    public static boolean isSameSoup(BlockPosWithLevel selfPos, BlockPosWithLevel pos) {
        if (selfPos.getBlockEntity() instanceof HotpotBlockEntity selfBlockEntity && pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity) {
            return selfBlockEntity.getSoup().getID().equals(hotpotBlockEntity.getSoup().getID());
        }

        return false;
    }

    public static int getPosSection(BlockPos blockPos, Vec3 pos) {
        Vec3 vec = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double x = vec.x() - 0.5f;
        double z = vec.z() - 0.5f;

        double sectionSize = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + sectionSize / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / sectionSize);
    }
}
