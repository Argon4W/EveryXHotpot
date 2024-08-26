package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
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
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Math;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class HotpotBlockEntity extends AbstractHotpotCodecTablewareBlockEntity<HotpotBlockEntity.Data, HotpotBlockEntity.PartialData> {
    public static final RecipeManager.CachedCheck<HotpotIngredientRecipeInput, HotpotSoupIngredientRecipe> INGREDIENT_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get());

    public static final Codec<Data> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                Codec.BOOL.fieldOf("can_be_removed").forGetter(Data::canBeRemoved),
                Codec.BOOL.fieldOf("infinite_water").forGetter(Data::isInfiniteWater),
                Codec.INT.fieldOf("time").forGetter(Data::getTime),
                Codec.INT.fieldOf("velocity").forGetter(Data::getVelocity),
                Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(Data::getSynchronizedWaterLevel),
                HotpotComponentSoupType.CODEC.fieldOf("soup").forGetter(Data::getSoup),
                HotpotContentSerializers.LIST_INDEXED_CODEC.fieldOf("contents").forGetter(Data::getContents)
            ).apply(data, Data::new))
    );

    public static final Codec<PartialData> PARTIAL_CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(PartialData::canBeRemoved),
                    Codec.BOOL.fieldOf("infinite_water").forGetter(PartialData::infiniteWater),
                    Codec.INT.fieldOf("time").forGetter(PartialData::time),
                    Codec.INT.fieldOf("velocity").forGetter(PartialData::velocity),
                    Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(PartialData::synchronizedWaterLevel),
                    HotpotComponentSoupType.UNSORTED_CODEC.fieldOf("soup").forGetter(PartialData::soup),
                    HotpotContentSerializers.LIST_INDEXED_CODEC.optionalFieldOf("contents").forGetter(PartialData::contents)
            ).apply(data, PartialData::new))
    );

    private Data data = getDefaultData();

    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    public double renderedWaterLevel = -1;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Data getDefaultData() {
        return new Data(true, false, 0, 0, 0.0, HotpotComponentSoupType.loadEmptySoup(), NonNullList.withSize(8, HotpotContentSerializers.loadEmptyContent()));
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
    public PartialData getPartialData(HolderLookup.Provider registryAccess) {
        return new PartialData(data.canBeRemoved, data.infiniteWater, data.time, data.velocity, data.synchronizedWaterLevel, data.soup, contentChanged ? Optional.of(data.contents) : Optional.empty());
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int hitLayer, LevelBlockPos pos) {
        int contentPos = getContentPos(hitPos);
        return data.soup.getContentResultByTableware(data.contents.get(contentPos), this, pos).map(c -> c.getContentItemStack(this, pos)).ifPresent(i -> setEmptyContent(contentPos, pos)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack setContentByTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        setContentByInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        return itemStack;
    }

    @Override
    public void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        data.soup.getPlayerInteractionResult(hitPos, player, hand, itemStack, this, selfPos).map(Holder::value).ifPresent(serializer -> setContentWhenEmpty(hitPos, () -> serializer.get(itemStack, this, selfPos), selfPos));
    }

    public void getContentByHand(int hitPos, LevelBlockPos pos) {
        removeContent(getContentPos(hitPos), pos);
    }

    private void getEmptyContentFromNeighbors(int hitPos, LevelBlockPos selfPos, TriConsumer<Integer, HotpotBlockEntity, LevelBlockPos> consumer) {
        new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos)).getFirst(10, HotpotBlockEntity::hasEmptyContent, (hotpotBlockEntity, pos) -> getEmptyContentAtBlockEntity(hotpotBlockEntity, pos, hitPos, consumer));
    }

    public void getEmptyContentAtBlockEntity(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, int hitPos, TriConsumer<Integer, HotpotBlockEntity, LevelBlockPos> consumer) {
        IntStream.concat(IntStream.of(getContentPos(hitPos)), IntStream.range(0, 8)).filter(hotpotBlockEntity::isEmptyContent).findFirst().ifPresent(contentPos -> consumer.accept(contentPos, hotpotBlockEntity, pos));
    }

    public void setItemStackContentWhenEmpty(int hitPos, ItemStack itemStack, LevelBlockPos selfPos) {
        data.soup.getContentSerializerResultFromItemStack(itemStack, this, selfPos).map(Holder::value).ifPresent(serializer -> setContentWhenEmpty(hitPos, () -> serializer.get(itemStack, this, selfPos), selfPos));
    }

    public IHotpotResult<IHotpotContent> removeContent(int contentPos, LevelBlockPos pos) {
        return data.soup.getContentResultByHand(data.soup.getContentResultByTableware(data.contents.get(contentPos), this, pos), this, pos).ifEmpty(() -> setEmptyContent(contentPos, pos));
    }

    public void onRemove(LevelBlockPos pos) {
        IntStream.range(0, data.contents.size()).forEach(i -> removeContent(i, pos).ifPresent(content -> pos.dropItemStack(content.getContentItemStack(this, pos))));
    }

    public void setContentWhenEmpty(int hitPos, Supplier<IHotpotContent> supplier, LevelBlockPos selfPos) {
        getEmptyContentFromNeighbors(hitPos, selfPos, (p, hotpotBlockEntity, pos) -> hotpotBlockEntity.setContent(p, supplier.get(), pos));
    }

    public void setEmptyContent(int contentPos, LevelBlockPos pos) {
        setContent(contentPos, HotpotContentSerializers.loadEmptyContent(), pos);
    }

    public void setContent(int contentPos, IHotpotContent content, LevelBlockPos pos) {
        data.contents.set(contentPos, content);
        markDataChanged();
        INGREDIENT_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotIngredientRecipeInput(this), pos.level()).map(RecipeHolder::value).ifPresent(recipe -> recipe.assemble(this).execute(this, pos));
    }

    public void setContent(int contentPos, IHotpotContent content) {
        data.contents.set(contentPos, content);
        markDataChanged();
    }

    public void setSoup(HotpotComponentSoup soup, LevelBlockPos pos) {
        this.data.soup = soup;
        pos.setBlockStateProperty(HotpotBlock.HOTPOT_LIT, this.data.soup.isHotpotLit(this, pos));
        markDataChangedAndNotify(pos);
    }

    private void synchronizeSoup(LevelBlockPos selfPos) {
        Map<HotpotBlockEntity, LevelBlockPos> neighbors = new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos) && !hotpotBlockEntity.soupSynchronized).getAll();
        List<IHotpotSoupComponentSynchronizer> synchronizers = data.soup.getSoupComponentSynchronizers(this, selfPos);

        neighbors.forEach((key, value) -> key.setSoupSynchronized());
        synchronizers.forEach(synchronizer -> synchronizeSoup(neighbors, synchronizer));
    }

    private void synchronizeSoup(Map<HotpotBlockEntity, LevelBlockPos> neighbors, IHotpotSoupComponentSynchronizer synchronizer) {
        neighbors.forEach((hotpotBlockEntity, pos) -> synchronizer.collect(hotpotBlockEntity, hotpotBlockEntity.getSoup(), pos));
        neighbors.forEach((hotpotBlockEntity, pos) -> synchronizer.apply(neighbors.size(), hotpotBlockEntity, hotpotBlockEntity.getSoup(), pos));
    }

    public int getContentPos(int hitPos) {
        double size = (360.0 / 8.0);
        double degree =  (data.time / 20.0 / 60.0) * 360.0 + size / 2.0;

        int base = (int) Math.floor((degree % 360.0) / size);
        int contentPos = hitPos - base;

        return contentPos < 0 ? 8 + contentPos : contentPos;
    }

    public void markDataChangedAndNotify(LevelBlockPos pos) {
        markDataChanged();
        pos.markAndNotifyBlock();
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public void awardExperience(double experience, LevelBlockPos pos) {
        data.soup.onAwardExperience(experience, this, pos);
    }

    public double getSynchronizedWaterLevel() {
        return data.synchronizedWaterLevel;
    }

    public double getWaterLevel() {
        return getSoup().getWaterLevel();
    }

    public void setWaterLevel(double waterLevel, LevelBlockPos pos) {
        data.soup.setWaterLevel(waterLevel, this, pos);
    }

    public boolean hasEmptyContent() {
        return data.contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public static boolean hasEmptyContent(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return hotpotBlockEntity.hasEmptyContent();
    }

    public boolean isEmptyContent(int pos) {
        return getContent(pos) instanceof HotpotEmptyContent;
    }

    public NonNullList<IHotpotContent> getContents() {
        return data.contents;
    }

    public IHotpotContent getContent(int pos) {
        return data.contents.get(pos);
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
        LevelBlockPos selfPos = new LevelBlockPos(level, pos);

        hotpotBlockEntity.data.time += 1 + hotpotBlockEntity.data.velocity;
        hotpotBlockEntity.data.velocity = Math.max(0, hotpotBlockEntity.data.velocity - 1);

        if (!hotpotBlockEntity.isSoupSynchronized()) {
            hotpotBlockEntity.synchronizeSoup(selfPos);
        }

        hotpotBlockEntity.setSoupSynchronized(false);

        hotpotBlockEntity.data.synchronizedWaterLevel = hotpotBlockEntity.getWaterLevel();
        double tickSpeed = hotpotBlockEntity.data.soup.getContentTickSpeed(hotpotBlockEntity, selfPos);

        if (hotpotBlockEntity.getWaterLevel() > 0.0f) {
            IntStream.range(0, hotpotBlockEntity.data.contents.size()).forEach(i -> tickContents(i, hotpotBlockEntity, selfPos, tickSpeed));
        }

        level.sendBlockUpdated(pos, state, state, 3);
        hotpotBlockEntity.setChanged();
    }

    public static void tickContents(int pos, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos, double tickSpeed) {
        IHotpotContent content = hotpotBlockEntity.data.contents.get(pos);

        if (content.onTick(hotpotBlockEntity, selfPos, tickSpeed)) {
            hotpotBlockEntity.data.soup.onContentUpdate(content, hotpotBlockEntity, selfPos);
            hotpotBlockEntity.markDataChanged();
        }

        if (content.shouldRemove(hotpotBlockEntity, selfPos)) {
            hotpotBlockEntity.removeContent(pos, selfPos);
            hotpotBlockEntity.markDataChanged();
        }
    }

    public static boolean isSameSoup(LevelBlockPos selfPos, LevelBlockPos pos) {
        if (!(selfPos.getBlockEntity() instanceof HotpotBlockEntity selfBlockEntity)) {
            return false;
        }

        if (!(pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return false;
        }

        return selfBlockEntity.getSoup().soupTypeHolder().equals(hotpotBlockEntity.getSoup().soupTypeHolder());
    }

    public static int getHitPos(BlockPos blockPos, Vec3 pos) {
        blockPos = blockPos.relative(Direction.UP);
        Vec3 vec = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double x = vec.x() - 0.5f;
        double z = vec.z() - 0.5f;

        double size = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + size / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / size);
    }

    public static class Data {
        private boolean canBeRemoved;
        private boolean infiniteWater;
        private int time;
        private int velocity;
        private double synchronizedWaterLevel;
        private HotpotComponentSoup soup;
        private NonNullList<IHotpotContent> contents;

        public Data(boolean canBeRemoved, boolean infiniteWater, int time, int velocity, double synchronizedWaterLevel, HotpotComponentSoup soup, NonNullList<IHotpotContent> contents) {
            this.canBeRemoved = canBeRemoved;
            this.infiniteWater = infiniteWater;
            this.time = time;
            this.velocity = velocity;
            this.synchronizedWaterLevel = synchronizedWaterLevel;
            this.soup = soup;
            this.contents = contents;
        }

        public Data fromPartialData(PartialData partialData) {
            this.canBeRemoved = partialData.canBeRemoved;
            this.infiniteWater = partialData.infiniteWater;
            this.time = partialData.time;
            this.velocity = partialData.velocity;
            this.synchronizedWaterLevel = partialData.synchronizedWaterLevel;
            this.soup = partialData.soup;
            this.contents = partialData.contents.orElse(contents);

            return this;
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

    public record PartialData(boolean canBeRemoved, boolean infiniteWater, int time, int velocity, double synchronizedWaterLevel, HotpotComponentSoup soup, Optional<NonNullList<IHotpotContent>> contents) implements AbstractHotpotCodecTablewareBlockEntity.PartialData<Data> {
        @Override
        public Data update(Data data) {
            return data.fromPartialData(this);
        }
    }
}
