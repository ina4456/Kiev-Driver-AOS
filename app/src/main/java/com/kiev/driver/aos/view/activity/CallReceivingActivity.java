package com.kiev.driver.aos.view.activity;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;

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

import java.util.List;

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


	public static void startActivity(Context context, boolean isFromWaitingCallList) {
		final Intent intent = new Intent(context, CallReceivingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(INTENT_KEY_IS_WC, isFromWaitingCallList);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		turnOnScreen();
		super.onCreate(savedInstanceState);
		LogHelper.e("onCreate()");

		mMainViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_receiving);
		mBinding.setLifecycleOwner(this);

		Intent passedIntent = getIntent();
		isFromWaitingCallList = passedIntent.getBooleanExtra(INTENT_KEY_IS_WC, false);

		subscribeViewModel();
	}

	private void turnOnScreen() {
		LogHelper.e("turnOnScreen()");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
			setShowWhenLocked(true);
			setTurnScreenOn(true);
			KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			if(keyguardManager != null)
				keyguardManager.requestDismissKeyguard(this, null);
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
					WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		}
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

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogHelper.e("onPause()");

		if (countDownTimer != null) {
			countDownTimer.cancel();
		}

		mMainApplication.setCurrentActivity(null);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
		LogHelper.e("onDestroy()");
	}

	@Override
	public void onBackPressed() {
		LogHelper.e("onBackPressed()");
		this.refuseCall();
	}


	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		LogHelper.e("onUserLeaveHint()");

		//홈 버튼을 눌러 콜 수신을 거부하는 경우.
		LogHelper.e("needToRequestToRefuseWhenCloseActivity : " + needToRequestToRefuseWhenCloseActivity);
		if (needToRequestToRefuseWhenCloseActivity) {
			this.refuseCall();
		}
	}

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			LogHelper.e("onReceive()");

			if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				LogHelper.e("The screen has turned off");

				if (needToRequestToRefuseWhenCloseActivity) {
					refuseCall();
				}
			}
		}
	};

	private void subscribeViewModel() {
		LiveData<Call> callInfo = mMainViewModel.getCallInfoLive();
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
						case Constants.CALL_STATUS_RECEIVING:
							LogHelper.e("onChanged-Call : CALL_STATUS_RECEIVING");
							initViews(call);
							break;

						case Constants.CALL_STATUS_ALLOCATED:
							LogHelper.e("onChanged-Call : CALL_STATUS_ALLOCATED");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							needToRequestToRefuseWhenCloseActivity = false;
							//mMainViewModel.updateCallInfo(call);
							//refreshUiWithIfCallInfoExist();
							finish();
							break;

						case Constants.CALL_STATUS_ALLOCATION_FAILED:
							LogHelper.i("onChanged-Call : CALL_STATUS_ALLOCATION_FAILED");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							showFailurePopup();
							needToRequestToRefuseWhenCloseActivity = false;
							refreshUiWithIfCallInfoExist();
							break;

						case Constants.CALL_STATUS_ALLOCATED_WHILE_GETON:
							LogHelper.i("onChanged-Call : CALL_STATUS_ALLOCATED_WHILE_GETON");
							callInfo.removeObserver(this);
							hasGotResponse = true;
							refreshUiWithIfCallInfoExist();
							finishActivity();
							break;

						default:
							LogHelper.i("onChanged-Call : default");
							break;
					}
				}
			}
		});

		LiveData<Configuration> config = mMainViewModel.getConfiguration();
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
	}

	private void initViews(Call tempCall) {
		mBinding.btnReject.setOnClickListener(this);
		mBinding.btnRequest.setOnClickListener(this);

		//RatingBar
		LayerDrawable stars = (LayerDrawable) mBinding.rbCallGrade.getProgressDrawable();
		setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(this, R.color.colorYellow));
		setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(this, R.color.colorYellow));
		setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(this, R.color.colorGray07));

		//LogHelper.e("temp call : " + tempCall);
		if (!isFromWaitingCallList) {
			LogHelper.e("normal call..");
			setViews(tempCall);

			int displayTime = mMainViewModel.getTimeForDisplayingCallBroadcast();
			LogHelper.e("displayTime : " + displayTime);
			if (displayTime <= 0) {
				displayTime = 5;
			}
			startCountDown(displayTime);

			//거리 표시
			int distance = mMainViewModel.getDistance(tempCall.getDepartureLat(), tempCall.getDepartureLong());
			tempCall.setDistance(distance);
			//LogHelper.e("distance : " + distance);
			mBinding.tvDistance.setText(tempCall.getCallDistanceToDeparture());

			//콜 등급 표시
			if (tempCall.getCallClass() == 0) {
				mBinding.rbCallGrade.setVisibility(View.GONE);
			} else {
				mBinding.rbCallGrade.setRating(tempCall.getCallClass());
			}

		} else {

			LogHelper.e("callInfoFromWaitingCallList..");
			setViewsAsWaitingResponse(tempCall);

			MutableLiveData<ResponseWaitCallOrderInfoPacket> waitingCallInfo = mMainViewModel.requestWaitingCallOrder(tempCall);
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


	private void refuseCall() {
		LogHelper.i("refuseCall()");
		needToRequestToRefuseWhenCloseActivity = false;
		this.requestAcceptOrRefuse(Packets.OrderDecisionType.Reject);
		this.refreshUiWithIfCallInfoExist();
		this.finishActivity();
	}

	private void requestAcceptOrRefuse(Packets.OrderDecisionType decisionType) {
		mMainViewModel.requestAcceptOrRefuse(decisionType);
		if (decisionType == Packets.OrderDecisionType.Reject) {
			mMainViewModel.clearTempCallInfo();
			mMainViewModel.setWaitingZone(null, false);
		}
	}

	private void refreshUiWithIfCallInfoExist() {
		LogHelper.d("refreshUiWithIfCallInfoExist()");
		mMainViewModel.refreshUiWithIfCallInfoExist();
	}

	public boolean isApplicationSentToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	private void finishActivity() {
		needToRequestToRefuseWhenCloseActivity = false;
		boolean wasBackground = mMainApplication.wasBackground();
		if (wasBackground) {
			moveTaskToBack(true);
			finish();
		} else {
			if (!isFinishing()) {
				finish();
			} else {
				super.onBackPressed();
			}
		}
	}

	private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			DrawableCompat.setTint(drawable, color);
		} else {
			drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		}
	}

	private void startCountDown(int displayTime) {
		countDownTimer = new CountDownTimer((displayTime + 1) * COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
			@Override
			public void onTick(long l) {
				setTextWithCount((int) (l / 1000));
			}

			@Override
			public void onFinish() {
				LogHelper.e("onFinish()");
				refuseCall();
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
			setViews(call);
		}
	}

	private void setViews(Call call) {
		LogHelper.e("setViews : " + call);
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

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissPopupDialog() / tag : " + tag);
		//배차 요청 실패
		if (tag.equals(DIALOG_TAG_FAILURE)) {
			this.finishActivity();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			//배차 거절
			case R.id.btn_reject:
				WavResourcePlayer.getInstance(this).play(R.raw.voice_126);
				refuseCall();
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
