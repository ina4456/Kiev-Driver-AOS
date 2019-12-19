package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityMyinfoChangeVehicleNumberBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.activity.LoginActivity;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.MyInfoViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class MyInfoChangeVehicleNumberActivity extends BaseActivity implements View.OnClickListener, PopupDialogFragment.PopupDialogListener, TextWatcher {

	private MyInfoViewModel mViewModel;
	private ActivityMyinfoChangeVehicleNumberBinding mBinding;
	private static final String DIALOG_TAG_VEHICLE_NUMBER_CHANGE_CANCEL = "dialog_tag_vehicle_number_change_cancel";
	private static final String DIALOG_TAG_VEHICLE_NUMBER_CHANGE = "dialog_tag_vehicle_number_change";
	private static final String DIALOG_TAG_VEHICLE_NUMBER_EMPTY = "dialog_tag_vehicle_number_empty";

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, MyInfoChangeVehicleNumberActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_myinfo_change_vehicle_number);
		mViewModel = new ViewModelProvider(this, new MyInfoViewModel.Factory(getApplication()))
				.get(MyInfoViewModel.class);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mViewModel);

		mBinding.etVehicleNumber.addTextChangedListener(this);
		mBinding.etVehicleNumber.requestFocus();

		mBinding.btnChange.setOnClickListener(this);
		initToolbar();
	}


	private void initToolbar() {
		setSupportActionBar(mBinding.myInfoChangeVehicleNumberToolbar.toolbar);
		mBinding.myInfoChangeVehicleNumberToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.myInfoChangeVehicleNumberToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.mi_vehicle_number));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}


	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
	@Override
	public void afterTextChanged(Editable s) {
		LogHelper.e("s.toString : "  + s.toString());
		LogHelper.e("s.length : "  + s.length());
		if (s.length() != 0 && s.length() >= 4) {
			mBinding.btnChange.setEnabled(true);
		} else {
			mBinding.btnChange.setEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				showPopup(DIALOG_TAG_VEHICLE_NUMBER_CHANGE_CANCEL
						, getString(R.string.mi_msg_change_vehicle_number_msg_cancel_back));
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		showPopup(DIALOG_TAG_VEHICLE_NUMBER_CHANGE_CANCEL
				, getString(R.string.mi_msg_change_vehicle_number_msg_cancel_back));
	}

	private void showPopup(String tag, String content) {
		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, tag)
				.setBtnLabel(getString(R.string.common_confirm), null)
				.setContent(content)
				.build();
		showPopupDialog(popup);
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		if (intent != null) {
			switch (tag) {
				//차량 변경
				case DIALOG_TAG_VEHICLE_NUMBER_CHANGE:
					LoginActivity.startActivity(MyInfoChangeVehicleNumberActivity.this);
					mViewModel.setNeedAutoLogin(false);
					finish();
					break;

				//차량 변경 취소
				case DIALOG_TAG_VEHICLE_NUMBER_CHANGE_CANCEL:
					boolean isPressedPositiveBtn = intent.getBooleanExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, false);
					if (isPressedPositiveBtn)
						finish();
					break;

				default:
					break;
			}
		}

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_change) {
			// TODO: 2019. 3. 4.  내부값 로그아웃 및 서버 로그아웃 전송 처리 추가 필요

			String vehicleNumber = mBinding.etVehicleNumber.getText().toString().trim();
			String originVehicleNumber = "";
			Configuration config =  mViewModel.getConfiguration().getValue();
			if (!vehicleNumber.isEmpty()) {
				if (config != null) {
					originVehicleNumber = config.getCarNumber();
				}

				if (!originVehicleNumber.isEmpty() && vehicleNumber.equals(originVehicleNumber)) {
					showPopup(DIALOG_TAG_VEHICLE_NUMBER_CHANGE_CANCEL
							, getString(R.string.mi_msg_change_vehicle_number_msg_cancel_back));
				} else {
					mViewModel.changeVehicleNumber(vehicleNumber);
					showPopup(DIALOG_TAG_VEHICLE_NUMBER_CHANGE
							, getString(R.string.mi_msg_change_vehicle_number_msg_login_again));
				}
			} else {
				showPopup(DIALOG_TAG_VEHICLE_NUMBER_EMPTY
						, getString(R.string.mi_msg_change_vehicle_number_msg_empty));
			}
		}
	}
}
