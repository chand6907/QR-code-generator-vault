#!/bin/bash

# ═══════════════════════════════════════════════════════════════
#  QRVault — GitHub Push Script
#  Run this once inside the QRVault folder:
#    chmod +x push_to_github.sh
#    ./push_to_github.sh
# ═══════════════════════════════════════════════════════════════

set -e

REPO_URL="https://github.com/chand6907/QR-code-generator-vault.git"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║        QRVault — GitHub Push Tool        ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# Check git is installed
if ! command -v git &> /dev/null; then
  echo "❌ Git is not installed. Please install it first:"
  echo "   https://git-scm.com/downloads"
  exit 1
fi

# Ask for GitHub credentials
echo "Enter your GitHub username:"
read -r GH_USER

echo ""
echo "Enter your GitHub Personal Access Token"
echo "(Get one at: GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens classic)"
echo "Give it 'repo' scope when creating it."
echo ""
read -rs GH_TOKEN
echo ""

# Build authenticated URL
AUTH_URL="https://${GH_USER}:${GH_TOKEN}@github.com/chand6907/QR-code-generator-vault.git"

echo "⏳ Initializing git repository..."
git init

echo "⏳ Setting remote origin..."
git remote remove origin 2>/dev/null || true
git remote add origin "$AUTH_URL"

echo "⏳ Staging all files..."
git add .

echo "⏳ Creating commit..."
git commit -m "🚀 Initial commit: QRVault — Spring Boot QR Code Generator with Authentication"

echo "⏳ Pushing to GitHub (branch: main)..."
git branch -M main
git push -u origin main --force

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║  ✅ Successfully pushed to GitHub!       ║"
echo "║                                          ║"
echo "║  Repo: github.com/chand6907/             ║"
echo "║        QR-code-generator-vault           ║"
echo "╚══════════════════════════════════════════╝"
echo ""
echo "📦 Next step — Deploy it live:"
echo ""
echo "  OPTION A (Railway — recommended):"
echo "    1. Go to https://railway.app"
echo "    2. Sign in with GitHub"
echo "    3. New Project → Deploy from GitHub Repo"
echo "    4. Select: chand6907/QR-code-generator-vault"
echo "    5. Done! Live URL given automatically ✅"
echo ""
echo "  OPTION B (Render):"
echo "    1. Go to https://render.com"
echo "    2. New → Web Service → Connect GitHub"
echo "    3. Select: chand6907/QR-code-generator-vault"
echo "    4. It reads render.yaml automatically ✅"
echo ""
