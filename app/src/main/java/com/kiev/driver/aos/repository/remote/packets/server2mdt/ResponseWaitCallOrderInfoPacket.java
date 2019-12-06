package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;
import com.kiev.driver.aos.util.LogHelper;

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
	private Packets.OrderKind orderKind; // 배차 구분 (1)

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

	public Packets.OrderKind getOrderKind() {
		return orderKind;
	}

	public void setOrderKind(Packets.OrderKind orderKind) {
		this.orderKind = orderKind;
	}

	@Override
	public void parse(byte[] buffers) {
		super.parse(buffers);

		try {
			corporationCode = readInt(2);
			carId = readInt(2);
			int success = readInt(1);
			isSuccess = success == 0x01;
			callReceiptDate = readString(11);
			callNumber = readInt(2);
//			String longitudeStr = readString(30);
//			String latitudeStr = readString(30);
//			if (longitudeStr != null && latitudeStr != null && !longitudeStr.isEmpty() && !latitudeStr.isEmpty()) {
//				longitude = Float.parseFloat(EncryptUtil.decodeStr(longitudeStr));
//				latitude = Float.parseFloat(EncryptUtil.decodeStr(latitudeStr));
//			}
			longitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));
			latitude = Float.parseFloat(EncryptUtil.decodeStr("" + readString(30)));

			callerPhone = EncryptUtil.decodeStr("" + readString(30));

			place = readString(41);
			placeExplanation = readString(101);
			destName = readString(41);

			String destLongitudeStr = EncryptUtil.decodeStr(readString(30));
			String destLatitudeStr = EncryptUtil.decodeStr(readString(30));
			destLongitude = Float.parseFloat(destLongitudeStr.isEmpty() ? "0" : destLongitudeStr);
			destLatitude = Float.parseFloat(destLatitudeStr.isEmpty() ? "0" : destLatitudeStr);
			
			int order = readInt(1);
			if (Packets.OrderKind.Normal.value == order) {
				orderKind = Packets.OrderKind.Normal;
			} else if (Packets.OrderKind.Wait.value == order) {
				orderKind = Packets.OrderKind.Wait;
			} else if (Packets.OrderKind.Forced.value == order) {
				orderKind = Packets.OrderKind.Forced;
			} else if (Packets.OrderKind.Manual.value == order) {
				orderKind = Packets.OrderKind.Manual;
			} else if (Packets.OrderKind.WaitOrder.value == order) {
				orderKind = Packets.OrderKind.WaitOrder;
			} else if (Packets.OrderKind.WaitOrderTwoWay.value == order) {
				orderKind = Packets.OrderKind.WaitOrderTwoWay;
			} else if (Packets.OrderKind.GetOnOrder.value == order) {
				orderKind = Packets.OrderKind.GetOnOrder;
			} else if (Packets.OrderKind.Mobile.value == order) {
				orderKind = Packets.OrderKind.Mobile;

				//2018. 01. 03 - 권석범
				//통합배차가 내려왔을 경우 정의가 되어 있지 않아 앱이 중지가 되는 증상이 있어 추가하고
				//그 외 없는 배차가 내려올시에 일반 배차로 지정함 (김용태 팀장 요청)
			} else if (Packets.OrderKind.Integration.value == order) {
				orderKind = Packets.OrderKind.Integration;

			} else if (Packets.OrderKind.WaitCall.value == order) {
				orderKind = Packets.OrderKind.WaitCall;
			} else {
				orderKind = Packets.OrderKind.Normal;
			}

		} catch (NumberFormatException e) {
			LogHelper.e(">> parse error");
			e.printStackTrace();
		}

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
				", orderKind=" + orderKind +
				'}';
	}
}