# Kukariri 🧠

[![codecov](https://codecov.io/gh/elimu-ai/kukariri/branch/main/graph/badge.svg?token=LFJL11C0TC)](https://codecov.io/gh/elimu-ai/kukariri)

Android application which handles the Spaced Repetition System (SRS)

![](https://user-images.githubusercontent.com/15718174/81469350-08eeeb00-9217-11ea-87c6-0d2ea093513e.png)

## Functionality

To test the functionality of this app, first go to one of the other apps/games where the student 
is learning new words (e.g. the Vitabu app), and press a word. Then, 4 minutes after that initial 
`LearningEvent, the word will appear here in the Kukariri app for review.

![WordAssessmentActivity](https://github.com/user-attachments/assets/e6c5cf75-13f6-4b8b-9978-3756e52dd0a7)

## Development 👩🏽‍💻

Compile APK:

```
./gradlew clean build
```

Install APK:

```
adb install app/build/outputs/apk/debug/ai.elimu.analytics-<versionCode>-debug.apk
```

### Code Coverage

[![codecov](https://codecov.io/gh/elimu-ai/kukariri/branch/main/graph/badge.svg?token=LFJL11C0TC)](https://codecov.io/gh/elimu-ai/kukariri)

[![](https://codecov.io/gh/elimu-ai/kukariri/branch/main/graphs/tree.svg?token=LFJL11C0TC)](https://codecov.io/gh/elimu-ai/kukariri)

```
./gradlew jacocoTestReport
open app/build/jacoco/jacocoHtml/index.html
```

---

<p align="center">
  <img src="https://github.com/elimu-ai/webapp/blob/main/src/main/webapp/static/img/logo-text-256x78.png" />
</p>
<p align="center">
  elimu.ai - Free open-source learning software for out-of-school children ✨🚀
</p>
<p align="center">
  <a href="https://elimu.ai">Website 🌐</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki#readme">Wiki 📃</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/orgs/elimu-ai/projects?query=is%3Aopen">Projects 👩🏽‍💻</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki/milestones">Milestones 🎯</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki#open-source-community">Community 👋🏽</a>
  &nbsp;•&nbsp;
  <a href="https://www.drips.network/app/drip-lists/41305178594442616889778610143373288091511468151140966646158126636698">Support 💜</a>
</p>
