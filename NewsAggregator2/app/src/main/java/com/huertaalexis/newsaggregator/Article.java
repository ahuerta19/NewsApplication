package com.huertaalexis.newsaggregator;

public class Article {

    private final String title;
    private final String date;
    private final String author;
    private final String photo;
    private final String desc;
    private final String num;
    private final String link;

    public Article(String title, String date, String author, String photo, String desc, String num, String link) {
        this.title = title;
        this.date = date;
        this.author = author;
        this.photo = photo;
        this.desc = desc;
        this.num = num;
        this.link = link;
    }
    public String getLink(){
        return link;
    }
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getAuthor(){
        return author;
    }
    public String getPhoto(){
        return photo;
    }
    public String getDesc(){
        return desc;
    }
    public String getNum(){
        return num;
    }
}
