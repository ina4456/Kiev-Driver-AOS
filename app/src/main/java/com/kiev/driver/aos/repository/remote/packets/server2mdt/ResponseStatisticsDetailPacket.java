package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * Created by sbkwon on 2019. 9. 4..
 * 콜정산정보 (GT-1916) 1457 Byte
 * Server -> MDT
 */
public class ResponseStatisticsDetailPacket extends ResponsePacket {

    private int carId; // Car ID (2)
	private Packets.StatisticListType queryType; //요청 구분(1) : 통합, 일반콜, 앱콜, 업무콜
	private int totalCount; //총 개수(2)
	private String callType; //콜 구분(20) : 일반콜, 앱콜, 업무콜
	private String callNumber; //콜 번호(40)
	private String receiptDate; //콜 접수일 (70)
	private String departure; //출발지 (220)
	private String destination; //도착지 (220)

	private String boardingTime; //승차시간(40)
	private String alightingTime; //하차시간(40)
	private String phoneNumber; //승객 전화번호(160)
	private boolean hasMore; //추가 리스트 유/무

	public ResponseStatisticsDetailPacket(byte[] bytes) {
		super(bytes);
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public Packets.StatisticListType getQueryType() {
		return queryType;
	}

	public void setQueryType(Packets.StatisticListType queryType) {
		this.queryType = queryType;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getBoardingTime() {
		return boardingTime;
	}

	public void setBoardingTime(String boardingTime) {
		this.boardingTime = boardingTime;
	}

	public String getAlightingTime() {
		return alightingTime;
	}

	public void setAlightingTime(String alightingTime) {
		this.alightingTime = alightingTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
    public void parse(byte[] buffers) {
		super.parse(buffers);
		carId = readInt(2);
		int qType = readInt(1);
		queryType = getCallType(qType);
		totalCount = readInt(2);
		callType = readString(20);
		callNumber = readString(40);
		receiptDate = readString(70);

		departure = readString(220);
		destination = readString(220);

		boardingTime = readString(40);
		alightingTime = readString(40);

		phoneNumber = readString(160);
		int more = readInt(1);
		hasMore = more == 0x01;
	}

	private Packets.StatisticListType getCallType(int query) {
		Packets.StatisticListType queryType;
		if (Packets.StatisticListType.NormalCall.value == query) {
			queryType = Packets.StatisticListType.NormalCall;
		} else if (Packets.StatisticListType.AppCall.value == query) {
			queryType = Packets.StatisticListType.NormalCall;
		} else if (Packets.StatisticListType.BusinessCall.value == query) {
			queryType = Packets.StatisticListType.BusinessCall;
		} else {
			queryType = Packets.StatisticListType.TotalCall;
		}
		return queryType;
	}

	@Override
	public String toString() {
		return "콜 정산 통계 상세 응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
				", queryType=" + queryType +
				", totalCount=" + totalCount +
				", callType=" + callType +
				", callNumber='" + callNumber + '\'' +
				", receiptDate='" + receiptDate + '\'' +
				", departure='" + departure + '\'' +
				", destination='" + destination + '\'' +
				", boardingTime='" + boardingTime + '\'' +
				", alightingTime='" + alightingTime + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", hasMore=" + hasMore +
				'}';
	}
}