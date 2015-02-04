package com.zlab.btcmonitor.workers;

import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getDepth;
import com.zlab.btcmonitor.adaptors.bm_DepthAdaptor;
import com.zlab.btcmonitor.elements.bm_ListElementsDepth;
import com.zlab.btcmonitor.bm_Main;

import java.util.ArrayList;

public class bm_Depth {
    public static void update_depth(String pair){

        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(true);
            }
        });
        int PAIR_CODE=-1;
        for(int i=0;i<VARs.pairs_CODE.length;i++)
        {if(VARs.pairs_CODE[i].equals(pair)){
            PAIR_CODE=i;
        }}

        if(bm_Main.pairAskElements[PAIR_CODE]!=null && bm_Main.pairBidsElements[PAIR_CODE]!=null){
            if(bm_Main.pairAskElements[PAIR_CODE].size()!=0 && bm_Main.pairBidsElements[PAIR_CODE].size()!=0){
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                bm_Main.btn_Buy.setEnabled(true);
                bm_Main.btn_Sell.setEnabled(true);
                bm_Main.btn_History.setEnabled(true);
                    }});
            } else {
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                bm_Main.btn_Buy.setEnabled(false);
                bm_Main.btn_Sell.setEnabled(false);
                bm_Main.btn_History.setEnabled(false);}});
            }
        } else {
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
            bm_Main.btn_Buy.setEnabled(false);
            bm_Main.btn_Sell.setEnabled(false);
            bm_Main.btn_History.setEnabled(false);}});
        }

        JsonObject depthObj = btce_getDepth.getDepthObj(pair);

        bm_Main.pairAskElements[PAIR_CODE] = new ArrayList <bm_ListElementsDepth>();
        bm_Main.pairBidsElements[PAIR_CODE] = new ArrayList <bm_ListElementsDepth>();

        if(depthObj!=null){
            JsonArray depthAsk = btce_getDepth.getDepthAsk(depthObj);
            JsonArray depthBids = btce_getDepth.getDepthBids(depthObj);

            if(depthAsk!=null) {
                for (int i = 0; i < depthAsk.size(); i++) {
                    bm_ListElementsDepth el = new bm_ListElementsDepth(
                            depthAsk.get(i).getAsJsonArray().get(0).getAsString(),
                            depthAsk.get(i).getAsJsonArray().get(1).getAsString()
                    );
                    bm_Main.pairAskElements[PAIR_CODE].add(el);
                }
                for (int i = 0; i < depthBids.size(); i++) {
                    bm_ListElementsDepth el = new bm_ListElementsDepth(
                            depthBids.get(i).getAsJsonArray().get(0).getAsString(),
                            depthBids.get(i).getAsJsonArray().get(1).getAsString()
                    );
                    bm_Main.pairBidsElements[PAIR_CODE].add(el);
                }
                final int PAIR_CODE_F = PAIR_CODE;
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bm_Main.pairAskAdaptor[PAIR_CODE_F] == null) {
                            bm_Main.pairAskAdaptor[PAIR_CODE_F] = new bm_DepthAdaptor(bm_Main.bm_MainContext, R.layout.depth_list_item, bm_Main.pairAskElements[PAIR_CODE_F]);
                            bm_Main.pairAskList.setAdapter(bm_Main.pairAskAdaptor[PAIR_CODE_F]);
                        } else {
                            bm_Main.pairAskAdaptor[PAIR_CODE_F].setItems(bm_Main.pairAskElements[PAIR_CODE_F]);
                            bm_Main.pairAskAdaptor[PAIR_CODE_F].notifyDataSetChanged();
                        }
                        if (bm_Main.pairBidsAdaptor[PAIR_CODE_F] == null) {
                            bm_Main.pairBidsAdaptor[PAIR_CODE_F] = new bm_DepthAdaptor(bm_Main.bm_MainContext, R.layout.depth_list_item, bm_Main.pairBidsElements[PAIR_CODE_F]);
                            bm_Main.pairBidsList.setAdapter(bm_Main.pairBidsAdaptor[PAIR_CODE_F]);
                        } else {
                            bm_Main.pairBidsAdaptor[PAIR_CODE_F].setItems(bm_Main.pairBidsElements[PAIR_CODE_F]);
                            bm_Main.pairBidsAdaptor[PAIR_CODE_F].notifyDataSetChanged();
                        }
                        bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
                        bm_Main.btn_Buy.setEnabled(true);
                        bm_Main.btn_Sell.setEnabled(true);
                    }
                });
            }
        } else {
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bm_Main.bm_MainContext, "Нет связи с сервером биржи.", Toast.LENGTH_SHORT).show();
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
                }
            });
        }


    }
}
