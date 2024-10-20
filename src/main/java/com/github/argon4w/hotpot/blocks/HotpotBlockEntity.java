package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotTablewareContainer;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.contents.IHotpotPickableContent;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupComponentSynchronizer;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotIngredientRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class HotpotBlockEntity extends AbstractHotpotCodecBlockEntity<HotpotBlockEntity.Data, HotpotBlockEntity.PartialData> implements IHotpotTablewareContainer {
    public static final RecipeManager.CachedCheck<HotpotIngredientRecipeInput, HotpotSoupIngredientRecipe> INGREDIENT_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get());
    public static final double ROTATING_CONTENT_INTERVAL = 360.0 / 8.0;

    public static final Codec<Data> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    Codec.BOOL.fieldOf("can_consume_contents").forGetter(Data::canConsumeContents),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(Data::canBeRemoved),
                    Codec.BOOL.fieldOf("infinite_water").forGetter(Data::isInfiniteWater),
                    Codec.INT.fieldOf("time").forGetter(Data::getTime),
                    Codec.INT.fieldOf("velocity").forGetter(Data::getVelocity),
                    Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(Data::getSynchronizedWaterLevel),
                    HotpotComponentSoupType.CODEC.fieldOf("soup").forGetter(Data::getSoup),
                    HotpotContentSerializers.HOTPOT_CONTENTS_CODEC.fieldOf("contents").forGetter(Data::getContents)
            ).apply(data, Data::new))
    );

    public static final Codec<PartialData> PARTIAL_CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    Codec.BOOL.fieldOf("can_consume_contents").forGetter(PartialData::canConsumeContents),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(PartialData::canBeRemoved),
                    Codec.BOOL.fieldOf("infinite_water").forGetter(PartialData::infiniteWater),
                    Codec.INT.fieldOf("time").forGetter(PartialData::time),
                    Codec.INT.fieldOf("velocity").forGetter(PartialData::velocity),
                    Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(PartialData::synchronizedWaterLevel),
                    HotpotComponentSoupType.PARTIAL_CODEC.fieldOf("soup").forGetter(PartialData::soup),
                    HotpotContentSerializers.HOTPOT_CONTENTS_CODEC.optionalFieldOf("contents").forGetter(PartialData::contents)
            ).apply(data, PartialData::new))
    );

    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    public double renderedWaterLevel = -1;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public ItemStack getContentByTableware(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos) {
        return getContentIndex(position).map(i -> data.soup.getContentResultByTableware(data.contents.get(i), this, pos).map(c -> c.getContentItemStack(this, pos).copy()).ifPresent(itemStack -> setEmptyContent(i, pos)).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setContentByInteraction(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        data.soup.getPlayerInteractionResult(position, player, hand, itemStack, this, pos).map(Holder::value).ifPresent(serializer -> setContentWhenEmpty(position, serializer, itemStack, pos, player.getDirection()));
    }

    @Override
    public Data getDefaultData(HolderLookup.Provider registryAccess) {
        return new Data(true, true, false, 0, 0, 0.0, HotpotComponentSoupType.loadEmptySoup(registryAccess), NonNullList.withSize(8, HotpotContentSerializers.loadEmptyContent()));
    }

    @Override
    public PartialData getPartialData(HolderLookup.Provider registryAccess) {
        return new PartialData(data.canConsumeContents, data.canBeRemoved, data.infiniteWater, data.time, data.velocity, data.synchronizedWaterLevel, data.soup, contentChanged ? Optional.of(data.contents) : Optional.empty());
    }

    @Override
    public Data onFullDataUpdate(Data data) {
        return data;
    }

    @Override
    public Data onFullDataUpdate(LevelBlockPos pos, Data data) {
        return data;
    }

    @Override
    public void onPartialDataUpdated() {
        contentChanged = false;
    }

    @Override
    public Codec<Data> getFullCodec() {
        return CODEC;
    }

    @Override
    public Codec<PartialData> getPartialCodec() {
        return PARTIAL_CODEC;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    public Optional<Integer> getContentIndex(int position) {
        return IntStream.range(0, 8).filter(index -> position == getContent(index).getContentSerializerHolder().value().indexToPosition(index, getTime())).boxed().max(Comparator.comparingInt(i -> getContent(i).getContentSerializerHolder().value().getPriority()));
    }

    private void synchronizeSoup(LevelBlockPos pos) {
        Map<HotpotBlockEntity, LevelBlockPos> neighbors = new BlockEntityFinder<>(pos, HotpotBlockEntity.class, (hotpotBlockEntity, pos2) -> isSameSoup(pos, pos2) && !hotpotBlockEntity.soupSynchronized).getAll();
        neighbors.forEach((key, value) -> key.setSoupSynchronized());
        data.soup.getSoupComponentSynchronizers(this, pos).stream().peek(synchronizer -> neighbors.forEach((hotpotBlockEntity, pos2) -> synchronizer.collect(hotpotBlockEntity, hotpotBlockEntity.getSoup(), pos2))).filter(IHotpotSoupComponentSynchronizer::shouldApply).forEach(synchronizer -> neighbors.forEach((hotpotBlockEntity, pos2) -> synchronizer.apply(neighbors.size(), hotpotBlockEntity, hotpotBlockEntity.getSoup(), pos2)));
    }

    public void setSoup(HotpotComponentSoup soup, LevelBlockPos pos) {
        this.data.soup = soup;
        pos.setBlockStateProperty(HotpotBlock.HOTPOT_LIT, this.data.soup.isHotpotLit(this, pos));
        markDataChangedAndNotify(pos);
    }

    public void setEmptySoup(LevelBlockPos pos) {
        setSoup(HotpotComponentSoupType.loadEmptySoup(pos.registryAccess()), pos);
    }

    public void markDataChangedAndNotify(LevelBlockPos pos) {
        markDataChanged();
        pos.markAndNotifyBlock();
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public void setContent(int index, IHotpotContent content) {
        data.contents.set(index, content);
        markDataChanged();
    }

    public void setContent(int index, IHotpotContent content, LevelBlockPos pos) {
        setContent(index, content);
        INGREDIENT_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotIngredientRecipeInput(this), pos.level()).map(RecipeHolder::value).ifPresent(recipe -> recipe.assemble(this, pos.registryAccess()).execute(this, pos));
    }

    public void setItemStackContentWhenEmpty(int position, ItemStack itemStack, LevelBlockPos pos) {
        data.soup.getContentSerializerResultFromItemStack(itemStack, this, pos).map(Holder::value).ifPresent(serializer -> setContentWhenEmpty(position, serializer, itemStack, pos, Direction.getRandom(pos.getRandomSource())));
    }

    public void setContentWhenEmpty(int position, IHotpotContentSerializer<?> serializer, ItemStack itemStack, LevelBlockPos pos, Direction direction) {
        setContentFromNeighbors(position, pos, itemStack, serializer, direction);
    }

    public void setContentWhenEmpty(Supplier<IHotpotContent> supplier, LevelBlockPos pos) {
        setContentFromNeighbors(pos, supplier);
    }

    public void setEmptyContent(int index, LevelBlockPos pos) {
        setContent(index, data.canConsumeContents ? HotpotContentSerializers.loadEmptyContent() : getContent(index), pos);
    }

    public void setEmptyContent(int index) {
        this.setContent(index, HotpotContentSerializers.loadEmptyContent());
    }

    public boolean hasEmptyContent() {
        return data.contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public boolean isEmptyContent(int pos) {
        return getContent(pos) instanceof HotpotEmptyContent;
    }

    public void setContentFromNeighbors(int position, LevelBlockPos pos, ItemStack itemStack, IHotpotContentSerializer<?> serializer, Direction direction) {
        new BlockEntityFinder<>(pos, HotpotBlockEntity.class, (hotpotBlockEntity, pos2) -> isSameSoup(pos, pos2)).getFirst(10, (hotpotBlockEntity, pos2) -> hotpotBlockEntity.hasEmptyContent(), (hotpotBlockEntity, pos2) -> hotpotBlockEntity.setContentAtBlockEntity(hotpotBlockEntity, pos2, itemStack, serializer, position, direction));
    }

    public void setContentFromNeighbors(LevelBlockPos pos, Supplier<IHotpotContent> supplier) {
        new BlockEntityFinder<>(pos, HotpotBlockEntity.class, (hotpotBlockEntity, pos2) -> isSameSoup(pos, pos2)).getFirst(10, (hotpotBlockEntity, pos2) -> hotpotBlockEntity.hasEmptyContent(), (hotpotBlockEntity, pos2) -> hotpotBlockEntity.setContentAtBlockEntity(hotpotBlockEntity, pos2, supplier));
    }

    public void setContentAtBlockEntity(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Supplier<IHotpotContent> supplier) {
        IntStream.range(0, 8).filter(hotpotBlockEntity::isEmptyContent).findFirst().ifPresent(index -> hotpotBlockEntity.setContent(index, supplier.get(), pos));
    }

    public void setContentAtBlockEntity(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, ItemStack itemStack, IHotpotContentSerializer<?> serializer, int position, Direction direction) {
        IntStream.concat(IntStream.of(serializer.positionToIndex(position, hotpotBlockEntity.getTime())), IntStream.range(0, 8)).filter(hotpotBlockEntity::isEmptyContent).findFirst().ifPresent(index -> hotpotBlockEntity.setContent(index, serializer.createContent(itemStack, hotpotBlockEntity, pos, direction), pos));
    }

    public NonNullList<IHotpotContent> getContents() {
        return data.contents;
    }

    public IHotpotContent getContent(int index) {
        return data.contents.get(index);
    }

    public void getContentByHand(int position, LevelBlockPos pos) {
        getContentIndex(position).ifPresent(i -> removeContent(i, pos));
    }

    public void pickContentByHand(Player player, InteractionHand hand, int position, LevelBlockPos pos) {
        getContentIndex(position).filter(i -> getContent(i) instanceof IHotpotPickableContent).ifPresent(i -> HotpotItemUtils.addToInventory(player, getContentByTableware(player, hand, position, 0, pos)));
    }

    public IHotpotResult<IHotpotContent> removeContent(int index, LevelBlockPos pos) {
        return data.soup.getContentResultByHand(data.soup.getContentResultByTableware(getContent(index), this, pos), this, pos).ifEmpty(() -> setEmptyContent(index, pos));
    }

    public void onRemove(LevelBlockPos pos) {
        IntStream.range(0, data.contents.size()).forEach(i -> removeContent(i, pos).ifPresent(content -> pos.dropCopiedItemStacks(content.getContentResultItemStacks(this, pos))));
    }

    public void awardExperience(double experience, LevelBlockPos pos) {
        data.soup.onAwardExperience(experience, this, pos);
    }

    public IHotpotContent getContentAtPosition(int position) {
        return getContentIndex(position).map(this::getContent).orElse(HotpotContentSerializers.loadEmptyContent());
    }

    public double getSynchronizedWaterLevel() {
        return data.synchronizedWaterLevel;
    }

    public double getWaterLevel() {
        return getSoup().getWaterLevel();
    }

    public void setWaterLevel(double waterLevel, LevelBlockPos pos) {
        data.soup.setWaterLevelWithOverflow(waterLevel, this, pos);
    }

    public HotpotComponentSoup getSoup() {
        return data.soup;
    }

    public int getTime() {
        return data.time;
    }

    public int getVelocity() {
        return data.velocity;
    }

    public void setVelocity(int velocity) {
        this.data.velocity = velocity;
    }

    public boolean isInfiniteWater() {
        return data.infiniteWater;
    }

    public void setInfiniteWater(boolean infiniteWater) {
        this.data.infiniteWater = infiniteWater;
    }

    public boolean canConsumeContents() {
        return data.canConsumeContents;
    }

    public boolean canBeRemoved() {
        return data.canBeRemoved;
    }

    public void setCanBeRemoved(boolean canBeRemoved) {
        this.data.canBeRemoved = canBeRemoved;
    }

    public void setSoupSynchronized(boolean soupSynchronized) {
        this.soupSynchronized = soupSynchronized;
    }

    public void setSoupSynchronized() {
        setSoupSynchronized(true);
    }

    public boolean isSoupSynchronized() {
        return soupSynchronized;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity hotpotBlockEntity) {
        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        hotpotBlockEntity.data.time += 1 + hotpotBlockEntity.data.velocity;
        hotpotBlockEntity.data.velocity = Math.max(0, hotpotBlockEntity.data.velocity - 1);

        if (!hotpotBlockEntity.isSoupSynchronized()) {
            hotpotBlockEntity.synchronizeSoup(blockPos);
        }

        hotpotBlockEntity.setSoupSynchronized(false);

        hotpotBlockEntity.data.synchronizedWaterLevel = hotpotBlockEntity.getWaterLevel();
        double tickSpeed = hotpotBlockEntity.data.soup.getContentTickSpeed(hotpotBlockEntity, blockPos);

        if (hotpotBlockEntity.getWaterLevel() > 0.0f) {
            IntStream.range(0, hotpotBlockEntity.data.contents.size()).forEach(i -> tickContents(i, hotpotBlockEntity, blockPos, tickSpeed));
        }

        level.sendBlockUpdated(pos, state, state, 3);
        hotpotBlockEntity.setChanged();
    }

    public static void tickContents(int position, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double tickSpeed) {
        IHotpotContent content = hotpotBlockEntity.data.contents.get(position);

        if (content.onTick(hotpotBlockEntity, pos, tickSpeed)) {
            hotpotBlockEntity.data.soup.onContentUpdate(content, hotpotBlockEntity, pos);
            hotpotBlockEntity.markDataChanged();
        }

        if (content.shouldRemove(hotpotBlockEntity, pos)) {
            hotpotBlockEntity.removeContent(position, pos);
            hotpotBlockEntity.markDataChanged();
        }
    }

    public static int getClickPosition(BlockPos blockPos, Vec3 pos) {
        blockPos = blockPos.relative(Direction.UP);
        Vec3 vec = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double x = vec.x() - 0.5f;
        double z = vec.z() - 0.5f;

        double size = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + size / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / size);
    }

    public static boolean isSameSoup(LevelBlockPos pos1, LevelBlockPos pos2) {
        if (!(pos1.getBlockEntity() instanceof HotpotBlockEntity blockEntity1)) {
            return false;
        }

        if (!(pos2.getBlockEntity() instanceof HotpotBlockEntity blockEntity2)) {
            return false;
        }

        return blockEntity1.getSoup().soupTypeHolder().equals(blockEntity2.getSoup().soupTypeHolder());
    }

    public static class Data {
        private boolean canConsumeContents;
        private boolean canBeRemoved;
        private boolean infiniteWater;
        private int time;
        private int velocity;
        private double synchronizedWaterLevel;
        private HotpotComponentSoup soup;
        private NonNullList<IHotpotContent> contents;

        public Data(boolean canConsumeContents, boolean canBeRemoved, boolean infiniteWater, int time, int velocity, double synchronizedWaterLevel, HotpotComponentSoup soup, NonNullList<IHotpotContent> contents) {
            this.canConsumeContents = canConsumeContents;
            this.canBeRemoved = canBeRemoved;
            this.infiniteWater = infiniteWater;
            this.time = time;
            this.velocity = velocity;
            this.synchronizedWaterLevel = synchronizedWaterLevel;
            this.soup = soup;
            this.contents = contents;
        }

        public Data fromPartialData(PartialData partialData) {
            this.canConsumeContents = partialData.canConsumeContents;
            this.canBeRemoved = partialData.canBeRemoved;
            this.infiniteWater = partialData.infiniteWater;
            this.time = partialData.time;
            this.velocity = partialData.velocity;
            this.synchronizedWaterLevel = partialData.synchronizedWaterLevel;
            this.soup = partialData.soup;
            this.contents = partialData.contents.orElse(contents);

            return this;
        }

        public boolean canConsumeContents() {
            return canConsumeContents;
        }

        public boolean canBeRemoved() {
            return canBeRemoved;
        }

        public boolean isInfiniteWater() {
            return infiniteWater;
        }

        public int getTime() {
            return time;
        }

        public int getVelocity() {
            return velocity;
        }

        public double getSynchronizedWaterLevel() {
            return synchronizedWaterLevel;
        }

        public HotpotComponentSoup getSoup() {
            return soup;
        }

        public NonNullList<IHotpotContent> getContents() {
            return contents;
        }
    }

    public record PartialData(boolean canConsumeContents, boolean canBeRemoved, boolean infiniteWater, int time, int velocity, double synchronizedWaterLevel, HotpotComponentSoup soup, Optional<NonNullList<IHotpotContent>> contents) implements AbstractHotpotCodecBlockEntity.PartialData<Data> {
        @Override
        public Data update(Data data) {
            return data.fromPartialData(this);
        }
    }
}
