package tw.edu.ntut.sdtlab.crawler.testapp.testlistandgrid;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListActivity extends AppCompatActivity {
    ListView listView;
    List<String> list;
    List<Boolean> listShow;
    //private Object[] listItem = {new CheckBox(), };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setOnItemClickListener(new OnItemClickListener()
                                        {
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                                            {
                                                /*
                                                CheckedTextView chkItem = (CheckedTextView) v.findViewById(R.id.check1);
                                                chkItem.setChecked(!chkItem.isChecked());
                                                //Toast.makeText(ListActivity.this, "您點選了第 "+(position+1)+" 項", Toast.LENGTH_SHORT).show();
                                                listShow.set(position, chkItem.isChecked());
                                                */
                                            }
                                        }
        );


        list = new ArrayList<String>();
        listShow = new ArrayList<Boolean>();
        for(int x=1;x<4;x++)
        {
            list.add("項目"+x);
            listShow.add(true);
        }
        ListAdapter adapterItem = new ListAdapter(this, list);
        listView.setAdapter(adapterItem);
    }
}
