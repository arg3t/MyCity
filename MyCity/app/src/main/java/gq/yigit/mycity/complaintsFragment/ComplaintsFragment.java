package gq.yigit.mycity.complaintsFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import gq.yigit.mycity.MainActivity;
import gq.yigit.mycity.R;
import gq.yigit.mycity.complaintsFragment.ComplaintsContent.ComplaintItem;
import gq.yigit.mycity.tools.FileActions;
import gq.yigit.mycity.tools.WebRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ComplaintsFragment extends Fragment implements WebRequest.responseListener {

	private static final String ARG_COLUMN_COUNT = "column-count";
	private int mColumnCount = 1;
	private OnListFragmentInteractionListener mListener;
	public static Geocoder geocoder;
	public ArrayList<Bitmap> stat_imgs;
	private RecyclerView recyclerView;
	public ComplaintsFragment() {
	}

	@SuppressWarnings("unused")
	public static ComplaintsFragment newInstance(int columnCount) {
		ComplaintsFragment fragment = new ComplaintsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_complaint_list, container, false);
		geocoder = new Geocoder(getContext(), Locale.getDefault());
		HashMap<String,String> params = new HashMap<>();
		ComplaintsContent.ITEMS.clear();
		ComplaintsContent.ITEM_MAP.clear();

		try {
			params.put("id", MainActivity.userData.getString("id"));
		}catch (JSONException e){
			Log.e("[ERROR]","Cannot get id");
		}
		stat_imgs = new ArrayList<>();
		stat_imgs.add(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.status_done));
		stat_imgs.add(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.status_pending));
		FileActions file_manager = new FileActions();
		String url = file_manager.readFromFile(getContext(),"server.config").trim();

		WebRequest request = new WebRequest(url + "/complaints",false,params,0);
		request.addListener(this);
		request.execute();

		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}

		}
		return view;
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnListFragmentInteractionListener {

		void onListFragmentInteraction(ComplaintItem item);
	}

	@Override
	public  void receivedResponse(boolean success,String response,int id){
		if(success) {
			try {
				JSONArray data = new JSONArray(response);

				for (int i = 0; i < data.length(); i++) {
					ComplaintItem item =ComplaintsContent.createComplaintItem(data.getJSONObject(i), geocoder, stat_imgs);
					ComplaintsContent.addItem(item);
				}
			} catch (JSONException e) {
				Log.e("[ERROR]", "Error occured with complaints response!");
			}

			recyclerView.setAdapter(new MyComplaintRecyclerViewAdapter(ComplaintsContent.ITEMS, mListener));
		}
	}
}
