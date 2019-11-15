package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityCallHistoryDetailListBinding;
import com.kiev.driver.aos.model.CallHistory;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.adapter.CallHistoryAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

public class CallHistoryDetailListActivity extends BaseActivity implements View.OnClickListener {

	private ActivityCallHistoryDetailListBinding mBinding;
	private static final String EXTRA_KEY_LIST_NUMBER = "extra_key_list_number";
	private CallHistoryAdapter mCallHistoryAdapter;

	public static void startActivity(Context context, int listNumber) {
		final Intent intent = new Intent(context, CallHistoryDetailListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(EXTRA_KEY_LIST_NUMBER, listNumber);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_history_detail_list);
		int titleRid = getIntent().getIntExtra(EXTRA_KEY_LIST_NUMBER, R.string.d_menu_call_history);

		initToolbar(titleRid);
		initRecyclerView();
		displayEmptyMsgTextView();
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
		/**
		 * 테스트 데이터
		 */
		ArrayList<CallHistory> callHistoryArrayList = new ArrayList<>();
		CallHistory callHistory = new CallHistory();
		callHistory.setDate("2019-02-20");
		callHistory.setDeparture("분당구 삼평동");
		callHistory.setDestination("판교 현대백화점");
		callHistory.setStartTime("10:20");
		callHistory.setEndTime("20:20");
		callHistory.setOrderStatus("운행완료");

		CallHistory callHistory2 = new CallHistory();
		callHistory2.setDate("2019-02-11");
		callHistory2.setDeparture("분당구 삼평동");
		callHistory2.setDestination("판교 현대백화점");
		callHistory2.setStartTime("10:20");
		callHistory2.setEndTime("20:20");
		callHistory2.setOrderStatus("운행완료");
		callHistory2.setPassengerPhoneNumber("01050556980");

		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory2);
		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory);
		callHistoryArrayList.add(callHistory);
		/**
		 * 테스트 데이터
		 */

		mCallHistoryAdapter = new CallHistoryAdapter(this, callHistoryArrayList);
		mBinding.rvCallHistory.setNestedScrollingEnabled(false);
		mBinding.rvCallHistory.setAdapter(mCallHistoryAdapter);
		mBinding.rvCallHistory.setFocusable(false);
		mBinding.rvCallHistory.scrollTo(0, 0);
	}


	private void displayEmptyMsgTextView() {
		if (mCallHistoryAdapter != null) {
			if (mCallHistoryAdapter.getItemCount() <= 0) {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.rvCallHistory.setVisibility(View.GONE);
			}
		}
	}


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
