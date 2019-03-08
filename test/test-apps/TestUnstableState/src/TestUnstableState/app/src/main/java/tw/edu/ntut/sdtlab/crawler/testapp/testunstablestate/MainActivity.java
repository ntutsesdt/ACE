package tw.edu.ntut.sdtlab.crawler.testapp.testunstablestate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button firstButton, secondButton, thirdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initFirstButton();
        this.initSecondButton();
        this.initThridButton();
    }

    private void initFirstButton() {
        this.firstButton = (Button) findViewById(R.id.first_button);
        this.firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FirstUnstableActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSecondButton() {
        this.secondButton = (Button) findViewById(R.id.second_button);
        this.secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StableActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initThridButton() {
        this.thirdButton = (Button) findViewById(R.id.third_button);
        this.thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondUnstableActivity.class);
                startActivity(intent);
            }
        });
    }
}
