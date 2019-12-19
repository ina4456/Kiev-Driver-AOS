package com.kiev.driver.aos.repository.remote.packets.mdt2server;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.RequestPacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 운행이력 요청 (GT-5915) 40 Byte
 * MDT -> Server
 */
public class RequestStatisticsDetailPacket extends RequestPacket {

	private int corporationCode; // 법인코드 (2)
	private int carId; // Car ID (4)
	private Packets.StatisticListType queryType; //요청 구분(1) : 통합, 일반콜, 앱콜, 업무콜
	private int startIndex; //시작 인덱스(1)
	private int requestCount; //요청개수(1)
	private Packets.StatisticPeriodType queryPeriod; //요청 기간 구분 (1): 오늘, 최근 7일, 이번달, 지난달
	private String phoneNumber; // Phone Number (30)


	public RequestStatisticsDetailPacket() {
		super(Packets.REQ_STATISTICS_DETAIL);
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

	public Packets.StatisticListType getQueryType() {
		return queryType;
	}

	public void setQueryType(Packets.StatisticListType queryType) {
		this.queryType = queryType;
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

	public Packets.StatisticPeriodType getQueryPeriod() {
		return queryPeriod;
	}

	public void setQueryPeriod(Packets.StatisticPeriodType queryPeriod) {
		this.queryPeriod = queryPeriod;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public byte[] toBytes() {
		super.toBytes();
		writeInt(corporationCode, 2);
		writeInt(carId, 4);
		writeInt(queryType.value, 1);
		writeInt(startIndex, 1);
		writeInt(requestCount, 1);
		writeInt(queryPeriod.value, 1);
		writeString(EncryptUtil.encodeStr(phoneNumber), 30);
		return buffers;
	}

	@Override
	public String toString() {
		return "콜 정산 통계 상세 요청 (0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", queryType=" + queryType +
				", startIndex=" + startIndex +
				", requestCount=" + requestCount +
				", queryPeriod=" + queryPeriod +
				", phoneNumber='" + phoneNumber + '\'' +
				'}';
	}
}
