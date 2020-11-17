# Bundle using JCEF Bundle Maven Plugin #

#### Step 1: Include maven repository ####

Create a new project and include a new maven repository:

```
<repositories>
    <repository>
        <id>jcef-maven</id>
        <name>JCef Maven Repository</name>
        <url>https://friwi.me/jcef/maven</url>
    </repository>
</repositories>
```

And a new plugin repository:

```
<pluginRepositories>
    <pluginRepository>
        <id>jcef-maven</id>
        <name>JCef Maven Repository</name>
        <url>https://friwi.me/jcef/maven</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

#### Step 2: Add dependencies ####

Add jcef-main to your dependencies. The artifact contains all that is needed, including natives.
You can find the newest version here: https://friwi.me/jcef/maven/org/cef/jcef-main/

```
<dependency>
    <groupId>org.cef</groupId>
    <artifactId>jcef-main</artifactId>
    <version>YOUR_VERSION_HERE</version>
</dependency>
```
If you only want to include specific operating systems or no natives at all,
use the artifact ``org.cef:jcef`` instead of ``org.cef:jcef-main`` and include the natives yourself
(``org.cef:jcef-natives-<linux/macosx/win><32/64>``).

#### Step 3: Initialize JCEF using JCEFLoader ####

As loading JCEF depends on your environment, we simplified it.
To create your CefApp instance, just call:

```
CefApp app = JCefLoader.installAndLoadCef();
```

You can pass an installation directory (for bundle extraction), CefSettings
and an array of arguments for the initialization. For now, just call the method without
arguments.

When you lost your CefApp instance, you can always get it back by calling:

```
CefApp app = CefApp.getInstance();
```

#### Step 4: Enjoy ####

Run an example or start coding using JCEF.
Examples can be found on the
<a href="https://github.com/chromiumembedded/java-cef/tree/master/java/tests">official JCEF repo</a>.
Make sure that you always call the loader before performing any calls towards JCEF!

#### Step 5: Deploy ####

Use the maven shade plugin to build a fat jar with dependencies of your project.
Add the shade plugin to your build section in pom.xml and fill in your main class name.
Keep the excludes for the native jars to prune them from your project.

```
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.2.4</version>
      <executions>
          <execution>
              <phase>package</phase>
              <goals>
                  <goal>shade</goal>
              </goals>
              <configuration>
                  <finalName>${project.artifactId}-jar-with-dependencies</finalName>
                  <transformers>
                      <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                          <manifestEntries>
                              <Main-Class>fully.qualified.MainClass</Main-Class>
                          </manifestEntries>
                      </transformer>
                  </transformers>
                  <filters>
                      <filter>
                          <artifact>*:*</artifact>
                          <excludes>
                              <exclude>jcef-natives-*.jar</exclude>
                          </excludes>
                      </filter>
                  </filters>
              </configuration>
          </execution>
      </executions>
      ugin>
  </plugins>
</build>
```

Now include the jcef-bundle-maven-plugin in your project (same section):

```
<plugin>
    <groupId>org.cef</groupId>
    <artifactId>jcef-bundle-maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <!-- Add your bundle builds here -->
    </executions>
</plugin>
```

Now add an execution for every operating system that you want to build:

##### Linux/Windows execution #####

```
<execution>
    <id>linuxwin</id>
    <goals>
        <goal>linux64</goal>
        <goal>win64</goal>
    </goals>
    <configuration>
        <exportType>zip</exportType>
        <relocations>
            <relocation>${project.artifactId}-jar-with-dependencies.jar:${project.artifactId}.jar</relocation>
        </relocations>
    </configuration>
</execution>
```

<b>Configuration options for Linux/Windows bundles</b>

Goals: ``linux32``, ``linux64``, ``win32`` and ``win64``

|Option|Default|Required|Description|
|------|-------|--------|-----------|
``exportType``|``plain``|false|The export type to use. ``plain`` exports as directory, ``zip`` as zip-archive and ``targz`` as tar.gz-archive. For Linux and Windows we recommend ``zip``, as the format can be extracted out-of-the-box and no executable permissions are required.|
``relocations[]``|-|true|Relocations to perform (e.g. copy jar file to bundle, or copy resources). Entries are described as relative paths ``from:to``, delimited by ``:``. Relocating folders is supported.<br/>From path: Relative to your projects build directory (e.g. ``target/``)<br/>To path: Relative to your bundle folder (e.g. ``target/jcef-bundle-X/``)|
``relativeBundlePath``|``cef_bundle``|false|The folder relative to the bundle folder, where the natives should reside. This has to be the same as the argument in the call to ``JCefLoader.installAndLoadCef(new File("cef_bundle"));``|

##### MacOSX execution #####

```
<execution>
    <id>mac</id>
    <goals>
        <goal>macosx64</goal>
    </goals>
    <configuration>
        <exportType>targz</exportType>
        <relocations>
            <relocation>${project.artifactId}-jar-with-dependencies.jar:${project.artifactId}.jar</relocation>
        </relocations>
        <mainClass>fully.qualified.MainClass</mainClass>
    </configuration>
</execution>
```

<b>Configuration options for MacOSX bundles</b>

Goal: ``macosx64``

|Option|Default|Required|Description|
|------|-------|--------|-----------|
``exportType``|``plain``|false|The export type to use. ``plain`` exports as directory, ``zip`` as zip-archive and ``targz`` as tar.gz-archive. For MacOSX we recommend ``targz``, as the helper and framework files need to be marked as executable to function.|
``relocations[]``|-|true|Relocations to perform (e.g. copy jar file to bundle, or copy resources). Entries are described as relative paths ``from:to``, delimited by ``:``. Relocating folders is supported.<br/>From path: Relative to your projects build directory (e.g. ``target/``)<br/>To path: Relative to your bundle folder, targeting the path for jar/class files (e.g. ``target/jcef-bundle-macosx64/jcef_app.app/Contents/Java/``)|
``mainClass``|-|true|The main class of the bundle (e.g. org.cef.tests.MainFrame)|
``bundleName``|``jcef_app.app``|false|The name of the bundle folder|
``bundleIdentifier``|``org.cef.jcef``|false|The identifier of the MacOS bundle|
``bundleDisplayName``|``jcef_app``|false|The name that MacOS will show the user for our bundle|
``bundleShortVersion``|``${project.version}``|false|A short version number (e.g. 1.0)|
``bundleVersion``|``${project.version}``|false|A long version number, can be the same as short version number (e.g. 1.0)|
``bundleCopyright``|-|false|Copyright information for this bundle (e.g. &copy; Fritz Windisch)|
``jvmOptions[]``|-|false|An array of options to pass to the JVM. Can not contain a new ``java.library.path``, the existing path points to the classpath (relocate your libraries with ``Y/X.dylib:X.dylib``).|
``jvmArgs[]``|-|false|An array of arguments to pass to the embedded program|
Tip: You can also copy a new ``CefIcons.icns`` file into your bundle to change the default CEF icon using a relocation.

##### Performing the build #####

Once you are satisfied with your configuration, run ``mvn clean package`` and your bundles will appear in your ``target/`` folder.