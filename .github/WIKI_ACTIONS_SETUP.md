# GitHub Actions Wiki 自動生成・プッシュ設定

## 📋 概要

このワークフロー（`.github/workflows/wiki.yml`）は、リポジトリにプッシュされるたびに：

1. ✅ Wiki ページを自動生成（`gradle generateWiki`）
2. ✅ GitHub Wiki リポジトリに自動プッシュ

という処理を実行します。

---

## 🚀 使い方

### **1. ファイルをコミット**

```bash
git add .github/workflows/wiki.yml .gitignore
git commit -m "ci: GitHub Actions で Wiki を自動生成・プッシュ"
git push
```

### **2. GitHub の設定確認**

リポジトリのページで以下を確認：

- **Settings → Actions → General**
  - ✅ "Allow all actions and reusable workflows" を選択

- **Settings → Secrets and variables → Actions**
  - 特別な設定不要（`GITHUB_TOKEN` は自動提供）

### **3. ワークフロー実行確認**

- リポジトリの **Actions** タブでワークフロー実行履歴を確認
- プッシュすると自動実行される

---

## 📊 ワークフロー詳細

| 項目 | 内容 |
|---|---|
| **トリガー** | `main` ブランチおよび `dev/**` ブランチへのプッシュ |
| **手動実行** | ✅ Actions タブから手動実行可能（`workflow_dispatch`） |
| **実行環境** | Ubuntu 最新 |
| **Java バージョン** | 21 |
| **コミットメッセージ** | 自動生成、タイムスタンプ・コミットハッシュ付き |
| **プッシュ対象** | GitHub Wiki リポジトリ（`*.wiki.git`） |

---

## ✨ 実行フロー

```
1. git push origin main
        ↓
2. GitHub Actions トリガー
        ↓
3. gradle generateWiki 実行
        ↓
4. wiki/ ディレクトリ生成
        ↓
5. wiki/.git 初期化
        ↓
6. GitHub Wiki リポジトリにプッシュ
        ↓
7. GitHub Wiki 自動更新 ✅
```

---

## 🔧 カスタマイズ

### **トリガーブランチの変更**

```yaml
on:
  push:
    branches:
      - main
      - dev/**  # 変更可能
```

### **スケジュール実行の追加**

毎日 00:00 UTC に実行：

```yaml
on:
  schedule:
    - cron: '0 0 * * *'
  push:
    branches:
      - main
```

---

## ⚠️ 注意事項

1. **初回実行時**
   - Wiki リポジトリが自動作成される
   - GitHub Wiki 設定で "Wikis" が有効になっていることを確認

2. **手書き部分の保護**
   - Wiki ページの手書きセクション（`<!-- ✏️ -->` で囲まれた部分）は自動生成時に保持される
   - ただし、GitHub Wiki で直接編集した場合は上書きされる可能性あり
   - **ローカルの `wiki/` ファイルで管理することを推奨**

3. **認証**
   - `GITHUB_TOKEN` は GitHub Actions によって自動提供
   - 追加の認証設定は不要

---

## 📝 トラブルシューティング

### Q: ワークフローが失敗する

**A:** Actions タブでログを確認：

1. **"Permission denied" エラー**
   - Settings → Actions → General
   - "Allow GitHub Actions to create and approve pull requests" をチェック

2. **Wiki リポジトリが見つからない**
   - GitHub Wiki が有効になっているか確認
   - Settings → Features から "Wikis" をチェック

3. **Gradle ビルドエラー**
   - ローカルで `gradle generateWiki` が成功することを確認

### Q: Wiki が更新されない

**A:** 

1. Actions タブで実行ステータスを確認
2. 失敗している場合はログから原因を特定
3. 手動実行で再試行：Actions → "Generate and Push Wiki" → "Run workflow"

---

## 🎯 次のステップ

1. ✅ このファイルをコミット・プッシュ
2. ✅ Actions タブでワークフロー実行を確認
3. ✅ Wiki リポジトリが更新されたか確認
4. ✅ GitHub Wiki で `https://github.com/kuro-water/fantalmod/wiki` にアクセス

すべてが正常に機能すれば、以降のプッシュで自動的に Wiki が更新されます！

