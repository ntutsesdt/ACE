package tw.edu.ntut.sdtlab.crawler.testapp.testmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;
import tw.edu.ntut.sdtlab.crawler.testapp.testmenu.R;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for(int i = 0; i < 1; i++){
            menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, "Item " + Integer.toString(i + 1));
        }
        SubMenu subMenu = menu.addSubMenu(3, Menu.FIRST, Menu.NONE, "Item 2");
        for(int i = 0; i < 1; i++){
            subMenu.add(Menu.FIRST, Menu.FIRST, Menu.NONE, "Sub Item " + Integer.toString(i + 1));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        textView.setText("nothing");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getGroupId() == Menu.NONE || item.getGroupId() == Menu.FIRST)
            textView.setText("menu item selected.");
        return super.onOptionsItemSelected(item);
    }
}
