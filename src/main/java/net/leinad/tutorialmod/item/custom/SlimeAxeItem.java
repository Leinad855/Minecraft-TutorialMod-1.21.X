package net.leinad.tutorialmod.item.custom;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.leinad.tutorialmod.component.ModDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeAxeItem extends AxeItem{

    public SlimeAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (attacker instanceof PlayerEntity player) {
            if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                applyBounceEffect(target, attacker, true);
            }
            player.getItemCooldownManager().set(this, 20);
            return super.postHit(stack, target, attacker);
        }

        applyBounceEffect(target, attacker, true);

        return super.postHit(stack, target, attacker);
    }

    private void applyBounceEffect(LivingEntity target, LivingEntity attacker, boolean playerKnockBack) {
        Vec3d direction = target.getPos().subtract(attacker.getPos()).normalize();
        double horizontalForce = 0.5;
        double verticalForce = 0.4;

        target.addVelocity(direction.x * horizontalForce, verticalForce, direction.z * horizontalForce);
        target.velocityModified = true;

        target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
        ((ServerWorld)target.getWorld()).spawnParticles(
                ParticleTypes.ITEM_SLIME,
                target.getX(), target.getY() + 1, target.getZ(),
                10, 0.3, 0.3, 0.3, 0.05
        );

        if (target != attacker) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 40, -1));
        }

        if (attacker.isOnGround() && playerKnockBack) {
            attacker.addVelocity(0, 0.5f, 0);
            attacker.velocityModified = true;
        }else if (playerKnockBack){
            attacker.addVelocity(0, 1, 0);
            attacker.velocityModified = true;
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient && user instanceof PlayerEntity player) {
            int chargeTicks = getMaxUseTime(stack, user) - remainingUseTicks;
            float progress = Math.min(chargeTicks / 30f, 1.0f);

            int count = (int) (2 + progress * 10);

            for (int i = 0; i < count; i++) {
                double radius = 0.6 + progress * 0.4;
                double angle = Math.random() * 2 * Math.PI;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;

                world.addParticle(
                        ParticleTypes.ITEM_SLIME,
                        player.getX() + dx,
                        player.getY() + 1.0 + Math.random() * 0.3,
                        player.getZ() + dz,
                        0, 0.02, 0
                );
            }

            if (chargeTicks == 30) {
                world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_SLIME_BLOCK_PLACE,
                        SoundCategory.PLAYERS, 0.5f, 1.5f);
            } else if (chargeTicks == 60) {
                world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_SLIME_JUMP,
                        SoundCategory.PLAYERS, 0.8f, 0.8f);
            }
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient) return;
        if (!(user instanceof PlayerEntity player)) return;

        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getChargeProgress(i);
        if (f >= 1.0f && user.isOnGround()){
            createExpandingShockwave((ServerWorld) world, (PlayerEntity) user);
            applyAreaAttack(user, user);

            player.getItemCooldownManager().set(this, 40);

        }

    }

    private void applyAreaAttack(LivingEntity mainTarget, LivingEntity attacker) {
        World world = attacker.getWorld();

        double radius = 3.0;
        float areaDamage = 4.0F;

        applyBounceEffect(mainTarget, attacker, true);

        List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class,
                mainTarget.getBoundingBox().expand(radius),
                e -> e != attacker && e != mainTarget && e.isAlive() && !e.isInvulnerable());

        for (LivingEntity target : nearby) {

            target.damage(world.getDamageSources().playerAttack((PlayerEntity) attacker), areaDamage);
            applyBounceEffect(target, attacker, false);
        }
    }

    private void createExpandingShockwave(ServerWorld world, PlayerEntity player) {
        final int rings = 6;
        final double maxRadius = 6.0;

        for (int tick = 0; tick < rings; tick++) {
            int finalTick = tick;
            world.getServer().execute(() -> {
                double radius = (maxRadius / rings) * (finalTick + 1);
                int particleCount = 40 + finalTick * 10;

                for (int i = 0; i < particleCount; i++) {
                    double angle = (2 * Math.PI / particleCount) * i;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;
                    double y = player.getY() + 1.0;

                    world.spawnParticles(ParticleTypes.ITEM_SLIME, x, y, z, 1, 0, 0, 0, 0);
                }
            });
        }

    }

    public static float getChargeProgress(int useTicks) {
        return useTicks / 20.0F;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 60;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }
}
