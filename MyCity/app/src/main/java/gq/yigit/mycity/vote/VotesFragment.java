package gq.yigit.mycity.vote;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import gq.yigit.mycity.*;
import gq.yigit.mycity.vote.VotesContent.VoteItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class VotesFragment extends Fragment implements responseListener, imageListener {

	private static final String ARG_COLUMN_COUNT = "column-count";
	private int mColumnCount = 1;
	private OnListFragmentInteractionListener mListener;
	public RecyclerView recyclerView;
	public String url;
	public int img_count = 0;
	public JSONArray votes;
	public ImageDownload img_downloader;
	public int j = 0;
	public VotesFragment() {
	}

	public static VotesFragment newInstance(int columnCount) {
		VotesFragment fragment = new VotesFragment();
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
		View view = inflater.inflate(R.layout.fragment_votes_list, container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}
			FileActions file_manager = new FileActions();
			url = (file_manager.readFromFile(context,"server.config")).trim();
			WebRequest web_manager = new WebRequest(url + "/votings",true,new HashMap<String,String>());
			web_manager.addListener(this);
			web_manager.execute();
		}
		return view;
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnListFragmentInteractionListener {
		void onListFragmentInteraction(VoteItem item);
	}

	public void receivedResponse(boolean success, String response){

		if(success) {
			try {
				votes = new JSONArray(response);
				img_downloader = new ImageDownload();
				img_downloader.addListener(this);
				img_downloader.execute(url+ ((JSONObject)votes.get(j)).getString("img"));

			}catch (Exception e){
				Log.e("[ERROR]:",e.getMessage());
			}
			Log.i("[INFO]",VotesContent.ITEMS.toString());
		}else{
			Log.e("[ERROR]:",response);
		}
	}

	public void imageDownloaded(Bitmap img){
		try {
			JSONObject vote = (JSONObject) votes.get(j);
			vote.put("img",img);
			j++;
			if(j>votes.length()-1) {
				for (int i = 0; i < votes.length(); i++) {
					JSONObject poll = (JSONObject) votes.get(i);
					VotesContent.addItem(new VoteItem(poll.get("id").toString(), poll.get("name").toString(), poll.get("desc").toString(), (Bitmap) poll.get("img")));
				}
				recyclerView.setAdapter(new MyVotesRecyclerViewAdapter(VotesContent.ITEMS, mListener));
			}else{
				img_downloader = new ImageDownload();
				img_downloader.addListener(this);
				img_downloader.execute(url+ ((JSONObject)votes.get(j)).getString("img"));
			}
		} catch (Exception e) {
			Log.e("[ERROR]", e.toString());
		}
	}

}
