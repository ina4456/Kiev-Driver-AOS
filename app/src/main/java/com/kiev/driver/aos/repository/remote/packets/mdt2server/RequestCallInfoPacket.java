package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.ByteUtil;
import com.kiev.driver.aos.util.LogHelper;

/**
 * 배차정보 요청 (GT-5A21) 19 Byte
 * MDT -> Server
 */
public class RequestCallInfoPacket extends RequestPacket {

	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (2)
	private String callReceiptDate; // 콜접수일자(ex : 2009-01-23) (11)
	private int callNumber; // 콜번호 (2)

	public RequestCallInfoPacket() {
		super(Packets.REQ_ORDER_INFO);
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

	@Override
	public byte[] toBytes() {
		super.toBytes();
		writeInt(corporationCode, 2);
		writeInt(carId, 2);
		writeString(callReceiptDate, 11);
		writeInt(callNumber, 2);
		LogHelper.e("buffer : " + ByteUtil.toHexString(buffers));
		return buffers;
	}

	@Override
	public String toString() {
		return "배차정보 요청 (0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", callReceiptDate='" + callReceiptDate + '\'' +
				", callNumber=" + callNumber;
	}
}
