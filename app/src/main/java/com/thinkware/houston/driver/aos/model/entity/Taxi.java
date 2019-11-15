package com.thinkware.houston.driver.aos.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "taxi_table")
public class Taxi {

	@PrimaryKey
	private int id;

	@ColumnInfo(name = "taxi_driver_phone_number")
	private String driverPhoneNumber;

	@ColumnInfo(name = "tax_driver_name")
	private String driverName;

	@ColumnInfo(name = "taxi_company_id")
	private String companyId;

	@ColumnInfo(name = "taxi_company_name")
	private String companyName;

	@ColumnInfo(name = "taxi_plate_number")
	private String taxiPlateNumber;

	@ColumnInfo(name = "taxi_color")
	private String taxiColor;

	@ColumnInfo(name = "taxi_model")
	private String taxiModel;

	@ColumnInfo(name = "taxi_latitude")
	private String latitude;

	@ColumnInfo(name = "taxi_longitude")
	private String longitude;



	public Taxi() {
		id = 1;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public String getTaxiPlateNumber() {
		return taxiPlateNumber;
	}

	public void setTaxiPlateNumber(String taxiPlateNumber) {
		this.taxiPlateNumber = taxiPlateNumber;
	}

	public String getTaxiColor() {
		return taxiColor;
	}

	public void setTaxiColor(String taxiColor) {
		this.taxiColor = taxiColor;
	}

	public String getTaxiModel() {
		return taxiModel;
	}

	public void setTaxiModel(String taxiModel) {
		this.taxiModel = taxiModel;
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


	@Override
	public String toString() {
		return "Taxi{" +
				"id=" + id +
				", driverPhoneNumber='" + driverPhoneNumber + '\'' +
				", driverName='" + driverName + '\'' +
				", companyId='" + companyId + '\'' +
				", companyName='" + companyName + '\'' +
				", taxiPlateNumber='" + taxiPlateNumber + '\'' +
				", taxiColor='" + taxiColor + '\'' +
				", taxiModel='" + taxiModel + '\'' +
				", latitude='" + latitude + '\'' +
				", longitude='" + longitude + '\'' +
				'}';
	}
}
