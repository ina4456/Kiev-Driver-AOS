package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ItemCallHistoryDetailListBinding;
import com.kiev.driver.aos.model.CallHistory;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;



public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {

	private ArrayList<CallHistory> mItems;
	private Context mContext;
	private CallHistoryCallback callback;

	public CallHistoryAdapter(Context context, ArrayList<CallHistory> items, CallHistoryCallback callback) {
		mContext = context;
		mItems = items;
		this.callback = callback;

		/*Application application = ((Activity)context).getApplication();
		MainApplication mainApplication = (MainApplication)application;
		mRepository = mainApplication.getRepository();*/
	}

	public interface CallHistoryCallback {
		void onCallToPassengerPressed(String phoneNumber);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_call_history_detail_list, parent, false);
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

	public void refreshData(ArrayList<CallHistory> runHistories) {
		if (runHistories != null) {
			mItems = runHistories;
		} else {
			mItems.clear();
		}
		notifyDataSetChanged();
	}

	public void addData(ArrayList<CallHistory> calls) {
		int curSize = getItemCount();
		mItems.addAll(calls);
		notifyItemRangeInserted(curSize, calls.size());
	}


	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ItemCallHistoryDetailListBinding mBinding;

		public ViewHolder(View view) {
			super(view);
			mBinding = DataBindingUtil.bind(view);
			if (mBinding != null) {
				mBinding.llBtnCallPassenger.setOnClickListener(this);
			}
		}

		private void bindBodyData(CallHistory item) {
			mBinding.tvDate.setText(item.getDate());
			mBinding.tvOrderStatus.setText(item.getCallTypeStr());
			mBinding.tvDeparture.setText(item.getDeparture());

			String destination = item.getDestination();
			if (destination.isEmpty())
				destination = mContext.getString(R.string.alloc_no_destination);
			mBinding.tvDestination.setText(destination);

			String startTime = item.getStartTime();
			if (startTime == null || startTime.equals("null") || startTime.isEmpty()) {
				startTime = "";
			}
			mBinding.tvStartTime.setText(startTime);

			String endTime = item.getEndTime();
			if (endTime == null || endTime.equals("null") || endTime.isEmpty()) {
				endTime = "";
			}
			mBinding.tvEndTime.setText(endTime);

			int colorId;
			int resourceId;
			String callType;
			if (item.getCallType() == Packets.StatisticListType.AppCall) {
				colorId = R.color.colorGreen01;
				resourceId = R.drawable.selector_rounded_border_rect_status_app;
				callType = mContext.getString(R.string.ch_call_type_app);
			} else if (item.getCallType() == Packets.StatisticListType.BusinessCall){
				colorId = R.color.colorBlue01;
				resourceId = R.drawable.selector_rounded_border_rect_status_business;
				callType = mContext.getString(R.string.ch_call_type_business);
			} else {
				colorId = R.color.colorYellow;
				resourceId = R.drawable.selector_rounded_border_rect_status_normal;
				callType = mContext.getString(R.string.ch_call_type_normal);
			}
			mBinding.tvCallType.setTextColor(mContext.getResources().getColor(colorId));
			mBinding.tvCallType.setBackgroundResource(resourceId);
			mBinding.tvCallType.setText(callType);

			mBinding.llBtnCallPassenger.setVisibility(View.GONE);
			String phoneNumber = item.getPassengerPhoneNumber();
			if (phoneNumber != null && !phoneNumber.isEmpty() && !phoneNumber.equals("0")) {
				mBinding.llBtnCallPassenger.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onClick(View v) {
			int index = getAdapterPosition();
			String phoneNumber = mItems.get(index).getPassengerPhoneNumber();


			if (phoneNumber != null && !phoneNumber.isEmpty()) {
				LogHelper.e("phoneNumber : " + phoneNumber);
				callback.onCallToPassengerPressed(phoneNumber);
			} else {
				LogHelper.e("전화번호가 유효하지 않음");
			}
		}
	}
}