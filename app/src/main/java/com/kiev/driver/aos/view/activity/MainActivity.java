package com.kiev.driver.aos.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityMainBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.model.entity.Taxi;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.WavResourcePlayer;
import com.kiev.driver.aos.view.activity.navigationdrawer.CallHistoryActivity;
import com.kiev.driver.aos.view.activity.navigationdrawer.MyInfoActivity;
import com.kiev.driver.aos.view.activity.navigationdrawer.NoticeActivity;
import com.kiev.driver.aos.view.activity.navigationdrawer.SettingActivity;
import com.kiev.driver.aos.view.fragment.MainAllocatedFragment;
import com.kiev.driver.aos.view.fragment.MainNormalFragment;
import com.kiev.driver.aos.view.fragment.PopupDialogFragment;
import com.kiev.driver.aos.viewmodel.MainViewModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends BaseActivity implements View.OnClickListener, PopupDialogFragment.PopupDialogListener {

	private static final String FRAGMENT_TAG_NORMAL = "fragment_tag_vacancy";
	private static final String FRAGMENT_TAG_ALLOCATED = "fragment_tag_allocated";
	private MainViewModel mMainViewModel;
	private ActivityMainBinding mBinding;


	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMainViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mBinding.setLifecycleOwner(this);

		subscribeViewModel(mMainViewModel);

		initToolbar();
		initNavigationDrawer();
		setListeners();
	}


	private void initToolbar() {
		setSupportActionBar(mBinding.mainToolbar.toolbar);
		ActionBar ab = getSupportActionBar();
		if (ab != null)
			ab.setDisplayShowTitleEnabled(false);
		mBinding.mainToolbar.toolbar.setBackgroundColor(Color.TRANSPARENT);

	}

	private void initNavigationDrawer() {
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mBinding.drawerLayout, mBinding.mainToolbar.toolbar, R.string.common_nav_open, R.string.common_nav_close);
		mBinding.drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		mBinding.mainToolbar.toolbar.setNavigationIcon(R.drawable.selector_bg_main_drawer_menu_btn);

		//full width navigation view
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mBinding.navView.getLayoutParams();
		params.width = metrics.widthPixels;
		mBinding.navView.setLayoutParams(params);
	}

	private void setListeners() {
		mBinding.mainToolbar.ibtnActionButton.setOnClickListener(this);
		mBinding.navViewBody.btnMyInfo.setOnClickListener(this);
		mBinding.navViewBody.btnLogout.setOnClickListener(this);
		mBinding.navViewBody.llBtnNotice.setOnClickListener(this);
		mBinding.navViewBody.llBtnMsg.setOnClickListener(this);
		mBinding.navViewBody.llBtnHistory.setOnClickListener(this);
		mBinding.navViewBody.llBtnSetting.setOnClickListener(this);
		mBinding.navViewBody.llBtnCallCenter.setOnClickListener(this);
		mBinding.navViewBody.ibtnNavClose.setOnClickListener(this);
	}

	private void subscribeViewModel(MainViewModel mainViewModel) {
		mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				LogHelper.e("onChanged-Call");
				if (call != null) {
					int callStatus = call.getCallStatus();
					LogHelper.e("onChanged-Call : " + callStatus);
					switch (callStatus) {
						case Constants.CALL_STATUS_DRIVING:
						case Constants.CALL_STATUS_BOARDED:
						case Constants.CALL_STATUS_ALIGHTED:
						case Constants.CALL_STATUS_ALLOCATED:
							setFragments(MainAllocatedFragment.newInstance(), FRAGMENT_TAG_ALLOCATED);
							break;

						case Constants.CALL_STATUS_VACANCY:
						case Constants.CALL_STATUS_WORKING:
							setFragments(MainNormalFragment.newInstance(), FRAGMENT_TAG_NORMAL);
							break;

						default:
							LogHelper.e("onChanged-Call : default : " + callStatus);
							setFragments(MainNormalFragment.newInstance(), FRAGMENT_TAG_NORMAL);
							break;
					}
				}
			}
		});


		// TODO: 2019. 3. 6. 내비게이션드로워 기사 정보 set
		MutableLiveData<ResponseMyInfoPacket> myInfo = mainViewModel.requestMyInfo();
		myInfo.observe(this, new Observer<ResponseMyInfoPacket>() {
			@Override
			public void onChanged(ResponseMyInfoPacket myInfoPacket) {
				LogHelper.e("onChanged()-driver");
				if (myInfoPacket != null) {
					String driverName = myInfoPacket.getDriverName();
					if (driverName != null) {
						String displayDriverName = String.format(getString(R.string.d_menu_driver_name), driverName);
						mBinding.navViewBody.tvDriverName.setText(displayDriverName);
						myInfo.removeObserver(this);
					}
				}
			}
		});

		mainViewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged()-configuration");
				mBinding.navViewBody.tvVehicleNumber.setText(String.valueOf(configuration.getCarId()));
			}
		});


		LiveData<Notice> noticeLiveData = mMainViewModel.getLatestNotice();
		noticeLiveData.observe(this, new Observer<Notice>() {
			@Override
			public void onChanged(Notice notice) {
				noticeLiveData.removeObserver(this);
				if (notice != null && notice.getId() != 0 && notice.isNotice()) {
					LogHelper.e("onChanged-Notice : " + notice.toString());
					Popup popup = new Popup
							.Builder(Popup.TYPE_TWO_BTN_WITH_TITLE, Constants.DIALOG_TAG_NOTICE)
							.setBtnLabel(getString(R.string.common_read_more), getString(R.string.common_close))
							.setTitle(getString(R.string.d_menu_notice))
							.setContent(notice.getTitle() + "\n\n" + notice.getContent())
							.build();
					showPopupDialog(popup);
					WavResourcePlayer.getInstance(getApplication()).play(R.raw.voice_115);
				}
			}
		});
	}


	public void setFragments(Fragment fragment, String tag) {
		LogHelper.e("setFragments()");

		FragmentManager fm = getSupportFragmentManager();
		Fragment currentFragment = fm.findFragmentByTag(tag);
		if (currentFragment == null) {
			LogHelper.e("current fragment is null");
			fm.beginTransaction()
					.replace(R.id.main_fragment_container, fragment, tag)
					.commit();
		} else {
			LogHelper.e("current fragment is not null");
		}
	}


	@Override
	public void onBackPressed() {
		if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
			mBinding.drawerLayout.closeDrawer(GravityCompat.START);
		} else {
			// TODO: 2019. 3. 4. 디바이스의 메인으로 보내고, 조건에 따라 플로팅 서비스 시작해야함.

			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);

			//super.onBackPressed();
		}
	}

	private void showCancelCallPopupDialogWhileAllocated(String dialogTag) {
		ArrayList<SelectionItem> cancelReasonItems = mMainViewModel.getCancelReasonList();
		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_SINGLE_SELECTION, dialogTag)
				.setSelectionItems(cancelReasonItems)
				.setIsHiddenStatusBar(false)
				.build();
		showPopupDialog(popup);
	}

	private void logoutOrFinishApp(boolean isJustFinishApp) {
		mMainViewModel.logoutOrFinishApp(isJustFinishApp);

		if (isJustFinishApp) {
			mMainApplication.closeApplication(this);
		} else {
			LoginActivity.startActivity(MainActivity.this);
			finish();
		}
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("tag : " + tag);
		switch (tag) {
			case Constants.DIALOG_TAG_FINISH_APP:
			case Constants.DIALOG_TAG_LOGOUT:
				LogHelper.e("onDismissPopupDialog 1 : " + tag);
				if (intent != null) {
					boolean isPressedPositiveBtn = intent.getBooleanExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, false);
					if (isPressedPositiveBtn) {
						Call call = mMainViewModel.getCallInformaintion();
						if (call != null) {
							int callStatus = call.getCallStatus();
							LogHelper.e("callStatus : " + callStatus);
							if (callStatus == Constants.CALL_STATUS_ALLOCATED) {
								showCancelCallPopupDialogWhileAllocated(tag.equals(Constants.DIALOG_TAG_FINISH_APP)
										? Constants.DIALOG_TAG_CANCEL_CALL_THEN_FINISH_APP
										: Constants.DIALOG_TAG_CANCEL_CALL_THEN_LOGOUT);

//							} else if (callStatus == Constants.CALL_STATUS_BOARDED
//									|| callStatus == Constants.CALL_STATUS_BOARDED_WITHOUT_DESTINATION) {

							} else {
 								mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);
								logoutOrFinishApp(tag.equals(Constants.DIALOG_TAG_FINISH_APP));
							}
						}
					}
				}
				break;



			case Constants.DIALOG_TAG_CANCEL_CALL_THEN_FINISH_APP:
			case Constants.DIALOG_TAG_CANCEL_CALL_THEN_LOGOUT:
				LogHelper.e("onDismissPopupDialog 2 : " + tag);
				if (intent != null) {
					// TODO: 2019. 3. 4. 탑승실패 사유 서버 전송 처리 필요
					//mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);
					mMainViewModel.requestCancelCall("");
					WavResourcePlayer.getInstance(MainActivity.this).play(R.raw.voice_151);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showFinishOrLogoutDialog(tag.equals(Constants.DIALOG_TAG_CANCEL_CALL_THEN_FINISH_APP)
									? R.id.ibtn_action_button
									: R.id.btn_logout);
						}
					}, 300);
				}

				break;


			case Constants.DIALOG_TAG_NOTICE:
				if (intent != null) {
					boolean isPressedPositiveBtn = intent.getBooleanExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, false);
					if (isPressedPositiveBtn) {
						NoticeActivity.startActivity(MainActivity.this, true);
					}
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_logout:
			case R.id.ibtn_action_button:
				showFinishOrLogoutDialog(view.getId());
				break;

			case R.id.btn_my_info:
				MyInfoActivity.startActivity(MainActivity.this);
				break;

			case R.id.ll_btn_notice:
				NoticeActivity.startActivity(MainActivity.this, true);
				break;

			case R.id.ll_btn_msg:
				NoticeActivity.startActivity(MainActivity.this, false);
				break;

			case R.id.ll_btn_history:
				CallHistoryActivity.startActivity(MainActivity.this);
				break;

			case R.id.ll_btn_setting:
				SettingActivity.startActivity(MainActivity.this);
				break;

			case R.id.ll_btn_call_center:
				mMainViewModel.makePhoneCallToCallCenter(MainActivity.this);
				break;

			case R.id.ibtn_nav_close:
				mBinding.drawerLayout.closeDrawer(Gravity.START, true);
				break;
		}


		if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mBinding.drawerLayout.closeDrawer(GravityCompat.START);
				}
			}, 500);

		}
	}

	private void showFinishOrLogoutDialog(int viewId) {
		boolean isLogout = viewId == R.id.btn_logout;
		String dialogTag = isLogout ? Constants.DIALOG_TAG_LOGOUT : Constants.DIALOG_TAG_FINISH_APP;
		String positiveBtnLabel = getString(isLogout ? R.string.logout_btn : R.string.main_btn_finish);
		String negativeBtnLabel = getString(R.string.common_cancel);

		String action = getString(isLogout ? R.string.alloc_action_logout : R.string.alloc_action_exit);
		String content = getString(isLogout ? R.string.logout_confirm_msg : R.string.main_btn_finish_msg);

		Call call = mMainViewModel.getCallInformaintion();
		//Call call = mMainViewModel.getCallInfo().getValue();
		if (call != null) {
			int callStatus = call.getCallStatus();
			if (callStatus == Constants.CALL_STATUS_ALLOCATED) {
				content = String.format(getString(R.string.alloc_logout_while_allocated), action);
				positiveBtnLabel = getString(R.string.ch_detail_cancel);
				negativeBtnLabel = getString(R.string.common_back);

			} else if (callStatus == Constants.CALL_STATUS_BOARDED
					|| callStatus == Constants.CALL_STATUS_BOARDED_WITHOUT_DESTINATION) {
				content = String.format(getString(R.string.alloc_logout_while_boarded), action);
				positiveBtnLabel = getString(isLogout ? R.string.logout_btn : R.string.main_btn_finish);
				negativeBtnLabel = getString(R.string.common_cancel);
			}

			Popup exitOrLogoutPopup = new Popup
					.Builder(Popup.TYPE_TWO_BTN_NORMAL, dialogTag)
					.setBtnLabel(positiveBtnLabel, negativeBtnLabel)
					.setContent(content)
					.build();
			showPopupDialog(exitOrLogoutPopup);
		}
	}
}
