package tw.edu.ntut.sdtlab.crawler.testapp.testnondeterministic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button button = null;
    private TextView textView;
    int counter =0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initTextView();
        this.button = (Button) findViewById(R.id.button);
        Intent intent=this.getIntent();
        counter = intent.getIntExtra("counter", 0);


        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter==0)
                {
                    Intent it = new Intent();
                    it.setClass(MainActivity.this, Main2Activity.class);
                    it.putExtra("counter", counter);
                    startActivity(it);
                }
              else
                {
                    Intent it = new Intent();
                    it.setClass(MainActivity.this, Main3Activity.class);
                    it.putExtra("counter", counter);
                    startActivity(it);
                }
            }
        });
    }

    private void initTextView() {
        this.textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
