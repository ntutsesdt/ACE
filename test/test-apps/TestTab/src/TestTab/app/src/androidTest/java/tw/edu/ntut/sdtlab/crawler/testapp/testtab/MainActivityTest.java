package tw.edu.ntut.sdtlab.crawler.testapp.testtab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import tw.edu.ntut.sdtlab.crawler.testapp.testtab.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private ActivityTestRule<MainActivity> activityTestRule;
    private boolean testStop = false;

    @Test
    public void testInitPrint() throws InterruptedException {
        activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
        activityTestRule.launchActivity(null);
        IntentFilter intentFilter = new IntentFilter("test");
        BroadcastReceiver brocastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                testStop = true;
            }
        };
        Activity activity = activityTestRule.getActivity();
        activity.registerReceiver(brocastReceiver, intentFilter);
        while (!activityTestRule.getActivity().isFinishing() && !testStop) {
        }
        activity.unregisterReceiver(brocastReceiver);
    }
}