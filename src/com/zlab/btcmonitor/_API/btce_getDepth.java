package com.zlab.btcmonitor._API;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zlab.btcmonitor.bm_Main;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class btce_getDepth {
    public static JsonObject getDepthObj(String pair) {
        Connection.Response response = null;
        try {
            String urlToJson = bm_Main.API_URL_PUBLIC + pair + "/depth";
            if(bm_Main.prefs_use_proxy){ urlToJson = bm_Main.API_ZLAB_PROXY_URL+urlToJson; }

            response = Jsoup.connect(urlToJson)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (Exception e) {
//            Log.e("ERR", e.getMessage());
        }

        if(response!=null){
            JsonElement jelement = new JsonParser().parse(response.body());
            JsonObject json_all = jelement.getAsJsonObject();
            return json_all;
        } else {
            return null;
        }
    }

    public static JsonArray getDepthAsk(JsonObject DepthObj){
        try{return DepthObj.get("asks").getAsJsonArray();}catch (Exception e){e.printStackTrace();return null;}
    }
    public static JsonArray getDepthBids(JsonObject DepthObj){
        try{return DepthObj.get("bids").getAsJsonArray();}catch (Exception e){e.printStackTrace();return null;}
    }

    //{"asks":[[700,0.07501942],[701,0.02498058],[721.89,0.12006]],"bids":[[700,0.0142025],[699.008,0.0734028],[660.002,0.01],[660,21.10717543]]}
}
