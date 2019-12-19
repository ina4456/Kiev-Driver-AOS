package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityCallReceivingBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.WavResourcePlayer;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.MainViewModel;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class CallReceivingActivity extends BaseActivity implements View.OnClickListener, PopupDialogFragment.PopupDialogListener {

	public static final String DIALOG_TAG_ALLOCATED = "dialog_tag_allocated";
	public static final String DIALOG_TAG_FAILURE = "dialog_tag_failure";
	private static final String INTENT_KEY_IS_WC = "intent_key_is_wc";
	private static final String INTENT_KEY_CALL_INFO = "intent_key_call_info";

	private MainViewModel mMainViewModel;
	private ActivityCallReceivingBinding mBinding;
	private static final int COUNT_DOWN_INTERVAL = 1000;
	private CountDownTimer countDownTimer;
	private boolean hasGotResponse = false;
	private boolean needToRequestToRefuseWhenCloseActivity = true;
	boolean isFromWaitingCallList;

	public static void startActivity(Context context, boolean isFromWaitingCallList, Call callInfo) {
		final Intent intent = new Intent(context, CallReceivingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(INTENT_KEY_IS_WC, isFromWaitingCallList);
		intent.putExtra(INTENT_KEY_CALL_INFO, callInfo);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogHelper.e("onCreate()");

		mMainViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_receiving);
		mBinding.setLifecycleOwner(this);
		//mBinding.setViewModel(mMainViewModel);

		Intent passedIntent = getIntent();
		isFromWaitingCallList = passedIntent.getBooleanExtra(INTENT_KEY_IS_WC, false);
		Call callInfoFromWaitingCallList = passedIntent.getParcelableExtra(INTENT_KEY_CALL_INFO);

		initViews(callInfoFromWaitingCallList);
		subscribeViewModel(mMainViewModel, callInfoFromWaitingCallList);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogHelper.e("onNewIntent()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogHelper.e("onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogHelper.e("onPause()");

		if (countDownTimer != null) {
			countDownTimer.cancel();
		}

		mMainApplication.setCurrentActivity(null);

		//홈 or 백 버튼을 눌러 콜 수신을 거부하여 onPause 가 호출될 경우.
		if (needToRequestToRefuseWhenCloseActivity) {
			LogHelper.e("onPause - needToRequestToRefuseWhenCloseActivity");
			WavResourcePlayer.getInstance(this).play(R.raw.voice_126);
			this.requestAcceptOrRefuse(Packets.OrderDecisionType.Reject);
			this.resetCallInfo();
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		boolean wasBackground = mMainApplication.wasBackground();
		//백그라운드 상태에서 진입 했을 경우, 백버튼 누를시 task 전체를 백그라운드로 이동.
		//그 외의 경우 콜 수신 화면만 닫음.
		if (wasBackground) {
			moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}


	private void initViews(Call call) {
		mBinding.btnReject.setOnClickListener(this);
		mBinding.btnRequest.setOnClickListener(this);

		if (!isFromWaitingCallList) {
			int displayTime = mMainViewModel.getTimeForDisplayingCallBroadcast();
			LogHelper.e("displayTime : " + displayTime);
			if (displayTime <= 0) {
				displayTime = 5;
			}
			startCountDown(displayTime);
		} else {
			setViewsAsWaitingResponse(call);
		}

		//RatingBar
		LayerDrawable stars = (LayerDrawable) mBinding.rbCallGrade.getProgressDrawable();
		setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(this, R.color.colorYellow));
		setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(this, R.color.colorYellow));
		setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(this, R.color.colorGray07));
	}

	private void subscribeViewModel(MainViewModel mainViewModel, Call callInfoFromWaitingCallList) {
		LiveData<Call> callInfo = mainViewModel.getCallInfo();
		callInfo.observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				hasGotResponse = true;
				LogHelper.e("onChanged()");
				if (call != null) {
					LogHelper.e("onChanged-Call : " + call.toString());
					CallReceivingActivity.super.finishLoadingProgress();
					int status = call.getCallStatus();
					switch (status) {
						case Constants.CALL_STATUS_ALLOCATED:
							LogHelper.e("onChanged-Call : CALL_STATUS_ALLOCATED");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							needToRequestToRefuseWhenCloseActivity = false;
							finish();
							break;

						case Constants.CALL_STATUS_ALLOCATION_FAILED:
							LogHelper.i("onChanged-Call : CALL_STATUS_ALLOCATION_FAILED");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							showFailurePopup();
							needToRequestToRefuseWhenCloseActivity = false;
							resetCallInfo();
							finishActivity();
							break;

						case Constants.CALL_STATUS_ALLOCATED_WHILE_GETON:
							LogHelper.i("onChanged-Call : CALL_STATUS_ALLOCATED_WHILE_GETON");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							needToRequestToRefuseWhenCloseActivity = false;
							resetCallInfo();
							finishActivity();
							break;

						case Constants.CALL_STATUS_BOARDED:
						case Constants.CALL_STATUS_DRIVING:
						case Constants.CALL_STATUS_VACANCY:
							//배차 방송 수신 중에 미터기 조작이 된다면, 수신된 배차 방송을 거절 처리하고 닫는다.
							//SP는 필요없으므로 주석 처리
							LogHelper.i("onChanged-Call : status : " + status);
							/*if (!isFromWaitingCallList) {
								WavResourcePlayer.getInstance(CallReceivingActivity.this).play(R.raw.voice_126);
								needToRequestToRefuseWhenCloseActivity = false;
								requestAcceptOrRefuse(Packets.OrderDecisionType.Reject);
								if (status == Constants.CALL_STATUS_VACANCY) {
									mMainViewModel.resetCallInfoForUi(Constants.CALL_STATUS_VACANCY);
								} else {
									mMainViewModel.resetCallInfoForUi(Constants.CALL_STATUS_BOARDED);
								}

								finish();
								callInfo.removeObserver(this);
							}*/

							break;


						default:
							LogHelper.i("onChanged-Call : default 콜 수신");

							//initViews();
							break;
					}

					if (!isFromWaitingCallList && status == 0) {
						setDataToViews(call);

						//거리 표시
						int distance = mainViewModel.getDistance(call.getDepartureLat(), call.getDepartureLong());
						call.setDistance(distance);
						//LogHelper.e("distance : " + distance);
						mBinding.tvDistance.setText(call.getCallDistanceToDeparture());

						//콜 등급 표시
						//테스트 call.setCallClass(new Ranom().nextInt(5));
						if (call.getCallClass() == 0) {
							mBinding.rbCallGrade.setVisibility(View.GONE);
						} else {
							mBinding.rbCallGrade.setRating(call.getCallClass());
						}
					}
				}
			}
		});

		LiveData<Configuration> config = mainViewModel.getConfiguration();
		config.observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged()-Configuration");
				float poiNameTextSize = getResources().getDimension(R.dimen.main_poi_name_text_size);
				float subPoiNameTextSize = getResources().getDimension(R.dimen.main_sub_poi_name_text_size);
				mBinding.tvDeparturePoi.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(poiNameTextSize));
				mBinding.tvDepartureAddr.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(subPoiNameTextSize));
				mBinding.tvDestinationPoi.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(poiNameTextSize));
				mBinding.tvDestinationAddr.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(subPoiNameTextSize));
			}
		});


		if (isFromWaitingCallList && callInfoFromWaitingCallList != null) {
			LogHelper.e("callInfoFromWaitingCallList..");
			MutableLiveData<ResponseWaitCallOrderInfoPacket> waitingCallInfo = mMainViewModel.requestWaitingCallOrder(callInfoFromWaitingCallList);
			waitingCallInfo.observe(this, new Observer<ResponseWaitCallOrderInfoPacket>() {
				@Override
				public void onChanged(ResponseWaitCallOrderInfoPacket response) {
					LogHelper.e("responseWaitCallOrderInfoPacket : " + response);
					waitingCallInfo.removeObserver(this);
					if (response != null) {
						if (response.isSuccess()) {
							//finish();
						} else {
							showFailurePopup();
						}
					}
				}
			});
		}
	}

	private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			DrawableCompat.setTint(drawable, color);
		} else {
			drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		}
	}

	private void finishActivity() {
		needToRequestToRefuseWhenCloseActivity = false;
		boolean wasBackground = mMainApplication.wasBackground();
		if (wasBackground) {
			moveTaskToBack(true);
			finish();
		} else {
			super.onBackPressed();
		}
	}

	private void startCountDown(int dismissSecond) {
		countDownTimer = new CountDownTimer((dismissSecond + 1) * COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
			@Override
			public void onTick(long l) {
				setTextWithCount((int) (l / 1000));
			}

			@Override
			public void onFinish() {
				LogHelper.e("onFinish()");
				needToRequestToRefuseWhenCloseActivity = false;
				requestAcceptOrRefuse(Packets.OrderDecisionType.Reject);
				resetCallInfo();
				finishActivity();
			}
		};
		countDownTimer.start();
	}

	private void setTextWithCount(int count) {
		String label = String.format(getString(R.string.alloc_btn_reject_call_with_count), count);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			mBinding.btnReject.setText(Html.fromHtml(label, Html.FROM_HTML_MODE_LEGACY));
		} else {
			mBinding.btnReject.setText(Html.fromHtml(label));
		}
	}

	private void setViewsAsWaitingResponse(Call call) {
		LogHelper.e("call :  " + call);
		startLoadingProgress();
		mBinding.tvCallState.setText(R.string.alloc_step_requesting_call);
		mBinding.btnRequest.setEnabled(false);
		mBinding.btnReject.setEnabled(false);
		mBinding.btnReject.setText(getString(R.string.alloc_btn_reject_call));
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}

		if (call != null) {
			setDataToViews(call);
		}
	}

	private void setDataToViews(Call call) {
		LogHelper.e("setDataToViews : " + call);
		mBinding.tvDistance.setText(call.getCallDistanceToDeparture());
		mBinding.tvDeparturePoi.setText(call.getDeparturePoi());
		mBinding.tvDepartureAddr.setText(call.getDepartureAddr());


		String destinationPoi = call.getDestinationPoi();
		String destinationAddr = call.getDestinationAddr();
		if (destinationPoi == null || destinationPoi.isEmpty()) {
			if (destinationAddr != null && !destinationAddr.isEmpty()) {
				destinationPoi = destinationAddr;
			} else {
				destinationPoi = getString(R.string.alloc_no_destination);
			}
		}
		mBinding.tvDestinationPoi.setText(destinationPoi);
		mBinding.tvDestinationAddr.setText(call.getDestinationAddr());
	}

	private void resetCallInfo() {
		LogHelper.d("resetCallInfo()");
		mMainViewModel.resetCallInfoForUi();
	}

	private void requestAcceptOrRefuse(Packets.OrderDecisionType decisionType) {
		mMainViewModel.requestAcceptOrRefuse(decisionType);
		if (decisionType == Packets.OrderDecisionType.Reject) {
			mMainViewModel.clearTempCallInfo();
			mMainViewModel.setWaitingZone(null, false);
		}
	}


	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissPopupDialog() / tag : " + tag);
		//배차 요청 실패
		if (tag.equals(DIALOG_TAG_FAILURE)) {
			this.finishActivity();
			this.resetCallInfo();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			//배차 거절
			case R.id.btn_reject:
				WavResourcePlayer.getInstance(this).play(R.raw.voice_126);
				this.requestAcceptOrRefuse(Packets.OrderDecisionType.Reject);
				this.resetCallInfo();
				this.finishActivity();
				break;

			//배차 요청
			case R.id.btn_request:
				hasGotResponse = false;
				super.startLoadingProgress();
				setViewsAsWaitingResponse(null);
				WavResourcePlayer.getInstance(this).play(R.raw.voice_124);
				this.requestAcceptOrRefuse(Packets.OrderDecisionType.Request);
				needToRequestToRefuseWhenCloseActivity = false;

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						LogHelper.e("hasGotResponse : " + hasGotResponse);
						if (!hasGotResponse) {
							WavResourcePlayer.getInstance(CallReceivingActivity.this).play(R.raw.voice_122);
							showFailurePopup();
						}
					}
				}, 10000);

				break;
		}
	}

	private void showFailurePopup() {
		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_FAILURE)
				.setContent(getString(R.string.alloc_msg_failed_call))
				.setBtnLabel(getString(R.string.common_confirm), null)
				.setDismissSecond(3)
				.build();
		showPopupDialog(popup);
	}

}
