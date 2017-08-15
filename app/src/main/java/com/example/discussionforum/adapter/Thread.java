package com.example.discussionforum.adapter;

/**
 * Created by neetu on 15/8/17.
 */

public class Thread {
    private String id , topic , body , author , date;

    public Thread(){

    }

    public Thread(String id , String topic , String body , String author , String date){
        this.id = id;
        this.topic = topic;
        this.body = body;
        this.author = author;
        this.date = date;
    }

    public String getTopic(){
        return topic;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public String getBody(){
        return body;
    }

    public void setBody(){
        this.body = body;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(){
        this.author = author;
    }

    public String getDate(){
        return date;
    }

    public void setDate(){
        this.date = date;
    }
}
