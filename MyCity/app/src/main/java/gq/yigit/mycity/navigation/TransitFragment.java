package gq.yigit.mycity.navigation;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import gq.yigit.mycity.R;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static gq.yigit.mycity.MainActivity.apikey;
import static gq.yigit.mycity.MainActivity.cntxt;


public class TransitFragment extends Fragment implements WebRequest.responseListener {




	private OnFragmentInteractionListener mListener;
	private OnRecyclerViewInteractionListener recyclerViewInteractionListener;
	private TextView place_name;
	private String[] place_data = {"","","",""};
	private PlacesClient placesClient;
	private String url;
	private String longitude;
	private String latitude;
	private RecyclerView recyclerView;

	public TransitFragment() {
		// Required empty public constructor
	}


	public static TransitFragment newInstance(String param1, String param2) {
		TransitFragment fragment = new TransitFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View rootView =  inflater.inflate(R.layout.fragment_transit, container, false);

		LinearLayout search_button = rootView.findViewById(R.id.search_layout);
		search_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Places.initialize(getContext(), apikey);

				placesClient = Places.createClient(getContext());

				int AUTOCOMPLETE_REQUEST_CODE = 1;

				List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);

				Intent intent = new Autocomplete.IntentBuilder(
						AutocompleteActivityMode.FULLSCREEN, fields).
						setLocationRestriction(RectangularBounds.newInstance(new LatLng(39.7281252,32.4848006),new LatLng(40.061707,32.9889204)))
						.build(getContext());
				startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
			}
		});
		place_name = rootView.findViewById(R.id.place_name);
		recyclerView = rootView.findViewById(R.id.route_view);
		try {
			latitude  = "39.9127897";
			longitude = "32.8073577";

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Place place = Autocomplete.getPlaceFromIntent(data);
				place_data[0] = place.getName();
				place_data[1] = place.getId();
				Log.i("[INFO]", "Place: " + place.getName() + ", " + place.getId());
				place_name.setText(place_data[0]);

				place_data[2] = String.valueOf(place.getLatLng().latitude);
				place_data[3] = String.valueOf(place.getLatLng().longitude);

				FileActions file_manager = new FileActions();
				url = file_manager.readFromFile(cntxt,"server.config").trim();

				HashMap<String,String> args = new HashMap<>();
				args.put("lat_usr",latitude);
				args.put("lng_usr",longitude);
				args.put("lat_dest",place_data[2]);
				args.put("lng_dest",place_data[3]);

				WebRequest request = new WebRequest(url + "/transit",false, args,0);
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
	public void receivedResponse(boolean success,String response, int reqid){
		Log.i("[INFO]",response);
		TransitContent.ITEMS.clear();
		try{
			JSONObject travel = new JSONObject(response);
			JSONArray routes = new JSONArray(travel.getString("routes"));
			for(int i = 0; i< routes.length();i++){
				JSONObject route = new JSONObject(routes.getString(i));
				TransitContent.addItem(new TransitContent.TransitItem(
						route.getString("name"),
						(new JSONArray(route.getString("names")).getString(0)),
						(new JSONArray(route.getString("names")).getString(1)),
						(new JSONArray(route.getString("time")).getString(0)),
						(new JSONArray(route.getString("time")).getString(1)),
						"bus"
				));
			}
			recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
			recyclerView.setAdapter(new TransitAdapter(TransitContent.ITEMS, recyclerViewInteractionListener));
		}catch (JSONException e){
			Log.e("[ERROR]", "Received invalid response from public transport service!");
		}
	}

	public interface OnRecyclerViewInteractionListener {
		void OnRecyclerViewInteractionListener(TransitContent.TransitItem item);
	}
}

class TransitContent {
	public static final List<TransitItem> ITEMS = new ArrayList<TransitItem>();


	public static final Map<String, TransitItem> ITEM_MAP = new HashMap<String, TransitItem>();
	public static void addItem(TransitItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.endTime, item);
	}
	public static void resetItems(){
		final List<TransitItem> ITEMS = new ArrayList<TransitItem>();


		final Map<String, TransitItem> ITEM_MAP = new HashMap<String, TransitItem>();
	}


	public static class TransitItem {
		public final String lineNo;
		public final String startName;
		public final String endName;
		public final String startTime;
		public final String endTime;
		public final String type;

		public TransitItem(String lineNo, String startName, String endName, String startTime, String endTime, String type) {
			this.lineNo = lineNo;
			this.startName = startName;
			this.endName = endName;
			this.startTime = startTime;
			this.endTime = endTime;
			this.type = type;
		}

	}
}

class TransitAdapter extends RecyclerView.Adapter<TransitAdapter.ViewHolder>{

	private final List<TransitContent.TransitItem> mValues;
	private final TransitFragment.OnRecyclerViewInteractionListener mListener;

	public TransitAdapter(List<TransitContent.TransitItem> items, TransitFragment.OnRecyclerViewInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.transit_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		holder.mItem = mValues.get(position);
		holder.stop1.setText(mValues.get(position).startName);
		holder.stop2.setText(mValues.get(position).endName);
		holder.time1.setText(mValues.get(position).startTime);
		holder.time2.setText(mValues.get(position).endTime);
		holder.line.setText(mValues.get(position).lineNo);
		String type = mValues.get(position).type;

		if(type.equals("bus")){

		}else if(type.equals("subway") || type.equals("ankaray")){

		}else if(type.equals("walk")){}

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.OnRecyclerViewInteractionListener(holder.mItem);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private final View mView;
		private final TextView stop1;
		private final TextView stop2;
		private final ImageView type;
		private final TextView line;
		private final TextView time1;
		private final TextView time2;

		private TransitContent.TransitItem mItem;

		private ViewHolder(View view) {
			super(view);
			mView = view;
			stop1 = view.findViewById(R.id.stop1_addr);
			stop2 = view.findViewById(R.id.stop2_addr);
			time1 = view.findViewById(R.id.stop1_time);
			time2 = view.findViewById(R.id.stop2_time);
			line = view.findViewById(R.id.line_no);
			type = view.findViewById(R.id.type_img);
		}

	}
}
