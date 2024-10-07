package org.kgcc.modid.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.kgcc.modid.*;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static List<Item> ItemList = new ArrayList<>();
    public static List<Block> BlockList = new ArrayList<>();

    public static void registerItems() {
//        ItemList.add(TestItem.register(new TestItem(), "test_item"));
//        ItemList.add(UsableItem.register(new UsableItem(), "usable_item"));
        ItemList.add(FantalIngotItem.register(new FantalIngotItem(), "fantal_ingot"));
        ItemList.add(RowFantalItem.register(new RowFantalItem(), "row_fantal"));

        BlockList.add(TestBlock.register(new TestBlock(), "test_block"));
        BlockList.add(FantalOreBlock.register(new FantalOreBlock(), "fantal_ore"));
        BlockList.add(FantalBlock.register(new FantalBlock(), "fantal_block"));


        ItemList.add(TutorialSwordItem.register(new TutorialSwordItem(), "fantal_sword"));
        ItemList.add(TutorialAxeItem.register(new TutorialAxeItem(), "fantal_axe"));
        ItemList.add(TutorialPickaxeItem.register(new TutorialPickaxeItem(), "fantal_pickaxe"));
        ItemList.add(TutorialShovelItem.register(new TutorialShovelItem(), "fantal_shovel"));
        ItemList.add(TutorialHoeItem.register(new TutorialHoeItem(), "fantal_hoe"));

        ItemList.add(TutorialHelmetItem.register(new TutorialHelmetItem(), "fantal_helmet"));
        ItemList.add(TutorialChestplateItem.register(new TutorialChestplateItem(), "fantal_chestplate"));
        ItemList.add(TutorialLegginsItem.register(new TutorialLegginsItem(), "fantal_leggings"));
        ItemList.add(TutorialBootsItem.register(new TutorialBootsItem(), "fantal_boots"));
    }
}
