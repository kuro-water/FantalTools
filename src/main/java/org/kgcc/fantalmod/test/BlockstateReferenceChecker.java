package org.kgcc.fantalmod.test;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class BlockstateReferenceChecker {
    /*jsonをチェックするやつだよ！
         でも信頼はしないでね！
    */
    private static final String BLOCKSTATES_DIR = "src/main/resources/assets/fantalmod/blockstates";
    private static final String MODELS_BLOCK_DIR = "src/main/resources/assets/fantalmod/models/block";
    private static final String MODELS_ITEM_DIR = "src/main/resources/assets/fantalmod/models/item";
    private static final String TEXTURES_BLOCK_DIR = "src/main/resources/assets/fantalmod/textures/block";
    private static final String TEXTURES_ITEM_DIR = "src/main/resources/assets/fantalmod/textures/item";

    public static void main(String[] args) throws IOException {
        // ブロックステート→モデル→テクスチャ
        Files.list(Paths.get(BLOCKSTATES_DIR))
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(BlockstateReferenceChecker::checkBlockstateFile);

        // アイテムモデル→テクスチャ
        Files.list(Paths.get(MODELS_ITEM_DIR))
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(BlockstateReferenceChecker::checkItemModelFile);
    }

    private static void checkBlockstateFile(Path blockstatePath) {
        try (Reader reader = Files.newBufferedReader(blockstatePath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject variants = json.getAsJsonObject("variants");
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                JsonObject variant = entry.getValue().getAsJsonObject();
                if (variant.has("model")) {
                    String modelPath = variant.get("model").getAsString();
                    String modelFile = modelPath.replace("fantalmod:", "")
                            .replace('/', File.separatorChar) + ".json";
                    Path modelFullPath = Paths.get(MODELS_BLOCK_DIR).getParent().resolve(modelFile);
                    if (!Files.exists(modelFullPath)) {
                        System.out.println("警告: モデルファイルが存在しません: " + modelFullPath);
                    } else {
                        checkModelTextures(modelFullPath, true);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("エラー: " + blockstatePath + " の解析中に例外が発生しました: " + e.getMessage());
        }
    }

    private static void checkItemModelFile(Path itemModelPath) {
        try (Reader reader = Files.newBufferedReader(itemModelPath)) {
            checkModelTextures(itemModelPath, false);
        } catch (Exception e) {
            System.out.println("エラー: " + itemModelPath + " のアイテムモデル解析中に例外が発生しました: " + e.getMessage());
        }
    }

    // isBlock: true=block, false=item
    private static void checkModelTextures(Path modelPath, boolean isBlock) {
        try (Reader reader = Files.newBufferedReader(modelPath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("textures")) {
                JsonObject textures = json.getAsJsonObject("textures");
                for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                    String textureFile = getTextureFile(isBlock, entry);
                    Path textureFullPath = Paths.get(isBlock ? TEXTURES_BLOCK_DIR : TEXTURES_ITEM_DIR).resolve(textureFile);
                    if (!Files.exists(textureFullPath)) {
                        System.out.println("警告: テクスチャファイルが存在しません: " + textureFullPath);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("エラー: " + modelPath + " のテクスチャ解析中に例外が発生しました: " + e.getMessage());
        }
    }
    
    private static @NotNull String getTextureFile(boolean isBlock, Map.Entry<String, JsonElement> entry) {
        String texturePath = entry.getValue().getAsString();
        if (texturePath.startsWith("fantalmod:")) {
            texturePath = texturePath.replace("fantalmod:", "");
        }
        String dir = isBlock ? "block/" : "item/";
        if (texturePath.startsWith(dir)) {
            texturePath = texturePath.substring(dir.length());
        }
        String textureFile = texturePath.replace('/', File.separatorChar) + ".png";
        return textureFile;
    }
}