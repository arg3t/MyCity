package gq.yigit.mycity.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

	private List<imageListener> listeners = new ArrayList<>();


	@Override
	protected Bitmap doInBackground(String... URL) {
		try {
			String imageURL = URL[0];
			HttpUriRequest request = new HttpGet(imageURL.toString());
			HttpClient httpClient = AcceptAllSSLSocketFactory.getNewHttpClient();
			HttpResponse response = httpClient.execute(request);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				byte[] bytes = EntityUtils.toByteArray(entity);

				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
						bytes.length);
				return bitmap;
			}else{
				return null;
			}
		}catch (Exception e){
			Log.e("[ERROR]","Download failed because " + e.getMessage());
			return null;
		}

	}

	@Override
	protected void onPostExecute(Bitmap result) {
		for (imageListener hl : listeners)
			hl.imageDownloaded(result);
	}

	public void addListener(imageListener toAdd) {
		listeners.add(toAdd);
	}
	
	public interface imageListener {
	    public void imageDownloaded(Bitmap img);
    }

}
