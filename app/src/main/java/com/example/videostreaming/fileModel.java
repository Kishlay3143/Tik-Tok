package com.example.videostreaming;

public class fileModel {
    String title,vUrl;

    public fileModel() {
    }

    public fileModel(final String title, final String vUrl) {
        this.title = title;
        this.vUrl = vUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getvUrl() {
        return vUrl;
    }

    public void setvUrl(final String vUrl) {
        this.vUrl = vUrl;
    }
}
