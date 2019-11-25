package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityWaitingCallListBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.adapter.WaitingCallListAdapter;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
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

import static com.kiev.driver.aos.repository.remote.packets.Packets.WaitCallListType.RequestFirstTime;
import static com.kiev.driver.aos.view.activity.CallReceivingActivity.DIALOG_TAG_FAILURE;


// TODO: 2019. 3. 11. 배차 요청 후 실패 및 리스트 갱신에 대한 처리 필요

public class WaitingCallListActivity extends BaseActivity implements View.OnClickListener
		, WaitingCallListAdapter.CallListCallback, PopupDialogFragment.PopupDialogListener {

	private static final int START_INDEX = 1;
	private boolean hasMoreData = false;

	private ActivityWaitingCallListBinding mBinding;
	private WaitingCallListAdapter mWaitingCallListAdapter;
	private MainViewModel mMainViewModel;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, WaitingCallListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainViewModel.Factory callFactory = new MainViewModel.Factory(getApplication());
		mMainViewModel = ViewModelProviders.of(this, callFactory).get(MainViewModel.class);
		subscribeMainViewModel(mMainViewModel);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_waiting_call_list);

		initToolbar();
		requestWaitCallList(START_INDEX);
		initRecyclerView();
		showListOrEmptyMsgView();
	}

	private void subscribeMainViewModel(MainViewModel mainViewModel) {
		mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				LogHelper.e("onChanged()-call");
				//콜 리스트에서 배차 요청을 하고 난 후, 배차 성공을 했다면, 해당 리스트를 닫는다.
				if (call.getCallStatus() == Constants.CALL_STATUS_ALLOCATED) {
					finish();
				}
			}
		});
	}

	private void initToolbar() {
		setSupportActionBar(mBinding.wcToolbar.toolbar);
		mBinding.wcToolbar.ibtnActionButton.setImageResource(R.drawable.selector_bg_common_refresh_btn);
		mBinding.wcToolbar.ibtnActionButton.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.main_btn_waiting_call_list));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	private void initRecyclerView() {
		LogHelper.e("initRecyclerView()");
		mWaitingCallListAdapter = new WaitingCallListAdapter(this, new ArrayList<>(), this);
		mBinding.rvWaitingCall.setNestedScrollingEnabled(false);
		mBinding.rvWaitingCall.setAdapter(mWaitingCallListAdapter);
		mBinding.rvWaitingCall.setFocusable(false);
		mBinding.rvWaitingCall.setVerticalScrollBarEnabled(true);
		mBinding.rvWaitingCall.scrollTo(0, 0);
		mBinding.rvWaitingCall.addOnScrollListener(mScrollListener);
	}


	private void showListOrEmptyMsgView() {
		if (mWaitingCallListAdapter != null) {
			if (mWaitingCallListAdapter.getItemCount() <= 0) {
				mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(R.string.wc_msg_no_waiting_call));
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.rvWaitingCall.setVisibility(View.GONE);
			} else {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.GONE);
				mBinding.rvWaitingCall.setVisibility(View.VISIBLE);
			}
		}
	}

	private void requestWaitCallList(int startIndex) {
		startLoadingProgress();

		Packets.WaitCallListType requestType = Packets.WaitCallListType.RequestAgain;
		if (startIndex == START_INDEX) {
			requestType = RequestFirstTime;
			if (mWaitingCallListAdapter != null) {
				mWaitingCallListAdapter.refreshData(null);
			}
		}

		MutableLiveData<ResponseWaitCallListPacket> liveData = mMainViewModel.requestWaitingCallList(requestType, startIndex);
		liveData.observe(this, new Observer<ResponseWaitCallListPacket>() {
			@Override
			public void onChanged(ResponseWaitCallListPacket response) {
				LogHelper.e("responseWaitCallListPacket : " + response);
				liveData.removeObserver(this);
				finishLoadingProgress();

				if (response != null) {
					ArrayList<Call> waitingCallList = new ArrayList<>();
					String[] callNumbers = response.getCallNumbers().split("\\|\\|");
					String[] callReceiptDates = response.getCallReceiptDates().split("\\|\\|");
					String[] callOrderCounts = response.getOrderCounts().split("\\|\\|");
					String[] departures = response.getDepartures().split("\\|\\|");
					String[] destinations = response.getDestinations().split("\\|\\|");
					String[] distances = response.getDistances().split("\\|\\|");

					LogHelper.e("callNumbers : " + callNumbers.length + " / callReceiptDates : " + callReceiptDates.length
							+ " / callOrderCounts : " + callOrderCounts.length + " / departures : " + departures.length
					 + " / destinations: " + destinations.length + " / distances : " + distances.length);

					if (response.getWaitCallCount() > 0 && callNumbers.length > 0) {
						hasMoreData = response.isHasMoreList();
						LogHelper.e("hasMoreData : " + hasMoreData);

						for (int i = 0; i < callNumbers.length ; i++) {
							Call call = new Call();
							call.setCallNumber(Integer.parseInt(callNumbers[i]));
							call.setCallReceivedDate(callReceiptDates[i]);
							call.setCallOrderCount(Integer.parseInt(callOrderCounts[i]));
							call.setDeparturePoi(departures[i]);
							call.setDestinationPoi(destinations[i]);
							call.setDistance(Integer.valueOf(distances[i]));

							waitingCallList.add(call);
						}

						if (startIndex == START_INDEX) {
							mWaitingCallListAdapter.refreshData(waitingCallList);
						} else {
							mWaitingCallListAdapter.addData(waitingCallList);
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
			requestWaitCallList(START_INDEX);
		}
	}

	@Override
	public void onListItemSelected(Call call) {
		LogHelper.e("selected Item : " + call.toString());

		MutableLiveData<ResponseWaitCallOrderInfoPacket> liveData = mMainViewModel.requestWaitingCallOrder(call);
		liveData.observe(this, new Observer<ResponseWaitCallOrderInfoPacket>() {
			@Override
			public void onChanged(ResponseWaitCallOrderInfoPacket response) {
				LogHelper.e("responseWaitCallOrderInfoPacket : " + response);
				liveData.removeObserver(this);
				if (response != null) {
					if (response.isSuccess()) {
						finish();
					} else {
						showFailurePopup();
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
						requestWaitCallList(totalItemCount + 1);
					}
				}
			}
		}
	};

	private void showFailurePopup() {
		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_FAILURE)
				.setContent(getString(R.string.alloc_msg_failed_call))
				.setBtnLabel(getString(R.string.common_confirm), null)
				.setDismissSecond(3)
				.build();
		showPopupDialog(popup);
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {

	}
}

