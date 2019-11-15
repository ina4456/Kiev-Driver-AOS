package com.thinkware.houston.driver.aos.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.ViewFloatingBinding;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.repository.Repository;
import com.thinkware.houston.driver.aos.util.CallManager;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.view.activity.MainActivity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;


public class FloatingViewService extends LifecycleService implements View.OnTouchListener, View.OnClickListener {
	private WindowManager windowManager;
	private ViewFloatingBinding mBinding;
	private View statusView;
	private float offsetX;
	private float offsetY;
	private int originalXPos;
	private int originalYPos;
	private boolean moving;
	private View topLeftView;
	private final int touchSlop = 10;

	private Repository mRepository;
	private Call mCall;
	private Configuration mConfig;


	private final IBinder binder = new FloatingViewService.LocalBinder();
	public class LocalBinder extends Binder {
		public FloatingViewService getService() {
			return FloatingViewService.this;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
		LogHelper.d(">> onBind()");
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogHelper.e("onCreate()");

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater != null) {
			mBinding = DataBindingUtil.inflate(inflater, R.layout.view_floating, null, false);
			statusView = mBinding.getRoot();
			statusView.setOnTouchListener(this);

			mBinding.btnFloatingMain.setOnClickListener(this);
			mBinding.btnFloatingMain.setOnTouchListener(this);
			mBinding.btnBoardingOrAlighting.setOnClickListener(this);
			mBinding.btnBoardingOrAlighting.setOnTouchListener(this);
			mBinding.btnCallPassenger.setOnClickListener(this);
			mBinding.btnCallPassenger.setOnTouchListener(this);

			mRepository = ((MainApplication)getApplication()).getRepository();
			mConfig = mRepository.getConfig();

			subscribeCallInfo(mRepository);
			addStatusView();
		}
	}

	private void subscribeCallInfo(Repository repository) {
		repository.getConfigLive().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged-configuration : " + configuration.toString());
				mConfig = configuration;
				WindowManager.LayoutParams params = (WindowManager.LayoutParams) statusView.getLayoutParams();
				if (params != null) {
					params.x = configuration.getFloatingBtnLastX();
					params.y = configuration.getFloatingBtnLastY();
					windowManager.updateViewLayout(statusView, params);
				}
			}
		});

		mRepository.getCallInfoLive().observe(FloatingViewService.this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				if (call != null) {
					LogHelper.e("onChanged-call : " + call.toString());
					mCall = call;

					mBinding.btnFloatingMain.setVisibility(mConfig.isUseMainBtn() ? View.VISIBLE : View.GONE);
					if (mConfig.isUseBoardingAlightingBtn()) {
						setVisibilityBoardingAlightingBtn(call.getCallStatus());
					}
					if (mConfig.isUseCallBtn()) {
						setVisibilityCallButton(call);
					}
				}
			}
		});
	}



	//탑승 or 하차 버튼 표시 여부
	private void setVisibilityBoardingAlightingBtn(int status) {
		LogHelper.e("setVisibilityBoardingAlightingBtn() : " + status);
		switch (status) {
			case Constants.CALL_STATUS_ALLOCATED:
				mBinding.btnBoardingOrAlighting.setVisibility(View.VISIBLE);
				mBinding.btnBoardingOrAlighting.setText(getString(R.string.floating_btn_boarding));

				break;

			case Constants.CALL_STATUS_BOARDED:
			case Constants.CALL_STATUS_BOARDED_WITHOUT_DESTINATION:
				mBinding.btnBoardingOrAlighting.setText(getString(R.string.floating_btn_alighting));
				Drawable backgroundDrawable;
				if(Build.VERSION.SDK_INT >= 21){
					backgroundDrawable = getResources().getDrawable(R.drawable.selector_bg_floating_btn_alighting, getTheme());
				} else {
					backgroundDrawable = getResources().getDrawable(R.drawable.selector_bg_floating_btn_alighting);
				}
				mBinding.btnBoardingOrAlighting.setBackground(backgroundDrawable);
				mBinding.btnBoardingOrAlighting.setTextColor(getResources().getColorStateList(R.color.selector_tc_floating_alight_btn));
				mBinding.btnBoardingOrAlighting.setVisibility(View.VISIBLE);
				mBinding.btnCallPassenger.setVisibility(View.GONE);
				break;

			default:
				mBinding.btnBoardingOrAlighting.setVisibility(View.GONE);
				break;
		}
	}

	//전화 버튼 표시 여부
	private void setVisibilityCallButton(Call call) {
		LogHelper.e("setVisibilityCallButton()");
		if (isNeedToDisplayCallButton(call)) {
			mBinding.btnCallPassenger.setVisibility(View.VISIBLE);
		} else {
			mBinding.btnCallPassenger.setVisibility(View.GONE);
		}
	}

	//전화 버튼 표시 여부
	private boolean isNeedToDisplayCallButton(Call call) {
		if (call != null) {
			String passengerPhoneNumber = call.getPassengerPhoneNumber();
			int callStatus = call.getCallStatus();
			LogHelper.e("passengerPhoneNumber : " + passengerPhoneNumber);
			LogHelper.e("callStatus : " + callStatus);

			if (passengerPhoneNumber != null && !passengerPhoneNumber.isEmpty() && !passengerPhoneNumber.equals("-1")) {
				if (callStatus == Constants.CALL_STATUS_ALLOCATED) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogHelper.e("onDestroy");
		stopService();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		LogHelper.e("onTaskRemoved");
		stopService();
	}

	private void stopService() {
		saveLastPosition();
		if (windowManager != null) {
			if (statusView != null && statusView.isAttachedToWindow())
				windowManager.removeView(statusView);

			if (topLeftView != null && topLeftView.isAttachedToWindow())
				windowManager.removeView(topLeftView);
		}

		this.stopSelf();
	}

	private void addStatusView() {
		int layoutFlag;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
		}

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				layoutFlag,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.START | Gravity.TOP;

		saveLastPosition();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowManager.addView(statusView, params);

		topLeftView = new View(this);
		WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				layoutFlag,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		topLeftParams.gravity = Gravity.START | Gravity.TOP;
		topLeftParams.x = 0;
		topLeftParams.y = 0;
		topLeftParams.width = 0;
		topLeftParams.height = 0;
		windowManager.addView(topLeftView, topLeftParams);
	}

	private void enterApplication() {
		Intent intent;
		Activity currentActivity = ((MainApplication)getApplication()).getCurrentActivity();
		if (currentActivity == null) {
			intent = new Intent(FloatingViewService.this, MainActivity.class);
		} else {
			intent = new Intent(FloatingViewService.this, currentActivity.getClass());
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		try {
			PendingIntent.getActivity(FloatingViewService.this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT)
					.send();
		} catch (PendingIntent.CanceledException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_boarding_or_alighting:
				LogHelper.e("버튼명 : " + mBinding.btnBoardingOrAlighting.getText());
				boolean isBoarding = mBinding.btnBoardingOrAlighting.getText().equals(getString(R.string.common_boarding));
				changeCallStatus(isBoarding);
				break;

			case R.id.btn_floating_main:
				enterApplication();
				break;

			case R.id.btn_call_passenger:
				LogHelper.e("call btn clicked");
				String phoneNumber = mCall.getPassengerPhoneNumber();
				CallManager.getInstance(this)
						.call(this, phoneNumber, true);

				break;
		}
	}

	private void changeCallStatus(boolean isBoarding) {
		LogHelper.e("탑승 여부 : " + isBoarding);

		//탑승
		if (isBoarding) {
			mRepository.changeCallStatus(Constants.CALL_STATUS_BOARDED);
			if(mConfig.isUseAutoRouteToDestination()) {
				enterApplication();
			}
		//하차
		} else {
			mRepository.changeCallStatus(Constants.CALL_STATUS_VACANCY);
		}
	}


	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getRawX();
			float y = event.getRawY();

			moving = false;

			int[] location = new int[2];
			statusView.getLocationOnScreen(location);

			originalXPos = location[0];
			originalYPos = location[1];

			offsetX = originalXPos - x;
			offsetY = originalYPos - y;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			int[] topLeftLocationOnScreen = new int[2];
			topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

			float x = event.getRawX();
			float y = event.getRawY();

			WindowManager.LayoutParams params = (WindowManager.LayoutParams) statusView.getLayoutParams();

			int newX = (int) (offsetX + x);
			int newY = (int) (offsetY + y);

			if (Math.abs(newX - originalXPos) < 50 && Math.abs(newY - originalYPos) < 50 && !moving) {
				return false;
			}

			params.x = newX - (topLeftLocationOnScreen[0]);
			params.y = newY - (topLeftLocationOnScreen[1]);

			windowManager.updateViewLayout(statusView, params);
			moving = true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (moving && !isInTouchSlop(offsetX + event.getRawX(), offsetY + event.getRawY())) {
				saveLastPosition();
				return true;
			}
		}

		return false;
	}

	private boolean isInTouchSlop(float newX, float newY) {
		if (Math.abs(newX - originalXPos) > touchSlop || Math.abs(newY - originalYPos) > touchSlop) {
			return false;
		}
		return true;
	}

	private void saveLastPosition() {
		if (mConfig != null) {
			WindowManager.LayoutParams params = (WindowManager.LayoutParams) statusView.getLayoutParams();
			if (params != null) {
				mConfig.setFloatingBtnLastX(params.x);
				mConfig.setFloatingBtnLastY(params.y);
			}
			mRepository.updateConfig(mConfig);
		}
	}
}
