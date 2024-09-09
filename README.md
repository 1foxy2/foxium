<img src="src/main/resources/logo.png" width="128">

# Foxium (Lithium for NeoForge)
![GitHub license](https://img.shields.io/github/license/1foxy2/foxium.svg)
![GitHub issues](https://img.shields.io/github/issues/1foxy2/foxium.svg)
![GitHub tag](https://img.shields.io/github/v/tag/1foxy2/foxium.svg)

Foxium is a free and open-source Minecraft mod which works to optimize many areas of the game in order to provide
better overall performance. It works on both the **client and server**, and **doesn't require the mod to be installed
on both sides**.

### What makes Foxium different?

One of the most important design goals in Foxium is *correctness*. Unlike other mods which apply optimizations to the
game, Foxium does not sacrifice vanilla functionality or behavior in the name of raw speed. It's a no compromises
solution for those wanting to speed up their game, and as such, installing Foxium should be completely transparent
to the player.

If you do encounter an issue where Foxium deviates from the norm, please don't hesitate to
[open an issue.](https://github.com/1foxy2/Foxium/issues) Each patch is carefully checked to ensure
vanilla parity, but after all, bugs are unavoidable. Before opening a new issue, please check using the search tool that your issue has not already been created, and that if
there is a suitable template for the issue you are opening, that it is filled out entirely. Issues which are duplicates
or do not contain the necessary information to triage and debug may be closed. 

### Support the developers

Foxium is made possible by the following core contributors [and others](https://github.com/CaffeineMC/lithium-fabric/graphs/contributors).
You can help support members of the core team by making a pledge to their Patreon pages below.

|    | Author   | Role   | Links   |
|----|:---------|:-------|:--------|
| ![jellysquid3's Avatar](https://avatars3.githubusercontent.com/u/1363084?s=32) | jellysquid3 | Project Lead | [Patreon](https://patreon.com/jellysquid) / [Contributions](https://github.com/CaffeineMC/lithium-fabric/commits?author=jellysquid3) |
| ![2No2Name's Avatar](https://avatars3.githubusercontent.com/u/50278648?s=32) | 2No2Name | Maintainer | [Patreon](https://patreon.com/2No2Name) / [Contributions](https://github.com/CaffeineMC/lithium-fabric/commits?author=2No2Name) |

---

## Installation

### Manual installation (recommended)

You will need NeoForge Loader installed in your game in order to load Foxium.

#### Stable releases

The latest releases of Lithium are published to our [Modrinth](https://modrinth.com/mod/foxium) and
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/foxium) pages. Releases are considered by our team to be
**suitable for general use**, but they are not guaranteed to be free of bugs and other issues.

Usually, releases will be made available on GitHub slightly sooner than other locations.

### Building from sources

Support is not provided for setting up build environments or compiling the mod. We ask that
users who are looking to get their hands dirty with the code have a basic understanding of compiling Java/Gradle
projects. The basic overview is provided here for those familiar.

#### Requirements

- JDK 17
    - You can either install this through a package manager such as [Chocolatey](https://chocolatey.org/) on Windows
      or [SDKMAN!](https://sdkman.io/) on other platforms. If you'd prefer to not use a package manager, you can always
      grab the installers or packages directly from [Adoptium](https://adoptium.net/).
- Gradle 7 or newer (optional)
    - The [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:using_wrapper) is provided
      in this repository can be used instead of installing a suitable version of Gradle yourself. However, if you are
      building many projects, you may prefer to install it yourself through a suitable package manager as to save disk
      space and to avoid many Gradle daemons sitting around in memory.

#### Building with Gradle

Lithium uses a typical Gradle project structure and can be built by simply running the default `build` task. After Gradle
finishes building the project, you can find the build artifacts (typical mod binaries, and their sources) in
`build/libs`.

**Tip:** If this is a one-off build, and you would prefer the Gradle daemon does not stick around in memory afterwards,
try adding the [`--no-daemon` flag](https://docs.gradle.org/current/userguide/gradle_daemon.html#sec:disabling_the_daemon)
to ensure that the daemon is torn down after the build is complete. However, subsequent builds of the project will
[start more slowly](https://docs.gradle.org/current/userguide/gradle_daemon.html#sec:why_the_daemon) if the Gradle
daemon is not available to be re-used.

Build artifacts ending in `api` are for developers compiling against Foxium's API.

### Configuration

Out of the box, no additional configuration is necessary once the mod has been installed. Foxium makes use of a
configuration override system which allows you to either forcefully disable problematic patches or enable incubating
patches which are otherwise disabled by default. As such, an empty config file simply means you'd like to use the
default configuration, which includes all stable optimizations by default.

See [the Wiki page](https://github.com/1foxy2/foxium/wiki) on the configuration file
format and all available options. The wiki may be outdated.

---
### License

Foxium is licensed under GNU LGPLv3, a free and open-source license. For more information, please see the
[license file](LICENSE.txt).
