# JCEF Maven Artifacts #

This repository provides artifacts to integrate JCEF in Maven projects.
Supports Linux, Windows and MacOSX.

See the official JCEF git repository:
<a href="https://bitbucket.org/chromiumembedded/java-cef/src/master/">bitbucket</a> or 
<a href="https://github.com/chromiumembedded/java-cef">github</a>.<br/>
See the jcefbuild binary builds for JCEF:
<a href="https://github.com/jcefbuild/jcefbuild">jcefbuild/jcefbuild</a>.

### How to use ###

There are two deployment options for JCEF with maven.
You can either embed the library in a fat jar or create a bundle
using the jcef-bundle-maven-plugin.

<b>Please choose a deploy option below to get instructions:</b>

|Option|Advantages|Disadvantages|
|----|-------|-----|
|**[Fat jar](doc/FAT_JAR.md)**|Fat jar is platform independent.|Not MacOSX compatible and 10 second bundle extraction time on first run. Fat jar for all platforms has ~320MB.|
|**[Bundle Plugin](doc/BUNDLE_PLUGIN.md)**|MacOSX compatible and no extraction time. A lot smaller (~80MB).|Bundles are platform dependent. Testing without exporting not possible on MacOSX.|
We recommend using the bundle plugin option.

### Repository ###

```
<repository>
    <id>jcef-maven</id>
    <name>JCef Maven Repository</name>
    <url>https://friwi.me/jcef/maven</url>
</repository>
```

#### Available artifacts: ####

|GroupId|ArtifactId|Description|
|-------|----------|-----------|
|org.cef|jcef|Contains all JCEF artifacts apart from natives (jcef-api and jcef-loader)|
|org.cef|jcef-api|Contains the JCEF API from the official JCEF repository|
|org.cef|jcef-bundle-maven-plugin|Maven Plugin used to create bundles|
|org.cef|jcef-loader|Artifact used to load JCEF in a Maven environment|
|org.cef|jcef-main|Contains all JCEF artifacts with natives (jcef-api, jcef-loader and Linux/Windows natives). MacOSX natives not included - MacOSX version only works from bundle!|
|org.cef|jcef-natives-linux32|32-bit Linux natives|
|org.cef|jcef-natives-linux64|64-bit Linux natives|
|org.cef|jcef-natives-macosx64|Universal MacOSX natives|
|org.cef|jcef-natives-win32|32-bit Windows natives|
|org.cef|jcef-natives-win64|64-bit Windows natives|