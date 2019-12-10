package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 내정보 요청 응답 (GT-5E12) 94 Byte
 * Server -> MDT
 */
public class ResponseMyInfoPacket extends ResponsePacket {

	private int carId; // Car ID (2)
	private String corporationName; // 법인회사 이름(20)
	private String carPlateNumber; // 차량번호 (20)
	private String driverName; // 운전자 이름 (20)
	private String carColor; // 차량색상 (10)
	private String carType; // 차량종류 (20)


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

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	@Override
	public void parse(byte[] buffers) {
		super.parse(buffers);
		carId = readInt(2);
		corporationName = readString(20);
		carPlateNumber = readString(20);
		driverName = readString(20);
		carColor = readString(10);
		carType = readString(20);
	}

	@Override
	public String toString() {
		return "내정보 요청 응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
				", corporationName='" + corporationName + '\'' +
				", carPlateNumber='" + carPlateNumber + '\'' +
				", driverName='" + driverName + '\'' +
				", carColor='" + carColor + '\'' +
				", carType='" + carType + '\'' +
				'}';
	}
}

