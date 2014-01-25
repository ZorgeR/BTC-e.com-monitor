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
    private String DiffLast;
    private String DiffBuy;
    private String DiffSell;
    //private Date postDate;
    //private Drawable avatar;

    public bm_ListElementCharts(String pair, String paircode, String last, String buy, String sell, String updated, String high, String low, String diflast, String diffbuy, String difsell)
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
        DiffLast = diflast;
        DiffBuy = diffbuy;
        DiffSell = difsell;
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

    public String getDiffLast(){
        return DiffLast;
    }
    public String getDiffBuy(){
        return DiffBuy;
    }
    public String getDiffSell(){
        return DiffSell;
    }

    public void setPair(String newpair)
    {
        Pair=newpair;
    }
    public void setDiffLast(String diflast)
    {
        DiffLast=diflast;
    }
    public void setDiffBuy(String difbuy)
    {
        DiffBuy=difbuy;
    }
    public void setDiffSell(String difsell)
    {
        DiffSell=difsell;
    }
}
