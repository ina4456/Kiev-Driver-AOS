package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 메시지 전송용청 응답 (GT-5812) 229 Byte
 * Server -> MDT
 */
public class ResponseMessageNewPacket extends ResponsePacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (2)
    private String message; // 메시지 본문 (201)
	private String messageSentDate; // 메시지 전송일자 (20)
	private String messageId; // 메시지 코드

    public ResponseMessageNewPacket(byte[] bytes) {
        super(bytes);
    }

    public int getCorporationCode() {
        return corporationCode;
    }

    public int getCarId() {
        return carId;
    }

    public String getMessage() {
        return message;
    }

	public String getMessageSentDate() {
		return messageSentDate;
	}

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        corporationCode = readInt(2);
        carId = readInt(2);
        message = readString(201);
		messageSentDate = readString(20);
		messageId = readString(4);
    }

    @Override
    public String toString() {
        return "메시지 응답 new (0x" + Integer.toHexString(messageType) + ") " +
                "corporationCode=" + corporationCode +
                ", carId=" + carId +
                ", message='" + message + '\'';
    }
}