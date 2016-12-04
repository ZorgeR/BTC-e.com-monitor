package com.zlab.btcmonitor.workers;

import android.widget.Toast;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.btce_cancelOrder;
import com.zlab.btcmonitor.bm_Main;

public class bm_CancelOrders {
    public static void doCancelOrder(final String order_id,String pair_code){
        JsonObject cancelOrderJson = btce_cancelOrder.doCancelOrder(order_id);
        boolean success = btce_cancelOrder.getSuccess(cancelOrderJson);
        if(success){
            //bm_Main.orderAdaptor=null;

            /*
            for(int i=0;i< bm_Main.orderList.getCount();i++){
                //if(bm_Main.orderAdaptor.getCount()<2){
                //    bm_Main.orderAdaptor = new bm_OrdersAdaptor(bm_Main.bm_MainState, R.layout.orders_list_item,bm_Main.orderElements);
                //} else {
                    if(bm_Main.orderAdaptor.getItem(i).getOrderID().equals(order_id)){
                        bm_Main.orderAdaptor.remove(bm_Main.orderAdaptor.getItem(i));
                        //bm_Main.orderElements.remove(i);
                        break;
                    }
                //}
            }   */

            //bm_Main.orderAdaptor.setItems(bm_Main.orderElements);

            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //bm_Main.orderAdaptor.notifyDataSetChanged();
                    Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.order)+" "+order_id+" "+bm_Main.bm_MainState.getResources().getString(R.string.closed),Toast.LENGTH_SHORT).show();
                    for(int i=0;i< bm_Main.orderAdaptor.getCount();i++){
                        //if(bm_Main.orderAdaptor.getCount()<2){
                        //    bm_Main.orderAdaptor = new bm_OrdersAdaptor(bm_Main.bm_MainState, R.layout.orders_list_item,bm_Main.orderElements);
                        //} else {
                        if(bm_Main.orderAdaptor.getItem(i).getOrderID().equals(order_id)){
                            bm_Main.orderAdaptor.remove(bm_Main.orderAdaptor.getItem(i));
                            //bm_Main.orderElements.remove(i);
                            break;
                        }
                        //}
                    }
                    bm_Main.btn_Orders.setText(bm_Main.bm_MainState.getResources().getString(R.string.your_orders)+" ("+bm_Main.orderAdaptor.getCount()+")");
                    if(bm_Main.orderAdaptor.getCount()==0){bm_Main.btn_Orders.setEnabled(false);
                    } else {
                        bm_Main.btn_Orders.setEnabled(true);
                    }
                }
            });

        } else {
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.order_error)+" "+order_id+" "+bm_Main.bm_MainState.getResources().getString(R.string.order_not_closed),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
