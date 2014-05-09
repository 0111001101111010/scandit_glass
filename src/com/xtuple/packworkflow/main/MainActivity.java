package com.xtuple.packworkflow.main;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;


public class MainActivity extends Activity {

// create variable instances

	private static final String TAG = MainActivity.class.getSimpleName();
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
		scanBarcode();

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
	private static final int TAKE_PICTURE_REQUEST = 1;

	public void scanBarcode(){
 Intent i = new Intent(getApplicationContext(), ScanditSDKDemoSimple.class);
		i.putExtra("VariableParameter","Value");
		startActivityForResult(i, SCANDIT_CODE_REQUEST);
		Log.d("@@@@", "Asking for Barcode");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
//	        String picturePath = data.getStringExtra(
//	                CameraManager.EXTRA_PICTURE_FILE_PATH);
//	        processPictureWhenReady(picturePath);
	    }
	    else if (requestCode == SCANDIT_CODE_REQUEST && resultCode == RESULT_OK){
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
}
