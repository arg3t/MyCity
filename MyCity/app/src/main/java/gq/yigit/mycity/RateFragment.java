package gq.yigit.mycity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.ImageDownload;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static gq.yigit.mycity.MainActivity.cntxt;


public class RateFragment extends Fragment implements WebRequest.responseListener, ImageDownload.imageListener,LocationListener {


	private OnFragmentInteractionListener mListener;
	private String url;
	private int i = 0;
	private JSONArray ratings;
	public RateFragment() {
		// Required empty public constructor
	}


	public static RateFragment newInstance(String param1, String param2) {
		RateFragment fragment = new RateFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocationManager locationManager = (LocationManager)
				cntxt.getSystemService(cntxt.LOCATION_SERVICE);

		LocationListener locationListener = this;
		if ( ContextCompat.checkSelfPermission( cntxt, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
			ActivityCompat.requestPermissions(MainActivity.mainActivity, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },1);
		}
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_rate, container, false);
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	@Override
	public void receivedResponse(boolean success, String response) {
		if(success){
			try {
				ratings = new JSONArray(response);
				ratings.get(i);
			}catch (JSONException e){
				Log.i("[ERROR]","Received an invalid response");
			}

		}
	}
	@Override
	public void imageDownloaded(Bitmap img){

	}

	@Override
	public void onLocationChanged(Location loc) {
		String longitude = "Longitude: " + loc.getLongitude();
		String latitude = "Latitude: " + loc.getLatitude();
		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(cntxt,"server.config").trim();

		HashMap<String,String> request = new HashMap<>();
		request.put("longitude",longitude);
		request.put("latitude","latitude");
		WebRequest web_manager = new WebRequest(url + "/ratings",false,request);
		web_manager.addListener(this);
		web_manager.execute();
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("[INFO]",provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}


