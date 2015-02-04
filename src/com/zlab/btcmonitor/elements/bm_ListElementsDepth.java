package com.zlab.btcmonitor.elements;

public class bm_ListElementsDepth {

    private String Price;
    private String Amount;

    public bm_ListElementsDepth(String price, String amount)
    {
        //"high":8.611,"low":6.4937,"avg":7.55235,"vol":8715193.94423,"vol_cur":1136312.65928,"last":8.3897,"buy":8.3897,"sell":8.375,"updated":1385057269,"server_time":1385057270
        Price = price;
        Amount = amount;
    }

    public String getPrice()
    {
        return Price;
    }
    public String getAmount()
    {
        return Amount;
    }
}
