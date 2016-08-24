package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 11/30/2015.
 */
public class LinkBean {

    private String link, linkname;

    public LinkBean(String link, String linkname) {
        this.link = link;
        this.linkname = linkname;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkname() {
        return linkname;
    }

    public void setLinkname(String linkname) {
        this.linkname = linkname;
    }
}
