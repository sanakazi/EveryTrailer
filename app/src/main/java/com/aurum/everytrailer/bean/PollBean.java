package com.aurum.everytrailer.bean;

/**
 * Created by VarunBarve on 12/16/2015.
 */
public class PollBean {

    private String pollName, pollID, pollCount, userPoll;

    public PollBean(String pollName, String pollID, String pollCount, String userPoll) {
        this.pollName = pollName;
        this.pollID = pollID;
        this.pollCount = pollCount;
        this.userPoll = userPoll;
    }

    public String getPollName() {
        return pollName;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public String getPollID() {
        return pollID;
    }

    public void setPollID(String pollID) {
        this.pollID = pollID;
    }

    public String getPollCount() {
        return pollCount;
    }

    public void setPollCount(String pollCount) {
        this.pollCount = pollCount;
    }

    public String getUserPoll() {
        return userPoll;
    }

    public void setUserPoll(String userPoll) {
        this.userPoll = userPoll;
    }
}
