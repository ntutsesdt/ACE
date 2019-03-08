package tw.edu.ntut.sdtlab.crawler.testapp.testunstablestate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SecondUnstableActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_unstable);
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
                        textView.setText(String.valueOf(System.currentTimeMillis()));
                    }
                });
            }
        }, 0, 100);

    }
}
