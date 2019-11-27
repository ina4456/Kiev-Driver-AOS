package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ItemWaitingZoneListBinding;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


public class WaitingZoneAdapter extends RecyclerView.Adapter<WaitingZoneAdapter.ViewHolder> {

	private ArrayList<WaitingZone> mItems;
	private Context mContext;
	private WaitingZoneListCallback mCallback;

	public WaitingZoneAdapter(Context context, ArrayList<WaitingZone> items, WaitingZoneListCallback callback) {
		mContext = context;
		mItems = items;
		mCallback = callback;
	}

	public interface WaitingZoneListCallback {
		void onListItemSelected(WaitingZone item, boolean isRequest);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_waiting_zone_list, parent, false);
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

	public void refreshData(ArrayList<WaitingZone> runHistories) {
		if (runHistories != null) {
			mItems = runHistories;
		} else {
			mItems.clear();
		}
		notifyDataSetChanged();
	}

	public void addData(ArrayList<WaitingZone> waitingZones) {
		int curSize = getItemCount();
		mItems.addAll(waitingZones);
		notifyItemRangeInserted(curSize, waitingZones.size() - 1);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ItemWaitingZoneListBinding mBinding;

		public ViewHolder(View view) {
			super(view);
			mBinding = DataBindingUtil.bind(view);
			if (mBinding != null) {
				mBinding.btnWzRequest.setOnClickListener(this);
				mBinding.btnWzCancel.setOnClickListener(this);
			}
		}

		private void bindBodyData(WaitingZone item) {
			mBinding.tvWzName.setText(item.getWaitingZoneName());
			mBinding.tvWzWaitingOrder.setText(String.valueOf(item.getNumberOfCarsInAreas()) );
			mBinding.btnWzRequest.setEnabled(item.isAvailableWait());


			if (item.getMyWaitingOrder() > 0) {
				mBinding.clWzItem.setEnabled(true);
				mBinding.clWzItem.setSelected(true);

				mBinding.tvWzName.setPressed(true);
				mBinding.tvWzWaitingOrder.setPressed(true);
				mBinding.btnWzRequest.setVisibility(View.GONE);
				mBinding.btnWzCancel.setVisibility(View.VISIBLE);
			} else {
				mBinding.clWzItem.setEnabled(false);
				mBinding.clWzItem.setSelected(false);

				mBinding.tvWzName.setPressed(false);
				mBinding.tvWzWaitingOrder.setPressed(false);
				mBinding.btnWzRequest.setVisibility(View.VISIBLE);
				mBinding.btnWzCancel.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {
			int index = getAdapterPosition();
			LogHelper.e("onClick : " + index);

			if (index != -1) {
				switch (v.getId()) {
					case R.id.btn_wz_request:
						LogHelper.e("btn_wz_request");
						mCallback.onListItemSelected(mItems.get(index), true);

//						mBinding.btnWzRequest.setVisibility(View.GONE);
//						mBinding.btnWzCancel.setVisibility(View.VISIBLE);
//						mBinding.tvWzName.setPressed(true);
//						mBinding.tvWzWaitingOrder.setPressed(true);
//						mBinding.clWzItem.setSelected(true);
//
//						for (int i = 0; i < mItems.size(); i++) {
//							if (i == index) {
//								LogHelper.e("i : " + index);
//								mItems.get(i).setAvailableWait(true);
//								mCallback.onListItemSelected(mItems.get(i), true);
//							} else {
//								mItems.get(i).setAvailableWait(false);
//							}
//						}
//
//						notifyDataSetChanged();
						break;


					case R.id.btn_wz_cancel:
						LogHelper.e("btn_wz_cancel");
						mCallback.onListItemSelected(mItems.get(index), false);
//						mBinding.btnWzRequest.setVisibility(View.VISIBLE);
//						mBinding.btnWzCancel.setVisibility(View.GONE);
//						mBinding.tvWzName.setPressed(false);
//						mBinding.tvWzWaitingOrder.setPressed(false);
//						mBinding.clWzItem.setSelected(false);


//						for (int i = 0; i < mItems.size(); i++) {
//							mItems.get(i).setBelongToThisWaitingZone(false);
//						}

//						notifyDataSetChanged();

						break;
				}
			}
		}
	}
}