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

import java.util.Iterator;

public class zlab_getPairs {

    public static JsonObject getPairsList() {
        Connection.Response response = null;
        try {
            String urlToJson = bm_Main.API_ZLAB_PAIRS_LIST_URL;
            if(bm_Main.prefs_use_proxy){ urlToJson = bm_Main.API_ZLAB_PROXY_URL+urlToJson; }

            response = Jsoup.connect(urlToJson)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (Exception e) {
            Log.e("ERR", e.getMessage());
        }

        if(response!=null){
            JsonElement jelement = new JsonParser().parse(response.body());
            try{
                JsonObject json_all = jelement.getAsJsonObject();
                return json_all;} catch (Exception e){e.printStackTrace();return null;}
        } else {
            return null;
        }
    }

    public static String[] getStringArray(JsonObject PairsArray){
         if(PairsArray==null)
                return null;

        JsonObject jsonPairs = PairsArray.get("pairs").getAsJsonObject();
        JSONObject js = null;

        try {
            js = new JSONObject(jsonPairs.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] arr=new String[js.length()];
         for(int i=0; i<arr.length; i++) {
             try {
                 arr[i]=js.names().get(i).toString();
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
         return arr;

        //arr = PairsArray.toString().replace("},{", " ,").split(" ");
        //return null;
    }

}
