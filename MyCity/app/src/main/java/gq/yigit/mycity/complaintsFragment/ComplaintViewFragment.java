package gq.yigit.mycity.complaintsFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gq.yigit.mycity.R;
import org.json.JSONException;
import org.json.JSONObject;

public class ComplaintViewFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";

	// TODO: Rename and change types of parameters
	private JSONObject mParam1;

	private OnFragmentInteractionListener mListener;

	public ComplaintViewFragment() {
		// Required empty public constructor
	}

	public static ComplaintViewFragment newInstance(String param1) {
		ComplaintViewFragment fragment = new ComplaintViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			try {
				mParam1 = new JSONObject(getArguments().getString(ARG_PARAM1));
			}catch(JSONException e){
				Log.e("[ERROR]", "JSON error occured while getting params in ComplaintViewFragment");
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView =  inflater.inflate(R.layout.fragment_complaint_view, container, false);
		TextView address = rootView.findViewById(R.id.cv_address);
		TextView comment = rootView.findViewById(R.id.cv_message);
		TextView priority = rootView.findViewById(R.id.cv_priority);
		TextView comment_action = rootView.findViewById(R.id.cv_response_message);
		TextView main_action = rootView.findViewById(R.id.cv_state_text);
		ImageView image_main = rootView.findViewById(R.id.cv_image);
		ImageView action_image = rootView.findViewById(R.id.cv_state_img);
		try {
			address.setText(mParam1.getString("address"));
			comment.setText(mParam1.getString("content"));
			priority.setText(mParam1.getJSONObject("status").getString("priority"));

			main_action.setText("Your complaint is being processed");
			comment_action.setText("Your complaint is being processed");
			action_image.setImageResource(R.drawable.status_pending);

			if(mParam1.getJSONObject("status").getBoolean("status")){
				main_action.setText("Your complaint has been processed");
				comment_action.setText(mParam1.getJSONObject("status").getString("comment"));
				action_image.setImageResource(R.drawable.status_done);
			}
			byte[] decodedString = Base64.decode(mParam1.getString("img"), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			image_main.setImageBitmap(decodedByte);
		}catch (JSONException e){
			Log.e("[ERROR]","JSONException occured while setting up ComplaintViewFragment");
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
}
