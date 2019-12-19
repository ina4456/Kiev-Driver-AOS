package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 주기응답 (GT-5212) 8 Byte
 * Server -> MDT
 */
public class ResponsePeriodSendingPacket extends ResponsePacket {

    private int carId; // Car ID (4)
    private boolean hasOrder; // 배차상태 (1)
    private int callNumber; // 콜번호 (2)
    private boolean hasMessage; // 메시지 유무 (1)

    public ResponsePeriodSendingPacket(byte[] bytes) {
        super(bytes);
    }

    public int getCarId() {
        return carId;
    }

    public boolean hasOrder() {
        return hasOrder;
    }

    public int getCallNumber() {
        return callNumber;
    }

    public boolean hasMessage() {
        return hasMessage;
    }

    @Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(4);
        int order = readInt(1);
        hasOrder = order == 0x01;
        callNumber = readInt(2);
        int message = readInt(1);
        hasMessage = message == 0x01;
    }

    @Override
    public String toString() {
        return "주기응답 (0x" + Integer.toHexString(messageType) + ") " +
                "carId=" + carId +
                ", hasOrder=" + hasOrder +
                ", callNumber=" + callNumber +
                ", hasMessage=" + hasMessage;
    }
}