package com.thinkware.houston.driver.aos.model;


public class WaitingZone {
	private String waitingZoneId;
	private String waitingZoneName;
	private String waitingTotalWaitingCount;    //전체 대기 인원
	private String waitingOrder;                //선택된 대기존에서 내 순번
	private boolean isBelongToThisWaitingZone;  //대기존에서 유저가 대기중인지 여부/
	private boolean possibleToSelect;

	public String getWaitingZoneId() {
		return waitingZoneId;
	}

	public void setWaitingZoneId(String waitingZoneId) {
		this.waitingZoneId = waitingZoneId;
	}

	public String getWaitingZoneName() {
		return waitingZoneName;
	}

	public void setWaitingZoneName(String waitingZoneName) {
		this.waitingZoneName = waitingZoneName;
	}

	public String getWaitingTotalWaitingCount() {
		return waitingTotalWaitingCount;
	}

	public void setWaitingTotalWaitingCount(String waitingTotalWaitingCount) {
		this.waitingTotalWaitingCount = waitingTotalWaitingCount;
	}

	public String getWaitingOrder() {
		return waitingOrder;
	}

	public void setWaitingOrder(String waitingOrder) {
		this.waitingOrder = waitingOrder;
	}

	public boolean isBelongToThisWaitingZone() {
		return isBelongToThisWaitingZone;
	}

	public void setBelongToThisWaitingZone(boolean belongToThisWaitingZone) {
		isBelongToThisWaitingZone = belongToThisWaitingZone;
	}

	public boolean isPossibleToSelect() {
		return possibleToSelect;
	}

	public void setPossibleToSelect(boolean possibleToSelect) {
		this.possibleToSelect = possibleToSelect;
	}

	@Override
	public String toString() {
		return "WaitingZone{" +
				"waitingZoneId='" + waitingZoneId + '\'' +
				", waitingZoneName='" + waitingZoneName + '\'' +
				", waitingTotalWaitingCount='" + waitingTotalWaitingCount + '\'' +
				", waitingOrder='" + waitingOrder + '\'' +
				", isBelongToThisWaitingZone=" + isBelongToThisWaitingZone +
				", possibleToSelect=" + possibleToSelect +
				'}';
	}
}
