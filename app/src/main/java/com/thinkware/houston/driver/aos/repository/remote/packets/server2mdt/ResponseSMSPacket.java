package com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt;

import com.thinkware.houston.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * Created by zic325 on 2016. 9. 8..
 * 메시지 전송용청 응답 (GT-1812) 207 Byte
 * Server -> MDT
 */
public class ResponseSMSPacket extends ResponsePacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
	private String phoneNumber; // 운전자 전화번호 (13)
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
	    phoneNumber = readString(13);
	    int isSuccess = readInt(1);
	    this.isSuccess = isSuccess == 0x02;
    }

    @Override
    public String toString() {
        return "SMS 응답 (0x" + Integer.toHexString(messageType) + ") " +
		        "corporationCode=" + corporationCode +
		        ", carId=" + carId +
		        ", phoneNumber='" + phoneNumber + '\'' +
		        ", isSuccess=" + isSuccess +
		        '}';
    }
}