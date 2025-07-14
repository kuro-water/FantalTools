// src/main/java/org/kgcc/fantalmod/registry/OreSmeltEventHandler.java
package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.skill.PickaxeSkill.SmeltSkill;

import java.util.List;
import java.util.Optional;

/**
 * OreSmeltEventHandler
 *
 * <p>ブロック破壊時に精錬スキルが有効なプレイヤーに対して、
 * ブロックのドロップを精錬されたアイテムに置き換えるイベントハンドラ</p>
 * <p>{@link SmeltSkill#isActive(PlayerEntity)}を参照する</p>
 */
public class OreSmeltEventHandler {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return true;
            if (!(world instanceof ServerWorld serverWorld)) return true;
            if (!SmeltSkill.isActive(player)) return true;

            BlockState blockState = serverWorld.getBlockState(pos);

            List<ItemStack> drops = Block.getDroppedStacks(blockState, serverWorld, pos, blockEntity);

            if(drops.isEmpty()){
                return true;
            }

            ItemStack itemStack = drops.get(0);
            String translationKey = itemStack.getTranslationKey();
            int dropCount = drops.size();

            for(ItemStack drop : drops){
                if(!drop.getTranslationKey().equals(translationKey)){
                    return true;
                }
            }

//             精錬レシピを検索
            Optional<BlastingRecipe> recipeOpt = serverWorld.getRecipeManager()
                    .getFirstMatch(RecipeType.BLASTING, new SimpleInventory(itemStack), serverWorld);

            if (recipeOpt.isEmpty()) {
                return true;
            }

            ItemStack result = recipeOpt.get().getOutput(serverWorld.getRegistryManager());
            if (result.isEmpty()) {
                return true;
            }

            // ブロックを手動で壊す
            serverWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
            Vec3d vec = Vec3d.ofCenter(pos);
            for(int i = 0; i < dropCount; i++) {
                // ドロップアイテムを生成
                serverWorld.spawnEntity(new ItemEntity(serverWorld, vec.x, vec.y, vec.z, result.copy()));
            }

            return false; // バニラのドロップと破壊をキャンセル
        });
    }
}