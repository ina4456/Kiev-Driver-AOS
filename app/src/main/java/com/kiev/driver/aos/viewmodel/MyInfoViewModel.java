package com.kiev.driver.aos.viewmodel;

import android.app.Application;

import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.SiteConstants;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Taxi;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.util.CarNumberConverter;
import com.kiev.driver.aos.util.LogHelper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyInfoViewModel extends AndroidViewModel {

	private final LiveData<Call> mCallInfo;
	private final LiveData<Configuration> mConfiguration;
	private final LiveData<Taxi> mTaxiInfo;
	private Repository mRepository;


	public MyInfoViewModel(@NonNull Application application, Repository repository) {
		super(application);
		LogHelper.e("MainViewModel()");
		mRepository = repository;
		mCallInfo = repository.getCallInfoLive();
		mConfiguration = repository.getConfigLive();
		mTaxiInfo = repository.getTaxiInfoLive();
		//mMyInfo = mRepository.requestMyInfo();

	}

	/**
	 * Expose the LiveData Comments query so the UI can observe it.
	 */
	public LiveData<Call> getCallInfo() {
		return mCallInfo;
	}
	public LiveData<Configuration> getConfiguration() {
		return mConfiguration;
	}

	public LiveData<Taxi> getTaxiInfo() {
		return mTaxiInfo;
	}

	public void changeVehicleNumber(String vehicleNumber) {
		Configuration configuration = mRepository.getConfig();
		if (configuration != null) {
			int vehicleNumberInt = 0;
			if (SiteConstants.USE_CAR_PLATE_NUMBER_FOR_LOGIN) {
				vehicleNumberInt = Integer.parseInt(CarNumberConverter.getCarIdFromCarNum(vehicleNumber));
			} else {
				vehicleNumberInt = Integer.parseInt("3" + vehicleNumber);
			}
			configuration.setCarId(vehicleNumberInt);
			configuration.setCarNumber(vehicleNumber);
			mRepository.updateConfig(configuration);
		}
	}

	public void logout() {
		mRepository.logout();
	}

	public void setNeedAutoLogin(boolean needAutoLogin) {
		LogHelper.e("setNeedAutoLogin() : " + needAutoLogin);
		Configuration configuration = this.mConfiguration.getValue();
		if (configuration != null) {
			configuration.setNeedAutoLogin(needAutoLogin);
			mRepository.updateConfig(configuration);
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
			return (T) new MyInfoViewModel(mApplication, mRepository);
		}
	}
}
