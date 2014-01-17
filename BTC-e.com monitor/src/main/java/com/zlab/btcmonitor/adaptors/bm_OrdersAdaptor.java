package com.zlab.btcmonitor.adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor.bm_Main;
import com.zlab.btcmonitor.elements.bm_ListElementOrder;
import com.zlab.btcmonitor.elements.bm_ListElementsDepth;
import com.zlab.btcmonitor.workers.bm_ActiveOrders;
import com.zlab.btcmonitor.workers.bm_CancelOrders;
import com.zlab.btcmonitor.workers.bm_Depth;
import com.zlab.btcmonitor.workers.bm_Trade;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class bm_OrdersAdaptor extends ArrayAdapter<bm_ListElementOrder> {

    private Context c;
    private int id;
    private List<bm_ListElementOrder> items;

    TextView text_pair;
    TextView text_action;
    TextView text_amount;
    TextView text_price;
    TextView text_result;
    Button btn_cancel_order;

    public bm_OrdersAdaptor(Context context, int LayoutID, List<bm_ListElementOrder> objects) {
        super(context, LayoutID, objects);
        c = context;
        id = LayoutID;
        items = objects;
    }

    public bm_ListElementOrder getItem(int i)
    {
        return items.get(i);
    }

    public List getAllItems()
    {
        return items;
    }
    public void setItems(List<bm_ListElementOrder> newList){
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

        text_pair = (TextView) v.findViewById(R.id.textOrdersPair);
        text_action  = (TextView) v.findViewById(R.id.textOrdersAction);
        text_amount = (TextView) v.findViewById(R.id.textOrdersAmount);
        text_price = (TextView) v.findViewById(R.id.textOrdersPrice);
        text_result = (TextView) v.findViewById(R.id.textOrdersResult);
        btn_cancel_order = (Button) v.findViewById(R.id.btnCancelOrder);

        bm_ListElementOrder o = items.get(position);

        if (o != null) {

            String pair_ui="";
            for(int i=0;i < bm_Main.pairs_CODE.length;i++){
                if(o.getPair().equals("\""+bm_Main.pairs_CODE[i]+"\"")){
                    text_pair.setText(bm_Main.pairs_UI[i]);
                    pair_ui=bm_Main.pairs_UI[i];
                }
            }

            NumberFormat formatter = new DecimalFormat("#0.00000");
            NumberFormat formatter2 = new DecimalFormat("#0.00");

            String print_price="";
            String print_amount="";
            String print_result="";

            Double amount = Double.parseDouble(o.getAmount());
            print_amount = amount.toString();


            if(o.getAmount().indexOf(".")!=-1 && o.getAmount().length()>6){
                if(o.getAmount().substring(o.getAmount().indexOf(".")).length()>3){
                    if(amount<1){
                        print_amount=formatter.format(amount);
                    } else {
                        print_amount=formatter2.format(amount);
                    }
                    //print_amount=formatter.format(Double.parseDouble(o.getAmount()));
                } else {
                    print_amount=o.getAmount();
                }
            } else {
                print_amount=o.getAmount();
            }

            Double rate = Double.parseDouble(o.getRate());

            if(o.getRate().indexOf(".")!=-1 && o.getRate().length()>6){
                    if(o.getRate().substring(o.getRate().indexOf(".")).length()>3){
                        if(rate<1){
                            print_price=formatter.format(rate);
                        } else {
                            print_price=formatter2.format(rate);
                        }
                    } else {
                        print_price=o.getRate();
                    }
            } else {
                print_price=o.getRate();
            }

            Double result=Double.parseDouble(o.getAmount()) * Double.parseDouble(o.getRate());
            String result_str=result.toString();

            if(result_str.indexOf(".")!=-1 && result_str.length()>6){
                    if(result_str.substring(result_str.indexOf(".")).length()>3){
                        if(result<1){
                            print_result=formatter.format(result);
                        } else {
                            print_result=formatter2.format(result);
                        }
                    } else {
                        print_result=result_str;
                    }
            } else {
                print_result=result_str;
            }


            text_amount.setText(print_amount+" "+pair_ui.split(" / ")[0]);
            text_price.setText(print_price+" "+pair_ui.split(" / ")[1]);

            if(o.getType().toString().contains("s")){
                text_action.setText("↗");
                text_action.setTextColor(0xffb40e0e);
                text_result.setText(print_result+" "+pair_ui.split(" / ")[1]);
            } else {
                text_action.setText("↙");
                text_action.setTextColor(0xff16ab04);
                text_result.setText(print_result+" "+pair_ui.split(" / ")[1]);
            }

            final String order_id = o.getOrderID();
            final String pair = o.getPair();
            btn_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_Main.bm_MainContext);
                    action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.order_cancelation));
                    LayoutInflater inflater = bm_Main.bm_MainState.getLayoutInflater();
                    View layer = inflater.inflate(R.layout.blank,null);

                    TextView textOfCancel = (TextView) layer.findViewById(R.id.textAlertCancelTrade);

                    textOfCancel.setText(bm_Main.bm_MainState.getResources().getString(R.string.order_cancelation_string)+"-"+order_id+"?");

                    action_dialog.setView(layer);
                    action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });



                    action_dialog.setPositiveButton(bm_Main.bm_MainState.getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Thread thread = new Thread()
                                    {
                                        @Override
                                        public void run() {
                                            bm_CancelOrders.doCancelOrder(order_id,pair);
                                            //bm_ActiveOrders.getActiveOrders(o.getPair());
                                        }
                                    };
                                    thread.start();
                                    //bm_Main.refresh_pair_page();
                                }
                            });
                    AlertDialog AboutDialog = action_dialog.create();
                    AboutDialog.show();
                }
            });

            /** --- **/
            if(bm_Main.currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){
                text_amount.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_price.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                text_result.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
            }
        }

        return v;
    }
}
