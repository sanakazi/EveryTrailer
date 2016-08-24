package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/23/2015.
 */
public class NewsBean {

    private String image;
    private String news;
    private String author;
    private String time;
    private String readMore;

    public NewsBean(String image, String news, String author, String time, String readMore) {
        this.image = image;
        this.news = news;
        this.author = author;
        this.time = time;
        this.readMore = readMore;
    }

    public String getReadMore() {
        return readMore;
    }

    public void setReadMore(String readMore) {
        this.readMore = readMore;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
