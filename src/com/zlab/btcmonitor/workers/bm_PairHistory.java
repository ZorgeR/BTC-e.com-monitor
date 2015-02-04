package com.zlab.btcmonitor.workers;

import android.widget.Toast;
import com.google.gson.JsonArray;
import com.zlab.btcmonitor.R;
import com.zlab.btcmonitor._API.btce_getHistory;
import com.zlab.btcmonitor.adaptors.bm_HistoryAdaptor;
import com.zlab.btcmonitor.bm_Main;
import com.zlab.btcmonitor.elements.bm_ListElementsHistory;
import org.json.JSONException;

import java.util.ArrayList;

public class bm_PairHistory {


    //{"date":1385899161,"price":31802,"amount":0.03,"tid":16863295,"price_currency":"RUR","item":"BTC","trade_type":"bid"}
    public static void getHistory(String pair){

        JsonArray historyArray = btce_getHistory.getHistoryArray(pair);

        if(historyArray!=null){

            bm_Main.historyElements = new ArrayList<bm_ListElementsHistory>();

                //JsonObject ordersReturn = btce_getActiveOrders.getReturn(activeOrders);
                          /*
                JSONArray jo = null;
                try {
                    jo = new JSONArray(historyArray.toString());
                } catch (JSONException e) {
                    Log.e("ERR", e.getMessage());
                }
                     */
                //final int size = jo.length();

                //Log.e("EEEEEEEEEEEEEEEEEEE::: ", ordersReturn.get(0).getAsJsonObject().entrySet().entrySet().get("68797198").getAsJsonObject().get("pair").toString());

                    for(int i=0;i<historyArray.size();i++){
                        //String key = jo.names().get(i).toString();
                        //JSONObject jo = new JSONObject(ordersReturn.get(i).toString());
                        //String key = jo.names().get(i).toString();

                        //JsonElement jelement = new JsonParser().parse(jo.get(key).toString());
                        //JsonObject getKeyElement = jelement.getAsJsonObject();

                        //Log.e("EEEEEEEEEEEE:",jo.length()+" "+key+" "+jo.get(key).toString());

                        //JsonObject getKeyElement = btce_getActiveOrders.getKeyElement(j,key);
                        //bm_ListElementsHistory(String date, String price, String amount, String tid, String pricecur, String itemcur, String type)
                        bm_ListElementsHistory eo = null;
                        try {
                            eo = new bm_ListElementsHistory(
                                    btce_getHistory.getDate(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getPrice(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getAmount(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getTid(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getPriceCurrency(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getItemCurrency(historyArray.get(i).getAsJsonObject()),
                                    btce_getHistory.getItemType(historyArray.get(i).getAsJsonObject())
                                    );
                            bm_Main.historyElements.add(eo);
                        } catch (JSONException e) {
                            //Log.e("ERR", "MSG");
                        }
                    }

                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bm_Main.historyAdaptor==null){
                            bm_Main.historyAdaptor = new bm_HistoryAdaptor(bm_Main.bm_MainContext, R.layout.history_list_item,bm_Main.historyElements);
                            // bm_Main.orderList.setAdapter(bm_Main.orderAdaptor);
                        } else {
                            bm_Main.historyAdaptor.setItems(bm_Main.historyElements);
                            bm_Main.historyAdaptor.notifyDataSetChanged();
                        }

                        bm_Main.btn_History.setEnabled(true);
                    }
                });
            } else {
                // no orders
                bm_Main.bm_MainState.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(bm_Main.bm_MainContext, "Ошибка при получении истории.", Toast.LENGTH_SHORT).show();
                        //bm_Main.btn_Orders.setEnabled(false);
                        //bm_Main.btn_Orders.setText("Ваши ордеры (0)");
                    }
                });
            }
    }
}
