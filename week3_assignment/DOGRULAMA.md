# Doğrulama sonuçları

Son kontrol: 9 Kasım 2026.

Koyu lacivert arayüz, yeni oda sekmeleri ve kompakt cihaz listesiyle kontroller yeniden çalıştırıldı.

| Kontrol | Sonuç |
| --- | --- |
| `assembleDebug` | Başarılı; kurulabilir debug APK üretildi |
| Repository unit testleri | 4/4 başarılı |
| ViewModel unit testleri | 10/10 başarılı |
| Android Compose ekran testleri | 5/5 başarılı |
| `lintDebug` | 0 hata; 9 bağımlılık/sürüm güncelleme önerisi |

Android ekran testleri API 36 / Android 16 ARM64 emülatöründe çalıştırıldı. Testler; 106 → 91 → 106 W değişimini, arama ve oda filtresinin birlikte çalışmasını, boş sonuçtan sıfırlamayı, çevrimdışı düğmenin devre dışı olmasını ve hızlı gerçek klavye girişinde metnin eksiksiz korunmasını doğrular.

Hızlı yazma testi Android'in `input text lamba` komutuyla harfleri aralarında Compose beklemesi olmadan gönderir. Metin alanı tam olarak `lamba` kalmalı, tüm odalarda 2 cihaz ve Salon'da 1 cihaz görünmelidir. Bu kontrol, arama alanının arka planda türetilen eski metinle güncellenmesine bağlı harf kaybı için regresyon testidir.

`screenshots/` içindeki dört PNG, çalışan uygulamanın gerçek emülatör ekran görüntüleridir. 120 FPS fiziksel cihazda ölçülmemiştir; performans hedefinin sınırları README'de belirtilmiştir.

Kontrolleri tekrarlamak için:

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
./gradlew :app:connectedDebugAndroidTest
```

İkinci komut açık bir Android cihazı veya emülatör gerektirir. Test raporları `app/build/reports/`, JUnit sonuçları `app/build/test-results/` ve `app/build/outputs/androidTest-results/` altında oluşturulur.
