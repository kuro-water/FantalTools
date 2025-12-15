package org.kgcc.fantalmod.recipetools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * レシピを画像にレンダリング
 */
public class RecipeRenderer {
    private static final int TEXTURE_SIZE = 16;
    private static final int GRID_MARGIN = 10;
    private static final int ITEM_SPACING = 5;
    private static final int ARROW_SIZE = 30;
    private static final int RESULT_SIZE = 32;
    private static final int TEXT_HEIGHT = 20;

    private final TextureCache textureCache;

    public RecipeRenderer(TextureCache textureCache) {
        this.textureCache = textureCache;
    }

    /**
     * レシピの種類に応じて画像をレンダリング
     */
    public void renderRecipe(RecipeData recipe, Path outputPath) throws IOException {
        switch (recipe.type()) {
            case "minecraft:crafting_shaped":
                renderShapedRecipe(recipe, outputPath);
                break;
            case "minecraft:crafting_shapeless":
                renderShapelessRecipe(recipe, outputPath);
                break;
            case "minecraft:smelting":
                renderSmeltingRecipe(recipe, outputPath);
                break;
            default:
                throw new IllegalArgumentException("未対応のレシピタイプ: " + recipe.type());
        }
    }

    /**
     * Shaped レシピを画像にレンダリング
     */
    private void renderShapedRecipe(RecipeData recipe, Path outputPath) throws IOException {
        int gridSize = TEXTURE_SIZE * 3 + ITEM_SPACING * 2;
        int totalWidth = GRID_MARGIN * 2 + gridSize + ARROW_SIZE + RESULT_SIZE + GRID_MARGIN * 2;
        int totalHeight = GRID_MARGIN * 2 + gridSize + GRID_MARGIN * 2;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, totalWidth, totalHeight);

        int gridX = GRID_MARGIN;
        int gridY = GRID_MARGIN;

        // 3x3 グリッドを描画
        String[][] grid = recipe.getGridItems();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = gridX + col * (TEXTURE_SIZE + ITEM_SPACING);
                int y = gridY + row * (TEXTURE_SIZE + ITEM_SPACING);

                String itemId = grid[row][col];
                // 空白セルの場合はテクスチャを描画しない
                if (itemId != null) {
                    BufferedImage texture = textureCache.getTexture(itemId);
                    if (texture != null) {
                        g2d.drawImage(texture, x, y, TEXTURE_SIZE, TEXTURE_SIZE, null);
                    }
                }

                // グリッドラインを描画
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(x, y, TEXTURE_SIZE, TEXTURE_SIZE);
            }
        }

        // 矢印を描画
        int arrowX = gridX + gridSize + GRID_MARGIN;
        int arrowY = gridY + (gridSize - ARROW_SIZE) / 2;
        drawArrow(g2d, arrowX, arrowY, ARROW_SIZE, ARROW_SIZE);

        // 結果アイテムを描画
        int resultX = arrowX + ARROW_SIZE + GRID_MARGIN;
        int resultY = gridY + (gridSize - RESULT_SIZE) / 2;
        BufferedImage resultTexture = textureCache.getTexture(recipe.resultItem());
        if (resultTexture != null) {
            g2d.drawImage(resultTexture, resultX, resultY, RESULT_SIZE, RESULT_SIZE, null);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRect(resultX, resultY, RESULT_SIZE, RESULT_SIZE);

        g2d.dispose();
        ImageIO.write(image, "png", outputPath.toFile());
    }

    /**
     * Shapeless レシピを画像にレンダリング
     */
    private void renderShapelessRecipe(RecipeData recipe, Path outputPath) throws IOException {
        int ingredientSize = TEXTURE_SIZE;
        int ingredientSpacing = ITEM_SPACING;
        int ingredientsWidth = Math.min(recipe.ingredients().size(), 3) * (ingredientSize + ingredientSpacing) + ingredientSpacing;
        int ingredientsHeight = (int) Math.ceil(recipe.ingredients().size() / 3.0) * (ingredientSize + ingredientSpacing) + ingredientSpacing;

        int totalWidth = GRID_MARGIN * 2 + ingredientsWidth + ARROW_SIZE + RESULT_SIZE + GRID_MARGIN * 2;
        int totalHeight = GRID_MARGIN * 2 + Math.max(ingredientsHeight, RESULT_SIZE) + GRID_MARGIN * 2;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, totalWidth, totalHeight);

        // タイトル
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Shapeless", GRID_MARGIN, GRID_MARGIN + 12);

        int ingredientX = GRID_MARGIN;
        int ingredientY = GRID_MARGIN + TEXT_HEIGHT;

        // 材料を描画
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            int row = i / 3;
            int col = i % 3;
            int x = ingredientX + col * (ingredientSize + ingredientSpacing);
            int y = ingredientY + row * (ingredientSize + ingredientSpacing);

            String itemId = recipe.ingredients().get(i);
            BufferedImage texture = textureCache.getTexture(itemId);

            if (texture != null) {
                g2d.drawImage(texture, x, y, ingredientSize, ingredientSize, null);
            }

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, ingredientSize, ingredientSize);
        }

        // 矢印を描画
        int arrowX = ingredientX + ingredientsWidth + GRID_MARGIN;
        int arrowY = ingredientY + (ingredientsHeight - ARROW_SIZE) / 2;
        drawArrow(g2d, arrowX, arrowY, ARROW_SIZE, ARROW_SIZE);

        // 結果アイテムを描画
        int resultX = arrowX + ARROW_SIZE + GRID_MARGIN;
        int resultY = ingredientY + (ingredientsHeight - RESULT_SIZE) / 2;
        BufferedImage resultTexture = textureCache.getTexture(recipe.resultItem());
        if (resultTexture != null) {
            g2d.drawImage(resultTexture, resultX, resultY, RESULT_SIZE, RESULT_SIZE, null);
        }
        String countText = recipe.resultCount() > 1 ? "x" + recipe.resultCount() : "";
        if (!countText.isEmpty()) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString(countText, resultX + RESULT_SIZE - 12, resultY + RESULT_SIZE - 2);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRect(resultX, resultY, RESULT_SIZE, RESULT_SIZE);

        g2d.dispose();
        ImageIO.write(image, "png", outputPath.toFile());
    }

    /**
     * Smelting レシピを画像にレンダリング
     */
    private void renderSmeltingRecipe(RecipeData recipe, Path outputPath) throws IOException {
        int ingredientSize = TEXTURE_SIZE * 2;
        int totalWidth = GRID_MARGIN * 2 + ingredientSize + ARROW_SIZE + RESULT_SIZE + GRID_MARGIN * 2;
        int totalHeight = GRID_MARGIN * 2 + TEXT_HEIGHT + Math.max(ingredientSize, RESULT_SIZE) + GRID_MARGIN;

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, totalWidth, totalHeight);

        // タイトル
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Furnace", GRID_MARGIN, GRID_MARGIN + 12);

        int ingredientX = GRID_MARGIN;
        int ingredientY = GRID_MARGIN + TEXT_HEIGHT;

        // 入力アイテムを描画
        BufferedImage ingredientTexture = textureCache.getTexture(recipe.ingredient());
        if (ingredientTexture != null) {
            g2d.drawImage(ingredientTexture, ingredientX, ingredientY, ingredientSize, ingredientSize, null);
        }
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(ingredientX, ingredientY, ingredientSize, ingredientSize);

        // 矢印を描画
        int arrowX = ingredientX + ingredientSize + GRID_MARGIN;
        int arrowY = ingredientY + (ingredientSize - ARROW_SIZE) / 2;
        drawArrow(g2d, arrowX, arrowY, ARROW_SIZE, ARROW_SIZE);

        // 結果アイテムを描画
        int resultX = arrowX + ARROW_SIZE + GRID_MARGIN;
        int resultY = ingredientY + (ingredientSize - RESULT_SIZE) / 2;
        BufferedImage resultTexture = textureCache.getTexture(recipe.resultItem());
        if (resultTexture != null) {
            g2d.drawImage(resultTexture, resultX, resultY, RESULT_SIZE, RESULT_SIZE, null);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRect(resultX, resultY, RESULT_SIZE, RESULT_SIZE);

        // クッキング時間と経験値を表示
        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Time: " + (recipe.cookingTime() / 20.0) + "s", ingredientX, totalHeight - GRID_MARGIN);
        g2d.drawString("XP: " + recipe.experience(), resultX, totalHeight - GRID_MARGIN);

        g2d.dispose();
        ImageIO.write(image, "png", outputPath.toFile());
    }

    /**
     * 矢印を描画
     */
    private void drawArrow(Graphics2D g2d, int x, int y, int width, int height) {
        int centerY = y + height / 2;
        int arrowHeadSize = 5;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // 横線
        g2d.drawLine(x, centerY, x + width - arrowHeadSize, centerY);

        // 矢印の先端
        int[] xPoints = { x + width, x + width - arrowHeadSize, x + width - arrowHeadSize };
        int[] yPoints = { centerY, centerY - arrowHeadSize, centerY + arrowHeadSize };
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}

