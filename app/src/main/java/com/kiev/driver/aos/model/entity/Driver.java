package com.kiev.driver.aos.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "driver_table")
public class Driver {

	@PrimaryKey
	private int id;

	@ColumnInfo(name = "driver_phone_number")
	private String driverPhoneNumber;

	@ColumnInfo(name = "driver_name")
	private String driverName;

	@ColumnInfo(name = "driver_company_id")
	private String companyId;

	@ColumnInfo(name = "driver_company_name")
	private String companyName;

	@ColumnInfo(name = "driver_vehicle_number")
	private String vehicleNumber;

	@ColumnInfo(name = "driver_latitude")
	private String latitude;

	@ColumnInfo(name = "driver_longitude")
	private String longitude;

	@ColumnInfo(name = "driver_business_area")
	private String businessArea;


	public Driver() {
		id = 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDriverPhoneNumber() {
		return driverPhoneNumber;
	}

	public void setDriverPhoneNumber(String driverPhoneNumber) {
		this.driverPhoneNumber = driverPhoneNumber;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	@Override
	public String toString() {
		return "Driver{" +
				"id=" + id +
				", driverPhoneNumber='" + driverPhoneNumber + '\'' +
				", driverName='" + driverName + '\'' +
				", companyId='" + companyId + '\'' +
				", companyName='" + companyName + '\'' +
				", vehicleNumber='" + vehicleNumber + '\'' +
				", latitude='" + latitude + '\'' +
				", longitude='" + longitude + '\'' +
				", businessArea='" + businessArea + '\'' +
				'}';
	}
}
