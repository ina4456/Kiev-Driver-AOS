package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * SMS 전송 요청 (GT-5813) 180 Byte
 * MDT -> Server
 */
public class RequestSendSMSPacket extends RequestPacket {

    private int serviceNumber; // 서비스번호 (1)
    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
	private String callReceiptDate; // 콜접수일자(ex : 2009-01-23) (11)
	private int callNumber; // 콜번호 (2)
	private String content; // 내용 (160)

    public RequestSendSMSPacket() {
	    super(Packets.REQ_SEND_SMS);
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

	public String getCallReceiptDate() {
		return callReceiptDate;
	}

	public void setCallReceiptDate(String callReceiptDate) {
		this.callReceiptDate = callReceiptDate;
	}

	public int getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(int callNumber) {
		this.callNumber = callNumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
    public byte[] toBytes() {
        super.toBytes();
        writeInt(serviceNumber, 1);
        writeInt(corporationCode, 2);
        writeInt(carId, 2);
		writeString(callReceiptDate, 11);
		writeInt(callNumber, 2);
        writeString(EncryptUtil.encodeStrUTF8(content), 160);
        return buffers;
    }

    @Override
    public String toString() {
        return "SMS 전송 요청 (0x" + Integer.toHexString(messageType) + ") " +
		        "serviceNumber=" + serviceNumber +
		        ", corporationCode=" + corporationCode +
		        ", carId=" + carId +
		        ", callReceiptDate='" + callReceiptDate + '\'' +
		        ", callNumber=" + callNumber +
		        ", content='" + content + '\'' +
		        '}';
    }
}
