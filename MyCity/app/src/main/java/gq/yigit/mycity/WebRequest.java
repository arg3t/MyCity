package gq.yigit.mycity;

import android.os.AsyncTask;
import android.util.Log;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class WebRequest extends AsyncTask<Void,Void,String> {
	private String url;
	private HashMap<String,String> request_content;
	private boolean request_type;//True = GET, False = POST

	private HttpClient client = HttpClientBuilder.create().build();
	private HttpGet get_request;
	private HttpPost post_request;
	private HttpResponse response;
	private List<responseListener> listeners = new ArrayList<>();


	WebRequest(String url, boolean request_type, HashMap<String,String> request_content){

		this.url = url;
		this.request_content = request_content;
		this.request_type = request_type;

		if(request_type){

			Iterator iterator = request_content.entrySet().iterator();
			this.url += "?";

			while(iterator.hasNext()){
				Map.Entry pair = (Map.Entry)iterator.next();
				this.url += pair.getKey() + "=" + pair.getValue() + "&";
				iterator.remove();
			}
			get_request = new HttpGet(this.url);
		}else{
			post_request = new HttpPost(this.url);
		}
	}

	protected String doInBackground(Void... params){
		if(!request_type){
			try {
				post_request = new HttpPost(url);
				List<NameValuePair> pairs = new ArrayList<>(request_content.size());

				Iterator iterator = request_content.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry pair = (Map.Entry) iterator.next();
					pairs.add(new BasicNameValuePair(pair.getKey().toString(), pair.getValue().toString()));
					iterator.remove();
				}
				post_request.setEntity(new UrlEncodedFormEntity(pairs));
				response = client.execute(post_request);
			}catch (Exception e){
				Log.e("[ERROR](request:67): ", e.toString());
			}
		}else{
			try {
				response = client.execute(get_request);
			}catch (Exception e){
				Log.e("[ERROR](request:74): ", e.toString());
			}
		}
		try {
			BufferedReader rd = new BufferedReader
					(new InputStreamReader(
							response.getEntity().getContent()));
			String line = "";
			String temp;
			while ((temp = rd.readLine()) != null) {
				line += temp;
			}
			return line;
		}catch(Exception e){
			Log.e("[ERROR](request:87): ", e.toString());
		}
		return "Error";
	}
	protected void onPostExecute(String result){
		for (responseListener hl : listeners)
			hl.receivedResponse(!result.equals("Error"),result);
	}

	public void addListener(responseListener toAdd) {
		listeners.add(toAdd);
	}

}