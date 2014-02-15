package org.what.cooking;

import android.util.Log;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.what.cooking.Model.Response;
import org.what.cooking.http.AsyncHttpClient;
import org.what.cooking.http.AsyncHttpResponseHandler;
import org.what.cooking.http.JsonHttpResponseHandler;
import org.what.cooking.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

public class DataTransfer extends Observable {
//    private String SERVER_URL = "http://clients.reigndesign.com/rdapp/data.json";
    private String SERVER_URL = "http://158.130.164.202/upload";
	private static DataTransfer instance=null;
    private DataTransfer dataTransferInstance;
	private Response[] responses;

	private DataTransfer()
	{
		dataTransferInstance = this;
	}
	
	public static DataTransfer getInstance()
	{
		if(instance==null)
		{
			instance = new DataTransfer();
		}
		return instance;
	}

    public void uploadImage(String photoPath) throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("file", new File(photoPath));
        AsyncHttpClient client = new AsyncHttpClient(5000);
        client.post(SERVER_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.d("DataTransfer", "Upload started!!!!");
            }

            @Override
            public void onFinish() {
                Log.d("DataTransfer","Upload finished");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("DataTransfer", "Upload success!!!!");
                dataTransferInstance.setChanged();
                dataTransferInstance.notifyObservers(new String("UPsuccess"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("DataTransfer", "Upload  failed!!!! "+responseBody );
                dataTransferInstance.setChanged();
                dataTransferInstance.notifyObservers(new String("UPfailure"));
            }
        });

	}
	
	public void jsonParsing(JSONObject response)
	{
		try
		{
            JSONArray responses_array = null;
            try {
                responses_array = response.getJSONArray("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            responses = new Response[responses_array.length()];

			for(int i=0;i<responses_array.length();i++)
			{
				response = responses_array.getJSONObject(i);
                try {
                    responses[i] = new Response(response.getString("url"),new URL(response.getString("title")));
                } catch (MalformedURLException e) {
                    Log.d("DataTransfer","Malformed URL");
                }
            }
			this.setChanged();
			this.notifyObservers(responses);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void getData()
	{
		AsyncHttpClient client = new AsyncHttpClient();
			client.get(SERVER_URL, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DataTransfer","get success!");
                    instance.jsonParsing(response);
                    dataTransferInstance.setChanged();
                    dataTransferInstance.notifyObservers(responses);
                }

                @Override
			    public void onFinish()
			    {
			    	Log.d("DataTransfer","get is finished");
			    }
			    @Override
			    public void onStart()
			    {
			    	Log.d("DataTransfer","get is started");
			    }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DataTransfer","get is failed");
                    dataTransferInstance.setChanged();
                    dataTransferInstance.notifyObservers("");
                }
            });

	}

}

