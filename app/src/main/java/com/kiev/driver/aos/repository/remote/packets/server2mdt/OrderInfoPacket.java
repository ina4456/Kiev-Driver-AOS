package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;
import com.kiev.driver.aos.util.LogHelper;

/**
 * 배차데이터 (GT-5311) 357 Byte
 * Server -> MDT
 */
public class OrderInfoPacket extends ResponsePacket {

	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private Packets.OrderKind orderKind; // 배차구분 (1)
	private String callReceiptDate; // 콜접수일자(ex : 2009-01-23) (11)
	private int callNumber; // 콜번호 (2)
	private float longitude; // 고객 경도 (30)
	private float latitude; // 고객 위도 (30)
	private String callerPhone; // 고객 연락처 (13)
	private String place; // 탑승지 (41)
	private String placeExplanation; // 탑승지 설명 (101)
	private int allocBoundary; // 배차범위 (2)
	private int orderCount; // 배차횟수 (1)

	private String destination; // 목적지 (41)
	private float destLongitude; // 목적지 경도 (30)
	private float destLatitude; // 목적지 위도 (30)
	private int callClass; // 콜 등급 (1)

	private boolean isReported; // 운행보고 여부 (Local에서만 사용하는 값이다.)

	public OrderInfoPacket(byte[] bytes) {
		super(bytes);
	}

	public OrderInfoPacket(CallerInfoResendPacket p) {
		super(new byte[p.getMessageType()]);
		LogHelper.e("packet msg type : " + p.getMessageType());

		corporationCode = p.getCorporationCode();
		carId = p.getCarId();
		orderKind = p.getOrderKind();
		callReceiptDate = p.getCallReceiptDate();
		callNumber = p.getCallNumber();
		longitude = p.getLongitude();
		latitude = p.getLatitude();
		callerPhone = p.getCallerPhone();
		place = p.getPlace();
		placeExplanation = p.getPlaceExplanation();
		allocBoundary = p.getAllocBoundary();
		orderCount = p.getOrderCount();

		destination = p.getDestination();
		destLongitude = p.getDestLongitude();
		destLatitude = p.getDestLatitude();
		callClass = p.getCallClass();
	}

	public OrderInfoPacket(ResponseWaitCallOrderInfoPacket p) {
		super(new byte[p.getMessageType()]);
		LogHelper.e("packet msg type : " + p.getMessageType());

		corporationCode = p.getCorporationCode();
		carId = p.getCarId();
		orderKind = p.getOrderKind();
		callReceiptDate = p.getCallReceiptDate();
		callNumber = p.getCallNumber();
		longitude = p.getLongitude();
		latitude = p.getLatitude();
		callerPhone = p.getCallerPhone();
		place = p.getPlace();
		placeExplanation = p.getPlaceExplanation();
		//orderCount = 0;
		//allocBoundary = 0;

		destination = p.getDestName();
		destLatitude = p.getDestLatitude();
		destLongitude = p.getDestLongitude();
	}


	public OrderInfoPacket(ResponseWaitAreaOrderInfoPacket p) {
		super(new byte[p.getMessageType()]);
		LogHelper.e("packet msg type : " + p.getMessageType());

		//corporationCode = p.getCorporationCode();
		carId = p.getCarId();
		orderKind = p.getOrderKind();
		callReceiptDate = p.getCallReceiptDate();
		callNumber = p.getCallNumber();
		longitude = p.getLongitude();
		latitude = p.getLatitude();
		callerPhone = p.getCallerPhone();
		place = p.getPlace();
		placeExplanation = p.getPlaceExplanation();
		orderCount = p.getOrderCount();
		allocBoundary = 0;

		destination = p.getDestination();
		destLatitude = p.getDestinationLatitude();
		destLongitude = p.getDestinationLongitude();
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

	public String getDestination() {
		return destination;
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

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean reported) {
		isReported = reported;
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

			destination = readString(41);

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
		return "배차데이터 목적지 추가 (0x" + Integer.toHexString(messageType) + ") " +
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
				", destName='" + destination + '\'' +
				", destLongitude='" + destLongitude + '\'' +
				", destLatitude='" + destLatitude + '\'' +
				", callClass=" + callClass +
				", isReported=" + isReported +
				'}';
	}
}