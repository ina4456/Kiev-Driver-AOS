package com.thinkware.houston.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.ActivityWaitingZoneListBinding;
import com.thinkware.houston.driver.aos.model.WaitingZone;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.view.adapter.WaitingZoneAdapter;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.viewmodel.MainViewModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class WaitingZoneListActivity extends BaseActivity implements View.OnClickListener, WaitingZoneAdapter.WaitingZoneListCallback {

	private MainViewModel mMainViewModel;
	private ActivityWaitingZoneListBinding mBinding;
	private WaitingZoneAdapter mWaitingZoneAdapter;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, WaitingZoneListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainViewModel.Factory callFactory = new MainViewModel.Factory(getApplication());
		mMainViewModel = ViewModelProviders.of(this, callFactory).get(MainViewModel.class);
		subscribeMainViewModel(mMainViewModel);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_waiting_zone_list);

		initToolbar();
		initRecyclerView();
		displayEmptyMsgTextView();
	}


	private void subscribeMainViewModel(MainViewModel mainViewModel) {
		mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				if (call != null) {

				}
			}
		});
	}

	private void initToolbar() {
		setSupportActionBar(mBinding.wzToolbar.toolbar);
		mBinding.wzToolbar.ibtnActionButton.setImageResource(R.drawable.selector_bg_common_refresh_btn);
		mBinding.wzToolbar.ibtnActionButton.setOnClickListener(this);
		mBinding.wzToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.main_btn_waiting_zone));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	private void initRecyclerView() {
		/**
		 * 테스트 데이터
		 */
		ArrayList<WaitingZone> waitingZoneArrayList = new ArrayList<>();


		for (int i = 0; i < 10; i++) {
			WaitingZone waitingZone = new WaitingZone();
			waitingZone.setWaitingZoneName("판교 현대백화점 " + i);
			waitingZone.setWaitingTotalWaitingCount("10");
			waitingZone.setBelongToThisWaitingZone(false);
			waitingZone.setWaitingOrder("10대 중 3번째");
			if (i > 5) {
				waitingZone.setPossibleToSelect(false);
			} else {
				waitingZone.setPossibleToSelect(true);
			}
			waitingZoneArrayList.add(waitingZone);
		}

		/**
		 * 테스트 데이터
		 */

		mWaitingZoneAdapter = new WaitingZoneAdapter(this, waitingZoneArrayList, this);
		mBinding.rvWaitingZone.setNestedScrollingEnabled(false);
		mBinding.rvWaitingZone.setAdapter(mWaitingZoneAdapter);
		mBinding.rvWaitingZone.setFocusable(false);
		mBinding.rvWaitingZone.setVerticalScrollBarEnabled(true);
		mBinding.rvWaitingZone.scrollTo(0, 0);
	}

	// TODO: 2019-06-21 대기장소가 없을 경우 표시하게 구현 필요
	private void displayEmptyMsgTextView() {
		if (mWaitingZoneAdapter != null) {
			if (mWaitingZoneAdapter.getItemCount() <= 0) {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.rvWaitingZone.setVisibility(View.GONE);
			}
		}
	}


	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ibtn_action_button) {
			// TODO: 2019-06-20 리프레시 리스트 구현
			LogHelper.e("refresh waiting zone list");
			//mWaitingZoneAdapter.refreshData();
		}
	}

	@Override
	public void onListItemSelected(WaitingZone item, boolean isRequest) {
		if (item != null) {
			mMainViewModel.changeCallStatus(isRequest
					? Constants.CALL_STATUS_VACANCY_IN_WAITING_ZONE
					: Constants.CALL_STATUS_VACANCY);
			mMainViewModel.setWaitingZone(item);
		}
	}
}
