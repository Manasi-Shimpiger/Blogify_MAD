package com.example.blogify;

public class Posts {
    private String ID, Date, imageURI, Content, Time, Title, Owner, OwnerID;

    public Posts() {
    }


    public Posts(String ID, String date, String imageURI, String content, String time, String title, String owner, String ownerID) {
        this.ID = ID;
        Date = date;
        this.imageURI = imageURI;
        Content = content;
        Time = time;
        Title = title;
        this.OwnerID = ownerID;
        Owner = owner;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }
}
