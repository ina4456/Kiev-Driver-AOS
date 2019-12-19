package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;
import com.kiev.driver.aos.util.EncryptUtil;

/**
 * 콜정산정보 (GT-5614) 114 Byte
 * Server -> MDT
 */
public class ResponseStatisticsPacket extends ResponsePacket {

    private int corporationCode; // 법인코드 (2)
    private int carId; // Car ID (4)
    private String phoneNumber; // Phone Number (30)
	
	//오늘(총콜, 일반콜, 업무콜, 앱콜, 첨두콜, 외곽콜) 각 (2)
	private int todayTotalCnt;
	private int todayNormalCnt;
	private int todayBusinessCnt;
	private int todayAppCnt;
	private int todayPeakCnt;
	private int todaySuburbCnt;

	//최근 7일(총콜, 일반콜, 업무콜, 앱콜, 첨두콜, 외곽콜) 각 (2)
	private int weekTotalCnt;
	private int weekNormalCnt;
	private int weekBusinessCnt;
	private int weekAppCnt;
	private int weekPeakCnt;
	private int weekSuburbCnt;

	//이번달(총콜, 일반콜, 업무콜, 앱콜, 첨두콜, 외곽콜) 각 (2)
	private int thisMonthTotalCnt;
	private int thisMonthNormalCnt;
	private int thisMonthBusinessCnt;
	private int thisMonthAppCnt;
	private int thisMonthPeakCnt;
	private int thisMonthSuburbCnt;

	//지난달(총콜, 일반콜, 업무콜, 앱콜, 첨두콜, 외곽콜) 각 (2)
	private int lastMonthTotalCnt;
	private int lastMonthNormalCnt;
	private int lastMonthBusinessCnt;
	private int lastMonthAppCnt;
	private int lastMonthPeakCnt;
	private int lastMonthSuburbCnt;

    private String memo; // 비고 (30)

    public ResponseStatisticsPacket(byte[] bytes) {
        super(bytes);
    }

	public int getCorporationCode() {
		return corporationCode;
	}

	public int getCarId() {
		return carId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public int getTodayTotalCnt() {
		return todayTotalCnt;
	}

	public int getTodayNormalCnt() {
		return todayNormalCnt;
	}

	public int getTodayBusinessCnt() {
		return todayBusinessCnt;
	}

	public int getTodayAppCnt() {
		return todayAppCnt;
	}

	public int getTodayPeakCnt() {
		return todayPeakCnt;
	}

	public int getTodaySuburbCnt() {
		return todaySuburbCnt;
	}

	public int getWeekTotalCnt() {
		return weekTotalCnt;
	}

	public int getWeekNormalCnt() {
		return weekNormalCnt;
	}

	public int getWeekBusinessCnt() {
		return weekBusinessCnt;
	}

	public int getWeekAppCnt() {
		return weekAppCnt;
	}

	public int getWeekPeakCnt() {
		return weekPeakCnt;
	}

	public int getWeekSuburbCnt() {
		return weekSuburbCnt;
	}

	public int getThisMonthTotalCnt() {
		return thisMonthTotalCnt;
	}

	public int getThisMonthNormalCnt() {
		return thisMonthNormalCnt;
	}

	public int getThisMonthBusinessCnt() {
		return thisMonthBusinessCnt;
	}

	public int getThisMonthAppCnt() {
		return thisMonthAppCnt;
	}

	public int getThisMonthPeakCnt() {
		return thisMonthPeakCnt;
	}

	public int getThisMonthSuburbCnt() {
		return thisMonthSuburbCnt;
	}

	public int getLastMonthTotalCnt() {
		return lastMonthTotalCnt;
	}

	public int getLastMonthNormalCnt() {
		return lastMonthNormalCnt;
	}

	public int getLastMonthBusinessCnt() {
		return lastMonthBusinessCnt;
	}

	public int getLastMonthAppCnt() {
		return lastMonthAppCnt;
	}

	public int getLastMonthPeakCnt() {
		return lastMonthPeakCnt;
	}

	public int getLastMonthSuburbCnt() {
		return lastMonthSuburbCnt;
	}

	public String getMemo() {
		return memo;
	}

	@Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        corporationCode = readInt(2);
        carId = readInt(4);
		phoneNumber = EncryptUtil.decodeStr("" + readString(30));

		todayTotalCnt = readInt(2);
		todayNormalCnt = readInt(2);
		todayBusinessCnt = readInt(2);
		todayAppCnt = readInt(2);
		todayPeakCnt = readInt(2);
		todaySuburbCnt = readInt(2);
		
		weekTotalCnt = readInt(2);
		weekNormalCnt = readInt(2);
		weekBusinessCnt = readInt(2);
		weekAppCnt = readInt(2);
		weekPeakCnt = readInt(2);
		weekSuburbCnt = readInt(2);
		
		thisMonthTotalCnt = readInt(2);
		thisMonthNormalCnt = readInt(2);
		thisMonthBusinessCnt = readInt(2);
		thisMonthAppCnt = readInt(2);
		thisMonthPeakCnt = readInt(2);
		thisMonthSuburbCnt = readInt(2);
		
		lastMonthTotalCnt = readInt(2);
		lastMonthNormalCnt = readInt(2);
		lastMonthBusinessCnt = readInt(2);
		lastMonthAppCnt = readInt(2);
		lastMonthPeakCnt = readInt(2);
		lastMonthSuburbCnt = readInt(2);
        
        memo = readString(30);
    }

	@Override
	public String toString() {
		return "콜정산정보 (0x" + Integer.toHexString(messageType) + ") " +
				"corporationCode=" + corporationCode +
				", carId=" + carId +
				", phoneNumber='" + phoneNumber + '\'' +
				", todayTotalCnt=" + todayTotalCnt +
				", todayNormalCnt=" + todayNormalCnt +
				", todayBusinessCnt=" + todayBusinessCnt +
				", todayAppCnt=" + todayAppCnt +
				", todayPeakCnt=" + todayPeakCnt +
				", todaySuburbCnt=" + todaySuburbCnt +
				", weekTotalCnt=" + weekTotalCnt +
				", weekNormalCnt=" + weekNormalCnt +
				", weekBusinessCnt=" + weekBusinessCnt +
				", weekAppCnt=" + weekAppCnt +
				", weekPeakCnt=" + weekPeakCnt +
				", weekSuburbCnt=" + weekSuburbCnt +
				", thisMonthTotalCnt=" + thisMonthTotalCnt +
				", thisMonthNormalCnt=" + thisMonthNormalCnt +
				", thisMonthBusinessCnt=" + thisMonthBusinessCnt +
				", thisMonthAppCnt=" + thisMonthAppCnt +
				", thisMonthPeakCnt=" + thisMonthPeakCnt +
				", thisMonthSuburbCnt=" + thisMonthSuburbCnt +
				", lastMonthTotalCnt=" + lastMonthTotalCnt +
				", lastMonthNormalCnt=" + lastMonthNormalCnt +
				", lastMonthBusinessCnt=" + lastMonthBusinessCnt +
				", lastMonthAppCnt=" + lastMonthAppCnt +
				", lastMonthPeakCnt=" + lastMonthPeakCnt +
				", lastMonthSuburbCnt=" + lastMonthSuburbCnt +
				", memo='" + memo + '\'' +
				'}';
	}
}