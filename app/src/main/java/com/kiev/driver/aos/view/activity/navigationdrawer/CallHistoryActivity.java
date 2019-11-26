package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityCallHistoryBinding;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsPacket;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.viewmodel.CallHistoryViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CallHistoryActivity extends BaseActivity implements View.OnClickListener {

	private CallHistoryViewModel mViewModel;
	private ActivityCallHistoryBinding mBinding;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, CallHistoryActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_history);
		mViewModel = new ViewModelProvider(this, new CallHistoryViewModel.Factory(getApplication()))
				.get(CallHistoryViewModel.class);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mViewModel);


		subscribeViewModel();

		initToolbar();
		mBinding.clCallHistoryListToday.setOnClickListener(this);
		mBinding.clCallHistoryListRecent7days.setOnClickListener(this);
		mBinding.clCallHistoryListThisMonth.setOnClickListener(this);
		mBinding.clCallHistoryListLastMonth.setOnClickListener(this);
	}

	private void subscribeViewModel() {
		MutableLiveData<ResponseStatisticsPacket> statistics = mViewModel.getStatistics();
		statistics.observe(this, new Observer<ResponseStatisticsPacket>() {
			@Override
			public void onChanged(ResponseStatisticsPacket response) {

				if (response != null) {
					statistics.removeObserver(this);
					mBinding.tvTodayTotalCount.setText(getString(R.string.ch_count, response.getTodayTotalCnt()));
					mBinding.tvTodayNormalCount.setText(getString(R.string.ch_count, response.getTodayNormalCnt()));
					mBinding.tvTodayAppCount.setText(getString(R.string.ch_count, response.getTodayAppCnt()));

					mBinding.tvRecent7daysTotalCount.setText(getString(R.string.ch_count, response.getWeekTotalCnt()));
					mBinding.tvRecent7daysNormalCount.setText(getString(R.string.ch_count, response.getWeekNormalCnt()));
					mBinding.tvRecent7daysAppCount.setText(getString(R.string.ch_count, response.getWeekAppCnt()));

					mBinding.tvThisMonthTotalCount.setText(getString(R.string.ch_count, response.getThisMonthTotalCnt()));
					mBinding.tvThisMonthNormalCount.setText(getString(R.string.ch_count, response.getThisMonthNormalCnt()));
					mBinding.tvThisMonthAppCount.setText(getString(R.string.ch_count, response.getThisMonthAppCnt()));

					mBinding.tvLastMonthTotalCount.setText(getString(R.string.ch_count, response.getLastMonthTotalCnt()));
					mBinding.tvLastMonthNormalCount.setText(getString(R.string.ch_count, response.getLastMonthNormalCnt()));
					mBinding.tvLastMonthAppCount.setText(getString(R.string.ch_count, response.getLastMonthAppCnt()));
				}
			}
		});
	}


	private void initToolbar() {
		setSupportActionBar(mBinding.callHistoryToolbar.toolbar);
		mBinding.callHistoryToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.callHistoryToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.d_menu_call_history));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.cl_call_history_list_today:
				CallHistoryDetailListActivity.startActivity(this, 0);
				break;
			case R.id.cl_call_history_list_recent_7days:
				CallHistoryDetailListActivity.startActivity(this, 1);
				break;
			case R.id.cl_call_history_list_this_month:
				CallHistoryDetailListActivity.startActivity(this, 2);
				break;
			case R.id.cl_call_history_list_last_month:
				CallHistoryDetailListActivity.startActivity(this, 3);
				break;
		}
	}
}
