package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/23/2015.
 */
public class ComingSoonBean {

    private String id;
    private String views;
    private String likePercentage;
    private String image;
    private String trailerTitle;
    private String movieName;
    private String trailerType;

    public ComingSoonBean(String id, String views, String likePercentage, String image, String trailerTitle, String movieName, String trailerType) {
        this.id = id;
        this.views = views;
        this.likePercentage = likePercentage;
        this.image = image;
        this.trailerTitle = trailerTitle;
        this.movieName = movieName;
        this.trailerType = trailerType;
    }

    public String getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(String trailerType) {
        this.trailerType = trailerType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getLikePercentage() {
        return likePercentage;
    }

    public void setLikePercentage(String likePercentage) {
        this.likePercentage = likePercentage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
