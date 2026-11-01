@echo off
echo ===================================================
echo   KIRA Auto Post - GitHub Push Helper
echo ===================================================
echo.

set /p REPO_URL="សូមបញ្ចូល GitHub Repository URL (ឧទាហរណ៍ https://github.com/username/kira-auto-post.git): "

if "%REPO_URL%"=="" (
    echo [ERROR] សូមបញ្ចូល URL របស់ GitHub Repository!
    pause
    exit /b
)

echo.
echo [1/4] កំពុងដកឯកសារដែលមិនចាំបាច់...
git rm --cached bun.lock bun.lockb 2>nul

echo [2/4] កំពុងរៀបចំ Git...
git init
git add .
git commit -m "KIRA Auto Post complete Android project"
git branch -M main

echo [3/4] កំពុងភ្ជាប់ទៅកាន់ GitHub...
git remote remove origin 2>nul
git remote add origin %REPO_URL%

echo [4/4] កំពុង Push ឡើងទៅ GitHub...
git push -u origin main

echo.
echo ===================================================
echo   ជោគជ័យ! សូមចូលទៅកាន់ Tab Actions លើ GitHub ដើម្បីទាញយក APK!
echo ===================================================
pause
