package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * Emergency 요청 (GT-5711) 78 Byte
 * MDT -> Server
 */
public class RequestEmergencyPacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
    private Packets.EmergencyType emergencyType; // Emergency 구분
    private String gpsTime; // GPS시간 (6) (년월일시분초 - ex : 090805112134)
    private int direction; // 주행방향 (2)
    private float longitude; // 경도 (30)
    private float latitude; // 위도 (30)
    private int speed; // 속도 (1)
    private Packets.BoardType taxiState; // 택시상태 (1)

    public RequestEmergencyPacket() {
        super(Packets.REQ_EMERGENCY);
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

    public Packets.EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(Packets.EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Packets.BoardType getTaxiState() {
        return taxiState;
    }

    public void setTaxiState(Packets.BoardType taxiState) {
        this.taxiState = taxiState;
    }

    @Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 4);
        writeInt(emergencyType.value, 1);
        writeDateTime(gpsTime, 6);
        writeInt(direction, 2);
	    writeString(EncryptUtil.encodeStr("" + longitude), 30);
	    writeString(EncryptUtil.encodeStr("" + latitude), 30);
        writeInt(speed, 1);
        writeInt(taxiState.value, 1);
        return buffers;
    }

    @Override
    public String toString() {
        return "Emergency 요청 (0x" + Integer.toHexString(messageType) + ") " +
                "serviceNumber=" + serviceNumber +
                ", corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", emergencyType=" + emergencyType +
                ", gpsTime='" + gpsTime + '\'' +
                ", direction=" + direction +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + speed +
                ", taxiState=" + taxiState;
    }
}
