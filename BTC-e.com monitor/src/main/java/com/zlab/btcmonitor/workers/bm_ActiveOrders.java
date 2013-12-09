package com.zlab.btcmonitor.workers;

import android.util.Log;
import android.widget.Toast;
import com.google.gson.*;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.btce_getActiveOrders;
import com.zlab.btcmonitor.adaptors.bm_DepthAdaptor;
import com.zlab.btcmonitor.adaptors.bm_OrdersAdaptor;
import com.zlab.btcmonitor.bm_Main;
import com.zlab.btcmonitor.elements.bm_ListElementOrder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class bm_ActiveOrders {
    public static void getActiveOrders(String pair){
        JsonObject activeOrders = btce_getActiveOrders.getActiveOrdersObj(pair);

        if(activeOrders!=null){
                boolean success = btce_getActiveOrders.getSuccess(activeOrders);
                //{"success":1,"return":{"68797198":{"pair":"ltc_rur","type":"buy","amount":1.00000000,"rate":200.00000000,"timestamp_created":1385420759,"status":0}}}

            //Log.e("ERSSSSSSSSSSSSSSS:",activeOrders.toString());
            //Log.e("ERSSSSSSSSSSSSSSS PAIRS:",pair);

                bm_Main.orderElements = new ArrayList<bm_ListElementOrder>();

                if(success){
                JsonObject ordersReturn = btce_getActiveOrders.getReturn(activeOrders);

                    JSONObject jo = null;
                    try {
                        jo = new JSONObject(ordersReturn.toString());
                    } catch (JSONException e) {
                        //Log.e("ERR", "MSG");
                    }

                    final int size = jo.length();

                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bm_Main.btn_Orders.setText(bm_Main.bm_MainState.getResources().getString(R.string.your_orders)+" ("+size+")");
                        }
                    });

                    //Log.e("EEEEEEEEEEEEEEEEEEE::: ", ordersReturn.get(0).getAsJsonObject().entrySet().entrySet().get("68797198").getAsJsonObject().get("pair").toString());

                    try {
                    for(int i=0;i<size;i++){
                        String key = jo.names().get(i).toString();
                        //JSONObject jo = new JSONObject(ordersReturn.get(i).toString());
                        //String key = jo.names().get(i).toString();

                        JsonElement jelement = new JsonParser().parse(jo.get(key).toString());
                        JsonObject getKeyElement = jelement.getAsJsonObject();

                        //Log.e("EEEEEEEEEEEE:",jo.length()+" "+key+" "+jo.get(key).toString());

                        //JsonObject getKeyElement = btce_getActiveOrders.getKeyElement(j,key);

                        bm_ListElementOrder eo = new bm_ListElementOrder(
                                btce_getActiveOrders.getPair(getKeyElement),
                                btce_getActiveOrders.getType(getKeyElement),
                                btce_getActiveOrders.getAmount(getKeyElement),
                                btce_getActiveOrders.getRate(getKeyElement),
                                btce_getActiveOrders.getTimestamp(getKeyElement),
                                btce_getActiveOrders.getStatus(getKeyElement),
                                key);

                        bm_Main.orderElements.add(eo);
                    }
                    } catch (JSONException e) {
                        //Log.e("ERR", "MSG");
                }

                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bm_Main.orderAdaptor==null){
                            bm_Main.orderAdaptor = new bm_OrdersAdaptor(bm_Main.bm_MainContext, R.layout.orders_list_item,bm_Main.orderElements);
                           // bm_Main.orderList.setAdapter(bm_Main.orderAdaptor);
                        } else {
                            bm_Main.orderAdaptor.setItems(bm_Main.orderElements);
                            bm_Main.orderAdaptor.notifyDataSetChanged();
                        }
                        bm_Main.btn_Orders.setEnabled(true);
                    }
                });
                } else {
                    // no orders

                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(bm_Main.bm_MainContext,"NO ORDERS.",Toast.LENGTH_SHORT).show();
                            bm_Main.btn_Orders.setEnabled(false);
                            bm_Main.btn_Orders.setText(bm_Main.bm_MainState.getResources().getString(R.string.your_orders)+" (0)");
                        }
                    });
                }
        } else {
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(bm_Main.bm_MainContext,"NULL ORDERS.",Toast.LENGTH_SHORT).show();
                    bm_Main.btn_Orders.setEnabled(false);
                    bm_Main.btn_Orders.setText(bm_Main.bm_MainState.getResources().getString(R.string.your_orders)+" (0)");
                }
            });
        }
        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
        if(bm_Main.orderList!=null){
        if(bm_Main.orderList.isShown()){bm_Main.orderList.setAdapter(bm_Main.orderAdaptor);}}
            }
        });
    }
    public static void getAllActiveOrders(){
        getActiveOrders(null);
    }
}
