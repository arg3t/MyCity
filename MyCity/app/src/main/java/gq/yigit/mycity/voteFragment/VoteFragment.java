package gq.yigit.mycity.voteFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import gq.yigit.mycity.R;
import gq.yigit.mycity.tools.*;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoteFragment extends Fragment implements responseListener, imageListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private String url;
	private String vote_id;

	private OnFragmentInteractionListener mListener;

	private ImageView header_img;
	private TextView title;
	private TextView desc;
	private Button submit;

	public VoteFragment() {
		// Required empty public constructor
	}

	// TODO: Rename and change types and number of parameters
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
		header_img = container.findViewById(R.id.header);
		title = container.findViewById(R.id.name);
		desc = container.findViewById(R.id.description);
		submit = container.findViewById(R.id.submit);

		FileActions file_manager = new FileActions();
		url = file_manager.readFromFile(getContext(),"server.config").trim();
		String url_vote = url + "/votings/" + vote_id;
		WebRequest request_manager = new WebRequest(url_vote,true,new HashMap<String, String>());
		request_manager.addListener(this);
		request_manager.execute();
		return inflater.inflate(R.layout.fragment_vote, container, false);
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

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	public void receivedResponse(boolean success,String response){
		if(success){
			try{
				JSONObject vote_data = new JSONObject(response);
				title.setText((String)vote_data.get("name"));
				desc.setText((String)vote_data.get("desc"));
				ImageDownload img_manager = new ImageDownload();
				img_manager.addListener(this);
				img_manager.execute(url+(String)vote_data.get("img"));
			}catch (Exception e){
				Log.e("[ERROR]",e.getMessage());
			}
		}
	}

	public void imageDownloaded(Bitmap img){
		header_img.setImageBitmap(img);
	}
}
