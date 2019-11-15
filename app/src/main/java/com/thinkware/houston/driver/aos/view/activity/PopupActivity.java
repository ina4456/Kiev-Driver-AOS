package com.thinkware.houston.driver.aos.view.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.model.Popup;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.view.fragment.PopupDialogFragment;
import com.thinkware.houston.driver.aos.view.activity.navigationdrawer.NoticeActivity;

import androidx.annotation.Nullable;

public class PopupActivity extends BaseActivity implements PopupDialogFragment.PopupDialogListener {

	private PopupDialogListener listener;

	public static void startActivity(Context context, Popup popup) {
		Intent intent = new Intent(context, PopupActivity.class);
		intent.putExtra(Constants.INTENT_KEY_POPUP_DIALOG, popup);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		try {
			pendingIntent.send();
		} catch (PendingIntent.CanceledException e) {
			e.printStackTrace();
		}
	}

	public interface PopupDialogListener {
		void onDismissPopupDialog(String tag, Intent intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Popup popup = (Popup) getIntent().getSerializableExtra(Constants.INTENT_KEY_POPUP_DIALOG);
		if (popup != null) {
			showPopupDialog(popup);
		}
		//listener = (PopupDialogListener) getCallingActivity();
	}



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Popup popup = (Popup) intent.getSerializableExtra(Constants.INTENT_KEY_POPUP_DIALOG);
		if (popup != null) {
			showPopupDialog(popup);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogHelper.e("onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);    //다이얼로그 종료시 전환 애니메이션 제거
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissPopupDialog() : " + tag);
		if (tag.equals(Constants.DIALOG_TAG_MESSAGE) && intent != null) {
			if (intent.getBooleanExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, false)) {
				NoticeActivity.startActivity(this, false);
			}
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		LogHelper.e("onBackPressed()");
		finish();
	}
}