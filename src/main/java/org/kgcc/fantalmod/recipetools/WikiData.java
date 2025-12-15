package org.kgcc.fantalmod.recipetools;

/**
 * Wiki ページ生成用のデータモデル（レコードクラス）
 */
public record WikiData(
    String itemId,
    String itemName,
    String recipeType,
    String recipeImage,
    String ingredients,
    String result,
    long lastUpdated
) {
    public static WikiData fromRecipe(RecipeData recipe, String imageName) {
        String ingredients = formatIngredients(recipe);
        String result = recipe.resultItem() + (recipe.resultCount() > 1 ? "×" + recipe.resultCount() : "");
        
        return new WikiData(
            recipe.name(),
            recipe.name(),
            recipe.type(),
            imageName,
            ingredients,
            result,
            System.currentTimeMillis()
        );
    }
    
    private static String formatIngredients(RecipeData recipe) {
        if (recipe.type().equals("minecraft:crafting_shaped")) {
            return formatShapedIngredients(recipe);
        } else if (recipe.type().equals("minecraft:crafting_shapeless")) {
            return formatShapelessIngredients(recipe);
        } else if (recipe.type().equals("minecraft:smelting")) {
            return recipe.ingredient();
        }
        return "Unknown";
    }
    
    private static String formatShapedIngredients(RecipeData recipe) {
        return recipe.keyToItem().values().stream()
            .distinct()
            .reduce((a, b) -> a + ", " + b)
            .orElse("Unknown");
    }
    
    private static String formatShapelessIngredients(RecipeData recipe) {
        return String.join(", ", recipe.ingredients());
    }
}
