package com.kiev.driver.aos.repository.remote.packets.mdt2server;


import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;

/**
 * Live패킷 (GT-E1F1) 4 Byte
 * MDT -> Server
 */
public class LivePacket extends RequestPacket {

    private int carId; // Car ID (4)

    public LivePacket() {
        super(Packets.REQ_LIVE);
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    @Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(carId, 4);
        return buffers;
    }

    @Override
    public String toString() {
        return "라이브패킷 (0x" + Integer.toHexString(messageType) + ") " +
                "carId=" + carId;
    }
}