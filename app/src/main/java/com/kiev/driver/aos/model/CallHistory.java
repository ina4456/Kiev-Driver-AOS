package com.kiev.driver.aos.model;

import com.kiev.driver.aos.repository.remote.packets.Packets;

public class CallHistory {
	private Packets.StatisticListType callType;
	private String callTypeStr;
	private int callId;
	private String date;
	private String departure;
	private String destination;
	private String startTime;
	private String endTime;
	private String passengerPhoneNumber;

	public int getCallId() {
		return callId;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCallTypeStr() {
		return callTypeStr;
	}

	public void setCallTypeStr(String callTypeStr) {
		this.callTypeStr = callTypeStr;
		switch (callTypeStr) {
			case "3":
				this.setCallType(Packets.StatisticListType.AppCall);
				break;
			case "4":
				this.setCallType(Packets.StatisticListType.BusinessCall);
				break;
			default:
				this.setCallType(Packets.StatisticListType.NormalCall);
				break;
		}
	}

	public Packets.StatisticListType getCallType() {
		return callType;
	}

	public void setCallType(Packets.StatisticListType callType) {
		this.callType = callType;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPassengerPhoneNumber() {
		return passengerPhoneNumber;
	}

	public void setPassengerPhoneNumber(String passengerPhoneNumber) {
		this.passengerPhoneNumber = passengerPhoneNumber;
	}

	@Override
	public String toString() {
		return "CallHistory{" +
				"date='" + date + '\'' +
				", callTypeStr='" + callTypeStr + '\'' +
				", departure='" + departure + '\'' +
				", destination='" + destination + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", passengerPhoneNumber='" + passengerPhoneNumber + '\'' +
				'}';
	}
}
