package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityWaitingZoneListBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaDecisionPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.adapter.WaitingZoneAdapter;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.MainViewModel;

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

import static com.kiev.driver.aos.view.activity.CallReceivingActivity.DIALOG_TAG_FAILURE;

public class WaitingZoneListActivity extends BaseActivity implements View.OnClickListener
		, WaitingZoneAdapter.WaitingZoneListCallback
		, PopupDialogFragment.PopupDialogListener {

	private static final int START_INDEX = 1;
	private boolean hasMoreData = false;
	private boolean isLoading = false;
	private MainViewModel mViewModel;
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
		LogHelper.e("onCreat()");
		mViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_waiting_zone_list);
		mBinding.setLifecycleOwner(this);

		initToolbar();
		subscribeViewModel(mViewModel);
		requestWaitZoneList(START_INDEX);
		initRecyclerView();
		showListOrEmptyMsgView();
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
		LogHelper.e("requestWaitZoneList : " + startIndex);
		isLoading = true;
		startLoadingProgress();

		if (startIndex == START_INDEX) {
			if (mWaitingZoneAdapter != null) {
				mWaitingZoneAdapter.refreshData(null);
			}
		}

		MutableLiveData<ResponseWaitAreaListPacket> liveData = mViewModel.requestWaitArea(Packets.WaitAreaRequestType.Normal, startIndex);
		liveData.observe(this, new Observer<ResponseWaitAreaListPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaListPacket response) {
				LogHelper.e("responseWaitCallListPacket : " + response);
				liveData.removeObserver(this);
				isLoading = false;
				finishLoadingProgress();

				if (response != null) {
					try {
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

							for (int i = 0; i < waitAreaIds.length; i++) {
								WaitingZone waitZone = new WaitingZone();
								waitZone.setWaitingZoneId(waitAreaIds[i]);
								waitZone.setWaitingZoneName(waitAreaNames[i]);
								waitZone.setNumberOfCarsInAreas(Integer.parseInt(numberOfCarsInAreas[i]));
								waitZone.setAvailableWait(isAvailableWaits[i].equals("Y"));
								waitZone.setMyWaitingOrder(Integer.parseInt(myWaitNumbers[i].equals("") ? "0" : myWaitNumbers[i]));

								waitingZoneList.add(waitZone);
							}

							if (startIndex == START_INDEX) {
								mWaitingZoneAdapter.refreshData(waitingZoneList);
								boolean hasOrder = false;
								for (WaitingZone waitingZone : waitingZoneList) {
									hasOrder = waitingZone.getMyWaitingOrder() != 0;
									if (hasOrder) {
										break;
									}
								}
								LogHelper.e("hasorder : " + hasOrder);
								if (!hasOrder) {
									mViewModel.setWaitingZone(null, false);
								}
							} else {
								mWaitingZoneAdapter.addData(waitingZoneList);
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



	private void subscribeViewModel(MainViewModel mainViewModel) {
		if (mainViewModel != null) {
			mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
				@Override
				public void onChanged(Call call) {
					LogHelper.e("onChanged-Call");
					if (call != null) {
						int callStatus = call.getCallStatus();
						LogHelper.e("onChanged-Call : " + callStatus);
						switch (callStatus) {
							case Constants.CALL_STATUS_DRIVING:
							case Constants.CALL_STATUS_BOARDED:
							case Constants.CALL_STATUS_ALLOCATED:
								finish();
								break;

							case Constants.CALL_STATUS_VACANCY:
								requestWaitZoneList(START_INDEX);
								break;
						}
					}
				}
			});
		}
	}


	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ibtn_action_button) {
			requestWaitZoneList(START_INDEX);
		}
	}

	@Override
	public void onListItemSelected(int index, WaitingZone item, boolean isRequest) {
		LogHelper.e("item : " + item + " / " + isRequest);
		if (item != null) {
			if (isRequest) {
				MutableLiveData<ResponseWaitAreaDecisionPacket> liveData = mViewModel.requestWaitDecision(item.getWaitingZoneId());
				liveData.observe(this, new Observer<ResponseWaitAreaDecisionPacket>() {
					@Override
					public void onChanged(ResponseWaitAreaDecisionPacket response) {
						liveData.removeObserver(this);
						LogHelper.e("대기 결정 응답 :  " + response);
						if (response.getWaitProcType() == Packets.WaitProcType.Success) {
							mViewModel.setWaitingZone(item, true);
							LogHelper.e("대기 지역 저장");
							requestWaitZoneList(START_INDEX);
						} else {
							showFailurePopup(isRequest);
						}
					}
				});
			} else {
				MutableLiveData<ResponseWaitAreaCancelPacket> liveData = mViewModel.requestWaitCancel(item.getWaitingZoneId());
				liveData.observe(this, new Observer<ResponseWaitAreaCancelPacket>() {
					@Override
					public void onChanged(ResponseWaitAreaCancelPacket response) {
						mViewModel.setWaitingZone(null, false);
						liveData.removeObserver(this);
						LogHelper.e("대기 취소 응답 :  " + response);
						if (response.getWaitCancelType() == Packets.WaitCancelType.Success) {
							mViewModel.setWaitingZone(item, false);
							requestWaitZoneList(START_INDEX);
						} else {
							showFailurePopup(isRequest);
						}

					}
				});
			}
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

					if (lastVisible >= totalItemCount - 1 && !isLoading) {
						LogHelper.e("lastVisibled : " + totalItemCount);

						mBinding.rvWaitingZone.post(new Runnable() {
							@Override
							public void run() {
								//layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
								requestWaitZoneList(totalItemCount + 1);
							}
						});
					}
				}
			}
		}
	};


	private void showFailurePopup(boolean isRequest) {
		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_FAILURE)
				.setContent(isRequest ? getString(R.string.wz_msg_request_failed)
						: getString(R.string.wz_msg_cancel_failed))
				.setBtnLabel(getString(R.string.common_confirm), null)
				.setDismissSecond(3)
				.build();
		showPopupDialog(popup);
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {

	}
}
