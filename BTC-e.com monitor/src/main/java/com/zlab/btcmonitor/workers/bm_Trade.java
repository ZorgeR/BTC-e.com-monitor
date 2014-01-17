package com.zlab.btcmonitor.workers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_doTrade;
import com.zlab.btcmonitor.bm_Main;

public class bm_Trade {
    public static void doTrade(final String pair, final String type, final String rate, final String amount){
        JsonObject doTrade = btce_doTrade.doTrade(pair, type, rate, amount);

        ///Log.e("EEEEEEEEEEEE:", doTrade.toString());

        final boolean success = btce_doTrade.getSuccess(doTrade);
        if (success){
        JsonObject doTradeReturn = btce_doTrade.getReturn(doTrade);


        final String Received = btce_doTrade.getReceived(doTradeReturn);
        final String Remain = btce_doTrade.getRemain(doTradeReturn);
        final String OrderID = btce_doTrade.getOrderID(doTradeReturn);
        String USD = btce_doTrade.getFunds(doTradeReturn, "USD");

        bm_Depth.update_depth(pair);
        bm_ActiveOrders.getActiveOrders(pair);

        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_Main.bm_MainContext);
                if(type.equals("sell")){action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.sell));}else{action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.buy));}
                LayoutInflater inflater = bm_Main.bm_MainState.getLayoutInflater();
                View layer = inflater.inflate(R.layout.trade_result,null);

                TextView textTradeResultSuccess = (TextView) layer.findViewById(R.id.textTradeResultSuccess);
                TextView textTradeResultAmount = (TextView) layer.findViewById(R.id.textTradeResultAmount);
                TextView textTradeResultPrice = (TextView) layer.findViewById(R.id.textTradeResultPrice);
                TextView textTradeResultReceived = (TextView) layer.findViewById(R.id.textTradeResultReceived);
                TextView textTradeResultRemain = (TextView) layer.findViewById(R.id.textTradeResultRemain);
                TextView textTradeResultOrderID = (TextView) layer.findViewById(R.id.textTradeResultOrderID);

                String cur ="";
                for(int i=0;i< bm_Main.pairs_UI.length;i++)
                {if(bm_Main.pairs_CODE[i].equals(pair)){
                    cur=bm_Main.pairs_UI[i].split(" / ")[0];
                }}

                textTradeResultSuccess.setText(bm_Main.bm_MainState.getResources().getString(R.string.result_success));
                if(type.equals("sell")){textTradeResultAmount.setText(bm_Main.bm_MainState.getResources().getString(R.string.you_sell)+" "+cur+": " + amount);}else{textTradeResultAmount.setText(bm_Main.bm_MainState.getResources().getString(R.string.you_buy)+" "+cur+": " + amount);}
                textTradeResultPrice.setText(bm_Main.bm_MainState.getResources().getString(R.string.price_for)+": "+rate);
                if(Received.equals("0")){
                    textTradeResultReceived.setText(bm_Main.bm_MainState.getResources().getString(R.string.receive)+": "+bm_Main.bm_MainState.getResources().getString(R.string.new_order));
                }else{
                    textTradeResultReceived.setText(bm_Main.bm_MainState.getResources().getString(R.string.receive)+": "+Received);
                }
                textTradeResultRemain.setText(bm_Main.bm_MainState.getResources().getString(R.string.still)+": "+Remain);
                textTradeResultOrderID.setText(bm_Main.bm_MainState.getResources().getString(R.string.transaction_id)+": "+OrderID);

                action_dialog.setView(layer);
                action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog AboutDialog = action_dialog.create();
                AboutDialog.show();
            }
        });
        } else {
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
            AlertDialog.Builder action_dialog = new AlertDialog.Builder(bm_Main.bm_MainContext);
            if(type.equals("sell")){action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.sell));}else{action_dialog.setTitle(bm_Main.bm_MainState.getResources().getString(R.string.buy));}
            LayoutInflater inflater = bm_Main.bm_MainState.getLayoutInflater();
            View layer = inflater.inflate(R.layout.trade_result,null);

            TextView textTradeResultSuccess = (TextView) layer.findViewById(R.id.textTradeResultSuccess);
            TextView textTradeResultAmount = (TextView) layer.findViewById(R.id.textTradeResultAmount);
            TextView textTradeResultPrice = (TextView) layer.findViewById(R.id.textTradeResultPrice);
            TextView textTradeResultReceived = (TextView) layer.findViewById(R.id.textTradeResultReceived);
            TextView textTradeResultRemain = (TextView) layer.findViewById(R.id.textTradeResultRemain);
            TextView textTradeResultOrderID = (TextView) layer.findViewById(R.id.textTradeResultOrderID);

            textTradeResultSuccess.setText(bm_Main.bm_MainState.getResources().getString(R.string.low_balance_nopermission));
            textTradeResultAmount.setVisibility(View.GONE);
            textTradeResultPrice.setVisibility(View.GONE);
            textTradeResultReceived.setVisibility(View.GONE);
            textTradeResultRemain.setVisibility(View.GONE);
            textTradeResultOrderID.setVisibility(View.GONE);

            action_dialog.setView(layer);
            action_dialog.setNegativeButton(bm_Main.bm_MainState.getResources().getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog AboutDialog = action_dialog.create();
            AboutDialog.show();}
                });
        }
    }
}
