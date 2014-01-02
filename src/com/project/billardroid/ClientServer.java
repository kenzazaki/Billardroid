package com.project.billardroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ClientServer extends AsyncTask<String, Void, JSONObject> {

	private HttpClient httpclient;
	private HttpPost httppost;
	private HttpResponse httpresponse;
	private HttpEntity httpentity;
	private InputStream inputstream;
	private JSONArray JSONResponse;
	
	ClientServer(String urlScript) {
		try{
	        httpclient = new DefaultHttpClient();
	        httppost = new HttpPost(urlScript);

		}
		catch(Exception e){
	        Log.e("log_tag", "Error in http connection "+e.toString());
		}
	}
	
	public void executeRequest() {
        try {
        	httpresponse = httpclient.execute(httppost);
            httpentity = httpresponse.getEntity();
            inputstream = httpentity.getContent();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http execution "+e.toString());
		}
	}
	
	public void setPostParameters(ArrayList<NameValuePair> nameValuePairs) {
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			Log.e("log_tag", "Error in http POST parameters "+e.toString());
		}
	}
	
	public JSONObject toJSONObject() {
		try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        inputstream.close();
	 
	        String result = sb.toString();
	        
	        JSONObject jObject = new JSONObject(result);
	        
	        return jObject;
		}
		catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
	        return null;
		}
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		executeRequest();
		return toJSONObject();
	}	
}
