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
 * Created by Ella on 2017-02-05. Used to display scoreboard with user results.
 */

public class ScoreboardAdapter extends BaseAdapter {


    private final ArrayList<Score> dataSource;
    private final LayoutInflater layoutInflater;

    public ScoreboardAdapter(Context context, ArrayList<Score> dataSource){
        this.dataSource = dataSource;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if(convertView == null) {
            v = layoutInflater.inflate(R.layout.score_row, parent, false);
        } else {
            v = convertView;
        }
        TextView tv = (TextView)v.findViewById(R.id.score_id);
        TextView tv2 = (TextView)v.findViewById(R.id.score_user);
        TextView tv3 = (TextView)v.findViewById(R.id.score_story);
        TextView tv4 = (TextView)v.findViewById(R.id.score_score);
        String id = String.valueOf(position +1);
        tv.setText(id); //highscore begins always from first position, not 0
        tv2.setText(dataSource.get(position).getUserName());
        tv3.setText(dataSource.get(position).getStoryTitle());
       tv4.setText(String.valueOf(dataSource.get(position).getPoints()));
        return v;
    }
}
