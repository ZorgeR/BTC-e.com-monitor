package com.zlab.btcmonitor.workers;

import com.google.gson.JsonObject;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.VARs;
import com.zlab.btcmonitor._API.btce_getInfo;
import com.zlab.btcmonitor.adaptors.bm_FundsAdaptor;
import com.zlab.btcmonitor.elements.bm_ListElementCharts;
import com.zlab.btcmonitor.bm_Main;
import com.zlab.btcmonitor.UI.navDrawer;

import java.util.ArrayList;

public class bm_Office {

    public static void updateInfo() {

        if(!bm_Main.fundsBlocked){
            bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(true);
                }
            });

            bm_Main.fundsBlocked=true;

            JsonObject INFO = btce_getInfo.getInfoObj();
        bm_Main.getInfo_data =bm_Main.bm_MainState.getResources().getString(R.string.last_query)+": ";

            if(INFO!=null){
        if(btce_getInfo.getSuccess(INFO)){
                bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.success)+".\n";
            JsonObject RETURN = btce_getInfo.getReturn(INFO);
            bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.trade_permission)+": ";
            if(btce_getInfo.getRights(RETURN, VARs.RIGHTS_TRADE)){
                bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.present)+".\n";
            } else {
                bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.no_present)+".\n";
            }
            bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.all_transaction)+": "+ btce_getInfo.getTransaction_count(RETURN)+"\n";
            bm_Main.getInfo_data = bm_Main.getInfo_data +bm_Main.bm_MainState.getResources().getString(R.string.open_orders)+": "+ btce_getInfo.getOpenOrders(RETURN);

            //bm_Main.office_stat=bm_Main.office_stat+"Ваши счета:";

            bm_Main.fundsListElements = new ArrayList<bm_ListElementCharts>();

            String[] funds_code = VARs.funds_code;

            for(int i=0;i<funds_code.length;i++){
                //Log.e("JSON: >>> ",Ticker.toString());
                //String pair, String last, String buy, String sell, String updated, String high, String low
                bm_ListElementCharts el = new bm_ListElementCharts(
                        funds_code[i],"",
                        btce_getInfo.getFunds(RETURN, funds_code[i]),
                        "","","","","","","","");

                //Log.e("LAST: ",el.getLast());
                bm_Main.fundsListElements.add(el);

                //btce_getTicker.get_avg(Ticker);
                //btce_getTicker.get_server_time(Ticker);
                //btce_getTicker.get_vol(Ticker);
                //btce_getTicker.get_vol_cur(Ticker);
            }
                       /**
                bm_Main.office_stat=bm_Main.office_stat+"USD: "+ +"\n";
                bm_Main.office_stat=bm_Main.office_stat+"BTC: "+ btce_getInfo.getFunds(RETURN, "BTC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"LTC: "+ btce_getInfo.getFunds(RETURN, "LTC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"RUR: "+ btce_getInfo.getFunds(RETURN, "RUR")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"NMC: "+ btce_getInfo.getFunds(RETURN, "NMC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"TRC: "+ btce_getInfo.getFunds(RETURN, "TRC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"PPC: "+ btce_getInfo.getFunds(RETURN, "PPC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"FTC: "+ btce_getInfo.getFunds(RETURN, "FTC")+"\n";
                bm_Main.office_stat=bm_Main.office_stat+"XPM: "+ btce_getInfo.getFunds(RETURN, "XPM")+"\n\n";*/

         } else {
                    bm_Main.getInfo_data = bm_Main.getInfo_data +"неудачно.\n\n";
                    bm_Main.getInfo_data = bm_Main.getInfo_data +"Ошибка:"+INFO.toString();
                }
            }
        bm_Main.bm_MainState.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bm_Main.fundsAdaptor = new bm_FundsAdaptor(bm_Main.bm_MainContext,R.layout.charts_list_item,bm_Main.fundsListElements);


                if(bm_Main.mTitle.equals(bm_Main.bm_MainState.getString(R.string.title_office))){
                    if(bm_Main.fundsListElements!=null){
                        bm_Main.fundsList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bm_Main.fundsList.setAdapter(bm_Main.fundsAdaptor);
                            }
                        },250);

                    }

                    if(bm_Main.getInfo_data != null && !bm_Main.getInfo_data.isEmpty()){
                        bm_Main.office_footer.setText(bm_Main.getInfo_data);
                    } else {
                        bm_Main.office_footer.setText(bm_Main.mTitle);
                    }
                    /*
                    if (navDrawer.mCallbacks != null) {
                        navDrawer.mCallbacks.onNavigationDrawerItemSelected(1);
                    }
                    */
                }

                //bm_Main.fundsAdaptor.notifyDataSetChanged();
                bm_Main.bm_MainState.setProgressBarIndeterminateVisibility(false);
            }
        });
            bm_Main.fundsBlocked=false;
        } else {
        }
    }
}