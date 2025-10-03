package net.leinad.tutorialmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class JumpBlock extends Block {
    public static final MapCodec<JumpBlock> CODEC = createCodec(JumpBlock::new);
    public static final IntProperty CHARGES = Properties.CHARGES;

    public JumpBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CHARGES, 0));
    }

    @Override
    public MapCodec<JumpBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int i = (Integer) state.get(CHARGES);

        if (i < 4) {
            world.setBlockState(pos, state.with(CHARGES, i + 1), Block.NOTIFY_ALL);
            world.playSound(player, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 3f, 1f);
        } else {
            world.setBlockState(pos, state.with(CHARGES, 0), Block.NOTIFY_ALL);
            world.playSound(player, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 3f, 1f);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        PlayerEntity user;

        if (entity.isPlayer()){
            user = (PlayerEntity) entity;
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 50, (Integer) state.get(CHARGES)));
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("tooltip.tutorialmod.jump_block.tooltip"));
        super.appendTooltip(stack, context, tooltip, options);
    }
}
