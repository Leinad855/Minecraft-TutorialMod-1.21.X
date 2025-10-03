package net.leinad.tutorialmod.item;

import com.mojang.datafixers.kinds.IdF;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.leinad.tutorialmod.TutorialMod;
import net.leinad.tutorialmod.item.custom.ChiselItem;
import net.leinad.tutorialmod.item.custom.CrabClawItem;
import net.leinad.tutorialmod.item.custom.PhantomClockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    public static final Item PINK_GARNET = registerItem("pink_garnet", new Item(new Item.Settings()));
    public static final Item RAW_PINK_GARNET = registerItem("raw_pink_garnet", new Item(new Item.Settings()));

    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(32)));

    public static final Item BISMUTH = registerItem("bismuth", new Item(new Item.Settings()));
    public static final Item RAW_BISMUTH = registerItem("raw_bismuth", new Item(new Item.Settings()));

    public static final Item PHANTOM_CLOCK = registerItem("phantom_clock", new PhantomClockItem(new Item.Settings().maxDamage(32)));
    public static final Item CRAB_CLAW = registerItem("crab_claw", new CrabClawItem(new Item.Settings().maxCount(1)));

    public static final Item CAULIFLOWER = registerItem("cauliflower", new Item(new Item.Settings().food(ModFoodComponents.CAULIFLOWER)) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.tutorialmod.cauliflower.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }
    });

    public static final Item STARLIGHT_ASHES = registerItem("starlight_ashes", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(TutorialMod.MOD_ID, name), item);
    }

    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registering Mod Items for " + TutorialMod.MOD_ID);
    }
}
