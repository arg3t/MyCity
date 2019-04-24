package gq.yigit.mycity.votesFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gq.yigit.mycity.R;
import gq.yigit.mycity.votesFragment.VotesFragment.OnListFragmentInteractionListener;
import gq.yigit.mycity.votesFragment.VotesContent.VoteItem;

import java.util.List;

public class MyVotesRecyclerViewAdapter extends RecyclerView.Adapter<MyVotesRecyclerViewAdapter.ViewHolder> {

	private final List<VoteItem> mValues;
	private final OnListFragmentInteractionListener mListener;

	public MyVotesRecyclerViewAdapter(List<VoteItem> items, OnListFragmentInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.votes_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		holder.mItem = mValues.get(position);
		holder.mIdView.setText(mValues.get(position).name);
		holder.mContentView.setText(mValues.get(position).details);
		holder.mImageView.setImageBitmap(mValues.get(position).img);

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
		public VoteItem mItem;

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
