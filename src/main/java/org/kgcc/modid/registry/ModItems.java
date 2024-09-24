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
    }
}
