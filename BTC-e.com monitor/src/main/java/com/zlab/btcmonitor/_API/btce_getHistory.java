package com.zlab.btcmonitor._API;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zlab.btcmonitor.bm_Main;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class btce_getHistory {
    public static JsonArray getHistoryArray(String pair) {
        Connection.Response response = null;
        try {
            response = Jsoup.connect(bm_Main.API_URL_PUBLIC + pair + "/trades")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (Exception e) {
//            Log.e("ERR", e.getMessage());
        }

        if(response!=null){
            JsonElement jelement = new JsonParser().parse(response.body());
            try{
            JsonArray json_all = jelement.getAsJsonArray();
            return json_all;} catch (Exception e){e.printStackTrace();return null;}
        } else {
            return null;
        }
    }
    //{"date":1385899161,"price":31802,"amount":0.03,"tid":16863295,"price_currency":"RUR","item":"BTC","trade_type":"bid"}
    public static JsonObject getItem(JsonArray HistoryArray,int i){
        return HistoryArray.get(i).getAsJsonObject();
    }

    public static String getDate(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("date").getAsString();
    }

    public static String getPrice(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("price").getAsString();
    }

    public static String getAmount(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("amount").getAsString();
    }

    public static String getTid(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("tid").getAsString();
    }

    public static String getPriceCurrency(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("price_currency").getAsString();
    }
    public static String getItemCurrency(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("item").getAsString();
    }
    public static String getItemType(JsonObject HistoryItem) throws JSONException {
        return HistoryItem.get("trade_type").getAsString();
    }


}
