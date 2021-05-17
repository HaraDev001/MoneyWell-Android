package com.money.well.controller.model;

public class NationObject {
    private int id;
    private String name;
    private String phonePrefix;
    private boolean selectStatus;

    public NationObject() {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName(){return  name;}
    public void setName(String name){this.name = name;}

    public String getPhonePrefix() { return phonePrefix; }
    public void setPhonePrefix(String phonePrefix) { this.phonePrefix = phonePrefix; }

    public boolean getSelectStatus(){ return  selectStatus;}
    public void setSelectStatus(boolean status){
        this.selectStatus = status;
    }
}
