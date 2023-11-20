package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoups;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class HotpotBlockEntity extends AbstractChopstickInteractiveBlockEntity implements ITickableTileEntity {
    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    private final NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HotpotContents.getEmptyContent().get());
    private IHotpotSoup soup = HotpotSoups.getEmptySoup().get();
    public float renderedWaterLevel = -1f;
    private float waterLevel = 0f;
    private int time = 0;
    private boolean infiniteWater = false;
    private boolean canBeRemoved = true;

    public HotpotBlockEntity() {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get());
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
    public ItemStack tryPlaceContentViaChopstick(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
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
        double degree = (time / 20f / 60f) * 360f + sectionSize / 2f;

        int rootSection = (int) Math.floor((degree % 360f) / sectionSize);
        int contentSection = hitSection - rootSection;

        return contentSection < 0 ? 8 + contentSection : contentSection;
    }

    public void onRemove(BlockPosWithLevel pos) {
        for (int i = 0; i < contents.size(); i++) {
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
    public void load(BlockState state, CompoundNBT compoundTag) {
        super.load(state, compoundTag);
        handleLoadTag(compoundTag);
    }

    public void handleLoadTag(CompoundNBT compoundTag) {
        canBeRemoved = !compoundTag.contains("CanBeRemoved", Constants.NBT.TAG_ANY_NUMERIC) || compoundTag.getBoolean("CanBeRemoved");
        infiniteWater = compoundTag.contains("InfiniteWater", Constants.NBT.TAG_ANY_NUMERIC) && compoundTag.getBoolean("InfiniteWater");
        time = compoundTag.contains("Time", Constants.NBT.TAG_ANY_NUMERIC) ? compoundTag.getInt("Time") : 0;
        waterLevel = compoundTag.contains("WaterLevel", Constants.NBT.TAG_FLOAT) ? compoundTag.getFloat("WaterLevel") : 0f;

        if (compoundTag.contains("Soup", Constants.NBT.TAG_COMPOUND)) {
            soup = IHotpotSoup.loadSoup(compoundTag.getCompound("Soup"));
        }

        if (compoundTag.contains("Contents", Constants.NBT.TAG_LIST)) {
            contents.clear();
            IHotpotContent.loadAll(compoundTag.getList("Contents", Constants.NBT.TAG_COMPOUND), contents);
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleLoadTag(pkt.getTag());
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag = super.save(compoundTag);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putFloat("WaterLevel", waterLevel);

        compoundTag.put("Soup", IHotpotSoup.save(soup));
        compoundTag.put("Contents", IHotpotContent.saveAll(contents));

        return compoundTag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compoundTag = new CompoundNBT();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putFloat("WaterLevel", waterLevel);

        if (contentChanged) {
            compoundTag.put("Soup", IHotpotSoup.save(soup));
            compoundTag.put("Contents", IHotpotContent.saveAll(contents));

            contentChanged = false;
        }

        return new SUpdateTileEntityPacket(getBlockPos(), 1, compoundTag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundTag = super.getUpdateTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
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

    public boolean isInfiniteWater() {
        return infiniteWater;
    }

    public void setInfiniteWater(boolean infiniteWater) {
        this.infiniteWater = infiniteWater;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    public void setCanBeRemoved(boolean canBeRemoved) {
        this.canBeRemoved = canBeRemoved;
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
        if (selfPos.getBlockEntity() instanceof HotpotBlockEntity && pos.getBlockEntity() instanceof HotpotBlockEntity) {
            return ((HotpotBlockEntity) selfPos.getBlockEntity()).getSoup().getID().equals(((HotpotBlockEntity) pos.getBlockEntity()).getSoup().getID());
        }

        return false;
    }

    public static int getPosSection(BlockPos blockPos, Vector3d pos) {
        Vector3d vec = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        double x = vec.x() - 0.5f;
        double z = vec.z() - 0.5f;

        double sectionSize = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + sectionSize / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / sectionSize);
    }

    @Override
    public void tick() {
        if (level.isClientSide) return;

        BlockPosWithLevel selfPos = new BlockPosWithLevel(getLevel(), getBlockPos());

        time++;
        synchronizeSoup(selfPos);
        soupSynchronized = false;

        waterLevel = soup.getWaterLevel(this, selfPos);

        int tickSpeed = soup.getContentTickSpeed(this, selfPos);

        if (tickSpeed < 0) {
            if (time % (-tickSpeed) == 0) {
                tickContent(this, selfPos);
            }
        } else {
            int i = 0;

            do {
                tickContent(this, selfPos);
            } while (++i < tickSpeed);
        }

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        setChanged();
    }
}