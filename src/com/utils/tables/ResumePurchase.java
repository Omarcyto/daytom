package com.utils.tables;

public class ResumePurchase {

    private String provideID;
    private String date;
    private String tcLocal;
    private String tcExt;
    private String total;
    private String literal;
    private String userID;
    private String state;
    private String shopID;
    private String purchaseID;

    public String getPurchaseID() {
        return purchaseID;
    }

    public void setPurchaseID(String purchaseID) {
        this.purchaseID = purchaseID;
    }



    public String getProvideID() {
        return provideID;
    }

    public String getDate() {
        return date;
    }

    public String getTcLocal() {
        return tcLocal;
    }

    public String getTcExt() {
        return tcExt;
    }

    public String getTotal() {
        return total;
    }

    public String getLiteral() {
        return literal;
    }

    public String getUserID() {
        return userID;
    }

    public String getState() {
        return state;
    }

    public String getShopID() {
        return shopID;
    }

    public void setProvideID(String provideID) {
        this.provideID = provideID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTcLocal(String tcLocal) {
        this.tcLocal = tcLocal;
    }

    public void setTcExt(String tcExt) {
        this.tcExt = tcExt;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
