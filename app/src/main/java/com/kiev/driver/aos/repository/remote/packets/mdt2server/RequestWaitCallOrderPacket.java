package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 대기콜 배차요청 (GT-1913) 120 Byte
 * MDT -> Server
 */
public class RequestWaitCallOrderPacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
    private String phoneNumber; // 운전자 전화번호 (30)
    private String callReceiptDate; // 콜접수일자 (11) (ex : 2009-01-13)
    private int callNumber; // 콜번호 (2)
	private int orderCount; // 배차횟수 (1) (배차데이터(GT-1312)에서 받은 횟수)
	private String gpsTime; // GPS시간 (6) (년월일시분초 - ex : 090805112134)
	private int direction; // 주행방향 (2)
	private float longitude; // 경도 (30)
	private float latitude; // 위도 (30)
	private int speed; // 속도 (1)
	private float distance; //거리 (4)


    public RequestWaitCallOrderPacket() {
	    super(Packets.REQUEST_WAIT_CALL_ORDER);
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

    public int getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(int callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallReceiptDate() {
        return callReceiptDate;
    }

    public void setCallReceiptDate(String callReceiptDate) {
        this.callReceiptDate = callReceiptDate;
    }

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
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

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 2);
	    writeString(EncryptUtil.encodeStr(phoneNumber), 30);
        writeString(callReceiptDate, 11);
        writeInt(callNumber, 2);
		writeInt(orderCount, 1);
		writeDateTime(gpsTime, 6);
		writeInt(direction, 2);
		writeString(EncryptUtil.encodeStr("" + longitude), 30);
		writeString(EncryptUtil.encodeStr("" + latitude), 30);
		writeInt(speed, 1);
		writeFloat(distance, 4);

        return buffers;
    }

    @Override
    public String toString() {
        return "실시간 위치 및 배차결정 (0x" + Integer.toHexString(messageType) + ") " +
                "serviceNumber=" + serviceNumber +
                ", corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", phoneNumber='" + phoneNumber +
                ", callNumber=" + callNumber +
                ", callReceiptDate='" + distance +
                ", callReceiptDate='" + callReceiptDate + '\'';
    }
}
