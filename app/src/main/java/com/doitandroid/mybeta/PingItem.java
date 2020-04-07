package com.doitandroid.mybeta;

public class PingItem {
    String pingID;
    String pingText;
    Integer pingRes;

    Integer reactRes;

    public PingItem(String pingID, String pingText, Integer pingRes) {
        this.pingID = pingID;
        this.pingText = pingText;
        this.pingRes = pingRes;
        // react 가 따로 정해지지 않으면
        // this.reactRes = null; 이건 adapter 에서 null체크하므로 이렇게 썼었음
        this.reactRes = R.raw.heart_48;
    }
    public PingItem(String pingID, String pingText, Integer pingRes, Integer reactRes) {

        this.pingID = pingID;
        this.pingText = pingText;
        this.pingRes = pingRes;
        this.reactRes = reactRes;
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

    public Integer getReactRes() {
        return reactRes;
    }

    public void setReactRes(Integer reactRes) {
        this.reactRes = reactRes;
    }

}
