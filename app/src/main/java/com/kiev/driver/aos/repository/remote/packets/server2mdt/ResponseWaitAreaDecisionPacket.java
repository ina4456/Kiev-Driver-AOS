package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;
import com.kiev.driver.aos.util.LogHelper;

/**
 * Created by zic325 on 2016. 9. 8..
 * 대기결정응답 (GT-1514) 19 Byte
 * Server -> MDT
 */
public class ResponseWaitAreaDecisionPacket extends ResponsePacket {

	private int carId; // Car ID (2)
	private Packets.WaitProcType waitProcType; // 대기처리 구분 (1)
	private String waitPlaceCode; // 대기지역 코드 (4)
	private float longitude; // 대기지역 경도 (30)
	private float latitude; // 대기지역 위도 (30)
	private int waitRange; // 대기범위 (2)
	private int waitOrder; // 대기순위 (2)

	public ResponseWaitAreaDecisionPacket(byte[] bytes) {
		super(bytes);
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public Packets.WaitProcType getWaitProcType() {
		return waitProcType;
	}

	public void setWaitProcType(Packets.WaitProcType waitProcType) {
		this.waitProcType = waitProcType;
	}

	public String getWaitPlaceCode() {
		return waitPlaceCode;
	}

	public void setWaitPlaceCode(String waitPlaceCode) {
		this.waitPlaceCode = waitPlaceCode;
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

	public int getWaitRange() {
		return waitRange;
	}

	public void setWaitRange(int waitRange) {
		this.waitRange = waitRange;
	}

	public int getWaitOrder() {
		return waitOrder;
	}

	public void setWaitOrder(int waitOrder) {
		this.waitOrder = waitOrder;
	}

	@Override
	public void parse(byte[] buffers) {
		super.parse(buffers);
		try {
			carId = readInt(2);
			int type = readInt(1);
			if (type == Packets.WaitProcType.Success.value) {
				waitProcType = Packets.WaitProcType.Success;
			} else if (type == Packets.WaitProcType.Fail.value) {
				waitProcType = Packets.WaitProcType.Fail;
			} else if (type == Packets.WaitProcType.Exist.value) {
				waitProcType = Packets.WaitProcType.Exist;
			}
			waitPlaceCode = readString(4);
			String longitudeStr = EncryptUtil.decodeStr(readString(30));
			String latitudeStr = EncryptUtil.decodeStr(readString(30));
			longitude = Float.parseFloat(longitudeStr.isEmpty() ? "0" : longitudeStr);
			latitude = Float.parseFloat(latitudeStr.isEmpty() ? "0" : latitudeStr);
			waitRange = readInt(2);
			waitOrder = readInt(2);

		} catch (NumberFormatException e) {
			LogHelper.e(">> parse error");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "대기결정응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
				", waitProcType=" + waitProcType +
				", waitPlaceCode='" + waitPlaceCode + '\'' +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", waitOrder=" + waitOrder +
				", waitRange=" + waitRange;
	}
}