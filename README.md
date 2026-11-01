# KIRA Auto Post - កម្មវិធីស្វ័យប្រវត្តិតបសារសមាជិកថ្មី Telegram
**បង្កើតឡើងដោយ KIRA Gaming**

គម្រោងនេះជា Native Android Application (Kotlin + Jetpack Compose + Material 3 + TDLib) សម្រាប់គណនី Telegram User (មិនមែន Bot ទេ) ដែលមានមុខងារស្វ័យប្រវត្តិតបសារទៅកាន់សមាជិកថ្មីដែលបាន Join ចូលក្នុងក្រុម (Telegram Groups) ដែលបានជ្រើសរើស។

---

## 🌟 លក្ខណៈពិសេសចម្បង (Key Features)

1. **គណនី Telegram (TDLib User)**: ចូលប្រើប្រាស់ដោយផ្ទាល់ជាមួយ `api_id`, `api_hash`, និងលេខទូរស័ព្ទ។ គាំទ្រ 2FA និងរក្សាទុក Session ដោយសុវត្ថិភាព។
2. **គ្រប់គ្រងក្រុម (Groups)**: ផ្ទុកបញ្ជីក្រុមទាំងអស់ដែលអ្នកបានចូលរួម បង្ហាញចំនួនសមាជិក និងប្រអប់ធីក (Checkbox) ជ្រើសរើសក្រុមដែលត្រូវដំណើរការ។
3. **កែសម្រួលសារ (Post Editor)**:
   - ប្រអប់សរសេរអត្ថបទសារ (Message Text)
   - ភ្ជាប់រូបភាព ឬ វីដេអូ (Photo/Video)
   - បន្ថែមតំណភ្ជាប់ជាច្រើន (Multiple Links)
   - ជម្រើស **Mention Username**: ដាក់ `@username` ឬឈ្មោះសមាជិកថ្មីនៅលើបន្ទាត់ដំបូងបង្អស់នៃសារ
4. **ប៊ូតុងតែមួយ ចាប់ផ្តើម / បញ្ឈប់ (ONE Toggle Button)**:
   - ចុច "ចាប់ផ្តើមផុសសារឥឡូវ" ដើម្បីបើកដំណើរការភ្លាមៗ ហើយប៊ូតុងនឹងប្តូរទៅជា "បញ្ឈប់ការផុសសារ"
   - ចុច "បញ្ឈប់ការផុសសារ" ដើម្បីផ្អាកជាបន្ទាន់ ហើយអាចកែប្រែសារបាន។ ពេលចុចចាប់ផ្តើមម្តងទៀត វានឹងប្រើប្រាស់សារថ្មីបំផុតដែលបានរក្សាទុក។
5. **Foreground Service & Safe Sending**:
   - ដំណើរការ Background ជាអចិន្ត្រៃយ៍ជាមួយ Notification
   - **Safe Sending 40s**: គម្លាតពេល 40 វិនាទី (អាចកំណត់បានក្នុង Settings) + រយៈពេលចៃដន្យ (Jitter 3-7 វិនាទី) ដើម្បីការពារ Flood Wait ឬ Ban ពី Telegram
   - ដោះស្រាយ `FLOOD_WAIT_X` ដោយស្វ័យប្រវត្តិតាមរយៈការរង់ចាំតាមវិនាទីដែល Telegram តម្រូវ
6. **របាយការណ៍ (Reports)**: កត់ត្រារាល់ការផ្ញើសារ (ពេលវេលា, ឈ្មោះក្រុម, សមាជិក, ស្ថានភាព ជោគជ័យ / បរាជ័យ / Flood Wait)។

---

## 📦 TDLib Prebuilt Dependency (គ្មានបញ្ហា Compile)

គម្រោងនេះប្រើប្រាស់បណ្ណាល័យ TDLib ដែលត្រូវបាន Prebuilt រួចជាស្រេចសម្រាប់ Android Architecture ទាំងអស់ (`arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`) តាមរយៈ JitPack:
- **Repository**: `https://jitpack.io`
- **Dependency**: `com.github.tdlibx:td:1.8.56`
(ឬ `io.github.tdlib-android:core:0.1.1` លើ Maven Central)
ដូច្នេះ GitHub Actions អាចទាញយក Build ចេញជា APK ដោយស្វ័យប្រវត្តិដោយមិនចាំបាច់មាន NDK setup ស្មុគស្មាញឡើយ។

---

## 🚀 របៀបរុញកូដទៅកាន់ GitHub និងទាញយក APK (How to Push to GitHub & Download APK)

### ជំហានទី ១: បង្កើត GitHub Repository ថ្មី
1. ចូលទៅកាន់ [https://github.com](https://github.com) រួចចុច **New repository**
2. ដាក់ឈ្មោះ Repo ឧទាហរណ៍ `kira-auto-post`
3. ជ្រើសរើស **Public** ឬ **Private** រួចចុច **Create repository**

### ជំហានទី ២: រុញកូដចេញពីម៉ាស៊ីនរបស់អ្នក (Push Code)
បើក Terminal / Command Prompt នៅក្នុង Folder គម្រោងនេះ ហើយដំណើរការពាក្យបញ្ជាខាងក្រោម៖

```bash
# ប្រសិនបើជួប error "bun.lock" សូមដំណើរការពាក្យបញ្ជានេះជាមុនសិន៖
git rm --cached bun.lock bun.lockb 2>/dev/null || true

git init
git add .
git commit -m "Initial commit for KIRA Auto Post"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/kira-auto-post.git
git push -u origin main
```
*(សូមប្តូរ `YOUR_USERNAME` ទៅជាឈ្មោះគណនី GitHub របស់អ្នក)*

### ជំហានទី ៣: រង់ចាំ GitHub Actions បង្កើត APK
1. បន្ទាប់ពី Push រួច សូមចូលទៅកាន់ទំព័រ GitHub Repository របស់អ្នក
2. ចុចលើ Tab **Actions** នៅផ្នែកខាងលើ
3. អ្នកនឹងឃើញ Workflow ឈ្មោះ **Build KIRA Auto Post APK** កំពុងដំណើរការ (រូបរង្វង់ពណ៌លឿង)
4. រង់ចាំប្រហែល ២ ទៅ ៤ នាទី រហូតដល់វាចេញសញ្ញាគ្រីសពណ៌បៃតង ✔️ **Success**

### ជំហានទី ៤: ទាញយកឯកសារ APK មកតម្លើងលើទូរស័ព្ទ
1. ចុចលើឈ្មោះ Run ដែលជោគជ័យនោះ (ឧទាហរណ៍: `Initial commit for KIRA Auto Post`)
2. អូសចុះមកក្រោមត្រង់ផ្នែក **Artifacts**
3. ចុចទាញយកឯកសារ **`app-debug-apk`** (វាជា file zip)
4. ពន្លា (Extract) file zip នោះ អ្នកនឹងទទួលបាន `app-debug.apk` សម្រាប់ដំឡើងលើទូរស័ព្ទដៃ Android របស់អ្នកភ្លាមៗ!

---

## ⚙️ ការកំណត់ជាមុនក្នុង Telegram (Telegram API Credentials)

ដើម្បីឱ្យគណនីរបស់អ្នកអាច Login បាន៖
1. ចូលទៅកាន់ [https://my.telegram.org](https://my.telegram.org)
2. ចូលគណនីជាមួយលេខទូរស័ព្ទ Telegram របស់អ្នក
3. ចុចលើ **API development tools**
4. បង្កើត App ថ្មីដើម្បីទទួលបាន **api_id** និង **api_hash**
5. យកលេខកូដទាំងពីរនេះទៅបំពេញក្នុងផ្ទាំង **គណនី (Accounts)** នៃកម្មវិធី KIRA Auto Post។

---

## 📝 កំណត់សម្គាល់ (Placeholders & Requirements Check)

- **Telegram API ID & Hash**: ក្នុងទម្រង់ Login នៃផ្ទាំងគណនី មានដាក់តម្លៃគំរូ។ អ្នកប្រើប្រាស់ត្រូវបញ្ចូល `api_id` និង `api_hash` ផ្ទាល់ខ្លួនពី [my.telegram.org](https://my.telegram.org) ដើម្បីភ្ជាប់ Telegram Account ពិតប្រាកដ។
- **App Icons**: រូប Logo ដើមត្រូវបាន Render ដាក់ចូលគ្រប់ Mipmap folder (`mipmap-mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`, `anydpi-v26`) ដោយមានផ្ទៃក្រោយពណ៌ខ្មៅ (Black Background) តាមការណែនាំ។
