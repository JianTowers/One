package com.example.one.tts.audio.entity;

public class ContentRes {
    private int begin_time ;
    private int end_time ;
    private int sentence_index ;
    private int frame_index ;

    private String text ;

    public ContentRes() {
    }

    public ContentRes(int begin_time,int end_time,int sentence_index,int frame_index, String text) {
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.sentence_index = sentence_index;
        this.frame_index = frame_index;
        this.text = text;

    }

    public int getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(int begin_time) {
        this.begin_time = begin_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSentence_index() {
        return sentence_index;
    }

    public void setSentence_index(int sentence_index) {
        this.sentence_index = sentence_index;
    }

    public int getFrame_index() {
        return frame_index;
    }

    public void setFrame_index(int frame_index) {
        this.frame_index = frame_index;
    }
}
