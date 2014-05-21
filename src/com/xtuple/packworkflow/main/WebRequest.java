package com.xtuple.packworkflow.main;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;


final class WebRequest extends Activity {
	
	//#HOME
	//public   String server = "http://192.168.0.194:8081/";
	//#WORK
	public static String server = "http://192.168.10.3:8081/";
	// #TRAVIS
	//public static String server = "http://10.0.7.142:8081/";
    private static AsyncHttpClient mClient = new AsyncHttpClient();
    //get stuff
    public static void getOrders(AsyncHttpResponseHandler handler) {
        mClient.get(server+"PackWorkflow", handler);
    }
    //send client
    public static void issueOrder(AsyncHttpResponseHandler handler, RequestParams params) {
        mClient.post(server+"dispatchIssue", params,handler);
    }
}