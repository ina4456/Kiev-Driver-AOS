package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * Created by zic325 on 2016. 9. 8..
 * 메시지 전송용청 응답 (GT-1812) 207 Byte
 * Server -> MDT
 */
public class ResponseSMSPacket extends ResponsePacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
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
        carId = readInt(2);
	    int isSuccess = readInt(1);
	    this.isSuccess = isSuccess == 0x01;
    }

    @Override
    public String toString() {
        return "SMS 응답 (0x" + Integer.toHexString(messageType) + ") " +
		        "corporationCode=" + corporationCode +
		        ", carId=" + carId +
		        ", isSuccess=" + isSuccess +
		        '}';
    }
}