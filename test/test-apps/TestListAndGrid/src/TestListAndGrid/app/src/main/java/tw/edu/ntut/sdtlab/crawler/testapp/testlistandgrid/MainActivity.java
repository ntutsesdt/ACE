package tw.edu.ntut.sdtlab.crawler.testapp.testlistandgrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ListView;
import android.widget.GridView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button listbutton;
    Button gridbutton;
    //Button fixedListbutton;
    //Button fixedGridbutton;
    ListView listView;
    GridView gridView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;

    private ArrayAdapter<String> gridadapter;
    private ArrayList<String> griditems;

    private int listCounter = 0;
    private int gridCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        listbutton = (Button)findViewById(R.id.listbutton);
        gridbutton = (Button)findViewById(R.id.gridbutton);
        listView = (ListView)findViewById(R.id.listView);
        gridView = (GridView)findViewById(R.id.gridView);
        //fixedListbutton = (Button)findViewById(R.id.FixedListButton);
        //fixedGridbutton = (Button)findViewById(R.id.FixedGridButton);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        griditems = new ArrayList<String>();
        gridadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, griditems);



        listbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                items.add("List"+listCounter);
                listCounter++;
                listView.setAdapter(adapter);
            }
        });
        gridbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                griditems.add("Grid"+gridCounter);
                gridCounter++;
                gridView.setAdapter(gridadapter);
            }
        });
/*
        fixedListbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        fixedGridbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ActivityGrid.class);
                startActivity(intent);
            }
        });
        */
    }
}
