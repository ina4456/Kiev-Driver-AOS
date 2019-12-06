package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityLoginBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.WavResourcePlayer;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.LoginViewModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.os.Build.VERSION_CODES.M;


public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher,
		PopupDialogFragment.PopupDialogListener {

	public static final String DIALOG_TAG_LOGIN_ERROR = "dialog_tag_login_error";
	public static final String DIALOG_TAG_CORPORATION_SELECTION = "dialog_tag_corporation_selection";
	private ActivityLoginBinding mBinding;
	private LoginViewModel mLoginViewModel;


	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK
		);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoginViewModel = new ViewModelProvider(this, new LoginViewModel.Factory(getApplication()))
				.get(LoginViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
		mBinding.setLifecycleOwner(this);


		MainApplication mainApplication = (MainApplication) getApplication();
		LogHelper.e("isRunning : " + mainApplication.isScenarioServiceConnected());

		if (!mainApplication.isScenarioServiceConnected()) {
			mainApplication.initScenarioService();
		} else {
			subscribeViewModel(mLoginViewModel);
		}

		setListeners();
		setEnableLoginButton();

		if (Build.VERSION.SDK_INT < M) {
			setScrollEdgeGlowColor(this, ContextCompat.getColor(this, R.color.colorYellow02));
		}
	}

	private void setListeners() {
		mBinding.btnLogin.setOnClickListener(this);
		mBinding.clCallCenter.setOnClickListener(this);
		mBinding.btnIndividual.setOnClickListener(this);
		mBinding.btnCorporation.setOnClickListener(this);

		mBinding.etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		mBinding.etPhoneNumber.addTextChangedListener(this);
		mBinding.etVehicleNumber.addTextChangedListener(this);
	}

	private void setEnableLoginButton() {
		boolean isValid = mLoginViewModel.checkValidate(
				mBinding.etPhoneNumber.getText().toString(),
				mBinding.etVehicleNumber.getText().toString());
		mBinding.btnLogin.setEnabled(isValid);
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() != 0) {
			setEnableLoginButton();
		} else {
			mBinding.btnLogin.setEnabled(false);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_login:
				login();
				break;

			case R.id.cl_call_center:
				mLoginViewModel.makePhoneCallToCallCenter(LoginActivity.this);
				break;

			case R.id.btn_individual:
				changeStateTaxiTypeBtn(mBinding.btnIndividual);
				mLoginViewModel.setTaxiType(-1);
				break;

			case R.id.btn_corporation:
				changeStateTaxiTypeBtn(mBinding.btnCorporation);
				showCorporationPopup();
				break;
		}
	}

	private void changeStateTaxiTypeBtn(CheckedTextView pressedBtn) {
		if (!pressedBtn.isChecked()) {
			if (pressedBtn.getId() == R.id.btn_individual) {
				mBinding.btnIndividual.setChecked(true);
				mBinding.btnCorporation.setChecked(false);
				mBinding.btnIndividual.setBackgroundResource(R.drawable.selector_popup_twobtn_02);
				mBinding.btnCorporation.setBackgroundResource(R.drawable.selector_popup_twobtn_01);
				mBinding.btnIndividual.setTextColor(getResources().getColorStateList(R.color.selector_tc_common_black_black30));
				mBinding.btnCorporation.setTextColor(getResources().getColorStateList(R.color.selector_tc_common_yel_yel01));
				mBinding.btnCorporation.setText(getString(R.string.login_corporation));
			} else {
				mBinding.btnIndividual.setChecked(false);
				mBinding.btnCorporation.setChecked(true);
				mBinding.btnIndividual.setBackgroundResource(R.drawable.selector_popup_twobtn_01);
				mBinding.btnCorporation.setBackgroundResource(R.drawable.selector_popup_twobtn_02);
				mBinding.btnIndividual.setTextColor(getResources().getColorStateList(R.color.selector_tc_common_yel_yel01));
				mBinding.btnCorporation.setTextColor(getResources().getColorStateList(R.color.selector_tc_common_black_black30));
			}
		}

	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissDialog()");
		if (tag.equals(DIALOG_TAG_CORPORATION_SELECTION)) {
			if (intent != null) {
				int corporationId = intent.getIntExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM_ID, -1);
				String corporationName = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);
				LogHelper.e("corporationName : " + corporationName + " / " + corporationId);
				if (corporationId != -1 && corporationName != null) {
					mBinding.btnCorporation.setText(getString(R.string.login_corporation_with_name, corporationName));
					mLoginViewModel.setTaxiType(corporationId);
				}
			} else {
				if (mBinding.btnCorporation.getText().equals(getString(R.string.login_corporation))) {
					mLoginViewModel.setTaxiType(-1);
				}
			}
		}
	}

	public void onScenarioServiceConnected(){
		subscribeViewModel(mLoginViewModel);
	}

	private void subscribeViewModel(LoginViewModel loginViewModel) {
		//앱 설정값
		LiveData<Configuration> config = loginViewModel.getConfiguration();
		loginViewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged()-Configuration");
				if (configuration != null) {
					setPhoneAndVehicleNumber(configuration);
					setTaxiType(configuration);

					LogHelper.e("isNeedAutoLogin() : " + configuration.isNeedAutoLogin());
					if (configuration.isNeedAutoLogin()) {
						login();
						config.removeObserver(this);
					}
				}
			}
		});

		LiveData<Call> call = loginViewModel.getCallInfo();
		call.observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				if (call != null) {
					LogHelper.e("call : " + call.toString());
				} else {
					LogHelper.e("call is null ");
					//비정상적인 데이터 조작으로 인해 기본 Call 정보가 없을 경우
					mLoginViewModel.initCallInfo();
				}
			}
		});
	}

	private void setPhoneAndVehicleNumber(Configuration configuration) {
		mBinding.etPhoneNumber.setText(configuration.getDriverPhoneNumber());
		String carId = String.valueOf(configuration.getCarIdForUI());
		LogHelper.e("carId : " + carId);
		if (configuration.getCarId() > 0) {
			mBinding.etVehicleNumber.setText(carId);
		}

		int length = mBinding.etPhoneNumber.getText().length();
		LogHelper.e("length : " + length);
		if (length == 0) {
			LogHelper.e("need to show keyboard");
			mBinding.etPhoneNumber.setSelection(length);
			mBinding.etPhoneNumber.requestFocus();
		} else if (length < 4) {
			mBinding.etVehicleNumber.setSelection(length);
			mBinding.etVehicleNumber.requestFocus();
		}
	}

	private void setTaxiType(Configuration configuration) {
		LogHelper.e("configuration : " + configuration.isCorporation());
		LogHelper.e("configuration : " + configuration.getCorporationCode());
		changeStateTaxiTypeBtn(configuration.isCorporation()
				? mBinding.btnCorporation : mBinding.btnIndividual);
		if (configuration.isCorporation()) {
			String corporationName = mLoginViewModel.getCorporationName(configuration.getCorporationCode());
			mBinding.btnCorporation.setText(getString(R.string.login_corporation_with_name, corporationName));
		}
	}

	//스크롤뷰 edge 효과 색 변경
	private void setScrollEdgeGlowColor(Context context, int brandColor) {
		//glow
		int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
		Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
		androidGlow.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
		//edge
		int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
		Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
		androidEdge.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
	}


	private void login() {
		String phoneNumber = mBinding.etPhoneNumber.getText().toString().trim();
		phoneNumber = phoneNumber.replaceAll("-", "");
		String vehicleNumber = mBinding.etVehicleNumber.getText().toString().trim();

		if (!phoneNumber.isEmpty() && !vehicleNumber.isEmpty()) {
			LogHelper.e("REQ-LOGIN phoneNumber : " + phoneNumber + " / vehicleNumber : " + vehicleNumber);
			final String vehicleNumForMobile = "3" + vehicleNumber;
			super.startLoadingProgress();
			mLoginViewModel.login(phoneNumber, vehicleNumForMobile).observe(LoginActivity.this, new Observer<ServiceRequestResultPacket>() {
				@Override
				public void onChanged(ServiceRequestResultPacket result) {
					LogHelper.e("RES-onChanged : ");
					mLoginViewModel.getLoginResult().removeObserver(this);
					LoginActivity.super.finishLoadingProgress();

					if (result != null) {
						if (result.getCertificationResult() == Packets.CertificationResult.Success) {
							String phoneNum = mBinding.etPhoneNumber.getText().toString();

							mLoginViewModel.savePhoneNumAndVehicleNumIfNeeded(phoneNum, vehicleNumForMobile);
							WavResourcePlayer.getInstance(getApplicationContext()).play(R.raw.voice_102);

							MainActivity.startActivity(LoginActivity.this);
							finish();

							LogHelper.i("login success, appVersion : " + result.getProgramVersion()
									+ "\n configVersion : " + result.getConfigurationVersion()
									+ "\n noticeCode : " + result.getNoticeCode());

						} else {
							showAndPlayLoginErrorMsg(result.getCertificationResult()
									, result.getCertCode());
						}
					} else {
						LogHelper.e("RES-onChanged : null");
						showAndPlayLoginErrorMsg(Packets.CertificationResult.NoResponse
								, Packets.CertificationResult.NoResponse.value);
					}
				}
			});
		}
	}

	public void showAndPlayLoginErrorMsg(Packets.CertificationResult result, int certCode) {
		String message = getString(R.string.fail_cert);
		Context context = getApplicationContext();

		switch (result) {
			case InvalidCar:
				message = context.getString(R.string.login_failed_not_register_vehicle_number) + " (0x" + Integer.toHexString(result.value) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_105);
				break;
			case InvalidContact:
				message = context.getString(R.string.login_failed_not_register_phone_number) + " (0x" + Integer.toHexString(result.value) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_104);
				break;
			case DriverPenalty:
				message = context.getString(R.string.fail_panelty) + " (0x" + Integer.toHexString(result.value) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_103);
				break;
			case InvalidHoliday:
				message = context.getString(R.string.fail_vacation) + " (0x" + Integer.toHexString(result.value) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_103);
				break;
			case NoResponse:
				message = context.getString(R.string.login_failed_server_connection_failed) + " (0x" + Integer.toHexString(result.value) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_103);
				break;
			default:
				message += "(0x" + Integer.toHexString(certCode) + ")";
				WavResourcePlayer.getInstance(context).play(R.raw.voice_103);
				break;
		}

		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_LOGIN_ERROR)
				.setTitle(getString(R.string.fail_cert))
				.setContent(message)
				.build();
		showPopupDialog(popup);
	}


	private void showCorporationPopup() {
		ArrayList<SelectionItem> selectionItems = mLoginViewModel.getCorporationList();
		showSelectionPopup(selectionItems, Popup.TYPE_ONE_BTN_SINGLE_SELECTION, "dialog_tag_corporation_selection");
	}

	private void showSelectionPopup(ArrayList<SelectionItem> selectionItems, int popupType, String popupTag) {
		Popup popup = new Popup
				.Builder(popupType, popupTag)
				.setSelectionItems(selectionItems)
				.build();
		showPopupDialog(popup);
	}
}