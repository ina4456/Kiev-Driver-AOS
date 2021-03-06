package com.kiev.driver.aos.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.SiteConstants;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.model.entity.Taxi;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseSMSPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaDecisionPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.util.CallManager;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.NavigationExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModel extends AndroidViewModel {

	private final LiveData<Configuration> mConfiguration;
	private final LiveData<Call> mCallInfo;
	private final LiveData<Notice> mNotice;

	public LiveData<Configuration> getConfiguration() {
		return mConfiguration;
	}

	public LiveData<Call> getCallInfoLive() {
		return mCallInfo;
	}

	public LiveData<Notice> getLatestNotice() {
		return mNotice;
	}

	public Call getCallInformaintion() {
		return mRepository.getCallInfo();
	}

	private ArrayList<SelectionItem> mMessageList;
	private ArrayList<SelectionItem> mCancelReasonList;


	private Repository mRepository;

	public MainViewModel(@NonNull Application application, Repository repository) {
		super(application);
		LogHelper.e("MainViewModel()");
		mRepository = repository;
		mConfiguration = repository.getConfigLive();
		mCallInfo = repository.getCallInfoLive();
		mNotice = repository.getLatestNotice();
		mMessageList = loadMessageList();
		mCancelReasonList = loadCancelReasonList();
	}

	public void logoutOrFinishApp(boolean isJustFinishApp) {
		LogHelper.e("logoutOrFinishApp()");
		// TODO: 2019-06-14 로그아웃 처리 서버 전송 구현 필요
		Configuration configuration = mRepository.getConfig();
		if (configuration != null) {
			configuration.setNeedAutoLogin(isJustFinishApp);
			configuration.setHasUserLoggedIn(false);
			mRepository.updateConfig(configuration);
		}

		mRepository.logout();
	}

	public void changeCallStatus(int callStatus) {
		LogHelper.e("changeCallStatus() : " + callStatus);
		mRepository.changeCallStatus(callStatus);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((MainApplication) getApplication()).progressOff();
			}
		}, 3000);
	}


	public void makePhoneCallToCallCenter(Context context) {
		LogHelper.e("makePhoneCallToCallCenter()");
		Configuration configuration = mRepository.getConfig();
		if (configuration != null) {
			String callCenterPhoneNumber = SiteConstants.CALL_CENTER_PHONE_NUMBER;
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

	public void makePhoneCallToPassenger(Context context) {
		LogHelper.e("makePhoneCallToPassenger()");
		Call call = this.mCallInfo.getValue();
		if (call != null) {
			String passengerPhoneNumber = call.getPassengerPhoneNumber();
			Configuration configuration = this.mConfiguration.getValue();
			LogHelper.e("스피커폰 사용 : " + configuration.isUseSpeakerPhone());
			if (passengerPhoneNumber != null && !passengerPhoneNumber.isEmpty() && configuration != null) {
				CallManager.getInstance(context)
						.call(context, passengerPhoneNumber, configuration.isUseSpeakerPhone());
			} else {
				LogHelper.e("call 정보 승객 전화 번호 오류");
			}
		} else {
			LogHelper.e("call 정보 오류");
		}
	}

	public void saveTaxiInfo(ResponseMyInfoPacket packet) {
		mRepository.upsertTaxiInfo(new Taxi(packet));
	}


	public String getNavigationPackageName(Context context, String navigationType) {
		return NavigationExecutor.getNavigationPackageName(context, navigationType);
	}

	public boolean isNavigationInstalled(Context context, String navigationType) {
		return NavigationExecutor.isNavigationInstalled(context, navigationType);
	}


	public void executeNavigation(Context context) {
		Call call = this.mCallInfo.getValue();
		Configuration configuration = this.mConfiguration.getValue();
		if (call != null) {
			LogHelper.e("call : " + call.toString());
			LogHelper.e("configuration : " + configuration.toString());

			//탑승 상태 외에는 전부 출발지 길탐색으로 판단함.
			boolean isDeparture = call.getCallStatus() != Constants.CALL_STATUS_BOARDED;
			LogHelper.e("isDeparture() : " + isDeparture);

			NavigationExecutor navigationExecutor = NavigationExecutor.getInstance();
			navigationExecutor.navigate(
					context,
					configuration != null ? configuration.getNavigation() : NavigationExecutor.NAVI_TYPE_TMAP,
					isDeparture ? call.getDeparturePoi() : call.getDestinationPoi(),
					isDeparture ? call.getDepartureLat() : call.getDestinationLat(),
					isDeparture ? call.getDepartureLong() : call.getDestinationLong()
			);
		} else {
			LogHelper.e("call 정보 오류 또는 정보 없음");
		}
	}

	private ArrayList<SelectionItem> loadMessageList() {
		Context context = getApplication();
		String carNumber = mRepository.getConfig().getCarNumber().substring(SiteConstants.USE_CAR_PLATE_NUMBER_FOR_LOGIN ? 0 : 2);
		ArrayList<SelectionItem> messagesItems = new ArrayList<>();
		List<String> messageList = Arrays.asList(context.getResources().getStringArray(R.array.msg_static_list));

		for (String message : messageList) {
			SelectionItem item = new SelectionItem();
			if (message.equals(context.getString(R.string.msg_soon))) {
				message = String.format(context.getString(R.string.msg_soon), carNumber);
			} else if (message.equals(context.getString(R.string.msg_arrival))) {
				message = String.format(context.getString(R.string.msg_arrival), carNumber);
			}
			item.setItemContent(message);
			item.setChecked(false);

			messagesItems.add(item);
		}

		return messagesItems;
	}

	public ArrayList<SelectionItem> getMessageList() {
		return mMessageList;
	}

	private ArrayList<SelectionItem> loadCancelReasonList() {
		Context context = getApplication();
		ArrayList<SelectionItem> cancelReasonItems = new ArrayList<>();

		List<String> cancelReasonList = Arrays.asList(context.getResources().getStringArray(R.array.alloc_cancel_reason_list));

		for (String reasonName : cancelReasonList) {
			SelectionItem item = new SelectionItem();
			item.setItemContent(reasonName);
			item.setChecked(false);
			cancelReasonItems.add(item);
		}
		return cancelReasonItems;
	}

	public ArrayList<SelectionItem> getCancelReasonList() {
		return mCancelReasonList;
	}

	public void requestAcceptOrRefuse(Packets.OrderDecisionType decisionType) {
		mRepository.requestAcceptOrRefuse(decisionType);
	}

	public void requestCancelCall(Packets.ReportKind cancelReason) {
		mRepository.requestCancelCall(cancelReason);
	}

	public void updateCallInfo(Call call) {
		call.setCallIsTemp(true);
		mRepository.updateCallInfo(call);
	}

	public void refreshUiWithIfCallInfoExist() {
		mRepository.refreshUiWithIfCallInfoExist();
	}

	public Call getCallInfo() {
		return mRepository.getCallInfo();
	}

	public void resetCallInfoForUi() {
		OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
		OrderInfoPacket getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);

		LogHelper.e("normal : " + (normal == null) + " / getOn : " + (getOn == null));

		if (normal == null || getOn == null) {
			mRepository.resetCallInfo(Constants.CALL_STATUS_VACANCY);
		}

		if (normal != null && getOn != null) {
			//승차중 배차의 경우 콜 수신 화면을 새로운 데이터와 함께 보여주고,
			//다시 원래 일반 배차 데이터를 표시함.
			Call call = new Call(normal);
			call.setCallStatus(Constants.CALL_STATUS_BOARDED);
			mRepository.updateCallInfo(call);
		}
	}

	public void resetCallInfoForUi(int status) {
		mRepository.resetCallInfo(status);
	}


	public MutableLiveData<ResponseSMSPacket> requestToSendSMS(String msg) {
		return mRepository.requestToSendSMS(msg);
	}

	public MutableLiveData<ResponseWaitCallListPacket> requestWaitingCallList(Packets.WaitCallListType waitCallListType, int startIndex) {
		return mRepository.requestWaitingCallList(waitCallListType, startIndex);
	}

	public MutableLiveData<ResponseWaitCallOrderInfoPacket> requestWaitingCallOrder(Call call) {
		return mRepository.requestWaitingCallOrder(call);
	}

	public MutableLiveData<ResponseMyInfoPacket> requestMyInfo() {
		return mRepository.requestMyInfo();
	}

	public MutableLiveData<ResponseWaitAreaListPacket> requestWaitArea(Packets.WaitAreaRequestType requestType, int startIndex) {
		return mRepository.requestWaitArea(requestType, startIndex);
	}

	public MutableLiveData<ResponseWaitAreaDecisionPacket> requestWaitDecision(String waitAreaId) {
		return mRepository.requestWaitDecision(waitAreaId);
	}

	public MutableLiveData<ResponseWaitAreaCancelPacket> requestWaitCancel(String waitAreaId) {
		return mRepository.requestWaitCancel(waitAreaId);
	}


	public void clearTempCallInfo() {
		mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Temp);
	}

	public WaitingZone getWaitingZone() {
		return mRepository.getWaitingZone();
	}

	public void setWaitingZone(WaitingZone waitingZone, boolean isSave) {
		if (isSave) {
			mRepository.saveWaitArea(waitingZone);
		} else {
			mRepository.clearWaitingZone();
		}
	}

	public boolean isUseAutoRoutingToTarget(boolean isDeparture) {
		Configuration config = mRepository.getConfig();
		if (config != null) {
			return isDeparture ? config.isUseAutoRouteToPassenger()
					: config.isUseAutoRouteToDestination();
		}
		return true;
	}

	public boolean hasNormalCall() {
		OrderInfoPacket normalCall = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
		return normalCall != null;
	}

	public boolean hasGetOnCall() {
		OrderInfoPacket getOnCall = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
		return getOnCall != null;
	}

	public String getPassengerPhoneNumber() {
		return mRepository.getCallInfo().getPassengerPhoneNumber();
	}

	public int getTimeForDisplayingCallBroadcast() {
		return mRepository.getConfig().getCvt();
	}

	public int getDistance(double latitude, double longitude) {
		return mRepository.getDistance(latitude, longitude);
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
			return (T) new MainViewModel(mApplication, mRepository);
		}
	}
}
