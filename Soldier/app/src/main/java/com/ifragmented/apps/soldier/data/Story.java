package com.ifragmented.apps.soldier.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ella on 2017-01-15.
 */

public class Story implements Serializable {

    private final String storyTitle;
    private final Dialogue intro;
    private final Dialogue outroLost;
    private final Dialogue outroWon;
    private final ArrayList<Dialogue>storyDialogues;
    private final String background;

    public Story(String storyTitle, Dialogue intro, Dialogue outroLost, Dialogue outroWon, ArrayList<Dialogue> storyDialogues, String background) {
        this.storyTitle = storyTitle;
        this.intro = intro;
        this.outroLost = outroLost;
        this.outroWon = outroWon;
        this.storyDialogues = storyDialogues;
        this.background = background;
    }

    public Dialogue getOutroWon() {
        return outroWon;
    }

    public Dialogue getOutroLost() {
        return outroLost;
    }

    public Dialogue getIntro() {
        return intro;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public ArrayList<Dialogue> getStoryDialogues() {
        return storyDialogues;
    }

    public Dialogue getStoryDialogue(int index) {
        return storyDialogues.get(index);
    }

    public String getBackground() {
        return background;
    }

    @Override
    public String toString() {
        String storyPrint ="Story: " + storyTitle + "\n";
        return storyPrint;
    }
}
