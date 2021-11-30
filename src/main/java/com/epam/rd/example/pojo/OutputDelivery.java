package com.epam.rd.example.pojo;

import java.io.Serializable;
import java.util.List;

public class OutputDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SEPARATOR = "-----------------------";

    private List<Package> packages;

    private int deliveryNumber;

    public OutputDelivery(List<Package> packages) {
        assert (packages.size() > 0);
        this.packages = packages;
        this.deliveryNumber = packages.get(0).getDeliveryNumber();
    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(int deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(SEPARATOR + "\n");
        buffer.append("org.springframework.integration.samples.cafe.pojo.InputDelivery #" + getDeliveryNumber() + "\n");
        for (Package newPackage : getPackages()) {
            buffer.append(newPackage);
            buffer.append("\n");
        }
        buffer.append(SEPARATOR + "\n");
        return buffer.toString();
    }

}
