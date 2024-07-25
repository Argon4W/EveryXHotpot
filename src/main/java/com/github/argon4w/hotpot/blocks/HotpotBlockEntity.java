package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupBaseRecipe;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupIngredientRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HotpotBlockEntity extends AbstractTablewareInteractiveBlockEntity {
    private boolean contentChanged = true;
    private boolean soupSynchronized = false;

    private NonNullList<IHotpotContent> contents = NonNullList.withSize(8, HotpotContents.buildEmptyContent());
    private IHotpotSoupType soup = HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildEmptySoup();
    public float renderedWaterLevel = -1f;
    private float waterLevel = 0f;
    private int time = 0;
    private int velocity = 0;
    private boolean infiniteWater = false;
    private boolean canBeRemoved = true;

    public HotpotBlockEntity(BlockPos pos, BlockState state) {
        super(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), pos, state);
    }

    private void tryFindEmptyContent(int hitPos, LevelBlockPos selfPos, TriConsumer<Integer, HotpotBlockEntity, LevelBlockPos> consumer) {
        new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos))
                .getFirst(10, (hotpotBlockEntity, pos) -> hotpotBlockEntity.hasEmptyContent(), (hotpotBlockEntity, pos) ->
                        Stream.of(getContentPos(hitPos), 0, 1, 2, 3, 4, 5, 6, 7)
                                .filter(hotpotBlockEntity::isEmptyContent)
                                .findFirst().ifPresent(contentPos -> consumer.accept(contentPos, hotpotBlockEntity, pos)));
    }

    @Override
    public ItemStack tryPlaceContentViaTableware(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        tryPlaceContentViaInteraction(hitPos, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        for (RecipeHolder<HotpotSoupBaseRecipe> holder : selfPos.level().getRecipeManager().getAllRecipesFor(HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_TYPE.get())) {
            HotpotSoupBaseRecipe recipe = holder.value();

            if (recipe.matches(itemStack) && getSoup().getResourceLocation().equals(recipe.getSourceSoup())) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, recipe.getRemainingItem()));

                setSoup(recipe.createResultSoup(), selfPos);
                setWaterLevel(recipe.getResultWaterLevel());

                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(recipe.getSoundEvent());
                selfPos.level().playSound(null, selfPos.pos(), soundEvent == null ? SoundEvents.EMPTY : soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);

                return;
            }
        }

        soup.interact(hitPos, player, hand, itemStack, this, selfPos).ifPresent(content -> tryFindEmptyContent(hitPos, selfPos, (p, hotpotBlockEntity, pos) -> {
            hotpotBlockEntity.placeContent(p, content, pos);
        }));
    }

    public void tryPlaceContent(int hitPos, IHotpotContent content, LevelBlockPos selfPos) {
        tryFindEmptyContent(hitPos, selfPos, (p, hotpotBlockEntity, pos) -> hotpotBlockEntity.placeContent(p, content, pos));
    }

    private void placeContent(int contentPos, IHotpotContent content, LevelBlockPos pos) {
        contents.set(contentPos, content);

        for (RecipeHolder<HotpotSoupIngredientRecipe> holder : pos.level().getRecipeManager().getAllRecipesFor(HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get())) {
            HotpotSoupIngredientRecipe recipe = holder.value();
            Optional<IHotpotSoupType> optional = recipe.matches(this, pos);

            if (optional.isEmpty()) {
                continue;
            }

            float waterLevel = getWaterLevel();

            setSoup(optional.get(), pos);
            setWaterLevel(waterLevel);
        }

        markDataChanged();
    }

    private void synchronizeSoup(LevelBlockPos selfPos) {
        if (soupSynchronized) {
            return;
        }

        Map<HotpotBlockEntity, LevelBlockPos> neighbors = new BlockEntityFinder<>(selfPos, HotpotBlockEntity.class, (hotpotBlockEntity, pos) -> isSameSoup(selfPos, pos) && !hotpotBlockEntity.soupSynchronized).getAll();
        neighbors.forEach((key, value) -> key.soupSynchronized = true);

        soup.getSynchronizer(this, selfPos).forEach(synchronizer -> {
            neighbors.forEach(synchronizer::collect);

            neighbors.forEach((hotpotBlockEntity, pos) -> {
                synchronizer.integrate(neighbors.size(), hotpotBlockEntity, pos);
            });
        });
    }

    public void tryTakeOutContentViaHand(Player player, int hitPos, LevelBlockPos pos) {
        int contentPos = getContentPos(hitPos);
        IHotpotContent content = contents.get(contentPos);

        if (!(content instanceof HotpotEmptyContent)) {
            soup.takeOutContentViaHand(content, soup.takeOutContentViaTableware(content, content.takeOut(player, this, pos), this, pos), this, pos);
            contents.set(contentPos, HotpotContents.buildEmptyContent());

            markDataChanged();
        }
    }

    @Override
    public ItemStack tryTakeOutContentViaTableware(Player player, int hitPos, LevelBlockPos pos) {
        int contentPos = getContentPos(hitPos);
        IHotpotContent content = contents.get(contentPos);

        if (!(content instanceof HotpotEmptyContent)) {
            ItemStack itemStack = soup.takeOutContentViaTableware(content, content.takeOut(player, this, pos), this, pos);

            contents.set(contentPos, HotpotContents.buildEmptyContent());
            markDataChanged();

            return itemStack;
        }

        return ItemStack.EMPTY;
    }

    public int getContentPos(int hitPos) {
        double size = (360f / 8f);
        double degree =  (time / 20f / 60f) * 360f + size / 2f;

        int root = (int) Math.floor((degree % 360f) / size);
        int contentPos = hitPos - root;

        return contentPos < 0 ? 8 + contentPos : contentPos;
    }

    public void onRemove(LevelBlockPos pos) {
        for (int i = 0; i < contents.size(); i ++) {
            IHotpotContent content = contents.get(i);

            soup.takeOutContentViaHand(content, content.takeOut(null, this, pos), this, pos);
            contents.set(i, HotpotContents.buildEmptyContent());
        }

        markDataChanged();
    }

    public void setSoup(IHotpotSoupType soup, LevelBlockPos pos) {
        this.soup = soup;
        markDataChanged();
        pos.markAndNotifyBlock();

        boolean hotpotLit = this.soup.isHotpotLit(this, pos);
        pos.setBlockState(pos.getBlockState().setValue(HotpotBlock.HOTPOT_LIT, hotpotLit));
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.loadAdditional(compoundTag, registryAccess);

        canBeRemoved = !compoundTag.contains("CanBeRemoved", Tag.TAG_ANY_NUMERIC) || compoundTag.getBoolean("CanBeRemoved");
        infiniteWater = compoundTag.contains("InfiniteWater", Tag.TAG_ANY_NUMERIC) && compoundTag.getBoolean("InfiniteWater");
        time = compoundTag.contains("Time", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Time") : 0;
        velocity = compoundTag.contains("Velocity", Tag.TAG_ANY_NUMERIC) ? compoundTag.getInt("Velocity") : 0;
        waterLevel = compoundTag.contains("WaterLevel", Tag.TAG_FLOAT) ?compoundTag.getFloat("WaterLevel") : 0f;

        if (compoundTag.contains("Soup", Tag.TAG_COMPOUND)) {
            soup = HotpotSoupTypes.loadSoup(compoundTag.getCompound("Soup"));
        }

        if (compoundTag.contains("Contents", Tag.TAG_LIST)) {
            contents.clear();
            HotpotContents.loadContents(compoundTag.getList("Contents", Tag.TAG_COMPOUND), registryAccess, contents);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(compoundTag, registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putInt("Velocity", velocity);
        compoundTag.putFloat("WaterLevel", waterLevel);

        compoundTag.put("Soup", HotpotSoupTypes.saveSoup(soup, registryAccess));
        compoundTag.put("Contents", HotpotContents.saveContents(contents, registryAccess));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (entity, access) -> {
            CompoundTag compoundTag = new CompoundTag();

            compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
            compoundTag.putBoolean("InfiniteWater", infiniteWater);
            compoundTag.putInt("Time", time);
            compoundTag.putInt("Velocity", velocity);
            compoundTag.putFloat("WaterLevel", waterLevel);

            if (contentChanged) {
                compoundTag.put("Soup", HotpotSoupTypes.saveSoup(soup, access));
                compoundTag.put("Contents", HotpotContents.saveContents(contents, access));

                contentChanged = false;
            }

            return compoundTag;
        });
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = super.getUpdateTag(registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteWater", infiniteWater);
        compoundTag.putInt("Time", time);
        compoundTag.putInt("Velocity", velocity);
        compoundTag.putFloat("WaterLevel", waterLevel);

        compoundTag.put("Soup", HotpotSoupTypes.saveSoup(soup, registryAccess));
        compoundTag.put("Contents", HotpotContents.saveContents(contents, registryAccess));

        return compoundTag;
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public NonNullList<IHotpotContent> getContents() {
        return contents;
    }

    public List<IHotpotContent> copyContents() {
        return Arrays.asList(getContents().toArray(new IHotpotContent[0]));
    }

    public void setContents(NonNullList<IHotpotContent> newContents) {
        contents = newContents;
        markDataChanged();
    }

    public boolean hasEmptyContent() {
        return contents.stream().anyMatch(content -> content instanceof HotpotEmptyContent);
    }

    public boolean isEmptyContent(int pos) {
        return getContent(pos) instanceof HotpotEmptyContent;
    }

    public IHotpotContent getContent(int pos) {
        return contents.get(pos);
    }

    public IHotpotSoupType getSoup() {
        return soup;
    }

    public LevelBlockPos getPos() {
        return new LevelBlockPos(level, worldPosition);
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(float waterLevel) {
        soup.setWaterLevel(this, getPos(), waterLevel);
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

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotBlockEntity blockEntity) {
        LevelBlockPos selfPos = new LevelBlockPos(level, pos);

        blockEntity.time += 1 + blockEntity.velocity;
        blockEntity.velocity = Math.max(0, blockEntity.velocity - 1);
        blockEntity.synchronizeSoup(selfPos);
        blockEntity.soupSynchronized = false;

        blockEntity.waterLevel = blockEntity.soup.getWaterLevel();

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

    public static void tickContent(HotpotBlockEntity blockEntity, LevelBlockPos selfPos) {
        for (int i = 0; i < blockEntity.contents.size(); i ++) {
            IHotpotContent content = blockEntity.contents.get(i);

            if (content.tick(blockEntity, selfPos)) {
                blockEntity.soup.contentUpdate(content, blockEntity, selfPos);
                blockEntity.markDataChanged();
            }

            if (content.shouldRemove(blockEntity, selfPos)) {
                selfPos.dropItemStack(content.takeOut(null, blockEntity, selfPos));
                blockEntity.contents.set(i, HotpotContents.buildEmptyContent());
            }
        }
    }

    public static boolean isSameSoup(LevelBlockPos selfPos, LevelBlockPos pos) {
        if (!(selfPos.getBlockEntity() instanceof HotpotBlockEntity selfBlockEntity)) {
            return false;
        }

        if (!(pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return false;
        }

        return selfBlockEntity.getSoup().getResourceLocation().equals(hotpotBlockEntity.getSoup().getResourceLocation());
    }

    public static int getHitPos(BlockPos blockPos, Vec3 pos) {
        Vec3 vec = pos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double x = vec.x() - 0.5f;
        double z = vec.z() - 0.5f;

        double size = (360f / 8f);
        double degree = Math.atan2(x, z) / Math.PI * 180f + size / 2f;
        degree = degree < 0f ? degree + 360f : degree;

        return (int) Math.floor(degree / size);
    }
}
