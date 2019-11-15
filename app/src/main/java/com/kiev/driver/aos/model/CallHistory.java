package com.kiev.driver.aos.model;

public class CallHistory {
	private String date;
	private String orderStatus;
	private String departure;
	private String destination;
	private String startTime;
	private String endTime;
	private String passengerPhoneNumber;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
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
				", orderStatus='" + orderStatus + '\'' +
				", departure='" + departure + '\'' +
				", destination='" + destination + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", passengerPhoneNumber='" + passengerPhoneNumber + '\'' +
				'}';
	}
}
