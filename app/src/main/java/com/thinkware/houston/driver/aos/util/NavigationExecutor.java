package com.thinkware.houston.driver.aos.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.skt.Tmap.TMapTapi;

public class NavigationExecutor {

	private static NavigationExecutor sInstance;

	public static final String NAVI_TYPE_TMAP = "티맵";
	public static final String NAVI_TYPE_KAKAO_NAVI = "카카오내비";
	public static final String NAVI_TYPE_ONE_NAVI = "원내비";

	private static final String KAKAO_NAVI_PACKAGE_NAGE = "com.locnall.KimGiSa";
	private static final String TMAP_PACKAGE_NAGE = "com.skt.tmap.ku";

	//원내비 LG
	private static final String ONE_NAVI_LG_UPLUS_PACKAGE_NAME = "com.lguplus.navi";
	private static final String ONE_NAVI_LG_UPLUS_ACTION = "com.lguplus.navi.action.NAVI_EXTERNAL_SERVICE";
	private static final String ONE_NAVI_LG_UPLUS_EXTRA_KIND = "com.lguplus.navi.extra.NAVI_EXTRA_KIND";
	private static final String ONE_NAVI_LG_UPLUS_EXTRA_VALUE = "com.lguplus.navi.extra.NAVI_EXTRA_VALUE";
	private static final String ONE_NAVI_LG_UPLUS_EXTRA_ADD_VALUE1 = "com.lguplus.navi.extra.NAVI_EXTRA_ADD_VALUE_1";
	private static final String ONE_NAVI_LG_UPLUS_EXTRA_ADD_VALUE2 = "com.lguplus.navi.extra.NAVI_EXTRA_ADD_VALUE_2";

	//원내비 KT, LG 공통
	private static final String ONE_NAVI_KT_PACKAGE_NAME = "kt.navi";
	private static final String ONE_NAVI_KT_ACTION= "one.navi.action.NAVI_EXTERNAL_SERVICE";
	private static final String ONE_NAVI_KT_EXTRA_KIND = "one.navi.extra.NAVI_EXTRA_KIND";
	private static final String ONE_NAVI_KT_EXTRA_VALUE = "one.navi.extra.NAVI_EXTRA_VALUE";
	private static final String ONE_NAVI_KT_EXTRA_ADD_VALUE_1 = "one.navi.extra.NAVI_EXTRA_ADD_VALUE_1";
	private static final String ONE_NAVI_KT_EXTRA_ADD_VALUE_2 = "one.navi.extra.NAVI_EXTRA_ADD_VALUE_2";
	private static final String ONE_NAVI_KT_CANCEL_NAVIGATION = "one.navi.action.CA";


	public static NavigationExecutor getInstance() {
		if (sInstance == null) {
			sInstance = new NavigationExecutor();
		}
		return sInstance;
	}

	private NavigationExecutor() { }

	public void navigate(Context context, String navigationType, String destinationName, double latitude, double longitude) {
		LogHelper.e("navigationType : " + navigationType
				+ " / destinationName : " + destinationName
				+ " / latitude : " + latitude
				+ " / longitude : " + longitude);
		if (destinationName == null)
			destinationName = "";

		switch (navigationType) {
			case NAVI_TYPE_TMAP:
				navigateTmap(context, destinationName, latitude, longitude);
				break;
			case NAVI_TYPE_KAKAO_NAVI:
				navigateKakaoNavi(context, destinationName, latitude, longitude);
				break;
			case NAVI_TYPE_ONE_NAVI:
				navigateOneNaviCommon(context, destinationName, latitude, longitude);
				break;
		}
	}


	private void navigateTmap(Context context, final String destinationName, final double latitude, final double longitude) {
		LogHelper.e("navigateTmap()");
		TMapTapi tMapTapi = new TMapTapi(context);
		boolean isTmapInstalled = tMapTapi.isTmapApplicationInstalled();
		if (isTmapInstalled) {
			tMapTapi.invokeRoute(destinationName, (float) longitude,(float) latitude);
		} else {
			Toast.makeText(context, "T Map이 설치되어 있지 않습니다. 환경설정에서 설치된 내비를 선택해 주세요.", Toast.LENGTH_SHORT).show();
		}
	}

	private void navigateKakaoNavi(Context context, final String destinationName, final double latitude, final double longitude) {
		LogHelper.e("navigateKakaoNavi()");
		Location destination = Location.newBuilder(destinationName, longitude, latitude)
				.build();
		NaviOptions options = NaviOptions.newBuilder()
				.setCoordType(CoordType.WGS84)
				.build();

		KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination)
				.setNaviOptions(options);
		KakaoNaviParams params = builder.build();
		KakaoNaviService.getInstance().navigate(context, params);
	}


	private void navigateOneNaviCommon(Context context, final String destinationName, final double latitude, final double longitude) {
		boolean isKTOneNaviInstalled = isPackageInstalled(context, ONE_NAVI_KT_PACKAGE_NAME);
		boolean isLGOneNaviInstalled = isPackageInstalled(context, ONE_NAVI_LG_UPLUS_PACKAGE_NAME);
		double[] location = {0, 0, longitude, latitude};

		LogHelper.e("isOneNaviKtInstalled : " + isKTOneNaviInstalled);
		LogHelper.e("isOneNaviLgInstalled : " + isLGOneNaviInstalled);

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		if (isKTOneNaviInstalled) {
			intent.setPackage(ONE_NAVI_KT_PACKAGE_NAME);
			intent.setAction(ONE_NAVI_KT_ACTION);
			intent.putExtra(ONE_NAVI_KT_EXTRA_KIND, "lonlat");
			intent.putExtra(ONE_NAVI_KT_EXTRA_VALUE, location);
			intent.putExtra(ONE_NAVI_KT_EXTRA_ADD_VALUE_2, destinationName);
		} else if (isLGOneNaviInstalled) {
			intent.setPackage(ONE_NAVI_LG_UPLUS_PACKAGE_NAME);
			intent.setAction(ONE_NAVI_LG_UPLUS_ACTION);
			intent.putExtra(ONE_NAVI_LG_UPLUS_EXTRA_KIND, "lonlat");
			intent.putExtra(ONE_NAVI_LG_UPLUS_EXTRA_VALUE, location);
			intent.putExtra(ONE_NAVI_LG_UPLUS_EXTRA_ADD_VALUE2, destinationName);
		} else {
			Toast.makeText(context, "원내비가 설치되어 있지 않습니다. 환경설정에서 설치된 내비를 선택해 주세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		context.sendBroadcast(intent);
	}

	private static boolean isPackageInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean isInstalled = true;
		try {
			pm.getPackageInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			isInstalled = false;
		}
		return isInstalled;
	}

	public static boolean isNavigationInstalled(Context context, String navigationType) {
		String packageName = getNavigationPackageName(context, navigationType);
		return isPackageInstalled(context, packageName);
	}

	public static String getNavigationPackageName(Context context, String navigationType) {
		String operatorName = "";
		String packageName = "";
		TelephonyManager telephonyManager = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE));
		if (telephonyManager != null) {
			operatorName = telephonyManager.getNetworkOperatorName();
//			LogHelper.e("operatorName : " + operatorName); //LG U+, olleh, SKTelecom
		}

		switch (navigationType) {
			case NavigationExecutor.NAVI_TYPE_KAKAO_NAVI:
				packageName = KAKAO_NAVI_PACKAGE_NAGE;
				break;

			case NavigationExecutor.NAVI_TYPE_ONE_NAVI:
				packageName = operatorName.equals("LG U+") ? ONE_NAVI_LG_UPLUS_PACKAGE_NAME : ONE_NAVI_KT_PACKAGE_NAME; //(sk는 kt로 연결되는 것으로 확인함.)
				break;

			case NavigationExecutor.NAVI_TYPE_TMAP:
				packageName = TMAP_PACKAGE_NAGE; //lg, kt
				//packageName = "com.skt.skaf.l001mtm091"; //one스토어 패키지명, 구글스토어는 12년도 티맵으로 연결되며 KT, LG 향으로 표시되어 있음
				break;
		}
		return packageName;
	}

}