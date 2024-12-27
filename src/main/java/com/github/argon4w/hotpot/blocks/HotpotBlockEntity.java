package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.fancytoys.blocks.AbstractCodecBlockEntity;
import com.github.argon4w.fancytoys.functions.Curry;
import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.fancytoys.streams.BlockEntityStreamBuilder;
import com.github.argon4w.fancytoys.items.ItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.blocks.IHotpotTablewareContainer;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotIngredientRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.core.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.joml.Math;

public class HotpotBlockEntity
        extends AbstractCodecBlockEntity<HotpotBlockEntity.Data, HotpotBlockEntity.PartialData>
        implements IHotpotTablewareContainer {

    public static final RecipeManager.CachedCheck<HotpotIngredientRecipeInput, HotpotSoupIngredientRecipe> INGREDIENT_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get());
    public static final double ROTATING_CONTENT_INTERVAL = 360.0 / 8.0;

    public static final Codec<Data> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(data ->
            data.group(
                    NeoForgeExtraCodecs.mapWithAlternative(Codec.BOOL.fieldOf("infinite_content"), Codec.BOOL.fieldOf("can_consume_contents").xmap(b -> !b, Function.identity())).forGetter(Data::isInfiniteContent),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(Data::canBeRemoved),
                    Codec.BOOL.fieldOf("infinite_water").forGetter(Data::isInfiniteWater),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(Codec.BOOL, "should_sync_soup", true).forGetter(Data::shouldSyncSoup),
                    Codec.INT.fieldOf("time").forGetter(Data::getTime),
                    Codec.INT.fieldOf("velocity").forGetter(Data::getVelocity),
                    Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(Data::getSyncedWaterLevel),
                    HotpotComponentSoupType.CODEC.fieldOf("soup").forGetter(Data::getSoup),
                    HotpotContentSerializers.HOTPOT_CONTENTS_CODEC.fieldOf("contents").forGetter(Data::getContents)
            ).apply(data, Data::new))
    );

    public static final Codec<PartialData> PARTIAL_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(data ->
            data.group(
                    Codec.BOOL.fieldOf("infinite_content").forGetter(PartialData::infiniteContent),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(PartialData::canBeRemoved),
                    Codec.BOOL.fieldOf("infinite_water").forGetter(PartialData::infiniteWater),
                    Codec.BOOL.fieldOf("should_sync_soup").forGetter(PartialData::shouldSyncSoup),
                    Codec.INT.fieldOf("time").forGetter(PartialData::time),
                    Codec.INT.fieldOf("velocity").forGetter(PartialData::velocity),
                    Codec.DOUBLE.fieldOf("synchronized_water_level").forGetter(PartialData::synchronizedWaterLevel),
                    HotpotComponentSoupType.PARTIAL_CODEC.fieldOf("soup").forGetter(PartialData::soup),
                    HotpotContentSerializers.HOTPOT_CONTENTS_CODEC.optionalFieldOf("contents").forGetter(PartialData::contents)
            ).apply(data, PartialData::new))
    );

    private boolean contentChanged;
    private boolean soupSynced;
    public double clientWaterLevel;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
        this.contentChanged = true;
        this.soupSynced = false;
        this.clientWaterLevel = -1;
    }

    @Override
    public PartialData getPartialData(HolderLookup.Provider registryAccess) {
        return new PartialData(
                data.infiniteContent,
                data.canBeRemoved,
                data.infiniteWater,
                data.shouldSyncSoup,
                data.time,
                data.velocity,
                data.syncedWaterLevel,
                data.soup,
                contentChanged ? Optional.of(data.contents) : Optional.empty());
    }

    @Override
    public Data getDefaultData(HolderLookup.Provider registryAccess) {
        return new Data(
                false,
                true,
                false,
                true,
                0,
                0,
                0.0,
                HotpotComponentSoupType.loadEmptySoup(registryAccess),
                NonNullList.withSize(8, HotpotContentSerializers.empty()));
    }

    @Override
    public void setContentByInteraction(Context context, ItemStack itemStack) {
        data.soup
                .getPlayerInteractionResult(context, itemStack, this)
                .map(Holder::value)
                .ifPresent(serializer -> setContentFromNeighbors(context.position(), context.pos(), itemStack, serializer, context.player().getDirection()));
    }

    @Override
    public ItemStack getContentByTableware(Context context) {
        return getContentIndex(context.position())
                .map(i -> takeContentItemStackByTableware(i, context.pos()))
                .orElse(ItemStack.EMPTY);
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

    private void syncSoup(LevelBlockPos pos) {
        Map<HotpotBlockEntity, LevelBlockPos> synced = getNeighbors(pos)
                .filterNot(HotpotBlockEntity::isSoupSynced)
                .build(pos)
                .peekKey(HotpotBlockEntity::setSoupSynced)
                .toMap();

        data.soup.getSyncData(this, pos)
                .stream()
                .peek(data -> synced.forEach((hotpot, pos2) -> data.collect(hotpot, hotpot.getSoup(), pos2)))
                .filter(IHotpotSoupSyncData::shouldApply)
                .forEach(data -> synced.forEach((hotpot, pos2) -> data.apply(synced.size(), hotpot, hotpot.getSoup(), pos2)));
    }

    public void setContentAtBlockEntity(
            HotpotBlockEntity hotpotBlockEntity,
            LevelBlockPos pos,
            ItemStack itemStack,
            IHotpotContentSerializer<?> serializer,
            int position,
            Direction direction) {
        IntStream
                .concat(IntStream.of(serializer.positionToIndex(position, hotpotBlockEntity.getTime())), getIndexStream())
                .filter(hotpotBlockEntity::isEmptyContent)
                .findFirst()
                .ifPresent(index -> hotpotBlockEntity.setContent(index, serializer.createContent(itemStack, hotpotBlockEntity, pos, direction), pos));
    }

    public void setContentAtBlockEntity(
            HotpotBlockEntity hotpotBlockEntity,
            LevelBlockPos pos,
            Supplier<IHotpotContent> supplier) {
        getIndexStream()
                .filter(hotpotBlockEntity::isEmptyContent)
                .findFirst()
                .ifPresent(index -> hotpotBlockEntity.setContent(index, supplier.get(), pos));
    }

    public void setContentFromNeighbors(
            int position,
            LevelBlockPos pos,
            ItemStack itemStack,
            IHotpotContentSerializer<?> serializer,
            Direction direction) {
        getNeighbors(pos)
                .build(pos)
                .filterKey(HotpotBlockEntity::hasEmptyContent)
                .findFirst((hotpotBlockEntity, pos2) -> hotpotBlockEntity.setContentAtBlockEntity(hotpotBlockEntity, pos2, itemStack, serializer, position, direction));
    }

    public void setContentFromNeighbors(LevelBlockPos pos, Supplier<IHotpotContent> supplier) {
        getNeighbors(pos)
                .build(pos)
                .filterKey(HotpotBlockEntity::hasEmptyContent)
                .findFirst((hotpotBlockEntity, pos2) -> hotpotBlockEntity.setContentAtBlockEntity(hotpotBlockEntity, pos2, supplier));
    }

    public void setContent(int index, IHotpotContent content, LevelBlockPos pos) {
        setContent(index, content);
        INGREDIENT_RECIPE_QUICK_CHECK
                .getRecipeFor(new HotpotIngredientRecipeInput(this), pos.level())
                .map(RecipeHolder::value)
                .ifPresent(recipe -> recipe.assemble(this, pos.registryAccess()).execute(this, pos));
    }

    public void setItemStackContentWhenEmpty(int position, ItemStack itemStack, LevelBlockPos pos) {
        data.soup
                .getContentSerializerResultFromItemStack(itemStack, this, pos)
                .map(Holder::value)
                .ifPresent(serializer -> setContentFromNeighbors(position, pos, itemStack, serializer, Direction.getRandom(pos.getRandomSource())));
    }

    public ItemStack takeContentItemStackByTableware(int index, LevelBlockPos pos) {
        return getContentResultByTableware(index, pos)
                .map(c -> c.getContentItemStack(this, pos).copy())
                .ifPresent(itemStack -> setEmptyContent(index, pos))
                .orElse(ItemStack.EMPTY);
    }

    public Optional<Integer> getContentIndex(int position) {
        return getIndexStream()
                .boxed()
                .filter(index -> position == getContentPosition(index))
                .max(Comparator.comparingInt(i -> getContent(i).getContentSerializerHolder().value().getPriority()));
    }

    public int getContentPosition(int index) {
        return getContent(index).getContentSerializerHolder().value().indexToPosition(index, getTime());
    }

    public void pickContentByHand(Context context) {
        getContentIndex(context.position()).ifPresent(i -> ItemUtils.addToInventory(context.player(), getContentByTableware(context)));
    }

    public IHotpotResult<IHotpotContent> getContentResultByTableware(int index, LevelBlockPos pos) {
        return data.soup.getContentResultByTableware(getContent(index), this, pos);
    }

    public IHotpotResult<IHotpotContent> getContentResultByHand(int index, LevelBlockPos pos) {
        return data.soup.getContentResultByHand(getContentResultByTableware(index, pos), this, pos);
    }

    public IHotpotResult<IHotpotContent> removeContent(int index, LevelBlockPos pos) {
        return getContentResultByHand(index, pos).ifEmpty(() -> setEmptyContent(index, pos));
    }

    public void forceRemoveContent(LevelBlockPos pos, int index) {
        removeContent(index, pos).ifPresent(Curry.of(this::dropContentResult, pos));
    }

    public void dropContentResult(LevelBlockPos pos, IHotpotContent content) {
        pos.dropCopiedItemStacks(content.getContentResultItemStacks(this, pos));
    }

    public IHotpotContent getContentAtPosition(int position) {
        return getContentIndex(position).map(this::getContent).orElse(HotpotContentSerializers.empty());
    }

    public void onContentUpdate(IHotpotContent content, LevelBlockPos pos) {
        data.soup.onContentUpdate(content, this, pos);
    }

    public void getContentByHand(int position, LevelBlockPos pos) {
        getContentIndex(position).ifPresent(i -> removeContent(i, pos));
    }

    public double getContentTickSpeed(LevelBlockPos pos) {
        return data.soup.getContentTickSpeed(this, pos);
    }

    public NonNullList<IHotpotContent> getContents() {
        return data.contents;
    }

    public IHotpotContent getContent(int index) {
        return data.contents.get(index);
    }

    public void setEmptyContent(int index, LevelBlockPos pos) {
        setContent(index, data.infiniteContent ? getContent(index) : HotpotContentSerializers.empty(), pos);
    }

    public void setEmptyContent(int index) {
        this.setContent(index, HotpotContentSerializers.empty());
    }

    public boolean hasEmptyContent() {
        return data.contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public boolean isEmptyContent(int pos) {
        return getContent(pos) instanceof HotpotEmptyContent;
    }

    public void setContent(int index, IHotpotContent content) {
        data.contents.set(index, content);
        markDataChanged();
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

    public void onRemove(LevelBlockPos pos) {
        getIndexStream().boxed().forEach(Curry.of(this::forceRemoveContent, pos));
    }

    public BlockEntityStreamBuilder<HotpotBlockEntity> getNeighbors(LevelBlockPos pos) {
        return getBuilder().filterPos(Curry.of(HotpotBlockEntity::isSameSoup, pos));
    }

    public BlockEntityStreamBuilder<HotpotBlockEntity> getBuilder() {
        return BlockEntityStreamBuilder.of(HotpotModEntry.HOTPOT_BLOCK_ENTITY);
    }

    public IntStream getIndexStream() {
        return IntStream.range(0, data.contents.size());
    }

    public void awardExperience(double experience, LevelBlockPos pos) {
        data.soup.onAwardExperience(experience, this, pos);
    }

    public double getSynchronizedWaterLevel() {
        return data.syncedWaterLevel;
    }

    public double getWaterLevel() {
        return getSoup().getWaterLevel();
    }

    public void setWaterLevel(double waterLevel, LevelBlockPos pos) {
        data.soup.setWaterLevelWithOverflow(waterLevel, this, pos);
    }

    public void updateSyncedWaterLevel() {
        data.syncedWaterLevel = getWaterLevel();
    }

    public void shrinkVelocity() {
        data.velocity = Math.max(0, data.velocity - 1);
    }

    public void applyVelocity() {
        data.time += 1 + data.velocity;
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

    public boolean isInfiniteContent() {
        return data.infiniteContent;
    }

    public boolean shouldSyncSoup() {
        return data.shouldSyncSoup;
    }

    public void setShouldSyncSoup(boolean shouldSyncSoup) {
        this.data.shouldSyncSoup = shouldSyncSoup;
    }

    public boolean canBeRemoved() {
        return data.canBeRemoved;
    }

    public void setCanBeRemoved(boolean canBeRemoved) {
        this.data.canBeRemoved = canBeRemoved;
    }

    public void setSoupSynced(boolean soupSynchronized) {
        this.soupSynced = soupSynchronized;
    }

    public void setSoupSynced() {
        setSoupSynced(true);
    }

    public boolean isSoupSynced() {
        return soupSynced;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            HotpotBlockEntity hotpotBlockEntity) {
        LevelBlockPos blockPos = new LevelBlockPos(level, pos);

        hotpotBlockEntity.applyVelocity();
        hotpotBlockEntity.shrinkVelocity();

        if (!hotpotBlockEntity.isSoupSynced() && hotpotBlockEntity.shouldSyncSoup()) {
            hotpotBlockEntity.syncSoup(blockPos);
        }

        hotpotBlockEntity.setSoupSynced(false);

        hotpotBlockEntity.updateSyncedWaterLevel();
        double tickSpeed = hotpotBlockEntity.getContentTickSpeed(blockPos);

        hotpotBlockEntity.getIndexStream().forEach(i -> tickContent(i, hotpotBlockEntity, blockPos, tickSpeed));
        level.sendBlockUpdated(pos, state, state, 3);
        hotpotBlockEntity.setChanged();
    }

    public static void tickContent(
            int index,
            HotpotBlockEntity hotpotBlockEntity,
            LevelBlockPos pos,
            double tickSpeed) {
        IHotpotContent content = hotpotBlockEntity.getContent(index);

        if (hotpotBlockEntity.getWaterLevel() <= 0.0f) {
            return;
        }

        if (content.onTick(hotpotBlockEntity, pos, tickSpeed)) {
            hotpotBlockEntity.onContentUpdate(content, pos);
            hotpotBlockEntity.markDataChanged();
        }

        if (content.shouldRemove(hotpotBlockEntity, pos)) {
            hotpotBlockEntity.removeContent(index, pos);
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

        return blockEntity1
                .getSoup()
                .soupTypeHolder()
                .equals(blockEntity2.getSoup().soupTypeHolder());
    }

    public static class Data {
        private boolean infiniteContent;
        private boolean canBeRemoved;
        private boolean infiniteWater;
        private boolean shouldSyncSoup;
        private int time;
        private int velocity;
        private double syncedWaterLevel;
        private HotpotComponentSoup soup;
        private NonNullList<IHotpotContent> contents;

        public Data(
                boolean infiniteContent,
                boolean canBeRemoved,
                boolean infiniteWater,
                boolean shouldSyncSoup,
                int time,
                int velocity,
                double syncedWaterLevel,
                HotpotComponentSoup soup,
                NonNullList<IHotpotContent> contents) {
            this.infiniteContent = infiniteContent;
            this.canBeRemoved = canBeRemoved;
            this.infiniteWater = infiniteWater;
            this.shouldSyncSoup = shouldSyncSoup;
            this.time = time;
            this.velocity = velocity;
            this.syncedWaterLevel = syncedWaterLevel;
            this.soup = soup;
            this.contents = contents;
        }

        public Data fromPartialData(PartialData partialData) {
            this.infiniteContent = partialData.infiniteContent;
            this.canBeRemoved = partialData.canBeRemoved;
            this.infiniteWater = partialData.infiniteWater;
            this.shouldSyncSoup = partialData.shouldSyncSoup;
            this.time = partialData.time;
            this.velocity = partialData.velocity;
            this.syncedWaterLevel = partialData.synchronizedWaterLevel;
            this.soup = partialData.soup;
            this.contents = partialData.contents.orElse(contents);

            return this;
        }

        public boolean canBeRemoved() {
            return canBeRemoved;
        }

        public boolean isInfiniteContent() {
            return infiniteContent;
        }

        public boolean isInfiniteWater() {
            return infiniteWater;
        }

        public boolean shouldSyncSoup() {
            return shouldSyncSoup;
        }

        public int getTime() {
            return time;
        }

        public int getVelocity() {
            return velocity;
        }

        public double getSyncedWaterLevel() {
            return syncedWaterLevel;
        }

        public HotpotComponentSoup getSoup() {
            return soup;
        }

        public NonNullList<IHotpotContent> getContents() {
            return contents;
        }
    }

    public record PartialData(
            boolean infiniteContent,
            boolean canBeRemoved,
            boolean infiniteWater,
            boolean shouldSyncSoup,
            int time,
            int velocity,
            double synchronizedWaterLevel,
            HotpotComponentSoup soup,
            Optional<NonNullList<IHotpotContent>> contents)
            implements AbstractCodecBlockEntity.PartialData<Data> {

        @Override
        public Data update(Data data) {
            return data.fromPartialData(this);
        }
    }
}
