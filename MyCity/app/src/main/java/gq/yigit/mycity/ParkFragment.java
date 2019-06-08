package gq.yigit.mycity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static gq.yigit.mycity.MainActivity.apikey;
import static gq.yigit.mycity.MainActivity.cntxt;


public class ParkFragment extends Fragment implements WebRequest.responseListener, OnMapReadyCallback {


	private OnFragmentInteractionListener mListener;
	private ImageView spot_img;
	private TextView place_name;
	private String url;

	private String latitude;
	private String longitude;
	private double lat_orig;
	private double lng_orig;

	private GoogleMap map;
	MapView mapFragment;

	public ParkFragment() {
		// Required empty public constructor
	}


	public static ParkFragment newInstance(String param1, String param2) {
		ParkFragment fragment = new ParkFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_park, container, false);
		mapFragment = rootView.findViewById(R.id.spot_loc);
		mapFragment.onCreate(savedInstanceState);
		spot_img = rootView.findViewById(R.id.spot_img);

		LinearLayout search_button = rootView.findViewById(R.id.search_layout_park);


		mapFragment.getMapAsync(this);

		search_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Places.initialize(getContext(), apikey);


				int AUTOCOMPLETE_REQUEST_CODE = 1;

				List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);

				Intent intent = new Autocomplete.IntentBuilder(
						AutocompleteActivityMode.FULLSCREEN, fields).
						setLocationRestriction(RectangularBounds.newInstance(new LatLng(39.7281252,32.4848006),new LatLng(40.061707,32.9889204)))
						.build(getContext());
				startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
			}
		});

		place_name = rootView.findViewById(R.id.place_name_park);
		try {
			lat_orig  = 39.9127897;
			lng_orig = 32.8073577;

		}catch (SecurityException e){
			Log.e("[ERROR]","An error occured with location permissions");
		}

		return rootView;
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
					+ " must implement OnComplaintsClicked");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Place place = Autocomplete.getPlaceFromIntent(data);

				place_name.setText(place.getName());

				FileActions file_manager = new FileActions();
				url = file_manager.readFromFile(cntxt,"server.config").trim();


				WebRequest request = new WebRequest(url + "/parking",true, new HashMap<String, String>(),0);
				request.addListener(this);
				request.execute();

			} else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
				Status status = Autocomplete.getStatusFromIntent(data);
				Log.i("[INFO]", status.getStatusMessage());
			} else if (resultCode == RESULT_CANCELED) {
			}
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
	public void receivedResponse(boolean success, String data,  int code){
		if(success){
			try{
				JSONObject spot = new JSONObject(data);

				byte[] decodedString = Base64.decode(spot.getString("img"), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				spot_img.setImageBitmap(decodedByte);
				latitude = spot.getString("lat");
				longitude = spot.getString("lng");

				LatLng spot_loc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
				map.addMarker(new MarkerOptions().position(spot_loc).title("Parking Spot"));

				LatLng loc = new LatLng(lat_orig, lng_orig);
				map.addMarker(new MarkerOptions().position(loc).title("Location"));

				map.moveCamera(CameraUpdateFactory.newLatLng(loc));


			}catch(JSONException e){
				Log.e("[ERROR]","Error parsing json from parking service");
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.

	}

	@Override
	public void onResume() {
		super.onResume();
		mapFragment.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapFragment.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapFragment.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapFragment.onLowMemory();
	}

}
