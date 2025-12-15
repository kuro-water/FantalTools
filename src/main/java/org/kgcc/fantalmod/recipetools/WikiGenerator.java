package org.kgcc.fantalmod.recipetools;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Wiki ページを一括生成
 */
public class WikiGenerator {
    private static final String RECIPES_DIR = "src/main/resources/data/fantalmod/recipes";
    private static final String WIKI_OUTPUT_DIR = "wiki";

    private final Path projectRoot;
    private final Path wikiDir;
    private final Gson gson;

    public WikiGenerator(Path projectRoot) {
        this.projectRoot = projectRoot;
        this.wikiDir = projectRoot.resolve(WIKI_OUTPUT_DIR);
        this.gson = new Gson();
    }

    public static void main(String[] args) {
        try {
            Path projectRoot = Paths.get(args.length > 0 ? args[0] : ".");
            WikiGenerator generator = new WikiGenerator(projectRoot);
            generator.generateAllPages();

            System.out.println("✓ Wiki ページの生成が完了しました");
            System.out.println("  出力先: " + generator.wikiDir.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("✗ Wiki 生成エラー:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * すべての Wiki ページを生成
     */
    public void generateAllPages() throws IOException {
        // wiki ディレクトリを作成
        Path itemsDir = wikiDir.resolve("items");
        Files.createDirectories(itemsDir);

        Path recipesPath = projectRoot.resolve(RECIPES_DIR);
        if (!Files.exists(recipesPath)) {
            System.err.println("✗ レシピディレクトリが見つかりません: " + recipesPath);
            return;
        }

        // レシピファイルを処理
        List<Path> jsonFiles = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(recipesPath, "*.json")) {
            for (Path file : stream) {
                jsonFiles.add(file);
            }
        }

        System.out.println("見つかったレシピファイル: " + jsonFiles.size());

        int successCount = 0;
        for (Path jsonFile : jsonFiles) {
            try {
                if (processRecipeFile(jsonFile, itemsDir)) {
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("✗ 処理失敗: " + jsonFile.getFileName() + " - " + e.getMessage());
            }
        }

        System.out.println("✓ Wiki ページ生成成功: " + successCount + " / " + jsonFiles.size());

        // README を生成
        generateReadme(itemsDir, successCount);
    }

    /**
     * 単一のレシピファイルを処理
     */
    private boolean processRecipeFile(Path jsonFile, Path itemsDir) throws IOException {
        String content = Files.readString(jsonFile);
        JsonElement element = JsonParser.parseString(content);

        if (!element.isJsonObject()) {
            return false;
        }

        JsonObject recipeJson = element.getAsJsonObject();
        String type = recipeJson.has("type") ? recipeJson.get("type").getAsString() : "";
        String recipeName = jsonFile.getFileName().toString().replace(".json", "");

        // サポートされているレシピタイプのみ処理
        if (!isSupportedRecipeType(type)) {
            System.out.println("⊘ スキップ（未対応タイプ）: " + jsonFile.getFileName());
            return false;
        }

        // RecipeData を解析（RecipeImageGenerator の parseXxxRecipe メソッドと同じ処理）
        RecipeData recipe = parseRecipe(recipeJson, recipeName, type);

        if (recipe == null) {
            return false;
        }

        // WikiData を作成
        String imageName = recipeName + ".png";
        WikiData wikiData = WikiData.fromRecipe(recipe, imageName);

        // Wiki ページを生成
        Path outputPath = itemsDir.resolve(recipeName + ".md");
        WikiPageBuilder.generatePage(wikiData, outputPath);

        return true;
    }

    /**
     * レシピタイプがサポートされているか確認
     */
    private boolean isSupportedRecipeType(String type) {
        return type.equals("minecraft:crafting_shaped") ||
               type.equals("minecraft:crafting_shapeless") ||
               type.equals("minecraft:smelting");
    }

    /**
     * レシピを解析（RecipeImageGenerator と同じロジック）
     */
    private RecipeData parseRecipe(JsonObject json, String recipeName, String type) {
        try {
            if (type.equals("minecraft:crafting_shaped")) {
                return parseShapedRecipe(json, recipeName);
            } else if (type.equals("minecraft:crafting_shapeless")) {
                return parseShapelessRecipe(json, recipeName);
            } else if (type.equals("minecraft:smelting")) {
                return parseSmeltingRecipe(json, recipeName);
            }
        } catch (Exception e) {
            System.err.println("  → パース失敗: " + e.getMessage());
        }
        return null;
    }

    // ...既存コード（RecipeImageGenerator の parseXxxRecipe メソッドと同じ）...

    private RecipeData parseShapedRecipe(JsonObject json, String recipeName) {
        JsonArray patternArray = json.getAsJsonArray("pattern");
        List<String> pattern = new ArrayList<>();
        for (JsonElement elem : patternArray) {
            pattern.add(elem.getAsString());
        }

        JsonObject keyObj = json.getAsJsonObject("key");
        Map<String, String> keyToItem = new HashMap<>();
        for (String key : keyObj.keySet()) {
            String itemId = keyObj.getAsJsonObject(key).get("item").getAsString();
            keyToItem.put(key, itemId);
        }

        String resultItem = json.getAsJsonObject("result").get("item").getAsString();
        return RecipeData.createShaped(recipeName, pattern, keyToItem, resultItem);
    }

    private RecipeData parseShapelessRecipe(JsonObject json, String recipeName) {
        JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
        List<String> ingredients = new ArrayList<>();
        for (JsonElement elem : ingredientsArray) {
            if (elem.isJsonObject()) {
                String itemId = elem.getAsJsonObject().get("item").getAsString();
                ingredients.add(itemId);
            }
        }

        JsonObject resultObj = json.getAsJsonObject("result");
        String resultItem = resultObj.get("item").getAsString();
        int resultCount = resultObj.has("count") ? resultObj.get("count").getAsInt() : 1;

        return RecipeData.createShapeless(recipeName, ingredients, resultItem, resultCount);
    }

    private RecipeData parseSmeltingRecipe(JsonObject json, String recipeName) {
        JsonElement ingredientElem = json.get("ingredient");
        String ingredient = ingredientElem.isJsonObject() ?
            ingredientElem.getAsJsonObject().get("item").getAsString() :
            ingredientElem.getAsString();

        String resultItem = json.get("result").getAsString();
        float experience = json.has("experience") ? json.get("experience").getAsFloat() : 0;
        int cookingtime = json.has("cookingtime") ? json.get("cookingtime").getAsInt() : 200;

        return RecipeData.createSmelting(recipeName, ingredient, resultItem, experience, cookingtime);
    }

    /**
     * Wiki の README を生成
     */
    private void generateReadme(Path itemsDir, int pageCount) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append("Fantalmod アイテム Wiki\n\n");
        sb.append("このページは自動生成されます。\n\n");
        sb.append("## アイテム一覧\n\n");

        // items ディレクトリ内のファイルを列挙
        List<Path> pages = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(itemsDir, "*.md")) {
            for (Path file : stream) {
                pages.add(file);
            }
        }

        // アイテム名でソート
        pages.sort(Comparator.comparing(p -> p.getFileName().toString()));

        for (Path page : pages) {
            String filename = page.getFileName().toString();
            String itemName = filename.replace(".md", "");
            sb.append("- [").append(itemName).append("](items/").append(filename).append(")\n");
        }

        Path readmePath = wikiDir.resolve("Home.md");
        Files.writeString(readmePath, sb.toString());
        System.out.println("✓ README 生成: Home.md");
    }
}
