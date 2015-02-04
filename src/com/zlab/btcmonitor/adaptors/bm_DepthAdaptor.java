package com.zlab.btcmonitor.adaptors;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor.elements.bm_ListElementsDepth;
import com.zlab.btcmonitor.bm_Main;

import java.util.List;

public class bm_DepthAdaptor extends ArrayAdapter<bm_ListElementsDepth> {

    private Context c;
    private int id;
    private List<bm_ListElementsDepth> items;

    TextView text_Price;
    TextView text_Amount;

    public bm_DepthAdaptor(Context context, int LayoutID, List<bm_ListElementsDepth> objects) {
        super(context, LayoutID, objects);
        c = context;
        id = LayoutID;
        items = objects;
    }

    public bm_ListElementsDepth getItem(int i)
    {
        return items.get(i);
    }

    public List getAllItems()
    {
        return items;
    }
    public void setItems(List<bm_ListElementsDepth> newList){
        items = newList;
    }

    @Override
    public View getView(final int position, View view,final ViewGroup parent) {
        //items=bm_Main.chartsListElements;

        View v = view;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }
        //ll = (RelativeLayout) v.findViewById(R.id.);

        bm_ListElementsDepth o = items.get(position);

        if (o != null) {

            text_Price = (TextView) v.findViewById(R.id.textDepthPrice);
            text_Amount = (TextView) v.findViewById(R.id.textDepthAmount);

            text_Price.setText(o.getPrice());
            text_Amount.setText(o.getAmount());

            if(bm_Main.THEME==android.R.style.Theme_Holo){
                text_Price.setTextColor(0xFFE7E5E4);
                text_Amount.setTextColor(0xFFE7E5E4);
            } else {
                text_Price.setTextColor(0xFF0E0D0B);
                text_Amount.setTextColor(0xFF0E0D0B);
            }

            /** --- **/
            if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
                text_Price.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_Amount.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            }
        }

        return v;
    }
}
