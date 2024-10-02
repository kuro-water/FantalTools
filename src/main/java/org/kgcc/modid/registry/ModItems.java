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


       ItemList.add(TutorialSwordItem.register(new TutorialSwordItem(), "tutorial_sword"));
       ItemList.add(TutorialAxeItem.register(new TutorialAxeItem(), "tutorial_axe"));
       ItemList.add(TutorialPickaxeItem.register(new TutorialPickaxeItem(), "tutorial_pickaxe"));
       ItemList.add(TutorialShovelItem.register(new TutorialShovelItem(), "tutorial_shovel"));
       ItemList.add(TutorialHoeItem.register(new TutorialHoeItem(), "tutorial_hoe"));

       ItemList.add(TutorialHelmetItem.register(new TutorialHelmetItem(), "tutorial_helmet"));
       ItemList.add(TutorialChestplateItem.register(new TutorialChestplateItem(), "tutorial_chestplate"));
       ItemList.add(TutorialLegginsItem.register(new TutorialLegginsItem(), "tutorial_leggins"));
       ItemList.add(TutorialBootsItem.register(new TutorialBootsItem(), "tutorial_boots"));
    }
}
