# Fat Jar using JCEF Maven #

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

Use the maven assembly plugin to build a fat jar with dependencies of your project.
Add the assembly plugin to your build section in pom.xml and fill in your main class name.

```
<build>
  <plugins>
    <plugin>
      <artifactId>maven-assembly-plugin</artifactId>
      <configuration>
        <archive>
          <manifest>
            <mainClass>fully.qualified.MainClass</mainClass>
          </manifest>
        </archive>
        <descriptorRefs>
          <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Then run the assembly:single goal to build a fat jar.