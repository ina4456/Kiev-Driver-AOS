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
		void onListItemSelected(int index, WaitingZone item, boolean isRequest);
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

	public void setViewsAsWaitingOrNot(boolean isWaiting, ViewHolder vh, int index, int waitOrder) {
		vh.mBinding.btnWzRequest.setVisibility(isWaiting ? View.GONE : View.VISIBLE);
		vh.mBinding.btnWzCancel.setVisibility(isWaiting ? View.VISIBLE : View.GONE);
		vh.mBinding.tvWzName.setPressed(isWaiting);
		vh.mBinding.tvWzWaitingOrder.setPressed(isWaiting);
		vh.mBinding.clWzItem.setSelected(isWaiting);

		for (int i = 0; i < mItems.size(); i++) {
			WaitingZone item = mItems.get(i);
			if (isWaiting) {
				if (i == index) {
					LogHelper.e("i : " + index);
					item.setMyWaitingOrder(waitOrder);
				} else {
					item.setAvailableWait(false);
					item.setMyWaitingOrder(0);
				}
			} else {
				item.setMyWaitingOrder(0);
			}
		}

		notifyDataSetChanged();
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


			if (item.getMyWaitingOrder() > 0) {
				mBinding.clWzItem.setEnabled(true);
				mBinding.clWzItem.setSelected(true);
				mBinding.tvWzName.setPressed(true);
				mBinding.tvWzWaitingOrder.setPressed(true);
				mBinding.btnWzCancel.setVisibility(View.VISIBLE);
				mBinding.tvWzWaitingOrder.setText(mContext.getString(
						R.string.wz_btn_waiting_and_order_count
						, item.getNumberOfCarsInAreas()
						, item.getMyWaitingOrder()
				));
				mBinding.btnWzRequest.setVisibility(View.GONE);
				mBinding.btnWzRequest.setEnabled(true);

			} else {
				mBinding.clWzItem.setEnabled(false);
				mBinding.clWzItem.setSelected(false);
				mBinding.tvWzName.setPressed(false);
				mBinding.tvWzWaitingOrder.setPressed(false);
				mBinding.btnWzCancel.setVisibility(View.GONE);
				mBinding.tvWzWaitingOrder.setText(mContext.getString(
						R.string.wz_btn_waiting_count, item.getNumberOfCarsInAreas()
				));

				boolean hasOrder = false;
				for (WaitingZone waitingZone : mItems) {
					if (waitingZone.getMyWaitingOrder() > 0) {
						hasOrder = true;
						break;
					}
				}

				mBinding.btnWzRequest.setVisibility(View.VISIBLE);
				mBinding.btnWzRequest.setEnabled(!hasOrder && item.isAvailableWait());
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
						mCallback.onListItemSelected(index, mItems.get(index), true);
						break;


					case R.id.btn_wz_cancel:
						LogHelper.e("btn_wz_cancel");
						mCallback.onListItemSelected(index, mItems.get(index), false);

						break;
				}
			}
		}
	}
}