package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 내정보 요청 (GT-5E11) 36 Byte
 * MDT -> Server
 */
public class RequestMyInfoPacket extends RequestPacket {

	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (4)
	private String phoneNumber; // Phone Number (30)

	public RequestMyInfoPacket() {
		super(Packets.REQ_MY_INFO);
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

	@Override
	public byte[] toBytes() {
		super.toBytes();
		writeInt(corporationCode, 2);
		writeInt(carId, 4);
		writeString(EncryptUtil.encodeStr(phoneNumber), 30);
		return buffers;
	}

	@Override
	public String toString() {
		return "내정보요청 (0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", phoneNumber='" + phoneNumber + '\'' +
				'}';
	}

}
