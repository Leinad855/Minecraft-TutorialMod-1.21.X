package net.leinad.tutorialmod.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ButterflyRelicItem extends Item {
    public ButterflyRelicItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 25, 50));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 500, 1));

            itemStack.decrementUnlessCreative(1, user);
            user.getItemCooldownManager().set(this, 400);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
