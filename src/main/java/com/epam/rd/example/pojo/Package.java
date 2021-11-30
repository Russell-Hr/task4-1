package com.epam.rd.example.pojo;

import java.io.Serializable;

public class Package implements Serializable {

    private static final long serialVersionUID = 1L;

    private DeliveryType deliveryType;

    private int deliveryNumber;

    public Package(int deliveryNumber, DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
        this.deliveryNumber = deliveryNumber;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(int deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    @Override
    public String toString() {
        return deliveryType.toString();
    }
}

