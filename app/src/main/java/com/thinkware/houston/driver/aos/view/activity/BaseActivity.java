package com.thinkware.houston.driver.aos.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MenuItem;

import com.gun0912.tedpermission.TedPermission;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.model.Popup;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.service.FloatingViewService;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.view.fragment.PopupDialogFragment;
import com.thinkware.houston.driver.aos.viewmodel.ConfigViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class BaseActivity  extends AppCompatActivity {

	private static final int REQUEST_CODE_OVERLAY = 1234;
	protected MainApplication mMainApplication;
	private ConfigViewModel mConfigViewModel;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainApplication = (MainApplication)this.getApplicationContext();

		ConfigViewModel.Factory callFactory = new ConfigViewModel.Factory(mMainApplication);
		mConfigViewModel = ViewModelProviders.of(this, callFactory).get(ConfigViewModel.class);
		subscribeToViewModel(mConfigViewModel);
	}

	private void subscribeToViewModel(ConfigViewModel configViewModel) {
		configViewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
			}
		});
	}

	@Override
	protected void onResume() {
		LogHelper.e("onResume()");
		super.onResume();
		this.setCurrentActivity(this);

		//권한 체크 액티비티 실행
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			LogHelper.e("Marshmallow 이상 권한 체크");

			if (!isAllGrantedPermission()) {
				PermissionActivity.startActivity(this);
			}
		} else {
			LogHelper.e("Marshmallow 미만 권한 체크 안함");

			/*boolean hasConfirmedPermission = SF.GetLocalDataBoolean(Constants.SP_PERMISSION_CONFIRMED);
			LogHelper.e("confirm : " + hasConfirmedPermission);
			if (!hasConfirmedPermission){
				LogHelper.e("first time");
				com.anyall_taxi.driver.aos.ui.PermissionActivity.startActivity(this);
			}*/
		}


		// TODO: 2019. 2. 9. 플로팅 뷰가 내비를 가린다는 이유로 제외, 추후 시나리오 정리 후 추가
		// TODO: 2019. 2. 20. 배차 후 승객 탑승전까지 전화버튼만 노출되도록 수정 추가 함.
		unbindFloatingStatusService();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogHelper.e("onStop()");
		LogHelper.e("mMainApplication.isBackground() :  " + mMainApplication.isBackground());

		if (mMainApplication.isBackground()) {
			//LogHelper.d(">> bindFloatingStatusService() : " + mMainApplication.getCurrentActivity().getClass().getSimpleName());
			Activity currentActivity = mMainApplication.getCurrentActivity();
			if (!(currentActivity instanceof PopupActivity)
					&& !(currentActivity instanceof LoginActivity)
					&& !(currentActivity instanceof SplashActivity)) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (Settings.canDrawOverlays(this)) {
						bindFloatingStatusService();
					}
				} else {
					bindFloatingStatusService();
				}
			}
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void showPopupDialog(Popup popup) {
		if (!isFinishing()) {
			PopupDialogFragment dialogFragment = PopupDialogFragment.newInstance(popup);
			FragmentManager fragmentManager = getSupportFragmentManager();

			PopupDialogFragment popupDialogFragment = (PopupDialogFragment)fragmentManager.findFragmentByTag(popup.getTag());
			if (popupDialogFragment != null) {
				fragmentManager.beginTransaction()
						.remove(popupDialogFragment)
						.commit();
			}

			fragmentManager.beginTransaction()
					.add(dialogFragment, popup.getTag())
					.commitAllowingStateLoss();
		}
	}

	public void startLoadingProgress() {
		((MainApplication)getApplication()).progressOn(this);
	}

	public void startLoadingProgress(String msg) {
		((MainApplication)getApplication()).progressOn(this, msg);
	}

	public void finishLoadingProgress() {
		((MainApplication)getApplication()).progressOff();
	}


	public boolean isAllGrantedPermission() {
		LogHelper.e("★★★★★★  isAllGrantedPermission  ");

		//기본 권한 위치, 전화, SMS
		boolean allGranted = TedPermission.isGranted(this
				, Manifest.permission.ACCESS_COARSE_LOCATION
				, Manifest.permission.ACCESS_FINE_LOCATION
				, Manifest.permission.CALL_PHONE
				, Manifest.permission.READ_PHONE_STATE);
		//, Manifest.permission.SEND_SMS);


		//배터리 최적화 무시
		boolean isIgnoringBatteryOptimizations = false;
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm != null) {
			isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
		}

		//다른 앱 위에 그리기
		boolean canDrawOverlays = false;
		if (Settings.canDrawOverlays(this)) {
			canDrawOverlays = true;
		}

		LogHelper.e("allGranted : " + allGranted + " / isIgnoringBatteryOptimizations : " + isIgnoringBatteryOptimizations + " / canDrawOverlays : " + canDrawOverlays);

		return allGranted && isIgnoringBatteryOptimizations && canDrawOverlays;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_OVERLAY) {
			if (Settings.canDrawOverlays(this)) {
				LogHelper.e("다른 앱 위에 그리기 권한 허용됨");
				LogHelper.e("resultCode : " + resultCode);
				/*if (SV.mMainService != null)
					if (mMainApplication.isBackground()) {
						//bindFloatingStatusService();
					}*/
			} else {
				LogHelper.e("다른 앱 위에 그리기 권한 허용 되지 않음");
			}
		}
	}

	public void setCurrentActivity(Activity activity) {
		mMainApplication.setCurrentActivity(activity);
	}

	/**
	 * FloatingService Binding..
	 */
	public void bindFloatingStatusService() {
		LogHelper.d(">> bindFloatingStatusService()");
		// TODO: 2019-06-14 로그아웃, 영업 종료, recent list 스와이프하여 종료 한 경우 서비스 실행 제외
		if (mConfigViewModel.hasUserLoggedIn() && mConfigViewModel.isFloatingBtnUse()) {
			Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
			this.startService(intent);
//				this.bindService(intent, floatingServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void unbindFloatingStatusService() {
		LogHelper.e("unbindFloatingStastusService()=-====1 : " + (mFloatingViewService == null));
		this.stopService(new Intent(getApplicationContext(), FloatingViewService.class));
	}

	/**
	 * bindService 미사용으로 일단 주석 처리
	 */
	//		if (mFloatingViewService != null) {
//			LogHelper.e("unbindFloatingStastusService()------2");
//			this.unbindService(floatingServiceConnection);
//			mFloatingViewService.stopSelf();
//			mFloatingViewService = null;
//		}
	// FloatingStatus local service
	private FloatingViewService mFloatingViewService;
	private ServiceConnection floatingServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
		                               IBinder service) {
			LogHelper.e(">> ServiceConnected : FloatingViewService");
			FloatingViewService.LocalBinder binder = (FloatingViewService.LocalBinder) service;
			mFloatingViewService = binder.getService();
			//mainApplication.startWakeUpService(mFloatingViewService, Constants.SERVICE_ID_FLOATING);
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			//mainApplication.stopWakUpService(mFloatingViewService, Constants.SERVICE_ID_FLOATING);
			LogHelper.e(">> onServiceDisconnected : FloatingViewService");
			mFloatingViewService = null;
		}
	};
}





