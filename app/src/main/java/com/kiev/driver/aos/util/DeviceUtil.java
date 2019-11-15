package com.kiev.driver.aos.util;

import android.content.res.Resources;

public class DeviceUtil {
	public static int convertDpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int convertPxToDp(int px) {
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}
}
