package com.zlab.btcmonitor.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class bm_ChartsAdaptor extends ArrayAdapter<bm_ListElementCharts> {

    private Context c;
    private int id;
    private List<bm_ListElementCharts> items;

    TextView text_PairLeft;
    TextView text_PairRight;
    TextView text_Last;
    TextView text_Buy;
    TextView text_Sell;
    TextView text_Updated;
    TextView text_High;
    TextView text_Low;
    TextView text_BuyDiff;
    TextView text_SellDiff;
    ImageView img_buy;
    ImageView img_sell;
    ImageView img_vector;

    //RelativeLayout ll;

    public bm_ChartsAdaptor(Context context, int LayoutID, List<bm_ListElementCharts> objects) {
        super(context, LayoutID, objects);
        c = context;
        id = LayoutID;
        items = objects;
        //items = hide(objects);
    }

    public bm_ListElementCharts getItem(int i)
    {
        return items.get(i);
    }

    public List getAllItems()
    {
        return items;
    }
    /*
    public void setItems(List<bm_ListElementCharts> newList){
        items = newList;
        //items = hide(newList);
    }   */

    public static List<bm_ListElementCharts> hide(List<bm_ListElementCharts> objects){
        List<bm_ListElementCharts> newlist=new ArrayList<bm_ListElementCharts>();
        for(int i=0;i< VARs.pairs_CODE.length;i++){
            if(bm_Main.chartsEnabled[i]){
                newlist.add(objects.get(i));
            }
        }
        return newlist;
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

            if(bm_Main.prefs_charts_classic){
                text_PairLeft.setText(o.getPair());
                text_PairRight.setText("");
                //text_PairRight.setWidth(0);
                //text_PairRight.setVisibility(View.GONE);
            } else {
                text_PairLeft.setText(o.getPair().split(" / ")[0]);
                text_PairRight.setText(o.getPair().split(" / ")[1]);
            }

            if(bm_Main.THEME==android.R.style.Theme_Holo){
                text_PairLeft.setTextColor(0xFFE7E5E4);
                text_PairRight.setTextColor(0xFFE7E5E4);
                text_Last.setTextColor(0xFFE7E5E4);
            } else {
                text_PairLeft.setTextColor(0xFF0E0D0B);
                text_PairRight.setTextColor(0xFF0E0D0B);
                text_Last.setTextColor(0xFF0E0D0B);
            }


            NumberFormat formatter2 = new DecimalFormat("#0.00");
            Double last = Double.parseDouble(o.getLast());

            if(o.getLast().indexOf(".")!=-1 && o.getLast().length()>6){
                if(o.getLast().substring(0,o.getLast().indexOf(".")).length()>3){
                    text_Last.setText(formatter2.format(last).replace(",","."));
                } else {
                    text_Last.setText(o.getLast());
                }
            } else {
                text_Last.setText(o.getLast());
            }

            //text_Last.setText(o.getLast());


            text_Buy.setText(o.getBuy());
            text_Sell.setText(o.getSell());
            //text_Updated.setText(o.getUpdated());
            //text_High.setText(o.getHigh());
            //text_Low.setText(o.getLow());
            if(!bm_Main.prefs_charts_detailed || o.getLast().equals("") || o.getLast().equals("0.00") ){
                text_Buy.setVisibility(View.INVISIBLE);
                text_Sell.setVisibility(View.INVISIBLE);
                img_buy.setVisibility(View.INVISIBLE);
                img_sell.setVisibility(View.INVISIBLE);
                text_SellDiff.setVisibility(View.INVISIBLE);
                text_BuyDiff.setVisibility(View.INVISIBLE);
            } else {
                text_Buy.setVisibility(View.VISIBLE);
                text_Sell.setVisibility(View.VISIBLE);
                img_buy.setVisibility(View.VISIBLE);
                img_sell.setVisibility(View.VISIBLE);
                text_SellDiff.setVisibility(View.VISIBLE);
                text_BuyDiff.setVisibility(View.VISIBLE);
            }
            /** --- **/
            if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
                text_PairLeft.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_PairRight.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
                text_Last.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
                text_Buy.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
                text_Sell.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_SellDiff.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_BuyDiff.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
            }

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int dps = 115;
            if(!bm_Main.prefs_charts_classic){
                dps=65;
            }
            int pixels = (int) (dps * scale + 0.5f);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)img_buy.getLayoutParams();
            params.setMargins(pixels, 0, 10, 0); //substitute parameters for left, top, right, bottom
            img_buy.setLayoutParams(params);

            if(bm_Main.prefs_show_vector){
                img_vector.setVisibility(View.VISIBLE);
                if(!o.getDiffLast().equals("") || !o.getDiffLast().equals("0.00")){
                    Double diff = 0.00;
                    Double diffSell = 0.00;
                    Double diffBuy = 0.00;

                    try{
                        diff = Double.parseDouble(o.getDiffLast());
                        diffSell = Double.parseDouble(o.getDiffSell());
                        diffBuy = Double.parseDouble(o.getDiffBuy());
                    } catch (IndexOutOfBoundsException e){
                    }

                    NumberFormat formatter = new DecimalFormat("#0.00000");

                    if(bm_Main.REFRESH_COUNTER<2){
                        diff=0.0000;diffSell=0.0000;diffBuy=0.0000;
                        //img_vector.setVisibility(View.INVISIBLE);
                    } else {
                        text_BuyDiff.setText(formatter.format(diffBuy));
                        text_SellDiff.setText(formatter.format(diffSell));
                    }

                    if(diff>0){
                        img_vector.setImageResource(R.drawable.up_arrow);
                    } else if(diff<0){
                        img_vector.setImageResource(R.drawable.down_arrow);
                    } else {
                        img_vector.setImageResource(R.drawable.none_arrow);
                    }

                    if(diffSell>0){
                        text_SellDiff.setTextColor(0xff16ab04);
                        if(!text_SellDiff.getText().toString().equals("")){text_SellDiff.setText("+"+text_SellDiff.getText().toString());}
                    } else if(diff<0){
                        text_SellDiff.setTextColor(0xffb40e0e);
                    } else {
                        text_SellDiff.setTextColor(Color.GRAY);
                        text_SellDiff.setText("");
                    }

                    if(diffBuy>0){
                        text_BuyDiff.setTextColor(0xff16ab04);
                        if(!text_BuyDiff.getText().toString().equals("")){text_BuyDiff.setText("+"+text_BuyDiff.getText().toString());}
                    } else if(diff<0){
                        text_BuyDiff.setTextColor(0xffb40e0e);
                    } else {
                        text_BuyDiff.setTextColor(Color.GRAY);
                        text_BuyDiff.setText("");
                    }

                } else {
                    img_vector.setImageResource(R.drawable.none_arrow);
                }
            } else {
                img_vector.setVisibility(View.GONE);
            }
            //imgVector

            /*
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String pair = o.getPair();
                    final int pos = position;
                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(bm_Main.bm_MainContext, pair+" - "+ bm_Main.chartsListDiff.get(pos), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            */
        }


        return v;
    }

}
