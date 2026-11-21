# Kukariri 🧠

Android application which visualizes the Spaced Repetition System (SRS)

![](https://github.com/user-attachments/assets/0f6a799c-9c59-49eb-9534-f000818c4aa2)

## Functionality

To test the functionality of this app, first go to one of the other apps/games where the student 
is being tested for knowledge of a skill, e.g. the [Herufi](https://github.com/elimu-ai/herufi)
app for learning sound-to-letter correspondences. In the app answer an assessment correctly, and 
go back to this app. Now, the student's recall probability of that knowledge item will be 
updated, and its _recall probability_ will appear in the UI in the form of number of stars.

### Recall Probability

The recall probability will drop over time. So after a few minutes, you will see the mastery 
drop from ★★★★★ to ★★★★☆, which indicates that the recall probability has dropped below 
80%. If you want to know the exact value of the recall probability, you can press the row 
of stars and see the percentage appear in a toast:

![](https://github.com/user-attachments/assets/8905c425-f0a8-4899-944d-9b0c805347cb)

## Spaced Repetition System (SRS)

The definition of mastery above is based on a Spaced Repetition System (SRS) which examines the 
_assessment events_ of the student, and calculates a recall probability. For implementation details, 
see [`SpacedRepetitionHelper.kt`](src/main/java/ai/elimu/kukariri/logic/SpacedRepetitionHelper.kt).

## Installation

> [!IMPORTANT]
> Note: This app depends on the [elimu.ai Analytics](https://github.com/elimu-ai/analytics) app to be installed.

## Development 👩🏽‍💻

Compile APK:

```
./gradlew clean build
```

Install APK:

```
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Code Coverage

[![codecov](https://codecov.io/gh/elimu-ai/kukariri/branch/main/graph/badge.svg?token=LFJL11C0TC)](https://codecov.io/gh/elimu-ai/kukariri)

[![](https://codecov.io/gh/elimu-ai/kukariri/branch/main/graphs/tree.svg?token=LFJL11C0TC)](https://codecov.io/gh/elimu-ai/kukariri)

```
./gradlew jacocoTestReport
open app/build/jacoco/jacocoHtml/index.html
```

### Content Provider Utils 📦

If you want to make changes to the elimu.ai Content Provider's utility library, see testing instructions at https://github.com/elimu-ai/content-provider/blob/main/README.md#utils-snapshot

### Gradle Upgrade

```
./gradlew wrapper --gradle-version x.x.x
```

### Release 📦

To perform a release, follow these steps:

1. Merge your PR into the `main` branch
1. Wait for the ["Gradle Release"](https://github.com/elimu-ai/kukariri/actions/workflows/gradle-release.yml) workflow to complete
1. Ensure that the new release version appears at https://github.com/elimu-ai/kukariri/releases

---

<p align="center">
  <img src="https://github.com/elimu-ai/webapp/blob/main/src/main/webapp/static/img/logo-text-256x78.png" />
</p>
<p align="center">
  elimu.ai - Free open-source learning software for out-of-school children 🚀✨
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
