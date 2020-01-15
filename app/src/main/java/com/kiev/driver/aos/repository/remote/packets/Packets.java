package com.kiev.driver.aos.repository.remote.packets;

/**
 * Created by zic325 on 2016. 9. 6..
 */
public class Packets {

	//----------------------------------------------------------------------------------------
	// MDT -> Server : 16
	//----------------------------------------------------------------------------------------
	public static final int REQ_ACK = 0xE111; // REQ_ACK
	public static final int REQ_SERVICE = 0x5111; // 서비스요청
	public static final int REQ_NOTICE = 0x5113; // 공지사항요청
	public static final int REQ_CONFIG = 0x5115; // 환경설정요청
	public static final int REQ_PERIOD = 0x5211; // 주기전송
	public static final int REQ_REPORT = 0x5411; // 운행보고

	public static final int REQ_MESSAGE = 0x5811; // 메시지요청
	public static final int REQ_MESSAGE_READ = 0x5815; // 메시지 읽음 확인
	public static final int REQ_SEND_SMS = 0x5813; // SMS 전송 요청

	public static final int REQ_ORDER_REALTIME = 0x5911; // 실시간 위치 및 배차요청
	public static final int REQ_ORDER_INFO = 0x5A21; // 배차정보요청 (목적지 추가)

	public static final int REQ_EMERGENCY = 0x5711; // Emergency 요청
	public static final int REQ_REST = 0x5B11; // 휴식/운행재개
	public static final int REQ_LIVE = 0xE1F1; // Live 패킷

	public static final int REQ_MY_INFO = 0x5E11; // 내 정보 요청
	public static final int REQ_NOTICE_LIST = 0x5137; // 공지사항 리스트 요청

	public static final int REQ_WAIT_AREA_LIST = 0x5521; // 대기장소현황 요청
	public static final int REQ_WAIT_AREA_DECISION = 0x5523; // 대기장소 대기 요청
	public static final int REQ_WAIT_AREA_CANCEL = 0x5515; // 대기취소
	public static final int REQ_WAIT_AREA_ORDER_INFO = 0x5517; // 대기배차고객정보 요청

	public static final int REQ_WAIT_CALL_LIST = 0x5D11; // 대기콜 리스트 요청
	public static final int REQ_WAIT_CALL_ORDER = 0x5913; // 대기콜 배차 요청

	public static final int REQ_STATISTICS = 0x5613; // 운행이력 통계 요청
	public static final int REQ_STATISTICS_DETAIL = 0x5915; // 운행이력 상세 요청





	//----------------------------------------------------------------------------------------
	// Server -> MDT : 19
	//----------------------------------------------------------------------------------------
	public static final int RES_ACK = 0xEF11; // 접속종료 (ACK의 응답 목적으로 사용 한다.)
	public static final int RES_SERVICE = 0x5112; // 서비스요청결과
	public static final int RES_NOTICE = 0x5114; // 공지사항
	public static final int RES_CONFIG = 0x5116; // 환경설정
	public static final int RES_PERIOD = 0x5212; // 주기응답
	public static final int RES_REPORT = 0x5412; // 운행보고응답

	public static final int RES_MESSAGE = 0x1812; // 메시지 응답
	public static final int RES_MESSAGE_NEW = 0x5812; // 메시지 응답
	public static final int RES_SEND_SMS = 0x5814; // SMS 전송 요청 응답

	public static final int RES_ORDER_INFO_BROADCAST = 0x5311; // 배차데이터 (목적지 추가)
	public static final int RES_ORDER_INFO = 0x5A22; // 고객정보재전송 (목적지 추가)
	public static final int RES_ORDER_INFO_PROC = 0x5314; // 배차데이터 처리

	public static final int RES_EMERGENCY_CANCEL = 0x5712; // Emergency 응답
	public static final int RES_REST = 0x5B12; // 휴식/운행응답

	public static final int RES_MY_INFO = 0x5E12; // 내 정보 요청 응답청
	public static final int RES_NOTICE_LIST = 0x5138; // 공지사항 리스트 요청 응답

	public static final int RES_WAIT_AREA_LIST = 0x5522; // 대기장소현황 요청 응답
	public static final int RES_WAIT_AREA_DECISION = 0x5524; // 대기장소 대기 요청 응답
	public static final int RES_WAIT_AREA_CANCEL = 0x5516; // 대기취소응답
	public static final int RES_WAIT_AREA_ORDER_INFO = 0x5518; // 대기배차고객정보

	public static final int RES_WAIT_CALL_LIST = 0x5D12; // 대기콜 리스트 요청 응답
	public static final int RES_WAIT_CALL_ORDER = 0x5914; // 대기콜 배차 요청 응답

	public static final int RES_STATISTICS = 0x5614; // 운행이력 통계 요청 응답
	public static final int RES_STATISTICS_DETAIL = 0x5916; // 운행이력 상세 요청 응답




	//----------------------------------------------------------------------------------------
	// AT Command : 4
	//----------------------------------------------------------------------------------------
	public static final int REQUEST_AT_COMMNAD = 0x9996; // 모뎀 AT Command
	public static final int RESPONSE_AT_COMMAND = 0x9997; // 모뎀 AT Command
	public static final int REQUEST_MODEM_NO = 0x9998; // 모뎀의 번호를 가져오기 위함.
	public static final int RESPONSE_MODEM_NO = 0x9999; // 모뎀의 번호를 가져오기 위함.

	//----------------------------------------------------------------------------------------
	// Enum
	//----------------------------------------------------------------------------------------

	// 개인법인체크
	public enum CorporationType {
		Default(0x00),
		Corporation(0x01), // 법인
		Indivisual(0x02); // 개인

		public int value;

		CorporationType(int value) {
			this.value = value;
		}
	}

	// 승차상태 or 택시상태
	public enum BoardType {
		Empty(0x00), // 빈차
		Boarding(0x01); // 승차중

		public int value;

		BoardType(int value) {
			this.value = value;
		}
	}

	// 휴식상태
	public enum RestType {
		Working(0), // 인증-운행 or 운행재개
		Rest(1), // 휴식 or 휴식요청
		Vacancy(10), // 인증 후 미터기/빈차등 세팅값을 휴식 상태에 올려 보낸다.
		KumHo(20), // 인증 후 미터기/빈차등 세팅값을 휴식 상태에 올려 보낸다.
		Hankook(21), // 인증 후 미터기/빈차등 세팅값을 휴식 상태에 올려 보낸다.
		Kwangshin(22), // 인증 후 미터기/빈차등 세팅값을 휴식 상태에 올려 보낸다.
		VacancyError(30), // 미터기/빈차등 오류 발생시 휴식 패킷에 상태를 올려 보낸다.
		TachoMeterError(31), // 미터기/빈차등 오류 발생시 휴식 패킷에 상태를 올려 보낸다.
		ModemError(40); // 모뎀 오류 발생시 휴식 패킷에 상태를 올려 보낸다.

		public int value;

		RestType(int value) {
			this.value = value;
		}
	}

	// 운행보고구분
	public enum ReportKind {
		GetOn(0x01), // 승차보고
		GetOff(0x02), // 하차보고
		Failed(0x03), // 탑승실패
		GetOnOrder(0x04), // 승차중 배차
		FailedPassengerCancel(0x05), // 탑승실패(손님이 취소)
		FailedNoShow(0x06), // 탑승실패(손님과 통화불가)
		FailedUseAnotherTaxi(0x07), // 탑승실패(손님이 다른 차량 이용)
		FailedEtc(0x08); // 탑승실패(기타 영업 장애)

		public int value;

		ReportKind(int value) {
			this.value = value;
		}
	}

	// 조회요청구분(대분류)
	public enum AccountType {
		Monthly(0x01), // 월별 콜 조회
		Period(0x02), // 기간별 콜 조회
		Daily(0x03); // 일별 콜 조회

		public int value;

		AccountType(int value) {
			this.value = value;
		}
	}

	// Emergency 구분
	public enum EmergencyType {
		Begin(0x01), // 시작
		End(0x02); // 중지

		public int value;

		EmergencyType(int value) {
			this.value = value;
		}
	}

	// 배차결정구분
	public enum OrderDecisionType {
		Request(0x01), // 배차 요청
		Reject(0x02), // 배차 거부
		OutOfDistance(0x03), // 거리 벗어남
		Fail(0x04), // 탑승실패
		Disconnect(0x05), // 접속종료 - 내부적으로 서버에서만 사용
		MultipleOrder(0x14), // 배차가 2개 이상일 때
		AlreadyOrderd(0x0D), // 배차가 1개일 때 - 현재상태가 빈차일 경우 (운행보고 안함 : 콜받고 운행보고 안된상태에서 콜수신된경우)
		Waiting(0x0C), // 대기배차 상태인데 일반콜 수신될 경우
		Driving(0x0A); // 주행중 일반콜 수신될 경우

		public int value;

		OrderDecisionType(int value) {
			this.value = value;
		}
	}

	// 인증결과
	public enum CertificationResult {
		Success(0x01), // 인증성공
		InvalidCar(0x02), // 차량 인증실패
		InvalidContact(0x03), // 연락처인증실패
		DriverPenalty(0x04), // 기사패널티 인증실패
		InvalidHoliday(0x05), // 휴무 조 인증실패
		NoResponse(0x06);

		public int value;

		CertificationResult(int value) {
			this.value = value;
		}
	}

	// 배차구분
	public enum OrderKind {
		Normal(1), // 일반배차(양방향경쟁)
		Wait(3), // 대기배차 (SMS)
		Forced(05), // 강제배차, 지정배차 (복지콜 사용)
		Manual(6), // 수동배차, 지정배차
		WaitOrder(7), // 대기배차 SMS (하남 이외 지역 사용)
		GetOnOrder(10), // 승차중 배차
		WaitOrderTwoWay(11), // 양방향 대기배차, 선택 대기배차 (하남사용)
		Integration(12), //통합배차 추가 2018. 01. 03 - 권석범
		Mobile(15),// 모바일배차
		WaitCall(17),// 대기콜배차
		Temp(99); // Temp(APP에서만 사용)


		public int value;

		OrderKind(int value) {
			this.value = value;
		}
	}

	// 배차데이터 처리 구분
	public enum OrderProcType {
		Display(0x01), // 배차데이터 Display
		Delete(0x02); // 배차데이터 삭제

		public int value;

		OrderProcType(int value) {
			this.value = value;
		}
	}

	// 대기처리 구분
	public enum WaitProcType {
		Success(0x01), // 대기배차등록 성공
		Fail(0x02), // 대기배차등록 실패
		Exist(0x03); // 대기배차 있음

		public int value;

		WaitProcType(int value) {
			this.value = value;
		}
	}

	// 대기취소처리 구분
	public enum WaitCancelType {
		Success(0x01), // 대기취소 성공
		Fail(0x02), // 대기취소 실패
        Exist(0x03), // 대기배차 있음
        AlreadyCancel(0x04); // 이미 취소 처리됨

		public int value;

		WaitCancelType(int value) {
			this.value = value;
		}
	}

	// 대기콜 리스트 구분
	public enum WaitCallListType {
		RequestFirstTime(0x00), // 최초요청
		RequestAgain(0x01); // 재요청

		public int value;

		WaitCallListType(int value) {
			this.value = value;
		}
	}


	// 통계 상세 리스트 조회 구분
	public enum StatisticListType {
		TotalCall(0x01),
		NormalCall(0x02),
		AppCall(0x03),
		BusinessCall(0x04);

		public int value;

		StatisticListType(int value) {
			this.value = value;
		}
	}

	// 통계 상세 조회 기간 구분
	public enum StatisticPeriodType {
		Today(0x01),
		Week(0x02),
		ThisMonth(0x03),
		LastMonth(0x04);

		public int value;

		StatisticPeriodType(int value) {
			this.value = value;
		}
	}


	// 대기 지역 현황 요청 구분
	public enum WaitAreaRequestType {
		Normal(0x00), // 일반 요청
		Waiting(0x01); // 대기 상태일 경우 요청

		public int value;

		WaitAreaRequestType(int value) {
			this.value = value;
		}
	}

	//----------------------------------------------------------------------------------------
	// Packet Size
	//----------------------------------------------------------------------------------------
	public static int getSize(int type) {
		switch (type) {
			case REQ_ACK: // REQ_ACK
				return 13;
			case REQ_SERVICE: // 서비스요청
				return 72;
			case REQ_NOTICE: // 공지사항요청
				return 9;
			case REQ_CONFIG: // 환경설정요청
				return 11;
			case REQ_PERIOD: // 주기전송
				return 86;
			case REQ_REPORT: // 운행보고
				return 133;
			case REQ_WAIT_AREA_CANCEL: // 대기취소
				return 43;
			case REQ_WAIT_AREA_ORDER_INFO: // 대기배차고객정보 요청
				return 9;
			case REQ_EMERGENCY: // Emergency 요청
				return 80;
			case REQ_MESSAGE: // 메시지요청
				return 9;
			case REQ_ORDER_REALTIME: // 실시간 위치 및 배차요청
				return 133;
			case REQ_REST: // 휴식/운행재개
				return 9;
			case REQ_LIVE: // Live 패킷
				return 6;
			case RES_ACK: // 접속종료 (ACK의 응답 목적으로 사용 한다.)
				return 3;
			case RES_SERVICE: // 서비스요청결과
				return 25;
			case RES_NOTICE: // 공지사항
				return 295;
			case RES_CONFIG: // 환경설정
				return 40;
			case RES_PERIOD: // 주기응답
				return 11;
			case RES_ORDER_INFO_BROADCAST: // 배차데이터 (목적지 추가)
				return 359;
			case RES_ORDER_INFO_PROC: // 배차데이터 처리
				return 9;
			case RES_REPORT: // 운행보고응답
				return 9;
			case RES_WAIT_AREA_CANCEL: // 대기취소응답
				return 7;
			case RES_WAIT_AREA_ORDER_INFO: // 대기배차고객정보
				return 354;
			case RES_EMERGENCY_CANCEL: // Emergency 응답
				return 8;
			case RES_MESSAGE: // 메시지 응답
				return 209;
			case RES_MESSAGE_NEW: // 메시지 응답
				return 231;
			case REQ_MESSAGE_READ:
				return 13;
			case RES_ORDER_INFO: // 고객정보 재전송 (목적지 추가)
				return 359;
			case RES_REST: // 휴식/운행응답
				return 7;
			case REQ_ORDER_INFO: // 배차정보 요청 (목적지 추가)
				return 21;

			/*신규 추가 패킷*/
			case REQ_SEND_SMS: // SMS전송 요청
				return 222;
			case RES_SEND_SMS: // SMS전송 요청 응답
				return 22;

			case REQ_MY_INFO: // 내정보 요청
				return 38;
			case RES_MY_INFO: // 내정보 요청 응답
				return 96;

			case REQ_WAIT_CALL_LIST: // 대기콜 리스트 요청
				return 71;
			case RES_WAIT_CALL_LIST: // 대기콜 리스트 요청 응답
				return 1175;

			case REQ_WAIT_CALL_ORDER: // 대기콜 배차 요청
				return 127;
			case RES_WAIT_CALL_ORDER: // 대기콜 배차 요청 응답
				return 356;

			case REQ_NOTICE_LIST:
				return 8;
			case RES_NOTICE_LIST:
				return 1506;

			case REQ_STATISTICS: // 콜 통계 요청
				return 69;
			case RES_STATISTICS: // 콜 통계 요청 응답
				return 116;

			case REQ_STATISTICS_DETAIL: // 콜 통계 상세 요청
				return 42;
			case RES_STATISTICS_DETAIL: // 콜 통계 상세 요청 응답
				return 820;

			case REQ_WAIT_AREA_LIST: // 대기 지역 현황 요청
				return 72;
			case RES_WAIT_AREA_LIST: // 대기 지역 현황 요청 응답
				return 509;

			case REQ_WAIT_AREA_DECISION: // 대기 지역 대기 요청
				return 109;
			case RES_WAIT_AREA_DECISION: // 대기 지역 대기 요청 응답
				return 75;

			default:
				return 0;
		}
	}
}
