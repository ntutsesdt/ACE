package tw.edu.ntut.sdtlab.crawler.testapp.testtab;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabActivity2 extends Fragment {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);
    }
*/
    private static final String ARG_SECTION_NUMBER = "section_number";

    public TabActivity2() {
    }

    public static TabActivity2 newInstance(int sectionNumber) {
        TabActivity2 fragment = new TabActivity2();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(tw.edu.ntut.sdtlab.crawler.testapp.testtab.R.layout.activity_tab2, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
