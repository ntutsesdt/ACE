package tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText result;
    Button button;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.layout.activity_main);
        result = (EditText)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.editText);
        button = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button);
        button2 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button2);
        button3 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button3);
        button4 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button4);
        button5 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button5);
        button6 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button6);
        button7 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button7);
        button8 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button8);
        button9 = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.button9);
        resetButton = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testdistanceapplication.R.id.resetButton);

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"1";
                result.setText(str);

                //result.setText("1");
            }
        });
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"2";
                result.setText(str);

                //result.setText("2");
            }
        });
        button3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"3";
                result.setText(str);

                //result.setText("3");
            }
        });
        button4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"4";
                result.setText(str);

                //result.setText("4");
            }
        });
        button5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"5";
                result.setText(str);

                //result.setText("5");
            }
        });
        button6.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"6";
                result.setText(str);

                //result.setText("6");
            }
        });
        button7.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"7";
                result.setText(str);

                //result.setText("7");
            }
        });
        button8.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"8";
                result.setText(str);

                //result.setText("8");
            }
        });
        button9.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                str = result.getText().toString();
                str = str+"9";
                result.setText(str);

                //result.setText("9");
            }
        });
        resetButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");
            }
        });
    }
}
