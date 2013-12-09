package com.zlab.btcmonitor.elements;

public class bm_ListElementsHistory {


    //{"date":1385899161,"price":31802,"amount":0.03,"tid":16863295,"price_currency":"RUR","item":"BTC","trade_type":"bid"}
    private String Date;
    private String Price;
    private String Amount;
    private String Tid;
    private String PriceCur;
    private String ItemCur;
    private String Type;

    public bm_ListElementsHistory(String date, String price, String amount, String tid, String pricecur, String itemcur, String type)
    {
            /*"pair":"btc_usd",
			"type":"sell",
			"amount":1.00000000,
			"rate":3.00000000,
			"timestamp_created":1342448420,
			"status":0*/

        Date = date;
        Type = type;
        Amount = amount;
        Price = price;
        Date = date;
        PriceCur = pricecur;
        ItemCur = itemcur;
        Tid = tid;
    }

    public String getDate()
    {
        return Date;
    }
    public String getPrice()
    {
        return Price;
    }
    public String getAmount()
    {
        return Amount;
    }
    public String getTid(){
        return Tid;
    }
    public String getPriceCur()
    {
        return PriceCur;
    }
    public String getItemCur(){
        return ItemCur;
    }
    public String getType()
    {
        return Type;
    }
}
