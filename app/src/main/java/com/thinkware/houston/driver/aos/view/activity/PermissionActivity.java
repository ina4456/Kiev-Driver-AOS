package com.thinkware.houston.driver.aos.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.ActivityPermissionBinding;
import com.thinkware.houston.driver.aos.model.Popup;
import com.thinkware.houston.driver.aos.repository.local.SharedPreferenceManager;
import com.thinkware.houston.driver.aos.service.ScenarioService;
import com.thinkware.houston.driver.aos.util.GpsHelper;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.view.fragment.PopupDialogFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;


public class PermissionActivity extends AppCompatActivity implements PopupDialogFragment.PopupDialogListener {

    private LocationManager mLocationManager = null;
//    private GPSLocListener mGpsLocListener = null;
    private static final int REQUEST_CODE_OVERLAY = 1234;
	private static final int REQUEST_CODE_IGNORE_OPTIMIZATION = 2345;
	public static final String DIALOG_TAG_PERMISSION = "dialog_tag_permission";
	public static final String DIALOG_TAG_OVERLAYDRAW = "dialog_tag_overlaydraw";

    private ActivityPermissionBinding mBinding;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, PermissionActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				| Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
		mBinding.tvPermissionMsg.setText(String.format(getString(R.string.permission_msg), getString(R.string.app_name)));
		mBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					checkPermission();
				} else {
					SharedPreferenceManager.getInstance(PermissionActivity.this)
							.setData(Constants.SP_PERMISSION_CONFIRMED, true);
					//gpsListenerInit();
					finish();
				}
			}
		});
	}

/*	private void gpsListenerInit() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(mGpsLocListener == null) {
			mGpsLocListener = new GPSLocListener(this);
		}
	}*/

    public void checkPermission() {
	    LogHelper.e("checkPermission()");
	    TedPermission.with(PermissionActivity.this)
			    .setPermissionListener(mPermissionListener)
			    .setDeniedMessage(getString(R.string.permission_msg_common))
			    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
					    , Manifest.permission.ACCESS_FINE_LOCATION
					    , Manifest.permission.CALL_PHONE
					    , Manifest.permission.READ_PHONE_STATE)
					    //, Manifest.permission.SEND_SMS)
			    .check();
    }


	PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            LogHelper.e("onPermissionGranted()--");

            if (TedPermission.isGranted(PermissionActivity.this
		            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
	            MainApplication mainApplication = (MainApplication) getApplication();
	            ScenarioService scenarioService = mainApplication.getScenarioService();
	            scenarioService.setGpsHelper(new GpsHelper(mainApplication));
            }

	        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        if (pm != null) {
		        String packageName = getPackageName();
	            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
		            showPopupForIgnoreBatteryOptimization(packageName);
	            } else {
	                //finish();
		            showPopupForOverlayDraw();
	            }
	        }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
	        //checkPermission();
	        Popup popup = new Popup
			        .Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_PERMISSION)
			        .setContent(getString(R.string.permission_finish_application_msg))
			        .setBtnLabel(getString(R.string.common_confirm_app_close), null)
			        .build();
	        showPopupDialog(popup);
        }
    };

    private void showPopupForIgnoreBatteryOptimization(String packageName) {
	    LogHelper.e("showPopupForIgnoreBatteryOptimization()");
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		    Intent i = new Intent();
		    i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
		    i.setData(Uri.parse("package:" + packageName));
		    startActivityForResult(i, REQUEST_CODE_IGNORE_OPTIMIZATION);
	    }
    }

    private void showPopupForOverlayDraw() {
    	LogHelper.e("showPopupForOverlayDraw()");
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
	        if (!Settings.canDrawOverlays(PermissionActivity.this)) {
		        Popup popup = new Popup
				        .Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_OVERLAYDRAW)
				        .setContent(getString(R.string.permission_overlay_reject_msg))
				        .setBtnLabel(getString(R.string.d_menu_setting), null)
				        .build();
		        showPopupDialog(popup);

	        } else {
	        	finish();
	        }
	    }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
	    /**
	     * Oreo 버그
	     * 1. batteryOptimization : 배터리 최적화 사용을 하지 않게 팝업에서 설정해도 단말 설정 메뉴에 바뀐채로 보이지 않음.
	     * 실제로는 바뀌어 있으며, 재부팅 해야지 정상적으로 보이는 것을 확인함.
	     *
	     * 2. canDrawOverlay : 실행된 설정 화면에서 해당 권한을 허용하고 단말 back 버튼 또는 뒤로가기 버튼을 눌러
	     * 다시 돌아와도 resultCode가 0(cancel) 로 리턴되는 증상이 있음.
	     * 따라서, Settings.canDrawOverlays 도 같이 호출하여 퍼미션 허용 여부를 판단함.
	     */
	    switch (requestCode) {
		    case REQUEST_CODE_IGNORE_OPTIMIZATION:
			    if (resultCode == Activity.RESULT_OK) {
				    LogHelper.e("isIgnoringBattery!!");
				    showPopupForOverlayDraw();
			    } else {
				    LogHelper.e("isIgnoringBattery!! --else");
			    }

			    break;

		    case REQUEST_CODE_OVERLAY:
		    	LogHelper.e("resultCode : " + resultCode);
		    	LogHelper.e("Settings.canDrawOverlays(this) : " + Settings.canDrawOverlays(this));
		    	if (resultCode == Activity.RESULT_OK || Settings.canDrawOverlays(this)) {
				    LogHelper.e("canDrawOverlay");
				    finish();
			    } else {
				    LogHelper.e("canDrawOverlay --else");
			    }
			    break;
		    default:
			    break;
	    }
    }

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
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

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
    	LogHelper.e("onDismissPopupDialog : " + tag);
    	if (tag != null && intent != null) {
		    switch (tag) {
			    case DIALOG_TAG_PERMISSION:
				    ActivityCompat.finishAffinity(this);
				    break;

			    case DIALOG_TAG_OVERLAYDRAW:
				    String packageName = getPackageName();
				    Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
						    Uri.parse("package:" + packageName));
				    startActivityForResult(overlayIntent, REQUEST_CODE_OVERLAY);
				    break;
			    default:
				    break;
		    }
	    }

	}
}



