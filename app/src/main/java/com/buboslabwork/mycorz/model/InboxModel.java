package com.buboslabwork.mycorz.model;

/**
 * Created by Admin on 8/17/2016.
 */
public class InboxModel {
    private String id;
    private String sender_userid;
    private String received_userid;
    private String sender_name;
    private String sender_namatoko;
    private String received_namatoko;
    private String received_name;
    private String message;
    private String tanggal;
    private String profileSender;
    private String profileReceiver;

    public String getid() {
        return this.id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getsender_userid() {
        return this.sender_userid;
    }

    public void setsender_userid(String sender_userid) {
        this.sender_userid = sender_userid;
    }

    public String getreceived_userid() {
        return this.received_userid;
    }

    public void setreceived_userid(String received_userid) {
        this.received_userid = received_userid;
    }

    public String getmessage() {
        return this.message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getsender_name() {
        return this.sender_name;
    }

    public void setsender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getreceived_name() {
        return this.received_name;
    }

    public void setreceived_name(String received_name) {
        this.received_name = received_name;
    }

    public String gettanggal() {
        return this.tanggal;
    }

    public void settanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getsender_namatoko() {
        return this.sender_namatoko;
    }

    public void setsender_namatoko(String sender_namatoko) {
        this.sender_namatoko = sender_namatoko;
    }

    public String getreceived_namatoko() {
        return this.received_namatoko;
    }

    public void setreceived_namatoko(String received_namatoko) {
        this.received_namatoko = received_namatoko;
    }

    public String getprofileSender() {
        return this.profileSender;
    }

    public void setprofileSender(String profileSender) {
        this.profileSender = profileSender;
    }

    public String getprofileReceiver() {
        return this.profileReceiver;
    }

    public void setprofileReceiver(String profileReceiver) {
        this.profileReceiver = profileReceiver;
    }
}
