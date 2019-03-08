package tw.edu.ntut.sdtlab.crawler.testapp.testlistandgrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class ActivityGrid extends AppCompatActivity {
    GridView gridView = null;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_grid);
        gridView = (GridView) findViewById(R.id.gridView2);
        list = new ArrayList<String>();
        for(int x=1;x<4;x++) {
            list.add("項目"+x);
        }
        GridAdapter adapterItem = new GridAdapter(this, list);
        gridView.setAdapter(adapterItem);
    }
}
