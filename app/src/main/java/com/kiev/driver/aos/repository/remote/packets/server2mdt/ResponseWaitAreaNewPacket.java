package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;


public class ResponseWaitAreaNewPacket extends ResponsePacket {

    private int carId; // Car ID (2)
	private int totalCount; //대기지역 총 개수(2)
	private String waitAreaNames;
	private String waitAreaIds;
	private String numberOfCarInAreas;
	private String isAvailableWaits;
	private String myWaitNumbers;
	private boolean hasMoreData;


    public ResponseWaitAreaNewPacket(byte[] bytes)
    {
        super(bytes);
    }

    public int getCarId() {
        return carId;
    }

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getWaitAreaNames() {
		return waitAreaNames;
	}

	public void setWaitAreaNames(String waitAreaNames) {
		this.waitAreaNames = waitAreaNames;
	}

	public String getWaitAreaIds() {
		return waitAreaIds;
	}

	public void setWaitAreaIds(String waitAreaIds) {
		this.waitAreaIds = waitAreaIds;
	}

	public String getNumberOfCarInAreas() {
		return numberOfCarInAreas;
	}

	public void setNumberOfCarInAreas(String numberOfCarInAreas) {
		this.numberOfCarInAreas = numberOfCarInAreas;
	}

	public String getIsAvailableWaits() {
		return isAvailableWaits;
	}

	public void setIsAvailableWaits(String isAvailableWaits) {
		this.isAvailableWaits = isAvailableWaits;
	}

	public String getMyWaitNumbers() {
		return myWaitNumbers;
	}

	public void setMyWaitNumbers(String myWaitNumbers) {
		this.myWaitNumbers = myWaitNumbers;
	}

	public boolean isHasMoreData() {
		return hasMoreData;
	}

	public void setHasMoreData(boolean hasMoreData) {
		this.hasMoreData = hasMoreData;
	}

	@Override
    public void parse(byte[] buffers) {
		super.parse(buffers);

        carId = readInt(2);
		totalCount = readInt(2);


		waitAreaNames = readString(300);
		waitAreaIds = readString(50);
		numberOfCarInAreas = readString(50);
		isAvailableWaits = readString(50);
		myWaitNumbers = readString(50);
		int hasMore = readInt(1);
		hasMoreData = hasMore == 0x01;
    }

	@Override
	public String toString() {
        return "대기지역현황응답 (0x" + Integer.toHexString(messageType) + ") " +
				"carId=" + carId +
				", totalCount=" + totalCount +
				", waitAreaNames='" + waitAreaNames + '\'' +
				", waitAreaIds='" + waitAreaIds + '\'' +
				", numberOfCarInAreas='" + numberOfCarInAreas + '\'' +
				", isAvailableWaits='" + isAvailableWaits + '\'' +
				", myWaitNumbers='" + myWaitNumbers + '\'' +
				", hasMoreData='" + hasMoreData + '\'' +
				'}';
	}
}
