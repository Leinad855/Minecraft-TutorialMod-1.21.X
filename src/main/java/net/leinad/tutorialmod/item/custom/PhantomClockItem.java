package net.leinad.tutorialmod.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class PhantomClockItem extends Item {
    public PhantomClockItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (world.getTimeOfDay() >= 13000 && world.getTimeOfDay() <= 23000){
            if (!world.isClient){
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 1, false, true));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 400, 1,false, true));

                world.playSound(
                        null,
                        user.getX(),
                        user.getY(),
                        user.getZ(),
                        SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.PLAYERS,
                        0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

                itemStack.damage(1, ((ServerWorld) world), ((ServerPlayerEntity) user),
                        item -> user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                user.getItemCooldownManager().set(this, 400);
            }
        }


        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.tutorialmod.phantom_clock.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
