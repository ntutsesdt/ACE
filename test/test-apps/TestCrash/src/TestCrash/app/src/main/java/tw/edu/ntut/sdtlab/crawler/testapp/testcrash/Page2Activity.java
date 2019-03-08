package tw.edu.ntut.sdtlab.crawler.testapp.testcrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Page2Activity extends AppCompatActivity {
    Button crashButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
        crashButton = (Button)findViewById(R.id.crashbutton);
        crashButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Integer num = null;
                num = num+1;
            }
        });
    }
}
