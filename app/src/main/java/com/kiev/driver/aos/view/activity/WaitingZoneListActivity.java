package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityWaitingZoneListBinding;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaNewPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitDecisionNewPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.adapter.WaitingZoneAdapter;
import com.kiev.driver.aos.viewmodel.MainViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WaitingZoneListActivity extends BaseActivity implements View.OnClickListener, WaitingZoneAdapter.WaitingZoneListCallback {

	private static final int START_INDEX = 1;
	private boolean hasMoreData = false;

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
		//subscribeMainViewModel(mMainViewModel);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_waiting_zone_list);

		initToolbar();
		requestWaitZoneList(START_INDEX);
		initRecyclerView();
		showListOrEmptyMsgView();
	}


	private void subscribeMainViewModel(MainViewModel mainViewModel) {
		MutableLiveData<ResponseWaitAreaNewPacket> waitArea = mainViewModel.requestWaitArea(Packets.WaitAreaRequestType.Normal, START_INDEX);
		waitArea.observe(this, new Observer<ResponseWaitAreaNewPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaNewPacket responseWaitAreaNewPacket) {
				LogHelper.e("onChanged() : " + responseWaitAreaNewPacket);
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
		mWaitingZoneAdapter = new WaitingZoneAdapter(this, new ArrayList(), this);
		mBinding.rvWaitingZone.setNestedScrollingEnabled(false);
		mBinding.rvWaitingZone.setAdapter(mWaitingZoneAdapter);
		mBinding.rvWaitingZone.setFocusable(false);
		mBinding.rvWaitingZone.setVerticalScrollBarEnabled(true);
		mBinding.rvWaitingZone.scrollTo(0, 0);
		mBinding.rvWaitingZone.addOnScrollListener(mScrollListener);
	}

	private void showListOrEmptyMsgView() {
		if (mWaitingZoneAdapter != null) {
			if (mWaitingZoneAdapter.getItemCount() <= 0) {
				mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(R.string.wz_msg_no_waiting_zone));
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.rvWaitingZone.setVisibility(View.GONE);
			} else {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.GONE);
				mBinding.rvWaitingZone.setVisibility(View.VISIBLE);
			}
		}
	}

	private void requestWaitZoneList(int startIndex) {
		startLoadingProgress();

		if (startIndex == START_INDEX) {
			if (mWaitingZoneAdapter != null) {
				mWaitingZoneAdapter.refreshData(null);
			}
		}

		MutableLiveData<ResponseWaitAreaNewPacket> liveData = mMainViewModel.requestWaitArea(Packets.WaitAreaRequestType.Normal, startIndex);
		liveData.observe(this, new Observer<ResponseWaitAreaNewPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaNewPacket response) {
				LogHelper.e("responseWaitCallListPacket : " + response);
				liveData.removeObserver(this);
				finishLoadingProgress();

				if (response != null) {
					ArrayList<WaitingZone> waitingZoneList = new ArrayList<>();
					String[] waitAreaIds = response.getWaitAreaIds().split("\\|\\|");
					String[] waitAreaNames = response.getWaitAreaNames().split("\\|\\|");
					String[] numberOfCarsInAreas = response.getNumberOfCarInAreas().split("\\|\\|");
					String[] isAvailableWaits = response.getIsAvailableWaits().split("\\|\\|");
					String[] myWaitNumbers = response.getMyWaitNumbers().split("\\|\\|");

					LogHelper.e("waitAreaIds : " + waitAreaIds.length + " / waitAreaNames : " + waitAreaNames.length
							+ " / numberOfCarsInAreas : " + numberOfCarsInAreas.length + " / isAvailableWaits : " + isAvailableWaits.length
							+ " / myWaitNumbers: " + myWaitNumbers.length);

					if (response.getTotalCount() > 0 && waitAreaIds.length > 0) {
						hasMoreData = response.isHasMoreData();
						LogHelper.e("hasMoreData : " + hasMoreData);

						for (int i = 0; i < waitAreaIds.length ; i++) {
							try {
								WaitingZone waitZone = new WaitingZone();
								waitZone.setWaitingZoneId(Integer.parseInt(waitAreaIds[i]));
								waitZone.setWaitingZoneName(waitAreaNames[i]);
								waitZone.setNumberOfCarsInAreas(Integer.parseInt(numberOfCarsInAreas[i]));
								waitZone.setAvailableWait(isAvailableWaits[i].equals("Y"));
								waitZone.setMyWaitingOrder(Integer.parseInt(myWaitNumbers[i].equals("") ? "0" : myWaitNumbers[i]));
								waitingZoneList.add(waitZone);
							} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
								e.printStackTrace();
							}
						}

						if (startIndex == START_INDEX) {
							mWaitingZoneAdapter.refreshData(waitingZoneList);
						} else {
							mWaitingZoneAdapter.addData(waitingZoneList);
						}

						showListOrEmptyMsgView();
					}
				}
			}
		});
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
		LogHelper.e("item : " + item + " / " + isRequest);
		if (item != null) {
			if (isRequest) {
				MutableLiveData<ResponseWaitDecisionNewPacket> liveData =  mMainViewModel.requestWaitDecision(item.getWaitingZoneId());
				liveData.observe(this, new Observer<ResponseWaitDecisionNewPacket>() {
					@Override
					public void onChanged(ResponseWaitDecisionNewPacket response) {
						liveData.removeObserver(this);
						LogHelper.e("대기 결정 응답 :  " + response);
					}
				});
			}


			// TODO: 2019-11-27 대기 결정 api 호출
//			mMainViewModel.changeCallStatus(isRequest
//					? Constants.CALL_STATUS_VACANCY_IN_WAITING_ZONE
//					: Constants.CALL_STATUS_VACANCY);
//			mMainViewModel.setWaitingZone(item);
		}
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
						requestWaitZoneList(totalItemCount + 1);
					}
				}
			}
		}
	};

}
