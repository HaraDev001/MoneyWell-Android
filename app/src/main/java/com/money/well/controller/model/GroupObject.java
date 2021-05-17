package com.money.well.controller.model;

public class GroupObject {
    private int id;
    private String name;
    private String adminName;
    private String photoUrl;
    private int noticeCount;
    private boolean joinStatus;

    public GroupObject() {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName(){return  name;}
    public void setName(String name){this.name = name;}

    public String getPhotoUrl() { return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}

    public int getNoticeCount() {return this.noticeCount;}
    public void setNoticeCount(int noticeCount){this.noticeCount = noticeCount;}

    public boolean getJoinStatus(){return this.joinStatus;}
    public void setJoinStatus(boolean joinStatus){this.joinStatus = joinStatus;}

    public String getAdminName() {return this.adminName;}
    public void setAdminName(String adminName){this.adminName = adminName;}
}
