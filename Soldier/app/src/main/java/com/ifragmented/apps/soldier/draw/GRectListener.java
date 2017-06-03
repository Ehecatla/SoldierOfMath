package com.ifragmented.apps.soldier.draw;

/**
 * Created by Ella on 2017-01-22. Used with ChallengeView to call out when rectangle answer objects
 * has left view area and when color showing of feedback to answer has been done callback.
 */

public interface GRectListener {

    /**
     * When color displaying of feedback to answer has been done, call this method.
     */
    void onColorAnimationDone();

    /**
     * When rectangles displaying answers in ChallengeView has left view area, call this method to
     * ensure that listener gets informed about it.
     */
    void onGRectsOutOfView();
}
