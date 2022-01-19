<div id="title" align="center">
<h1>JCEF MAVEN</h1>
<a href="../../releases/latest"><img alt="build-all" src="../../actions/workflows/maven-release.yml/badge.svg"></img></a>
<a href="https://search.maven.org/artifact/me.friwi/jcefmaven"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/me.friwi/jcefmaven.svg?label=Maven%20Central"></img></a>

<h4>Independent project to produce maven artifacts for the JCef project</h4>
<h6>Visit the JCEF repo at <a href="https://bitbucket.org/chromiumembedded/java-cef/src/master/">bitbucket</a> or <a href="https://github.com/chromiumembedded/java-cef">github</a> </h6>

<h5><img alt="browser" src="demo.png"></img><br>
Embed a complete browser in your Java Apps - supports Java 8+</h5>
<br>

**Supports**
<table>
  <tr><td align="right"><a href="#"><img src="https://simpleicons.org/icons/linux.svg" alt="linux" width="32" height="32"></a></td><td align="left">amd64, arm64, i386, arm</td></tr>
  <tr><td align="right"><a href="#"><img src="https://simpleicons.org/icons/windows.svg" alt="windows" width="32" height="32"></a></td><td align="left">amd64, arm64, i386</td></tr>
  <tr><td align="right"><a href="#"><img src="https://simpleicons.org/icons/apple.svg" alt="macosx" width="32" height="32"></a></td><td align="left">amd64, arm64</td></tr>
</table>
  
</div>

## Installation
**Use with Maven:**
```Maven POM
<dependency>
    <groupId>me.friwi</groupId>
    <artifactId>jcefmaven</artifactId>
    <version>95.7.14.4</version>
</dependency>
```

**Use with Gradle:**
```Gradle
implementation 'me.friwi:jcefmaven:95.7.14.4'
```

---

## How to use
You can find the most recent versions of the artifacts on the [releases](../../releases) page of this repository. Alongside each release is also a table with platforms that have been tested. If you have tested a platform and build combination that has not been tested before (using the [sample app](https://github.com/jcefmaven/jcefsampleapp)), make sure to open a [new issue](../../issues/new?assignees=&labels=test+report&template=report_artifact_working.md&title=%5BTR%5D+Test+report) to share your findings!

Once you found a version you want to use, include it as a dependency into your project. An example include for Maven and Gradle can be seen above.
This will only include the base jcef library and jogl in your project. Natives will be downloaded and extracted on first run. If you want to skip downloading and instead bundle the natives, include the native artifacts in your project dependencies. You can see all of them [here](../../packages?tab=packages&q=natives). It is recommended to only include one bundle per build though, as each bundle is ~100MB. If you wish to include them, make sure you export one build per platform!

Once you added your dependencies, you need to fire up jcefmaven in your code. No worries, it's not complicated!
```java
//Create a new CefAppBuilder instance
CefAppBuilder builder = new CefAppBuilder();

//Configure the builder instance
builder.setInstallDir(new File("jcef-bundle")); //Default
builder.setProgressHandler(new ConsoleProgressHandler()); //Default
builder.addJcefArgs("--disable-gpu"); //Just an example
builder.getCefSettings().windowless_rendering_enabled = true; //Default - select OSR mode

//Set an app handler. Do not use CefApp.addAppHandler(...), it will break your code on MacOSX!
builder.setAppHandler(new MavenCefAppHandlerAdapter(){...});

//Build a CefApp instance using the configuration above
CefApp app = builder.build();
```
From there, continue to write your app using jcef as you are used to. You can call `builder.build()` as many times as you want. It is even thread-safe while initializing (will pause threads and return when initialization was completed).

If you need some code examples to create your first app, have a look at the [tests](jcefmaven/src/test) on this repository or at the [sample app](https://github.com/jcefmaven/jcefsampleapp).

#### Some additional useful code snippets
If you want to get the current platform as determined by jcefmaven (e.g. to disable osr on win-arm64), you can use:
```java
EnumPlatform platform = EnumPlatform.getCurrentPlatform();
EnumOS os = platform.getOs();
```

If you want to obtain version information, you can use:
```java
//Provides build version data. Requires build_meta.json to be on classpath.
CefBuildInfo buildInfo = CefBuildInfo.fromClasspath();

//Provides JCEF version data. You can call this after initialization.
CefVersion cefVersion = cefApp.getVersion();
```

## Requirements
- Java 8 or later

## Limitations
- No OSR mode supported on win-arm64 (no jogamp)
- `CefApp.addAppHandler(...)` should not be used. Use `builder.setAppHandler(...)` instead (requires a `CefMavenAppHandlerAdapter`)

## Reporting bugs
Please only report bugs here that are related to the maven artifacts.
Please report bugs in JCEF/CEF to the [corresponding repository on Bitbucket](https://bitbucket.org/chromiumembedded/).

