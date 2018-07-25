package com.utils.tables;

public class Range {

    private String line;
    private String costEnd;
    private String gains;
    private String shopID;

    public void setLine(String line) {
        this.line = line;
    }

    public void setCostInit(String costInit) {
        this.costInit = costInit;
    }

    public void setCostEnd(String costEnd) {
        this.costEnd = costEnd;
    }

    public void setGains(String gains) {
        this.gains = gains;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    private String costInit;

    public String getLine() {
        return line;
    }

    public String getCostInit() {
        return costInit;
    }

    public String getCostEnd() {
        return costEnd;
    }

    public String getGains() {
        return gains;
    }

    public String getShopID() {
        return shopID;
    }
}
