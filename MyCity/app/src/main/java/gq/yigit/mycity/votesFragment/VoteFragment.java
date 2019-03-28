package gq.yigit.mycity.votesFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import gq.yigit.mycity.MainActivity;
import gq.yigit.mycity.R;
import gq.yigit.mycity.tools.*;
import gq.yigit.mycity.tools.WebRequest.responseListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static gq.yigit.mycity.tools.ImageDownload.*;


public class VoteFragment extends Fragment implements responseListener, imageListener {

	private static final String ARG_PARAM1 = "param1";
	private String url;
	private String vote_id;
	private String user_vote = "";
	private OnFragmentInteractionListener mListener;

	private ImageView header_img;
	private TextView title;
	private TextView desc;
	private Spinner spinner;
	private Button submit;
	private Context cntxt;

	public VoteFragment() {
		// Required empty public constructor
	}

	public static VoteFragment newInstance(String voteid) {
		VoteFragment fragment = new VoteFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, voteid);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			vote_id = getArguments().getString(ARG_PARAM1);
		}
		Log.i("[INFO]","Voting right now");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_vote, container, false);
		header_img = rootView.findViewById(R.id.header);
		title = rootView.findViewById(R.id.name);
		desc = rootView.findViewById(R.id.description);
		submit = rootView.findViewById(R.id.submit);
		spinner = rootView.findViewById(R.id.vote);
		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(getContext(),"server.config").trim();
		String url_vote = url + "/votings/" + vote_id;
		WebRequest data_request = new WebRequest(url_vote,true,new HashMap<String, String>());
		data_request.addListener(this);
		data_request.execute();
		cntxt = getContext();
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

	public void receivedResponse(boolean success,String response){
		Log.i("[INFO]",response);
		if(success){
			try{
				JSONObject vote_data = new JSONObject(response);
				title.setText((String)vote_data.get("name"));
				desc.setText((String)vote_data.get("desc"));
				ImageDownload img_manager = new ImageDownload();
				img_manager.addListener(this);
				img_manager.execute(url+(String)vote_data.get("img"));

				ArrayAdapter<String> adapter;
				List<String> list;
				list = new ArrayList<String>();

				Iterator keys = vote_data.getJSONObject("votes").keys();
				list.add("Please Select...");
				while (keys.hasNext()) {
					Object key = keys.next();
					JSONObject value = vote_data.getJSONObject("votes").getJSONObject((String) key);
					String component = value.getString("name");
					list.add(component);
				}

				adapter = new ArrayAdapter<String>(getContext(),
						android.R.layout.simple_spinner_item, list);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

			}catch (Exception e){
				Log.e("[ERROR]",e.getMessage());
			}

			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					Log.i("[INFO]",String.valueOf(position));
					user_vote = String.valueOf(position);
				}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		}
	}

	public void imageDownloaded(Bitmap img){
		header_img.setImageBitmap(img);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!user_vote.isEmpty() && Integer.parseInt(user_vote) > 0){
					HashMap<String,String> params = new HashMap<>();
					params.put("voting_id",vote_id);
					params.put("vote_id",user_vote);
					try {
						params.put("voter_id", MainActivity.userData.get("id").toString());
					}catch (JSONException e){
						Log.e("[ERROR]","Cannot get user data");
					}
					WebRequest vote_request = new WebRequest(url+"/vote",false, params);
					vote_request.execute();
				}else{
					Toast.makeText(cntxt,"Please selet a vote!",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
