package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 대기결정 (GT-5523) 107 Byte
 * MDT -> Server
 */
public class RequestWaitAreaDecisionPacket extends RequestPacket {
	private int serviceNumber; // 서비스번호 (1)
	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private String phoneNumber; //기사 연락처 (30)
	private String gpsTime; // GPS시간 (6) (년월일시분초 - ex : 090805112134)
	private float longitude; // 경도 (30)
	private float latitude; // 위도 (30)
	private String waitAreaId; //대기배차결정 지역코드 (4)

	public RequestWaitAreaDecisionPacket() {
		super(Packets.REQ_WAIT_AREA_DECISION);
	}

	public int getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getWaitAreaId() {
		return waitAreaId;
	}

	public void setWaitAreaId(String waitAreaId) {
		this.waitAreaId = waitAreaId;
	}

	@Override
	public byte[] toBytes() {
		super.toBytes();
		writeInt(serviceNumber, 1);
		writeInt(corporationCode, 2);
		writeInt(carId, 2);
		writeString(EncryptUtil.encodeStr("" + phoneNumber), 30);
		writeDateTime(gpsTime, 6);
		writeString(EncryptUtil.encodeStr("" + longitude), 30);
		writeString(EncryptUtil.encodeStr("" + latitude), 30);
		writeString(waitAreaId, 4);

		return buffers;
	}


	@Override
	public String toString() {
		return "대기지역 대기 결정 요청 (0x" + Integer.toHexString(messageType) + ") " +
				"serviceNumber=" + serviceNumber +
				", corporationCode=" + corporationCode +
				", carId=" + carId +
				", phoneNumber='" + phoneNumber + '\'' +
				", gpsTime='" + gpsTime + '\'' +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", waitAreaId=" + waitAreaId +
				'}';
	}
}
