package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityCallHistoryDetailListBinding;
import com.kiev.driver.aos.model.CallHistory;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsDetailPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.adapter.CallHistoryAdapter;
import com.kiev.driver.aos.viewmodel.CallHistoryViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CallHistoryDetailListActivity extends BaseActivity implements View.OnClickListener {

	private static final int START_INDEX = 1;
	private boolean hasMoreData = false;
	private Packets.StatisticPeriodType periodType;

	private ActivityCallHistoryDetailListBinding mBinding;
	private static final String EXTRA_KEY_PERIOD_TYPE = "extra_key_list_number";
	private CallHistoryAdapter mCallHistoryAdapter;
	private CallHistoryViewModel mViewModel;

	public static void startActivity(Context context, int periodType) {
		final Intent intent = new Intent(context, CallHistoryDetailListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(EXTRA_KEY_PERIOD_TYPE, periodType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_history_detail_list);
		mViewModel = new ViewModelProvider(this, new CallHistoryViewModel.Factory(getApplication()))
				.get(CallHistoryViewModel.class);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mViewModel);


		int titleRid;
		int periodTypeInt = getIntent().getIntExtra(EXTRA_KEY_PERIOD_TYPE, 0);
		switch (periodTypeInt) {
			case 1:
				periodType = Packets.StatisticPeriodType.Week;
				titleRid = R.string.ch_recent_7_days;
				break;
			case 2:
				periodType = Packets.StatisticPeriodType.ThisMonth;
				titleRid = R.string.ch_this_month;
				break;
			case 3:
				periodType = Packets.StatisticPeriodType.LastMonth;
				titleRid = R.string.ch_last_month;
				break;
			default:
				periodType = Packets.StatisticPeriodType.Today;
				titleRid = R.string.ch_today;
				break;
		}


		initToolbar(titleRid);
		requestHistoryList(periodType, START_INDEX);
		initRecyclerView();
		showListOrEmptyMsgView();
	}

	private void initToolbar(int titleRid) {
		setSupportActionBar(mBinding.callHistoryDetailToolbar.toolbar);
		mBinding.callHistoryDetailToolbar.ibtnActionButton.setOnClickListener(this);
		mBinding.callHistoryDetailToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.callHistoryDetailToolbar.btnActionButton.setOnClickListener(this);
		mBinding.callHistoryDetailToolbar.btnActionButton.setVisibility(View.VISIBLE);
		mBinding.callHistoryDetailToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(titleRid));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	private void initRecyclerView() {
		mCallHistoryAdapter = new CallHistoryAdapter(this, new ArrayList<>());
		mBinding.rvCallHistory.setNestedScrollingEnabled(false);
		mBinding.rvCallHistory.setAdapter(mCallHistoryAdapter);
		mBinding.rvCallHistory.setFocusable(false);
		mBinding.rvCallHistory.setVerticalScrollBarEnabled(true);
		mBinding.rvCallHistory.scrollTo(0, 0);
		mBinding.rvCallHistory.addOnScrollListener(mScrollListener);
	}

	private void showListOrEmptyMsgView() {
		if (mCallHistoryAdapter != null) {
			if (mCallHistoryAdapter.getItemCount() <= 0) {
				mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(R.string.ch_msg_no_history));
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.rvCallHistory.setVisibility(View.GONE);
			} else {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.GONE);
				mBinding.rvCallHistory.setVisibility(View.VISIBLE);
			}
		}
	}

	private void requestHistoryList(Packets.StatisticPeriodType periodType, int startIndex) {
		startLoadingProgress();

		MutableLiveData<ResponseStatisticsDetailPacket> historyPacket =
				mViewModel.getStatisticsDetail(Packets.StatisticListType.TotalCall, periodType, startIndex);

		if (startIndex == START_INDEX) {
			if (mCallHistoryAdapter != null) {
				mCallHistoryAdapter.refreshData(null);
			}
		}

		historyPacket.observe(this, new Observer<ResponseStatisticsDetailPacket>() {
			@Override
			public void onChanged(ResponseStatisticsDetailPacket response) {
				//LogHelper.e("UI 리스폰스 전달: " + response);
				historyPacket.removeObserver(this);
				finishLoadingProgress();

				if (response != null) {
					try {
						mBinding.tvCallHistoryCompletedCount.setText(getString(R.string.ch_count, response.getTotalCount()));
						ArrayList<CallHistory> historyList = new ArrayList<>();
						String[] callNumbers = response.getCallNumber().split("\\|\\|");
						String[] callTypes = response.getCallType().split("\\|\\|");
						String[] callReceiptDates = response.getReceiptDate().split("\\|\\|");
						String[] departures = response.getDeparture().split("\\|\\|");
						String[] destinations = response.getDestination().split("\\|\\|");
						String[] boardedTimes = response.getBoardingTime().split("\\|\\|");
						String[] alightedTimes = response.getAlightingTime().split("\\|\\|");
						String[] phoneNumbers = response.getPhoneNumber().split("\\|\\|");


						LogHelper.e("callTypes : " + callTypes.length
								+ "callNumbers : " + callNumbers.length + " / callReceiptDates : " + callReceiptDates.length
								+ " / departures : " + departures.length + " / destinations: " + destinations.length
								+ " / boardingTimes: " + boardedTimes.length + " / alightedTimes : " + alightedTimes.length
								+ " / phoneNumbers : " + phoneNumbers.length);

						if (response.getTotalCount() > 0 && callNumbers.length > 0) {
							hasMoreData = response.isHasMore();
							LogHelper.e("hasMoreData : " + hasMoreData);

							for (int i = 0; i < callNumbers.length; i++) {
								CallHistory history = new CallHistory();
								history.setCallId(Integer.parseInt(callNumbers[i]));
								history.setCallType(callTypes[i]);
								history.setDate(callReceiptDates[i]);
								history.setDeparture(departures[i]);
								history.setDestination(destinations[i]);
								history.setStartTime(boardedTimes[i]);
								history.setEndTime(alightedTimes[i]);
								history.setPassengerPhoneNumber(phoneNumbers[i]);
								historyList.add(history);
							}

							if (startIndex == START_INDEX) {
								mCallHistoryAdapter.refreshData(historyList);
							} else {
								mCallHistoryAdapter.addData(historyList);
							}

							showListOrEmptyMsgView();

						}
					} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}


	RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);

			if (hasMoreData) {
				LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
				if (layoutManager != null) {
					int totalItemCount = layoutManager.getItemCount();
					int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

					if (lastVisible >= totalItemCount - 1) {
						LogHelper.e("lastVisibled : " + totalItemCount);
						requestHistoryList(periodType, totalItemCount + 1);
					}
				}
			}
		}
	};


	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_action_button) {
			// TODO: 2019-06-21 필터 변경에 따른 리스트 변경 구현 필요
			String filter = mBinding.callHistoryDetailToolbar.btnActionButton.getText().toString();
			if (filter.equals(getString(R.string.ch_call_type_all))) {
				filter = getString(R.string.ch_call_type_normal);
			} else if (filter.equals(getString(R.string.ch_call_type_normal))) {
				filter = getString(R.string.ch_call_type_business);
			} else {
				filter = getString(R.string.ch_call_type_all);
			}
			mBinding.callHistoryDetailToolbar.btnActionButton.setText(filter);
		}
	}
}
