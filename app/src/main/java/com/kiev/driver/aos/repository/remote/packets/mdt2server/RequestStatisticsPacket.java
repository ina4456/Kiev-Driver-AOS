package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 콜 정산 통계 요청 (GT-1613) 67 Byte
 * MDT -> Server
 */
public class RequestStatisticsPacket extends RequestPacket {

	private int serviceNumber; // 서비스번호 (1)
	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private String phoneNumber; // Phone Number (30)
	private String memo; // Memo (30)


	public RequestStatisticsPacket() {
		super(Packets.REQ_STATISTICS);

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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public byte[] toBytes() {
		super.toBytes();
		writeInt(serviceNumber, 1);
		writeInt(corporationCode, 2);
		writeInt(carId, 2);
		writeString(EncryptUtil.encodeStr(phoneNumber), 30);
		writeString(memo, 30);
		return buffers;
	}

	@Override
	public String toString() {
		return "콜 정산 통계 요청 (0x" + Integer.toHexString(messageType) + ") " +
				"serviceNumber=" + serviceNumber +
			", corporationCode=" + corporationCode +
			", carId=" + carId +
			", phoneNumber='" + phoneNumber + '\'' +
			", memo='" + memo + '\'' +
			'}';
	}

}
