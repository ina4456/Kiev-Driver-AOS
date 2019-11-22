package com.kiev.driver.aos.viewmodel;

import android.app.Application;

import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseNoticeListPacket;
import com.kiev.driver.aos.util.LogHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NoticeViewModel extends AndroidViewModel {

	private Repository mRepository;

	public NoticeViewModel(@NonNull Application application, Repository repository, boolean isNotice) {
		super(application);
		LogHelper.e("LoginViewModel()");
		mRepository = repository;
	}

	public LiveData<List<Notice>> getNoticeList(boolean isNotice) {
		LogHelper.e("isNotice : " + isNotice);
		return mRepository.getNoticeList(isNotice);
	}

	public MutableLiveData<ResponseNoticeListPacket> getNoticeListFromServer() {
		return mRepository.getNoticeListFromServer();
	}

	public static class Factory extends ViewModelProvider.NewInstanceFactory {
		@NonNull
		private final Application mApplication;
		private final Repository mRepository;
		private final boolean mIsNotice;

		public Factory(@NonNull Application application, boolean isNotice) {
			mApplication = application;
			mRepository = ((MainApplication) application).getRepository();
			mIsNotice = isNotice;
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			//noinspection unchecked
			return (T) new NoticeViewModel(mApplication, mRepository, mIsNotice);
		}
	}
}
