package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * Created by zic325 on 2016. 9. 8..
 * 대기지역요청 (GT-1511) 22 Byte
 * MDT -> Server
 */
public class RequestWaitAreaNewPacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
	private Packets.WaitAreaRequestType requestType; //대기리스트 요청 구분(1)
    private float longitude; // 경도 (30)
    private float latitude; // 위도 (30)
	private int startIndex; //시작 인덱스
	private int requestCount; //요청 개수

    public RequestWaitAreaNewPacket() {
        super(Packets.REQUEST_WAIT_AREA_NEW);
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

	public Packets.WaitAreaRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(Packets.WaitAreaRequestType requestType) {
		this.requestType = requestType;
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

	@Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 2);
		writeInt(requestType.value, 1);
		writeString(EncryptUtil.encodeStr(""+ longitude), 30);
		writeString(EncryptUtil.encodeStr(""+ latitude), 30);
		writeInt(startIndex, 1);
		writeInt(requestCount, 1);
        return buffers;
    }

	@Override
	public String toString() {
        return "대기지역 현황 요청 (0x" + Integer.toHexString(messageType) + ") " +
				"serviceNumber=" + serviceNumber +
				", corporationCode=" + corporationCode +
				", carId=" + carId +
				", requestType=" + requestType +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", startIndex=" + startIndex +
				", requestCount=" + requestCount +
				'}';
	}
}
