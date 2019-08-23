###### tags: `Crawler ReadMe Ref.`

## Running ACE with **Source Code**

Run ACE through IntelliJ IDEA: 
1. Install Java JDK, Intellij, Maven (can be installed by Intellij), graphviz, and adb.
2. Download or clone ACE source code into the directory `ACE`.
3. Open Intellij and import the project under the `ACE` directory.
    - Click 'Import Project', 'OK', and 'Maven'; use default value for the rest of the settings.
4. Open `./configuration/configuration.xml` and modify the following properties.
    - `<adb>` -- The path of 'adb.exe' file.
        - ![](https://i.imgur.com/OCNTk0x.png)
    - `<graphvizLayout>` -- The path of graphviz 'dot.exe' file.
        - ![](https://i.imgur.com/E9XTgWx.png)
    - `<deviceSerialNumber>` -- Serial number of the Android device to be used.
        - Change the setting of the device to developer mode (the setting is device dependent).
            - For example, Samsung A8:
            Click `Settings` -> `About phone` -> `Software information`
        - Click on `Build number` several (7 or more) times 
            - Then the `Developer options` will be shown on the first page of `Settings`
            - In the `Developer options`, turn on the `ON` button and then turn on `USB debugging` and `Stay awake` options
            - ![](https://i.imgur.com/am4EbS4.jpg)![](https://i.imgur.com/hhA5kHc.jpg)
        - Connect the android device to the computer. The serial number of the device can be obtained via executing the command `adb devices` from command line.
        - ![](https://i.imgur.com/xxTg6Su.png)
    - `<packageName>` -- App's packageName.
        - Look at the AndroidManifest.xml file of the app to get packageName.
            - For example, the packageName of the TestInputData (under `./test/test-apps/TestInputData`) is `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata`.
            ![](https://i.imgur.com/EdYmYvE.png)
    - `<launcherActivity>` -- App's activity to be launched.
        - Look at the AndroidManifest.xml file of the app to get launcherActivity.
        - For example, the launcherActivity of the TestInputData app (under `./test/test-apps/TestInputData`) is `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.MainActivity`, which is the concatenation of `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata` and `.MainActivity`.
            ![](https://i.imgur.com/SWXczl7.png)
5. Execute 'main.java' to start ACE.
