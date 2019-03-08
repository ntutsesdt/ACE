package tw.edu.ntut.sdtlab.crawler.testapp.testrootstate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class State1Activity extends AppCompatActivity {
    private Button ClossButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.layout.activity_state1);

        this.ClossButton = (Button) findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.id.ClossButton);
        ClossButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        });
    }
}
