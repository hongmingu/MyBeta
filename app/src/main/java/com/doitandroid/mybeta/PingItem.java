package com.doitandroid.mybeta;

public class PingItem {
    String pingID;
    String pingText;
    Integer pingRes;

    public PingItem(String pingID, String pingText, Integer pingRes) {
        this.pingID = pingID;
        this.pingText = pingText;
        this.pingRes = pingRes;
    }

    public String getPingID() {
        return pingID;
    }

    public void setPingID(String pingID) {
        this.pingID = pingID;
    }

    public String getPingText() {
        return pingText;
    }

    public void setPingText(String pingText) {
        this.pingText = pingText;
    }

    public Integer getPingRes() {
        return pingRes;
    }

    public void setPingRes(Integer pingRes) {
        this.pingRes = pingRes;
    }

    public Boolean testID(String testID){
        return pingID.equals(testID);
    }
}
