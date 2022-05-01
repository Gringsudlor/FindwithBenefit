package com.example.findwithbenefit;

public class Booking {
    public String name, status;

    public Booking(){

    }

    public Booking(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String cost) {
        this.status = status;
    }

}
