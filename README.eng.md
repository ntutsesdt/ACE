# Android CrawlEr (ACE) _ ReadMe (English version)
###### tags: `Crawler` 
License : MIT

*Read this in other languages: [English](README.eng.md), [中文](https://hackmd.io/5Pf9l2ImQkKSQtIt2Lkkew?view#Android-CrawlEr-ACE-_-ReadMe)*
## **Getting Started**

### **Prerequisites**
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* [Graphviz 2.38](https://www.graphviz.org/)
* [Android Debug Bridge (adb) v1.0.40](https://developer.android.com/studio/releases/platform-tools.html)

####  **Library** 
* [junit 4.12](https://junit.org/junit4/)
* [dom4j 1.6.1](https://dom4j.github.io/)
* [jaxen 1.1.6](https://mvnrepository.com/artifact/jaxen/jaxen)
* [jacoco 0.7.4](https://www.eclemma.org/jacoco/)

####  **Our System Environment** 
* Windows 10
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) v2017.2
* Apache Maven v3.5.0
* Android Studio v3.1.4

### How to run ACE with **executable jar**
* Download zip( [Download Link](???))
    *  ACE need to use './configuration/configuration.xml' and './input-data/example-input-data.xm

* Open '/configuration/configuration.xml' and modify the following properties.
    * `<adb>` -- The path of 'adb.exe' , an ADB tool
    * `<graphvizLayout>` -- The path of '.\release\bin\dot.exe', Graphviz file
    * `<deviceSerialNumber>` -- Serial number of Android device
        > After the device is connected to the computer, the serial number of the device can be obtained via the command `adb devices` from command line.
    * `<packageName>` and `<launchableActivity>` -- Application's packageName and the name of the Activity being launched
    *  Other properties can be modified as needed 

* Execute command`java -jar AndroidCrawler-1.0-SNAPSHOT.jar` to start up ACE

![Execute jar cmd](https://i.imgur.com/67eIKkE.png)
    
<font color=#ff5050 >
※The App to be tested needs to be manually installed on the Android device.
</font>

### How to run ACE with **Source Code**

####  **Prerequisites**
* [Apache Maven](https://maven.apache.org/)


Run ACE through IntelliJ IDEA: 
1. Open IntelliJ and select 'Import Project'. Then select the project folder
2. Select 'Import project from external model' and select 'Maven'. Then all settings will use the default value.
3. Open '/configuration/configuration.xml' and modify the following properties.
    * `<adb>` -- The path of 'adb.exe' , an ADB tool
    * `<graphvizLayout>` -- The path of '.\release\bin\dot.exe', Graphviz file
    * `<deviceSerialNumber>` -- Serial number of Android device
        > After the device is connected to the computer, the serial number of the device can be obtained via the command `adb devices` from command line.
    * `<packageName>` and `<launchableActivity>` -- Application's packageName and the name of the Activity being launched
    *  Other properties can be modified as needed 
4. Execute 'main.java' to start ACE

<font color=#ff5050 >
※The app to be tested needs to be manually installed on the Android device.
</font>



----
## Build an **executable jar** with **Maven** (need Source Code)

Build an executable jar by maven command or using IntelliJ IDE

### Building the jar file
- #### Use **Command line**

    - Execute command `mvn package`  under the project root folder
    - And then finish building AndroidCrawler-1.0-SNAPSHOT.jar
        ![Build jar cmd](https://i.imgur.com/UXaWKVB.png)
    


- #### Use **IntelliJ IDEA**

    1. After Open the project, Click on 'View>Tool Windows>Maven Projects'
    2. Double Click on 'Lifecycle/package' to package an ACE jar file

### Move your builded jar
- Location of builded jar：![Build jar cmd_2](https://i.imgur.com/NPifn3Y.png)
Copy './target/AndroidCrawler-1.0-SNAPSHOT.jar' to [project root folder](https://i.imgur.com/7roxtGa.png) 
(To make ACE able to find './configuration/configuration.xml' and './input-data/example-input-data.xml')
