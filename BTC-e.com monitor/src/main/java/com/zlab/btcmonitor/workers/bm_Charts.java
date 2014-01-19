package com.zlab.btcmonitor.workers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getTicker;
import com.zlab.btcmonitor.elements.bm_ChartsListDiffElements;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class bm_Charts {
    //bm_Office.updateInfo();
    private static String[] pairs_code;
    private static String[] pairs_UI;

    public static void update_charts(){

        if(!bm_Main.chartsBlocked){
            bm_Main.chartsBlocked=true;

            pairs_code = bm_Main.pairs_CODE.clone();
            pairs_UI = bm_Main.pairs_UI.clone();

            JsonObject Ticker;
            bm_Main.REFRESH_COUNTER++;

            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(true);
                }
            });


            bm_Main.chartsListElementsOld = new ArrayList<bm_ListElementCharts>(bm_Main.chartsListElements);
            listElementsSizeCheck(pairs_code);



            //bm_Main.chartsListElements = new ArrayList<bm_ListElementCharts>();



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


            for(int i=0;i<pairs_code.length;i++){
                                /*
                Ticker = btce_getTicker.getTickerObj(pairs_code[i]);
                final String pair = pairs_UI[i];
                                  */
     //listElementsSizeCheck(pairs_code);
                Ticker = btce_getTicker.getTickerObj(pairs_code[i]);
                final String pair = pairs_UI[i];

                //Log.e("JSON: >>> ", Ticker.toString());
                //String pair, String last, String buy, String sell, String updated, String high, String low


                if(Ticker!=null){
     //listElementsSizeCheck(pairs_code);
                    //try{
                final bm_ListElementCharts el = new bm_ListElementCharts(pairs_UI[i],pairs_code[i],
                        btce_getTicker.get_last(Ticker),
                        btce_getTicker.get_buy(Ticker),
                        btce_getTicker.get_sell(Ticker),
                        btce_getTicker.get_updated(Ticker),
                        btce_getTicker.get_high(Ticker),
                        btce_getTicker.get_low(Ticker));
                //Log.e("LAST: ",el.getLast());

                    bm_Main.chartsListElements.remove(i);
                    bm_Main.chartsListElements.add(i,el);

                    if(pairs_code.length!=bm_Main.txtLast.length)
                    {
                        bm_Main.txtLast=new String[pairs_code.length];
                        bm_Main.txtLow=new String[pairs_code.length];
                        bm_Main.txtHigh=new String[pairs_code.length];
                    }

                    bm_Main.txtLast[i] = btce_getTicker.get_last(Ticker);
                    bm_Main.txtLow[i] = btce_getTicker.get_low(Ticker);
                    bm_Main.txtHigh[i] = btce_getTicker.get_high(Ticker);

                    if(!bm_Main.chartsListElementsOld.get(i).getLast().equals("0.00")){

                        try {
                            bm_Main.chartsListDiff.remove(i);

                        //bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                            //@Override
                            //public void run() {
                                bm_Main.chartsListDiff.add(i,new bm_ChartsListDiffElements(
                                        String.valueOf(
                                                Double.parseDouble(bm_Main.chartsListElements.get(i).getLast())
                                                        -
                                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getLast())
                                        ),pairs_code[i]));

                                bm_Main.chartsListDiffSell.remove(i);
                                bm_Main.chartsListDiffSell.add(i,new bm_ChartsListDiffElements(
                                        String.valueOf(
                                                Double.parseDouble(bm_Main.chartsListElements.get(i).getSell())
                                                        -
                                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getSell())
                                        ),pairs_code[i]));

                                bm_Main.chartsListDiffBuy.remove(i);
                                bm_Main.chartsListDiffBuy.add(i,new bm_ChartsListDiffElements(
                                        String.valueOf(
                                                Double.parseDouble(bm_Main.chartsListElements.get(i).getBuy())
                                                        -
                                                        Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getBuy())
                                        ),pairs_code[i]));
                            //}
                        //});
                        } catch (IndexOutOfBoundsException e){

                        }

                    }
                    //} catch (IndexOutOfBoundsException e){
                    //}
                    //Log.e("chartsListElements." + i, bm_Main.chartsListElements.get(i).getLast());
                    //Log.e("chartsListElementsOld."+i,bm_Main.chartsListElementsOld.get(i).getLast());
                    //Log.e("chartsListDiffSell."+i,bm_Main.chartsListDiffSell.get(i));
                    //Log.e("chartsListDiffBuy."+i,bm_Main.chartsListDiffBuy.get(i));
                    invalidateAdaptor();
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

            //invalidateAdaptor();
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
                    bm_Main.chartsAdaptor.setItems(bm_Main.chartsListElements);
                    bm_Main.chartsAdaptor.notifyDataSetChanged();
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

    private static void invalidateAdaptor(){
        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bm_Main.chartsAdaptor.setItems(bm_Main.chartsListElements);
                bm_Main.chartsAdaptor.notifyDataSetChanged();
            }
        });
    }

    private static void listElementsSizeCheck(final String[] pairs_code){

        List<bm_ChartsListDiffElements> chartsListDiffTmp;
        List<bm_ChartsListDiffElements> chartsListDiffSellTmp;
        List<bm_ChartsListDiffElements> chartsListDiffBuyTmp;

        if(bm_Main.chartsListDiff==null){
            bm_Main.chartsListDiff=new ArrayList<bm_ChartsListDiffElements>();
            bm_Main.chartsListDiffSell=new ArrayList<bm_ChartsListDiffElements>();
            bm_Main.chartsListDiffBuy=new ArrayList<bm_ChartsListDiffElements>();

            for(int i=0;i<pairs_code.length;i++){
                bm_Main.chartsListDiff.add(new bm_ChartsListDiffElements("0.00","none"));
                bm_Main.chartsListDiffSell.add(new bm_ChartsListDiffElements("0.00","none"));
                bm_Main.chartsListDiffBuy.add(new bm_ChartsListDiffElements("0.00","none"));
            }
        }
        //if(bm_Main.chartsListElements.size()!=pairs_code.length || bm_Main.chartsListDiff.size()!=pairs_code.length){

            while(bm_Main.chartsListElements.size()>pairs_code.length){
                for(int i=0;i<pairs_code.length;i++){
                    for(int j=0;j<bm_Main.chartsListElements.size();j++){
                        if(!pairs_code[i].contains(bm_Main.chartsListElements.get(j).getPairCode())){
                            bm_Main.chartsListElements.remove(j);
                            bm_Main.chartsListElementsOld.remove(j);
                        }
                    }
                }
                //bm_Main.chartsListElements.remove(bm_Main.chartsListElements.size()-1);
                //bm_Main.chartsListElementsOld.remove(bm_Main.chartsListElementsOld.size()-1);
            }



            while(bm_Main.chartsListDiff.size()>pairs_code.length){
                bm_Main.chartsListDiff.remove(bm_Main.chartsListDiff.size()-1);
                bm_Main.chartsListDiffSell.remove(bm_Main.chartsListDiffSell.size()-1);
                bm_Main.chartsListDiffBuy.remove(bm_Main.chartsListDiffBuy.size()-1);
            }
            //invalidateAdaptor();

            while(bm_Main.chartsListElements.size()<pairs_code.length){
                FileInputStream fis = null;
                int i=bm_Main.chartsListElements.size();

                try {
                    fis = bm_Main.bm_MainState.openFileInput("charts_"+pairs_code[i]+".json");

                    StringBuffer fileContent = new StringBuffer("");
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) != -1) {
                        fileContent.append(new String(buffer));
                    }
                    final String[] chartsElementsArray=fileContent.toString().split("&");

                            bm_Main.chartsListElements.add(i,new bm_ListElementCharts(
                                    chartsElementsArray[0],
                                    chartsElementsArray[1],
                                    chartsElementsArray[2],
                                    chartsElementsArray[3],
                                    chartsElementsArray[4],
                                    chartsElementsArray[5],
                                    chartsElementsArray[6],
                                    chartsElementsArray[7]));

                    //invalidateAdaptor();
                            bm_Main.chartsListElementsOld.add(i,bm_Main.chartsListElements.get(i));
                } catch (IOException e) {
                    //Log.e("ERR", "MSG");
                            bm_Main.chartsListElements.add(new bm_ListElementCharts(pairs_UI[i],pairs_code[i],"0.00","0.00","0.00","","",""));
                            bm_Main.chartsListElementsOld.add(new bm_ListElementCharts(pairs_UI[i],pairs_code[i],"0.00","0.00","0.00","","",""));
                }
            }



            while (bm_Main.chartsListDiff.size()<pairs_code.length){
                int c=bm_Main.chartsListDiff.size();
                bm_Main.chartsListDiff.add(c,new bm_ChartsListDiffElements(bm_Main.chartsListElements.get(c).getLast(),bm_Main.chartsListElements.get(c).getPairCode()));
                bm_Main.chartsListDiffSell.add(c, new bm_ChartsListDiffElements(bm_Main.chartsListElements.get(c).getSell(),bm_Main.chartsListElements.get(c).getPairCode()));
                bm_Main.chartsListDiffBuy.add(c, new bm_ChartsListDiffElements(bm_Main.chartsListElements.get(c).getBuy(),bm_Main.chartsListElements.get(c).getPairCode()));
            }

        chartsListDiffTmp=bm_Main.chartsListDiff;
        chartsListDiffSellTmp=bm_Main.chartsListDiffSell;
        chartsListDiffBuyTmp=bm_Main.chartsListDiffBuy;

                for (int i=0;i<pairs_code.length;i++){
                            bm_Main.chartsListElements.get(i).setPair(pairs_UI[i]);
                            for(int j=0;j<chartsListDiffTmp.size();j++){
                                if(chartsListDiffTmp.get(j).getPairCode().equals(pairs_code[i])){
                                    bm_Main.chartsListDiff.set(i,chartsListDiffTmp.get(j));
                                    bm_Main.chartsListDiffSell.set(i,chartsListDiffSellTmp.get(j));
                                    bm_Main.chartsListDiffBuy.set(i,chartsListDiffBuyTmp.get(j));
                                }
                            }
                        }

        //invalidateAdaptor();
    }
}
