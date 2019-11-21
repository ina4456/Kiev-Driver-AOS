package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 대기콜 배차정보 전송 (GT-1914) 353 Byte
 * Server -> MDT
 */
public class ResponseWaitCallOrderInfoPacket extends ResponsePacket {


	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private boolean isSuccess;  //성공 여부(1)
	private String callReceiptDate; // 콜접수일자(ex : 2009-01-23) (11)
	private int callNumber; // 콜번호 (2)
	private float longitude; // 고객 경도 (30)
	private float latitude; // 고객 위도 (30)
	private String callerPhone; // 고객 연락처 (30)
	private String place; // 탑승지 (41)
	private String placeExplanation; // 탑승지 설명 (101)
	private String destName;// 목적지 장소명 (41)
	private float destLongitude; // 목적지 경도 (4)
	private float destLatitude; // 목적지 위도 (4)

	public ResponseWaitCallOrderInfoPacket(byte[] bytes) {
		super(bytes);
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

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public String getCallReceiptDate() {
		return callReceiptDate;
	}

	public void setCallReceiptDate(String callReceiptDate) {
		this.callReceiptDate = callReceiptDate;
	}

	public int getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(int callNumber) {
		this.callNumber = callNumber;
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

	public String getCallerPhone() {
		return callerPhone;
	}

	public void setCallerPhone(String callerPhone) {
		this.callerPhone = callerPhone;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPlaceExplanation() {
		return placeExplanation;
	}

	public void setPlaceExplanation(String placeExplanation) {
		this.placeExplanation = placeExplanation;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public float getDestLongitude() {
		return destLongitude;
	}

	public void setDestLongitude(float destLongitude) {
		this.destLongitude = destLongitude;
	}

	public float getDestLatitude() {
		return destLatitude;
	}

	public void setDestLatitude(float destLatitude) {
		this.destLatitude = destLatitude;
	}

	@Override
	public void parse(byte[] buffers) {
		super.parse(buffers);
		corporationCode = readInt(2);
		carId = readInt(2);
		int sucess = readInt(1);
		isSuccess = sucess == 0x01;
		callReceiptDate = readString(11);
		callNumber = readInt(2);
		longitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));
		latitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));
		callerPhone = EncryptUtil.decodeStr("" + readString(30));

//		longitude = readFloat(30);
//		latitude = readFloat(30);
//		callerPhone = readString(30);

		place = readString(41);
		placeExplanation = readString(101);
		destName = readString(41);

		destLongitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));
		destLatitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));
//		destLongitude = readFloat(30);
//		destLatitude = readFloat(30);

	}

	@Override
	public String toString() {
			return "대기콜 배차요청 응답 (0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", isSuccess=" + isSuccess +
				", callReceiptDate='" + callReceiptDate + '\'' +
				", callNumber=" + callNumber +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", callerPhone='" + callerPhone + '\'' +
				", place='" + place + '\'' +
				", placeExplanation='" + placeExplanation + '\'' +
				", destName='" + destName + '\'' +
				", destLongitude=" + destLongitude +
				", destLatitude=" + destLatitude +
				'}';
	}
}