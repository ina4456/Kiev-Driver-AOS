package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityCallHistoryBinding;
import com.kiev.driver.aos.view.activity.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

public class CallHistoryActivity extends BaseActivity implements View.OnClickListener {

	private ActivityCallHistoryBinding mBinding;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, CallHistoryActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_history);

		initToolbar();
		mBinding.clCallHistoryListToday.setOnClickListener(this);
		mBinding.clCallHistoryListRecent7days.setOnClickListener(this);
		mBinding.clCallHistoryListThisMonth.setOnClickListener(this);
		mBinding.clCallHistoryListLastMonth.setOnClickListener(this);
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
				CallHistoryDetailListActivity.startActivity(this, R.string.ch_today);
				break;
			case R.id.cl_call_history_list_recent_7days:
				CallHistoryDetailListActivity.startActivity(this, R.string.ch_recent_7_days);
				break;
			case R.id.cl_call_history_list_this_month:
				CallHistoryDetailListActivity.startActivity(this, R.string.ch_this_month);
				break;
			case R.id.cl_call_history_list_last_month:
				CallHistoryDetailListActivity.startActivity(this, R.string.ch_last_month);
				break;
		}
	}
}
