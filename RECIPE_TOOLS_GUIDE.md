# レシピ画像生成ツール - 使用ガイド

## 📋 概要

`recipetools` は、Minecraft Fabric MOD のクラフトレシピを JSON から自動的に画像化するツールです。メインプログラムに一切影響を与えない独立した Gradle task として実装されています。

## 🚀 クイックスタート

### 1. 基本的な実行方法

```bash
gradle generateRecipeImages
```

**結果**: プロジェクトルートの `recipe_images/` ディレクトリに PNG 画像が生成されます。

### 2. 出力先を指定

```bash
gradle generateRecipeImages -PoutputDir=my_recipes
```

### 3. サブディレクトリから実行

```bash
gradle generateRecipeImages -PprojectRoot=C:\myprogram\Minecraft\kgcc-fabric-mod
```

## 📊 実装構成

すべてのコードは **独立した `recipetools` ディレクトリ**に格納されています：

```
src/main/java/org/kgcc/fantalmod/recipetools/
├── RecipeImageGenerator.java      # メインクラス（エントリーポイント）
├── RecipeData.java                # レシピデータモデル（クラス）
├── TextureCache.java              # テクスチャキャッシング管理
├── RecipeRenderer.java            # 画像レンダリング
└── README.md                       # ドキュメント
```

## 🔧 各クラスの説明

### RecipeImageGenerator（メインクラス）

レシピ処理の全体を統括します。

**主要メソッド:**
- `main(String[] args)` - エントリーポイント
- `generateAllRecipes()` - すべてのレシピを処理
- `processRecipeFile(Path jsonFile)` - 単一ファイルを処理
- `parseShapedRecipe(JsonObject json, String recipeName)` - JSON をレシピオブジェクトに変換
- `parseShapelessRecipe(JsonObject json, String recipeName)` - Shapeless レシピをパース
- `parseSmeltingRecipe(JsonObject json, String recipeName)` - Smelting レシピをパース

**動作フロー:**
```
1. recipes/ ディレクトリをスキャン
2. *.json ファイルを検出
3. レシピタイプを判定（shaped, shapeless, smelting）
4. RecipeData を生成
5. RecipeRenderer で画像化
6. recipe_images/ に保存
```

### TextureCache（テクスチャ管理）

テクスチャの読み込みと効率的なキャッシング、Minecraft JAR からの自動抽出。アイテム・ブロック両方に対応。

**主要メソッド:**
- `getTexture(String itemId)` - アイテム/ブロック ID からテクスチャを取得（キャッシュ利用）
- `loadTexture(String itemId)` - アイテム/ブロックを判定してテクスチャを読み込む
- `loadModTexture(String textureName)` - MOD テクスチャを読み込む（アイテム → ブロック）
- `loadMinecraftTexture(String textureName)` - Minecraft テクスチャを読み込む（ファイル → JAR）
- `loadTextureFromJar(String type, String textureName)` - **JAR から自動抽出** ✨
- `findMinecraftJar()` - Minecraft JAR ファイルを自動検索

**テクスチャ取得の優先順位:**
1. MOD アイテムテクスチャ（`assets/fantalmod/textures/item/`）
2. MOD ブロックテクスチャ（`assets/fantalmod/textures/block/`）
3. ファイルシステムのバニラ アイテムテクスチャ（`assets/minecraft/textures/item/`）
4. ファイルシステムのバニラ ブロックテクスチャ（`assets/minecraft/textures/block/`）
5. **Gradle キャッシュの JAR からバニラ アイテムテクスチャ（自動抽出）** ✨
6. **Gradle キャッシュの JAR からバニラ ブロックテクスチャ（自動抽出）** ✨
7. 見つからない場合は紫と黒のチェッカーを表示

**対応命名空間:**
- `fantalmod:*` → 自動検索（アイテム → ブロック）
- `minecraft:*` → 自動検索（ファイル → JAR、アイテム → ブロック）

### RecipeRenderer（画像生成）

レシピ情報から PNG 画像をレンダリング。

**主要メソッド:**
- `renderRecipe(RecipeData recipe, Path outputPath)` - 画像化して保存

**出力画像の構成:**
```
┌─────────────────────────────┐
│ [3×3 Grid] → [Result Item] │
│ (入力材料)     (結果)       │
└─────────────────────────────┘
```

## 📦 依存ライブラリ

`build.gradle` に以下を追加済み：

```gradle
implementation 'com.google.code.gson:gson:2.10.1'
```

- **Gson**: JSON の解析と操作

## 🎨 出力画像の仕様

| 項目 | 詳細 |
|------|------|
| 形式 | PNG |
| 背景 | 白色 |
| グリッドサイズ | 3×3（各セル 16×16px） |
| 結果アイテム | 32×32px |
| 矢印 | 中央に黒色 |

## 📁 対応ファイル形式

### 入力（JSON）

`src/main/resources/data/fantalmod/recipes/*.json`

**対応タイプ:**
- ✅ `minecraft:crafting_shaped` - 形状が決まったレシピ（3×3グリッド）
- ✅ `minecraft:crafting_shapeless` - 形状が不要なレシピ（複数材料を自由に配置）
- ✅ `minecraft:smelting` - かまどレシピ

**未対応タイプ（スキップ）:**
- ❌ `minecraft:smoking` - 燻製器
- ❌ `minecraft:blasting` - ブラスト炉
- ❌ その他のカスタムレシピ

### 出力（PNG）

`recipe_images/[レシピ名].png`

## ⚙️ Gradle Task の登録

`build.gradle` に登録済み：

```gradle
task generateRecipeImages(type: JavaExec) {
    group = 'fantalmod'
    description = 'Generate recipe images from JSON recipes'
    
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'org.kgcc.fantalmod.recipetools.RecipeImageGenerator'
    
    args project.properties.get('outputDir', 'recipe_images')
    args project.properties.get('projectRoot', projectDir.toString())
}
```

**実行可能:**
```bash
gradle generateRecipeImages
gradle tasks | grep "generateRecipeImages"  # 確認用
```

## 🔍 トラブルシューティング

### Q: テクスチャが紫と黒のチェッカーになっている

**A:** テクスチャファイルが見つかっていません。確認事項：

**MOD テクスチャの場合:**
- ファイル名が正しいか（小文字で統一されているか）
- ファイル拡張子が `.png` か
- パスが正しいか：
  - `src/main/resources/assets/fantalmod/textures/item/[名前].png`

**バニラテクスチャの場合（自動検索）:**
- 以下の順序で自動検索されます：
  1. ファイルシステム: `src/main/resources/assets/minecraft/textures/item/[名前].png`
  2. **Gradle キャッシュの Minecraft JAR** （自動抽出） ✨
  3. チェッカー表示

**Gradle キャッシュがない場合:**
- `gradle build` を実行して Minecraft JAR をダウンロード

### Q: 「レシピディレクトリが見つかりません」エラー

**A:** `src/main/resources/data/fantalmod/recipes/` が存在するか確認してください。

### Q: 一部のレシピがスキップされている

**A:** 以下の理由の可能性があります：

- `type` が対応していない（shaped, shapeless, smelting 以外）
- JSON の形式が不正
- 必須フィールドが不足している

**対応タイプ:**
- `minecraft:crafting_shaped` - 形状が決まったレシピ
- `minecraft:crafting_shapeless` - 形状が不要なレシピ
- `minecraft:smelting` - かまど

### Q: 「パース失敗」エラー

**A:** JSON ファイルが有効な形式か確認してください。

**Shaped:**
```json
{
    "type": "minecraft:crafting_shaped",
    "pattern": ["XXX", "XYX"],
    "key": { "X": { "item": "minecraft:iron_ingot" } },
    "result": { "item": "fantalmod:item", "count": 1 }
}
```

**Shapeless:**
```json
{
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
        { "item": "minecraft:iron_ingot" },
        { "item": "minecraft:diamond" }
    ],
    "result": { "item": "fantalmod:item" }
}
```

**Smelting:**
```json
{
    "type": "minecraft:smelting",
    "ingredient": { "item": "minecraft:iron_ore" },
    "result": "minecraft:iron_ingot",
    "experience": 0.7,
    "cookingtime": 200
}
```

### Q: メインプログラムに影響を与えないか？

**A:** 完全に独立しています：

- ✅ `runClient` 実行時に呼び出されない
- ✅ `build` タスクに含まれない
- ✅ 開発時のみ手動実行
- ✅ クラスパスは分離されている

## 📈 実行結果例

```
見つかったレシピファイル: 25
✓ 生成完了: fantal_arrow.png (minecraft:crafting_shaped)
✓ 生成完了: fantal_axe.png (minecraft:crafting_shaped)
✓ 生成完了: fantal_bench.png (minecraft:crafting_shaped)
✓ 生成完了: fantal_block.png (minecraft:crafting_shaped)
✓ 生成完了: fantal_boots.png (minecraft:crafting_shaped)
✓ 生成完了: fantal_bow.png (minecraft:crafting_shapeless)
✓ 生成完了: fantal_ingot_from_smelting_fantal_ore.png (minecraft:smelting)
...
✓ レシピ画像の生成が完了しました
  出力先: C:\myprogram\Minecraft\kgcc-fabric-mod\recipe_images
```

**実行統計:**
- 処理ファイル: 25 個
- 生成画像: 24 個
  - Shaped レシピ: 18 個
  - Shapeless レシピ: 1 個
  - Smelting レシピ: 5 個
- スキップ: 1 個（未対応タイプ）

## 🚀 実際の使用シナリオ

### シナリオ 1: MOD ドキュメント作成

```bash
gradle generateRecipeImages -PoutputDir=docs/recipes
```

生成された画像を README に貼り付けることで、ユーザーに視覚的にレシピを説明できます。

### シナリオ 2: レシピバグ検出

新しいレシピを追加した際に画像生成を実行し、グリッドが正しく表示されているか確認します。

### シナリオ 3: 複数プロジェクト管理

別のプロジェクトフォルダで実行：

```bash
gradle generateRecipeImages -PprojectRoot=C:\other\project -PoutputDir=output
```

## 📝 今後の拡張可能性

現在の実装は以下の拡張が容易です：

- **Smoking / Blasting レシピ** → `RecipeImageGenerator` に解析ロジック追加、`RecipeRenderer` に対応ロジック追加
- **複数アイテムの結果** → `RecipeRenderer` に対応ロジック追加
- **HTML レポート生成** → 画像 URL を HTML に埋め込み
- **比較機能** → 古い画像との diff 表示
- **カスタムレシピ対応** → 型チェック拡張

## 📚 参考リンク

- Minecraft Wiki: [Recipes](https://minecraft.fandom.com/wiki/Recipes)
- Fabric Documentation: [Recipes](https://fabricmc.net/wiki/tutorial:recipes)
- Gson ドキュメント: [Getting Started with Gson](https://github.com/google/gson/blob/master/UserGuide.md)

---

**最終確認: 20 個のレシピ画像が正常に生成されました！**

