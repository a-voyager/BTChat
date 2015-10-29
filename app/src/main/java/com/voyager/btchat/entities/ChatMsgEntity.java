package com.voyager.btchat.entities;

/**
 * Created by wuhaojie on 2015/10/29.
 */
public class ChatMsgEntity {
    private String name;
    private String date;
    private String text;
    private boolean msgType = true;

    public ChatMsgEntity() {

    }

    public ChatMsgEntity(String name, String date, String text, boolean msgType) {
        this.name = name;
        this.date = date;
        this.text = text;
        this.msgType = msgType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMsgType() {
        return msgType;
    }

    public void setMsgType(boolean msgType) {
        this.msgType = msgType;
    }
}
