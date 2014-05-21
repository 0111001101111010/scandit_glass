package com.xtuple.packworkflow.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
//import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.Gesture;
import com.loopj.android.http.*;
import com.xtuple.XtupleRestClient;

import org.json.*;
public class MainActivity extends Activity {

// create variable instances
	public List<String> barcodes  = new ArrayList<String>();
	public List<String> descriptions  = new ArrayList<String>();
	public List<String> uuids  = new ArrayList<String>();
	public String obj;
    private TextToSpeech mSpeech;
	
    private GestureDetector mGestureDetector;
	private static final int SCANDIT_CODE_REQUEST =2;
	private static final int SPEECH_REQUEST = 1;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//keeps the camera on
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		//javascript that
		final Activity that = this;
	    makeCard(this,"Hello","World");
		makeRequest(that);
		
		//speech
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
             mSpeech.speak("Welcome to ex two pull", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        //gesture detector 
		mGestureDetector = createGestureDetector(this);
	}
	
	private void makeRequest(final Activity that){
        WebRequest.getOrders(new AsyncHttpResponseHandler() {
        	
            @Override
            public void onStart() {
                // Initiated the request
            }

            @Override
            public void onSuccess(String response) {
                // Successfully got a response
            }

            @Override
            public void onFailure(Throwable e, String response) {
                // Response failed :(
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)\
            }
        });

	};
	
	void makeCard(Activity that, String body, String footer){
		Card card1 = new Card(that);
        card1.setImageLayout(Card.ImageLayout.FULL);
		card1.setText(body);
		card1.setFootnote(footer);
		View card1View = card1.getView();
		setContentView(card1View);
  
	};
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
		try {
			String check = XtupleRestClient.onPacklist(obj,contents);
			//its on the list
			if(!check.equals("NOPE")){
                //startActivity(new Intent(this, StopStopWatchActivity.class));
    				//selected
            RequestParams params = new RequestParams();
            params.put("uuid", check);
            params.put("quantity", "1");
    		makeCard(this,"Issued","");
    		
          final Activity that = this;
          WebRequest.issueOrder(new AsyncHttpResponseHandler() {
        	            @Override
        	            public void onStart() {
        	                // Initiated the request
        	            	makeCard(that,"Sending Request...","xTuple");
        	            	//wait to make sure request was processed 
        	                Handler handler = new Handler(); 
        	                handler.postDelayed(new Runnable() { 
        	                     public void run() { 
        	                    	 //random waiting
        	                     } 
        	                }, 2000); 
        	            }

        	            @Override
        	            public void onSuccess(String response) {
        	                // Successfully got a response
        	            }

        	            @Override
        	            public void onFailure(Throwable e, String response) {
        	                // Response failed :(
        	            }

        	            @Override
        	            public void onFinish() {
        	            	//refresh the list and grab next item
        	            	getIssuable(that);
        	            }
        	        }, params);
                Log.v("@@@@","IssueToShipping");				
			}
			else{
			//it isn't
    	        Card newCard = new Card(this);
    	        newCard.setImageLayout(Card.ImageLayout.FULL);
    			newCard.setText("That line item is not on this order, please check again");
				newCard.setFootnote("xTuple");
				View card1View1 = newCard.getView();
				setContentView(card1View1);	
			}
			} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);	
		}
	    }
		else if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
	        List<String> results = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String spokenText = results.get(0);
	        makeCard(this,spokenText,"foo");
	        //issue line
	        //issue partial
	        	//quantity
	        //scan

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
                	displaySpeechRecognizer();
                	
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
    @Override
    public void onDestroy() {
        mSpeech.shutdown();
        mSpeech = null;

        super.onDestroy();
    }
    //recognize speech
	private void displaySpeechRecognizer() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    startActivityForResult(intent, SPEECH_REQUEST);
	}
 
	
	private void getIssuable(final Activity that){
        WebRequest.getOrders(new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
        		makeCard(that,"Loading...","xTuple");
            }

            @Override
            public void onSuccess(String response) {
                // Successfully got a response
            	obj = response;
            	if (!"No Issuable Items!".equals(response)){
                    try{
                        barcodes  = XtupleRestClient.getIssueToShippingAtShipping(response);
                        descriptions = XtupleRestClient.getIssueToShippingDescriptions(response); 
                        uuids = XtupleRestClient.getOrderUUIDs(response);
                        int size = XtupleRestClient.numLineItems(response);
                        double weight = XtupleRestClient.getWeight(response);
                        int position= XtupleRestClient.getIssuetoShippingLinePosition(response, uuids.get(0));
                        //add them
                        String footer = "Total Weight: " + weight + "- Line items "+ position + " of total " + size;
		        		makeCard(that,descriptions.get(0),footer);
		        		
		                mSpeech.speak("line items are available to be issued.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    catch (Exception e) {
         				// TODO Auto-generated catch block
            			throw new RuntimeException(e);
         				
         			}
                 }
                 else{
         		    //mlsText.add("No Issuable Items!");
		        	makeCard(that,"No More Issuable Orders Exiting!","xTuple");
	                mSpeech.speak("No More Issuable Orders Exiting!", TextToSpeech.QUEUE_FLUSH, null);
	                //exit the app
	                Handler handler = new Handler(); 
	                handler.postDelayed(new Runnable() { 
	                     public void run() { 
	                          finish();
	                     } 
	                }, 5000); 
                 }		
            	
            }

            @Override
            public void onFailure(Throwable e, String response) {
                // Response failed :(
            	try {
					throw e;
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
               //	Log.d("fail",response);
            }

            @Override
            public void onFinish() {
               	Log.d("finish","");
                // Completed the request (either success or failure)
            }
        });		
	}
}
