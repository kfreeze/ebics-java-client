EBICS Java Client
=====

## Changes to the original:

- Took some inspiration and code from: [ebics-java-web-client](https://github.com/spaced/ebics-web-client/tree/master)

- Updated to EBICS 3.0 (H005)
- Dependency Updates and Java 21
- Added [pain.008.001.08.xsd](src/main/xsd/pain.008.001.08.xsd) and a DocumentBuilder for it, mainly used for SDD
- Added german error translations
- Code Cleanup, refactoring and fixed some sonar issues
- Updated to Version 1.3

This library allows to interact with banks using the EBICS (Electronic Banking Internet Communication Standard)

You can use the `EbicsClient` as command line tool or use it from your Java application.

How to get started:

https://github.com/ebics-java/ebics-java-client/wiki/EBICS-Client-HowTo

You can build it directly from the source with maven or use the releases from [JitPack](https://jitpack.io/#ebics-java/ebics-java-client/).

Gradle:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
    implementation 'com.github.ebics-java:ebics-java-client:master-SNAPSHOT'
}
```
Maven
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.ebics-java</groupId>
    <artifactId>ebics-java-client</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
 

This project is based on https://sourceforge.net/p/ebics/

Main differences with this fork:

- Support for French, German and Swiss banks
- Command line client to do the setup, initialization and to download files from the bank
- Use of maven for compilation instead of ant + Makefile + .sh scripts
