package com.xtuple.packworkflow.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.loopj.android.http.*;
import com.xtuple.XtupleRestClient;

import org.json.*;
public class MainActivity extends Activity {

// create variable instances
	public List<String> barcodes  = new ArrayList<String>();
	public List<String> descriptions  = new ArrayList<String>();
	public List<String> uuids  = new ArrayList<String>();
	public String obj;
	
    private GestureDetector mGestureDetector;
	private static final int SCANDIT_CODE_REQUEST =2;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//keeps the camera on
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		//javascript that
		final Activity that = this;
	    makeCard(this,"Hello","World");
		makeRequest(that);
		
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
            
            Card newCard = new Card(this);
            newCard.setImageLayout(Card.ImageLayout.FULL);
    		newCard.setText("Issued");
    		newCard.setFootnote("xTuple");
    		View card1View1 = newCard.getView();
    		setContentView(card1View1);	
    		
               	Log.d("start","");
           //WebRequest.issueOrder(new AsyncHttpResponseHandler(),params);
            final Activity foo = this;
          WebRequest.issueOrder(new AsyncHttpResponseHandler() {
        	            @Override
        	            public void onStart() {
        	                // Initiated the request
        	                Card newCard = new Card(foo);
        	                newCard.setImageLayout(Card.ImageLayout.FULL);
        	        		newCard.setText("Issuing....");
        	        		newCard.setFootnote("xTuple");
        	        		View card1View1 = newCard.getView();
        	        		setContentView(card1View1);
        		               	Log.d("start","");
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
        	                // Completed the request (either success or failure)
        		    	        WebRequest.getOrders(new AsyncHttpResponseHandler() {
        		    	            @Override
        		    	            public void onStart() {
        		    	                // Initiated the request

        		    	                Card newCard = new Card(foo);
        		    	                newCard.setImageLayout(Card.ImageLayout.FULL);
        		    	        		newCard.setText("Loading Next Line Item....");
        		    	        		newCard.setFootnote("xTuple");
        		    	        		View card1View1 = newCard.getView();
        		    	        		setContentView(card1View1);
        		    		               	Log.d("start","");
        		    		                mSpeech.speak("Loading Next Line Item....", TextToSpeech.QUEUE_FLUSH, null);
        		    	            }

        		    	            @Override
        		    	            public void onSuccess(String response) {
        		    	                // Successfully got a response
        		    	            	obj = response;
        		    	            	if (!"No Issuable Items!".equals(response)){
        		    	                    try{
        		    	                        barcodes  = XtupleRestClient.getIssueToShippingAtShipping(response);
        		    	                        descriptions = XtupleRestClient.getIssueToShippingDescriptions(response);       
        		    	                      // uuids = XtupleRestClient.getOrderUUIDs();
        		    	                        uuids = XtupleRestClient.getOrderUUIDs(response);
        		    	                                                    
        		    	                        //add them
        		    			                Card newCard = new Card(foo);
        		    			                newCard.setImageLayout(Card.ImageLayout.FULL);
        		    		                    newCard.setText(descriptions.get(0));
        		    			        		newCard.setFootnote("xTuple");
        		    			        		View card1View1 = newCard.getView();
        		    			        		setContentView(card1View1);
        		    			        		
        		    			               mSpeech.speak("Next Line Item Ready", TextToSpeech.QUEUE_FLUSH, null);
        		    	                    }
        		    	                    catch (Exception e) {
        		    	         				// TODO Auto-generated catch block
        		    	            			throw new RuntimeException(e);
        		    	         				
        		    	         			}
        		    	                 }
        		    	                 else{
        		    	         		    //mlsText.add("No Issuable Items!");
        		    		                Card newCard = new Card(foo);
        		    		                newCard.setImageLayout(Card.ImageLayout.FULL);
        		    	                     newCard.setText("Order Complete, No more Issuable Items!");
        		    		        		newCard.setFootnote("xTuple");
        		    		        		View card1View1 = newCard.getView();
        		    		        		setContentView(card1View1);
        		    		                mSpeech.speak("Order Complete, No more Issuable Items!", TextToSpeech.QUEUE_FLUSH, null);
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
    

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode,
//	        Intent data) {
//		if (requestCode == SCANDIT_CODE_REQUEST && resultCode == RESULT_OK){
//           //scandit activity
// 
//	    } 
//	    super.onActivityResult(requestCode, resultCode, data);
//	}

}
