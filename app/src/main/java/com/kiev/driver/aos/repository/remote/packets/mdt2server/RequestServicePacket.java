package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 서비스 요청 (GT-5111) 70 Byte
 * MDT -> Server
 */
public class RequestServicePacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
    private String phoneNumber; // Phone Number (30)
    private Packets.CorporationType corporationType; // 개인법인체크 (1)
    private int programVersion; // 프로그램 버전 (2)
    private String modemNumber; // 모뎀 번호 (30)

    public RequestServicePacket() {
	    super(Packets.REQ_SERVICE);
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

    public Packets.CorporationType getCorporationType() {
        return corporationType;
    }

    public void setCorporationType(Packets.CorporationType corporationType) {
        this.corporationType = corporationType;
    }

    public int getProgramVersion() {
        return programVersion;
    }

    public void setProgramVersion(int programVersion) {
        this.programVersion = programVersion;
    }

    public String getModemNumber() {
        return modemNumber;
    }

    public void setModemNumber(String modemNumber) {
        this.modemNumber = modemNumber;
    }

    @Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 4);
	    writeString(EncryptUtil.encodeStr(phoneNumber), 30);
        writeInt(corporationType.value, 1);
        writeInt(programVersion, 2);
        writeString(EncryptUtil.encodeStr(modemNumber), 30);
        return buffers;
    }

    @Override
    public String toString() {
        return "서비스요청 (0x" + Integer.toHexString(messageType) + ") " +
                "serviceNumber=" + serviceNumber +
                ", corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", corporationType=" + corporationType +
                ", programVersion=" + programVersion +
                ", modemNumber='" + modemNumber + '\'';
    }
}
