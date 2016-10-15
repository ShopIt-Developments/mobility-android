package com.mobility.android.data.network.api;

import com.google.gson.annotations.SerializedName;
import com.mobility.android.data.network.Endpoint;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface PaymentApi {

    @POST(Endpoint.PAYMENT_SCAN)
    Observable<Void> scan(@Path("order_id") String order, @Body PaymentScan payment);

    @POST(Endpoint.PAYMENT_PRICE)
    Observable<Void> price(@Path("order_id") String order, @Body PaymentPrice payment);

    @POST(Endpoint.PAYMENT_APPROVE)
    Observable<Void> approve(@Path("order_id") String order);

    @POST(Endpoint.PAYMENT_CANCEL)
    Observable<Void> cancel(@Path("order_id") String order);

    class PaymentScan {

        @SerializedName("qr_code")
        public String qrCode;

        @SerializedName("payment_type")
        public String type;

        @SerializedName("credit_card")
        public PaymentCreditCard card;

        class PaymentCreditCard {

            @SerializedName("card_number")
            public String cardNumber;

            @SerializedName("expiration_date")
            public String expirationDate;

            @SerializedName("security_code")
            public String securityCode;
        }
    }

    class PaymentPrice {

        @SerializedName("price")
        public float price;

        @SerializedName("currency")
        public String currency;

        @SerializedName("bill")
        public String bill;
    }
}
