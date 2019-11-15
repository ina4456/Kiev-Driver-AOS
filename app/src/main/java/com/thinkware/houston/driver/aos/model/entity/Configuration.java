package com.thinkware.houston.driver.aos.model.entity;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.util.DeviceUtil;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "config_table")
public class Configuration {

	@PrimaryKey
	private int id;

	//차량 설정
	@ColumnInfo(name = "config_is_corporation")
	private boolean isCorporation;       // 개인/법인 여부
	@ColumnInfo(name = "config_corporation_code")
	private int corporationCode; // 법인 코드
	@ColumnInfo(name = "config_car_id")
	private int carId;            // 차량 id
	@ColumnInfo(name = "config_car_number")
	private String carNumber;            // 차량 번호
	@ColumnInfo(name = "config_driver_phone_number")
	private String driverPhoneNumber;    // 운전자 휴대폰 번호
	@ColumnInfo(name = "config_device_phone_number")
	private String devicePhoneNumber;

	//개인 설정
	@ColumnInfo(name = "config_call_center_number")
	private String callCenterNumber;
	@ColumnInfo(name = "config_use_auto_send_sms_when_got_call")
	private boolean useAutoSendSmsWhenGotCall;
	@ColumnInfo(name = "config_use_speaker_phone")
	private boolean useSpeakerPhone;
	@ColumnInfo(name = "config_use_auto_route_to_passenger")
	private boolean useAutoRouteToPassenger;
	@ColumnInfo(name = "config_use_auto_route_to_destination")
	private boolean useAutoRouteToDestination;
	@ColumnInfo(name = "config_navigation")
	private String navigation;
	@ColumnInfo(name = "config_need_auto_login")
	private boolean needAutoLogin;
	@ColumnInfo(name = "config_use_boarding_alighting_btn")
	private boolean useBoardingAlightingBtn;
	@ColumnInfo(name = "config_use_main_btn")
	private boolean useMainBtn;
	@ColumnInfo(name = "config_use_call_btn")
	private boolean useCallBtn;
	@ColumnInfo(name = "config_font_size_int")
	private int fontSizeInt;

	//서비스 섫정
	@ColumnInfo(name = "config_service_code")
	private String serviceCode;         // 서비스 코드(향지코드)
	@ColumnInfo(name = "config_service_number")
	private int serviceNumber;         // 콜 서비스 넘버
	@ColumnInfo(name = "config_call_server_ip")
	private String callServerIp;         // 콜 서버 IP
	@ColumnInfo(name = "config_call_server_port")
	private int callServerPort;           // 콜 서버 PORT
	@ColumnInfo(name = "config_call_update_server_ip")
	private String updateServerIp; // 업데이트 서버 IP
	@ColumnInfo(name = "config_call_update_server_port")
	private int updateServerPort; // 업데이트 서버 PORT
	@ColumnInfo(name = "config_pst")
	private int pst; // 주기 전송 시간
	@ColumnInfo(name = "config_psd")
	private int psd; // 주기 전송 거리 (
	@ColumnInfo(name = "config_rc")
	private int rc; // 모바일콜 배차 후 승차신호 올라올 때까지 8초 주기로 주기 전송.
	@ColumnInfo(name = "config_rt")
	private int rt; // 재시도 시간 (Live 패킷 전송 주기)
	@ColumnInfo(name = "config_cvt")
	private int cvt; // 콜 방송 표기 시간
	@ColumnInfo(name = "config_ls")
	private boolean ls; // 로그 저장 여부
	@ColumnInfo(name = "config_setting_password")
	private String password; // 환경설정 비밀번호
	@ColumnInfo(name = "config_emergency_period_time")
	private int emergencyPeriodTime; // Emergency 주기 시간

	//not shown SettingActivity
	@ColumnInfo(name = "config_has_confirmed_permission_guide")
	private boolean hasConfirmedPermissionGuide;
	@ColumnInfo(name = "config_has_user_logged_in")
	private boolean hasUserLoggedIn;
	@ColumnInfo(name = "config_floating_btn_last_x")
	private int floatingBtnLastX;
	@ColumnInfo(name = "config_floating_btn_last_y")
	private int floatingBtnLastY;

	//기타
	@ColumnInfo(name = "config_app_version")
	private int appVersion;
	@ColumnInfo(name = "config_config_version")
	private int configurationVersion; // 환경설정 버전
	@ColumnInfo(name = "config_last_notice_version")
	private int lastNoticeVersion; // 공지사항 버전




	public Configuration() {
		id = 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDevicePhoneNumber() {
		return devicePhoneNumber;
	}

	public void setDevicePhoneNumber(String devicePhoneNumber) {
		this.devicePhoneNumber = devicePhoneNumber;
	}

	public String getCallCenterNumber() {
		return callCenterNumber;
	}

	public void setCallCenterNumber(String callCenterNumber) {
		this.callCenterNumber = callCenterNumber;
	}

	public boolean isUseAutoSendSmsWhenGotCall() {
		return useAutoSendSmsWhenGotCall;
	}

	public void setUseAutoSendSmsWhenGotCall(boolean useAutoSendSmsWhenGotCall) {
		this.useAutoSendSmsWhenGotCall = useAutoSendSmsWhenGotCall;
	}

	public boolean isUseSpeakerPhone() {
		return useSpeakerPhone;
	}

	public void setUseSpeakerPhone(boolean useSpeakerPhone) {
		this.useSpeakerPhone = useSpeakerPhone;
	}

	public boolean isUseAutoRouteToPassenger() {
		return useAutoRouteToPassenger;
	}

	public void setUseAutoRouteToPassenger(boolean useAutoRouteToPassenger) {
		this.useAutoRouteToPassenger = useAutoRouteToPassenger;
	}

	public boolean isUseAutoRouteToDestination() {
		return useAutoRouteToDestination;
	}

	public void setUseAutoRouteToDestination(boolean useAutoRouteToDestination) {
		this.useAutoRouteToDestination = useAutoRouteToDestination;
	}

	public String getNavigation() {
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}

	public boolean isNeedAutoLogin() {
		return needAutoLogin;
	}

	public void setNeedAutoLogin(boolean needAutoLogin) {
		this.needAutoLogin = needAutoLogin;
	}

	public boolean isUseBoardingAlightingBtn() {
		return useBoardingAlightingBtn;
	}

	public void setUseBoardingAlightingBtn(boolean useBoardingAlightingBtn) {
		this.useBoardingAlightingBtn = useBoardingAlightingBtn;
	}

	public boolean isUseMainBtn() {
		return useMainBtn;
	}

	public void setUseMainBtn(boolean useMainBtn) {
		this.useMainBtn = useMainBtn;
	}

	public boolean isUseCallBtn() {
		return useCallBtn;
	}

	public void setUseCallBtn(boolean useCallBtn) {
		this.useCallBtn = useCallBtn;
	}


	public int getFontSizeInt() {
		return fontSizeInt;
	}

	public int getFontSizeFromSetting(float viewFontSize) {
		viewFontSize = DeviceUtil.convertPxToDp((int)viewFontSize);
		if (fontSizeInt == Constants.FONT_SIZE_SMALL) {
			viewFontSize = viewFontSize - Constants.FONT_SIZE_INC_DEC_VALUE;
		} else if (fontSizeInt == Constants.FONT_SIZE_LARGE) {
			viewFontSize = viewFontSize + Constants.FONT_SIZE_INC_DEC_VALUE;
		}
		return (int)viewFontSize;
	}

	public void setFontSizeInt(int fontSizeInt) {
		this.fontSizeInt = fontSizeInt;
	}

	public boolean isHasConfirmedPermissionGuide() {
		return hasConfirmedPermissionGuide;
	}

	public void setHasConfirmedPermissionGuide(boolean hasConfirmedPermissionGuide) {
		this.hasConfirmedPermissionGuide = hasConfirmedPermissionGuide;
	}

	public boolean isHasUserLoggedIn() {
		return hasUserLoggedIn;
	}

	public void setHasUserLoggedIn(boolean hasUserLoggedIn) {
		this.hasUserLoggedIn = hasUserLoggedIn;
	}

	public int getFloatingBtnLastX() {
		return floatingBtnLastX;
	}

	public void setFloatingBtnLastX(int floatingBtnLastX) {
		this.floatingBtnLastX = floatingBtnLastX;
	}

	public int getFloatingBtnLastY() {
		return floatingBtnLastY;
	}

	public void setFloatingBtnLastY(int floatingBtnLastY) {
		this.floatingBtnLastY = floatingBtnLastY;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public int getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	public String getCallServerIp() {
		return callServerIp;
	}

	public void setCallServerIp(String callServerIp) {
		this.callServerIp = callServerIp;
	}

	public int getCallServerPort() {
		return callServerPort;
	}

	public void setCallServerPort(int callServerPort) {
		this.callServerPort = callServerPort;
	}

	public String getUpdateServerIp() {
		return updateServerIp;
	}

	public void setUpdateServerIp(String updateServerIp) {
		this.updateServerIp = updateServerIp;
	}

	public int getUpdateServerPort() {
		return updateServerPort;
	}

	public void setUpdateServerPort(int updateServerPort) {
		this.updateServerPort = updateServerPort;
	}

	public int getPst() {
		return pst;
	}

	public void setPst(int pst) {
		this.pst = pst;
	}

	public int getPsd() {
		return psd;
	}

	public void setPsd(int psd) {
		this.psd = psd;
	}

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public int getRt() {
		return rt;
	}

	public void setRt(int rt) {
		this.rt = rt;
	}

	public int getCvt() {
		return cvt;
	}

	public void setCvt(int cvt) {
		this.cvt = cvt;
	}

	public boolean isLs() {
		return ls;
	}

	public void setLs(boolean ls) {
		this.ls = ls;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getEmergencyPeriodTime() {
		return emergencyPeriodTime;
	}

	public void setEmergencyPeriodTime(int emergencyPeriodTime) {
		this.emergencyPeriodTime = emergencyPeriodTime;
	}

	public boolean isCorporation() {
		return isCorporation;
	}

	public void setCorporation(boolean corporation) {
		isCorporation = corporation;
	}

	public int getCorporationCode() {
		return corporationCode;
	}

	public void setCorporationCode(int corporationCode) {
		this.corporationCode = corporationCode;
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getDriverPhoneNumber() {
		return driverPhoneNumber;
	}

	public void setDriverPhoneNumber(String driverPhoneNumber) {
		this.driverPhoneNumber = driverPhoneNumber;
	}

	public int getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

	public int getConfigurationVersion() {
		return configurationVersion;
	}

	public void setConfigurationVersion(int configurationVersion) {
		this.configurationVersion = configurationVersion;
	}

	public int getLastNoticeVersion() {
		return lastNoticeVersion;
	}

	public void setLastNoticeVersion(int lastNoticeVersion) {
		this.lastNoticeVersion = lastNoticeVersion;
	}

	@Override
	public String toString() {
		return "Configuration{" +
				"id=" + id +
				", isCorporation=" + isCorporation +
				", corporationCode=" + corporationCode +
				", carId=" + carId +
				", carNumber='" + carNumber + '\'' +
				", driverPhoneNumber='" + driverPhoneNumber + '\'' +
				", devicePhoneNumber='" + devicePhoneNumber + '\'' +
				", callCenterNumber='" + callCenterNumber + '\'' +
				", useAutoSendSmsWhenGotCall=" + useAutoSendSmsWhenGotCall +
				", useSpeakerPhone=" + useSpeakerPhone +
				", useAutoRouteToPassenger=" + useAutoRouteToPassenger +
				", useAutoRouteToDestination=" + useAutoRouteToDestination +
				", navigation='" + navigation + '\'' +
				", needAutoLogin=" + needAutoLogin +
				", useBoardingAlightingBtn=" + useBoardingAlightingBtn +
				", useMainBtn=" + useMainBtn +
				", useCallBtn=" + useCallBtn +
				", fontSizeInt=" + fontSizeInt +
				", serviceCode='" + serviceCode + '\'' +
				", serviceNumber=" + serviceNumber +
				", callServerIp='" + callServerIp + '\'' +
				", callServerPort=" + callServerPort +
				", updateServerIp='" + updateServerIp + '\'' +
				", updateServerPort=" + updateServerPort +
				", pst=" + pst +
				", psd=" + psd +
				", rc=" + rc +
				", rt=" + rt +
				", cvt=" + cvt +
				", ls=" + ls +
				", password='" + password + '\'' +
				", emergencyPeriodTime=" + emergencyPeriodTime +
				", hasConfirmedPermissionGuide=" + hasConfirmedPermissionGuide +
				", hasUserLoggedIn=" + hasUserLoggedIn +
				", floatingBtnLastX=" + floatingBtnLastX +
				", floatingBtnLastY=" + floatingBtnLastY +
				", appVersion=" + appVersion +
				", configurationVersion=" + configurationVersion +
				", lastNoticeVersion=" + lastNoticeVersion +
				'}';
	}
}
