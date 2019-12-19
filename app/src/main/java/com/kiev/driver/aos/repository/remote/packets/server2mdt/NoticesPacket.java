package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 공지사항 (GT-5114) 293 Byte
 * Server -> MDT
 */

public class NoticesPacket extends ResponsePacket {
    private int carId; // car ID (2)
    private int noticeCode; // 공지코드 (2)
    private String noticeTitle; // 공지제목 (31)
    private String notice; // 공지본문 (256)

    public NoticesPacket(byte[] bytes) {
        super(bytes);
    }

    public int getCarId() {
        return carId;
    }

    public int getNoticeCode() {
        return noticeCode;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNotice() {
        return notice;
    }

    @Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(4);
        noticeCode = readInt(2);
        noticeTitle = readString(31);
        notice = readString(256);
    }

    @Override
    public String toString() {
        return "공지사항 (0x" + Integer.toHexString(messageType) + ") " +
                "carId=" + carId +
                ", noticeCode=" + noticeCode +
                ", noticeTitle='" + noticeTitle + '\'' +
                ", notice='" + notice + '\'';
    }
}