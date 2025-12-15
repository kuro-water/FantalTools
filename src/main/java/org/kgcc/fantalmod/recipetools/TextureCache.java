package org.kgcc.fantalmod.recipetools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * テクスチャをメモリにキャッシュして管理
 * MOD/バニラ問わず、アイテム・ブロックテクスチャ、ファイルシステム・JAR ファイルに対応
 */
public class TextureCache {
    private final Path projectRoot;
    private final Path modItemTexturesDir;
    private final Path modBlockTexturesDir;
    private final Path minecraftItemTexturesDir;
    private final Path minecraftBlockTexturesDir;
    private final Map<String, BufferedImage> cache = new HashMap<>();
    private final BufferedImage missingTexture;
    private JarFile minecraftJar;

    public TextureCache(Path projectRoot, String modTexturesPath, String minecraftTexturesPath) {
        this.projectRoot = projectRoot;
        this.modItemTexturesDir = projectRoot.resolve(modTexturesPath + "/item");
        this.modBlockTexturesDir = projectRoot.resolve(modTexturesPath + "/block");
        this.minecraftItemTexturesDir = projectRoot.resolve(minecraftTexturesPath + "/item");
        this.minecraftBlockTexturesDir = projectRoot.resolve(minecraftTexturesPath + "/block");
        this.missingTexture = createMissingTexture();
        this.minecraftJar = findMinecraftJar();
    }

    /**
     * アイテム/ブロック ID からテクスチャを取得
     * 例: "fantalmod:fantal_core" → アイテム/ブロック判定して取得
     * 例: "minecraft:stone" → stone.png
     */
    public BufferedImage getTexture(String itemId) {
        if (itemId == null) {
            return missingTexture;
        }

        if (cache.containsKey(itemId)) {
            return cache.get(itemId);
        }

        BufferedImage texture = loadTexture(itemId);
        cache.put(itemId, texture != null ? texture : missingTexture);
        
        if (texture == null) {
            System.out.println("  ⚠ テクスチャ読み込み失敗: " + itemId);
        }
        
        return cache.get(itemId);
    }

    /**
     * テクスチャファイルを読み込む（アイテムとブロック両方を試す）
     */
    private BufferedImage loadTexture(String itemId) {
        String[] parts = itemId.split(":");
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String textureName = parts.length > 1 ? parts[1] : parts[0];

        // MOD のテクスチャを試す（アイテム優先）
        if ("fantalmod".equals(namespace)) {
            BufferedImage texture = loadModTexture(textureName);
            if (texture != null) {
                System.out.println("  ✓ MOD テクスチャ読み込み成功: " + itemId);
                return texture;
            }
        }

        // Minecraft のテクスチャを試す（アイテム優先）
        if ("minecraft".equals(namespace)) {
            BufferedImage texture = loadMinecraftTexture(textureName);
            if (texture != null) {
                System.out.println("  ✓ バニラ テクスチャ読み込み成功: " + itemId);
                return texture;
            }
        }

        return null;
    }

    /**
     * MOD のテクスチャを読み込む（アイテム → ブロック の順で試す）
     */
    private BufferedImage loadModTexture(String textureName) {
        // まずアイテムテクスチャを試す
        Path itemPath = modItemTexturesDir.resolve(textureName + ".png");
        if (Files.exists(itemPath)) {
            try {
                return javax.imageio.ImageIO.read(itemPath.toFile());
            } catch (IOException e) {
                System.err.println("  MOD アイテムテクスチャ読み込み失敗: " + itemPath);
            }
        }

        // 次にブロックテクスチャを試す
        Path blockPath = modBlockTexturesDir.resolve(textureName + ".png");
        if (Files.exists(blockPath)) {
            try {
                return javax.imageio.ImageIO.read(blockPath.toFile());
            } catch (IOException e) {
                System.err.println("  MOD ブロックテクスチャ読み込み失敗: " + blockPath);
            }
        }

        return null;
    }

    /**
     * Minecraft のテクスチャを読み込む（ファイルシステム → JAR の順で試す）
     */
    private BufferedImage loadMinecraftTexture(String textureName) {
        System.out.println("    [DEBUG] バニラテクスチャ読み込み試行: " + textureName);
        
        // ファイルシステムのアイテムテクスチャを試す
        Path itemPath = minecraftItemTexturesDir.resolve(textureName + ".png");
        if (Files.exists(itemPath)) {
            try {
                System.out.println("      → item/ から読み込み成功");
                return javax.imageio.ImageIO.read(itemPath.toFile());
            } catch (IOException e) {
                System.err.println("  バニラ アイテムテクスチャ読み込み失敗: " + itemPath);
            }
        }

        // ファイルシステムのブロックテクスチャを試す
        Path blockPath = minecraftBlockTexturesDir.resolve(textureName + ".png");
        if (Files.exists(blockPath)) {
            try {
                System.out.println("      → block/ から読み込み成功");
                return javax.imageio.ImageIO.read(blockPath.toFile());
            } catch (IOException e) {
                System.err.println("  バニラ ブロックテクスチャ読み込み失敗: " + blockPath);
            }
        }

        // JAR ファイルからアイテムテクスチャを取得
        BufferedImage jarItemTexture = loadTextureFromJar("item", textureName);
        if (jarItemTexture != null) {
            System.out.println("      → JAR の item/ から読み込み成功");
            return jarItemTexture;
        }

        // JAR ファイルからブロックテクスチャを取得
        BufferedImage jarBlockTexture = loadTextureFromJar("block", textureName);
        if (jarBlockTexture != null) {
            System.out.println("      → JAR の block/ から読み込み成功");
            return jarBlockTexture;
        }

        // 特殊な場合：glass_pane のような pane/ 系は親ブロックのテクスチャを試す
        if (textureName.endsWith("_pane")) {
            String baseTextureName = textureName.substring(0, textureName.length() - 5); // "_pane" を削除
            System.out.println("      → _pane サフィックスを検出。親テクスチャを試行: " + baseTextureName);
            
            BufferedImage parentTexture = loadTextureFromJar("block", baseTextureName);
            if (parentTexture != null) {
                System.out.println("      → 親ブロックテクスチャから読み込み成功: " + baseTextureName);
                return parentTexture;
            }
        }

        System.out.println("      → テクスチャが見つかりません（item/ と block/ の両方で未検出）");
        return null;
    }

    /**
     * Gradle キャッシュの Minecraft JAR ファイルからテクスチャを取得
     */
    private BufferedImage loadTextureFromJar(String type, String textureName) {
        if (minecraftJar == null) {
            System.out.println("        [DEBUG] Minecraft JAR が見つかりません");
            return null;
        }

        try {
            // assets/minecraft/textures/{type}/{textureName}.png
            String entryPath = "assets/minecraft/textures/" + type + "/" + textureName + ".png";
            System.out.println("        [DEBUG] JAR 検索: " + entryPath);
            JarEntry entry = minecraftJar.getJarEntry(entryPath);

            if (entry != null) {
                System.out.println("        [DEBUG]   → 見つかりました");
                try (InputStream is = minecraftJar.getInputStream(entry)) {
                    BufferedImage image = javax.imageio.ImageIO.read(is);
                    return image;
                }
            }

            System.out.println("        [DEBUG]   → 見つかりません");

            // アイテムテクスチャが見つからない場合、ブロックテクスチャも試す（その逆も）
            if ("item".equals(type)) {
                String blockEntryPath = "assets/minecraft/textures/block/" + textureName + ".png";
                System.out.println("        [DEBUG] クロスチェック: " + blockEntryPath);
                JarEntry blockEntry = minecraftJar.getJarEntry(blockEntryPath);
                if (blockEntry != null) {
                    System.out.println("        [DEBUG]   → 見つかりました（block/）");
                    try (InputStream is = minecraftJar.getInputStream(blockEntry)) {
                        BufferedImage image = javax.imageio.ImageIO.read(is);
                        return image;
                    }
                }
                System.out.println("        [DEBUG]   → 見つかりません（block/）");
            } else if ("block".equals(type)) {
                String itemEntryPath = "assets/minecraft/textures/item/" + textureName + ".png";
                System.out.println("        [DEBUG] クロスチェック: " + itemEntryPath);
                JarEntry itemEntry = minecraftJar.getJarEntry(itemEntryPath);
                if (itemEntry != null) {
                    System.out.println("        [DEBUG]   → 見つかりました（item/）");
                    try (InputStream is = minecraftJar.getInputStream(itemEntry)) {
                        BufferedImage image = javax.imageio.ImageIO.read(is);
                        return image;
                    }
                }
                System.out.println("        [DEBUG]   → 見つかりません（item/）");
            }
        } catch (IOException e) {
            System.err.println("  JAR からテクスチャ読み込み失敗: " + type + "/" + textureName);
        }

        return null;
    }

    /**
     * Minecraft JAR ファイルを検索
     */
    private JarFile findMinecraftJar() {
        try {
            // Gradle キャッシュディレクトリを探索
            Path gradleCacheDir = projectRoot.resolve(".gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-project-root");
            
            if (!Files.exists(gradleCacheDir)) {
                return null;
            }

            // 最新の JAR ファイルを探す
            return Files.walk(gradleCacheDir)
                    .filter(p -> p.toString().endsWith(".jar") && !p.toString().contains("-sources"))
                    .findFirst()
                    .map(p -> {
                        try {
                            return new JarFile(p.toFile());
                        } catch (IOException e) {
                            System.err.println("  Minecraft JAR のオープン失敗: " + p);
                            return null;
                        }
                    })
                    .orElse(null);
        } catch (IOException e) {
            System.err.println("  Minecraft JAR の検索失敗: " + e.getMessage());
            return null;
        }
    }

    /**
     * 見つからないテクスチャの代替画像（紫と黒のチェッカー）
     */
    private BufferedImage createMissingTexture() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        int purple = 0xFF00FF;
        int black = 0x000000;

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int color = ((x / 8 + y / 8) % 2 == 0) ? purple : black;
                img.setRGB(x, y, color);
            }
        }

        return img;
    }

    /**
     * キャッシュをクリア
     */
    public void clear() {
        cache.clear();
        if (minecraftJar != null) {
            try {
                minecraftJar.close();
            } catch (IOException e) {
                System.err.println("  Minecraft JAR のクローズ失敗");
            }
        }
    }
}

