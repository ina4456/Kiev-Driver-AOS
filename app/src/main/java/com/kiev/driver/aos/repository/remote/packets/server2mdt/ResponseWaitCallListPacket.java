package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 대기콜 리스트 (GT-5D12) 1173 Byte
 * Server -> MDT
 */

public class ResponseWaitCallListPacket extends ResponsePacket {

    private int carId; // Car ID(2)
	private int waitCallCount; // 대기콜 총 개수(2)
    private String callNumbers;
    private String callReceiptDates;
	private String orderCounts;
	private String departures;
	private String destinations;
	private String distances;
	private boolean hasMoreList;

    public ResponseWaitCallListPacket(byte[] bytes) {
        super(bytes);
    }

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public int getWaitCallCount() {
		return waitCallCount;
	}

	public void setWaitCallCount(int waitCallCount) {
		this.waitCallCount = waitCallCount;
	}

	public String getCallNumbers() {
		return callNumbers;
	}

	public void setCallNumbers(String callNumbers) {
		this.callNumbers = callNumbers;
	}

	public String getCallReceiptDates() {
		return callReceiptDates;
	}

	public void setCallReceiptDates(String callReceiptDates) {
		this.callReceiptDates = callReceiptDates;
	}

	public String getOrderCounts() {
		return orderCounts;
	}

	public void setOrderCounts(String orderCount) {
		this.orderCounts = orderCount;
	}

	public String getDepartures() {
		return departures;
	}

	public void setDepartures(String departures) {
		this.departures = departures;
	}

	public String getDestinations() {
		return destinations;
	}

	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

	public String getDistances() {
		return distances;
	}

	public void setDistances(String distances) {
		this.distances = distances;
	}

	public boolean isHasMoreList() {
		return hasMoreList;
	}

	public void setHasMoreList(boolean hasMoreList) {
		this.hasMoreList = hasMoreList;
	}

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);

        carId = readInt(4);
		waitCallCount = readInt(2);

		callNumbers = readString(72);
		callReceiptDates = readString(130);
		orderCounts = readString(42);
		departures = readString(420);
		destinations = readString(420);
		distances = readString(82);
		int moreList = readInt(1);
		hasMoreList = moreList == 0x01;

    }

    @Override
    public String toString() {
        return "대기콜리스트 응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
			", waitCallCount=" + waitCallCount +
			", callNumbers='" + callNumbers + '\'' +
			", callReceiptDates='" + callReceiptDates + '\'' +
			", orderCounts='" + orderCounts + '\'' +
			", departures='" + departures + '\'' +
			", destinations='" + destinations + '\'' +
			", distances='" + distances + '\'' +
			", hasMoreList=" + hasMoreList +
			'}';
    }
}
