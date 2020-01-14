package com.kiev.driver.aos.repository;

import com.kiev.driver.aos.AppExecutors;
import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.model.entity.Taxi;
import com.kiev.driver.aos.repository.local.AppDatabase;
import com.kiev.driver.aos.repository.local.SharedPreferenceManager;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseNoticeListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseSMSPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsDetailPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaDecisionPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.kiev.driver.aos.service.ScenarioService;
import com.kiev.driver.aos.util.LogHelper;

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
				mDatabase.callDao().update(call);
			} else {
				mDatabase.callDao().insert(call);
			}
		});
	}


	public void refreshUiWithIfCallInfoExist() {
		Call call;
		OrderInfoPacket normal = loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
		if (normal != null && normal.getCallNumber() > 0) {
			LogHelper.e("기존 콜 : " + normal.toString());
			call = new Call(normal);
			call.setCallStatus(Constants.CALL_STATUS_BOARDED);
			updateCallInfo(call);
		} else {
			LogHelper.e("normal call is null : " + getCallInfo().toString());
			//일반콜 정보는 없는데, 현재 승차 상태가 탑승인 경우 목적지 정보가 없는 길빵 손님으로 간주한다.
			//수락했을 경우에 대한 처리 필요...
			call = new Call();
			if (mScenarioService.getBoardType() == Packets.BoardType.Boarding) {
				LogHelper.e("boarded");
				call.setCallStatus(Constants.CALL_STATUS_BOARDED);
			} else {
				LogHelper.e("not boarded");
				OrderInfoPacket getOn = loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
				if (getOn != null && getOn.getCallNumber() > 0) {
					call = new Call(getOn);
					call.setCallStatus(Constants.CALL_STATUS_ALLOCATED);
					saveCallInfoWithOrderKind(getOn, Packets.OrderKind.Normal);
				} else {
					call.setCallStatus(Constants.CALL_STATUS_VACANCY);
				}
			}
			updateCallInfo(call);
		}
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

	public ResponseWaitAreaOrderInfoPacket loadWaitCallInfo() {
		return mSharedPreferenceManager.getWaitOrderInfo();
	}

	public void saveWaitCallInfo(ResponseWaitAreaOrderInfoPacket responseWaitAreaOrderInfoPacket) {
		mSharedPreferenceManager.setWaitOrderInfo(responseWaitAreaOrderInfoPacket);
	}

	public void saveWaitArea(WaitingZone waitingZone) {
		mSharedPreferenceManager.setWaitArea(waitingZone);
	}


	public WaitingZone getWaitingZone() {
		return mSharedPreferenceManager.getWaitingArea();
	}

	public void clearWaitingZone() {
		mSharedPreferenceManager.clearWaitingArea();
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
	public void requestCancelCall(Packets.ReportKind cancelReason) {
		ResponseWaitAreaOrderInfoPacket wait = mSharedPreferenceManager.getWaitOrderInfo();
		if (wait != null) {
			mScenarioService.requestReport(
					wait.getCallNumber(), wait.getOrderCount(),
					wait.getOrderKind(), wait.getCallReceiptDate(),
					cancelReason, 0, 0);
//					Packets.ReportKind.Failed, 0, 0);
		} else {
			OrderInfoPacket normal = mSharedPreferenceManager.getNormalCallInfo();
			if (normal != null) {
				mScenarioService.requestReport(
						normal.getCallNumber(), normal.getOrderCount(),
						normal.getOrderKind(), normal.getCallReceiptDate(),
						cancelReason, 0, 0);
//				Packets.ReportKind.Failed, 0, 0);
			}
		}
	}

	public MutableLiveData<ResponseSMSPacket> requestToSendSMS(String msg) {
		Call savedCall = getCallInfo();
		mScenarioService.requestToSendSMS(msg, savedCall);
		final MutableLiveData<ResponseSMSPacket> data = mScenarioService.getResponseSMSPacket();
		data.observeForever(new Observer<ResponseSMSPacket>() {
			@Override
			public void onChanged(ResponseSMSPacket responseSMSPacket) {
				LogHelper.e("requestToSendSMS - onChanged");
				data.postValue(responseSMSPacket);
				data.removeObserver(this);
			}
		});

		return data;
	}

	public MutableLiveData<ResponseMyInfoPacket> requestMyInfo() {
		if (mScenarioService == null){
			return null;
		}

		mScenarioService.requestMyInfo();
		final MutableLiveData<ResponseMyInfoPacket> data = mScenarioService.getResponseMyInfo();
		data.observeForever(new Observer<ResponseMyInfoPacket>() {
			@Override
			public void onChanged(ResponseMyInfoPacket responseMyInfoPacket) {
				LogHelper.e("requestMyInfo - onChanged");
				data.postValue(responseMyInfoPacket);
				data.removeObserver(this);
			}
		});

		return data;
	}


	public MutableLiveData<ResponseWaitCallListPacket> requestWaitingCallList(Packets.WaitCallListType waitCallListType, int startIndex) {
		mScenarioService.requestWaitCallList(waitCallListType, startIndex);
		final MutableLiveData<ResponseWaitCallListPacket> data = mScenarioService.getResponseWaitCallListPacket();
		data.observeForever(new Observer<ResponseWaitCallListPacket>() {
			@Override
			public void onChanged(ResponseWaitCallListPacket response) {
				LogHelper.e("requestWaitingCallList - onChanged");
				data.postValue(response);
				data.removeObserver(this);
			}
		});

		return data;
	}


	public MutableLiveData<ResponseWaitCallOrderInfoPacket> requestWaitingCallOrder(Call call) {
		mScenarioService.requestWaitCallOrder(call);

		final MutableLiveData<ResponseWaitCallOrderInfoPacket> data = mScenarioService.getResponseWaitCallOrderInfoPacket();
		data.observeForever(new Observer<ResponseWaitCallOrderInfoPacket>() {
			@Override
			public void onChanged(ResponseWaitCallOrderInfoPacket response) {
				LogHelper.e("requestWaitingCallList - onChanged");
				data.postValue(response);
				data.removeObserver(this);
			}
		});

		return data;
	}

	public MutableLiveData<ResponseStatisticsPacket> requestStatistics() {
		mScenarioService.requestStatistics();

		final MutableLiveData<ResponseStatisticsPacket> data = mScenarioService.getStatisticPacket();
		data.observeForever(new Observer<ResponseStatisticsPacket>() {
			@Override
			public void onChanged(ResponseStatisticsPacket response) {
				LogHelper.e("requestWaitingCallList - onChanged");
				data.postValue(response);
				data.removeObserver(this);
			}
		});

		return data;
	}

	public MutableLiveData<ResponseStatisticsDetailPacket> requestStatisticsDetail(Packets.StatisticListType type, Packets.StatisticPeriodType period, int startIndex) {
		mScenarioService.requestStatisticsDetail(type, period, startIndex);
		final MutableLiveData<ResponseStatisticsDetailPacket> data = mScenarioService.getStatisticDetailPacket();
		data.observeForever(new Observer<ResponseStatisticsDetailPacket>() {
			@Override
			public void onChanged(ResponseStatisticsDetailPacket response) {
				data.postValue(response);
				data.removeObserver(this);
			}
		});
		return data;
	}

	public MutableLiveData<ResponseWaitAreaListPacket> requestWaitArea(Packets.WaitAreaRequestType type, int startIndex) {
		mScenarioService.requestWaitAreaNew(type, startIndex);
		final MutableLiveData<ResponseWaitAreaListPacket> data = mScenarioService.getWaitAreaPacket();
		data.observeForever(new Observer<ResponseWaitAreaListPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaListPacket response) {
				data.postValue(response);
				data.removeObserver(this);
			}
		});
		return data;
	}

	public MutableLiveData<ResponseWaitAreaDecisionPacket> requestWaitDecision(String waitAreaId) {
		mScenarioService.requestWaitDecisionNew(waitAreaId);
		final MutableLiveData<ResponseWaitAreaDecisionPacket> data = mScenarioService.getWaitDecisionPacket();
		data.observeForever(new Observer<ResponseWaitAreaDecisionPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaDecisionPacket response) {
				data.postValue(response);
				data.removeObserver(this);
			}
		});
		return data;
	}

	public MutableLiveData<ResponseWaitAreaCancelPacket> requestWaitCancel(String waitAreaId) {
		mScenarioService.requestWaitCancel(waitAreaId);
		final MutableLiveData<ResponseWaitAreaCancelPacket> data = mScenarioService.getWaitCancelPacket();
		data.observeForever(new Observer<ResponseWaitAreaCancelPacket>() {
			@Override
			public void onChanged(ResponseWaitAreaCancelPacket response) {
				data.postValue(response);
				data.removeObserver(this);
			}
		});
		return data;
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
	 * 공지사항 or 메시지 (내부 저장 항목)
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
	 * 공지사항(서버 호출)
	 * =================================================================
	 */
	public MutableLiveData<ResponseNoticeListPacket> getNoticeListFromServer() {
		mScenarioService.requestNoticeList();
		return mScenarioService.getNoticeListPacket();
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
