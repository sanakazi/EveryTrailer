package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/26/2015.
 */
public class ReviewBean {

    private String ratingsount;
    private String title;
    private String description;
    private String likecount;
    private String name;
    private String date;
    private String ratingId;
    private String userlike;
    private String profilePic;

    public ReviewBean(String title, String description, String likecount, String name, String date, String ratingsount, String ratingId, String userlike, String profilePic) {
        this.ratingsount = ratingsount;
        this.title = title;
        this.description = description;
        this.likecount = likecount;
        this.name = name;
        this.date = date;
        this.ratingId = ratingId;
        this.userlike = userlike;
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserlike() {
        return userlike;
    }

    public void setUserlike(String userlike) {
        this.userlike = userlike;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getRatingsount() {
        return ratingsount;
    }

    public void setRatingsount(String ratingsount) {
        this.ratingsount = ratingsount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
