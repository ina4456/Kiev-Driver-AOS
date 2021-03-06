package com.kiev.driver.aos.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.gun0912.tedpermission.TedPermission;
import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.MainApplication;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.WaitingZone;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.repository.Repository;
import com.kiev.driver.aos.repository.remote.manager.NetworkListener;
import com.kiev.driver.aos.repository.remote.manager.NetworkManager;
import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.AckPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.LivePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.PeriodSendingPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestCallInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestConfigPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestEmergencyPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestMessagePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestMessageReadPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestMyInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestNoticeListPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestNoticePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestOrderRealtimePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestRestPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestSendSMSPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestServicePacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestStatisticsDetailPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestStatisticsPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitAreaDecisionPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitAreaOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.RequestWaitCallOrderPacket;
import com.kiev.driver.aos.repository.remote.packets.mdt2server.ServiceReportPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.CallerInfoResendPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.NoticesPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoProcPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMessageNewPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMessagePacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseNoticeListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponsePeriodSendingPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseRestPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseSMSPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseServiceReportPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsDetailPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaDecisionPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceConfigPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.kiev.driver.aos.util.GpsHelper;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.WavResourcePlayer;
import com.kiev.driver.aos.view.activity.CallReceivingActivity;
import com.kiev.driver.aos.view.activity.PopupActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


public class ScenarioService extends LifecycleService {

	//----------------------------------------------------------------------------------------
	// fields
	//----------------------------------------------------------------------------------------
	public static final int MSG_PERIOD = 1;
	public static final int MSG_LIVE = 2;
	public static final int MSG_EMERGENCY = 3;
	public static final int MSG_AREA_CHECK = 4;
	public static final int MSG_REPORT = 5;
	public static final int MSG_ACK = 6;
	public static final int MSG_SERVICE_ACK = 7;
	public static final int MSG_DEVICE_WATCH = 8;
	public static final int MSG_REQ_WAIT_AREA_STATE = 9;
	public static final String SERVICE_NAME = ScenarioService.class.getSimpleName();

	private NetworkManager networkManager;
	private GpsHelper gpsHelper;
	private Packets.BoardType boardType; // 승차 상태
	private Packets.RestType restType; // 휴식 상태
	private Packets.EmergencyType emergencyType; // 긴급상황 상태
	private boolean hasCertification; // 서비스 인증 성공 여부
	private boolean isAvailableNetwork, isValidPort; // DebugWindow에 네트워크 상태, Port 상태를 표시하기 위함
	private int reportRetryCount, ackRetryCount;
	// 전체 화면 Activity 팝업(공지사항, 메시지 등)이 보여질 때 이전 상태가 Background 였는지 저장 한다.
	// 이전 상태가 Background 였다면 MainActivity가 보여지지 않도록 아이나비를 한번 더 호출해 준다.
	//private boolean isPrevStatusBackground;
	// 모바일 배차를 받고 승차보고가 올라가기 전까지는 주기 전송 시간을 cfgLoader.getRc()로 한다.
	// 모바일 배차 승차보고 후 주기 시간을 정상적으로 되돌리기 위해 사용 한다
	private int periodTerm;
	private boolean isUsedDestination; // 목적지 정보 사용여부
	private Packets.OrderDecisionType lastOrderDecisionType = Packets.OrderDecisionType.Reject;


	private MainApplication mMainApplication;
	private Repository mRepository;
	private Configuration mConfiguration;

	private MutableLiveData<ServiceRequestResultPacket> mServiceResultPacket;
	public MutableLiveData<ServiceRequestResultPacket> getLoginResultData() {
		return mServiceResultPacket;
	}

	private MutableLiveData<ResponseSMSPacket> mResponseSMSPacket;

	public MutableLiveData<ResponseSMSPacket> getResponseSMSPacket() {
		return mResponseSMSPacket;
	}

	private MutableLiveData<ResponseMyInfoPacket> mResponseMyInfo;

	public MutableLiveData<ResponseMyInfoPacket> getResponseMyInfo() {
		return mResponseMyInfo;
	}

	private MutableLiveData<ResponseWaitCallListPacket> mResponseWaitCallListPacket;

	public MutableLiveData<ResponseWaitCallListPacket> getResponseWaitCallListPacket() {
		return mResponseWaitCallListPacket;
	}

	private MutableLiveData<ResponseWaitCallOrderInfoPacket> mResponseWaitCallOrderInfoPacket;

	public MutableLiveData<ResponseWaitCallOrderInfoPacket> getResponseWaitCallOrderInfoPacket() {
		return mResponseWaitCallOrderInfoPacket;
	}

	private MutableLiveData<ResponseNoticeListPacket> mNoticeListPacket;

	public MutableLiveData<ResponseNoticeListPacket> getNoticeListPacket() {
		return mNoticeListPacket;
	}

	private MutableLiveData<ResponseStatisticsPacket> mStatisticPacket;

	public MutableLiveData<ResponseStatisticsPacket> getStatisticPacket() {
		return mStatisticPacket;
	}


	private MutableLiveData<ResponseStatisticsDetailPacket> mStatisticDetailPacket;

	public MutableLiveData<ResponseStatisticsDetailPacket> getStatisticDetailPacket() {
		return mStatisticDetailPacket;
	}

	private MutableLiveData<ResponseWaitAreaListPacket> mWaitArea;

	public MutableLiveData<ResponseWaitAreaListPacket> getWaitAreaPacket() {
		return mWaitArea;
	}

	private MutableLiveData<ResponseWaitAreaDecisionPacket> mWaitDecision;

	public MutableLiveData<ResponseWaitAreaDecisionPacket> getWaitDecisionPacket() {
		return mWaitDecision;
	}

	private MutableLiveData<ResponseWaitAreaCancelPacket> mWaitCancel;

	public MutableLiveData<ResponseWaitAreaCancelPacket> getWaitCancelPacket() {
		return mWaitCancel;
	}

	public Packets.BoardType getBoardType() {
		return boardType;
	}

	public void setBoardType(Packets.BoardType boardType) {
		this.boardType = boardType;
	}

	public Packets.RestType getRestType() {
		return restType;
	}

	public void setGpsHelper(GpsHelper gpsHelper) {
		this.gpsHelper = gpsHelper;
	}

	public GpsHelper getGpsHelper() {
		return gpsHelper;
	}

	private Call mCallInfo;
	//----------------------------------------------------------------------------------------
	// life-cycle
	//----------------------------------------------------------------------------------------

	private final IBinder binder = new ScenarioService.LocalBinder();
	public class LocalBinder extends Binder {
		public ScenarioService getService() {
			LogHelper.d(">> getService()");
			return ScenarioService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogHelper.d(">> onCreate()");
		mMainApplication = (MainApplication) getApplication();

		boardType = Packets.BoardType.Empty;
		restType = Packets.RestType.Working;
		emergencyType = Packets.EmergencyType.End;

		mRepository = mMainApplication.getRepository();
		mRepository.getConfigLive().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				mConfiguration = configuration;
				periodTerm = configuration.getPst();
				LogHelper.e("Scenario onChanged : " + configuration.toString());

				if (networkManager == null) {
					networkManager = NetworkManager.getInstance(mMainApplication);
					networkManager.addNetworkListener(networkListener);
					//networkManager.connect(cfgLoader.getCallServerIp(), cfgLoader.getCallServerPort());
				}

				boolean isLocationPermissionGranted = TedPermission.isGranted(getApplicationContext()
						, Manifest.permission.ACCESS_COARSE_LOCATION
						, Manifest.permission.ACCESS_FINE_LOCATION);
				LogHelper.e("permission granted : " + isLocationPermissionGranted);
				if (isLocationPermissionGranted && gpsHelper == null) {
					gpsHelper = new GpsHelper(getApplicationContext());
				} else {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
						gpsHelper = new GpsHelper(getApplicationContext());
					}
				}
			}
		});

		mRepository.getCallInfoLive().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				if (call != null) {
					LogHelper.e("onChanged-Call : " + call.getCallStatus());
					mCallInfo = call;
//					if (call.getCallStatus() == Constants.CALL_STATUS_BOARDED
//					|| call.getCallStatus() == Constants.CALL_STATUS_DRIVING) {
//						boardType = Packets.BoardType.Boarding;
//					} else {
//						boardType = Packets.BoardType.Empty;
//					}
				}
			}
		});
	}

	public void changeCallStatus(int callStatus) {
		LogHelper.e("changeCallStatus() : " + callStatus);

		switch (callStatus) {
			case Constants.CALL_STATUS_VACANCY:
				applyVacancy(0, 0);
				break;

			case Constants.CALL_STATUS_ALLOCATION_FAILED:
				mCallInfo.setCallStatus(Constants.CALL_STATUS_ALLOCATION_FAILED);
				mRepository.updateCallInfo(mCallInfo);
				break;

			case Constants.CALL_STATUS_DRIVING:
			case Constants.CALL_STATUS_BOARDED:
				LogHelper.e("CALL_STATUS_BOARDED_OR_DRIVE : " + callStatus);
				applyDriving(0, 0);
				mCallInfo.setCallStatus(Constants.CALL_STATUS_BOARDED);
				mRepository.updateCallInfo(mCallInfo);
				break;

			case Constants.CALL_STATUS_WORKING:
				requestRest(Packets.RestType.Working);
				break;

			case Constants.CALL_STATUS_RESTING:
				requestRest(Packets.RestType.Rest);
				break;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
		LogHelper.d(">> onBind()");
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogHelper.d(">> onDestroy()");
		this.stopSelf();

	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		LogHelper.e(">> onTaskRemoved()");
		this.stopSelf();
	}

	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		return format.format(new Date(System.currentTimeMillis()));
	}

	public void logout() {
		if (networkManager != null) {
			hasCertification = false;
			pollingHandler.removeMessages(MSG_PERIOD);
			pollingHandler.removeMessages(MSG_LIVE);
			networkManager.disconnect();
			networkManager.livePacketDisconnect();
		}
	}

	/**
	 * 빈차등/미터기의 승차 신호를 처리 한다.
	 *
	 * @param fare    요금 (빈차등일 경우 0)
	 * @param mileage 거리 (빈차등일 경우 0)
	 */
	public void applyVacancy(int fare, int mileage) {
//		if (boardType == Packets.BoardType.Empty) {
//			LogHelper.d(">> Skip empty signal. already empty.");
//			return;
//		}

		boardType = Packets.BoardType.Empty;
		// 인증 전에는 빈차 신호를 무시 한다.
		if (hasCertification) {
			requestBoardState(Packets.ReportKind.GetOff, fare, mileage);
		}
	}

	/**
	 * 빈차등/미터기의 승차 신호를 처리 한다.
	 *
	 * @param fare    요금 (빈차등일 경우 0)
	 * @param mileage 거리 (빈차등일 경우 0)
	 */
	public void applyDriving(int fare, int mileage) {
//		if (boardType == Packets.BoardType.Boarding) {
//			LogHelper.d(">> Skip driving signal. already driving.");
//			return;
//		}

		boardType = Packets.BoardType.Boarding;
		// 인증 전에는 승차 신호를 무시 한다.
		if (hasCertification) {
			requestBoardState(Packets.ReportKind.GetOn, fare, mileage);

			// 모바일 배차 수신 후 승차 신호가 올라갈 때 주기를 다시 변경 한다.
			// 배차가 하나도 없을 경우에 모바일 배차가 수신 되므로 orderkind 구분 하지 않아도 된다.
			if (periodTerm != mConfiguration.getPst()) {
				periodTerm = mConfiguration.getPst();
				pollingPeriod(periodTerm);
			}

			// 대기 상태이면서 대기 고객 정보가 없을 때 주행이 올라오면 대기 취소를 한다.
			WaitingZone waitingZone = mRepository.getWaitingZone();
			if (waitingZone != null && mRepository.loadWaitCallInfo() == null) {
				requestWaitCancel(waitingZone.getWaitingZoneId());
			}
		}
	}

	/**
	 * 패킷을 요청한다.
	 * Socket 접속이 끊어지는 경우를 방지하기 위해
	 * 마지막 요청 이후 일정 시간 이내에 요청이 없을 경우
	 * Live 패킷을 전송하여 Connection을 유지한다.
	 */
	public void request(RequestPacket packet) {
		networkManager.request(packet);

		// 마지막으로 요청 된 리퀘스트 이후 일정 시간 부터 라이브 전송 한다.
		pollingHandler.removeMessages(MSG_LIVE);
		pollingHandler.sendEmptyMessageDelayed(MSG_LIVE, mConfiguration.getRt() * 1000);
	}

	/**
	 * Ack 패킷을 요청 한다. Retry로직이 포함되어 있다.(최대 3회 5초간격)
	 */
	public void requestAck(final int messageType, final int serviceNo, final int callNo) {
		ackRetryCount = 0;

		AckPacket packet = new AckPacket();
		packet.setServiceNumber(serviceNo);
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setAckMessage(messageType);
		packet.setParameter(callNo);

		Message msg = pollingHandler.obtainMessage();
		msg.what = MSG_ACK;
		msg.obj = packet;

		pollingHandler.removeMessages(MSG_ACK);
		pollingHandler.sendMessage(msg);
	}

	/**
	 * 서비스요청 패킷을 요청 한다.
	 * 요청 후 3초 이내에 응답이 없을 경우 Error를 보여준다.
	 */
	public void requestServicePacket(String phoneNumber, String vehicleNumber, boolean withTimer) {
		//LogHelper.e("REQ-LOGIN : requestServicePacket() : " + mConfiguration);
		mServiceResultPacket = new MutableLiveData<>();

		RequestServicePacket packet = new RequestServicePacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(Integer.valueOf(vehicleNumber));
		packet.setPhoneNumber(phoneNumber);
		packet.setCorporationType(mConfiguration.isCorporation() ? Packets.CorporationType.Corporation : Packets.CorporationType.Indivisual);
		packet.setProgramVersion(mConfiguration.getAppVersion());
		packet.setModemNumber(TextUtils.isEmpty(mConfiguration.getDevicePhoneNumber()) ? "" : mConfiguration.getDevicePhoneNumber()); // 단말의 실제 전화 번호
		request(packet);

		pollingHandler.removeMessages(MSG_SERVICE_ACK);
		if (withTimer) {
			pollingHandler.sendEmptyMessageDelayed(MSG_SERVICE_ACK, 10000);
		}
	}

	/**
	 * 환경설정요청 패킷을 요청한다.
	 */
	private void requestConfig(int cfgVersion) {
		RequestConfigPacket packet = new RequestConfigPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setConfigurationCode(cfgVersion);
		request(packet);
	}

	/**
	 * 공지사항요청 패킷을 요청한다.
	 */
	private void requestNotice() {
		RequestNoticePacket packet = new RequestNoticePacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		request(packet);
	}


	/**
	 * 메시지 요청 패킷을 요청한다.
	 */
	private void requestMessage() {
		RequestMessagePacket packetMsg = new RequestMessagePacket();
		packetMsg.setServiceNumber(mConfiguration.getServiceNumber());
		packetMsg.setCorporationCode(mConfiguration.getCorporationCode());
		packetMsg.setCarId(mConfiguration.getCarId());
		request(packetMsg);
	}

	private void requestMessageRead(int msgId) {
		RequestMessageReadPacket packetMsg = new RequestMessageReadPacket();
		packetMsg.setServiceNumber(mConfiguration.getServiceNumber());
		packetMsg.setCorporationCode(mConfiguration.getCorporationCode());
		packetMsg.setCarId(mConfiguration.getCarId());
		packetMsg.setMessageId(msgId);
		request(packetMsg);
	}

	/**
	 * 주기전송 패킷을 요청한다.
	 */
	private void requestPeriod() {
		PeriodSendingPacket packet = new PeriodSendingPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setSendingTime(getCurrentTime());
		packet.setGpsTime(gpsHelper.getTime());
		packet.setDirection(gpsHelper.getBearing());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());

		// FIXME: 2019-09-05 좌표 하드코딩
//		packet.setLongitude(127.1083173f);
//		packet.setLatitude(37.4024142f);

		packet.setSpeed(gpsHelper.getSpeed());
		packet.setBoardState(boardType);
		packet.setRestState(restType);
		request(packet);
	}

	/**
	 * 라이브 패킷을 요청한다.
	 */
	private void requestLive() {
		LivePacket packet = new LivePacket();
		packet.setCarId(mConfiguration.getCarId());
		request(packet);
	}

	/**
	 * Emergency 요청 패킷을 요청한다.
	 */
	private void requestEmergency() {
		RequestEmergencyPacket packet = new RequestEmergencyPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setEmergencyType(emergencyType);
		packet.setGpsTime(gpsHelper.getTime());
		packet.setDirection(gpsHelper.getBearing());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setSpeed(gpsHelper.getSpeed());
		packet.setTaxiState(boardType);
		request(packet);
	}

	/**
	 * 휴식/운행재개 패킷을 요청한다.
	 */
	public void requestRest(Packets.RestType restType) {
		LogHelper.e("changeCallStatus()");
		RequestRestPacket packet = new RequestRestPacket();
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		if (mConfiguration.getCorporationCode() == 0) {
			LogHelper.e("corporationCode is 0, server will not response..");
		}
		packet.setCarId(mConfiguration.getCarId());
		packet.setRestType(restType);
		request(packet);
	}


	/**
	 * 운행보고 패킷을 요청 한다. Retry 로직이 포함되어 있다. (최대 3회 5초 간격)
	 */
	public void requestReport(final int callNo, final int orderCount, final Packets.OrderKind orderKind,
	                          final String callDate, final Packets.ReportKind kind,
	                          final int fare, final int mileage) {
		reportRetryCount = 0;

		ServiceReportPacket packet = new ServiceReportPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setCallNumber(callNo);
		packet.setOrderCount(orderCount);
		packet.setOrderKind(orderKind);
		packet.setCallReceiptDate(callDate);
		packet.setReportKind(kind);
		packet.setGpsTime(gpsHelper.getTime());
		packet.setDirection(gpsHelper.getBearing());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setSpeed(gpsHelper.getSpeed());
		packet.setTaxiState(boardType);
		packet.setFare(fare);
		packet.setDistance(mileage);

		Message msg = pollingHandler.obtainMessage();
		msg.what = MSG_REPORT;
		msg.obj = packet;

		pollingHandler.removeMessages(MSG_REPORT);
		pollingHandler.sendMessage(msg);
	}


	/**
	 * 1. 승/빈차 신호를 서버에 전송 한다.
	 * 2. UI에서 조작한 탑승 하차 신호를 서버에 전송한다.
	 * 저장된 배차 정보가 없을 경우 주기 전송 패킷을 요청한다.
	 * 저장된 배차 정보가 있을 경우 운행 보고 패킷을 요청한다.
	 */
	public void requestBoardState(Packets.ReportKind kind, int fare, int mileage) {
		LogHelper.e("requestBoardState kind : " + kind);
		ResponseWaitAreaOrderInfoPacket wait = mRepository.loadWaitCallInfo();
		OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);

		if (wait == null && normal == null) {
			LogHelper.d(">> Not exist saved passenger info. request period sending");
			requestPeriod();

			// 2019. 01. 30 (용용)  콜없을때 : 주행/빈차시 길안내 취소 :  <메인화면>창 안닫히는 문제 + 메뉴바 안나타나는 문제개선
			// 콜메뉴 전체화면 상태에서 네비길안내 취소를 호출하면 네비화면으로 바뀌면서 메뉴바 갱신이 됨. 메뉴바 사라졌을경우 미터기 주행/빈차 조작으로 나타나게할수도 있을듯함
			if (kind == Packets.ReportKind.GetOn) {
				LogHelper.write("#### 승차 주기 보고");
			} else {
				LogHelper.write("#### 하차 주기 보고");
			}

			// 길에서 손님 태운 후 승차 상태에서 승차 중 배차를 받음 -> 하차 버튼 -> 고객 정보 창이 보여져야 함
			if (kind == Packets.ReportKind.GetOff) {
				LogHelper.e("requestBoardState() 하차 처리..");
				mMainApplication.setCurrentActivity(null);
				mRepository.resetCallInfo(Constants.CALL_STATUS_VACANCY);
				refreshSavedPassengerInfo(0);
				checkReservation();
			}
		} else {
			if (wait != null) {
				wait.setReported(true);
				mRepository.saveWaitCallInfo(wait);
				requestReport(
						wait.getCallNumber(), wait.getOrderCount(),
						wait.getOrderKind(), wait.getCallReceiptDate(),
						kind, fare, mileage);
			} else if (normal != null) {
				normal.setReported(true);
				mRepository.saveCallInfoWithOrderKind(normal, Packets.OrderKind.Normal);
				requestReport(
						normal.getCallNumber(), normal.getOrderCount(),
						normal.getOrderKind(), normal.getCallReceiptDate(),
						kind, fare, mileage);
			}
		}
	}

	/**
	 * 대기지역요청 패킷을 요청한다.
	 */
	public void requestWaitAreaNew(Packets.WaitAreaRequestType requestType, int startIndex) {
		mWaitArea = new MutableLiveData<>();

		RequestWaitAreaListPacket packet = new RequestWaitAreaListPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setRequestType(requestType);
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setStartIndex(startIndex);
		packet.setRequestCount(10);

		request(packet);
	}

	/**
	 * 대기결정 패킷을 요청한다.
	 */
	public void requestWaitDecisionNew(String waitAreaId) {
		mWaitDecision = new MutableLiveData<>();

		RequestWaitAreaDecisionPacket packet = new RequestWaitAreaDecisionPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setGpsTime(gpsHelper.getTime());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setWaitAreaId(waitAreaId);

		request(packet);
	}

	/**
	 * 대기취소 패킷을 요청한다.
	 */
	public void requestWaitCancel(String waitPlaceCode) {
		mWaitCancel = new MutableLiveData<>();
		// 저장된 대기지역이 있는데 대기지역을 다시 요청하는 경우는 취소의 케이스로 간주한다.
		ResponseWaitAreaOrderInfoPacket wait = mRepository.loadWaitCallInfo();
		if (wait != null) {
			requestReport(
					wait.getCallNumber(),
					wait.getOrderCount(),
					wait.getOrderKind(),
					wait.getCallReceiptDate(),
					Packets.ReportKind.Failed, 0, 0);
		}
		RequestWaitAreaCancelPacket packet = new RequestWaitAreaCancelPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setAreaCode(waitPlaceCode);
		request(packet);
	}

	/**
	 * 대기배차고객정보 요청 패킷을 요청한다.
	 */
	public void requestWaitPassengerInfo() {
		RequestWaitAreaOrderInfoPacket packet = new RequestWaitAreaOrderInfoPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		request(packet);
	}

	/**
	 * 실시간 위치 및 배차결정 패킷을 요청한다.
	 */
	public void requestOrderRealtime(Packets.OrderDecisionType type, OrderInfoPacket info) {
		RequestOrderRealtimePacket packet = new RequestOrderRealtimePacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setCallNumber(info.getCallNumber());
		packet.setCallReceiptDate(info.getCallReceiptDate());
		packet.setDecisionType(type);
		packet.setSendTime(getCurrentTime());
		packet.setGpsTime(gpsHelper.getTime());
		packet.setDirection(gpsHelper.getBearing());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setSpeed(gpsHelper.getSpeed());
		packet.setDistance(gpsHelper.getDistance(info.getLatitude(), info.getLongitude()));
		packet.setOrderCount(info.getOrderCount());
		request(packet);
	}


	public void requestOrderRealtime(Packets.OrderDecisionType type, Call call) {
		LogHelper.e("requestOrderRealtime : " + type);
		lastOrderDecisionType = type;
		if (call != null && call.getCallNumber() > 0) {
			RequestOrderRealtimePacket packet = new RequestOrderRealtimePacket();
			packet.setServiceNumber(mConfiguration.getServiceNumber());
			packet.setCorporationCode(mConfiguration.getCorporationCode());
			packet.setCarId(mConfiguration.getCarId());
			packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
			packet.setCallNumber(call.getCallNumber());
			packet.setCallReceiptDate(call.getCallReceivedDate());
			packet.setDecisionType(type);
			packet.setSendTime(getCurrentTime());
			packet.setGpsTime(gpsHelper.getTime());
			packet.setDirection(gpsHelper.getBearing());
			packet.setLongitude(gpsHelper.getLongitude());
			packet.setLatitude(gpsHelper.getLatitude());
			packet.setSpeed(gpsHelper.getSpeed());
			packet.setDistance(gpsHelper.getDistance((float)call.getDepartureLat(), (float)call.getDepartureLong()));
			packet.setOrderCount(call.getCallOrderCount());
			request(packet);
		}
	}

	// 배차상태 & 저장된 고객정보가 없을 경우 배차정보요청 패킷(GT-1A11) 전송
	public void requestCallInfo(int callNumber) {
		RequestCallInfoPacket packet = new RequestCallInfoPacket();
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		Calendar calendar = Calendar.getInstance();
		String today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
		packet.setCallReceiptDate(today);
		packet.setCallNumber(callNumber);
		//LogHelper.e("callInfoPacket : " + packet);
		request(packet);
	}

	public void requestToSendSMS(String msg, Call call) {
		mResponseSMSPacket = new MutableLiveData<>();

		RequestSendSMSPacket packet = new RequestSendSMSPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setCallReceiptDate(call.getCallReceivedDate());
		packet.setCallNumber(call.getCallNumber());
		packet.setContent(msg);
		request(packet);
	}

	public void requestMyInfo() {
		mResponseMyInfo = new MutableLiveData<>();

		RequestMyInfoPacket packet = new RequestMyInfoPacket();
		packet.setCarId(mConfiguration.getCarId());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		request(packet);
	}

	public void requestWaitCallList(Packets.WaitCallListType waitCallListType, int startIndex) {
		mResponseWaitCallListPacket = new MutableLiveData<>();

		RequestWaitCallListPacket packet = new RequestWaitCallListPacket();
		packet.setCarId(mConfiguration.getCarId());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setWaitCallListType(waitCallListType);
		packet.setStartIndex(startIndex);
		packet.setRequestCount(10);
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		request(packet);
	}

	public void requestWaitCallOrder(Call callInfo) {
		mResponseWaitCallOrderInfoPacket = new MutableLiveData<>();

		RequestWaitCallOrderPacket packet = new RequestWaitCallOrderPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setCallReceiptDate(callInfo.getCallReceivedDate());
		packet.setCallNumber(callInfo.getCallNumber());
		packet.setOrderCount(callInfo.getCallOrderCount());
		packet.setGpsTime(gpsHelper.getTime());
		packet.setDirection(gpsHelper.getBearing());
		packet.setLongitude(gpsHelper.getLongitude());
		packet.setLatitude(gpsHelper.getLatitude());
		packet.setSpeed(gpsHelper.getSpeed());
		packet.setDistance(callInfo.getDistance());

		packet.setOrderKind(Packets.OrderKind.WaitCall);
		request(packet);
	}

	public void requestNoticeList() {
		mNoticeListPacket = new MutableLiveData<>();

		RequestNoticeListPacket packet = new RequestNoticeListPacket();
		packet.setTargetServer(mConfiguration.isCorporation() ? 2 : 1);
		packet.setCarId(mConfiguration.getCarId());
		packet.setListType(0x01);

		request(packet);
	}


	public void requestStatistics() {
		mStatisticPacket = new MutableLiveData<>();

		RequestStatisticsPacket packet = new RequestStatisticsPacket();
		packet.setServiceNumber(mConfiguration.getServiceNumber());
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());
		packet.setMemo("");

		request(packet);
	}

	public void requestStatisticsDetail(Packets.StatisticListType type, Packets.StatisticPeriodType period, int startIndex) {
		mStatisticDetailPacket = new MutableLiveData<>();

		RequestStatisticsDetailPacket packet = new RequestStatisticsDetailPacket();
		packet.setCorporationCode(mConfiguration.getCorporationCode());
		packet.setCarId(mConfiguration.getCarId());
		packet.setQueryType(type);
		packet.setStartIndex(startIndex);
		packet.setRequestCount(5);
		packet.setQueryPeriod(period);
		packet.setPhoneNumber(mConfiguration.getDriverPhoneNumber());

		request(packet);
	}



	/**
	 * 배차 데이터를 아래의 순서로 업데이트 한다.
	 * 1. 전달 받은 콜넘버와 저장 받은 배차데이터를 비교하여
	 * 해당 사항이 있는 경우 배차데이터를 삭제 한다.
	 * 2. 배차1이 없고 배차2가 존재한다면 배차2 -> 배차1로 이동 후
	 * 배차2를 삭제 한다.
	 *
	 * @param callNo 콜번호
	 */
	private void refreshSavedPassengerInfo(int callNo) {
		LogHelper.d(">> refreshSavedPassengerInfo-callNo : " + callNo);
		ResponseWaitAreaOrderInfoPacket wait = mRepository.loadWaitCallInfo();
		if (wait != null && wait.getCallNumber() == callNo) {
			LogHelper.d(">> refreshSavedPassengerInfo - 1");

			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Wait);
			mRepository.clearWaitingZone();
		}

		OrderInfoPacket getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
		if (getOn != null && getOn.getCallNumber() == callNo) {
			LogHelper.d(">> refreshSavedPassengerInfo - 2");
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
		}

		OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
		if (normal != null && normal.getCallNumber() == callNo) {
			LogHelper.d(">> refreshSavedPassengerInfo - 3");
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Normal);
			OrderInfoPacket _getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
			mRepository.saveCallInfoWithOrderKind(_getOn, Packets.OrderKind.Normal);
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
		}

		LogHelper.d(">> refreshSavedPassengerInfo-end");
		if (getOn != null && normal == null) {
			LogHelper.d(">> refreshSavedPassengerInfo-change getOn data to normal");
			LogHelper.d(">> refreshSavedPassengerInfo - 4");
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Normal);
			OrderInfoPacket _getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);

			mRepository.saveCallInfoWithOrderKind(_getOn, Packets.OrderKind.Normal);
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);

			//배차 데이터 정리 완료 후, 승차중 배차 데이터가 존재하고, 일반 배차 데이터가 없을 경우
			//승차중 배차 데이터를 배차 상태로 UI에 표시한다.
			if (_getOn != null) {
				LogHelper.e("getOn is not null");
				Call call = new Call(_getOn);
				call.setCallStatus(Constants.CALL_STATUS_ALLOCATED);
				call.setDistance(gpsHelper.getDistance((float)call.getDepartureLat(), (float)call.getDepartureLong()));
				mRepository.updateCallInfo(call);
			}
		}
	}


	/**
	 * 저장되어 있는 배차가 있다면 고객 정보팝업을 보여준다.
	 */
	private void checkReservation() {
		LogHelper.d(">> Check reserved call info.");
		OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
		if (normal != null) {
			LogHelper.d(">> has passenger info.");
			LogHelper.e("승차중 배차 존재, 배차 상태로 UI 변경");
			Call call = new Call(normal);
			call.setCallStatus(Constants.CALL_STATUS_ALLOCATED);
			call.setDistance(gpsHelper.getDistance((float)call.getDepartureLat(), (float)call.getDepartureLong()));
			mRepository.updateCallInfo(call);
		} else {
			LogHelper.e(">> doesn't have passenger info.");
		}
	}

	//----------------------------------------------------------------------------------------
	// NetworkManager Callback
	//----------------------------------------------------------------------------------------
	private NetworkListener networkListener = new NetworkListener() {

		@Override
		public void onConnectedServer() {
			LogHelper.write("#### Socket Connected");
			if (hasCertification) {
				// 소켓 연결이 끊어진 후 재접속시 주기 전송을 한번 한다.
				requestPeriod();
			}
		}

		@Override
		public void onDisconnectedServer(ErrorCode code) {
			LogHelper.write("#### Connect Error : " + code);
		}

		@Override
		public void onReceivedPacket(@NonNull ResponsePacket response) {
			LogHelper.write(">> RES " + response);

			Context context = getApplication();
			mConfiguration = mRepository.getConfig();

			int messageType = response.getMessageType();

			if (messageType != Packets.RES_SERVICE
					&& !hasCertification) {
				LogHelper.d(">> Skip received packet. Invalid service certification.");
				return;
			}

			switch (messageType) {
				case Packets.RES_SERVICE: { // 서비스 요청 응답
					pollingHandler.removeMessages(MSG_SERVICE_ACK);

					ServiceRequestResultPacket resPacket = (ServiceRequestResultPacket) response;
					mServiceResultPacket.postValue(resPacket);
					LogHelper.e("서비스 요청 응답 : " + resPacket);

					// TODO: 2019-10-14 버전 차이 날 경우 플레이 스토어 안내 팝업 처리해야 할 듯.
					/*if (resPacket.getProgramVersion() > getConfiguration.getAppVersion()) {
						startService(new Intent("com.thinkware.florida.otaupdater.Updater"));
						LogHelper.i("ota updater 실행");
					}*/

					if (resPacket.getCertificationResult() == Packets.CertificationResult.Success) {
						hasCertification = true;
						isUsedDestination = resPacket.isUsedDestination();

						periodTerm = mConfiguration.getPst();
						ResponseWaitAreaOrderInfoPacket wait = mRepository.loadWaitCallInfo();
						OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
						if (wait != null
								&& wait.getOrderKind() == Packets.OrderKind.Mobile
								&& !wait.isReported()) {
							periodTerm = mConfiguration.getRc();
						} else if (normal != null
								&& normal.getOrderKind() == Packets.OrderKind.Mobile
								&& !normal.isReported()) {
							periodTerm = mConfiguration.getRc();
						}
						pollingPeriod(periodTerm);

						if (resPacket.getConfigurationVersion() > mConfiguration.getConfigurationVersion()) {
							// 환경 설정 버전이 높을 경우 환경 설정을 요청한다
							// 응답 후 파일로 저장하고 IP를 변경하도록 한다.
							requestConfig(mConfiguration.getConfigurationVersion());
						}

						// 서비스 번호
						if (resPacket.getServiceNumber() != mConfiguration.getServiceNumber()) {
							mConfiguration.setServiceNumber(resPacket.getServiceNumber());
						}

						if (mRepository.getWaitingZone() != null) {
							pollingCheckWaitRange(true);
						}

						//공지사항
						final int noticeCode = resPacket.getNoticeCode();
						if (noticeCode > 0) {
							if (noticeCode != mConfiguration.getLastNoticeVersion()) {
								mConfiguration.setLastNoticeVersion(noticeCode);
								requestNotice();
							}
						} else {
							mConfiguration.setLastNoticeVersion(noticeCode);
						}


						WaitingZone waitingZone = mRepository.getWaitingZone();
						//if (waitOrderInfoPacket != null && boardType != Packets.BoardType.Boarding) {
						LogHelper.e("normal : " + normal);
						if (waitingZone != null && normal == null) {
							// 2. 저장 되어 있는 대기요청고객정보가 없지만 대기요청 상태의 경우 0x1517 대기배차고객정보 요청해서 정보를 가져온다.
							LogHelper.e("requestWaitPassengerInfo()");
							requestWaitPassengerInfo();
						} else if (waitingZone == null) {
							mRepository.clearWaitingZone();
						}

						mRepository.updateConfig(mConfiguration);
					}
					break;
				}

				case Packets.RES_CONFIG: { // 환경 설정 응답
					ServiceConfigPacket packet = (ServiceConfigPacket) response;
					//mConfiguration.setCarId(packet.getCarId());
					mConfiguration.setConfigurationVersion(packet.getVersion());
					mConfiguration.setPst(packet.getPeriodSendingTime());
					mConfiguration.setPsd(packet.getPeriodSendingRange());
					mConfiguration.setRc(packet.getRetryNumber());
					mConfiguration.setRt(packet.getRetryTime());
					mConfiguration.setCvt(packet.getCallAcceptanceTime());
					mConfiguration.setEmergencyPeriodTime(packet.getPeriodEmergency());
					mConfiguration.setCallServerIp(packet.getCallServerIp());
					mConfiguration.setCallServerPort(packet.getCallServerPort());
					mConfiguration.setUpdateServerIp(packet.getUpdateServerIp());
					mConfiguration.setUpdateServerPort(packet.getUpdateServerPort());
					mConfiguration.setPassword(packet.getPassword());
					mRepository.updateConfig(mConfiguration);

					// 환경 설정 아이피가 변경 되었으므로 변경된 아이피로 접속 한다.
					if (!networkManager.getIp().equals(mConfiguration.getCallServerIp())) {
						networkManager.disconnect();
						networkManager.connect(mConfiguration.getCallServerIp(), mConfiguration.getCallServerPort());
					}
				}
				break;


				case Packets.RES_NOTICE: { // 공지사항
					NoticesPacket packet = (NoticesPacket) response;
					Notice notice = new Notice();
					notice.setId(packet.getNoticeCode());
					notice.setTitle(packet.getNoticeTitle());
					notice.setContent(packet.getNotice());
					notice.setNotice(true);
					mRepository.insertNotice(notice);
				}
				break;

				case Packets.RES_PERIOD: { // 주기 전송 응답
					ResponsePeriodSendingPacket packet = (ResponsePeriodSendingPacket) response;
					// 메시지 존재하는 경우
					if (packet.hasMessage()) {
						requestMessage();
					}

					// 배차상태 & 저장된 고객정보가 없을 경우 배차정보요청 패킷(GT-1A11) 전송
					LogHelper.e("packet.hasOrder : " + packet.hasOrder());
					if (packet.hasOrder()) {
						OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
						OrderInfoPacket getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
						if (normal == null && getOn == null) {
							LogHelper.e("orderInfoPacket is null");
							requestCallInfo(packet.getCallNumber());
						}
					}

					LogHelper.e("current boardType : " + boardType + " / packet boarded : " + packet.boarded());
					if (boardType == Packets.BoardType.Empty && packet.boarded()) {
						boardType = Packets.BoardType.Boarding;
						mCallInfo.setCallStatus(Constants.CALL_STATUS_BOARDED);
						mRepository.updateCallInfo(mCallInfo);
					} else if (boardType == Packets.BoardType.Boarding && !packet.boarded()) {
						boardType = Packets.BoardType.Empty;
						mCallInfo.setCallStatus(Constants.CALL_STATUS_VACANCY);
						mRepository.updateCallInfo(mCallInfo);
					}
				}
				break;

				case Packets.RES_REST: { // 휴식/운행재개
					Packets.RestType restType = ((ResponseRestPacket) response).getRestType();
					LogHelper.e("RESPONSE : " + response.toString());
					if (restType != null &&
							(restType == Packets.RestType.Rest || restType == Packets.RestType.Working)) {
						LogHelper.e("RESPONSE 2: " + response.toString());
						ScenarioService.this.restType = restType;
						if (restType == Packets.RestType.Rest) {
							mCallInfo.setCallStatus(Constants.CALL_STATUS_RESTING);
							WavResourcePlayer.getInstance(context).play(R.raw.voice_112);

							WaitingZone waitingZone = mRepository.getWaitingZone();
							if (waitingZone != null) {
								mRepository.requestWaitCancel(waitingZone.getWaitingZoneId());
								mRepository.clearWaitingZone();
							}

						} else {
							mCallInfo.setCallStatus(Constants.CALL_STATUS_WORKING);
							WavResourcePlayer.getInstance(context).play(R.raw.voice_114);
						}

						mRepository.updateCallInfo(mCallInfo);
					}
				}
				break;

				case Packets.RES_REPORT: { // 운행보고 응답
					pollingHandler.removeMessages(MSG_REPORT);
					mMainApplication.setWasBackground(mMainApplication.isBackground());

					ResponseServiceReportPacket packet = (ResponseServiceReportPacket) response;
					Packets.ReportKind reportKind = packet.getReportKind();
					LogHelper.e("response service report : " + packet.toString());

					if (reportKind == Packets.ReportKind.GetOff
							|| reportKind == Packets.ReportKind.Failed
							|| reportKind == Packets.ReportKind.FailedPassengerCancel
							|| reportKind == Packets.ReportKind.FailedNoShow
							|| reportKind == Packets.ReportKind.FailedUseAnotherTaxi
							|| reportKind == Packets.ReportKind.FailedEtc) {
						// 빈차등이 회사별로 다르기 때문에 예약과 빈차를 같이 보내야 정상 처리 된다.
						if (packet.getReportKind() == Packets.ReportKind.Failed
								|| reportKind == Packets.ReportKind.FailedPassengerCancel
								|| reportKind == Packets.ReportKind.FailedNoShow
								|| reportKind == Packets.ReportKind.FailedUseAnotherTaxi
								|| reportKind == Packets.ReportKind.FailedEtc) {
							WavResourcePlayer.getInstance(context).play(R.raw.voice_151);
						}

						//하차 응답 수신
						LogHelper.e("하차 응답 수신");
						boardType = Packets.BoardType.Empty;
						mCallInfo.setCallStatus(Constants.CALL_STATUS_ALIGHTED);
						mRepository.updateCallInfo(mCallInfo);

						((MainApplication)getApplication()).setCurrentActivity(null);
						refreshSavedPassengerInfo(packet.getCallNumber());
						checkReservation();
					} else if (reportKind == Packets.ReportKind.GetOn) {
						LogHelper.e("승차 응답 수신");
						boardType = Packets.BoardType.Boarding;
						mCallInfo.setCallStatus(Constants.CALL_STATUS_BOARDED);
						mRepository.updateCallInfo(mCallInfo);
					}

					if (reportKind == Packets.ReportKind.GetOn) {
						LogHelper.write("#### 승차 보고 -> callNo : " + packet.getCallNumber());
					} else if (reportKind == Packets.ReportKind.GetOff) {
						LogHelper.write("#### 하차 보고 -> callNo : " + packet.getCallNumber());
					} else if (reportKind == Packets.ReportKind.Failed) {
						LogHelper.write("#### 탑승 실패 보고 -> callNo : " + packet.getCallNumber());
						mRepository.clearWaitingZone();
					}
				}
				break;

				case Packets.RES_MESSAGE:  // 메시지 응답
				case Packets.RES_MESSAGE_NEW: { // 메시지 응답
					Notice message;
					if (messageType == Packets.RES_MESSAGE) {
						ResponseMessagePacket responseMessagePacket = (ResponseMessagePacket) response;
						message = new Notice("", responseMessagePacket.getMessage(), "", false);
					} else {
						ResponseMessageNewPacket responseMessageNewPacket = (ResponseMessageNewPacket) response;
						message = new Notice("", responseMessageNewPacket.getMessage(), responseMessageNewPacket.getMessageSentDate(), false);

						requestMessageRead(responseMessageNewPacket.getMessageId());
					}

					mRepository.insertNotice(message);

					if (hasCertification) {
						Popup popup = new Popup
								.Builder(Popup.TYPE_TWO_BTN_WITH_TITLE, Constants.DIALOG_TAG_MESSAGE)
								.setTitle(getString(R.string.common_message))
								.setBtnLabel(getString(R.string.common_read_more), getString(R.string.common_close))
								.setContent(message.getContent())
								.build();
						PopupActivity.startActivity(ScenarioService.this, popup);
						WavResourcePlayer.getInstance(ScenarioService.this).play(R.raw.voice_142);
					}
				}
				break;

				case Packets.RES_ORDER_INFO_BROADCAST: {// 배차데이터 (목적지 추가)
					if (emergencyType == Packets.EmergencyType.Begin
							|| restType == Packets.RestType.Rest) {
						// 응급 상황 중이면 콜 수신을 무시한다.
						LogHelper.d(">> Skip receive call when emergency or rest");
						return;
					}

					mMainApplication.setWasBackground(mMainApplication.isBackground());
					OrderInfoPacket packet = (OrderInfoPacket) response;
					LogHelper.e("Order info : " + packet.toString());

					int serviceNumber = mConfiguration.getServiceNumber();

					ResponseWaitAreaOrderInfoPacket waitCall = mRepository.loadWaitCallInfo();
					OrderInfoPacket normalCall = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
					OrderInfoPacket getOnCall = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
					LogHelper.e("waitCall : " + (waitCall == null));
					LogHelper.e("normalCall : " + (normalCall == null));
					LogHelper.e("getOnCall : " + (getOnCall == null));


					//LogHelper.e("주기 전송 중지... : " + periodTerm * 1000);
					pollingHandler.removeMessages(MSG_PERIOD);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							//LogHelper.e("주기 전송 재개...");
							pollingPeriod(periodTerm);
						}
					}, periodTerm * 1000);


					if ((waitCall != null || normalCall != null)
							&& getOnCall != null) {
						// - 0x14 : 배차가 2개 이상인지 (일반배차가 있는 상태에서 또 일반배차가 내려 올 경우)
						LogHelper.e("if 1");
						requestOrderRealtime(Packets.OrderDecisionType.MultipleOrder, packet);
					} else if ((normalCall != null || waitCall != null)
							&& boardType == Packets.BoardType.Empty) {
						// - 0x0D : 배차가 1개 일때 - 현재상태가 빈차일 경우 (운행보고 안함 : 콜받고 운행보고 안된상태에서 콜수신된경우)
						LogHelper.e("if 2");
						requestOrderRealtime(Packets.OrderDecisionType.AlreadyOrderd, packet);
					} else if (boardType == Packets.BoardType.Boarding
							&& packet.getOrderKind() == Packets.OrderKind.Normal) {
						// - 0x0A : 주행중 일반콜 수신될 경우
						LogHelper.e("if 3");
						requestOrderRealtime(Packets.OrderDecisionType.Driving, packet);
					} else if (mRepository.getWaitingZone() != null
							&& packet.getOrderKind() == Packets.OrderKind.Normal) {
						// - 0x0C : 대기배차 상태인데 일반콜 수신될 경우
						LogHelper.e("if 4");
						requestOrderRealtime(Packets.OrderDecisionType.Waiting, packet);
					} else if (mRepository.getWaitingZone() != null
							&& packet.getOrderKind() == Packets.OrderKind.WaitOrderTwoWay) {
						// 대기배차 상태인데 양방향 대기배차 수신될 경우 (하남사용)
						LogHelper.e("if 5");
						mRepository.saveCallInfoWithOrderKind(packet, Packets.OrderKind.Temp);
						//복지콜 추가로 인한 분기처리. - 서비스 번호
						if (serviceNumber == Constants.AREA_SUNGNAM_BOKJI) {
							requestOrderRealtime(Packets.OrderDecisionType.Request, packet);
						} else {
							WavResourcePlayer.getInstance(context).play(R.raw.voice_160_116);

							LogHelper.e("need to show CallReceivingActivity 111");
							updateCallInfo(packet, Constants.CALL_STATUS_RECEIVING);
							showCallReceivingActivity(true);
						}
					} else {
						LogHelper.e("else");
						mRepository.saveCallInfoWithOrderKind(packet, Packets.OrderKind.Temp);

						//복지콜 추가로 인한 분기처리. - 서비스 번호
						if (serviceNumber == Constants.AREA_SUNGNAM_BOKJI) {
							requestOrderRealtime(Packets.OrderDecisionType.Request, packet);
						} else {
							if (packet.getMessageType() == Packets.RES_ORDER_INFO_BROADCAST
									&& packet.getCallClass() > 0) {
								WavResourcePlayer.getInstance(context).play(R.raw.voice_170);
							} else {
								WavResourcePlayer.getInstance(context).play(R.raw.voice_160_116);
							}

							// 일반 배차와 승차중 배차일 경우 콜 수신 화면 표시
							LogHelper.e("need to show CallReceivingActivity 222");
							updateCallInfo(packet, Constants.CALL_STATUS_RECEIVING);

							showCallReceivingActivity(true);
						}
					}
				}
				break;

				case Packets.RES_WAIT_AREA_ORDER_INFO: { // 대기배차고객정보 응답
					LogHelper.e("RES_WAIT_AREA_ORDER_INFO~~~~~");
					ResponseWaitAreaOrderInfoPacket resp = (ResponseWaitAreaOrderInfoPacket) response;
					if (resp.getCallNumber() > 0) {
						//WavResourcePlayer.getInstance(context).play(R.raw.voice_132);
						// 서버에 Packet을 한번 요청하면 데이터가 초기화 되기 때문에 콜번호가 유효한 경우에만 저장을 한다.
						//mRepository.saveWaitCallInfo(resp);

						// 대기 배차 완료시 서버의 대기목록에서 빠지므로 로컬 파일 지우도록 한다.
						mRepository.clearWaitingZone();
						mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Wait);
						mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.WaitOrder);
						mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.WaitCall);
						requestAck(resp.getMessageType(), mConfiguration.getServiceNumber(), resp.getCallNumber());

						OrderInfoPacket orderInfo = new OrderInfoPacket(resp);
						mRepository.saveCallInfoWithOrderKind(orderInfo, Packets.OrderKind.Normal);
						updateCallInfo(orderInfo, Constants.CALL_STATUS_ALLOCATED);

						LogHelper.write("#### 콜 수락(대기) -> callNo : " + resp.getCallNumber());
					}
				}
				break;

				case Packets.RES_ORDER_INFO: { // 고객정보재전송 응답
					LogHelper.e("CALLER INFO RESEND~~~~");
					CallerInfoResendPacket p = (CallerInfoResendPacket) response;

					OrderInfoPacket tempPacket = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Temp);
					// 임시 저장 패킷이 없는 경우 배차데이터 처리(1314)까지는 완료가 되었다는 의미이므로
					// 고객 정보가 있는 경우 고객 정보 팝업을 보여준다.
					LogHelper.e("CALLER INFO RESEND~~~~ : " + (tempPacket == null));
					if (tempPacket == null) {
						requestAck(messageType, mConfiguration.getServiceNumber(), p.getCallNumber());

						if (p.getOrderKind() == Packets.OrderKind.GetOnOrder) {
							LogHelper.e("CALLER INFO RESEND~~~~2 : " + (p.getOrderKind() == Packets.OrderKind.GetOnOrder));

							OrderInfoPacket getOn = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);
							OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
							if (getOn != null && normal == null) {
								mRepository.saveCallInfoWithOrderKind(getOn, Packets.OrderKind.Normal);
								mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.GetOnOrder);

							} else if (getOn != null && getOn.getCallNumber() == p.getCallNumber()) {
								updateCallInfo(getOn, Constants.CALL_STATUS_ALLOCATED);

							} else {
								OrderInfoPacket orderInfo = new OrderInfoPacket(p);
								mRepository.saveCallInfoWithOrderKind(orderInfo, Packets.OrderKind.GetOnOrder);
								updateCallInfo(orderInfo, Constants.CALL_STATUS_ALLOCATED);
							}
						} else {
							LogHelper.e("CALLER INFO RESEND~~~~3 : " + p.getOrderKind());

							OrderInfoPacket normal = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Normal);
							if (normal != null && normal.getCallNumber() == p.getCallNumber()) {
								updateCallInfo(normal, Constants.CALL_STATUS_ALLOCATED);
							} else {
								// 주기 전송에서 hasOrder가 true로 내려오지만, 해당 콜 종류를 알 수 없어 normal을 기준으로 판단하므로, normal 콜에 대기배차 고객정보를 저장함.
								OrderInfoPacket orderInfo = new OrderInfoPacket(p);
								mRepository.saveCallInfoWithOrderKind(orderInfo, Packets.OrderKind.Normal);
								updateCallInfo(orderInfo, Constants.CALL_STATUS_ALLOCATED);
							}
						}
					} else {
						// 임시 저장 패킷이 남아 있는 경우 배차데이터 처리(1314)를 받지 못했다는 뜻이므로
						// 1314 수신과 동일하게 처리 한다. (Packets.RES_ORDER_INFO_PROC)

						boolean isFailed = checkCallValidation(messageType, tempPacket, p.getCallNumber(), p.getCarId());
						processCallInfo(context, messageType, tempPacket, p.getCallNumber(), isFailed);
					}
				}
				break;

				case Packets.RES_ORDER_INFO_PROC: { // 배차데이터 처리
					OrderInfoPacket tempPacket = mRepository.loadCallInfoWithOrderKind(Packets.OrderKind.Temp);
					LogHelper.e("RES_ORDER_INFO_PROC : " + (tempPacket == null));
					OrderInfoProcPacket p = (OrderInfoProcPacket) response;
					boolean isFailed;
					if (tempPacket == null || p.getOrderProcType() != Packets.OrderProcType.Display) {
						LogHelper.e("RES_ORDER_INFO_PROC if");
						isFailed = true;
					} else {
						LogHelper.e("RES_ORDER_INFO_PROC else ");
						isFailed = checkCallValidation(messageType, tempPacket, p.getCallNumber(), p.getCarId());
					}

					if (tempPacket != null && tempPacket.getOrderKind() == Packets.OrderKind.WaitOrderTwoWay) {
						//대기콜(하남)인 경우, 배차 처리되면 대기 상태 해제 한다. 그리고, 아이나비 화면으로 가기 때문에 부작용 없다고 생각한다.
						mRepository.clearWaitingZone();
					}

					processCallInfo(context, messageType, tempPacket, p.getCallNumber(), isFailed);
				}
				break;


				case Packets.RES_SEND_SMS: {
					ResponseSMSPacket resPacket = (ResponseSMSPacket) response;
					LogHelper.e("RES_SEND_SMS : " + resPacket);
					mResponseSMSPacket.postValue(resPacket);
				}
				break;

				case Packets.RES_MY_INFO: {
					ResponseMyInfoPacket resPacket = (ResponseMyInfoPacket) response;
					LogHelper.e("RES_MY_INFO : " + resPacket);
					mResponseMyInfo.postValue(resPacket);
				}
				break;


				case Packets.RES_WAIT_CALL_LIST: {
					ResponseWaitCallListPacket resPacket = (ResponseWaitCallListPacket) response;
					LogHelper.e("RES_WAIT_CALL_LIST : " + resPacket);
					mResponseWaitCallListPacket.postValue(resPacket);
				}
				break;

				case Packets.RES_WAIT_CALL_ORDER: {
					ResponseWaitCallOrderInfoPacket resPacket = (ResponseWaitCallOrderInfoPacket) response;
					mResponseWaitCallOrderInfoPacket.postValue(resPacket);
					LogHelper.e("RES_WAIT_CALL_ORDER : " + resPacket);


					OrderInfoPacket tempPacket = new OrderInfoPacket(resPacket);
					processCallInfo(context, messageType, tempPacket, tempPacket.getCallNumber(), !resPacket.isSuccess());

					if (resPacket.isSuccess()) {
						LogHelper.e("대기콜 배차 성공 - 대기 지역 대기 취소 처리");

						WaitingZone waitingZone = mRepository.getWaitingZone();
						if (waitingZone != null) {
							requestWaitCancel(waitingZone.getWaitingZoneId());
							mRepository.clearWaitingZone();
							mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Wait);
							mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.WaitOrder);
							mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.WaitCall);
						}
					}
				}
				break;

				case Packets.RES_NOTICE_LIST: {
					ResponseNoticeListPacket resPacket = (ResponseNoticeListPacket) response;
					LogHelper.e("RES_NOTICE_LIST : " + resPacket);
					mNoticeListPacket.postValue(resPacket);
				}
				break;

				case Packets.RES_STATISTICS: {
					ResponseStatisticsPacket resPacket = (ResponseStatisticsPacket) response;
					LogHelper.e("RES_STATISTICS : " + resPacket);
					mStatisticPacket.postValue(resPacket);
				}
				break;

				case Packets.RES_STATISTICS_DETAIL: {
					ResponseStatisticsDetailPacket resPacket = (ResponseStatisticsDetailPacket) response;
					LogHelper.e("RES_STATISTICS_DETAIL : " + resPacket);
					mStatisticDetailPacket.postValue(resPacket);
				}
				break;

				case Packets.RES_WAIT_AREA_LIST: {
					ResponseWaitAreaListPacket resPacket = (ResponseWaitAreaListPacket) response;
					LogHelper.e("RES_WAIT_AREA_LIST : " + resPacket);
					mWaitArea.postValue(resPacket);
				}
				break;

				case Packets.RES_WAIT_AREA_DECISION: {
					ResponseWaitAreaDecisionPacket resPacket = (ResponseWaitAreaDecisionPacket) response;
					LogHelper.e("RES_WAIT_AREA_LIST : " + resPacket);

					mWaitDecision.postValue(resPacket);
				}
				break;

				case Packets.RES_WAIT_AREA_CANCEL: {
					ResponseWaitAreaCancelPacket resPacket = (ResponseWaitAreaCancelPacket) response;
					LogHelper.e("RES_WAIT_AREA_CANCEL : " + resPacket);
					mWaitCancel.postValue(resPacket);
					if (resPacket.getWaitCancelType() == Packets.WaitCancelType.Success
							|| resPacket.getWaitCancelType() == Packets.WaitCancelType.AlreadyCancel) {
						mRepository.clearWaitingZone();
					} else if (resPacket.getWaitCancelType() == Packets.WaitCancelType.Exist) {
						requestWaitPassengerInfo();
					}
				}
				break;
			}
		}
	};

	private boolean checkCallValidation(int messageType, OrderInfoPacket tempPacket, int callNumber, int carId) {
		LogHelper.e("checkCallValidation() tempPacket : " + tempPacket);
		boolean isFailed;
		if (tempPacket.getCallNumber() != callNumber) {
			// 실시간 위치 및 배차요청으로 올린 콜넘버와 응답의 콜넘버가 다른 겨우 서비스 넘버 97로 REQ_ACK
			requestAck(messageType, 97, callNumber);
			isFailed = true;
		} else if (tempPacket.getCarId() != carId) {
			// 실시간 위치 및 배차요청으로 올린 콜ID와 응답의 콜ID가 다른 겨우 서비스 넘버 98로 REQ_ACK
			requestAck(messageType, 98, callNumber);
			isFailed = true;
		} else if (tempPacket.getOrderKind() != Packets.OrderKind.GetOnOrder
				&& boardType == Packets.BoardType.Boarding) {
			// 승차 중 인데 승차 중 배차가 아니면
			requestAck(messageType, 99, callNumber);
			isFailed = true;
		} else {
			isFailed = false;
		}
		return isFailed;
	}

	private void processCallInfo(Context context, int messageType, OrderInfoPacket tempPacket, int callNumber, boolean isFailed) {
		if (isFailed) {
			LogHelper.e("processCallInfo - Failed..... lastOrderDecisionType : " + lastOrderDecisionType);
			//PND와 동시 사용하는 경우에, PND에서 배차 요청(수락)을 한 경우, SP는 해당 결과를 같이 통보(배차데이터 처리(5314)) 받는다.
			//하지만 그 사이에 SP는 배차 방송에 대한 응답 시간이 만료되어 거절을 전송하면서, TEMP 콜정보를 삭제한다.
			//그 이후에 서버로 부터 배차데이터 처리(5314)를 수신할 경우, 갖고 있는 콜정보가 없으므로, 단말에서 배차 실패로 판단한다.
			//따라서 1차적으로 lastOrderDecisionType 값이 요청인 경우에는 요청에 대한 실패로 판단하며,
			//lastOrderDecisionType 값이 거절이며 tempPacket이 없을 경우 서버로 고객정보 재전송을 요청한다.
			//하지만 이 경우 서버가 아직 PND에서 ACK를 수신하기 전 일 수 있으므로, 3초 정도의 딜레이를 주고 요청하며,
			//그 이후에는 주기전송에서 배차정보를 요청하여 스마트폰에서 배차정보를 표시하는 딜레이를 줄인다.

			if (lastOrderDecisionType == Packets.OrderDecisionType.Request) {
				WavResourcePlayer.getInstance(context).play(R.raw.voice_122);
			} else if (lastOrderDecisionType == Packets.OrderDecisionType.Reject && tempPacket == null && callNumber > 0){
				LogHelper.e("고객정보 재전송 요청 : " + callNumber);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						requestCallInfo(callNumber);
					}
				}, 2000);
			}

			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Temp);
			this.changeCallStatus(Constants.CALL_STATUS_ALLOCATION_FAILED);

			LogHelper.e("processCallInfo - Failed..... isBackground : " + mMainApplication.isBackground());
			if (mMainApplication.isBackground()) {
				showCallReceivingActivity(false);
			}

		} else {
			int status;
			if (tempPacket.getOrderKind() == Packets.OrderKind.GetOnOrder) {
				mRepository.saveCallInfoWithOrderKind(tempPacket, Packets.OrderKind.GetOnOrder);
				LogHelper.write("#### 콜 수락(승차중배차) -> callNo : " + tempPacket.getCallNumber());
				status = Constants.CALL_STATUS_ALLOCATED_WHILE_GETON;
			} else {
				mRepository.saveCallInfoWithOrderKind(tempPacket, Packets.OrderKind.Normal);
				LogHelper.write("#### 콜 수락(일반) -> callNo : " + tempPacket.getCallNumber());
				status = Constants.CALL_STATUS_ALLOCATED;
			}

			// 모바일 배차 완료시 주기전송 간격을 8초로 변경 한다.
			// 승차신호가 올라오면 정상 주기로 다시 변경 한다.
			if (tempPacket.getOrderKind() == Packets.OrderKind.Mobile) {
				periodTerm = mConfiguration.getRc();
				pollingPeriod(periodTerm);
			}


			// 승차 중이라면, 배차 완료 여부를 음성으로만 알려줌.
			// 승차 중이 아니라면 화면으로 알려줌.
			if (boardType != Packets.BoardType.Boarding) {
				// 주행 중이 아닐 때는 고객 정보 창에서 음성이 나온다.
				updateCallInfo(tempPacket, status);
				if (mMainApplication.isBackground()) {
					showCallReceivingActivity(false);
				}
			} else {
				WavResourcePlayer.getInstance(context).play(R.raw.voice_120);
				updateCallInfo(tempPacket, status);
			}
			requestAck(messageType, mConfiguration.getServiceNumber(), callNumber);
			mRepository.clearCallInfoWithOrderKind(Packets.OrderKind.Temp);
		}
	}

	private void showCallReceivingActivity(boolean needToShowNew) {
		LogHelper.e("showCallReceivingActivity : " + needToShowNew + ",hasCertification: " + hasCertification);
		if (hasCertification) {
			Intent intent = new Intent(ScenarioService.this, CallReceivingActivity.class);
			if (needToShowNew) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			startActivityWithPendingIntent(intent);
		}
	}

	private void updateCallInfo(OrderInfoPacket packet, int callStatus) {
		Call call = new Call(packet);
		call.setCallStatus(callStatus);
		call.setDistance(gpsHelper.getDistance((float) call.getDepartureLat(), (float) call.getDepartureLong()));
		mRepository.updateCallInfo(call);
	}


	//----------------------------------------------------------------------------------------
	// polling & timer
	//----------------------------------------------------------------------------------------
	/**
	 * 주기 전송 패킷을 일정 간격마다 요청 한다.
	 */
	private void pollingPeriod(int period) {
		LogHelper.d(">> Polling period : " + period + " sec");
		pollingHandler.removeMessages(MSG_PERIOD);

		Message msg = pollingHandler.obtainMessage();
		msg.what = MSG_PERIOD;
		msg.arg1 = period;

		pollingHandler.sendMessage(msg);
	}

	/**
	 * Emergency 요청 패킷을 일정 간격마다 요청 한다.
	 */
	public void pollingEmergency() {
		pollingHandler.removeMessages(MSG_EMERGENCY);
		pollingHandler.sendEmptyMessage(MSG_EMERGENCY);

		// 응급 상황에서는 주기를 올리지 않아야 한다.
		pollingHandler.removeMessages(MSG_PERIOD);
	}

	/**
	 * 지정된 거리를 벗어 날 경우 대기취소 패킷을 요청 한다.
	 */
	private void pollingCheckWaitRange(boolean start) {
		// 범위를 벗어 난 상태에서 speed 5 이상이면 대기 취소를 요청 할 것 (5초 주기로 체크)
		pollingHandler.removeMessages(MSG_AREA_CHECK);
		if (start) {
			pollingHandler.sendEmptyMessage(MSG_AREA_CHECK);
		}
	}

	private void startActivityWithPendingIntent(Intent intent) {
		PendingIntent pendingIntent = PendingIntent.getActivity(ScenarioService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long now = Calendar.getInstance().getTimeInMillis();
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, now, pendingIntent);
	}


	@SuppressLint("HandlerLeak")
	private Handler pollingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			if (isDestroyed) {
//				return;
//			}

			switch (msg.what) {
				case MSG_PERIOD:
					requestPeriod();
					int period = msg.arg1;

					Message msgNew = obtainMessage();
					msgNew.what = MSG_PERIOD;
					msgNew.arg1 = period;
					sendMessageDelayed(msgNew, period * 1000);
					break;
				case MSG_LIVE:
					requestLive();
					pollingHandler.sendEmptyMessageDelayed(MSG_LIVE, mConfiguration.getRt() * 1000);
					break;
				case MSG_EMERGENCY:
					requestEmergency();
					sendEmptyMessageDelayed(MSG_EMERGENCY, mConfiguration.getEmergencyPeriodTime() * 1000);
					break;
				case MSG_AREA_CHECK:
					LogHelper.d(">> Wait Area : Search");
					WaitingZone waitingZone = mRepository.getWaitingZone();
					LogHelper.d(">> Wait Area : Speed -> " + gpsHelper.getSpeed());
					if (waitingZone != null && gpsHelper.getSpeed() > 5) {
						float distance = gpsHelper.getDistance(waitingZone.getLatitude(), waitingZone.getLongitude());
						LogHelper.d(">> Wait Area : distance -> " + distance + ". range -> " + waitingZone.getWaitRange());
						if (distance > waitingZone.getWaitRange()) {
							LogHelper.d(">> Wait Area : Out of area");
							removeMessages(MSG_AREA_CHECK);
							requestWaitCancel(waitingZone.getWaitingZoneId());
							Popup popup = new Popup
									.Builder(Popup.TYPE_ONE_BTN_NORMAL, Constants.DIALOG_TAG_EXIT_WAITING_ZONE)
									.setContent(getString(R.string.wz_msg_exit_waiting_zone))
									.setBtnLabel(getString(R.string.common_confirm), null)
									.setDismissSecond(3)
									.build();
							PopupActivity.startActivity(ScenarioService.this, popup);
							return;
						}
					}
					sendEmptyMessageDelayed(MSG_AREA_CHECK, 5000);
					break;
				case MSG_REPORT:
					LogHelper.e("poling handler MSG_REPORT");
					ServiceReportPacket sp = (ServiceReportPacket) msg.obj;
					if (reportRetryCount >= 3) {
						if (sp.getReportKind() == Packets.ReportKind.Failed) {
							refreshSavedPassengerInfo(sp.getCallNumber());
						}
						removeMessages(MSG_REPORT);
					} else {
						request(sp);
						reportRetryCount++;

						Message newMsg = obtainMessage();
						newMsg.what = MSG_REPORT;
						newMsg.obj = sp;
						sendMessageDelayed(newMsg, 5000);
					}
					break;
				case MSG_ACK:
					if (ackRetryCount >= 3) {
						removeMessages(MSG_ACK);
					} else {
						AckPacket packet = (AckPacket) msg.obj;
						request(packet);
						ackRetryCount++;

						Message newMsg = obtainMessage();
						newMsg.what = MSG_ACK;
						newMsg.obj = packet;
						sendMessageDelayed(newMsg, 5000);
					}
					break;
				case MSG_SERVICE_ACK:
					LogHelper.d(">> Failed to certify in 10 sec.");
					mServiceResultPacket.postValue(null); //new ServiceRequestResultPacket(null, null)
					break;

				case MSG_DEVICE_WATCH:
					//watchDevice();
					sendEmptyMessageDelayed(MSG_DEVICE_WATCH, 1500);
					break;

				case MSG_REQ_WAIT_AREA_STATE:
					//requestWaitAreaStateInner();
					break;
			}
		}
	};

}
