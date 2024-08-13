package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotIngredientRecipeInput;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class HotpotBlockEntity extends AbstractTablewareInteractiveBlockEntity {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupBaseRecipe> BASE_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_TYPE.get());
    public static final RecipeManager.CachedCheck<HotpotIngredientRecipeInput, HotpotSoupIngredientRecipe> INGREDIENT_RECIPE_QUICK_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get());

    private final NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HotpotContentSerializers.getEmptyContent());
    private IHotpotSoup soup = HotpotSoupTypeSerializers.buildEmptySoup();

    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    public float renderedWaterLevel = -1f;

    private int time = 0;
    private int velocity = 0;

    private boolean infiniteWater = false;
    private boolean canBeRemoved = true;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    private void getEmptyContent(int hitPos, LevelBlockPos selfPos, TriConsumer<Integer, HotpotBlockEntity, LevelBlockPos> consumer) {
        new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos)).getFirst(10, HotpotBlockEntity::hasEmptyContent, (hotpotBlockEntity, pos) -> getEmptyContentAt(hotpotBlockEntity, pos, hitPos, consumer));
    }

    public void getEmptyContentAt(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, int hitPos, TriConsumer<Integer, HotpotBlockEntity, LevelBlockPos> consumer) {
        IntStream.concat(IntStream.of(getContentPos(hitPos)), IntStream.range(0, 8)).filter(hotpotBlockEntity::isEmptyContent).findFirst().ifPresent(contentPos -> consumer.accept(contentPos, hotpotBlockEntity, pos));
    }

    public void setContent(int hitPos, Supplier<IHotpotContent> supplier, LevelBlockPos selfPos) {
        getEmptyContent(hitPos, selfPos, (p, hotpotBlockEntity, pos) -> hotpotBlockEntity.placeContent(p, supplier.get(), pos));
    }

    public void tryPlaceItemStack(int hitPos, ItemStack itemStack, LevelBlockPos selfPos) {
        soup.getContentSerializerFromItemStack(itemStack, this, selfPos).ifPresent(serializer -> setContent(hitPos, () -> serializer.get(itemStack, this, selfPos), selfPos));
    }

    private void placeContent(int contentPos, IHotpotContent content, LevelBlockPos pos) {
        contents.set(contentPos, content);
        INGREDIENT_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotIngredientRecipeInput(this), pos.level()).map(RecipeHolder::value).ifPresent(recipe -> recipe.assemble(this).execute(this, pos));

        markDataChanged();
    }

    @Override
    public ItemStack setContentViaTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        setContentViaInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        return itemStack;
    }

    @Override
    public void setContentViaInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        Optional<HotpotSoupBaseRecipe> optional = BASE_RECIPE_QUICK_CHECK.getRecipeFor(new HotpotRecipeInput(itemStack, getSoup()), selfPos.level()).map(RecipeHolder::value);

        if (optional.isEmpty()) {
            soup.interact(hitPos, player, hand, itemStack, this, selfPos).ifPresent(serializer -> setContent(hitPos, () -> serializer.get(itemStack, this, selfPos), selfPos));
            return;
        }

        HotpotSoupBaseRecipe recipe = optional.get();
        player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));

        setSoup(recipe.getResultSoup(), selfPos);
        setWaterLevel(recipe.getResultWaterLevel(), selfPos);
        selfPos.playSound(recipe.getSoundEvent());
    }

    @Override
    public ItemStack getContentViaTableware(Player player, InteractionHand hand, int hitPos, int hitLayer, LevelBlockPos pos) {
        int contentPos = getContentPos(hitPos);
        IHotpotContent content = contents.get(contentPos);

        if (content instanceof HotpotEmptyContent) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = soup.getContentViaTableware(content, content.takeOut(player, this, pos), this, pos);
        contents.set(contentPos, HotpotContentSerializers.getEmptyContent());
        markDataChanged();

        return itemStack;
    }

    public void getContentViaHand(Player player, int hitPos, LevelBlockPos pos) {
        int contentPos = getContentPos(hitPos);
        IHotpotContent content = contents.get(contentPos);

        if (content instanceof HotpotEmptyContent) {
            return;
        }

        soup.getContentViaHand(content, soup.getContentViaTableware(content, content.takeOut(player, this, pos), this, pos), this, pos);
        contents.set(contentPos, HotpotContentSerializers.getEmptyContent());

        markDataChanged();
    }

    public int getContentPos(int hitPos) {
        double size = (360f / 8f);
        double degree =  (time / 20f / 60f) * 360f + size / 2f;

        int root = (int) Math.floor((degree % 360f) / size);
        int contentPos = hitPos - root;

        return contentPos < 0 ? 8 + contentPos : contentPos;
    }

    public void setSoup(IHotpotSoup soup, LevelBlockPos pos) {
        this.soup = soup;

        boolean hotpotLit = this.soup.isHotpotLit(this, pos);
        pos.setBlockState(pos.getBlockState().setValue(HotpotBlock.HOTPOT_LIT, hotpotLit));

        markDataChanged();
        pos.markAndNotifyBlock();
    }

    private void synchronizeSoup(LevelBlockPos selfPos) {
        Map<HotpotBlockEntity, LevelBlockPos> neighbors = new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos) && !hotpotBlockEntity.soupSynchronized).getAll();
        List<IHotpotSoupSynchronizer> synchronizers = soup.getSynchronizers(this, selfPos);

        neighbors.forEach((key, value) -> key.setSoupSynchronized());
        synchronizers.forEach(synchronizer -> synchronizeSoup(neighbors, synchronizer));
    }

    private void synchronizeSoup(Map<HotpotBlockEntity, LevelBlockPos> neighbors, IHotpotSoupSynchronizer synchronizer) {
        neighbors.forEach(synchronizer::collect);
        neighbors.forEach((hotpotBlockEntity, pos) -> synchronizer.integrate(neighbors.size(), hotpotBlockEntity, pos));
    }

    public void removeContent(int pos, LevelBlockPos selfPos) {
        IHotpotContent content = contents.get(pos);
        soup.getContentViaHand(content, soup.getContentViaTableware(content, content.takeOut(null, this, selfPos), this, selfPos), this, selfPos);
        contents.set(pos, HotpotContentSerializers.getEmptyContent());
    }

    public void onRemove(LevelBlockPos pos) {
        IntStream.range(0, contents.size()).forEach(i -> removeContent(i, pos));
        markDataChanged();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, this::getUpdatePacketTag);
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.loadAdditional(compoundTag, registryAccess);

        canBeRemoved = !compoundTag.contains("CanBeRemoved", Tag.TAG_ANY_NUMERIC) || compoundTag.getBoolean("CanBeRemoved");
        infiniteWater = compoundTag.contains("InfiniteWater", Tag.TAG_ANY_NUMERIC) && compoundTag.getBoolean("InfiniteWater");
        time = compoundTag.contains("Time", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Time") : 0;
        velocity = compoundTag.contains("Velocity", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Velocity") : 0;
        soup = HotpotSoupTypeSerializers.loadSoup(compoundTag.getCompound("Soup"), registryAccess);

        if (compoundTag.contains("Contents", Tag.TAG_LIST)) {
            contents.clear();
            HotpotContentSerializers.loadContents(compoundTag.getList("Contents", Tag.TAG_COMPOUND), registryAccess, contents);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(compoundTag, registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putInt("Velocity", velocity);

        compoundTag.put("Soup", HotpotSoupTypeSerializers.saveSoup(soup, registryAccess));
        compoundTag.put("Contents", HotpotContentSerializers.saveContents(contents, registryAccess));
    }

    public CompoundTag getUpdatePacketTag(BlockEntity blockEntity, HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putInt("Velocity", velocity);
        compoundTag.put("Soup", HotpotSoupTypeSerializers.saveSoup(soup, registryAccess));

        if (!contentChanged) {
            return compoundTag;
        }

        compoundTag.put("Contents", HotpotContentSerializers.saveContents(contents, registryAccess));

        contentChanged = false;
        return compoundTag;
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = super.getUpdateTag(registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putInt("Velocity", velocity);
        compoundTag.put("Soup", HotpotSoupTypeSerializers.saveSoup(soup, registryAccess));
        compoundTag.put("Contents", HotpotContentSerializers.saveContents(contents, registryAccess));

        return compoundTag;
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public boolean hasEmptyContent() {
        return contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public static boolean hasEmptyContent(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return hotpotBlockEntity.hasEmptyContent();
    }

    public boolean isEmptyContent(int pos) {
        return getContent(pos) instanceof HotpotEmptyContent;
    }

    public void setContent(int i, IHotpotContent content) {
        contents.set(i, content);
        markDataChanged();
    }

    public NonNullList<IHotpotContent> getContents() {
        return contents;
    }

    public IHotpotContent getContent(int pos) {
        return contents.get(pos);
    }

    public IHotpotSoup getSoup() {
        return soup;
    }

    public float getWaterLevel() {
        return getSoup().getWaterLevel();
    }

    public void setWaterLevel(float waterLevel, LevelBlockPos pos) {
        soup.setWaterLevel(this, pos, waterLevel);
    }

    public int getTime() {
        return time;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
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

        hotpotBlockEntity.time += 1 + hotpotBlockEntity.velocity;
        hotpotBlockEntity.velocity = Math.max(0, hotpotBlockEntity.velocity - 1);

        if (!hotpotBlockEntity.isSoupSynchronized()) {
            hotpotBlockEntity.synchronizeSoup(selfPos);
        }

        hotpotBlockEntity.setSoupSynchronized(false);
        float tickSpeed = hotpotBlockEntity.soup.getContentTickSpeed(hotpotBlockEntity, selfPos);

        if (hotpotBlockEntity.getWaterLevel() > 0.0f) {
            IntStream.range(0, hotpotBlockEntity.contents.size()).forEach(i -> tickContents(i, hotpotBlockEntity, selfPos, tickSpeed));
        }

        level.sendBlockUpdated(pos, state, state, 3);
        hotpotBlockEntity.setChanged();
    }

    public static void tickContents(int pos, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos, float tickSpeed) {
        IHotpotContent content = hotpotBlockEntity.contents.get(pos);

        if (content.tick(hotpotBlockEntity, selfPos, tickSpeed)) {
            hotpotBlockEntity.soup.onContentUpdate(content, hotpotBlockEntity, selfPos);
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

        return selfBlockEntity.getSoup().getSoupTypeHolder().equals(hotpotBlockEntity.getSoup().getSoupTypeHolder());
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
}
