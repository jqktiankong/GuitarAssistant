package com.example.jqk.guitarassistant.event;

public class MessageEvent {
    public int mark;
    public final String message;
    public final int state;

    public MessageEvent(int mark, String message, int state) {
        this.mark = mark;
        this.message = message;
        this.state = state;
    }

    public int getMark() {
        return mark;
    }

    public String getMessage() {
        return message;
    }

    public int getState() {
        return state;
    }
}
