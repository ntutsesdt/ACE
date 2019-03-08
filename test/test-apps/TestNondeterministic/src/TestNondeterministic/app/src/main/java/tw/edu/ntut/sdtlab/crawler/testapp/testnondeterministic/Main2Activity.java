package tw.edu.ntut.sdtlab.crawler.testapp.testnondeterministic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {
    private Button button = null;
    private Button clickButton = null;
    private Button backButton = null;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = this.getIntent();
        counter = intent.getIntExtra("counter", 0);
        button = (Button)findViewById(R.id.button2);
        clickButton = (Button)findViewById(R.id.button4);
        backButton =(Button)findViewById(R.id.button7);
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(Main2Activity.this, MainActivity.class);
                counter++;
                it.putExtra("counter", counter);
                startActivity(it);
                }
                });
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(Main2Activity.this, MainActivity.class);
                counter++;
                it.putExtra("counter", counter);
                startActivity(it);
            }

        });
        clickButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(Main2Activity.this, Main4Activity.class);
                startActivity(it);
            }
        });
    }
}
