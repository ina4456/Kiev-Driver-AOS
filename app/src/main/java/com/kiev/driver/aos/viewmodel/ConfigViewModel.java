package com.kiev.driver.aos.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.kiev.driver.aos.BuildConfig;
import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.NavigationExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ConfigViewModel extends AndroidViewModel {

	private final LiveData<Configuration> mConfiguration;
	private Repository mRepository;


	public ConfigViewModel(@NonNull Application application, Repository repository) {
		super(application);
		LogHelper.e("ConfigViewModel()");
		mRepository = repository;
		mConfiguration = repository.getConfigLive();
		//LogHelper.e("ConfigViewModel() : " + mConfiguration.getValue().toString());
	}

	public LiveData<Configuration> getConfiguration() {
		return mConfiguration;
	}

	public boolean hasUserLoggedIn() {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			return configuration.isHasUserLoggedIn();
		}
		return false;
	}

	public void changeConfigCheckBoxValues(int checkBoxId, boolean isChecked) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			switch (checkBoxId) {
				case R.id.tv_msg_auto_send:
					LogHelper.e("배차 메시지 자동 전송 : " + isChecked);
					configuration.setUseAutoSendSmsWhenGotCall(isChecked);
					break;

				case R.id.tv_speaker_phone:
					LogHelper.e("스피커 폰 : " + isChecked);
					configuration.setUseSpeakerPhone(isChecked);
					break;

				case R.id.tv_auto_routing_to_passenger:
					LogHelper.e("손님위치 : " + isChecked);
					configuration.setUseAutoRouteToPassenger(isChecked);
					break;

				case R.id.tv_auto_routing_to_destination:
					LogHelper.e("목적지 : " + isChecked);
					configuration.setUseAutoRouteToDestination(isChecked);
					break;
			}
			mRepository.updateConfig(configuration);
		}
	}

	public void changeNavigation(String navigationType) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			configuration.setNavigation(navigationType);
			mRepository.updateConfig(configuration);
		}
	}

	public String getNavigationPackageName(Context context, String navigationType) {
		return NavigationExecutor.getNavigationPackageName(context, navigationType);
	}

	public boolean isNavigationInstalled(Context context, String navigationType) {
		return NavigationExecutor.isNavigationInstalled(context, navigationType);
	}

	public ArrayList<SelectionItem> getServiceInfo(Context context) {
		ArrayList<SelectionItem> list = new ArrayList<>();
		List<String> serviceTitles = Arrays.asList(context.getResources().getStringArray(R.array.setting_service_info_list));

		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			for (String s: serviceTitles) {
				SelectionItem selectionItem = new SelectionItem();
				selectionItem.setItemTitle(s);
				if (s.equals(context.getString(R.string.setting_service_info_app_version))) {
					selectionItem.setItemContent(BuildConfig.VERSION_NAME);
				} else if (s.equals(context.getString(R.string.setting_service_info_url))) {
					selectionItem.setItemContent(Constants.SERVER_URL);
				} else if (s.equals(context.getString(R.string.setting_service_info_call_center))) {
					selectionItem.setItemContent(Constants.CALL_CENTER_PHONE_NUMBER);
				}

				list.add(selectionItem);
			}
		}

		return list;
	}

	public String getFloatingBtnCheckValuesStr(Context context) {
		Configuration configuration = this.getConfiguration().getValue();
		String displayTxt = "";

		if (configuration != null) {
			StringBuilder checkedItems = new StringBuilder();
			boolean useAll = true;
			boolean notUse = true;
			if (configuration.isUseBoardingAlightingBtn()) {
				checkedItems.append(", ").append(context.getString(R.string.setting_floating_btn_boarding_alighting));
				notUse = false;
			} else {
				useAll = false;
			}

			if (configuration.isUseMainBtn()) {
				checkedItems.append(", ").append(context.getString(R.string.setting_floating_btn_entering_main_short));
				notUse = false;
			} else {
				useAll = false;
			}

			if (configuration.isUseCallBtn()) {
				checkedItems.append(", ").append(context.getString(R.string.setting_floating_btn_call));
				notUse = false;
			} else {
				useAll = false;
			}

			if (useAll)
				checkedItems = new StringBuilder(context.getString(R.string.setting_floating_btn_using_all));

			if (notUse)
				checkedItems = new StringBuilder(context.getString(R.string.setting_floating_btn_not_use));

			displayTxt = checkedItems.toString();

			if (displayTxt.startsWith(", ")) {
				displayTxt = displayTxt.replaceFirst(", ", "");
			}
		}

		return displayTxt;
	}

	public boolean isFloatingBtnUse() {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			if (configuration.isUseMainBtn()
					|| configuration.isUseBoardingAlightingBtn()
					|| configuration.isUseCallBtn()) {
				return true;
			}
		}
		return false;
	}

	public boolean isTheFloatingBtnUse(Context context, String btnName) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			if (btnName.equals(context.getString(R.string.setting_floating_btn_boarding_alighting))) {
				return configuration.isUseBoardingAlightingBtn();

			} else if (btnName.equals(context.getString(R.string.setting_floating_btn_entering_main))) {
				return configuration.isUseMainBtn();

			} else if (btnName.equals(context.getString(R.string.setting_floating_btn_call))) {
				return configuration.isUseCallBtn();
			}
		}
		return false;
	}

	public void setFloatingBtnCheckValues(Context context, ArrayList<SelectionItem> items) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			for (SelectionItem p : items) {
				String btnName = p.getItemContent();
				if (btnName.equals(context.getString(R.string.setting_floating_btn_boarding_alighting))) {
					configuration.setUseBoardingAlightingBtn(p.isChecked());

				} else if (btnName.equals(context.getString(R.string.setting_floating_btn_entering_main))) {
					configuration.setUseMainBtn(p.isChecked());

				} else if (btnName.equals(context.getString(R.string.setting_floating_btn_call))) {
					configuration.setUseCallBtn(p.isChecked());
				}
			}

			mRepository.updateConfig(configuration);
		}
	}

	public void setFontSize(Context context, String fontSizeStr) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			int fontSize = getFontSize(context, fontSizeStr);
			configuration.setFontSizeInt(fontSize);
			mRepository.updateConfig(configuration);
		}
	}

	private int getFontSize(Context context, String fontSizeStr) {
		int fontSize = Constants.FONT_SIZE_NORMAL;
		if (fontSizeStr.equals(context.getString(R.string.setting_font_size_small))) {
			fontSize = Constants.FONT_SIZE_SMALL;
		} else if (fontSizeStr.equals(context.getString(R.string.setting_font_size_large))) {
			fontSize = Constants.FONT_SIZE_LARGE;
		}
		return fontSize;
	}

	public String getFontSizeStr(Context context) {
		Configuration configuration = this.getConfiguration().getValue();
		String fontSizeStr = context.getString(R.string.setting_font_size_normal);

		if (configuration != null) {
			int savedFontSize = configuration.getFontSizeInt();
			if (savedFontSize == Constants.FONT_SIZE_SMALL) {
				fontSizeStr = context.getString(R.string.setting_font_size_small);
			} else if (savedFontSize == Constants.FONT_SIZE_LARGE) {
				fontSizeStr = context.getString(R.string.setting_font_size_large);
			}
			return fontSizeStr;
		}
		return fontSizeStr;
	}

	public boolean isSelectedFontSize(Context context, String fontSizeStr) {
		Configuration configuration = this.getConfiguration().getValue();
		if (configuration != null) {
			int savedFontSize = configuration.getFontSizeInt();
			int fontSize = getFontSize(context, fontSizeStr);

			if (fontSize == savedFontSize) {
				return true;
			}
		}
		return false;
	}

	// TODO: 2019. 3. 8. 안드로이드 버전, 모델명, 엡버젼, 통신사,
	public void initConfigData(Context context) {
		Configuration configuration = mConfiguration.getValue();
		String appVersion = "";
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			appVersion = info.versionName;
			//configuration.setAppVersion(appVersion);
			mRepository.updateConfig(configuration);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * A creator is used to inject the product ID into the ViewModel
	 * <p>
	 * This creator is to showcase how to inject dependencies into ViewModels. It's not
	 * actually necessary in this case, as the product ID can be passed in a public method.
	 */
	public static class Factory extends ViewModelProvider.NewInstanceFactory {

		@NonNull
		private final Application mApplication;
		private final Repository mRepository;

		public Factory(@NonNull Application application) {
			mApplication = application;
			mRepository = ((MainApplication) application).getRepository();
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			//noinspection unchecked
			return (T) new ConfigViewModel(mApplication, mRepository);
		}
	}
}


