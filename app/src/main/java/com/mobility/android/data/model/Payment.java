package com.mobility.android.data.model;

import timber.log.Timber;

public class Payment {

    public static final String TYPE_CREDIT_CARD = "card";
    public static final String TYPE_CASH = "cash";
    public static final String TYPE_PAYPAL = "paypal";

    private String qrCode;
    private String currency = "EUR";
    private String id;

    private String type;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Timber.i("Set new id: %s", id);
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        Timber.w("Set payment type: %s", type);
        this.type = type;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "qrCode='" + qrCode + '\'' +
                ", currency='" + currency + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
