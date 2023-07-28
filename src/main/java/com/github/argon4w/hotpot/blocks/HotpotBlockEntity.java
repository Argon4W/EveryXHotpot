package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoups;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class HotpotBlockEntity extends AbstractChopstickInteractiveBlockEntity {
    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    private final NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HotpotContents.getEmptyContent().get());
    private IHotpotSoup soup = HotpotSoups.getEmptySoup().get();
    public float renderedWaterLevel = -1f;
    private float waterLevel = 0f;
    private int time = 0;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    private void tryFindEmptyContent(int hitSection, BlockPosWithLevel selfPos, TriConsumer<Integer, HotpotBlockEntity, BlockPosWithLevel> consumer) {
        new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos))
                .getFirst(10, (hotpotBlockEntity, pos) -> hotpotBlockEntity.hasEmptyContent(), (hotpotBlockEntity, pos) -> {
                    Stream.of(getContentSection(hitSection), 0, 1, 2, 3, 4, 5, 6, 7)
                            .filter(hotpotBlockEntity::isEmptyContent)
                            .findFirst().ifPresent(section -> consumer.accept(section, hotpotBlockEntity, pos));
                });
    }

    @Override
    public ItemStack tryPlaceContentViaChopstick(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        soup.interact(hitSection, player, hand, itemStack, this, selfPos).ifPresent(content -> tryFindEmptyContent(hitSection, selfPos, (section, hotpotBlockEntity, pos) -> {
            hotpotBlockEntity.placeContent(section, content, pos);
        }));
    }

    public void tryPlaceContent(int hitSection, IHotpotContent content, BlockPosWithLevel selfPos) {
        tryFindEmptyContent(hitSection, selfPos, (section, hotpotBlockEntity, pos) -> hotpotBlockEntity.placeContent(section, content, pos));
    }

    private void placeContent(int section, IHotpotContent content, BlockPosWithLevel pos) {
        Optional<IHotpotContent> remappedContent = soup.remapContent(content, this, pos);
        contents.set(section, remappedContent.orElseGet(HotpotContents.getEmptyContent()));

        HotpotSoups.ifMatchSoup(this, pos, supplier -> setSoup(supplier.apply(this, pos), pos));
        markDataChanged();
    }

    public void consumeContent(UnaryOperator<IHotpotContent> operator) {
        contents.replaceAll(operator);
        markDataChanged();
    }

    public void consumeAllContents() {
        consumeContent(ignored -> HotpotContents.getEmptyContent().get());
    }

    private void synchronizeSoup(BlockPosWithLevel selfPos) {
        if (soupSynchronized) {
            return;
        }

        soup.getSynchronizer(this, selfPos).ifPresent(synchronizer -> {
            Map<HotpotBlockEntity, BlockPosWithLevel> neighbors = new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos) && !hotpotBlockEntity.soupSynchronized).getAll();

            neighbors.forEach((hotpotBlockEntity, pos) -> {
                hotpotBlockEntity.soupSynchronized = true;
                synchronizer.collect(hotpotBlockEntity, pos);
            });

            neighbors.forEach((hotpotBlockEntity, pos) -> {
                synchronizer.integrate(neighbors.size(), hotpotBlockEntity, pos);
            });
        });
    }

    public void tryTakeOutContentViaHand(int hitSection, BlockPosWithLevel pos) {
        int contentSection = getContentSection(hitSection);
        IHotpotContent content = contents.get(contentSection);

        if (!(content instanceof HotpotEmptyContent)) {
            soup.takeOutContentViaHand(content, soup.takeOutContentViaChopstick(content, content.takeOut(this, pos), this, pos), this, pos);
            contents.set(contentSection, HotpotContents.getEmptyContent().get());

            markDataChanged();
        }
    }

    @Override
    public ItemStack tryTakeOutContentViaChopstick(int hitSection, BlockPosWithLevel pos) {
        int contentSection = getContentSection(hitSection);
        IHotpotContent content = contents.get(contentSection);

        if (!(content instanceof HotpotEmptyContent)) {
            ItemStack itemStack = soup.takeOutContentViaChopstick(content, content.takeOut(this, pos), this, pos);

            contents.set(contentSection, HotpotContents.getEmptyContent().get());
            markDataChanged();

            return itemStack;
        }

        return ItemStack.EMPTY;
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

            soup.takeOutContentViaHand(content, content.takeOut(this, pos), this, pos);
            contents.set(i, HotpotContents.getEmptyContent().get());
        }

        markDataChanged();
    }

    public void setSoup(IHotpotSoup soup, BlockPosWithLevel pos) {
        this.soup = soup;
        markDataChanged();
        pos.markAndNotifyBlock();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        time = compoundTag.contains("Time", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Time") : 0;
        waterLevel = compoundTag.contains("WaterLevel", Tag.TAG_FLOAT) ?compoundTag.getFloat("WaterLevel") : 0f;

        if (compoundTag.contains("Soup", Tag.TAG_COMPOUND)) {
            soup = IHotpotSoup.loadSoup(compoundTag.getCompound("Soup"));
        }

        if (compoundTag.contains("Contents", Tag.TAG_LIST)) {
            contents.clear();
            IHotpotContent.loadAll(compoundTag.getList("Contents", Tag.TAG_COMPOUND), contents);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.putInt("Time", time);
        compoundTag.putFloat("WaterLevel", waterLevel);

        compoundTag.put("Soup", IHotpotSoup.save(soup));
        compoundTag.put("Contents", IHotpotContent.saveAll(contents));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (entity) -> {
            CompoundTag compoundTag = new CompoundTag();

            compoundTag.putInt("Time", time);
            compoundTag.putFloat("WaterLevel", waterLevel);

            if (contentChanged) {
                compoundTag.put("Soup", IHotpotSoup.save(soup));
                compoundTag.put("Contents", IHotpotContent.saveAll(contents));

                contentChanged = false;
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

        compoundTag.put("Soup", IHotpotSoup.save(soup));
        compoundTag.put("Contents", IHotpotContent.saveAll(contents));

        return compoundTag;
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public NonNullList<IHotpotContent> getContents() {
        return contents;
    }

    public boolean hasEmptyContent() {
        return contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public boolean isEmptyContent(int section) {
        return getContent(section) instanceof HotpotEmptyContent;
    }

    public IHotpotContent getContent(int section) {
        return contents.get(section);
    }

    public IHotpotSoup getSoup() {
        return soup;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public int getTime() {
        return time;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity blockEntity) {
        BlockPosWithLevel selfPos = new BlockPosWithLevel(level, pos);

        blockEntity.time ++;
        blockEntity.synchronizeSoup(selfPos);
        blockEntity.soupSynchronized = false;

        blockEntity.waterLevel = blockEntity.soup.getWaterLevel(blockEntity, selfPos);

        int tickSpeed = blockEntity.soup.getContentTickSpeed(blockEntity, selfPos);

        if (tickSpeed < 0) {
            if (blockEntity.time % (- tickSpeed) == 0) {
                tickContent(blockEntity, selfPos);
            }
        } else {
            int i = 0;

            do {
                tickContent(blockEntity, selfPos);
            } while (++ i < tickSpeed);
        }

        level.sendBlockUpdated(pos, state, state, 3);
        blockEntity.setChanged();
    }

    public static void tickContent(HotpotBlockEntity blockEntity, BlockPosWithLevel selfPos) {
        for (IHotpotContent content : blockEntity.contents) {
            if (content.tick(blockEntity, selfPos)) {
                blockEntity.soup.contentUpdate(content, blockEntity, selfPos);
                blockEntity.markDataChanged();
            }
        }
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
