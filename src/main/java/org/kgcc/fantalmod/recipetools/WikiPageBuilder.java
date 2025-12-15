package org.kgcc.fantalmod.recipetools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Wiki ページの生成と更新を行う
 */
public class WikiPageBuilder {
    private static final String AUTO_SECTION_START = "<!-- 🔄 自動生成: 編集しないでください -->";
    private static final String AUTO_SECTION_END = "<!-- /🔄 自動生成 -->";
    private static final String CUSTOM_SECTION_START = "<!-- ✏️ 手書き部分: 自由に編集してください -->";
    private static final String CUSTOM_SECTION_END = "<!-- /✏️ 手書き部分 -->";

    /**
     * Wiki ページを生成または更新
     */
    public static void generatePage(WikiData data, Path outputPath) throws IOException {
        String existingContent = null;
        String customContent = "";

        // 既存ファイルがあれば手書き部分を抽出
        if (Files.exists(outputPath)) {
            existingContent = Files.readString(outputPath);
            customContent = extractCustomSection(existingContent);
        }

        // 新しいコンテンツを生成
        String newContent = buildPageContent(data, customContent);
        
        Files.writeString(outputPath, newContent);
        System.out.println("✓ Wiki ページ生成: " + outputPath.getFileName());
    }

    /**
     * 既存コンテンツから手書きセクションを抽出
     */
    private static String extractCustomSection(String content) {
        int startIdx = content.indexOf(CUSTOM_SECTION_START);
        int endIdx = content.indexOf(CUSTOM_SECTION_END);

        if (startIdx != -1 && endIdx != -1) {
            return content.substring(startIdx, endIdx + CUSTOM_SECTION_END.length());
        }

        // 手書きセクションがなければ作成
        return CUSTOM_SECTION_START + "\n\n## 説明\n<!-- ここに説明を入力 -->\n\n" + CUSTOM_SECTION_END;
    }

    /**
     * ページコンテンツを構築
     */
    private static String buildPageContent(WikiData data, String customContent) {
        StringBuilder sb = new StringBuilder();

        // タイトル
        sb.append("# ").append(data.itemName()).append("\n\n");

        // 手書きセクション（最初に配置）
        sb.append(customContent).append("\n\n");

        // 自動生成セクション：レシピ情報
        sb.append(AUTO_SECTION_START).append("\n\n");
        sb.append("## レシピ情報\n\n");
        sb.append("![Recipe](../../images/recipes/").append(data.recipeImage()).append(")\n\n");

        sb.append("| 項目 | 内容 |\n");
        sb.append("|---|---|\n");
        sb.append("| **タイプ** | ").append(formatRecipeType(data.recipeType())).append(" |\n");
        sb.append("| **材料** | ").append(data.ingredients()).append(" |\n");
        sb.append("| **成果物** | ").append(data.result()).append(" |\n\n");

        // メタデータ
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(data.lastUpdated()));
        sb.append("_このセクションは自動生成です。最終更新: ").append(dateStr).append("_\n");
        sb.append(AUTO_SECTION_END).append("\n");

        return sb.toString();
    }

    /**
     * レシピタイプを日本語に変換
     */
    private static String formatRecipeType(String type) {
        return switch (type) {
            case "minecraft:crafting_shaped" -> "クラフト（形状固定）";
            case "minecraft:crafting_shapeless" -> "クラフト（形状自由）";
            case "minecraft:smelting" -> "かまど焼成";
            default -> type;
        };
    }
}
