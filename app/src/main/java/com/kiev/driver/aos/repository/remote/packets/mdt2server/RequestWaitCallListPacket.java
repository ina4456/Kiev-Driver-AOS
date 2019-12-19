package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 대기콜 리스트 (GT-5D11) 69 Byte
 * MDT -> Server
 */
public class RequestWaitCallListPacket extends RequestPacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
	private Packets.WaitCallListType waitCallListType; // 대기콜리스트타입 (1)
	private int startIndex; // 시작인덱스 (1)
	private int requestCount; // 요청개수 (1)
    private float longitude; // 경도 (30)
    private float latitude; // 위도 (30)

    public RequestWaitCallListPacket() {
        super(Packets.REQ_WAIT_CALL_LIST);
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

	public Packets.WaitCallListType getWaitCallListType() {
		return waitCallListType;
	}

	public void setWaitCallListType(Packets.WaitCallListType waitCallListType) {
		this.waitCallListType = waitCallListType;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
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

	@Override
    public byte[] toBytes() {
        super.toBytes();

        writeInt(corporationCode, 2);
        writeInt(carId, 4);
		writeInt(waitCallListType.value, 1);
		writeInt(startIndex, 1);
		writeInt(requestCount, 1);
		writeString(EncryptUtil.encodeStr(""+ longitude), 30);
		writeString(EncryptUtil.encodeStr(""+ latitude), 30);

        return buffers;
    }

    @Override
    public String toString() {
        return "대기콜 리스트 요청 (0x" + Integer.toHexString(messageType) + ") " +
		        "corporationCode=" + corporationCode +
		        ", carId=" + carId +
		        ", waitCallListType=" + waitCallListType +
		        ", startIndex=" + startIndex +
		        ", requestCount=" + requestCount +
		        ", longitude=" + longitude +
		        ", latitude=" + latitude +
		        '}';
    }
}

