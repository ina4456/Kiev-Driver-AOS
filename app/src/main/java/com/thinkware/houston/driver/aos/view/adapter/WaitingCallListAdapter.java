package com.thinkware.houston.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.ItemWaitingCallListBinding;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


public class WaitingCallListAdapter extends RecyclerView.Adapter<WaitingCallListAdapter.ViewHolder> {

	private ArrayList<Call> mItems;
	private Context mContext;
	private CallListCallback mCallback;

	public WaitingCallListAdapter(Context context, ArrayList<Call> items, CallListCallback callback) {
		this.mContext = context;
		this.mItems = items;
		this.mCallback = callback;
	}

	public interface CallListCallback {
		void onListItemSelected(Call item);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_waiting_call_list, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.bindBodyData(mItems.get(position));
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void refreshData(ArrayList<Call> runHistories) {
		if (runHistories != null) {
			mItems = runHistories;
		} else {
			mItems.clear();
		}
		notifyDataSetChanged();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ItemWaitingCallListBinding mBinding;

		public ViewHolder(View view) {
			super(view);
			mBinding = DataBindingUtil.bind(view);
			if (mBinding != null) {
				mBinding.btnWcRequest.setOnClickListener(this);
			}
		}

		private void bindBodyData(Call item) {
			//LogHelper.e("item : " + item.toString());
			mBinding.tvDeparture.setText(item.getDeparturePoi());
			mBinding.tvDestination.setText(item.getDestinationPoi());
		}

		@Override
		public void onClick(View v) {
			int index = getAdapterPosition();
			LogHelper.e("onClick : " + index);
			Call item = mItems.get(index);
			mCallback.onListItemSelected(item);
		}
	}
}