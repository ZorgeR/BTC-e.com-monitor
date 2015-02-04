package com.zlab.btcmonitor._API;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zlab.btcmonitor.bm_Main;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class btce_getTicker {
    public static JsonObject getTickerObj(String pair) {
        Connection.Response response = null;
        try {
            response = Jsoup.connect(bm_Main.API_URL_PUBLIC + pair + "/ticker")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (Exception e) {      /** FIX MalformedURLException **/
//            Log.e("ERR", e.getMessage());
        }

        if(response!=null){
            JsonElement jelement = new JsonParser().parse(response.body());
            JsonObject json_all = jelement.getAsJsonObject();
            try{
            return json_all.get("ticker").getAsJsonObject();
            } catch (Exception e){e.printStackTrace();return null;}
        } else {
            return null;
        }
    }

    //{"ticker":{"high":8.611,"low":6.4937,"avg":7.55235,"vol":8710419.79383,"vol_cur":1135522.1716,"last":8.37,"buy":8.37,"sell":8.368689,"updated":1385057451,"server_time":1385057452}}

    public static String get_high(JsonObject Ticker){
        return Ticker.get("high").toString();
    }
    public static String get_low(JsonObject Ticker){
        return Ticker.get("low").toString();
    }
    public static String get_avg(JsonObject Ticker){
        return Ticker.get("avg").toString();
    }
    public static String get_vol(JsonObject Ticker){
        return Ticker.get("vol").toString();
    }
    public static String get_vol_cur(JsonObject Ticker){
        return Ticker.get("vol_cur").toString();
    }
    public static String get_last(JsonObject Ticker){
        return Ticker.get("last").toString();
    }
    public static String get_buy(JsonObject Ticker){
        return Ticker.get("buy").toString();
    }
    public static String get_sell(JsonObject Ticker){
        return Ticker.get("sell").toString();
    }
    public static String get_updated(JsonObject Ticker){
        return Ticker.get("updated").toString();
    }
    public static String get_server_time(JsonObject Ticker){
        return Ticker.get("server_time").toString();
    }

}
