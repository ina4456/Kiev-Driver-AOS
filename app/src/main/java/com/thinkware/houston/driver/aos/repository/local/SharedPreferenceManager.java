package com.thinkware.houston.driver.aos.repository.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.ResponseWaitDecisionPacket;
import com.thinkware.houston.driver.aos.repository.remote.packets.server2mdt.WaitOrderInfoPacket;
import com.thinkware.houston.driver.aos.util.LogHelper;

import java.lang.reflect.Type;
import java.util.Map;

import androidx.annotation.NonNull;

public class SharedPreferenceManager {
	private static final String LOCAL_DATA = "HOUSTON_SP.LOCAL_DATA";
	private static final String SP_KEY_WAIT_AREA = "wait_area";
	private static final String SP_KEY_CALL_INFO_WAIT = "call_info_wait";
	private static final String SP_KEY_CALL_INFO_TEMP = "call_info_temp";
	private static final String SP_KEY_CALL_INFO_NORMAL = "call_info_normal";
	private static final String SP_KEY_CALL_INFO_GET_ON = "call_info_get_on";



	private SharedPreferences sharedPrefs;
	private static SharedPreferenceManager instance;

	private SharedPreferenceManager(Context context) {
		sharedPrefs = context.getApplicationContext().getSharedPreferences(LOCAL_DATA, Context.MODE_PRIVATE);
	}

	public static synchronized SharedPreferenceManager getInstance(Context context) {
		if (instance == null)
			instance = new SharedPreferenceManager(context);
		return instance;
	}

	public void putBoolean(String key, boolean value) {
		sharedPrefs.edit().putBoolean(key, value).apply();
	}

	public void putInt(String key, int value) {
		sharedPrefs.edit().putInt(key, value).apply();
	}

	public void putLong(String key, long value) {
		sharedPrefs.edit().putLong(key, value).apply();
	}

	public void putString(String key, String value) {
		sharedPrefs.edit().putString(key, value).apply();
	}

	public boolean getBoolean(String key, boolean value) {
		return sharedPrefs.getBoolean(key, value);
	}

	public int getInt(String key, int defValue) {
		return sharedPrefs.getInt(key, defValue);
	}

	public long getLong(String key, long value) {
		return sharedPrefs.getLong(key, value);
	}

	public String getString(String key, String defValue) {
		return sharedPrefs.getString(key, defValue);
	}

	public Map<String, ?> getAll() {
		return sharedPrefs.getAll();
	}

	public void clear() {
		sharedPrefs.edit().clear().apply();
	}

	public void remove(String key) {
		sharedPrefs.edit().remove(key).apply();
	}


	public <T extends Object> void setData(String key, T value) {
		//LogHelper.e("value type : " + (value instanceof Integer));
		if (value instanceof Integer) {
			putInt(key, (Integer) value);
		} else if (value instanceof String) {
			putString(key, (String)value);
		} else if (value instanceof Boolean) {
			putBoolean(key, (Boolean) value);
		} else if (value instanceof Long) {
			putLong(key, (Long)value);
		}
	}

	public <T extends Object> T getData(String key, Class<T> type) {
		//LogHelper.e("type : " + type.getCanonicalName());

		switch (type.getCanonicalName()) {
			case "java.lang.Integer":
				return type.cast(sharedPrefs.getInt(key, -1));
			case "java.lang.String":
				return type.cast(sharedPrefs.getString(key, "-1"));
			case "java.lang.Boolean":
				return type.cast(sharedPrefs.getBoolean(key, false));
			case "java.lang.Long":
				return type.cast(sharedPrefs.getLong(key, -1));
			default:
				return null;
		}
	}

	private void setDataAsJson(String key, String json) {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(key, json);
		editor.apply();
	}

	private <T extends  Object> T getDataAsJson(String key, Class<T> type) {
		Gson gson = new Gson();
		String json = getString(key, null);
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		return gson.fromJson(json, (Type)type);
	}

	public void clearData(String key) {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.remove(key);
		editor.apply();
	}


	/**
	 * ===================================================================
	 * 대기상태 저장
	 */
	public void setWaitArea(@NonNull ResponseWaitDecisionPacket packet) {
		LogHelper.write("==> [대기상태 저장] : " + packet);
		setDataAsJson(SP_KEY_WAIT_AREA, new Gson().toJson(packet));
	}
	/**
	 * @return 대기상태
	 */
	public ResponseWaitDecisionPacket getWaitArea() {
		return getDataAsJson(SP_KEY_WAIT_AREA, ResponseWaitDecisionPacket.class);
	}
	/**
	 * 대기상태 삭제
	 */
	public void clearWaitArea() {
		LogHelper.write("==> [대기상태 삭제]");
		clearData(SP_KEY_WAIT_AREA);
	}


	/**
	 * ===================================================================
	 * 대기배차 정보 저장
	 */
	public void setWaitOrderInfo(@NonNull WaitOrderInfoPacket packet) {
		LogHelper.write("==> [대기배차 정보 저장] : " + packet);
		setDataAsJson(SP_KEY_CALL_INFO_WAIT, new Gson().toJson(packet));
	}
	/**
	 * @return 대기배차 정보
	 */
	public WaitOrderInfoPacket getWaitOrderInfo() {
		return getDataAsJson(SP_KEY_CALL_INFO_WAIT, WaitOrderInfoPacket.class);
	}
	/**
	 * 대기배차 정보 삭제
	 */
	public void clearWaitOrderInfo() {
		LogHelper.write("==> [대기배차 정보 삭제]");
		clearData(SP_KEY_CALL_INFO_WAIT);
	}


	/**
	 * ===================================================================
	 * 임시배차 정보 저장
	 */
	public void setTempCallInfo(@NonNull OrderInfoPacket packet) {
		LogHelper.write("==> [임시배차 정보 저장] : " + packet);
		setDataAsJson(SP_KEY_CALL_INFO_TEMP, new Gson().toJson(packet));
	}
	/**
	 * @return 임시배차 정보
	 */
	public OrderInfoPacket getTempCallInfo() {
		return getDataAsJson(SP_KEY_CALL_INFO_TEMP, OrderInfoPacket.class);
	}
	/**
	 * 임시배차 정보 삭제
	 */
	public void clearTempCallInfo() {
		LogHelper.write("==> [임시배차 정보 삭제]");
		clearData(SP_KEY_CALL_INFO_TEMP);
	}


	/**
	 * ===================================================================
	 * 일반배차 정보 저장
	 */
	public void setNormalCallInfo(@NonNull OrderInfoPacket packet) {
		LogHelper.write("==> [배차1 정보 저장] : " + packet);
		setDataAsJson(SP_KEY_CALL_INFO_NORMAL, new Gson().toJson(packet));
	}

	/**
	 * @return 일반배차 정보
	 */
	public OrderInfoPacket getNormalCallInfo() {
		return getDataAsJson(SP_KEY_CALL_INFO_NORMAL, OrderInfoPacket.class);
	}

	/**
	 * 일반배차 정보 삭제
	 */
	public void clearNormalCallInfo() {
		LogHelper.write("==> [배차1 정보 삭제]");
		clearData(SP_KEY_CALL_INFO_NORMAL);
	}


	/**
	 * ===================================================================
	 * 승차중배차 정보 저장
	 */
	public void setGetOnCallInfo(@NonNull OrderInfoPacket packet) {
		LogHelper.write("==> [배차2 정보 저장] : " + packet);
		setDataAsJson(SP_KEY_CALL_INFO_GET_ON, new Gson().toJson(packet));
	}

	/**
	 * @return 저장된 승차중배차 정보
	 */
	public OrderInfoPacket getGetOnCallInfo() {
		return getDataAsJson(SP_KEY_CALL_INFO_GET_ON, OrderInfoPacket.class);
	}

	/**
	 * 승차중배차 정보 삭제
	 */
	public void clearGetOnCallInfo() {
		LogHelper.write("==> [배차2 정보 삭제]");
		clearData(SP_KEY_CALL_INFO_GET_ON);
	}
}
