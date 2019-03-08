package tw.edu.ntut.sdtlab.crawler.testapp.testunstablestate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirstUnstableActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_unstable);
        this.initTextView();
    }

    private void initTextView() {
        this.textView = (TextView) findViewById(R.id.text_view);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SS");
                        textView.setText(String.valueOf(format.format(date)));
                    }
                });
            }
        }, 0, 100);

    }
}
