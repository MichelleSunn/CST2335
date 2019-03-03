package com.example.androidlabs;

public class Message {
    private String message;
    private long id;
    private boolean isSend;



    Message(String message, boolean isSend, long id) {
        this.message = message;
        this.isSend = isSend;
        this.id = id;

    }

    public String getMessage() {
        return message;
    }

    public Boolean getIsSend() {
        return isSend;
    }

    public long getId() {
        return id;
    }
}
