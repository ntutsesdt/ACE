###### tags: `Crawler ReadMe Ref.`

## **How to run ACE with executable jar**
> The following example explains the crawling of the `TestInputData` app.
1. Install Java JRE, graphviz, and adb.
2. Download ACE zip ([Download Link](https://github.com/ntutsesdt/ACE/releases/download/v1.0/ACE_jar.zip)) and unzip it into the directory `ACE`
3. Change directory to `ACE`
4. Open `./configuration/configuration.xml` and modify the following properties.
    * `<adb>` -- The path of 'adb.exe' file.
        > ![](https://i.imgur.com/OCNTk0x.png)
    * `<graphvizLayout>` -- The path of graphviz 'dot.exe' file.
        > ![](https://i.imgur.com/E9XTgWx.png)
    * `<deviceSerialNumber>` -- Serial number of the Android device to be used.
        > * Change the setting of the device to developer mode (the setting is device dependent).
        >> * For example, Samsung A8:
        >>> * Click `Settings` -> `About phone` -> `Software information`
        >>> * Click on `Build number` several (7 or more) times 
        >>> * Then the `Developer options` will be shown on the first page of `Settings`
        >>> * In the `Developer options`, turn on the `ON` button and then turn on `USB debugging` and `Stay awake` options
        >>> ![](https://i.imgur.com/am4EbS4.jpg)![](https://i.imgur.com/hhA5kHc.jpg)
        > * Connect the android device to the computer. The serial number of the device can be obtained via executing the command `adb devices` from command line.
        > ![](https://i.imgur.com/xxTg6Su.png)
    * `<packageName>` -- App's packageName.
        * Look at the AndroidManifest.xml file of the app to get packageName.
            * For example, the packageName of the TestInputData (under `./test/test-apps/TestInputData`) is `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata`.
            ![](https://i.imgur.com/EdYmYvE.png)
    * `<launcherActivity>` -- App's activity to be launched.
        * Look at the AndroidManifest.xml file of the app to get launcherActivity.
        * For example, the launcherActivity of the TestInputData app (under `./test/test-apps/TestInputData`) is `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.MainActivity`, which is the concatenation of `tw.edu.ntut.sdtlab.crawler.testapp.testinputdata` and `.MainActivity`.
            ![](https://i.imgur.com/SWXczl7.png)
5. Install the TestInputData app into the target android device.
    > ![](https://i.imgur.com/MpsN347.png)
    > ![](https://i.imgur.com/SAE7Y6Z.png)
    
6. Prepare input-data (See [Set Input Field Data](#Set-Input-Field-Data) for more information)
    * Create directory `./input-data` and copy the file `./test/test-apps/TestInputData/input-data/input.xml` to `./input-data`. The file `input.xml` specifies the data values of input fields.
        > `mkdir ./input-data` (can be ignored if the directory already exists)
        > `cp ./test/test-apps/TestInputData/input-data/input.xml ./input-data/.`
    * Change `./configuration/configuration.xml` and set the option `inputDataFileName` to `input.xml`.
        > ![](https://i.imgur.com/uwxNQbw.png)



7. Run ACE: run `java -jar ACE.jar` and wait for the crawling to be completed.
    ![](https://i.imgur.com/wpJtrox.png)

8. Look at the directory `.\output\date_time_testinputdata_XXX`. Open StateTransitionDiagram.svg with a browser (e.g., Chrome). The diagram should be like:
![](https://i.imgur.com/447CRY9.png)
