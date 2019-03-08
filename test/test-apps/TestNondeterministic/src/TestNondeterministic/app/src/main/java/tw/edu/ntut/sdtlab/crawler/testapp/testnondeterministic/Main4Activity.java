package tw.edu.ntut.sdtlab.crawler.testapp.testnondeterministic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Main4Activity extends AppCompatActivity {
    private Button page2Button = null;
    private Button page3Button = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        page2Button = (Button)findViewById(R.id.button5);
        page3Button = (Button)findViewById(R.id.button6);
    }
}
