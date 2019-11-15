package com.thinkware.houston.driver.aos.repository;

import com.thinkware.houston.driver.aos.AppExecutors;
import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.model.entity.Notice;
import com.thinkware.houston.driver.aos.model.entity.Taxi;
import com.thinkware.houston.driver.aos.repository.local.AppDatabase;
import com.thinkware.houston.driver.aos.repository.local.SharedPreferenceManager;
import com.thinkware.houston.driver.aos.repository.remote.packets.Packets;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.ResponseWaitDecisionPacket;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.WaitOrderInfoPacket;
import com.thinkware.houston.driver.aos.service.ScenarioService;
import com.thinkware.houston.driver.aos.util.LogHelper;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


public class Repository {
	private static Repository sInstance;

	private final AppDatabase mDatabase;
	private final AppExecutors mAppExecutors;
	private final MainApplication mMainApplication;
	private ScenarioService mScenarioService;

	private MediatorLiveData<Configuration> mObservableConfiguration;
	private MediatorLiveData<Call> mObservableCall;

	private SharedPreferenceManager mSharedPreferenceManager;


	public static Repository getInstance(final AppDatabase database, AppExecutors appExecutors, MainApplication mainApplication) {
		if (sInstance == null) {
			synchronized (Repository.class) {
				if (sInstance == null) {
					sInstance = new Repository(database, appExecutors, mainApplication);
				}
			}
		}
		return sInstance;
	}

	private Repository(final AppDatabase database, AppExecutors appExecutors, MainApplication mainApplication) {
		LogHelper.e("Repository()");
		mDatabase = database;
		mAppExecutors = appExecutors;
		mMainApplication = mainApplication;
		mScenarioService = mainApplication.getScenarioService();
		mSharedPreferenceManager = SharedPreferenceManager.getInstance(mainApplication);

		mObservableConfiguration = new MediatorLiveData<>();
		mObservableConfiguration.addSource(mDatabase.configDao().getConfigLive(),
				new Observer<Configuration>() {
					@Override
					public void onChanged(Configuration config) {
						LogHelper.e("onChanged()-configuration");
						if (mDatabase.getDatabaseCreated().getValue() != null) {
							mObservableConfiguration.postValue(config);
						}
					}
				});

		mObservableCall = new MediatorLiveData<>();
		mObservableCall.addSource(mDatabase.callDao().getCallInfoForUiLive(),
				new Observer<Call>() {
					@Override
					public void onChanged(Call call) {
						LogHelper.e("onChanged()-call");
						if (mDatabase.getDatabaseCreated().getValue() != null) {
							mObservableCall.postValue(call);
						}
					}
				});
	}

	/**
	 * =================================================================
	 * 인증 요청
	 * =================================================================
	 */
	public LiveData<ServiceRequestResultPacket> requestServicePacket(String phoneNumber, String vehicleNumber) {
		if (mScenarioService == null) {
			mScenarioService = mMainApplication.getScenarioService();
		}
		LogHelper.e("scenarioService : " + (mScenarioService == null));

		final MutableLiveData<ServiceRequestResultPacket> data = new MutableLiveData<>();
		if (mScenarioService != null) {
			mScenarioService.requestServicePacket(phoneNumber, vehicleNumber, true);
			MutableLiveData<ServiceRequestResultPacket> resultLiveData = mScenarioService.getLoginResultData();
			resultLiveData.observeForever(new Observer<ServiceRequestResultPacket>() {
				@Override
				public void onChanged(ServiceRequestResultPacket resultPacket) {
					LogHelper.e("RES-LOGIN : getLoginResultData()");
					data.postValue(resultPacket);
					resultLiveData.removeObserver(this);
				}
			});
		}
		return data;
	}

	public void logout() {
		LogHelper.e("logout() : " + (mScenarioService == null));
		if (mScenarioService != null) {
			mScenarioService.logout();
		}
	}


	/**
	 * =================================================================
	 * 환경설정
	 * =================================================================
	 */
	public Configuration getConfig() {
		LogHelper.e("getConfig()");
		try {
			Callable<Configuration> callable = new Callable<Configuration>() {
				@Override
				public Configuration call() throws Exception {
					return mDatabase.configDao().getConfig();
				}
			};
			Future<Configuration> future = Executors.newSingleThreadExecutor().submit(callable);
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public LiveData<Configuration> getConfigLive() {
		return mDatabase.configDao().getConfigLive();
	}

	public void updateConfig(Configuration configuration) {
		LogHelper.e("updateConfig()");
		mAppExecutors.diskIO().execute(() -> {
			mDatabase.configDao().upsert(configuration);
		});
	}

	public void updateTaxiInfo(Taxi taxi) {
		LogHelper.e("updateTaxiInfo()");
		mAppExecutors.diskIO().execute(() -> {
			mDatabase.taxiDao().upsert(taxi);
		});
	}




	/**
	 * =================================================================
	 * 콜 정보
	 * =================================================================
	 */
	public void resetCallInfo(int status) {
		LogHelper.e("resetCallInfo() : " + status);
		mAppExecutors.diskIO().execute(() -> {
			mDatabase.callDao().deleteCallInfo();
			Call call = new Call();
			call.setCallStatus(status <= 0 ? Constants.CALL_STATUS_VACANCY : status);
			mDatabase.callDao().insert(call);
		});
	}


	//UI 업데이트용 LIVE DATA 콜정보
	public LiveData<Call> getCallInfoLive() {
		LogHelper.e("getCallInfoLive()");
		return mDatabase.callDao().getCallInfoForUiLive();
	}


	public Call getCallInfo() {
		try {
			Callable<Call> callable = new Callable<Call>() {
				@Override
				public Call call() throws Exception {
					return mDatabase.callDao().getCallInfoForUi();
				}
			};
			Future<Call> future = Executors.newSingleThreadExecutor().submit(callable);
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateCallInfo(Call call) {
		LogHelper.e("try to update call info : " + call.toString());
		Call savedCall = getCallInfo();
		mAppExecutors.diskIO().execute(() -> {
			if (savedCall != null) {
				call.setId(savedCall.getId());
			}
			mDatabase.callDao().update(call);
		});
	}

	public WaitOrderInfoPacket loadWaitCallInfo() {
		return mSharedPreferenceManager.getWaitOrderInfo();
	}

	public void saveWaitCallInfo(WaitOrderInfoPacket waitOrderInfoPacket) {
		mSharedPreferenceManager.setWaitOrderInfo(waitOrderInfoPacket);
	}

	public OrderInfoPacket loadCallInfoWithOrderKind(Packets.OrderKind kind) {
		switch (kind) {
			case Normal:
				return mSharedPreferenceManager.getNormalCallInfo();
			case GetOnOrder:
				return mSharedPreferenceManager.getGetOnCallInfo();
			case Temp:
				return mSharedPreferenceManager.getTempCallInfo();
		}
		return null;
	}

	public void saveCallInfoWithOrderKind(OrderInfoPacket orderInfoPacket, Packets.OrderKind kind) {
		switch (kind) {
			case Normal:
				mSharedPreferenceManager.setNormalCallInfo(orderInfoPacket);
				break;
			case GetOnOrder:
				mSharedPreferenceManager.setGetOnCallInfo(orderInfoPacket);
				break;
			case Temp:
				mSharedPreferenceManager.setTempCallInfo(orderInfoPacket);
				break;
		}
	}

	public void clearCallInfoWithOrderKind(Packets.OrderKind kind) {
		switch (kind) {
			case Normal:
				mSharedPreferenceManager.clearNormalCallInfo();
				break;
			case GetOnOrder:
				mSharedPreferenceManager.clearGetOnCallInfo();
				break;
			case Temp:
				mSharedPreferenceManager.clearTempCallInfo();
				break;
		}
	}

	public ResponseWaitDecisionPacket getWaitArea() {
		return mSharedPreferenceManager.getWaitArea();
	}

	public void clearWaitArea() {
		mSharedPreferenceManager.clearWaitArea();
	}



	//콜상태
	public void changeCallStatus(int callStatus) {
		LogHelper.e("changeCallStatus()");
		if (mScenarioService != null) {
			mScenarioService.changeCallStatus(callStatus);
		}
	}

	//콜 수락 & 거절
	public void requestAcceptOrRefuse(Packets.OrderDecisionType decisionType) {
		Call savedCall = getCallInfo();
		mScenarioService.requestOrderRealtime(decisionType, savedCall);
	}

	//콜 취소
	public void requestCancelCall(String cancelReason) {
		WaitOrderInfoPacket wait = mSharedPreferenceManager.getWaitOrderInfo();
		if (wait != null) {
			mScenarioService.requestReport(
					wait.getCallNumber(), wait.getOrderCount(),
					wait.getOrderKind(), wait.getCallReceiptDate(),
					Packets.ReportKind.Failed, 0, 0);
		} else {
			OrderInfoPacket normal = mSharedPreferenceManager.getNormalCallInfo();
			if (normal != null) {
				mScenarioService.requestReport(
						normal.getCallNumber(), normal.getOrderCount(),
						normal.getOrderKind(), normal.getCallReceiptDate(),
						Packets.ReportKind.Failed, 0, 0);
			}
		}
	}

	public void requsetToSendSMS(String msg) {
		Call savedCall = getCallInfo();
		mScenarioService.requestToSendSMS(msg, savedCall);
	}


	public int getDistance(double latitude, double longitude) {
		return (int) mScenarioService.getGpsHelper().getDistance((float)latitude, (float)longitude);
	}


	/**
	 * =================================================================
	 * 차량/기사정보
	 * =================================================================
	 */
	public LiveData<Taxi> getTaxiInfoLive() {
		LogHelper.e("getTaxiInfoLive()");
		return mDatabase.taxiDao().getTaxiInfoLive();
	}

	public void upsertTaxiInfo(Taxi taxi) {
		LogHelper.e("upsertTaxiInfo()");
		mAppExecutors.diskIO().execute(() -> {
			mDatabase.taxiDao().upsert(taxi);
		});
	}

	/**
	 * =================================================================
	 * 공지사항
	 * =================================================================
	 */
	public LiveData<Notice> getLatestNotice() {
		LogHelper.e("getLatestNotice()");
		return mDatabase.noticeDao().getLatestNotice();
	}

	public LiveData<List<Notice>> getNoticeList(boolean isNotice) {
		return mDatabase.noticeDao().getNoticeList(isNotice);
	}

	public void insertNotice(Notice notice) {
		LogHelper.e("insertNotice()");
		mAppExecutors.diskIO().execute(() -> {
			mDatabase.noticeDao().insert(notice);
		});
	}

	/**
	 * =================================================================
	 * 기타
	 * =================================================================
	 */
	public <T extends Object> void setSharedData(String key, T value) {
		mSharedPreferenceManager.setData(key, value);
	}
}
