package tw.edu.ntut.sdtlab.crawler.testapp.testlistandgrid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by oil on 2016/6/23.
 */
public class GridAdapter extends BaseAdapter {
    private Activity activity;
    private List<String> mList;
    protected int counter = 0;

    private static LayoutInflater inflater = null;

    public GridAdapter(Activity a, List<String> list)
    {
        activity = a;
        mList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount()
    {
        return 8;
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        /*
        CheckedTextView chkBshow = null;
        View vi = convertView;
        if(convertView==null)
        {
            vi = inflater.inflate(R.layout.list_item, null);
        }
        chkBshow = (CheckedTextView) vi.findViewById(R.id.check1);
        chkBshow.setText(mList.get(position).toString());
        return vi;
        */
        System.out.println("position = " + position);
        View v = convertView;
        CheckBox cb = null;
        TextView tv = null;
        //tv = (TextView) v.findViewById(R.id.textView2);
        if(v == null) {
            if (position < 2) {
                v = inflater.inflate(R.layout.list_item, null);
                cb = (CheckBox) v.findViewById(R.id.checkBox);
                cb.setText("CheckBox" + position);
                v.setTag(cb);
            } else {
                v = inflater.inflate(R.layout.list_item2, null);
                tv = (TextView) v.findViewById(R.id.textView2);
                tv.setText("Label" + position);
                v.setTag(tv);
            }
        }
        return v;
    }
}
