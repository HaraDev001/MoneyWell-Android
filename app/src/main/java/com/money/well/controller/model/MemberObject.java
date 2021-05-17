package com.money.well.controller.model;

public class MemberObject {
    private int memberId;
    private String name;
    private String photoUrl;
    private String memberRole;

    public MemberObject() {

    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getName(){return  name;}
    public void setName(String name){this.name = name;}

    public String getPhotoUrl() { return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}

    public String getMemberRole(){ return this.memberRole;}
    public void setMemberRole(String memberRole){this.memberRole = memberRole;}

}
