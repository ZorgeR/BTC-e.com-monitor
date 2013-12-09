package com.zlab.btcmonitor.adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor.bm_Main;
import com.zlab.btcmonitor.elements.bm_ListElementOrder;
import com.zlab.btcmonitor.elements.bm_ListElementsHistory;
import com.zlab.btcmonitor.workers.bm_CancelOrders;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class bm_HistoryAdaptor extends ArrayAdapter<bm_ListElementsHistory> {

    private Context c;
    private int id;
    private List<bm_ListElementsHistory> items;

    //bm_ListElementsHistory(String date, String price, String amount, String tid, String pricecur, String itemcur, String type)
    TextView text_date;
    TextView text_price;
    TextView text_amount;
    TextView text_tid;
    TextView text_pricecur;
    TextView text_itemcur;
    TextView text_type;
    TextView text_result;
    //Button btn_cancel_order;

    public bm_HistoryAdaptor(Context context, int LayoutID, List<bm_ListElementsHistory> objects) {
        super(context, LayoutID, objects);
        c = context;
        id = LayoutID;
        items = objects;
    }

    public bm_ListElementsHistory getItem(int i)
    {
        return items.get(i);
    }

    public List getAllItems()
    {
        return items;
    }
    public void setItems(List<bm_ListElementsHistory> newList){
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

        //text_date = (TextView) v.findViewById(R.id.textHistoryDate);
        text_price  = (TextView) v.findViewById(R.id.textHistoryPrice);
        text_amount = (TextView) v.findViewById(R.id.textHistoryAmount);
        //text_tid = (TextView) v.findViewById(R.id.textHistoryTid);
        //text_pricecur = (TextView) v.findViewById(R.id.textHistoryPriceCur);
        //text_itemcur = (TextView) v.findViewById(R.id.textHistoryItemCur);
        text_type = (TextView) v.findViewById(R.id.textHistoryType);
        text_result = (TextView) v.findViewById(R.id.textHistoryResult);

        //btn_cancel_order = (Button) v.findViewById(R.id.btnCancelOrder);

        bm_ListElementsHistory o = items.get(position);

        if (o != null) {

            NumberFormat formatter = new DecimalFormat("#0.00");

            if(o.getType().toString().contains("s")){
                text_type.setText("↗");
                text_type.setTextColor(0xffb40e0e);
                text_result.setText(String.valueOf(formatter.format(Double.parseDouble(o.getAmount()) * Double.parseDouble(o.getPrice())))+" "+o.getPriceCur());
            } else {
                text_type.setText("↙");
                text_type.setTextColor(0xff16ab04);
                text_result.setText(String.valueOf(formatter.format(Double.parseDouble(o.getAmount()) * Double.parseDouble(o.getPrice())))+" "+o.getPriceCur());
            }

            text_price.setText(o.getPrice()+" "+o.getPriceCur());

            text_amount.setText(o.getAmount()+" "+o.getItemCur());

            /** --- **/
            if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
                text_price.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_amount.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_result.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
            }
        }

        return v;
    }
}
