package com.thinkware.houston.driver.aos.repository.remote.packets.mdt2server;

import com.thinkware.houston.driver.aos.repository.remote.packets.Packets;
import com.thinkware.houston.driver.aos.repository.remote.packets.RequestPacket;
import com.thinkware.houston.driver.aos.util.EncryptUtil;

/**
 * Created by zic325 on 2016. 9. 8..
 * 주기전송 (GT-1211) 32 Byte
 * MDT -> Server
 */
public class PeriodSendingPacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
    private String sendingTime; // 전송시간 (6) (년월일시분초 - ex : 090805112134)
    private String gpsTime; // GPS시간 (6) (년월일시분초 - ex : 090805112134)
    private int direction; // 주행방향 (2)
    private float longitude; // 경도 (4)
    private float latitude; // 위도 (4)
    private int speed; // 속도 (1)
    private Packets.BoardType boardState; // 승차상태 (1)
    private Packets.RestType restState; // 휴식상태 (1)

    public PeriodSendingPacket() {
//        super(Packets.PERIOD_SENDING);
	    super(Packets.DRIVER_APP_PERIOD_SENDING);

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

    public String getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
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

	// FIXME: 2019-09-05 개발 서버 에러로 스피드 71이하로 보내야 정상적인 리턴 확인 가능, 서버 수정 필요
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Packets.BoardType getBoardState() {
        return boardState;
    }

    public void setBoardState(Packets.BoardType boardState) {
        this.boardState = boardState;
    }

    public Packets.RestType getRestState() {
        return restState;
    }

    public void setRestState(Packets.RestType restState) {
        this.restState = restState;
    }

    @Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 2);
        writeDateTime(sendingTime, 6);
        writeDateTime(gpsTime, 6);
        writeInt(direction, 2);
//        writeFloat(longitude, 4);
//        writeFloat(latitude, 4);
	    writeString(EncryptUtil.encodeStr("" + longitude), 30);
	    writeString(EncryptUtil.encodeStr("" + latitude), 30);
        writeInt(speed, 1);
        writeInt(boardState.value, 1);
        writeInt(restState.value, 1);
        return buffers;
    }

    @Override
    public String toString() {
        return "주기전송 (0x" + Integer.toHexString(messageType) + ") " +
                "serviceNumber=" + serviceNumber +
                ", corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", sendingTime='" + sendingTime + '\'' +
                ", gpsTime='" + gpsTime + '\'' +
                ", direction=" + direction +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + speed +
                ", boardState=" + boardState +
                ", restState=" + restState;
    }
}
