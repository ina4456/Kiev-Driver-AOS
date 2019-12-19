package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;

/**
 * 휴식/운행재개 (GT-5B11) 7 Byte
 * MDT -> Server
 */
public class RequestRestPacket extends RequestPacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
    private Packets.RestType restType; // 구분 (1)

    public RequestRestPacket() {
        super(Packets.REQ_REST);
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

    public Packets.RestType getRestType() {
        return restType;
    }

    public void setRestType(Packets.RestType restType) {
        this.restType = restType;
    }

    @Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(corporationCode, 2);
        writeInt(carId, 4);
        writeInt(restType.value, 1);
        return buffers;
    }

    @Override
    public String toString() {
        return "휴식/운행재개 (0x" + Integer.toHexString(messageType) + ") " +
                "corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", restType=" + restType;
    }
}
