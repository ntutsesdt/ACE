package tw.edu.ntut.sdtlab.crawler.testapp.testrootstate;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private LinearLayout linearLayout;
    public int count = -1;
    private ListAdapter listAdapter = null;
    private final String FILE_FOLDER_PATH = "/storage/emulated/0/MyAndroid" ;
    private final String FILE_PATH ="/storage/emulated/0/MyAndroid/TestRootState.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.layout.activity_main);

        textView = (TextView)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.id.root);
        linearLayout = (LinearLayout)findViewById(tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.id.linearLayout);
        TextView descTextView = new TextView(this);
        String descText = "root 順序:\nroot1 > root2 > root3-1  > root3-2 > root3-2 > root3-2 > root3-2 > root3-2 > root1 > root2 > root3-1 > root3-2 > root3-2 > root3-2 > root3-2 > root3-2 ...依此類推";
        descTextView.setText(descText);
        linearLayout.addView(descTextView, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));

        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String state = decideWhichRootState();
        createRoot(state);

        writeFile();
    }

    private String decideWhichRootState() {
        if (count < 0) count = 0;
        int mcount = count % 8;
        count++;

        String state = "";
        if (mcount == 0) state = "root1";
        if (mcount == 1) state = "root2";
        if (mcount == 2) state = "root3-1";
        if (mcount == 3) state = "root3-2";
        if (mcount == 4) state = "root3-2";
        if (mcount == 5) state = "root3-2";
        if (mcount == 6) state = "root3-2";
        if (mcount == 7) state = "root3-2";
        return state;
    }

    private void createRoot(String state)
    {
        switch(state) {
            case "root1":
                createRoot1();
                break;
            case "root2":
                createRoot2();
                break;
            case "root3-1":
               createRoot3_1();
                break;
            case "root3-2":
                createRoot3_2();
                break;
        }
    }

    private void createRoot1()
    {
        textView.setText("Root " + Integer.toString(count % 8));
        Button button = new Button(this);
        button.setText("Root " + Integer.toString(count % 8));
        linearLayout.addView(button, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, State1Activity.class);
                startActivity(intent1);
            }

        });
    }

    private void createRoot2()
    {
        textView.setText("Root " + Integer.toString(count % 8));
        Button button = new Button(this);
        button.setText("Root " + Integer.toString(count % 8));
        linearLayout.addView(button, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, State2Activity.class);
                startActivity(intent2);
            }
        });
    }

    private void createRoot3_1()
    {
        listAdapter = new ArrayAdapter<>(this, tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.layout.support_simple_spinner_dropdown_item, new String[]{"A", "B"});
        textView.setText("Root 3");
        ListView listView1 = new ListView(this);
        listView1.setAdapter(listAdapter);
        linearLayout.addView(listView1);
    }

    private void createRoot3_2()
    {
        listAdapter = new ArrayAdapter<>(this, tw.edu.ntut.sdtlab.crawler.testapp.testrootstate.R.layout.support_simple_spinner_dropdown_item, new String[]{"A", "B", "C"});
        textView.setText("Root 3");
        ListView listView2 = new ListView(this);
        listView2.setAdapter(listAdapter);
        linearLayout.addView(listView2);
    }

    //讀檔
    public void readFile() throws IOException {
            //建立文件檔儲存路徑
            File mFile = new File(FILE_FOLDER_PATH);

            //若沒有檔案儲存路徑時則建立此檔案路徑
            if(!mFile.exists())
                mFile.mkdirs();

            //讀取文件檔路徑
            FileReader mFileReader = new FileReader(FILE_PATH);

            BufferedReader mBufferedReader = new BufferedReader(mFileReader);
            String mTextLine = mBufferedReader.readLine();
            if (mTextLine!=null)
                count =Integer.parseInt(mTextLine);
    }

    //寫檔
    public void writeFile() {
        try
        {
            File mFile = new File(FILE_FOLDER_PATH);

            if(!mFile.exists())
                mFile.mkdirs();

            FileWriter mFileWriter = new FileWriter(FILE_PATH);
            mFileWriter.write(Integer.toString(count));
            mFileWriter.close();
        }
        catch (Exception e) {
        }
    }
}