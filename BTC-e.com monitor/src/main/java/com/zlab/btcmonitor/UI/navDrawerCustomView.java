package com.zlab.btcmonitor.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zlab.btcmonitor.bm_Main;

public class navDrawerCustomView extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public navDrawerCustomView(Context context, String[] values) {
        super(context, android.R.layout.simple_list_item_activated_1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);

        // Customization to your textView here
        textView.setText(values[position]);
        if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        }

        //textView.setTextSize(20);

        return rowView;
    }
}