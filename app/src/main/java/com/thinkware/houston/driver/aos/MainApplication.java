package com.thinkware.houston.driver.aos;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.skt.Tmap.TMapTapi;
import com.thinkware.houston.driver.aos.repository.Repository;
import com.thinkware.houston.driver.aos.repository.local.AppDatabase;
import com.thinkware.houston.driver.aos.service.ScenarioService;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.view.activity.LoginActivity;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

	private static final int MSG_CODE_RESTART_SERVICE = 9999;

	private AppExecutors mAppExecutors;
	private ArrayList<Activity> activities;
	private boolean isScenarioServiceConnected;
	private boolean isCalledUnbindService;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		LogHelper.setTag(getString(R.string.app_name));
		LogHelper.enableDebug(BuildConfig.DEBUG);
//		LogHelper.enableDebug(true);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		Stetho.initializeWithDefaults(this);
		checkTMapKeyCertified();

		mAppExecutors = new AppExecutors();
		activities = new ArrayList<>();
		registerActivityLifecycleCallbacks(this);
	}

	public AppDatabase getDatabase() {
		return AppDatabase.getInstance(this, mAppExecutors);
	}

	public Repository getRepository() {
		return Repository.getInstance(getDatabase(), mAppExecutors, this);
	}

	public AppExecutors getAppExecutors() {
		return mAppExecutors;
	}


	public ScenarioService getScenarioService() {
		return scenarioService;
	}
	public void setScenarioService(ScenarioService scenarioService) {
		this.scenarioService = scenarioService;
	}

	public void initScenarioService() {
		isScenarioServiceConnected = false;
		LogHelper.d("initScenarioService : " + (scenarioService == null));
		if (scenarioService == null) {
			Intent intent = new Intent(this, ScenarioService.class);
			startService(intent);
			this.bindService();
		}
	}


	public boolean isScenarioServiceConnected(){
		return isScenarioServiceConnected;
	}

	private void bindService() {
		LogHelper.d("bindService()");
		unbindService();
		Intent intent = new Intent(this, ScenarioService.class);
		startService(intent);
		bindService(intent, mScenarioServiceConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		isCalledUnbindService = true;
		LogHelper.e("unBindService() : " + (scenarioService == null));
		if (scenarioService != null) {
			//scenarioService.stopSelf();
			Intent intent = new Intent(this, ScenarioService.class);
			stopService(intent);
			try {
//				scenarioService.unbindService(mScenarioServiceConnection);
				unbindService(mScenarioServiceConnection);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			scenarioService = null;
		}
	}


	/**
	 * ScenarioService
	 */
	private ScenarioService scenarioService = null;
	private ServiceConnection mScenarioServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			LogHelper.e("ScenarioService onServiceConnected()");
			ScenarioService.LocalBinder binder = (ScenarioService.LocalBinder) service;
			scenarioService = binder.getService();
			if (scenarioService != null) {
				isScenarioServiceConnected = true;
				setScenarioService(scenarioService);
				// FIXME: 2019-10-07
				//mainApplication.startWakeUpService(scenarioService, Constants.SERVICE_ID_SCENARIO);

				LoginActivity activity = (LoginActivity) getActivity(LoginActivity.class);
				if (activity != null) {
					activity.onScenarioServiceConnected();
				}
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			LogHelper.e("ScenarioService onServiceDisconnected()");
			isScenarioServiceConnected = false;
			if (scenarioService != null) {
				// FIXME: 2019-10-07
				//mainApplication.stopWakUpService(scenarioService, Constants.SERVICE_ID_SCENARIO);

				scenarioService.stopSelf();
				scenarioService = null;
			}

			if (!isCalledUnbindService) {
				handler.sendEmptyMessageDelayed(MSG_CODE_RESTART_SERVICE, 2000);
			}

		}
	};


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_CODE_RESTART_SERVICE:
					LogHelper.e("handleMessage : " + msg.what);
					MainApplication.this.bindService();
					break;
			}
		}
	};


	public Activity getActivity(Class<?> cls) {
		if (cls != null) {
			String name = cls.getSimpleName();
			for (Activity act : activities) {
				if (act.getClass().getSimpleName().equals(name)) {
					return act;
				}
			}
		}
		return null;
	}

	private Activity mCurrentActivity = null;
	public Activity getCurrentActivity(){
		return mCurrentActivity;
	}
	public void setCurrentActivity(Activity mCurrentActivity){
		this.mCurrentActivity = mCurrentActivity;
	}

	public enum AppStatus {
		BACKGROUND,                // app is background
		RETURNED_TO_FOREGROUND,    // app returned to foreground(or first launch)
		FOREGROUND;                // app is foreground
	}
	// running activity count
	private int running = 0;
	private Activity lastActivity;
	private AppStatus appStatus;
	private boolean wasBackground;

	// check if app is return foreground
	public boolean isReturnedForeground() {
		return appStatus.ordinal() == AppStatus.RETURNED_TO_FOREGROUND.ordinal();
	}

	public boolean isBackground() {
		return appStatus.ordinal() == AppStatus.BACKGROUND.ordinal();
	}

	public boolean wasBackground() {
		return wasBackground;
	}

	public void setWasBackground(boolean wasBackground) {
		this.wasBackground = wasBackground;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		LogHelper.e("onActivityCreated");
		activities.add(activity);
		LogHelper.e("activitied size : " + activities.size());
	}

	@Override
	public void onActivityStarted(Activity activity) {
		LogHelper.e("onActivityStarted : " + appStatus + " / " + running);
		if (++running == 1) {
			// running activity is 1,
			// app must be returned from background just now (or first launch)
			appStatus = AppStatus.RETURNED_TO_FOREGROUND;
		} else if (running > 1) {
			// 2 or more running activities,
			// should be foreground already.
			appStatus = AppStatus.FOREGROUND;
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {
	}

	@Override
	public void onActivityPaused(Activity activity) {
		//팝업 액티비티가 아닌 경우에만 lastActivity 로 할당
	}

	@Override
	public void onActivityStopped(Activity activity) {
		LogHelper.e("onActivityStopped : " + appStatus + " / " + running);
		if (--running == 0) {
			appStatus = AppStatus.BACKGROUND;
			LogHelper.e("onActivityStopped222 : " + appStatus);
			if (lastActivity != null) {
				LogHelper.e("last activity : " + lastActivity.getClass());
			}
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		activities.remove(activity);
	}


	//최초 1회만 콜백됨.
	private void checkTMapKeyCertified() {
		//테스트키 : c0e83049-344e-37b9-af0c-f0dec57d1e4a
		//app 키	: 8a071793-4686-3167-8bb1-8bed20a70788
		new Thread() {
			public void run() {
				//타워소프트
				//mTMapTapi.setSKTMapAuthentication("8a071793-4686-3167-8bb1-8bed20a70788");
				//개인
				TMapTapi tmapApi = new TMapTapi(getApplicationContext());
				tmapApi.setSKTMapAuthentication("c091d13f-9d61-46c9-b22e-0c09b77334f3");
				tmapApi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
					@Override
					public void SKTMapApikeySucceed() {
						LogHelper.e("tmap authentication is succeed");
					}

					@Override
					public void SKTMapApikeyFailed(String s) {
						LogHelper.e("tmap authentication is failed " + s);
					}
				});
			}
		}.start();
	}

	public void closeApplication(Activity activity) {
		unbindService();
		ActivityCompat.finishAffinity(activity);
		System.exit(0);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}

	private AppCompatDialog progressDialog;
	public void progressOn(Activity activity) {
		progressOn(activity, "");
	}

	public void progressOn(Activity activity, String message) {
		if (activity == null || activity.isFinishing()) {
			return;
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			setProgressMsg(message);
		} else {
			progressDialog = new AppCompatDialog(activity, R.style.ProgressDialogStyle); //, R.style.Theme_AppCompat_Translucent);
			progressDialog.setCancelable(false);
			progressDialog.setContentView(R.layout.view_loading_progress);
			progressDialog.show();
			setProgressMsg(message);
		}
	}

	private void setProgressMsg(String message) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			return;
		}
		TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
		if (!TextUtils.isEmpty(message)) {
			tv_progress_message.setText(message);
		}
	}

	public void progressOff() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (final IllegalArgumentException e) {
			LogHelper.e("IllegalArgumentException progressDialog ");
			e.printStackTrace();
		} catch (final Exception e) {
			LogHelper.e("Exception progressDialog ");
		} finally {
			progressDialog = null;
		}
	}
}
