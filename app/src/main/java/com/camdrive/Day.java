package com.camdrive;

/**
 * Created by root on 08.09.16.
 */
public class Day {
    String id;
    boolean records;
    boolean enable;
    String text;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRecords(boolean records) {
        this.records = records;
    }

    public void setText(String text) {
        this.text = text;
    }
}
