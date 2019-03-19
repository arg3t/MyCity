package gq.yigit.mycity.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

	private List<imageListener> listeners = new ArrayList<>();


	@Override
	protected Bitmap doInBackground(String... URL) {

		String imageURL = URL[0];
		Log.d("[BOOKMARK]",imageURL);

		Bitmap bitmap = null;
		try {
			// Download Image from URL
			InputStream input = new java.net.URL(imageURL).openStream();
			// Decode Bitmap
			bitmap = BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		for (imageListener hl : listeners)
			hl.imageDownloaded(result);
	}

	public void addListener(imageListener toAdd) {
		listeners.add(toAdd);
	}
}
