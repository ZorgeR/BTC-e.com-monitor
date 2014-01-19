package com.zlab.btcmonitor.elements;

public class bm_ChartsListDiffElements {

    private String value;
    private String PairCode;

    public bm_ChartsListDiffElements(String newvalue, String newpaircode)
    {
        //"high":8.611,"low":6.4937,"avg":7.55235,"vol":8715193.94423,"vol_cur":1136312.65928,"last":8.3897,"buy":8.3897,"sell":8.375,"updated":1385057269,"server_time":1385057270
        value = newvalue;
        PairCode = newpaircode;
    }

    public String getPairCode()
    {
        return PairCode;
    }
    public String getValue()
    {
        return value;
    }

    /*
    public void setPair(String newpair)
    {
        Pair=newpair;
    }
    */
}
