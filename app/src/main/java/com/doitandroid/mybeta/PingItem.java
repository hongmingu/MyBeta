package com.doitandroid.mybeta;

public class PingItem {
    String PingID;
    String PingText;
    Integer PingRes;

    public PingItem(String pingID, String pingText, Integer pingRes) {
        PingID = pingID;
        PingText = pingText;
        PingRes = pingRes;
    }

    public String getPingID() {
        return PingID;
    }

    public void setPingID(String pingID) {
        PingID = pingID;
    }

    public String getPingText() {
        return PingText;
    }

    public void setPingText(String pingText) {
        PingText = pingText;
    }

    public Integer getPingRes() {
        return PingRes;
    }

    public void setPingRes(Integer pingRes) {
        PingRes = pingRes;
    }

    public Boolean testID(String testID){
        return PingID.equals(testID);
    }
}
