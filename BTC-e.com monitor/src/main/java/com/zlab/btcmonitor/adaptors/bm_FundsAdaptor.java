package com.zlab.btcmonitor.adaptors;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;

import java.util.List;

public class bm_FundsAdaptor extends ArrayAdapter<bm_ListElementCharts> {

    private Context c;
    private int id;
    private List<bm_ListElementCharts> items;

    TextView text_PairLeft;
    TextView text_PairRight;
    TextView text_Last;
    TextView text_Buy;
    TextView text_Sell;
    TextView text_BuyDiff;
    TextView text_SellDiff;
    TextView text_Updated;
    TextView text_High;
    TextView text_Low;
    ImageView img_buy;
    ImageView img_sell;
    ImageView img_vector;

    //RelativeLayout ll;

    public bm_FundsAdaptor(Context context, int LayoutID, List<bm_ListElementCharts> objects) {
        super(context, LayoutID, objects);
        c = context;
        id = LayoutID;
        items = objects;
    }

    public bm_ListElementCharts getItem(int i)
    {
        return items.get(i);
    }

    public List getAllItems()
    {
        return items;
    }

    @Override
    public View getView(final int position, View view,final ViewGroup parent) {
        View v = view;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }
        //ll = (RelativeLayout) v.findViewById(R.id.);

        bm_ListElementCharts o = items.get(position);

        if (o != null) {

            text_PairLeft = (TextView) v.findViewById(R.id.textPairLeft);
            text_PairRight = (TextView) v.findViewById(R.id.textPairRight);
            text_Last = (TextView) v.findViewById(R.id.textLast);
            text_Buy = (TextView) v.findViewById(R.id.textBuy);
            text_Sell = (TextView) v.findViewById(R.id.textSell);
            text_BuyDiff = (TextView) v.findViewById(R.id.textBuyDiff);
            text_SellDiff = (TextView) v.findViewById(R.id.textSellDiff);
            //text_Updated = (TextView) v.findViewById(R.id.textUpdated);
            //text_High = (TextView) v.findViewById(R.id.textHigh);
            //text_Low = (TextView) v.findViewById(R.id.textLow);
            img_buy = (ImageView) v.findViewById(R.id.imgBuy);
            img_sell = (ImageView) v.findViewById(R.id.imgSell);
            img_vector = (ImageView) v.findViewById(R.id.imgVector);

            if(bm_Main.THEME==android.R.style.Theme_Holo){
                text_PairLeft.setTextColor(0xFFE7E5E4);
                text_PairRight.setTextColor(0xFFE7E5E4);
                text_Last.setTextColor(0xFFE7E5E4);
            } else {
                text_PairLeft.setTextColor(0xFF0E0D0B);
                text_PairRight.setTextColor(0xFF0E0D0B);
                text_Last.setTextColor(0xFF0E0D0B);
            }

            text_PairLeft.setText(o.getPair());
            text_PairRight.setText("");

            text_Last.setText(o.getLast());
            //text_Buy.setText(o.getBuy());
            //text_Sell.setText(o.getSell());
            //text_Updated.setText(o.getUpdated());
            //text_High.setText(o.getHigh());
            //text_Low.setText(o.getLow());

            text_Buy.setVisibility(View.GONE);
            text_Sell.setVisibility(View.GONE);
            text_BuyDiff.setVisibility(View.GONE);
            text_SellDiff.setVisibility(View.GONE);

            img_buy.setVisibility(View.GONE);
            img_sell.setVisibility(View.GONE);
            img_vector.setVisibility(View.GONE);
            /** --- **/
            if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            text_PairLeft.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            //text_PairRight.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            text_Last.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            //text_Buy.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            //text_Sell.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
            }
        }

        return v;
    }

}
