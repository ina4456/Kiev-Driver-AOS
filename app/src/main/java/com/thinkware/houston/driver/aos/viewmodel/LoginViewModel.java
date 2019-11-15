package com.thinkware.houston.driver.aos.viewmodel;

import android.app.Application;
import android.content.Context;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.model.SelectionItem;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.repository.Repository;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.thinkware.houston.driver.aos.util.CallManager;
import com.thinkware.houston.driver.aos.util.LogHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModel extends AndroidViewModel {

	private Repository mRepository;
	private final LiveData<Configuration> mConfiguration;
	private final LiveData<Call> mCallInfo;
	private final LiveData<ServiceRequestResultPacket> mLoginResult = new MutableLiveData<>();
	private ArrayList<SelectionItem> mCorporationList;


	public LoginViewModel(@NonNull Application application, Repository repository) {
		super(application);
		LogHelper.e("ConfigViewModel()");
		mRepository = repository;
		mConfiguration = repository.getConfigLive();
		mCallInfo = repository.getCallInfoLive();
		mCorporationList = loadCorporationList();
	}


	public LiveData<Configuration> getConfiguration() {
		return mConfiguration;
	}
	public LiveData<Call> getCallInfo() {
		return mCallInfo;
	}
	public LiveData<ServiceRequestResultPacket> getLoginResult() {
		return mLoginResult;
	}


	public void initCallInfo() {
		mRepository.resetCallInfo(Constants.CALL_STATUS_VACANCY);
	}


	public boolean checkValidate(String phoneNumber, String vehicleNumber) {
		if (phoneNumber != null && !phoneNumber.isEmpty()
			&& vehicleNumber != null && !vehicleNumber.isEmpty()) {

			// TODO: 2019-10-14 car number 적용전까지는 ID로 로그인하게 유지하므로, 길이 체크 기준 변경함.
//			if (phoneNumber.length() >= 10 && vehicleNumber.length() >= 7) {
			if (vehicleNumber.length() >= 4) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void makePhoneCallToCallCenter(Context context) {
		LogHelper.e("makePhoneCallToCallCenter()");
		Configuration configuration = mRepository.getConfig();
		if (configuration != null) {
			String callCenterPhoneNumber = configuration.getCallCenterNumber();
			if (callCenterPhoneNumber != null && !callCenterPhoneNumber.isEmpty()) {
				CallManager.getInstance(context)
						.call(context, callCenterPhoneNumber, configuration.isUseSpeakerPhone());
			} else {
				LogHelper.e("콜센터 전화 번호 오류");
			}
		} else {
			LogHelper.e("설정 정보 오류");
		}
	}

	public LiveData<ServiceRequestResultPacket> login(String phoneNumber, String vehicleNumber) {
		LogHelper.e("REQ-LOGIN : login");

		phoneNumber = phoneNumber.replaceAll("-", "");
		return mRepository.requestServicePacket(phoneNumber, vehicleNumber);
	}

	public void savePhoneNumAndVehicleNumIfNeeded(String inputPhoneNum, String inputVehicleNum) {
		Configuration configuration = mRepository.getConfig();
		if (configuration != null) {
			// 인증 완료 되면 전화번호를 저장 한다.
			inputPhoneNum = inputPhoneNum.replaceAll("-", "").trim();
			String phoneNum = configuration.getDriverPhoneNumber();
			LogHelper.e("save Phone Number originPhoneNum : " + phoneNum + " / inputPhoneNum : " + inputPhoneNum);
			if (phoneNum == null || phoneNum.isEmpty() || !phoneNum.equals(inputPhoneNum)) {
				configuration.setDriverPhoneNumber(inputPhoneNum);
			}

			int inputVehicleNumInt = Integer.parseInt(inputVehicleNum.trim());
			int vehicleNum = configuration.getCarId();
			LogHelper.e("save Vehicle Number origin Vehicle Num : " + vehicleNum + " / inputVehicleNumInt : " + inputVehicleNumInt);
			if (vehicleNum == 0 || vehicleNum != inputVehicleNumInt) {
				configuration.setCarId(inputVehicleNumInt);
			}

			LogHelper.e("configuration : " + configuration.toString());
			configuration.setHasUserLoggedIn(true);
			mRepository.updateConfig(configuration);
		}
	}

	private String getFormatPhoneNumberWithDash(String phoneNum) {
		LogHelper.e("getFormatPhoneNumberWithDash : " + phoneNum + " / " + phoneNum.length());
		phoneNum = phoneNum.replaceFirst("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3");
		return phoneNum;
	}

	private ArrayList<SelectionItem> loadCorporationList() {
		Context context = getApplication();
		int corporationCode = mRepository.getConfig().getCorporationCode();
		//LogHelper.e("corporationCode : " + corporationCode);

		ArrayList<SelectionItem> items = new ArrayList<>();
		List<String> corporationList = Arrays.asList(context.getResources().getStringArray(R.array.sn_corporation_list));

		int index = 0;
		for (String corporation : corporationList) {
			index++;
			// LogHelper.e("corporationCode : " + corporationCode + " / " + (index + 10) + " / " + (corporationCode == (index + 10)));

			SelectionItem item = new SelectionItem();
			item.setItemContent(corporation);
			item.setChecked((corporationCode == (index + 10)));
			item.setItemId(index + 10);
			items.add(item);
		}

		return items;
	}

	public String getCorporationName(int corporationCode) {
		String name = mCorporationList.get(corporationCode - 11).getItemContent();
		return name;
	}

	public ArrayList<SelectionItem> getCorporationList() {
		return mCorporationList;
	}

	public void setTaxiType(int corporationCode) {
		Configuration configuration = mRepository.getConfig();
		if (corporationCode != -1) {
			configuration.setCorporation(true);
			configuration.setCorporationCode(corporationCode);
			int index = 0;
			for (SelectionItem item : mCorporationList) {
				LogHelper.e("index  : " + index + " / " + (corporationCode - 11));
				if (index == corporationCode - 11) {
					item.setChecked(true);
				} else {
					item.setChecked(false);
				}
				index++;
			}
		} else {
			configuration.setCorporation(false);
			configuration.setCorporationCode(10);
			for (SelectionItem item : mCorporationList) {
				item.setChecked(false);
			}
		}

		mRepository.updateConfig(configuration);
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
			return (T) new LoginViewModel(mApplication, mRepository);
		}
	}
}
