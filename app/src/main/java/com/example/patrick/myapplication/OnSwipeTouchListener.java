package com.example.patrick.myapplication;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
/**
 * The OnSwipeTouchListener class extends the OnTouchListener
 * class to allow it to recognise when the user has swiped
 * across the object. This is used in the DateScreen class
 * to allow the user to swipe between days.
 *
 */
class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener (Context context){
        //Set up the GestureListener to listen for swipes
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Use gestureDetector onTouchEvent method.
        return gestureDetector.onTouchEvent(event);
    }

    //GestureListener analyses whatever gesture the user makes and
    //uses the method corresponding to it.
    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        //Notified when a tap occurs with the Down MotionEvent that triggered it.
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        //Takes in the motionevents and determines whether it was a swipe,
        //and what it's direction was.
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                //Find the difference between the start and end of the motion
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                //If the difference was greater on the X axis, then the move was horizontal
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                //If the difference on the Y axis, then the move was horizontal
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            //If the move didn't satisfy any of the conditions,
            //return false as it wasn't a swipe
            return result;
        }
    }

    //Methods to be set when an OnSwipeTouchListener object is created.
    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    private void onSwipeTop() {
    }

    private void onSwipeBottom() {
    }
}

