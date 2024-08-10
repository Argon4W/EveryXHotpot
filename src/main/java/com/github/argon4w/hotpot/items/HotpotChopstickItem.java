package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.AbstractTablewareInteractiveBlockEntity;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotChopstickDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HotpotChopstickItem extends HotpotPlacementBlockItem<HotpotPlacedChopstick> implements IHotpotTablewareItem, IHotpotItemContainer {
    public HotpotChopstickItem() {
        super(HotpotPlacementSerializers.PLACED_CHOPSTICK_SERIALIZER, new Properties()
                .stacksTo(1)
                .component(HotpotModEntry.HOTPOT_CHOPSTICK_DATA_COMPONENT, HotpotChopstickDataComponent.EMPTY)
                .attributes(new ItemAttributeModifiers(List.of(
                        new ItemAttributeModifiers.Entry(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "chopstick_block_range_modifier"), 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND),
                        new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "chopstick_entity_range_modifier"), 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                ), true))
        );
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, HotpotPlacedChopstick placement, ItemStack itemStack) {
        placement.setChopstickItemSlot(itemStack);
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        return getHeldItemStack(itemStack.copy());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (isFood(heldItemStack) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        if (!player.getInventory().add(heldItemStack)) {
            player.drop(heldItemStack, false);
        }

        setHeldItemStack(itemStack, ItemStack.EMPTY);

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return itemStack;
        }

        if (isFood(heldItemStack)) {
            setHeldItemStack(itemStack, heldItemStack.finishUsingItem(level, livingEntity));
        }

        return itemStack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (isFood(heldItemStack)) {
            return heldItemStack.getUseAnimation();
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return 0;
        }

        if (isFood(heldItemStack)) {
            return 8;
        }

        return 0;
    }

    @Override
    public void tablewareInteract(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, AbstractTablewareInteractiveBlockEntity blockEntity, LevelBlockPos selfPos) {
        if (!(itemStack.getItem() instanceof HotpotChopstickItem)) {
            return;
        }

        ItemStack heldItemStack = HotpotChopstickItem.getHeldItemStack(itemStack);
        heldItemStack = heldItemStack.isEmpty() ? blockEntity.getContentViaTableware(player, hand, hitPos, layer, selfPos) : blockEntity.setContentViaTableware(hitPos, layer, player, hand, heldItemStack, selfPos);

        if (heldItemStack.getItem().canFitInsideContainerItems()) {
            HotpotChopstickItem.setHeldItemStack(itemStack, heldItemStack);
            return;
        }

        selfPos.dropItemStack(heldItemStack);
        HotpotChopstickItem.setHeldItemStack(itemStack, ItemStack.EMPTY);
    }

    public static boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD);
    }

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_CHOPSTICK_DATA_COMPONENT);
    }

    public static HotpotChopstickDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_CHOPSTICK_DATA_COMPONENT, HotpotChopstickDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotChopstickDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_CHOPSTICK_DATA_COMPONENT, dataComponent);
    }

    public static void setHeldItemStack(ItemStack chopstick, ItemStack itemStack) {
        setDataComponent(chopstick, new HotpotChopstickDataComponent(itemStack.copy()));
    }

    public static ItemStack getHeldItemStack(ItemStack itemStack) {
        return getDataComponent(itemStack).itemStack().copy();
    }
}
