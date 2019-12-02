package com.kiev.driver.aos.model.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "call_table")
public class Call implements Parcelable {

	@PrimaryKey(autoGenerate = true)
	private int id;

	@ColumnInfo(name = "call_number")
	private int callNumber;

	@ColumnInfo(name = "call_type")
	private int callType;

	@ColumnInfo(name = "call_is_temp")
	private boolean callIsTemp;

	@ColumnInfo(name = "call_class")
	private int callClass;

	@ColumnInfo(name = "call_order_count")
	private int callOrderCount;

	@ColumnInfo(name = "call_received_date")
	private String callReceivedDate;

	@ColumnInfo(name = "call_status")
	private int callStatus;

	@ColumnInfo(name = "call_distance_to_departure")
	private String callDistanceToDeparture;

	@ColumnInfo(name = "call_waiting_zone_id")
	private String waitingZoneId;
	@ColumnInfo(name = "call_waiting_zone_name")
	private String waitingZoneName;

	@ColumnInfo(name = "call_departure_poi")
	private String departurePoi;
	@ColumnInfo(name = "call_departure_addr")
	private String departureAddr;
	@ColumnInfo(name = "call_departure_lat")
	private double departureLat;
	@ColumnInfo(name = "call_departure_long")
	private double departureLong;

	@ColumnInfo(name = "call_destination_poi")
	private String destinationPoi;
	@ColumnInfo(name = "call_destination_addr")
	private String destinationAddr;
	@ColumnInfo(name = "call_destination_lat")
	private double destinationLat;
	@ColumnInfo(name = "call_destination_long")
	private double destinationLong;

	@ColumnInfo(name = "call_passenger_phone_number")
	private String passengerPhoneNumber;

	//복지콜
	@ColumnInfo(name = "call_passenger_name")
	private String passengerName;
	@ColumnInfo(name = "call_handicap_code")
	private String handicapCode;
	@ColumnInfo(name = "call_use_wheelchair")
	private boolean useWheelchair;

	private boolean isReported; // 운행보고 여부 (Local에서만 사용하는 값이다.)
	private float distance;


	public Call() {
	}
	public Call(OrderInfoPacket packet) {
		this.setCallNumber(packet.getCallNumber());
		this.setCallType(packet.getOrderKind().value);
		this.setCallClass(packet.getCallClass());
		this.setCallReceivedDate(packet.getCallReceiptDate());
		this.setPassengerPhoneNumber(packet.getCallerPhone());
		this.setDepartureLat(packet.getLatitude());
		this.setDepartureLong(packet.getLongitude());
		this.setDeparturePoi(packet.getPlace());
		this.setDepartureAddr(packet.getPlaceExplanation());
		this.setDestinationLat(packet.getDestLatitude());
		this.setDestinationLong(packet.getDestLongitude());
		this.setDestinationPoi(packet.getDestName());
		this.setDestinationAddr(packet.getDestination());

		//복지콜
		this.setPassengerName(packet.getCallerName());
		this.setHandicapCode(packet.getErrorCode());
		this.setCallOrderCount(packet.getOrderCount());
		this.setUseWheelchair(packet.isWheelChair().equals("Y"));
	}

	private Call(Parcel in) {
		readFromParcel(in);
	}

	public static final Creator<Call> CREATOR = new Creator<Call>() {
		@Override
		public Call createFromParcel(Parcel in) {
			return new Call(in);
		}

		@Override
		public Call[] newArray(int size) {
			return new Call[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeInt(callNumber);
		parcel.writeInt(callType);
		parcel.writeByte((byte)(callIsTemp ? 1 : 0));
		parcel.writeInt(callClass);
		parcel.writeInt(callOrderCount);
		parcel.writeString(callReceivedDate);
		parcel.writeInt(callStatus);
		parcel.writeFloat(distance);
		parcel.writeString(callDistanceToDeparture);
		parcel.writeString(waitingZoneId);
		parcel.writeString(waitingZoneName);
		parcel.writeString(departurePoi);
		parcel.writeString(departureAddr);
		parcel.writeDouble(departureLat);
		parcel.writeDouble(departureLong);
		parcel.writeString(destinationPoi);
		parcel.writeString(destinationAddr);
		parcel.writeDouble(destinationLat);
		parcel.writeDouble(destinationLong);
		parcel.writeString(passengerPhoneNumber);
		parcel.writeString(handicapCode);
		parcel.writeString(passengerPhoneNumber);
		parcel.writeByte((byte)(useWheelchair ? 1 : 0));

	}

	private void readFromParcel(Parcel in) {
		id = in.readInt();
		callNumber = in.readInt();
		callType = in.readInt();
		callIsTemp = in.readByte() != 0;
		callClass = in.readInt();
		callOrderCount = in.readInt();
		callReceivedDate = in.readString();
		callStatus = in.readInt();
		distance = in.readFloat();
		callDistanceToDeparture = in.readString();
		waitingZoneId = in.readString();
		waitingZoneName = in.readString();
		departurePoi = in.readString();
		departureAddr = in.readString();
		departureLat = in.readDouble();
		departureLong = in.readDouble();
		destinationPoi = in.readString();
		destinationAddr = in.readString();
		destinationLat = in.readDouble();
		destinationLong = in.readDouble();
		passengerPhoneNumber = in.readString();

		passengerName = in.readString();
		handicapCode = in.readString();
		useWheelchair = in.readByte() != 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(int callNumber) {
		this.callNumber = callNumber;
	}

	public int getCallType() {
		return callType;
	}

	@Ignore
	public Packets.OrderKind getCallTypeEnum() {
		for (Packets.OrderKind kind :Packets.OrderKind.values()) {
			if (kind.value == callType) {
				return kind;
			}
		}
		return null;
	}

	public int getCallClass() {
		return callClass;
	}

	public void setCallClass(int callClass) {
		this.callClass = callClass;
	}

	public boolean isCallIsTemp() {
		return callIsTemp;
	}

	public void setCallIsTemp(boolean callIsTemp) {
		this.callIsTemp = callIsTemp;
	}

	public int getCallOrderCount() {
		return callOrderCount;
	}

	public void setCallOrderCount(int callOrderCount) {
		this.callOrderCount = callOrderCount;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public int getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(int callStatus) {
		this.callStatus = callStatus;
	}

	public String getDeparturePoi() {
		return departurePoi;
	}

	public void setDeparturePoi(String departurePoi) {
		this.departurePoi = departurePoi;
	}

	public String getDepartureAddr() {
		return departureAddr;
	}

	public void setDepartureAddr(String departureAddr) {
		this.departureAddr = departureAddr;
	}

	public String getDestinationPoi() {
		return destinationPoi;
	}

	public void setDestinationPoi(String destinationPoi) {
		this.destinationPoi = destinationPoi;
	}

	public String getDestinationAddr() {
		return destinationAddr;
	}

	public void setDestinationAddr(String destinationAddr) {
		this.destinationAddr = destinationAddr;
	}

	public double getDepartureLat() {
		return departureLat;
	}

	public void setDepartureLat(double departureLat) {
		this.departureLat = departureLat;
	}

	public double getDepartureLong() {
		return departureLong;
	}

	public void setDepartureLong(double departureLong) {
		this.departureLong = departureLong;
	}

	public double getDestinationLat() {
		return destinationLat;
	}

	public void setDestinationLat(double destinationLat) {
		this.destinationLat = destinationLat;
	}

	public double getDestinationLong() {
		return destinationLong;
	}

	public void setDestinationLong(double destinationLong) {
		this.destinationLong = destinationLong;
	}

	public String getPassengerPhoneNumber() {
		return passengerPhoneNumber;
	}

	public void setPassengerPhoneNumber(String passengerPhoneNumber) {
		this.passengerPhoneNumber = passengerPhoneNumber;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getCallDistanceToDeparture() {
		return callDistanceToDeparture;
	}

	public void setCallDistanceToDeparture(String callDistanceToDeparture) {
		this.callDistanceToDeparture = callDistanceToDeparture;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		//LogHelper.e("distance : " + distance);
		this.distance = distance;
		String distanceStr;
		float distanceTemp;

		if (distance < 1000) {
			distanceStr = (int)distance + "m";
		} else {
			distanceTemp = distance / 1000;
			distanceTemp = distanceTemp + ((distance - (distanceTemp * 1000)) / 1000);
			distanceStr = String.format("%.1f", distanceTemp);
			distanceStr = distanceStr + "km";
		}
		//LogHelper.e("distanceStr : " + distanceStr);

		this.setCallDistanceToDeparture(distanceStr);
	}

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

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean reported) {
		isReported = reported;
	}

	public String getCallReceivedDate() {
		return callReceivedDate;
	}

	public void setCallReceivedDate(String callReceivedDate) {
		this.callReceivedDate = callReceivedDate;
	}

	public String getHandicapCode() {
		return handicapCode;
	}

	public void setHandicapCode(String handicapCode) {
		this.handicapCode = handicapCode;
	}

	public boolean isUseWheelchair() {
		return useWheelchair;
	}

	public void setUseWheelchair(boolean useWheelchair) {
		this.useWheelchair = useWheelchair;
	}

	@Override
	public String toString() {
		return "Call{" +
				"id=" + id +
				", callNumber=" + callNumber +
				", callType=" + callType +
				", callIsTemp=" + callIsTemp +
				", callClass=" + callClass +
				", callOrderCount=" + callOrderCount +
				", callReceivedDate='" + callReceivedDate + '\'' +
				", callStatus=" + callStatus +
				", distance=" + distance +
				", callDistanceToDeparture='" + callDistanceToDeparture + '\'' +
				", waitingZoneId='" + waitingZoneId + '\'' +
				", waitingZoneName='" + waitingZoneName + '\'' +
				", departurePoi='" + departurePoi + '\'' +
				", departureAddr='" + departureAddr + '\'' +
				", departureLat=" + departureLat +
				", departureLong=" + departureLong +
				", destinationPoi='" + destinationPoi + '\'' +
				", destinationAddr='" + destinationAddr + '\'' +
				", destinationLat=" + destinationLat +
				", destinationLong=" + destinationLong +
				", passengerPhoneNumber='" + passengerPhoneNumber + '\'' +
				", passengerName='" + passengerName + '\'' +
				", handicapCode='" + handicapCode + '\'' +
				", useWheelchair=" + useWheelchair +
				", isReported=" + isReported +
				'}';
	}
}
