package com.zlab.btcmonitor.elements;

public class bm_ListElementOrder {

        private String Pair;
        private String Type;
        private String Amount;
        private String Rate;
        private String Timestamp;
        private String Status;
        private String OrderID;

        public bm_ListElementOrder(String pair, String type, String amount, String rate, String timestamp, String status, String orderID)
        {
            /*"pair":"btc_usd",
			"type":"sell",
			"amount":1.00000000,
			"rate":3.00000000,
			"timestamp_created":1342448420,
			"status":0*/

            Pair = pair;
            Type = type;
            Amount = amount;
            Rate = rate;
            Timestamp = timestamp;
            Status = status;
            OrderID = orderID;
        }

        public String getPair()
        {
            return Pair;
        }
        public String getType()
        {
            return Type;
        }
        public String getAmount()
        {
            return Amount;
        }
        public String getRate()
        {
            return Rate;
        }
        public String getTimestamp()
        {
            return Timestamp;
        }
        public String getStatus()
        {
            return Status;
        }
        public String getOrderID(){
            return OrderID;
        }
        }
