package tw.edu.ntut.sdtlab.crawler.ace.util;

import org.junit.*;
import org.junit.rules.ExpectedException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ConfigItemException;

import javax.naming.ConfigurationException;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class EnvironmentCheckerTest {
    private EnvironmentChecker environmentChecker;
    private  String wrongPath;
    private  String wrongString ;
    private Object[] arguments;
    @Before
    public void setup() {
        environmentChecker =new EnvironmentChecker();
    }

    @Test
    public void testCheckEnviroment() throws InterruptedException, IllegalAccessException, NoSuchFieldException {
        environmentChecker.checkEnviroment();
    }

    @Test
    public void testADB() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkADBPath", String.class);
        method.setAccessible(true);
    }
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Test
    public void testADBException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //arrange

        Method method = EnvironmentChecker.class.getDeclaredMethod("checkADBPath", String.class);

        method.setAccessible(true);
        wrongPath="C:/user/Program File/adb.exe";
        arguments = new Object[]{wrongPath};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            Assert.assertEquals("Cannot find adb command at "+wrongPath,e.getTargetException().getMessage());
        }
    }
    @Test
    public void testGraphviz() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkGraphvizPath", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker, Config.GRAPHVIZ_LAYOUT_PATH);
    }

    @Test
    public void testGraphvizException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkGraphvizPath", String.class);
        method.setAccessible(true);
        wrongPath ="C:\\graphiz\\dot.exe";
        arguments = new Object[]{wrongPath};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
            Assert.assertEquals("Cannot find graphviz command at "+wrongPath,e.getTargetException().getMessage());
        }
    }
    @Test
    public void testCheckSerialNumber() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkSerialNumber", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker, Config.DEVICE_SERIAL_NUMBER);
    }
    @Test
    public void testSerialNumberException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkSerialNumber", String.class);
        method.setAccessible(true);
        wrongString= "A123456";
        arguments = new Object[]{wrongString};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
            Assert.assertEquals("Cannot find the device with serial number \""+wrongString+"\"",e.getTargetException().getMessage());
        }
    }
    @Test
    public void testCheckPackageName() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkPackageExist", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker, Config.PACKAGE_NAME);
    }
    @Test
    public void testPackageNameException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkPackageExist", String.class);
        method.setAccessible(true);
        wrongString = "tw.edu.ntut.testapp" ;
        arguments = new Object[]{wrongString};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
            Assert.assertEquals("Cannot find the app with packageName \""+ wrongString+"\"",e.getTargetException().getMessage());
        }
    }
    @Test
    public void testCheckLauncherActivity() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkActivityExist", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker, Config.LAUNCHER_ACTIVITY);
    }
    @Test
    public void testLauncherActivityException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkActivityExist", String.class);
        method.setAccessible(true);
        wrongPath ="tw.edu.ntut.sdtlab.testinputdata.MainActivity";
        arguments = new Object[]{wrongPath};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
            Assert.assertEquals("Cannot launch the app with launcherActivity \""+wrongPath+"\"",e.getTargetException().getMessage());
        }
    }
    @Test
    public void testCheckInputDataPath() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkInputDataPath", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker, Config.INPUT_DATA_FILE_NAME);
    }
    @Test
    public void testInputDataPathException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkInputDataPath", String.class);
        method.setAccessible(true);
        wrongPath ="inputData.xml";
        arguments = new Object[]{wrongPath};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
            Assert.assertEquals("Cannot find the input data at ./input-data/"+wrongPath,e.getTargetException().getMessage());
        }
    }
    @Test
    public void testCheckConfiguration() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkConfiguration", String.class);
        method.setAccessible(true);
        method.invoke(this.environmentChecker,"configuration/configuration.xml" );
    }
    @Test
    public void testConfigurationException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EnvironmentChecker.class.getDeclaredMethod("checkConfiguration", String.class);
        method.setAccessible(true);
        wrongPath ="configuration.xml";
        arguments = new Object[]{wrongPath};
        try{
            method.invoke(this.environmentChecker,arguments);
        }
        catch (InvocationTargetException e) {
            System.out.println(e.getTargetException().getMessage());
           
            Assert.assertEquals("Cannot find the Configuration at ./configuration/" +wrongPath,e.getTargetException().getMessage());
        }
    }
}
