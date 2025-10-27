package org.kgcc.fantalmod.recipetools;

import java.util.*;

/**
 * クラフトレシピのデータを表すレコードクラス
 * shaped、shapeless、smelting などのレシピタイプに対応
 */
public record RecipeData(
    String name,
    String type,
    List<String> pattern,           // shaped の場合のみ使用
    Map<String, String> keyToItem,  // shaped の場合のみ使用
    List<String> ingredients,        // shapeless の場合のみ使用
    String ingredient,               // smelting の場合のみ使用
    String resultItem,
    int resultCount,
    float experience,
    int cookingTime
) {
    // レコードのコンストラクタはコンパイラが自動生成

    /**
     * shaped レシピ用のファクトリメソッド
     */
    public static RecipeData createShaped(String name, List<String> pattern,
                                         Map<String, String> keyToItem, String resultItem) {
        return new RecipeData(name, "minecraft:crafting_shaped", pattern, keyToItem,
                            null, null, resultItem, 1, 0, 0);
    }

    /**
     * shapeless レシピ用のファクトリメソッド
     */
    public static RecipeData createShapeless(String name, List<String> ingredients,
                                            String resultItem, int resultCount) {
        return new RecipeData(name, "minecraft:crafting_shapeless", null, null,
                            ingredients, null, resultItem, resultCount, 0, 0);
    }

    /**
     * smelting レシピ用のファクトリメソッド
     */
    public static RecipeData createSmelting(String name, String ingredient,
                                           String resultItem, float experience, int cookingTime) {
        return new RecipeData(name, "minecraft:smelting", null, null,
                            null, ingredient, resultItem, 1, experience, cookingTime);
    }

    /**
     * shaped レシピ用：パターンから 3x3 グリッドを生成
     */
    public String[][] getGridItems() {
        if (!type.equals("minecraft:crafting_shaped") || pattern == null) {
            return null;
        }

        String[][] grid = new String[3][3];

        // 初期化（すべて null）
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = null;
            }
        }

        // パターンを3x3グリッドに埋め込む
        int startRow = (3 - pattern.size()) / 2;
        for (int i = 0; i < pattern.size(); i++) {
            String patternLine = pattern.get(i);
            int startCol = (3 - patternLine.length()) / 2;
            for (int j = 0; j < patternLine.length(); j++) {
                char c = patternLine.charAt(j);
                if (c != ' ') {
                    String itemId = keyToItem.get(String.valueOf(c));
                    grid[startRow + i][startCol + j] = itemId;
                }
            }
        }

        return grid;
    }
}

