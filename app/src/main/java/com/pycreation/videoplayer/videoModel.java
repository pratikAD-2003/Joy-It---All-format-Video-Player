package com.pycreation.videoplayer;

public class videoModel {
    String size;
    String path;
    String duration;
    String date;
    String videoUri;
    String folderId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    int type;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public videoModel(String size, String path, String duration, String date, String videoUri, String folderId, String folderName, String name,int type) {
        this.type = type;
        this.size = size;
        this.path = path;
        this.duration = duration;
        this.date = date;
        this.videoUri = videoUri;
        this.folderId = folderId;
        this.folderName = folderName;
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    String folderName;
}
