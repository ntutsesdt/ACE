package tw.edu.ntut.sdtlab.crawler.testapp.testnondeterministic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {
    private Button button = null;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = this.getIntent();
        counter = intent.getIntExtra("counter", 0);
        button = (Button)findViewById(R.id.button3);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(Main3Activity.this, MainActivity.class);
                it.putExtra("counter", counter);
                startActivity(it);
            }

        });
    }
}
