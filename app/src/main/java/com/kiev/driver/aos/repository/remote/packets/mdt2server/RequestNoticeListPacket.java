package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;

/**
 * 공지사항 리스트 요청 (GT-5137) 6 Byte
 * MDT -> Server
 */
public class RequestNoticeListPacket extends RequestPacket {

    private int targetServer; // 서버 구분 (1)
    private int carId; // Car ID (2)
	private int listType; //리스트 구분 (1)

    public RequestNoticeListPacket() {
        super(Packets.REQ_NOTICE_LIST);
    }

    public int getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(int targetServer) {
        this.targetServer = targetServer;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	@Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(targetServer, 1);
        writeInt(carId, 2);
        writeInt(listType, 1);
        return buffers;
    }

    @Override
    public String toString() {
        return "공지사항 리스트 요청 (0x" + Integer.toHexString(messageType) + ") " +
                "targetServer=" + targetServer +
                ", carId=" + carId +
                ", corporationCode=" + listType;
    }
}
