package gq.yigit.mycity;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.ImageDownload;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment implements WebRequest.responseListener, ImageDownload.imageListener {


	private TextView temp_text;
	private TextView humi_text;
	private TextView pres_text;
	private TextView city_text;
	private ImageView weather_img;
	private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
	private String key = "d6907927a2b9224a0b60d0565c207377";
	private String url;

	private OnFragmentInteractionListener mListener;
	private OnRecyclerViewInteractionListener recyclerViewInteractionListener;
	public MainFragment() {
	}

	public static MainFragment newInstance(String param1, String param2) {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		temp_text = rootView.findViewById(R.id.temp_text);
		humi_text = rootView.findViewById(R.id.humidity);
		city_text = rootView.findViewById(R.id.city_name);
		pres_text = rootView.findViewById(R.id.pressure);
		weather_img = rootView.findViewById(R.id.forecast_img);
		recyclerView = rootView.findViewById(R.id.anouncements);
		swipeRefreshLayout = rootView.findViewById(R.id.simpleSwipeRefreshLayout);

		HashMap<String,String> params = new HashMap<>();
		Location curloc = null;
		try {
			for(int i = 0;i<10;i++) {
				try {
					curloc = MainActivity.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					params.put("lat", String.valueOf(curloc.getLatitude()));
					params.put("lon", String.valueOf(curloc.getLongitude()));
				}catch (NullPointerException e){
					continue;
				}
			}
			try {
					params.put("lat", String.valueOf(curloc.getLatitude()));
					params.put("lon", String.valueOf(curloc.getLongitude()));
			}catch (NullPointerException e){
				params.put("lat", "39.9123762");
				params.put("lon", "32.8097898");
			}
		}catch (SecurityException e){
			Log.e("[ERROR]", "An error occured with location permissions");

		}
		params.put("appid",key);

		url = FileActions.readFromFile(getContext(),"server.config").trim();

		WebRequest request = new WebRequest("https://api.openweathermap.org/data/2.5/weather",true,params,0);
		request.addListener(this);
		request.execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
				refresh();
            }
        });
		return rootView;
	}

	public void refresh() {
		WebRequest request = new WebRequest(url + "/announcements",true,new HashMap<String, String>(),1);
		request.addListener(this);
		request.execute();
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
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	@Override
	public void receivedResponse(boolean success, String response,int id){
		if(id == 0) {
			try {
				JSONObject weatherdata = new JSONObject(response).getJSONArray("weather").getJSONObject(0);
				JSONObject temp = new JSONObject(response).getJSONObject("main");
				temp_text.setText((int)(Float.parseFloat(temp.getString("temp")) - 272.15) + " °C");
				humi_text.setText("Humidity: %" + temp.getString("humidity"));
				pres_text.setText("Pressure: " + temp.getString("pressure") + "hpa");
				city_text.setText(new JSONObject(response).getString("name"));
				ImageDownload imageDownload = new ImageDownload();
				imageDownload.addListener(this);
				imageDownload.execute(String.format("http://openweathermap.org/img/w/%s.png", weatherdata.getString("icon")));


			} catch (JSONException e) {
				Log.e("[ERROR]", "Cannot process weather data");
			}
		}if(id == 1) {
			AnnounceContent.ITEMS.clear();
			try {
				JSONArray announcements = new JSONArray(response);
				for(int i = 0; i< announcements.length();i++){
					JSONObject obj = announcements.getJSONObject(i);
					AnnounceContent.addItem(new AnnounceContent.AnnounceItem(
							String.valueOf(i),
							obj.getString("text1"),
							obj.getString("text2")
					));
				}
				recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
				recyclerView.setAdapter(new AnnouncementAdapter(AnnounceContent.ITEMS, recyclerViewInteractionListener));

			} catch (JSONException e) {
				Log.e("[ERROR]", "Cannot process weather data");
			}

		}
	}

	@Override
	public void imageDownloaded(Bitmap img) {
		weather_img.setImageBitmap(Bitmap.createScaledBitmap(img,100,100,true));
		refresh();

	}
	public interface OnRecyclerViewInteractionListener {
		void OnRecyclerViewInteraction(AnnounceContent.AnnounceItem item);
	}

}

class AnnounceContent {

	public static final List<AnnounceItem> ITEMS = new ArrayList<>();


	public static final Map<String, AnnounceItem> ITEM_MAP = new HashMap<>();
	public static void addItem(AnnounceItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}


	public static class AnnounceItem {
		public final String id;
		public final String name;
		public final String details;

		public AnnounceItem(String id, String name, String details) {
			this.id = id;
			this.name = name;
			this.details = details;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}

class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

	private final List<AnnounceContent.AnnounceItem> mValues;
	private final MainFragment.OnRecyclerViewInteractionListener mListener;

	public AnnouncementAdapter(List<AnnounceContent.AnnounceItem> items, MainFragment.OnRecyclerViewInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.votes_list_item, parent, false);
		return new AnnouncementAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final AnnouncementAdapter.ViewHolder holder, int position) {

		holder.mItem = mValues.get(position);
		holder.mIdView.setText(mValues.get(position).name);
		holder.mContentView.setText(mValues.get(position).details);

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.OnRecyclerViewInteraction(holder.mItem);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final TextView mContentView;
		public AnnounceContent.AnnounceItem mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = (TextView) view.findViewById(R.id.item_number);
			mContentView = (TextView) view.findViewById(R.id.content);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
