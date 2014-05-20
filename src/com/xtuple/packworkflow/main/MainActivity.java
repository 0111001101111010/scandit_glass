package com.xtuple.packworkflow.main;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.Gesture;


public class MainActivity extends Activity {

// create variable instances

    private GestureDetector mGestureDetector;
	private static final int SCANDIT_CODE_REQUEST =2;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//keeps the camera on
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		Card card1 = new Card(this);
		card1.setText("Welcome to PackWorkflow!!");
		card1.setFootnote("xTuple");
		View card1View = card1.getView();
		setContentView(card1View);
		mGestureDetector = createGestureDetector(this);
	}
/**
 * Boiler plate google code
 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_CAMERA) {
	        // Stop the preview and release the camera.
	        // Execute your logic as quickly as possible
	        // so the capture happens quickly.
	        return false;
	    } else {
	        return super.onKeyDown(keyCode, event);
	    }
	}

	/*** boiler plate google code
	 * https://developers.google.com/glass/develop/gdk/media-camera/camera
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    // Re-acquire the camera and start the preview.
	}

	public void scanBarcode(){
 Intent i = new Intent(getApplicationContext(), ScanditSDKDemoSimple.class);
		i.putExtra("VariableParameter","Value");
		startActivityForResult(i, SCANDIT_CODE_REQUEST);
		Log.d("@@@@", "Asking for Barcode");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SCANDIT_CODE_REQUEST && resultCode == RESULT_OK){
       		Log.d("before","SCANDIT_CODE_REQUEST");
    		String contents = data.getStringExtra("SCAN_RESULT");
           	Log.d("after","SCANDIT_CODE_REQUEST");
            Card newCard = new Card(this);
            newCard.setImageLayout(Card.ImageLayout.FULL);
    		newCard.setText(contents);
    		newCard.setFootnote("xTuple");
    		View card1View1 = newCard.getView();
    		setContentView(card1View1);
	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * end of boiler plate
	 */
    private GestureDetector createGestureDetector(Context context) {
    GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                	Log.d("@@@@", "TAP");
									scanBarcode();
                    // do something on tap
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                	Log.d("@@@@", "2-TAP");
                	return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                	Log.d("@@@@", "SWIPE_RIGHT");
                	return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                	Log.d("@@@@", "SWIPE_LEFT");
                	return true;
                }
                return false;
            }
        });
        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
              // do something on finger count changes
            }
        });
        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {

            	return true;
                // do something on scrolling
            }
        });
        return gestureDetector;
    }

    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}
