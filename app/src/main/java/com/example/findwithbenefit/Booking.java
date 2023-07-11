package com.example.findwithbenefit;

public class Booking {
    public String name, status, user ;

    public Booking(){

    }

    public Booking(String name, String status, String user) {
        this.name = name;
        this.status = status;
        this.user = user;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.status = user;
    }

}
