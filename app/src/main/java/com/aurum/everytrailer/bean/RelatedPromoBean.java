package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/30/2015.
 */
public class RelatedPromoBean {

    private String title, trailerId, movieId, videoImageURL, videoURL, videoViewCount, userLike, totalLikeCount;

    public RelatedPromoBean(String title, String trailerId, String movieId, String videoImageURL, String videoURL, String videoViewCount, String userLike, String totalLikeCount) {
        this.title = title;
        this.trailerId = trailerId;
        this.movieId = movieId;
        this.videoImageURL = videoImageURL;
        this.videoURL = videoURL;
        this.videoViewCount = videoViewCount;
        this.userLike = userLike;
        this.totalLikeCount = totalLikeCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getVideoImageURL() {
        return videoImageURL;
    }

    public void setVideoImageURL(String videoImageURL) {
        this.videoImageURL = videoImageURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getVideoViewCount() {
        return videoViewCount;
    }

    public void setVideoViewCount(String videoViewCount) {
        this.videoViewCount = videoViewCount;
    }

    public String getUserLike() {
        return userLike;
    }

    public void setUserLike(String userLike) {
        this.userLike = userLike;
    }

    public String getTotalLikeCount() {
        return totalLikeCount;
    }

    public void setTotalLikeCount(String totalLikeCount) {
        this.totalLikeCount = totalLikeCount;
    }
}
