package com.kiev.driver.aos.model;


public class WaitingZone {
	private int waitingZoneId;
	private String waitingZoneName;
	private int numberOfCarsInAreas;    //전체 대기 인원
	private boolean isAvailableWait;
	private int myWaitingOrder;                //선택된 대기존에서 내 순번


	public int getWaitingZoneId() {
		return waitingZoneId;
	}

	public void setWaitingZoneId(int waitingZoneId) {
		this.waitingZoneId = waitingZoneId;
	}

	public String getWaitingZoneName() {
		return waitingZoneName;
	}

	public void setWaitingZoneName(String waitingZoneName) {
		this.waitingZoneName = waitingZoneName;
	}

	public int getNumberOfCarsInAreas() {
		return numberOfCarsInAreas;
	}

	public void setNumberOfCarsInAreas(int numberOfCarsInAreas) {
		this.numberOfCarsInAreas = numberOfCarsInAreas;
	}

	public boolean isAvailableWait() {
		return isAvailableWait;
	}

	public void setAvailableWait(boolean availableWait) {
		isAvailableWait = availableWait;
	}

	public int getMyWaitingOrder() {
		return myWaitingOrder;
	}

	public void setMyWaitingOrder(int myWaitingOrder) {
		this.myWaitingOrder = myWaitingOrder;
	}

	@Override
	public String toString() {
		return "WaitingZone{" +
				"waitingZoneId=" + waitingZoneId +
				", waitingZoneName='" + waitingZoneName + '\'' +
				", numberOfCarsInAreas=" + numberOfCarsInAreas +
				", isAvailableWait=" + isAvailableWait +
				", myWaitingOrder=" + myWaitingOrder +
				'}';
	}
}
