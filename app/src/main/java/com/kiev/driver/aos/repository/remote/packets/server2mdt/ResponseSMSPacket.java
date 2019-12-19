package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * SMS 전송 요청 응답 (GT-5814) 7 Byte
 * Server -> MDT
 */
public class ResponseSMSPacket extends ResponsePacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
	private boolean isSuccess; // 성공여부 (1)


	public ResponseSMSPacket(byte[] bytes) {
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

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        corporationCode = readInt(2);
        carId = readInt(4);
	    int isSuccess = readInt(1);
	    this.isSuccess = isSuccess == 0x01;
    }

    @Override
    public String toString() {
        return "SMS 전송 요청 응답 (0x" + Integer.toHexString(messageType) + ") " +
		        "corporationCode=" + corporationCode +
		        ", carId=" + carId +
		        ", isSuccess=" + isSuccess +
		        '}';
    }
}