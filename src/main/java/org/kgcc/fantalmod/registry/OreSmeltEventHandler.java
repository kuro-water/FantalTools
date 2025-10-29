package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.skill.SmeltSkill;
import org.kgcc.fantalmod.util.FantalStateManager;

import java.util.List;
import java.util.Optional;

public class OreSmeltEventHandler {
    /**
     * <p>ブロック破壊時に精錬スキルが有効なプレイヤーの場合、
     * 精錬されたアイテムをドロップさせるイベントハンドラ</p>
     * <p>{@link SmeltSkill#isActive(PlayerEntity)}を参照する</p>
     */
    public static void register() {
        // ブロック破壊前イベントを登録
        // true:バニラのドロップと破壊を行う
        // false:バニラのドロップと破壊をキャンセルし、このイベントでドロップを行う
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, blockState, blockEntity) -> {
            if (!SmeltSkill.isActive(player)) {
                return true;
            }
            if (world.isClient()) {
                return true;
            }
            if (!(world instanceof ServerWorld serverWorld)) {
                return true;
            }
            
            List<ItemStack> drops = Block.getDroppedStacks(blockState, serverWorld, pos, blockEntity);
            if (drops.isEmpty()) {
                return true;
            }
            
            ItemStack itemStack = drops.get(0);
            String translationKey = itemStack.getTranslationKey();
            int dropCount = drops.size();
            
            // ドロップアイテムのtranslationキーが一致しない=ドロップアイテムの種類が複数ある
            // つまりチェスト等を壊してドロップした場合なので、精錬しない
            // 幸運で同種アイテムが複数個ドロップする場合があるので、こんなチェックをしている
            for (ItemStack drop : drops) {
                if (!drop.getTranslationKey().equals(translationKey)) {
                    return true;
                }
            }
            
            
            // 精錬レシピの検索
            // RecipeType.BLASTINGは、鉱石等の精錬レシピ（溶鉱炉のレシピ）のみを検索する
            RecipeManager recipeManager = serverWorld.getRecipeManager();
            Optional<BlastingRecipe> recipeOpt
                    = recipeManager.getFirstMatch(RecipeType.BLASTING, new SimpleInventory(itemStack), serverWorld);
            
            if (recipeOpt.isEmpty()) {
                // 精錬レシピがない
                return true;
            }
            
            ItemStack result = recipeOpt.get().getOutput(serverWorld.getRegistryManager());
            if (result.isEmpty()) {
                // 精錬結果が空のアイテムスタック
                return true;
            }
            
            // ブロックを手動で壊す
            serverWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
            Vec3d vec = Vec3d.ofCenter(pos);
            MinecraftServer server = world.getServer();
            ItemStack hand = player.getStackInHand(player.getActiveHand());
            for (int i = 0; i < dropCount; i++) {
                // ドロップアイテムを生成
                serverWorld.spawnEntity(new ItemEntity(serverWorld, vec.x, vec.y, vec.z, result.copy()));
                
                if (!player.isCreative()) {
                    // 本来発生する耐久値減少処理もキャンセルしてしまうので、ここで減らす
                    hand.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                }
                
                // 侵食
                FantalStateManager.addFantalPollution(server, player, 1);
            }
            
            return false; // バニラのドロップと破壊をキャンセル
        });
    }
}