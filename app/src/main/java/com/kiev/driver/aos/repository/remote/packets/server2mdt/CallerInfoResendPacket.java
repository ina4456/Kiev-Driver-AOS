package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;
import com.kiev.driver.aos.util.LogHelper;

/**
 * 고객정보 재전송 (GT-5A22) 357 Byte
 * Server -> MDT
 */
public class CallerInfoResendPacket extends ResponsePacket {

	private final static int BYTE_LENGTH_FOR_DISABLED_PERSON = 250;
	private final static int BYTE_LENGTH_FOR_DISABLED_PERSON_WITH_DEST = 300;

	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private Packets.OrderKind orderKind; // 배차구분 (1)
	private String callReceiptDate; // 콜접수일자(ex : 2009-01-23) (11)
	private int callNumber; // 콜번호 (2)
	private float longitude; // 고객 경도 (30)
	private float latitude; // 고객 위도 (30)
	private String callerPhone; // 고객 연락처 (30)
	private String place; // 탑승지 (41)
	private String placeExplanation; // 탑승지 설명 (101)
	private int allocBoundary; // 배차범위 (2)
	private int orderCount; // 배차횟수 (1)

	private String callerName; // 고객이름 (11)
	private String errorCode; // 장애코드 (11)
	private String destination; // 목적지 (41)
	private boolean isWheelChair; // 휠체어여부 (1)

	private String destName;// 목적지 장소명 (41)
	private float destLongitude; // 목적지 경도 (30)
	private float destLatitude; // 목적지 위도 (30)
	private int callClass; // 콜 등급 (1)

	private boolean isCallForDisabledPerson; //복지콜 여부

	public CallerInfoResendPacket(byte[] bytes) {
		super(bytes);
	}

	public int getCorporationCode() {
		return corporationCode;
	}

	public int getCarId() {
		return carId;
	}

	public Packets.OrderKind getOrderKind() {
		return orderKind;
	}

	public String getCallReceiptDate() {
		return callReceiptDate;
	}

	public int getCallNumber() {
		return callNumber;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public String getCallerPhone() {
		return callerPhone;
	}

	public String getPlace() {
		return place;
	}

	public String getPlaceExplanation() {
		return placeExplanation;
	}

	public int getAllocBoundary() {
		return allocBoundary;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public String getCallerName() {
		return callerName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getDestination() {
		return destination;
	}

	public boolean isWheelChair() {
		return isWheelChair;
	}

	public String getDestName() {
		return destName;
	}

	public float getDestLongitude() {
		return destLongitude;
	}

	public float getDestLatitude() {
		return destLatitude;
	}

	public int getCallClass() {
		return callClass;
	}

	public boolean isCallForDisabledPerson() {
		return isCallForDisabledPerson;
	}

	@Override
	public void parse(byte[] buffers) {
		super.parse(buffers);
		try {
			corporationCode = readInt(2);
			carId = readInt(2);
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
			}
			callReceiptDate = readString(11);
			callNumber = readInt(2);

			String departLongitudeStr = EncryptUtil.decodeStr(readString(30));
			String departLatitudeStr = EncryptUtil.decodeStr(readString(30));
			longitude = Float.parseFloat(departLongitudeStr.isEmpty() ? "0" : departLongitudeStr);
			latitude = Float.parseFloat(departLatitudeStr.isEmpty() ? "0" : departLatitudeStr);
			callerPhone = EncryptUtil.decodeStr(readString(30));

			place = readString(41);
			placeExplanation = readString(101);
			allocBoundary = readInt(2);
			orderCount = readInt(1);

			destName = readString(41);
			String destLongitudeStr = EncryptUtil.decodeStr(readString(30));
			String destLatitudeStr = EncryptUtil.decodeStr(readString(30));
			destLongitude = Float.parseFloat(destLongitudeStr.isEmpty() ? "0" : destLongitudeStr);
			destLatitude = Float.parseFloat(destLatitudeStr.isEmpty() ? "0" : destLatitudeStr);

			callClass = readInt(1);
		} catch (NumberFormatException e) {
			LogHelper.e(">> parse error");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "고객정보재전송 목적지 추가(0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", orderKind=" + orderKind +
				", callReceiptDate='" + callReceiptDate + '\'' +
				", callNumber=" + callNumber +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", callerPhone='" + callerPhone + '\'' +
				", place='" + place + '\'' +
				", placeExplanation='" + placeExplanation + '\'' +
				", allocBoundary=" + allocBoundary +
				", orderCount=" + orderCount +
				", destName='" + destName + '\'' +
				", destLongitude='" + destLongitude + '\'' +
				", destLatitude='" + destLatitude + '\'' +
				", callClass=" + callClass +
				'}';
	}
}