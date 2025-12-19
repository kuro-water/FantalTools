# Recipe Tools - レシピ画像生成ツール

このツールは、Minecraft Fabric MOD のレシピ JSON ファイルから自動的にレシピ画像を生成します。

## 機能

- **JSON 解析**: `recipes/` ディレクトリ内のすべてのレシピを解析
  - Shaped レシピ（3×3 グリッド形式）
  - Shapeless レシピ（自由配置形式）
  - Smelting レシピ（かまど）
- **テクスチャ取得**: MOD/バニラ問わずアイテム・ブロックのテクスチャを自動取得
  - MOD アイテムテクスチャ（`assets/fantalmod/textures/item/`）
  - MOD ブロックテクスチャ（`assets/fantalmod/textures/block/`）
  - バニラ テクスチャ（ファイルシステム + JAR 自動抽出）
- `src/main/resources/assets/fantalmod/textures/block/` - MOD ブロックテクスチャ
- ファイルシステムに配置されている場合：`src/main/resources/assets/minecraft/textures/{item,block}/`
  - JAR ファイル内の `assets/minecraft/textures/{item,block}/` から自動抽出
テクスチャをメモリにキャッシュして効率を向上させます。MOD/バニラ問わず、アイテム・ブロックテクスチャ、ファイルシステム・JAR ファイルに対応。
- `getTexture()` - アイテム/ブロック ID からテクスチャを取得（キャッシュ利用）
- `loadTexture()` - アイテム/ブロックを判定してテクスチャを読み込む
- `loadModTexture()` - MOD テクスチャを読み込む（アイテム → ブロック の順で試す）
- `loadMinecraftTexture()` - Minecraft テクスチャを読み込む（ファイル → JAR の順で試す）
1. MOD アイテムテクスチャ（`assets/fantalmod/textures/item/`）
2. MOD ブロックテクスチャ（`assets/fantalmod/textures/block/`）
3. ファイルシステムのバニラ アイテムテクスチャ（`assets/minecraft/textures/item/`）
4. ファイルシステムのバニラ ブロックテクスチャ（`assets/minecraft/textures/block/`）
5. Gradle キャッシュの JAR からバニラ アイテムテクスチャ（自動抽出）
6. Gradle キャッシュの JAR からバニラ ブロックテクスチャ（自動抽出）
7. 見つからない場合は紫と黒のチェッカーを表示
**対応命名空間:**
- `fantalmod:*` → 自動検索（アイテム → ブロック）
- `minecraft:*` → 自動検索（ファイル → JAR、アイテム → ブロック）

### カスタム出力先を指定

```bash
gradle generateRecipeImages -PoutputDir=path/to/custom/output
```

### プロジェクトルートを指定（サブディレクトリで実行する場合）

```bash
gradle generateRecipeImages -PprojectRoot=C:\path\to\project
```

## 出力結果

生成される画像の形式はレシピタイプによって異なります：

### Shaped レシピ
- **左側**: クラフトテーブルの 3×3 グリッド
  - グリッドラインで区切られた 9 つのセル
  - 各セルに配置されたアイテムテクスチャ
- **中央**: 矢印（→）
- **右側**: クラフト結果のアイテムテクスチャ

### Shapeless レシピ
- **左側**: 複数の材料（最大3×3グリッド表示）
- **中央**: 矢印（→）
- **右側**: クラフト結果のアイテムテクスチャ
- **タイトル**: "Shapeless"

### Smelting レシピ
- **左側**: 入力アイテムテクスチャ
- **中央**: 矢印（→）
- **右側**: 結果アイテムテクスチャ
- **情報**: クッキング時間と経験値を表示

### サポートされるレシピタイプ

- ✅ `minecraft:crafting_shaped` - 形状が決まったレシピ
- ✅ `minecraft:crafting_shapeless` - 形状が不要なレシピ
- ✅ `minecraft:smelting` - かまどレシピ

### スキップされるレシピ

以下のタイプは自動的にスキップされます（コンソール出力で確認可能）：

- ❌ `minecraft:smoking` - 燻製器
- ❌ `minecraft:blasting` - ブラスト炉
- ❌ その他のカスタムレシピ

## ディレクトリ構成

```
src/main/java/org/kgcc/fantalmod/recipetools/
├── RecipeImageGenerator.java    # メインエントリーポイント
├── RecipeData.java               # レシピデータモデル
├── TextureCache.java             # テクスチャキャッシング
└── RecipeRenderer.java           # 画像レンダリングロジック
```

## 必要なリソース

生成を成功させるには、以下のディレクトリが存在する必要があります：

- `src/main/resources/data/fantalmod/recipes/` - レシピ JSON ファイル
- `src/main/resources/assets/fantalmod/textures/item/` - MOD アイテムテクスチャ

**Minecraft（バニラ）テクスチャについて:**
- ファイルシステムに配置されている場合：`src/main/resources/assets/minecraft/textures/item/`
- **ない場合は自動的に Gradle キャッシュの Minecraft JAR から抽出します**
  - `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-project-root/` から取得
  - JAR ファイル内の `assets/minecraft/textures/item/` から自動抽出
  - 別途ファイルを配置する必要はありません ✅

## トラブルシューティング

### 「テクスチャが見つからない」エラー

テクスチャファイルが正しいディレクトリに配置されているか確認してください：

- `src/main/resources/assets/fantalmod/textures/item/[アイテム名].png`
- `src/main/resources/assets/minecraft/textures/item/[アイテム名].png`

見つからない場合は、紫と黒のチェッカーパターン（Missing Texture）が表示されます。

### 「レシピディレクトリが見つからない」エラー

`src/main/resources/data/fantalmod/recipes/` が存在するか確認してください。

### 「パース失敗」エラー

JSON ファイルが有効な形式か確認してください。以下の構造が必要です：

```json
{
    "type": "minecraft:crafting_shaped",
    "pattern": ["...", "..."],
    "key": { "X": { "item": "..." }, ... },
    "result": { "item": "...", "count": 1 }
}
```

## クラス説明

### RecipeImageGenerator

メインクラス。レシピ JSON ファイルを読み込み、画像生成を調整します。

- `generateAllRecipes()` - すべてのレシピを処理
- `processRecipeFile()` - 単一のレシピファイルを処理
- `parseShapedRecipe()` - shaped レシピを JSON から解析

### RecipeData

レシピ情報を保持するモデルクラス。

- `getPattern()` - クラフトパターンを取得
- `getKeyToItem()` - キー文字とアイテム ID のマッピングを取得
- `getGridItems()` - 3×3 グリッドのアイテム配列を取得

### TextureCache

テクスチャをメモリにキャッシュして効率を向上させます。MOD テクスチャ、ファイルシステムのバニラテクスチャ、JAR ファイルのバニラテクスチャに対応。

- `getTexture()` - アイテムID からテクスチャを取得（キャッシュ利用）
- `loadTexture()` - ファイルシステムからテクスチャを読み込む
- `loadTextureFromJar()` - Gradle キャッシュの JAR ファイルからバニラテクスチャを抽出
- `findMinecraftJar()` - Minecraft JAR ファイルを自動検索

**テクスチャ取得の優先順位:**
1. MOD のテクスチャディレクトリ（`assets/fantalmod/textures/item/`）
2. ファイルシステムのバニラテクスチャ（`assets/minecraft/textures/item/`）
3. Gradle キャッシュの Minecraft JAR（自動抽出）
4. 見つからない場合は紫と黒のチェッカーを表示

### RecipeRenderer

レシピを PNG 画像にレンダリングします。

- `renderRecipe()` - レシピを画像として出力
- `drawArrow()` - レシピの矢印を描画

## メインプログラムへの影響

このツールは完全に独立しており、メインプログラムに影響を与えません：

- ✓ `runClient` コマンドに影響なし
- ✓ `build` タスクに影響なし
- ✓ 単独の Gradle task として実行可能
- ✓ 開発時にのみ使用（本番ビルドに含まれない）

## ライセンス

このツールは MOD プロジェクトの一部です。

