package com.ifragmented.apps.soldier.data;

/**
 * Created by Ella on 2017-02-05.
 */

public class Score {

    private String userName;
    private String storyTitle;
    private int points;

    public Score(String userName, String storyName, int points) {
        this.userName = userName;
        this.storyTitle = storyName;
        this.points = points;
    }

    public String getUserName() {
        return userName;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public int getPoints() {
        return points;
    }
}
