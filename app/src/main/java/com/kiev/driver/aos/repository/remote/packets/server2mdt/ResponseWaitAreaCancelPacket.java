package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 대기취소응답 (GT-5516) 5 Byte
 * Server -> MDT
 */
public class ResponseWaitAreaCancelPacket extends ResponsePacket {

    private int carId; // Car ID (4)
    private Packets.WaitCancelType waitCancelType; // 대기취소처리 구분 (1)

    public ResponseWaitAreaCancelPacket(byte[] bytes) {
        super(bytes);
    }

    public int getCarId() {
        return carId;
    }

    public Packets.WaitCancelType getWaitCancelType() {
        return waitCancelType;
    }

    @Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(4);
        int type = readInt(1);
        if (type == Packets.WaitCancelType.Success.value) {
            waitCancelType = Packets.WaitCancelType.Success;
        } else if (type == Packets.WaitCancelType.Fail.value) {
            waitCancelType = Packets.WaitCancelType.Fail;
        } else if (type == Packets.WaitCancelType.Exist.value) {
            waitCancelType = Packets.WaitCancelType.Exist;
        } else if (type == Packets.WaitCancelType.AlreadyCancel.value) {
            waitCancelType = Packets.WaitCancelType.AlreadyCancel;
        }
    }

    @Override
    public String toString() {
        return "대기취소응답 (0x" + Integer.toHexString(messageType) + ") " +
                "carId=" + carId +
                ", waitCancelType=" + waitCancelType;
    }
}