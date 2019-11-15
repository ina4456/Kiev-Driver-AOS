package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * Created by zic325 on 2016. 9. 8..
 * 메시지 전송용청 응답 (GT-1812) 207 Byte
 * Server -> MDT
 */
public class ResponseMyInfoPacket extends ResponsePacket {

    private int carId; // Car ID (2)
	private String corporationName; // 법인회사 이름(20)
	private String carPlateNumber; // 차량번호 (20)
	private String driverName; // 운전자 이름 (20)


	public ResponseMyInfoPacket(byte[] bytes) {
        super(bytes);
    }

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	public String getCarPlateNumber() {
		return carPlateNumber;
	}

	public void setCarPlateNumber(String carPlateNumber) {
		this.carPlateNumber = carPlateNumber;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(2);
	    corporationName = readString(20);
	    carPlateNumber = readString(20);
	    driverName = readString(20);
    }

    @Override
    public String toString() {
        return "내정보 요청 응답 (0x" + Integer.toHexString(messageType) + ") " +
		        "carId=" + carId +
		        ", corporationName='" + corporationName + '\'' +
		        ", carPlateNumber='" + carPlateNumber + '\'' +
		        ", driverName='" + driverName + '\'' +
		        '}';
    }

}