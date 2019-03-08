package tw.edu.ntut.sdtlab.crawler.testapp.testinputdata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText accountText = null;
    private EditText passwordText = null;
    private Button checkButton = null;

    private String userAccount1 = "test1";
    private String userPassword1 = "test135";

    private String userAccount2 = "test2";
    private String userPassword2 = "test246";

    private String userAccount3 = "Eric";
    private String userPassword3 = "EricPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.R.layout.activity_main);

        accountText = (EditText) findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.R.id.account);
        accountText.requestFocus();
        passwordText = (EditText) findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.R.id.password);
        checkButton = (Button)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testinputdata.R.id.checkbutton);

        checkButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkUserAccount(accountText.getText().toString(), passwordText.getText().toString())) {
                    Intent it = new Intent();
                    it.setClass(MainActivity.this, ErrorActivity.class);
                    startActivity(it);
                }
                else {
                    Intent it = new Intent();
                    it.setClass(MainActivity.this, CorrectActivity.class);
                    startActivity(it);
                }
            }

        });

    }

    protected boolean checkUserAccount(String userAccount, String userPassword) {
        if(userAccount.equals(userAccount1) && userPassword.equals(userPassword1))
            return true;
        else if(userAccount.equals(userAccount2) && userPassword.equals(userPassword2))
            return true;
        else if(userAccount.equals(userAccount3) && userPassword.equals(userPassword3))
            return true;
        return false;
    }
}
