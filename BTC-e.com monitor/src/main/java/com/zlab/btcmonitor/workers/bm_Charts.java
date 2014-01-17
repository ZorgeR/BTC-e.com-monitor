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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

            String[] pairs_code = bm_Main.pairs_CODE;
            String[] pairs_UI = bm_Main.pairs_UI;

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

            /*
            if(bm_Main.chartsListElements.size()!=pairs_code.length){
                // Падение
                //Если меньше, добавить строки
                //Если больше, убрать
            }   */

            while(bm_Main.chartsListElements.size()>pairs_code.length){
                bm_Main.chartsListElements.remove(bm_Main.chartsListElements.size()-1);
                bm_Main.chartsListElementsOld.remove(bm_Main.chartsListElementsOld.size()-1);
            }


            while(bm_Main.chartsListElements.size()<pairs_code.length){
                FileInputStream fis = null;
                int i=bm_Main.chartsListElements.size();

                try {
                    fis = bm_Main.bm_MainState.openFileInput("charts_"+bm_Main.pairs_CODE[i]+".json");

                    StringBuffer fileContent = new StringBuffer("");
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) != -1) {
                        fileContent.append(new String(buffer));
                    }
                    String[] chartsElementsArray=fileContent.toString().split("&");

                    bm_Main.chartsListElements.add(i,new bm_ListElementCharts(
                            chartsElementsArray[0],
                            chartsElementsArray[1],
                            chartsElementsArray[2],
                            chartsElementsArray[3],
                            chartsElementsArray[4],
                            chartsElementsArray[5],
                            chartsElementsArray[6],
                            chartsElementsArray[7]));
                } catch (Exception e) {
                //Log.e("ERR", "MSG");
                    bm_Main.chartsListElements.add(new bm_ListElementCharts(bm_Main.pairs_UI[i],bm_Main.pairs_CODE[i],"0.00","0.00","0.00","","",""));
                    bm_Main.chartsListElementsOld.add(new bm_ListElementCharts(bm_Main.pairs_UI[i],bm_Main.pairs_CODE[i],"0.00","0.00","0.00","","",""));
                }
            }

            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.chartsAdaptor.notifyDataSetInvalidated();
                }});

            String fail_list="";
                               /*
            List<bm_ListElementCharts> newChartsList = new ArrayList<bm_ListElementCharts>();

            for(int j=0;j<bm_Main.chartsListElements.size();j++){
                if(bm_Main.prefs_enabled_charts.contains(bm_Main.chartsListElements.get(j).getPair())){
                    newChartsList.add(bm_Main.chartsListElements.get(j));
                }
            }

            bm_Main.chartsListElements=newChartsList;
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.chartsAdaptor.setItems(bm_Main.chartsListElements);
                    bm_Main.chartsAdaptor.notifyDataSetChanged();}});  */
                            /*
            for(int i=0;i<pairs_UI.length;i++){
                if(!bm_Main.prefs_enabled_charts.contains(pairs_UI[i])){
                    //if(bm_Main.chartsListElements.size()>1){
                    for(int j=0;j<bm_Main.chartsListElements.size();j++){
                        if(bm_Main.chartsListElements.get(j).getPair().equals(pairs_UI[i])){
                        bm_Main.chartsListElements.remove(j);}
                    }
                    //}
                }
            }
                    */


            for(int i=0;i<bm_Main.pairs_CODE.length;i++){
                                /*
                Ticker = btce_getTicker.getTickerObj(pairs_code[i]);
                final String pair = pairs_UI[i];
                                  */

                Ticker = btce_getTicker.getTickerObj(bm_Main.pairs_CODE[i]);
                final String pair = bm_Main.pairs_UI[i];

                //Log.e("JSON: >>> ", Ticker.toString());
                //String pair, String last, String buy, String sell, String updated, String high, String low

                if(Ticker!=null){
                bm_ListElementCharts el = new bm_ListElementCharts(bm_Main.pairs_UI[i],bm_Main.pairs_CODE[i],
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

                    try{
                    if(!bm_Main.chartsListElementsOld.get(i).getLast().equals("0.00")){
                            bm_Main.chartsListDiff.remove(i);


                        bm_Main.chartsListDiff.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getLast())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getLast())));

                        try{
                            bm_Main.chartsListDiffSell.remove(i);
                        } catch (IndexOutOfBoundsException e){
                        }

                        bm_Main.chartsListDiffSell.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getSell())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getSell())));

                        try{
                            bm_Main.chartsListDiffBuy.remove(i);
                        } catch (IndexOutOfBoundsException e){
                        }

                        bm_Main.chartsListDiffBuy.add(i,String.valueOf(
                                Double.parseDouble(bm_Main.chartsListElements.get(i).getBuy())
                                        -
                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getBuy())));
                    }
                    } catch (IndexOutOfBoundsException e){
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
                    if(fail_list.equals("")){
                        fail_list=pair.replace(" ","");
                    } else {
                        fail_list=fail_list+", "+pair.replace(" ","");
                    }
                }
                //btce_getTicker.get_avg(Ticker);
                //btce_getTicker.get_server_time(Ticker);
                //btce_getTicker.get_vol(Ticker);
                //btce_getTicker.get_vol_cur(Ticker);
            }

            if(!fail_list.equals("")){
                final String fail_list_throw=fail_list;
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /** **/
                        //Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.cant_retrieve)+": "+pair,Toast.LENGTH_SHORT).show();
                        Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.cant_retrieve)+": "+fail_list_throw,Toast.LENGTH_LONG).show();
                    }
                });
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
