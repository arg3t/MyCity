package gq.yigit.mycity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.*;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.ImageDownload;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static gq.yigit.mycity.MainActivity.cntxt;


public class RateFragment extends Fragment implements WebRequest.responseListener, ImageDownload.imageListener {


	private OnFragmentInteractionListener mListener;
	private String url;
	private int i = 0;
	private JSONArray ratings;
	private ArrayList<Bitmap> images = new ArrayList<>();
	private ArrayList<String> names = new ArrayList<>();

	private Spinner spinner;
	private ImageView rateImg;
	private EditText rateNote;
	private Button rateSubmit;
	private RatingBar rateStar;

	private int rate = -1;
	private int rate_id = -1;
	private String comment;
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


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_rate, container, false);

		spinner = rootView.findViewById(R.id.rate_spinner);
		rateImg = rootView.findViewById(R.id.rate_img);
		rateNote = rootView.findViewById(R.id.rate_note);
		rateSubmit = rootView.findViewById(R.id.rate_submit);
		rateStar = rootView.findViewById(R.id.rate_star);

		rateStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
				rate = (int)v*2;
			}
		});
		rateSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				boolean checkup = true;
				comment = rateNote.getText().toString();

				HashMap<String,String> request = new HashMap<>();

				if(rate >= 0){
					request.put("score",String.valueOf(rate));
				}else{
					checkup = false;
					Toast.makeText(getContext(),"Please rate your experince!",Toast.LENGTH_LONG).show();
				}

				if(rate_id >= 0){
					request.put("rating_id",String.valueOf(rate_id));
				}else{
					checkup = false;
					Toast.makeText(getContext(),"Please select a rating!",Toast.LENGTH_LONG).show();
				}

				if(checkup) {
					try {
						request.put("rater_id", MainActivity.userData.getString("id"));
					}catch (JSONException e){
						Log.e("[ERROR]","An error occured with json!");
					}
					request.put("note",comment);
					WebRequest rate_sender = new WebRequest(url + "/rate", false, request,0);
					rate_sender.execute();
				}
			}
		});
		

		String latitude  = "39.9127897";
		String longitude = "32.8073577";

		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(cntxt,"server.config").trim();
		images.add(BitmapFactory.decodeResource(cntxt.getResources(),
				R.drawable.magnifier));
		names.add("Please Select a Place");
		HashMap<String,String> request = new HashMap<>();
		request.put("longitude",longitude);
		request.put("latitude",latitude);
		WebRequest web_manager = new WebRequest(url + "/ratings",false,request,0);
		web_manager.addListener(this);
		web_manager.execute();
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
	public void receivedResponse(boolean success, String response,int id) {
		if(success){
			try {
				ratings = new JSONArray(response);
				names.add(((JSONObject)ratings.get(i)).getString("name"));
				downloadImg(i);
			}catch (JSONException e){
				Log.i("[ERROR]","Received an invalid response");
			}

		}
	}
	@Override
	public void imageDownloaded(Bitmap img){
	 images.add(img);
	 i += 1;
	 if(ratings.length() < i){
	 	try {
		    names.add(((JSONObject)ratings.get(i)).getString("name"));
		    downloadImg(i);
	    }catch (JSONException e){
	 		Log.e("[ERROR]","JSON error occured while downloading image at " + i);
	    }
	 }else{
		 CustomAdapter adapter = new CustomAdapter(getContext(), images, names);
		 spinner.setAdapter(adapter);
		 spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			 @Override
			 public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			 	if(i != 0) {
				    rate_id = i;
				    rateImg.setImageBitmap(images.get(i));
			    }

			 }

			 @Override
			 public void onNothingSelected(AdapterView<?> adapterView) {

			 }
		 });

	 }
	}

	private void downloadImg(int i) throws JSONException {
		ImageDownload downloader = new ImageDownload();
		downloader.addListener(this);
		downloader.execute(url + ((JSONObject) ratings.get(i)).get("img"));
	}


}

class CustomAdapter extends BaseAdapter {
	Context context;
	ArrayList<Bitmap> images;
	ArrayList<String> ratings;
	LayoutInflater inflater;

	public CustomAdapter(Context applicationContext, ArrayList<Bitmap> flags, ArrayList<String> ratings) {
		this.context = applicationContext;
		this.images = flags;
		this.ratings = ratings;
		inflater = (LayoutInflater.from(applicationContext));
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		view = inflater.inflate(R.layout.rate_spinner_layout, null);
		ImageView icon = (ImageView) view.findViewById(R.id.rate_item_img);
		TextView names = (TextView) view.findViewById(R.id.rate_item_text);
		icon.setImageBitmap(images.get(i));
		names.setText(ratings.get(i));
		return view;
	}
}


