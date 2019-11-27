package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.model.CallHistory;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.util.CallManager;
import com.kiev.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;



public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {

	private ArrayList<CallHistory> mItems;
	private Context mContext;
	private Repository mRepository;

	public CallHistoryAdapter(Context context, ArrayList<CallHistory> items) {
		mContext = context;
		mItems = items;

		/*Application application = ((Activity)context).getApplication();
		MainApplication mainApplication = (MainApplication)application;
		mRepository = mainApplication.getRepository();*/
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
		LogHelper.e("@@@@ addData");
		int curSize = getItemCount();
		mItems.addAll(calls);
		notifyItemRangeInserted(curSize, calls.size() - 1);
	}


	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private TextView tvDate, tvOrderStatus, tvDeparture, tvDestination, tvStartTime, tvEndTime;
		private LinearLayout btnCallPassenger;

		public ViewHolder(View view) {
			super(view);
			tvDate = view.findViewById(R.id.tv_date);
			tvOrderStatus = view.findViewById(R.id.tv_order_status);
			tvDeparture = view.findViewById(R.id.tv_departure);
			tvDestination = view.findViewById(R.id.tv_destination);
			tvStartTime = view.findViewById(R.id.tv_start_time);
			tvEndTime = view.findViewById(R.id.tv_end_time);
			btnCallPassenger = view.findViewById(R.id.ll_btn_call_passenger);
		}

		private void bindBodyData(CallHistory item) {

			tvDate.setText(item.getDate());
			tvOrderStatus.setText(item.getCallType());
			tvDeparture.setText(item.getDeparture());

			String destination = item.getDestination();
			if (destination.isEmpty())
				destination = mContext.getString(R.string.alloc_no_destination);
			tvDestination.setText(destination);

			String startTime = item.getStartTime();
			if (startTime == null || startTime.isEmpty()) {
				startTime = "";
			}
			tvStartTime.setText(startTime);

			String endTime = item.getEndTime();
			if (endTime == null || endTime.isEmpty()) {
				endTime = "";
			}
			tvEndTime.setText(endTime);

			btnCallPassenger.setOnClickListener(this);
			btnCallPassenger.setVisibility(View.GONE);


			String phoneNumber = item.getPassengerPhoneNumber();
			if (phoneNumber != null && !phoneNumber.isEmpty()) {
				btnCallPassenger.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onClick(View v) {
			int index = getAdapterPosition();
			String phoneNumber = mItems.get(index).getPassengerPhoneNumber();


			if (phoneNumber != null && !phoneNumber.isEmpty()) {
				LogHelper.e("phoneNumber : " + phoneNumber);

				// FIXME: 2019-11-27 사용자 환경 설정값으로 스피커폰 사용 여부 설정 필요
				CallManager.getInstance(mContext)
						.call(mContext, phoneNumber, true);
			} else {
				LogHelper.e("전화번호가 유효하지 않음");
			}
		}
	}
}