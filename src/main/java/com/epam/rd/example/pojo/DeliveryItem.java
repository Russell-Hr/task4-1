package com.epam.rd.example.pojo;

import java.io.Serializable;

public class DeliveryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private DeliveryType type;

    private int deliveryNumber;

    public DeliveryItem(int deliveryNumber, DeliveryType type) {
        this.deliveryNumber = deliveryNumber;
        this.type = type;
    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(int deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public DeliveryType getType() {
        return type;
    }

    public void setType(DeliveryType type) {
        this.type = type;
    }

    public String toString() {
        return this.type.toString();
    }
}