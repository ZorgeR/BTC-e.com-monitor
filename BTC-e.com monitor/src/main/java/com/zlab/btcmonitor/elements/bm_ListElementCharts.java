package com.zlab.btcmonitor.elements;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class bm_ListElementCharts {

    private String Pair;
    private String PairCode;
    private String Last;
    private String Buy;
    private String Sell;
    private String Updated;
    private String High;
    private String Low;
    //private Date postDate;
    //private Drawable avatar;

    public bm_ListElementCharts(String pair, String paircode, String last, String buy, String sell, String updated, String high, String low)
    {
        //"high":8.611,"low":6.4937,"avg":7.55235,"vol":8715193.94423,"vol_cur":1136312.65928,"last":8.3897,"buy":8.3897,"sell":8.375,"updated":1385057269,"server_time":1385057270
        Pair = pair;
        PairCode = paircode;
        Last = last;
        Buy = buy;
        Sell = sell;
        Updated = updated;
        High = high;
        Low = low;
    }

    public String getPair()
    {
        return Pair;
    }
    public String getPairCode()
    {
        return PairCode;
    }
    public String getLast()
    {
        return Last;
    }
    public String getBuy(){
        return Buy;
    }
    public String getSell(){
        return Sell;
    }
    public String getUpdated()
    {
        return Updated;
    }
    public String getHigh()
    {
        return High;
    }
    public String getLow()
    {
        return Low;
    }
    public String getAsString(){
        return Pair+"&"+PairCode+"&"+Last+"&"+Buy+"&"+Sell+"&"+Updated+"&"+High+"&"+Low;
    }

    public void setPair(String newpair)
    {
        Pair=newpair;
    }
}
