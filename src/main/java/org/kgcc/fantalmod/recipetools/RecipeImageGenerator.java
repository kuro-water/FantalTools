package org.kgcc.fantalmod.recipetools;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * レシピ JSON ファイルから画像を生成するメインクラス
 */
public class RecipeImageGenerator {
    private static final String RECIPES_DIR = "src/main/resources/data/fantalmod/recipes";
    private static final String TEXTURES_DIR = "src/main/resources/assets/fantalmod/textures";
    private static final String MINECRAFT_TEXTURES_DIR = "src/main/resources/assets/minecraft/textures";

    private final Path projectRoot;
    private final Path outputDir;
    private final Gson gson;
    private final TextureCache textureCache;

    public RecipeImageGenerator(Path projectRoot, Path outputDir) {
        this.projectRoot = projectRoot;
        this.outputDir = outputDir;
        this.gson = new Gson();
        this.textureCache = new TextureCache(projectRoot, TEXTURES_DIR, MINECRAFT_TEXTURES_DIR);
    }

    public static void main(String[] args) {
        try {
            Path projectRoot = Paths.get(args.length > 1 ? args[1] : ".");
            Path outputDir = Paths.get(args.length > 0 ? args[0] : "recipe_images");
            
            // items サブディレクトリを追加
            Path itemsDir = outputDir.resolve("items");

            // 出力ディレクトリを作成
            Files.createDirectories(itemsDir);

            RecipeImageGenerator generator = new RecipeImageGenerator(projectRoot, itemsDir);
            generator.generateAllRecipes();

            System.out.println("✓ レシピ画像の生成が完了しました");
            System.out.println("  出力先: " + itemsDir.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("✗ エラーが発生しました:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * すべてのレシピ JSON ファイルを処理
     */
    public void generateAllRecipes() throws IOException {
        Path recipesPath = projectRoot.resolve(RECIPES_DIR);

        if (!Files.exists(recipesPath)) {
            System.err.println("✗ レシピディレクトリが見つかりません: " + recipesPath);
            return;
        }

        List<Path> jsonFiles = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(recipesPath, "*.json")) {
            for (Path file : stream) {
                jsonFiles.add(file);
            }
        }

        System.out.println("見つかったレシピファイル: " + jsonFiles.size());

        for (Path jsonFile : jsonFiles) {
            try {
                processRecipeFile(jsonFile);
            } catch (Exception e) {
                System.err.println("✗ 処理失敗: " + jsonFile.getFileName() + " - " + e.getMessage());
            }
        }
    }

    /**
     * 単一のレシピ JSON ファイルを処理
     */
    private void processRecipeFile(Path jsonFile) throws IOException {
        String content = Files.readString(jsonFile);
        JsonElement element = JsonParser.parseString(content);

        if (!element.isJsonObject()) {
            System.out.println("⊘ スキップ（無効な形式）: " + jsonFile.getFileName());
            return;
        }

        JsonObject recipeJson = element.getAsJsonObject();
        String type = recipeJson.has("type") ? recipeJson.get("type").getAsString() : "";
        String recipeName = jsonFile.getFileName().toString().replace(".json", "");

        RecipeData recipe = null;

        // レシピタイプに応じて解析
        if ("minecraft:crafting_shaped".equals(type)) {
            recipe = parseShapedRecipe(recipeJson, recipeName);
        } else if ("minecraft:crafting_shapeless".equals(type)) {
            recipe = parseShapelessRecipe(recipeJson, recipeName);
        } else if ("minecraft:smelting".equals(type)) {
            recipe = parseSmeltingRecipe(recipeJson, recipeName);
        } else {
            System.out.println("⊘ スキップ（未対応タイプ）: " + jsonFile.getFileName() + " (" + type + ")");
            return;
        }

        if (recipe != null) {
            RecipeRenderer renderer = new RecipeRenderer(textureCache);
            Path outputPath = outputDir.resolve(recipeName + ".png");
            renderer.renderRecipe(recipe, outputPath);
            System.out.println("✓ 生成完了: " + recipeName + ".png (" + type + ")");
        }
    }

    /**
     * shaped レシピをパース
     */
    private RecipeData parseShapedRecipe(JsonObject json, String recipeName) {
        try {
            // pattern の取得
            JsonArray patternArray = json.getAsJsonArray("pattern");
            List<String> pattern = new ArrayList<>();
            for (JsonElement elem : patternArray) {
                pattern.add(elem.getAsString());
            }

            // key の取得
            JsonObject keyObj = json.getAsJsonObject("key");
            Map<String, String> keyToItem = new HashMap<>();
            for (String key : keyObj.keySet()) {
                JsonObject itemObj = keyObj.getAsJsonObject(key);
                String itemId = itemObj.get("item").getAsString();
                keyToItem.put(key, itemId);
            }

            // result の取得
            JsonObject resultObj = json.getAsJsonObject("result");
            String resultItem = resultObj.get("item").getAsString();

            return RecipeData.createShaped(recipeName, pattern, keyToItem, resultItem);
        } catch (Exception e) {
            System.err.println("  → パース失敗: " + e.getMessage());
            return null;
        }
    }

    /**
     * shapeless レシピをパース
     */
    private RecipeData parseShapelessRecipe(JsonObject json, String recipeName) {
        try {
            // ingredients の取得
            JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
            List<String> ingredients = new ArrayList<>();
            for (JsonElement elem : ingredientsArray) {
                if (elem.isJsonObject()) {
                    JsonObject itemObj = elem.getAsJsonObject();
                    if (itemObj.has("item")) {
                        ingredients.add(itemObj.get("item").getAsString());
                    }
                }
            }

            // result の取得
            JsonObject resultObj = json.getAsJsonObject("result");
            String resultItem = resultObj.get("item").getAsString();
            int resultCount = resultObj.has("count") ? resultObj.get("count").getAsInt() : 1;

            return RecipeData.createShapeless(recipeName, ingredients, resultItem, resultCount);
        } catch (Exception e) {
            System.err.println("  → パース失敗: " + e.getMessage());
            return null;
        }
    }

    /**
     * smelting レシピをパース
     */
    private RecipeData parseSmeltingRecipe(JsonObject json, String recipeName) {
        try {
            // ingredient の取得
            JsonElement ingredientElem = json.get("ingredient");
            String ingredient;
            if (ingredientElem.isJsonObject()) {
                ingredient = ingredientElem.getAsJsonObject().get("item").getAsString();
            } else {
                ingredient = ingredientElem.getAsString();
            }

            // result の取得
            String resultItem = json.get("result").getAsString();

            // experience と cookingtime の取得
            float experience = json.has("experience") ? json.get("experience").getAsFloat() : 0;
            int cookingtime = json.has("cookingtime") ? json.get("cookingtime").getAsInt() : 200;

            return RecipeData.createSmelting(recipeName, ingredient, resultItem, experience, cookingtime);
        } catch (Exception e) {
            System.err.println("  → パース失敗: " + e.getMessage());
            return null;
        }
    }
}

