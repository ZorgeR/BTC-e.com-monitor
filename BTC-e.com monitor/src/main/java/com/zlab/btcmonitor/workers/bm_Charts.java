package com.zlab.btcmonitor.workers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getTicker;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class bm_Charts {
    //bm_Office.updateInfo();
    public static void update_charts(){

        if(!bm_Main.chartsBlocked){
            bm_Main.REFRESH_COUNTER++;

            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(true);
                    /*
                    if (navDrawer.mCallbacks != null) {
                        navDrawer.mCallbacks.onNavigationDrawerItemSelected(0);
                    } */
                                        /*
                                        if (navDrawer.mCallbacks != null) {
                                            navDrawer.mCallbacks.onNavigationDrawerItemSelected(0);
                                        }
                                        */
                }
            });

                         /*
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bm_Main.bm_MainContext, "Обновляю данные...", Toast.LENGTH_SHORT).show();
                }
            });    */
            bm_Main.chartsBlocked=true;

            String[] pairs_code = VARs.pairs_CODE;
            String[] pairs_UI = VARs.pairs_UI;

            JsonObject Ticker;

            bm_Main.chartsListElementsOld = new ArrayList<bm_ListElementCharts>(bm_Main.chartsListElements);

            //bm_Main.chartsListElements = new ArrayList<bm_ListElementCharts>();

            if(bm_Main.chartsListDiff==null){
                bm_Main.chartsListDiff=new ArrayList<String>();
                bm_Main.chartsListDiffSell=new ArrayList<String>();
                bm_Main.chartsListDiffBuy=new ArrayList<String>();

                for (int i = 0; i < pairs_UI.length; i++) {
                    bm_Main.chartsListDiff.add("0.00");
                    bm_Main.chartsListDiffSell.add("0.00");
                    bm_Main.chartsListDiffBuy.add("0.00");
                }
            }

            for(int i=0;i<pairs_code.length;i++){
                Ticker = btce_getTicker.getTickerObj(pairs_code[i]);
                final String pair = pairs_UI[i];
                //Log.e("JSON: >>> ", Ticker.toString());
                //String pair, String last, String buy, String sell, String updated, String high, String low
                if(Ticker!=null){
                bm_ListElementCharts el = new bm_ListElementCharts(pairs_UI[i],
                        btce_getTicker.get_last(Ticker),
                        btce_getTicker.get_buy(Ticker),
                        btce_getTicker.get_sell(Ticker),
                        btce_getTicker.get_updated(Ticker),
                        btce_getTicker.get_high(Ticker),
                        btce_getTicker.get_low(Ticker));
                //Log.e("LAST: ",el.getLast());
                bm_Main.chartsListElements.remove(i);
                bm_Main.chartsListElements.add(i,el);

                    bm_Main.txtLast[i] = btce_getTicker.get_last(Ticker);
                    bm_Main.txtLow[i] = btce_getTicker.get_low(Ticker);
                    bm_Main.txtHigh[i] = btce_getTicker.get_high(Ticker);

                    if(!bm_Main.chartsListElementsOld.get(i).getLast().equals("0.00")){
                        bm_Main.chartsListDiff.remove(i);
                        bm_Main.chartsListDiff.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getLast())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getLast())));

                        bm_Main.chartsListDiffSell.remove(i);
                        bm_Main.chartsListDiffSell.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getSell())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getSell())));

                        bm_Main.chartsListDiffBuy.remove(i);
                        bm_Main.chartsListDiffBuy.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getBuy())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getBuy())));
                    }
                    //Log.e("chartsListElements." + i, bm_Main.chartsListElements.get(i).getLast());
                    //Log.e("chartsListElementsOld."+i,bm_Main.chartsListElementsOld.get(i).getLast());
                    //Log.e("chartsListDiffSell."+i,bm_Main.chartsListDiffSell.get(i));
                    //Log.e("chartsListDiffBuy."+i,bm_Main.chartsListDiffBuy.get(i));

                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bm_Main.chartsAdaptor.setItems(bm_Main.chartsListElements);
                            bm_Main.chartsAdaptor.notifyDataSetChanged();
                        }
                    });
                } else {
                    //bm_Main.chartsListElements.remove(i);
                    //bm_Main.chartsListElements.add(i,bm_Main.chartsListElementsOld.get(i));
                    bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.cant_retrieve)+": "+pair,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //btce_getTicker.get_avg(Ticker);
                //btce_getTicker.get_server_time(Ticker);
                //btce_getTicker.get_vol(Ticker);
                //btce_getTicker.get_vol_cur(Ticker);
            }

            //if(bm_Main.chartsListElements.size()==0){bm_Main.chartsListElements=bm_Main.chartsListElementsOld;}

            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.chartsAdaptor.setItems(bm_Main.chartsListElements);
                    bm_Main.chartsAdaptor.notifyDataSetChanged();
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
                    /*
                    if (navDrawer.mCallbacks != null) {
                        navDrawer.mCallbacks.onNavigationDrawerItemSelected(0);
                    } */
                                        /*
                                        if (navDrawer.mCallbacks != null) {
                                            navDrawer.mCallbacks.onNavigationDrawerItemSelected(0);
                                        }
                                        */
                }
            });
            bm_Main.chartsBlocked=false;
        } else {
            /* bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bm_Main.bm_MainContext,"Уже работаю! Ждите!",Toast.LENGTH_LONG).show();
                }
            });  */
        }
    }
}
