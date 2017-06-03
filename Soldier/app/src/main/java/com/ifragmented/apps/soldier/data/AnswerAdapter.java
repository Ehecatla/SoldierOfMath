package com.ifragmented.apps.soldier.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ifragmented.apps.soldier.R;

import java.util.ArrayList;

/**
 * Created by Ella on 2017-01-21.
 */

public class AnswerAdapter extends BaseAdapter {

    private ArrayList<Answer> dataSource;
    private LayoutInflater layoutInflater;

    public AnswerAdapter(Context context, ArrayList<Answer> dataSource){
        this.dataSource = dataSource;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return dataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;
        if(view == null) {
            v = layoutInflater.inflate(R.layout.answer_row, viewGroup, false);
        } else {
            v = view;
        }
        TextView tv = (TextView)v.findViewById(R.id.answer_row_tv);
        tv.setText(dataSource.get(i).getAnswerText());
        return v;
    }
}
