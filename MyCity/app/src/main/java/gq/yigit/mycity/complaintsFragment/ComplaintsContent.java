package gq.yigit.mycity.complaintsFragment;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintsContent {

	public static final List<ComplaintItem> ITEMS = new ArrayList<ComplaintItem>();

	public static final Map<String, ComplaintItem> ITEM_MAP = new HashMap<String, ComplaintItem>();



	public static void addItem(ComplaintItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static ComplaintItem createComplaintItem(JSONObject obj,Geocoder geocoder, ArrayList<Bitmap> stat_imgs) throws JSONException{
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(
					Double.parseDouble(obj.getString("lat")),
					Double.parseDouble(obj.getString("lng")),
					1);
		} catch (IOException ioException) {
			// Catch network or other I/O problems.
			Log.e("[ERROR]", "Service not available", ioException);
		} catch (IllegalArgumentException illegalArgumentException) {
			// Catch invalid latitude or longitude values.
			Log.e("[ERROR]", "LatLng not appropriate. Lattitude:" +
					obj.getString("lat") +
					", Longitude = " +
					obj.getString("lng"), illegalArgumentException);
		}
		Address address = null;
		if (!(addresses == null || addresses.size()  == 0)) {
			address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<String>();

			for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}
			Log.i("[INFO]", "Address found " + address.getAddressLine(0));
		}

		Bitmap stat_img = stat_imgs.get(1);
		if(obj.getJSONObject("response").getBoolean("status")){
			stat_img = stat_imgs.get(0);
		}
		return new ComplaintItem(address.getAddressLine(0),
				obj.getString("datetime"),
				obj.getString("img"),
				obj.getJSONObject("response"),
				obj.getString("content"),
				String.valueOf(ITEMS.size()),
				stat_img
				);
	}


	public static class ComplaintItem{
		public final String loc;
		public final String datetime;
		public final String image;
		public final JSONObject status;
		public final String content;
		public final String id;
		public final Bitmap status_img;
		public ComplaintItem(String loc, String datetime, String image, JSONObject status, String content, String id, Bitmap status_img) {
			this.loc = loc;
			this.datetime = datetime;
			this.image = image;
			this.status =  status;
			this.content = content;
			this.id = id;
			this.status_img = status_img;
		}

		@Override
		public String toString() {
			String data = "";
			try{
				JSONObject obj = new JSONObject();
				obj.put("address",loc);
				obj.put("datetime",datetime);
				obj.put("img",image);
				obj.put("status",status);
				obj.put("content",content);

				data = obj.toString();
			}catch (JSONException e){
				Log.e("[ERROR]","JSONException occured in ComplaintItem.toString() method");
			}

			return data;
		}
	}

}
