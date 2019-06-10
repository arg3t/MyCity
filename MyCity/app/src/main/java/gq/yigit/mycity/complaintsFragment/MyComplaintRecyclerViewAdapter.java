package gq.yigit.mycity.complaintsFragment;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gq.yigit.mycity.MainActivity;
import gq.yigit.mycity.R;
import org.json.JSONException;


import java.util.List;

public class MyComplaintRecyclerViewAdapter extends RecyclerView.Adapter<MyComplaintRecyclerViewAdapter.ViewHolder> {

	private final List<ComplaintsContent.ComplaintItem> mValues;
	private final ComplaintsFragment.OnListFragmentInteractionListener mListener;

	public MyComplaintRecyclerViewAdapter(List<ComplaintsContent.ComplaintItem> items, ComplaintsFragment.OnListFragmentInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public MyComplaintRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.votes_list_item, parent, false);
		return new MyComplaintRecyclerViewAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final MyComplaintRecyclerViewAdapter.ViewHolder holder, int position) {

		holder.mItem = mValues.get(position);
		holder.mIdView.setText(mValues.get(position).loc);
		holder.mContentView.setText(mValues.get(position).datetime);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		params.height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				50,
				MainActivity.pix_density
		);
		params.width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				70,
				MainActivity.pix_density
		);
		params.topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				10,
				MainActivity.pix_density
		);
		holder.mImageView.setLayoutParams(params);
		try {
			holder.mImageView.setImageResource(R.drawable.status_pending);
			if (mValues.get(position).status.getBoolean("status")) {
				holder.mImageView.setImageResource(R.drawable.status_done);
			}
		}catch (JSONException e){
			Log.e("[ERROR]","An error occured with image");
		}
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onListFragmentInteraction(holder.mItem);
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
		public final ImageView mImageView;
		public ComplaintsContent.ComplaintItem mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = (TextView) view.findViewById(R.id.item_number);
			mContentView = (TextView) view.findViewById(R.id.content);
			mImageView = (ImageView) view.findViewById(R.id.vote_img);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
