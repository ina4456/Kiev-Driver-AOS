package com.kiev.driver.aos.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.gun0912.tedpermission.TedPermission;

public class CallManager {

	private static CallManager sInstance;
	private boolean callByApp;
	private TelephonyManager mTelephonyManager;

	public static CallManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new CallManager(context);
		}
		return sInstance;
	}

	private CallManager(Context context) {
		mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
	}

	public void call(Context context, String targetNumber, boolean useSpeakerPhone) {
		if (TedPermission.isGranted(context, Manifest.permission.CALL_PHONE)) {

			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + targetNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

			PhoneStateReceiver phoneStateListener = new PhoneStateReceiver(context, useSpeakerPhone);
			mTelephonyManager.listen(phoneStateListener, phoneStateListener.LISTEN_CALL_STATE);
			callByApp = true;
		} else {
			LogHelper.e("permission is not granted");
		}
	}

	public class PhoneStateReceiver extends PhoneStateListener {
		private Context context;
		private boolean useSpeakerPhone;

		public PhoneStateReceiver(Context context, boolean useSpeakerPhone) {
			this.context = context;
			this.useSpeakerPhone = useSpeakerPhone;
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

			LogHelper.e("onCallStateCahnged() : " + state + " / " + incomingNumber);
			switch (state) {

				case TelephonyManager.CALL_STATE_OFFHOOK:
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}

					LogHelper.e("onCallStateCahnged() CALL_STATE_OFFHOOK : " + state + " / " + incomingNumber);
					if (useSpeakerPhone && callByApp) {
						audioManager.setMode(AudioManager.MODE_IN_CALL);

						LogHelper.e("isHeadsetOn(context) : " + isHeadsetOn(context));
						if (isHeadsetOn(context)) {
							audioManager.setSpeakerphoneOn(false);
						} else {
							audioManager.setSpeakerphoneOn(true);
						}
						callByApp = false;
					}
					break;

				case TelephonyManager.CALL_STATE_IDLE:
					LogHelper.e("onCallStateCahnged() CALL_STATE_IDLE : " + state + " / " + incomingNumber);

					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setSpeakerphoneOn(false);
					break;
			}
		}
	}

	private boolean isHeadsetOn(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		if (am == null)
			return false;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn();
		} else {
			AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

			for (int i = 0; i < devices.length; i++) {
				AudioDeviceInfo device = devices[i];

				if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
						|| device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
						|| device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
						|| device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
					return true;
				}
			}
		}
		return false;
	}
}
