package org.kgcc.modid.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.kgcc.modid.*;

public class ModItems {
    public static final Item FANTAL_INGOT = FantalIngotItem.register(new FantalIngotItem(), "fantal_ingot");
    public static final Item ROW_FANTAL = RowFantalItem.register(new RowFantalItem(), "row_fantal");

    public static final Block TEST_BLOCK = TestBlock.register(new TestBlock(), "test_block");
    public static final Block FANTAL_ORE = FantalOreBlock.register(new FantalOreBlock(), "fantal_ore");
    public static final Block FANTAL_BLOCK = FantalBlock.register(new FantalBlock(), "fantal_block");

    public static final Item FANTAL_SWORD = FantalSwordItem.register(new FantalSwordItem(), "fantal_sword");
    public static final Item FANTAL_AXE = FantalAxeItem.register(new FantalAxeItem(), "fantal_axe");
    public static final Item FANTAL_PICKAXE = FantalPickaxeItem.register(new FantalPickaxeItem(), "fantal_pickaxe");
    public static final Item FANTAL_SHOVEL = FantalShovelItem.register(new FantalShovelItem(), "fantal_shovel");
    public static final Item FANTAL_HOE = FantalHoeItem.register(new FantalHoeItem(), "fantal_hoe");

    public static final Item FANTAL_HELMET = FantalHelmetItem.register(new FantalHelmetItem(), "fantal_helmet");
    public static final Item FANTAL_CHESTPLATE = FantalChestplateItem.register(new FantalChestplateItem(), "fantal_chestplate");
    public static final Item FANTAL_LEGGINGS = FantalLeggingsItem.register(new FantalLeggingsItem(), "fantal_leggings");
    public static final Item FANTAL_BOOTS = FantalBootsItem.register(new FantalBootsItem(), "fantal_boots");

//    public static final Item USABLE_ITEM = UsableItem.register(new UsableItem(), "usable_item");
}
