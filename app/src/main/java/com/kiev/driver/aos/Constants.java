package com.kiev.driver.aos;

public class Constants {
	public static final String SP_PERMISSION_CONFIRMED = "sp_permission_confirmed";

	public static final String DIALOG_TAG_FINISH_APP = "dialog_tag_finish_app";
	public static final String DIALOG_TAG_LOGOUT = "dialog_tag_logout";
	public static final String DIALOG_TAG_CANCEL_CALL = "dialog_tag_cancel_call";
	public static final String DIALOG_TAG_CANCEL_CALL_THEN_LOGOUT = "dialog_tag_cancel_call_then_logout";
	public static final String DIALOG_TAG_CANCEL_CALL_THEN_FINISH_APP = "dialog_tag_cancel_call_then_finish_app";
	public static final String DIALOG_TAG_COMPLETE_CALL = "dialog_tag_complete_call";
	public static final String DIALOG_TAG_COMPLETE_CALL_THEN_FINISH_APP = "dialog_tag_complete_call_then_finish_app";
	public static final String DIALOG_TAG_COMPLETE_CALL_THEN_LOGOUT = "dialog_tag_complete_call_then_logout";
	public static final String DIALOG_TAG_SEND_SMS = "dialog_tag_send_sms";


	public static final String DIALOG_TAG_NOTICE = "dialog_tag_notice";
	public static final String DIALOG_TAG_ALLOCATION_FAILURE = "dialog_tag_allocation_failure";
	public static final String DIALOG_TAG_MESSAGE_SELECTION = "dialog_tag_message_selection";
	public static final String DIALOG_TAG_CANCEL_REASON_SELECTION = "dialog_tag_cancel_reason_selection";
	public static final String DIALOG_TAG_ROUTING_TO_DEPARTURE = "dialog_tag_routing_to_departure";
	public static final String DIALOG_TAG_ROUTING_TO_DESTINATION = "dialog_tag_routing_to_destination";
	public static final String DIALOG_TAG_ALIGHTED = "dialog_tag_alighted";
	public static final String DIALOG_TAG_MESSAGE = "dialog_tag_message";
	public static final String DIALOG_TAG_MESSAGE_WILL_DISMISS = "dialog_tag_message_will_dismiss";
	public static final String DIALOG_TAG_MESSAGE_FROM_LIST = "dialog_tag_message_from_list";


	public static final String DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN = "dialog_intent_key_pressed_positive_btn";
	public static final String DIALOG_INTENT_KEY_SELECTED_ITEM = "dialog_intent_key_selected_item";
	public static final String DIALOG_INTENT_KEY_SELECTED_ITEM_INSTALLED = "dialog_intent_key_selected_item_installed";
	public static final String DIALOG_INTENT_KEY_SELECTED_ITEM_ID = "dialog_intent_key_selected_item_id";
	public static final String DIALOG_INTENT_KEY_ITEMS = "dialog_intent_key_checked_items";

	public static final String INTENT_KEY_POPUP_DIALOG = "intent_key_popup_dialog";


	public static final int CALL_STATUS_VACANCY = 1000;
	public static final int CALL_STATUS_VACANCY_IN_WAITING_ZONE = 1100;
	public static final int CALL_STATUS_DRIVING = 2000;     //주행
	public static final int CALL_STATUS_ALLOCATED = 3000;   //배차
	public static final int CALL_STATUS_ALLOCATION_FAILED = 3100;   //배차실패
	public static final int CALL_STATUS_ALLOCATED_WHILE_GETON = 3200;   //승차중배차
	public static final int CALL_STATUS_BOARDED = 4000;    //탑승
	public static final int CALL_STATUS_ALIGHTED = 5000;   //하차
	public static final int CALL_STATUS_BOARDED_WITHOUT_DESTINATION = 4100;
	public static final int CALL_STATUS_WORKING = 8000;
	public static final int CALL_STATUS_RESTING = 9000;

	public static final int FONT_SIZE_INC_DEC_VALUE = 8;
	public static final int FONT_SIZE_SMALL = 0;
	public static final int FONT_SIZE_NORMAL = 1;
	public static final int FONT_SIZE_LARGE= 2;



	//서비스번호는 1 BYTE 로 표현 가능해야 한다. 그러므로 범위는 0~255이다.

	/**
	 *  대기관리 UI 적용 : 서비스번호 ( 0, 5, 6, 100, 11, 12)
	 *  일반 대기 UI 적용 : 위의 서비스 번호 이외의 지역
	 */

	//성남은 개인과 법인이 동일 버전을 쓰므로 대표번호인 0을 쓴다.
	public final static int AREA_SUNGNAM_GEN = 0;       //성남 일반
	public final static int AREA_SUNGNAM_GAEIN = 5;     //성남 개인
	public final static int AREA_SUNGNAM_CORP = 6;      //성남 법인
	public final static int AREA_SUNGNAM_MOBUM = 9;     //성남 모범
	public final static int AREA_SUNGNAM_BOKJI = 22;    //성남 복지

	public final static int AREA_KWANGJU = 3;           //광주

	//하남은 개인과 법인이 동일 버전을 쓰므로 대표번호인 100을 쓴다.
	public final static int AREA_HANAM_GEN = 100;       //하남 일반
	public final static int AREA_HANAM_GAEIN = 11;      //하남 개인
	public final static int AREA_HANAM_CORP = 12;       //하남 법인

	public final static int AREA_ICHON = 13;            //이천


	// TODO: 2019. 3. 11. 프로젝트 구조 잡고, 사이트별 Constants 정의 필요
	public static final String CALL_CENTER_PHONE_NUMBER = "010505569802";
	public static final String SERVER_URL = "houston.thinkware.co.kr:29522";
	public static final String TMAP_API_KEY = "c091d13f-9d61-46c9-b22e-0c09b77334f3";
}