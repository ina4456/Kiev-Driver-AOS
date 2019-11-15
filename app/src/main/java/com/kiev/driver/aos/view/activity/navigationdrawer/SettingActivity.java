package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivitySettingsBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.ConfigViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PopupDialogFragment.PopupDialogListener {

	public static final String DIALOG_TAG_NAVI = "DIALOG_TAG_NAVI";
	public static final String DIALOG_TAG_NAVI_INSTALL_TO_MOVE = "DIALOG_TAG_NAVI_INSTALL_TO_MOVE";
	public static final String DIALOG_TAG_FLOATING_BTN = "DIALOG_TAG_FLOATING_BTN";
	public static final String DIALOG_TAG_FONT_SIZE = "DIALOG_TAG_FONT_SIZE";
	public static final String DIALOG_TAG_SERVICE = "DIALOG_TAG_SERVICE";
	private ActivitySettingsBinding mBinding;
	private ConfigViewModel mConfigViewModel;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, SettingActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mConfigViewModel = new ViewModelProvider(this, new ConfigViewModel.Factory(getApplication()))
				.get(ConfigViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mConfigViewModel);

		subscribeViewModel(mConfigViewModel);

		initToolbar();
		setListeners();

	}

	private void subscribeViewModel(ConfigViewModel configViewModel) {
		configViewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged()-Configuration");
				if (configuration != null) {
					boolean isNavigationInstalled = configViewModel.isNavigationInstalled(getApplicationContext(), configuration.getNavigation());
					if (isNavigationInstalled) {
						mBinding.tvSettingNavigationInstall.setVisibility(View.GONE);
						mBinding.vSettingNavigationInstallLine.setVisibility(View.GONE);
					} else {
						mBinding.tvSettingNavigationInstall.setVisibility(View.VISIBLE);
						mBinding.vSettingNavigationInstallLine.setVisibility(View.VISIBLE);
						mBinding.tvSettingNavigationInstall.setText(getString(R.string.setting_navigation_non_install));
					}

					String values = configViewModel.getFloatingBtnCheckValuesStr(getApplicationContext());
					mBinding.tvSettingFloatingBtnType.setText(values);

					String fontSize = configViewModel.getFontSizeStr(getApplicationContext());
					mBinding.tvSettingFontSizeSelected.setText(fontSize);
				}
			}
		});
	}

	private void setListeners() {
		mBinding.tvMsgAutoSend.setOnClickListener(this);
		mBinding.tvSpeakerPhone.setOnClickListener(this);
		mBinding.tvAutoRoutingToPassenger.setOnClickListener(this);
		mBinding.tvAutoRoutingToDestination.setOnClickListener(this);
		mBinding.clSettingNavigation.setOnClickListener(this);
		mBinding.clSettingFloatingBtn.setOnClickListener(this);
		mBinding.clSettingFontSize.setOnClickListener(this);
		mBinding.clSettingServiceInfo.setOnClickListener(this);
	}

	private void initToolbar() {
		setSupportActionBar(mBinding.settingToolbar.toolbar);
		mBinding.settingToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.settingToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.d_menu_setting));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.cl_setting_navigation:
				ArrayList<SelectionItem> naviSelectionItems = new ArrayList<>();
				List<String> msgList = Arrays.asList(getResources().getStringArray(R.array.setting_navigation_list));
				for (String s: msgList) {
					SelectionItem selectionItem = new SelectionItem();
					selectionItem.setItemContent(s);

					boolean isNaviInstalled = mConfigViewModel.isNavigationInstalled(this, s);
					selectionItem.setNaviInstalled(isNaviInstalled);

					if (s.equals(mBinding.tvSettingNavigationType.getText())) {
						selectionItem.setChecked(true);
					}
					naviSelectionItems.add(selectionItem);
				}

				Popup popup = new Popup
						.Builder(Popup.TYPE_ONE_BTN_NAVI_SELECTION, DIALOG_TAG_NAVI)
						.setBtnLabel(null, getString(R.string.common_cancel))
						.setSelectionItems(naviSelectionItems)
						.build();
				showPopupDialog(popup);
				break;

			case R.id.cl_setting_floating_btn:
				ArrayList<SelectionItem> floatingBtnSelectionItems = new ArrayList<>();
				List<String> floatingBtnList = Arrays.asList(getResources().getStringArray(R.array.setting_floating_btn_list));
				for (String s: floatingBtnList) {
					SelectionItem selectionItem = new SelectionItem();
					selectionItem.setItemContent(s);

					boolean isChecked = mConfigViewModel.isTheFloatingBtnUse(getApplicationContext(), s);
					selectionItem.setChecked(isChecked);
					floatingBtnSelectionItems.add(selectionItem);
				}

				Popup floatingBtnPopup = new Popup
						.Builder(Popup.TYPE_ONE_BTN_CHECK_SELECTION, DIALOG_TAG_FLOATING_BTN)
						.setBtnLabel(getString(R.string.common_confirm), null)
						.setSelectionItems(floatingBtnSelectionItems)
						.build();
				showPopupDialog(floatingBtnPopup);
				break;


			case R.id.cl_setting_font_size:
				ArrayList<SelectionItem> fontSizeSelectionItems = new ArrayList<>();
				List<String> fontSizeList = Arrays.asList(getResources().getStringArray(R.array.setting_font_size_list));
				for (String s: fontSizeList) {
					SelectionItem selectionItem = new SelectionItem();
					selectionItem.setItemContent(s);

					boolean isChecked = mConfigViewModel.isSelectedFontSize(getApplicationContext(), s);
					selectionItem.setChecked(isChecked);
					fontSizeSelectionItems.add(selectionItem);
				}

				Popup fontSizePopup = new Popup
						.Builder(Popup.TYPE_ONE_BTN_SINGLE_SELECTION, DIALOG_TAG_FONT_SIZE)
						.setBtnLabel(null, getString(R.string.common_cancel))
						.setSelectionItems(fontSizeSelectionItems)
						.build();
				showPopupDialog(fontSizePopup);
				break;

			case R.id.cl_setting_service_info:
				ArrayList<SelectionItem> serviceItems = mConfigViewModel.getServiceInfo(getApplicationContext());
				Popup serviceInfoPopup = new Popup
						.Builder(Popup.TYPE_ONE_BTN_TITLE_CONTENT, DIALOG_TAG_SERVICE)
						.setBtnLabel(null, getString(R.string.common_confirm))
						.setSelectionItems(serviceItems)
						.build();
				showPopupDialog(serviceInfoPopup);
				break;

			default:
				LogHelper.e("click...");
				if (view instanceof CheckedTextView) {
					((CheckedTextView) view).toggle();
					mConfigViewModel.changeConfigCheckBoxValues(view.getId(), ((CheckedTextView)view).isChecked());
				}
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
		mConfigViewModel.changeConfigCheckBoxValues(compoundButton.getId(), isChecked);
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissPopupDialog : " + tag);
		if (intent != null) {
			String selectedItem = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);

			switch (tag) {
				case DIALOG_TAG_NAVI:
					LogHelper.e("selectedNavi : " + selectedItem);
					if (selectedItem != null && !selectedItem.isEmpty()) {
						mConfigViewModel.changeNavigation(selectedItem);
						boolean isNavigationInstalled = intent.getBooleanExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM_INSTALLED, false);
						if (!isNavigationInstalled) {
							String content = String.format(getString(R.string.setting_navigation_install_move_msg), selectedItem);
							Popup popup = new Popup
									.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_NAVI_INSTALL_TO_MOVE)
									.setBtnLabel(getString(R.string.common_confirm), null)
									.setContent(content)
									.setTitle(selectedItem)
									.build();
							showPopupDialog(popup);
						}
					}
					break;

				case DIALOG_TAG_NAVI_INSTALL_TO_MOVE:
					String appPackageName = mConfigViewModel.getNavigationPackageName(this, selectedItem);
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
					}
					break;


				case DIALOG_TAG_FLOATING_BTN:
					ArrayList<SelectionItem> items = intent.getParcelableArrayListExtra(Constants.DIALOG_INTENT_KEY_ITEMS);
					if (items != null) {
						mConfigViewModel.setFloatingBtnCheckValues(getApplicationContext(), items);
					}
					break;


				case DIALOG_TAG_FONT_SIZE:
					if (selectedItem != null && !selectedItem.isEmpty()) {
						mConfigViewModel.setFontSize(getApplicationContext(), selectedItem);
					}

					break;

				default:
					break;
			}
		}
	}
}
