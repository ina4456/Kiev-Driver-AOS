package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 내정보 요청 응답 (GT-5138) 1504 Byte
 * Server -> MDT
 */

public class ResponseNoticeListPacket extends ResponsePacket {

    private int carId; // Car ID(2)
	private int noticeCode1;    //공지코드1 (2)
	private int noticeCode2;    //공지코드2 (2)
	private int noticeCode3;    //공지코드3 (2)
	private int noticeCode4;    //공지코드4 (2)
	private int noticeCode5;    //공지코드5 (2)

	private String noticeDate1;    //공지시작일1 (11)
	private String noticeDate2;    //공지시작일2 (11)
	private String noticeDate3;    //공지시작일3 (11)
	private String noticeDate4;    //공지시작일4 (11)
	private String noticeDate5;    //공지시작일5 (11)

	private String noticeTitle1;    //공지제목1 (31)
	private String noticeTitle2;    //공지제목2 (31)
	private String noticeTitle3;    //공지제목3 (31)
	private String noticeTitle4;    //공지제목4 (31)
	private String noticeTitle5;    //공지제목5 (31)

	private String noticeContent1;    //공지본문1 (256)
	private String noticeContent2;    //공지본문2 (256)
	private String noticeContent3;    //공지본문3 (256)
	private String noticeContent4;    //공지본문4 (256)
	private String noticeContent5;    //공지본문5 (256)

	public ResponseNoticeListPacket(byte[] bytes) {
		super(bytes);
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public int getNoticeCode1() {
		return noticeCode1;
	}

	public void setNoticeCode1(int noticeCode1) {
		this.noticeCode1 = noticeCode1;
	}

	public int getNoticeCode2() {
		return noticeCode2;
	}

	public void setNoticeCode2(int noticeCode2) {
		this.noticeCode2 = noticeCode2;
	}

	public int getNoticeCode3() {
		return noticeCode3;
	}

	public void setNoticeCode3(int noticeCode3) {
		this.noticeCode3 = noticeCode3;
	}

	public int getNoticeCode4() {
		return noticeCode4;
	}

	public void setNoticeCode4(int noticeCode4) {
		this.noticeCode4 = noticeCode4;
	}

	public int getNoticeCode5() {
		return noticeCode5;
	}

	public void setNoticeCode5(int noticeCode5) {
		this.noticeCode5 = noticeCode5;
	}

	public String getNoticeDate1() {
		return noticeDate1;
	}

	public void setNoticeDate1(String noticeDate1) {
		this.noticeDate1 = noticeDate1;
	}

	public String getNoticeDate2() {
		return noticeDate2;
	}

	public void setNoticeDate2(String noticeDate2) {
		this.noticeDate2 = noticeDate2;
	}

	public String getNoticeDate3() {
		return noticeDate3;
	}

	public void setNoticeDate3(String noticeDate3) {
		this.noticeDate3 = noticeDate3;
	}

	public String getNoticeDate4() {
		return noticeDate4;
	}

	public void setNoticeDate4(String noticeDate4) {
		this.noticeDate4 = noticeDate4;
	}

	public String getNoticeDate5() {
		return noticeDate5;
	}

	public void setNoticeDate5(String noticeDate5) {
		this.noticeDate5 = noticeDate5;
	}

	public String getNoticeTitle1() {
		return noticeTitle1;
	}

	public void setNoticeTitle1(String noticeTitle1) {
		this.noticeTitle1 = noticeTitle1;
	}

	public String getNoticeTitle2() {
		return noticeTitle2;
	}

	public void setNoticeTitle2(String noticeTitle2) {
		this.noticeTitle2 = noticeTitle2;
	}

	public String getNoticeTitle3() {
		return noticeTitle3;
	}

	public void setNoticeTitle3(String noticeTitle3) {
		this.noticeTitle3 = noticeTitle3;
	}

	public String getNoticeTitle4() {
		return noticeTitle4;
	}

	public void setNoticeTitle4(String noticeTitle4) {
		this.noticeTitle4 = noticeTitle4;
	}

	public String getNoticeTitle5() {
		return noticeTitle5;
	}

	public void setNoticeTitle5(String noticeTitle5) {
		this.noticeTitle5 = noticeTitle5;
	}

	public String getNoticeContent1() {
		return noticeContent1;
	}

	public void setNoticeContent1(String noticeContent1) {
		this.noticeContent1 = noticeContent1;
	}

	public String getNoticeContent2() {
		return noticeContent2;
	}

	public void setNoticeContent2(String noticeContent2) {
		this.noticeContent2 = noticeContent2;
	}

	public String getNoticeContent3() {
		return noticeContent3;
	}

	public void setNoticeContent3(String noticeContent3) {
		this.noticeContent3 = noticeContent3;
	}

	public String getNoticeContent4() {
		return noticeContent4;
	}

	public void setNoticeContent4(String noticeContent4) {
		this.noticeContent4 = noticeContent4;
	}

	public String getNoticeContent5() {
		return noticeContent5;
	}

	public void setNoticeContent5(String noticeContent5) {
		this.noticeContent5 = noticeContent5;
	}

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(4);

        noticeCode1 = readInt(2);
		noticeDate1 = readString(11);
		noticeTitle1 = readString(31);
		noticeContent1 = readString(256);

		noticeCode2 = readInt(2);
		noticeDate2 = readString(11);
		noticeTitle2 = readString(31);
		noticeContent2 = readString(256);

		noticeCode3 = readInt(2);
		noticeDate3 = readString(11);
		noticeTitle3 = readString(31);
		noticeContent3 = readString(256);

		noticeCode4 = readInt(2);
		noticeDate4 = readString(11);
		noticeTitle4 = readString(31);
		noticeContent4 = readString(256);

		noticeCode5 = readInt(2);
		noticeDate5 = readString(11);
		noticeTitle5 = readString(31);
		noticeContent5 = readString(256);

    }

	@Override
	public String toString() {
        return "공지사항 리스트 응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
				", noticeCode1=" + noticeCode1 +
				", noticeCode2=" + noticeCode2 +
				", noticeCode3=" + noticeCode3 +
				", noticeCode4=" + noticeCode4 +
				", noticeCode5=" + noticeCode5 +
				", noticeDate1='" + noticeDate1 + '\'' +
				", noticeDate2='" + noticeDate2 + '\'' +
				", noticeDate3='" + noticeDate3 + '\'' +
				", noticeDate4='" + noticeDate4 + '\'' +
				", noticeDate5='" + noticeDate5 + '\'' +
				", noticeTitle1='" + noticeTitle1 + '\'' +
				", noticeTitle2='" + noticeTitle2 + '\'' +
				", noticeTitle3='" + noticeTitle3 + '\'' +
				", noticeTitle4='" + noticeTitle4 + '\'' +
				", noticeTitle5='" + noticeTitle5 + '\'' +
				", noticeContent1='" + noticeContent1 + '\'' +
				", noticeContent2='" + noticeContent2 + '\'' +
				", noticeContent3='" + noticeContent3 + '\'' +
				", noticeContent4='" + noticeContent4 + '\'' +
				", noticeContent5='" + noticeContent5 + '\'' +
				'}';
	}
}
