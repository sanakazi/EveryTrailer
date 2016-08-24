package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/18/2015.
 */
public class TopBean {

    private String views;
    private String likePercentage;
    private String image;
    private String movieName;
    private String id;
    private String trailertitle;
    private String trailerType;

    public TopBean(String id, String views, String likePercentage, String image, String trailertitle, String movieName, String trailerType) {
        this.views = views;
        this.likePercentage = likePercentage;
        this.image = image;
        this.movieName = movieName;
        this.id = id;
        this.trailertitle = trailertitle;
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

    public String getTrailertitle() {
        return trailertitle;
    }

    public void setTrailertitle(String trailertitle) {
        this.trailertitle = trailertitle;
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
