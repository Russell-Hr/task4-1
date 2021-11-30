package com.epam.rd.example.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InputDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DeliveryItem> deliveryItems = new ArrayList<DeliveryItem>();

    private int number;

    public InputDelivery(int number) {
        this.number = number;
    }

    public void addItem(DeliveryType deliveryType) {
        this.deliveryItems.add(new DeliveryItem(this.number, deliveryType));
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<DeliveryItem> getItems() {
        return this.deliveryItems;
    }

    public void setItems(List<DeliveryItem> deliveryItems) {
        this.deliveryItems = deliveryItems;
    }
}
