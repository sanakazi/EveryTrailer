package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/25/2015.
 */
public class AllMoviesBean {

    private String languageId;
    private String language;
    private String image;

    public AllMoviesBean(String languageId, String language, String image) {
        this.languageId = languageId;
        this.language = language;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
