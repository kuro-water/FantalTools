package org.kgcc.modid.registry;

import org.kgcc.modid.TestBlock;
import org.kgcc.modid.TestItem;
import org.kgcc.modid.UsableItem;

public class ModItems {
    public static void registerItems() {
        TestItem.initialize();
        TestBlock.initialize();
        UsableItem.register(new UsableItem(), "usable_item");
    }
}
