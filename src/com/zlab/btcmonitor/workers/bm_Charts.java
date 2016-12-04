package com.zlab.btcmonitor.workers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getTicker;
import com.zlab.btcmonitor._API.zlab_getPairs;
import com.zlab.btcmonitor.adaptors.bm_ChartsAdaptor;
import com.zlab.btcmonitor.elements.bm_ChartsListDiffElements;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;

import java.util.ArrayList;
import java.util.Arrays;

public class bm_Charts {
    //bm_Office.updateInfo();
    //private static String[] pairs_code;
    //private static String[] pairs_UI;

    public static void update_charts(/*String[] pairscode, String[] pairsui*/){

        String[] pairs_from_server = null;

        try {
            pairs_from_server = zlab_getPairs.getStringArray(zlab_getPairs.getPairsList());
        } catch (Exception e){

        }

        if (pairs_from_server != null){
        if (!Arrays.equals(VARs.pairs_CODE, pairs_from_server)){
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /** **/
                    //Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.new_pair_detected),Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(bm_Main.bm_MainContext);
                    builder.setMessage(bm_Main.bm_MainState.getResources().getString(R.string.new_pair_detected))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            bm_Main.saveNewPairsID(pairs_from_server);

            VARs.pairs_CODE = pairs_from_server;
            VARs.pairs_UI = new String[VARs.pairs_CODE.length];
            for (int i=0;i<VARs.pairs_CODE.length;i++){
                VARs.pairs_UI[i] = VARs.pairs_CODE[i].split("_")[0].toUpperCase()+" / "+VARs.pairs_CODE[i].split("_")[1].toUpperCase();
            }
            bm_Main.chartsEnabled = new boolean[VARs.pairs_CODE.length];
            bm_Main.getSettigns();
            //Arrays.fill(bm_Main.chartsEnabled, true);

            bm_Main.chartsArrayBlank();
        }}

            //pairs_code = pairscode;
            //pairs_UI = pairsui;

            JsonObject Ticker=null;

            bm_Main.REFRESH_COUNTER++;
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(true);
                }
            });

            bm_Main.chartsListElementsOld = new ArrayList<bm_ListElementCharts>(bm_Main.chartsListElements);
        /*
        if(bm_Main.chartsListDiff==null){
            bm_Main.chartsListDiff=new ArrayList<bm_ChartsListDiffElements>();
            bm_Main.chartsListDiffSell=new ArrayList<bm_ChartsListDiffElements>();
            bm_Main.chartsListDiffBuy=new ArrayList<bm_ChartsListDiffElements>();

            for(int i=0;i<VARs.pairs_CODE.length;i++){
                bm_Main.chartsListDiff.add(new bm_ChartsListDiffElements("0.00","none"));
                bm_Main.chartsListDiffSell.add(new bm_ChartsListDiffElements("0.00","none"));
                bm_Main.chartsListDiffBuy.add(new bm_ChartsListDiffElements("0.00","none"));
            }
        }
          */
            String fail_list="";
            for(int i=0;i<VARs.pairs_CODE.length;i++){
                if(bm_Main.chartsEnabled[i]){
                    Ticker = btce_getTicker.getTickerObj(VARs.pairs_CODE[i]);
                }
                final String pair = VARs.pairs_UI[i];

                if(Ticker!=null){
                bm_ListElementCharts el = new bm_ListElementCharts(VARs.pairs_UI[i],VARs.pairs_CODE[i],
                        btce_getTicker.get_last(Ticker),
                        btce_getTicker.get_buy(Ticker),
                        btce_getTicker.get_sell(Ticker),
                        btce_getTicker.get_updated(Ticker),
                        btce_getTicker.get_high(Ticker),
                        btce_getTicker.get_low(Ticker),
                        "0.00",
                        "0.00",
                        "0.00");

                    if(Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getLast())!=0){
                            el.setDiffLast(String.valueOf(
                                            Double.parseDouble(btce_getTicker.get_last(Ticker))
                                                    -
                                                    Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getLast())));

                            el.setDiffSell(String.valueOf(
                                            Double.parseDouble(btce_getTicker.get_sell(Ticker))
                                                    -
                                                    Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getSell())));

                            el.setDiffBuy(String.valueOf(
                                            Double.parseDouble(btce_getTicker.get_buy(Ticker))
                                                    -
                                                    Double.parseDouble(bm_Main.chartsListElementsOld.get(i).getBuy())));
                    }

                    bm_Main.chartsListElements.remove(i);
                    bm_Main.chartsListElements.add(i,el);

                    if(VARs.pairs_CODE.length!=bm_Main.txtLast.length)
                    {
                        bm_Main.txtLast=new String[VARs.pairs_CODE.length];
                        bm_Main.txtLow=new String[VARs.pairs_CODE.length];
                        bm_Main.txtHigh=new String[VARs.pairs_CODE.length];
                    }

                    bm_Main.txtLast[i] = btce_getTicker.get_last(Ticker);
                    bm_Main.txtLow[i] = btce_getTicker.get_low(Ticker);
                    bm_Main.txtHigh[i] = btce_getTicker.get_high(Ticker);
                    invalidateAdaptor();
                } else {
                    if(bm_Main.chartsEnabled[i]){
                        if(fail_list.equals("")){
                            fail_list=pair.replace(" ","");
                        } else {
                            fail_list=fail_list+", "+pair.replace(" ","");
                        }
                    }
                }
                Ticker=null;
            }

            if(!fail_list.equals("")){
                final String fail_list_throw=fail_list;
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /** **/
                        Toast.makeText(bm_Main.bm_MainContext,bm_Main.bm_MainState.getResources().getString(R.string.cant_retrieve)+": "+fail_list_throw,Toast.LENGTH_LONG).show();
                    }
                });
            }

            //final JsonObject pairsObject = zlab_getPairs.getList();
            //final JsonArray arr = pairsObject.getAsJsonArray();

            bm_Main.reHide();
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
                    //bm_Main.chartsAdaptor.setItems(bm_ChartsAdaptor.hide(bm_Main.chartsListElements));
                    bm_Main.chartsAdaptor.notifyDataSetChanged();
                    //bm_Main.chartsAdaptor.notifyDataSetInvalidated();
                }
            });
            bm_Main.chartsBlocked=false;



    }

    private static void invalidateAdaptor(){
        //bm_Main.reHide();
        bm_Main.reHide();
        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //bm_Main.chartsListElementsToShow=bm_ChartsAdaptor.hide(chartsListElements);
                //bm_Main.chartsAdaptor.setItems(bm_ChartsAdaptor.hide(bm_Main.chartsListElements));
                //bm_Main.chartsAdaptor.notifyDataSetChanged();
                //bm_Main.chartsAdaptor = new bm_ChartsAdaptor(bm_Main.bm_MainContext,R.layout.charts_list_item,bm_Main.chartsListElementsToShow);
                //bm_Main.chartsList.setAdapter(bm_Main.chartsAdaptor);
                //bm_Main.chartsList.requestLayout();
                bm_Main.chartsAdaptor.notifyDataSetChanged();
                //bm_Main.chartsAdaptor.notifyDataSetInvalidated();
            }
        });
        //try{Thread.sleep(2000);} catch (InterruptedException e) {}
    }

    /*
    public static void listElementsSizeCheckkkkk(String[] pairscode, String[] pairsui){

        pairs_code = pairscode;
        pairs_UI = pairsui;

        List<bm_ChartsListDiffElements> chartsListDiffTmp;
        List<bm_ChartsListDiffElements> chartsListDiffSellTmp;
        List<bm_ChartsListDiffElements> chartsListDiffBuyTmp;


            while(bm_Main.chartsListElements.size()>pairs_code.length){
                for(int i=0;i<pairs_code.length;i++){
                    for(int j=0;j<bm_Main.chartsListElements.size();j++){
                        if(!pairs_code[i].contains(bm_Main.chartsListElements.get(j).getPairCode())){
                            bm_Main.chartsListElements.remove(j);
                            bm_Main.chartsListElementsOld.remove(j);
                        }
                    }
                }
            }



            while(bm_Main.chartsListDiff.size()>pairs_code.length){
                bm_Main.chartsListDiff.remove(bm_Main.chartsListDiff.size()-1);
                bm_Main.chartsListDiffSell.remove(bm_Main.chartsListDiffSell.size()-1);
                bm_Main.chartsListDiffBuy.remove(bm_Main.chartsListDiffBuy.size()-1);
            }

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


    }   */
}
