package com.kiev.driver.aos.util;

public class CarNumberConverter {

	private static final int CAR_NUM_AREA_SEOUL = 0x0;
	private static final int CAR_NUM_AREA_BUSAN = 0x1;
	private static final int CAR_NUM_AREA_DAEGU = 0x2;
	private static final int CAR_NUM_AREA_INCHEON = 0x3;
	private static final int CAR_NUM_AREA_GWANGJU = 0x4;
	private static final int CAR_NUM_AREA_DAEJEON = 0x5;
	private static final int CAR_NUM_AREA_ULSAN = 0x6;
	private static final int CAR_NUM_AREA_GYEONGGI = 0x7;
	private static final int CAR_NUM_AREA_GANGWON = 0x8;
	private static final int CAR_NUM_AREA_CHOONGBOOK = 0x9;
	private static final int CAR_NUM_AREA_CHOONGNAM = 0xA;
	private static final int CAR_NUM_AREA_JEONBOOK = 0xB;
	private static final int CAR_NUM_AREA_JEONNAM = 0xC;
	private static final int CAR_NUM_AREA_GYEONGBOOK = 0xD;
	private static final int CAR_NUM_AREA_GYEONGNAM = 0xE;
	private static final int CAR_NUM_AREA_JEJU = 0xF;
	private static final String CAR_NUM_AREA_STR_SEOUL = "서울";
	private static final String CAR_NUM_AREA_STR_BUSAN = "부산";
	private static final String CAR_NUM_AREA_STR_DAEGU = "대구";
	private static final String CAR_NUM_AREA_STR_INCHEON = "인천";
	private static final String CAR_NUM_AREA_STR_GWANGJU = "광주";
	private static final String CAR_NUM_AREA_STR_DAEJEON = "대전";
	private static final String CAR_NUM_AREA_STR_ULSAN = "울산";
	private static final String CAR_NUM_AREA_STR_GYEONGGI = "경기";
	private static final String CAR_NUM_AREA_STR_GANGWON = "강원";
	private static final String CAR_NUM_AREA_STR_CHOONGBOOK = "충북";
	private static final String CAR_NUM_AREA_STR_CHOONGNAM = "충남";
	private static final String CAR_NUM_AREA_STR_JEONBOOK = "전북";
	private static final String CAR_NUM_AREA_STR_JEONNAM = "전남";
	private static final String CAR_NUM_AREA_STR_GYEONGBOOK = "경북";
	private static final String CAR_NUM_AREA_STR_GYEONGNAM = "경남";
	private static final String CAR_NUM_AREA_STR_JEJU = "제주";

	private static final int CAR_NUM_USAGE_BA = 0x1;
	private static final int CAR_NUM_USAGE_SA = 0x2;
	private static final int CAR_NUM_USAGE_A = 0x3;
	private static final int CAR_NUM_USAGE_JA = 0x4;
	private static final String CAR_NUM_USAGE_STR_BA = "바";
	private static final String CAR_NUM_USAGE_STR_SA = "사";
	private static final String CAR_NUM_USAGE_STR_A = "아";
	private static final String CAR_NUM_USAGE_STR_JA = "자";


	public static String getCarIdFromCarNum(String carNum) {
		String carId = "";
		if (!carNum.isEmpty()) {
			String area = convertArea(carNum.substring(0, 2));
			String type = carNum.substring(2, 4);
			String usage = convertUsage(carNum.substring(4, 5));
			String number = "3" + carNum.substring(5, 9);
			carId = area + type + usage + number;
			LogHelper.e("차량 area : " + area);
			LogHelper.e("차량 type : " + type);
			LogHelper.e("차량 usage : " + usage);
			LogHelper.e("차량 number : " + number);
			LogHelper.e("차량 carId : " + carId);
		}

		return carId;
	}

	public static String getCarNumFromCarId(int carId) {
		String carNum = "";
		int prefix = carId / 100000;
		int number = (carId % 100000) - 30000;

		int area = prefix / 1000;
		int type = (prefix % 1000) / 10;
		int usage = prefix % 10;

		carNum = convertArea(area) + type + convertUsage(usage) + number;
		LogHelper.e("차량 prefix : "  + prefix);
		LogHelper.e("차량 number : "  + number);
		LogHelper.e("차량 area : "  + area);
		LogHelper.e("차량 type : "  + type);
		LogHelper.e("차량 usage : "  + usage);
		LogHelper.e("차량 carNum : "  + carNum);

		return carNum;
	}


	private static String convertArea(String area) {
		String result;
		switch (area) {
			case CAR_NUM_AREA_STR_BUSAN:
				result = String.valueOf(CAR_NUM_AREA_BUSAN);
				break;
			case CAR_NUM_AREA_STR_DAEGU:
				result = String.valueOf(CAR_NUM_AREA_DAEGU);
				break;
			case CAR_NUM_AREA_STR_INCHEON:
				result = String.valueOf(CAR_NUM_AREA_INCHEON);
				break;
			case CAR_NUM_AREA_STR_GWANGJU:
				result = String.valueOf(CAR_NUM_AREA_GWANGJU);
				break;
			case CAR_NUM_AREA_STR_DAEJEON:
				result = String.valueOf(CAR_NUM_AREA_DAEJEON);
				break;
			case CAR_NUM_AREA_STR_ULSAN:
				result = String.valueOf(CAR_NUM_AREA_ULSAN);
				break;
			case CAR_NUM_AREA_STR_GYEONGGI:
				result = String.valueOf(CAR_NUM_AREA_GYEONGGI);
				break;
			case CAR_NUM_AREA_STR_GANGWON:
				result = String.valueOf(CAR_NUM_AREA_GANGWON);
				break;
			case CAR_NUM_AREA_STR_CHOONGBOOK:
				result = String.valueOf(CAR_NUM_AREA_CHOONGBOOK);
				break;
			case CAR_NUM_AREA_STR_CHOONGNAM:
				result = String.valueOf(CAR_NUM_AREA_CHOONGNAM);
				break;
			case CAR_NUM_AREA_STR_JEONBOOK:
				result = String.valueOf(CAR_NUM_AREA_JEONBOOK);
				break;
			case CAR_NUM_AREA_STR_JEONNAM:
				result = String.valueOf(CAR_NUM_AREA_JEONNAM);
				break;
			case CAR_NUM_AREA_STR_GYEONGBOOK:
				result = String.valueOf(CAR_NUM_AREA_GYEONGBOOK);
				break;
			case CAR_NUM_AREA_STR_GYEONGNAM:
				result = String.valueOf(CAR_NUM_AREA_GYEONGNAM);
				break;
			case CAR_NUM_AREA_STR_JEJU:
				result = String.valueOf(CAR_NUM_AREA_JEJU);
				break;
			default:
				result = String.valueOf(CAR_NUM_AREA_SEOUL);
				break;

		}
		return result;
	}

	private static String convertArea(int area) {
		String result;
		switch (area) {
			case CAR_NUM_AREA_BUSAN:
				result = CAR_NUM_AREA_STR_BUSAN;
				break;
			case CAR_NUM_AREA_DAEGU:
				result = CAR_NUM_AREA_STR_DAEGU;
				break;
			case CAR_NUM_AREA_INCHEON:
				result = CAR_NUM_AREA_STR_INCHEON;
				break;
			case CAR_NUM_AREA_GWANGJU:
				result = CAR_NUM_AREA_STR_GWANGJU;
				break;
			case CAR_NUM_AREA_DAEJEON:
				result = CAR_NUM_AREA_STR_DAEJEON;
				break;
			case CAR_NUM_AREA_ULSAN:
				result = CAR_NUM_AREA_STR_ULSAN;
				break;
			case CAR_NUM_AREA_GYEONGGI:
				result = CAR_NUM_AREA_STR_GYEONGGI;
				break;
			case CAR_NUM_AREA_GANGWON:
				result = CAR_NUM_AREA_STR_GANGWON;
				break;
			case CAR_NUM_AREA_CHOONGBOOK:
				result = CAR_NUM_AREA_STR_CHOONGBOOK;
				break;
			case CAR_NUM_AREA_CHOONGNAM:
				result = CAR_NUM_AREA_STR_CHOONGNAM;
				break;
			case CAR_NUM_AREA_JEONBOOK:
				result = CAR_NUM_AREA_STR_JEONBOOK;
				break;
			case CAR_NUM_AREA_JEONNAM:
				result = CAR_NUM_AREA_STR_JEONNAM;
				break;
			case CAR_NUM_AREA_GYEONGBOOK:
				result = CAR_NUM_AREA_STR_GYEONGBOOK;
				break;
			case CAR_NUM_AREA_GYEONGNAM:
				result = CAR_NUM_AREA_STR_GYEONGNAM;
				break;
			case CAR_NUM_AREA_JEJU:
				result = CAR_NUM_AREA_STR_JEJU;
				break;
			default:
				result = CAR_NUM_AREA_STR_SEOUL;
				break;

		}
		return result;
	}


	private static String convertUsage(String usage) {
		String result;
		switch (usage) {
			case "사":
				result = String.valueOf(CAR_NUM_USAGE_SA);
				break;
			case "아":
				result = String.valueOf(CAR_NUM_USAGE_A);
				break;
			case "자":
				result = String.valueOf(CAR_NUM_USAGE_JA);
				break;
			default:
				result = String.valueOf(CAR_NUM_USAGE_BA);
				break;
		}
		return result;
	}

	private static String convertUsage(int usage) {
		String result;
		switch (usage) {
			case CAR_NUM_USAGE_SA:
				result = CAR_NUM_USAGE_STR_SA;
				break;
			case CAR_NUM_USAGE_A:
				result = CAR_NUM_USAGE_STR_A;
				break;
			case CAR_NUM_USAGE_JA:
				result = CAR_NUM_USAGE_STR_JA;
				break;
			default:
				result = CAR_NUM_USAGE_STR_BA;
				break;
		}
		return result;
	}
}
