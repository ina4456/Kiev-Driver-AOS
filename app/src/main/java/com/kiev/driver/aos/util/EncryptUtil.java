package com.kiev.driver.aos.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class EncryptUtil {

	public static String decodeStr(String inpStr) {

		String oupStr = "";
		try {
			String tmpStr = "";
			for (int idx = 0; idx < inpStr.length(); idx++) {
				tmpStr += inpStr.substring(inpStr.length() - (idx + 1), inpStr.length() - idx);
			}

			int size = tmpStr.length() / 2;
			byte[] byteStr = new byte[size];
			for (int idx = 0; idx < size; idx++) {
				byteStr[idx] = (byte) Integer.parseInt(tmpStr.substring((idx * 2), (idx * 2) + 2), 16);
			}

			oupStr = new String(byteStr, Charset.forName("euc-kr"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return oupStr;
	}


	public static String encodeStr(String inpStr) {

		String oupStr = "";
		try {
			String tmpStr = "";
			byte[] tmpByte = inpStr.getBytes(Charset.forName("EUC-KR"));

			for (int idx = 0; idx < tmpByte.length; idx++) {
				tmpStr += String.format("%x", (tmpByte[idx]));
			}

			for (int idx = 0; idx < tmpStr.length(); idx++) {
				oupStr += tmpStr.substring(tmpStr.length() - (idx + 1), tmpStr.length() - idx);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return oupStr;
	}


	public void checkEncDec() {

		String[] tmpStr = {"1234567", "가나다라마바", "!@#$%", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz"};

		for (int idx = 0; idx < tmpStr.length; idx++) {

			System.out.println(tmpStr[idx] + " >>Encode>> " + encodeStr(tmpStr[idx]) + " >>Decode>> " + decodeStr(encodeStr(tmpStr[idx])));

		}
	}



	public static String encodeStrUTF8(String str) {
		try {
			LogHelper.e("encodeStrUTF8 () " + str);
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
}
