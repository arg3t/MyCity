package gq.yigit.mycity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CrashMapFragment extends Fragment implements WebRequest.responseListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private CrashMapFragment activity;
	MapView mMapView;
	private GoogleMap googleMap;
	private String url;
	ArrayList<LatLng> markerPoints;

	private static String API_KEY = "AIzaSyCe3xvqc_FyrPDvu7MptJ3h2GyR1-EpCLw";

	private OnFragmentInteractionListener mListener;

	public CrashMapFragment() {
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment CrashMapFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static CrashMapFragment newInstance(String param1, String param2) {
		CrashMapFragment fragment = new CrashMapFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_crash_map, container, false);

		mMapView= (MapView) rootView.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		mMapView.onResume();
		activity = this;

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap mMap) {
				googleMap = mMap;

				if (checkLocationPermission()) {
					if (ContextCompat.checkSelfPermission(getActivity(),
							Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {
						googleMap.setMyLocationEnabled(true);
					}
				}
				markerPoints = new ArrayList<LatLng>();

				googleMap.getUiSettings().setCompassEnabled(true);
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);
				googleMap.getUiSettings().setRotateGesturesEnabled(true);

				FileActions file_manager = new FileActions();
				url = file_manager.readFromFile(getContext(),"server.config").trim();
				String url_crashes = url + "/crashes";
				WebRequest data_request = new WebRequest(url_crashes,true,new HashMap<String, String>(),0);
				data_request.addListener(activity);
				data_request.execute();

				CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(39.925533,32.866287)).zoom(13).build();
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		});

		return rootView;
	}

	public void receivedResponse(boolean success,String response, int reqid){
		if(success){
			try{
				JSONObject crashes = new JSONObject(response);
				Iterator<String> iter = crashes.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						JSONArray c = crashes.getJSONArray(key);
						for (int i = 0 ; i < c.length(); i++) {
							JSONObject crash = c.getJSONObject(i);
							JSONObject location = crash.getJSONObject("location");
							String lat = location.getString("latitude");
							String lng = location.getString("longitude");

							String priority = crash.getString("priority");
							String message = crash.getString("message");

							LatLng ankara = new LatLng(Float.parseFloat(lat),Float.parseFloat(lng));
							googleMap.addMarker(new MarkerOptions().position(ankara).
									title("Priority " + priority).snippet(message));
						}
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			}catch (Exception e){
				Log.e("[ERROR]",e.getMessage());
			}
		}
	}

	// TODO: Rename method, update argument and hook method into UI event
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

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				new AlertDialog.Builder(getActivity())
						.setTitle("")
						.setMessage("")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//Prompt the user once explanation has been shown
								ActivityCompat.requestPermissions(getActivity(),new String[]
										{Manifest.permission.ACCESS_FINE_LOCATION},1);
							}
						})
						.create()
						.show();


			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						1);
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1:
			{
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// location-related task you need to do.
					if (ContextCompat.checkSelfPermission(getActivity(),
							Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {


						googleMap.setMyLocationEnabled(true);
					}

				} else {



				}
				return;
			}

		}
	}
}
