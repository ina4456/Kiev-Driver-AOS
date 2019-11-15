package com.thinkware.houston.driver.aos.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.MainApplication;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.FragmentMainAllocatedBinding;
import com.thinkware.houston.driver.aos.model.Popup;
import com.thinkware.houston.driver.aos.model.SelectionItem;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.util.WavResourcePlayer;
import com.thinkware.houston.driver.aos.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class MainAllocatedFragment extends BaseFragment implements View.OnClickListener, PopupDialogFragment.PopupDialogListener {

	private MainViewModel mMainViewModel;
	private FragmentMainAllocatedBinding mBinding;
	private static final String DIALOG_TAG_SEND_SMS = "dialog_tag_send_sms";
	private static final String DIALOG_TAG_NO_NEED_RESPONSE = "dialog_tag_no_need_response";
	private static final String DIALOG_TAG_INSTALL_NAVI = "dialog_tag_install_navi";
	private static final String DIALOG_TAG_CANCEL_ROUTING = "dialog_tag_cancel_routing";

	public static MainAllocatedFragment newInstance() {
		MainAllocatedFragment fragment = new MainAllocatedFragment();
		return fragment;
	}

	//ConstraintLayout의 Group을 사용할 경우 개별적인 setVisibility가 적용되지 않음.
	private ArrayList<View> groupDepartureAndDestination = new ArrayList<>();
	private ArrayList<View> groupButtonsBeforeBoarding = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LogHelper.e("onCreateView()");
		mMainViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getActivity().getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_main_allocated, container, false);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mMainViewModel);

		groupDepartureAndDestination.add(mBinding.tvHere);
		groupDepartureAndDestination.add(mBinding.tvTargetPoi);
		groupDepartureAndDestination.add(mBinding.tvTargetSubPoi);
		groupDepartureAndDestination.add(mBinding.btnRouting);
		groupDepartureAndDestination.add(mBinding.viewDividerDepDes);
		groupDepartureAndDestination.add(mBinding.tvPrevNextTargetType);
		groupDepartureAndDestination.add(mBinding.tvPrevOrNextTarget);

		groupButtonsBeforeBoarding.add(mBinding.btnBoarding);
		groupButtonsBeforeBoarding.add(mBinding.btnCancelBoarding);
		groupButtonsBeforeBoarding.add(mBinding.btnCall);
		groupButtonsBeforeBoarding.add(mBinding.btnMessage);


		setVisibilityGroup(groupDepartureAndDestination, View.GONE);
		setVisibilityGroup(groupButtonsBeforeBoarding, View.GONE);
		mBinding.btnAlighting.setVisibility(View.GONE);
		mBinding.tvBoardingWithoutDestination.setVisibility(View.GONE);

		subscribeViewModel(mMainViewModel);
		setListeners();

		return mBinding.getRoot();
	}

	private void subscribeViewModel(MainViewModel mainViewModel) {
		// TODO: 2019. 3. 6. 콜 상태 변경에 따른 화면 분기 처리

		mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {

				if (isAdded() && call != null) {
					LogHelper.e("onChanged()-call : " + call.toString());
					MainAllocatedFragment.super.finishLoadingProgress();
					int callStatus = call.getCallStatus();
					switch (callStatus) {
						case Constants.CALL_STATUS_ALLOCATED:
						case Constants.CALL_STATUS_BOARDED:
							if (call.getCallNumber() != 0) {
								setViewsWithCallInfo(call);
							} else {
								setViewsWithCallInfo(null);
							}
							break;

						case Constants.CALL_STATUS_ALIGHTED:
							Popup popup = new Popup.Builder(Popup.TYPE_ONE_BTN_NORMAL, Constants.DIALOG_TAG_ALIGHTED)
									.setContent(getString(R.string.alloc_msg_complete))
									.setDismissSecond(3)
									.build();
							showPopupDialog(popup);
							break;

						default:
							break;
					}
				}
			}
		});

		LiveData<Configuration> config = mainViewModel.getConfiguration();
		config.observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("onChanged()-Configuration");
				float poiNameTextSize = getResources().getDimension(R.dimen.main_poi_name_text_size);
				float subPoiNameTextSize = getResources().getDimension(R.dimen.main_sub_poi_name_text_size);
				float boardingRestingTextSize = getResources().getDimension(R.dimen.main_status_boarding_resting_text_size);

				mBinding.tvTargetPoi.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(poiNameTextSize));
				mBinding.tvTargetSubPoi.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(subPoiNameTextSize));
				mBinding.tvBoardingWithoutDestination.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(boardingRestingTextSize));

			}
		});
	}


	private void setListeners() {
		mBinding.btnCall.setOnClickListener(this);
		mBinding.btnMessage.setOnClickListener(this);

		mBinding.btnRouting.setOnClickListener(this);
		mBinding.btnCancelBoarding.setOnClickListener(this);
		mBinding.btnBoarding.setOnClickListener(this);
		mBinding.btnAlighting.setOnClickListener(this);
	}

	/**
	 * 출발지, 목적지 정보 표시
	 * 탑승전 : 거리, 출발지(poi, addr), 길안내, 메시지, 전화, 운행취소, 손님탑승 버튼 표시
	 * 탑승후 : 목적지(poi, addr), 길안내 버튼 표시(좌표값 유무에 따름), 손님하차 버튼 표시
	 */
	private void setViewsWithCallInfo(Call callInfo) {
		LogHelper.e("setViewsWithCallInfo() : ");

//		mBinding.btnCall.setEnabled(true);
//		mBinding.btnCancelBoarding.setEnabled(true);
//		mBinding.btnRouting.setEnabled(true);

		if (callInfo != null) {
			LogHelper.i("allocationInfo not null ");
			boolean isBoarded = callInfo.getCallStatus() == Constants.CALL_STATUS_BOARDED;
			setVisibilityGroup(groupDepartureAndDestination, View.VISIBLE);
			setDataToView(callInfo);
			setEnabledRouteBtn(isBoarded, callInfo);
			mBinding.tvBoardingWithoutDestination.setVisibility(View.GONE);

			LogHelper.i("isPassengerBoarded : " + isBoarded);
			//탑승전
			if (!isBoarded) {
				LogHelper.i("탑승 전");
				setVisibilityGroup(groupButtonsBeforeBoarding, View.VISIBLE);
				mBinding.tvCallState.setText(getString(R.string.alloc_step_allocated_call));
				mBinding.btnAlighting.setVisibility(View.GONE);
				mBinding.btnRouting.setBackgroundResource(R.drawable.selector_bg_route_passenger_btn);
				WavResourcePlayer.getInstance(getContext()).play(R.raw.voice_120);

				//탑승후
			} else {
				LogHelper.i("탑승 후");
				setVisibilityGroup(groupButtonsBeforeBoarding, View.GONE);
				mBinding.tvCallState.setText(getString(R.string.alloc_step_boarded));
				mBinding.btnAlighting.setVisibility(View.VISIBLE);
				mBinding.btnRouting.setBackgroundResource(R.drawable.selector_bg_route_destination_btn);

				LogHelper.e("visibility : " + mBinding.btnMessage.getVisibility());
			}

			//조건
			//1. 출발지 OR 목적지의 좌표값이 정상이며 길안내 버튼이 활성화되었을 경우
			//2. 배차가 막 되었을 경우 (승차중배차 수신 화면을 보여주고, 그 뒤에 다시 화면에 진입한 경우 제외)
			//수신 직후에 데이터가 저장되는지 ?? 또는 수락을 눌러야만 저장되는지 ?? 확인필요
			LogHelper.e("hasGetOnCall : " + mMainViewModel.hasGetOnCall());
			if (mBinding.btnRouting.isEnabled() && !mMainViewModel.hasGetOnCall())
				showRoutingPopup(isBoarded);


		} else {
			LogHelper.i("allocationInfo null ");
			//배차 정보가 없다면, UI나 미터기에서 주행을 누른것으로 간주하고, 손님 탑승중인 텍스트와 하차 버튼 표시함
			mBinding.tvCallState.setText(getString(R.string.alloc_step_boarded));
			setVisibilityGroup(groupDepartureAndDestination, View.GONE);
			setVisibilityGroup(groupButtonsBeforeBoarding, View.GONE);
			mBinding.btnAlighting.setVisibility(View.VISIBLE);
			mBinding.tvBoardingWithoutDestination.setVisibility(View.VISIBLE);
			//mScenarioService.requestChangeStatus(Constants.INTENT_VALUE_CHANGE_STATUS_BOARDED);

		}
	}

	private void setVisibilityGroup(ArrayList<View> group, int visibility) {
		for (View view : group) {
			view.setVisibility(visibility);
		}
	}

	private void setDataToView(Call call) {
		LogHelper.i("setDataToView() : " + call.toString());

		//탑승 여부에 따른 출발 또는 도착 지 설정
		boolean isBoarded = call.getCallStatus() == Constants.CALL_STATUS_BOARDED;
		String poi = !isBoarded ? call.getDeparturePoi() : call.getDestinationPoi();
		String address = !isBoarded ? call.getDepartureAddr() : call.getDestinationAddr();
		String nextOrPrevPoi = !isBoarded ? call.getDestinationPoi() : call.getDeparturePoi();
		String nextOrPrevAddress = !isBoarded ? call.getDestinationAddr() : call.getDepartureAddr();

		LogHelper.i("setDataToView() : " + poi + ", " + address + ", " + nextOrPrevPoi + ", " + nextOrPrevAddress);

		mBinding.tvTargetPoi.setText("");
		mBinding.tvTargetSubPoi.setText("");
		mBinding.tvPrevOrNextTarget.setText("");

		setCurrentTargetView(isBoarded, poi, address);
		setNextOrPrevTargetView(isBoarded, nextOrPrevPoi, nextOrPrevAddress);
	}

	//현재 목적지 정보 설정
	private void setCurrentTargetView(boolean isBoarded, String poi, String address) {
		String currentTargetLabel = !isBoarded ? getString(R.string.alloc_passenger_location)
				: getString(R.string.alloc_destination);
		mBinding.tvHere.setText(currentTargetLabel);

		if (poi != null && !poi.isEmpty() && address != null && !address.isEmpty()) {
			mBinding.tvTargetPoi.setText(poi);
			mBinding.tvTargetSubPoi.setText(address);
		} else if ((poi == null || poi.isEmpty()) && (address != null && !address.isEmpty())) {
			mBinding.tvTargetPoi.setText(address);
			mBinding.tvTargetSubPoi.setVisibility(View.GONE);
		} else if ((poi != null && !poi.isEmpty()) && (address == null || address.isEmpty())) {
			mBinding.tvTargetPoi.setText(poi);
			mBinding.tvTargetSubPoi.setVisibility(View.GONE);
		} else {
			mBinding.tvTargetPoi.setText(getString(!isBoarded
					? R.string.alloc_no_departure
					: R.string.alloc_no_destination));
			mBinding.tvTargetSubPoi.setVisibility(View.GONE);
		}
	}


	//하단 표시 다음 또는 이전 목적지 정보 설정
	private void setNextOrPrevTargetView(boolean isBoarded, String nextOrPrevPoi, String nextOrPrevAddress) {
		String nextOrPrevTargetLabel = !isBoarded ? getString(R.string.alloc_destination)
				: getString(R.string.alloc_departure);
		mBinding.tvPrevNextTargetType.setText(nextOrPrevTargetLabel);

		if ((nextOrPrevPoi == null || nextOrPrevPoi.isEmpty())
				&& (nextOrPrevAddress != null && !nextOrPrevAddress.isEmpty())) {
			nextOrPrevPoi = nextOrPrevAddress;
		}
		if (nextOrPrevPoi == null || nextOrPrevPoi.isEmpty()) {
			mBinding.tvPrevOrNextTarget.setText(getString(!isBoarded
					? R.string.alloc_no_destination
					: R.string.alloc_no_departure));
		} else {
			mBinding.tvPrevOrNextTarget.setText(nextOrPrevPoi);
		}
	}

	private void setEnabledRouteBtn(boolean isBoarded, Call callInfo) {
		double latitude = !isBoarded ? callInfo.getDepartureLat() : callInfo.getDestinationLat();
		double longitude = !isBoarded ? callInfo.getDepartureLong() : callInfo.getDestinationLong();

		if (latitude != 0.0 && longitude != 0.0) {
			mBinding.btnRouting.setEnabled(true);
		} else {
			mBinding.btnRouting.setEnabled(false);
		}
	}



	@Override
	public void onClick(View view) {
		LogHelper.e("onClick()");
		Configuration configuration = mMainViewModel.getConfiguration().getValue();

		switch (view.getId()) {
			case R.id.btn_routing:
				if (configuration != null) {
					String navigation = configuration.getNavigation();
					if (mMainViewModel.isNavigationInstalled(getContext(), navigation)) {
						WavResourcePlayer.getInstance(getContext()).play(R.raw.voice_128);
						mMainViewModel.executeNavigation(getContext());
					} else {
						LogHelper.e("설정에서 선택된 내비 설치되어 있지 않음. - 설치 팝업 표출");
						// TODO: 2019. 3. 11. 설정창에 설치된 다른 내비게이션을 선택하라는 문구 필요할 듯.
						showSelectionNaviInstallPopup();
					}
				}
				break;

			case R.id.btn_call:
				mMainViewModel.makePhoneCallToPassenger(getContext());
				break;

			case R.id.btn_message:
				showSelectionSendMsgPopup();
				break;

			case R.id.btn_cancel_boarding:
				showSelectionCancelReasonPopup();
				break;

			case R.id.btn_boarding:
				super.startLoadingProgress();
				LogHelper.e("탑승");
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_BOARDED);


				// TODO: 2019. 3. 11. 서버로 탑승 상태 전송
				/*Call call = mMainViewModel.getCallInfo().getValue();
				if (call != null
						&& call.getDestinationLat() != 0d
						&& call.getDestinationLong() != 0d) {
					LogHelper.e("목적지 좌표 있음");

					LogHelper.e("isUseAutoRouteToDestination() : " + configuration.isUseAutoRouteToDestination());
					boolean isNaviInstalled = mMainViewModel.isNavigationInstalled(getContext(), configuration.getNavigation());
					if (configuration.isUseAutoRouteToDestination() && isNaviInstalled) {
						//showAutoRouteToDestinationPopup();
						showRoutingPopup(false);
					} else {
						mMainViewModel.changeCallStatus(Constants.CALL_STATUS_BOARDED);
					}

					mMainViewModel.changeCallStatus(Constants.CALL_STATUS_BOARDED);


				} else {
					LogHelper.e("목적지 정보 없음");
					mMainViewModel.changeCallStatus(Constants.CALL_STATUS_BOARDED_WITHOUT_DESTINATION);
					// TODO: 2019. 3. 11.  목적지 좌표가 잘못된 경우에 대한 예외 처리 필요
				}*/


				break;

			case R.id.btn_alighting:
				super.startLoadingProgress();
				LogHelper.e("하차");
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);

				//showNormalMsgPopup(Constants.DIALOG_TAG_COMPLETE_CALL, getString(R.string.alloc_msg_complete));

				break;
		}
	}

	private void showNormalMsgPopup(String tag, String content) {
		Popup completePopup = new Popup
				.Builder(Popup.TYPE_NO_BTN, tag)
				.setDismissSecond(2)
				.setContent(content)
				.build();
		showPopupDialog(completePopup);
	}

	private void showAutoRouteToDestinationPopup() {
		Popup autoRoutingToDestinationPopup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, Constants.DIALOG_TAG_ROUTING_TO_DESTINATION)
				.setContent(getString(R.string.alloc_routing_to_destination))
				.setDismissSecond(3)
				.build();
		showPopupDialog(autoRoutingToDestinationPopup);
	}

	private void showRoutingPopup(boolean isBoarded) {
//		FragmentManager fm = getFragmentManager();
//		PopupDialogFragment myFragment = (PopupDialogFragment)fm.findFragmentByTag(Constants.DIALOG_TAG_ALIGHTED);
//		LogHelper.e("fragment : " + (myFragment != null));
//		if (myFragment != null && myFragment.isVisible()) {
//			LogHelper.e("fragment : " + myFragment.isVisible() );
//
//			LogHelper.e("close fragment");
//			fm.beginTransaction().remove(myFragment);
//		}


		boolean isUseAutoRoutingToTarget = mMainViewModel.isUseAutoRoutingToTarget(!isBoarded);
		if (isUseAutoRoutingToTarget) {
			String dialogTag = !isBoarded ? Constants.DIALOG_TAG_ROUTING_TO_DEPARTURE
					: Constants.DIALOG_TAG_ROUTING_TO_DESTINATION;
			String content = !isBoarded ? getString(R.string.alloc_routing_to_passenger)
					: getString(R.string.alloc_routing_to_destination);


			Popup popup = new Popup.Builder(Popup.TYPE_ONE_BTN_NORMAL, dialogTag)
					.setContent(content)
					.setDismissSecond(3)
					.build();
			showPopupDialog(popup);
		}
	}


	private void showSelectionSendMsgPopup() {
		ArrayList<SelectionItem> selectionItems = mMainViewModel.getMessageList();
		showSelectionPopup(selectionItems, Popup.TYPE_SMS, DIALOG_TAG_SEND_SMS);
	}

	private void showSelectionCancelReasonPopup() {
		ArrayList<SelectionItem> selectionItems = mMainViewModel.getCancelReasonList();
		showSelectionPopup(selectionItems, Popup.TYPE_ONE_BTN_SINGLE_SELECTION, Constants.DIALOG_TAG_CANCEL_REASON_SELECTION);
	}

	private void showSelectionPopup(ArrayList<SelectionItem> selectionItems, int popupType, String popupTag) {
		Popup cancelPopup = new Popup
				.Builder(popupType, popupTag)
				.setSelectionItems(selectionItems)
				.build();
		showPopupDialog(cancelPopup);
	}

	private void showSelectionNaviInstallPopup() {
		ArrayList<SelectionItem> selectionItems = new ArrayList<>();
		List<String> msgList = Arrays.asList(getResources().getStringArray(R.array.setting_navigation_list));
		for (String s: msgList) {
			SelectionItem selectionItem = new SelectionItem();
			selectionItem.setItemContent(s);
			selectionItems.add(selectionItem);
		}

		Popup popup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NAVI_INSTALL, DIALOG_TAG_INSTALL_NAVI)
				.setBtnLabel(null, getString(R.string.common_cancel))
				.setSelectionItems(selectionItems)
				.build();
		showPopupDialog(popup);
	}

	private void showCancelRoutingPopup() {
		Popup cancelRoutingPopup = new Popup
				.Builder(Popup.TYPE_ONE_BTN_NORMAL, DIALOG_TAG_CANCEL_ROUTING)
				.setContent(getString(R.string.setting_navigation_not_installed_msg))
				.build();
		showPopupDialog(cancelRoutingPopup);
	}


	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		LogHelper.e("onDismissPopupDialog()... tag : " + tag);
		super.finishLoadingProgress();

		switch (tag) {
			//콜 완료
			case Constants.DIALOG_TAG_COMPLETE_CALL:
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);
				break;

			case Constants.DIALOG_TAG_CANCEL_REASON_SELECTION:
				if (intent != null) {
					//탑승 실패 처리
					String cancelReason = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);
					LogHelper.e("cancelResason : " + cancelReason);
					if (cancelReason != null) {
						// TODO: 2019-09-09 인솔라인 탑승실패 요청 패킷 확정 시 수정 필요
						/*String reason = INTENT_VALUE_TAG_CANCEL_CUSTOMER_CANCEL;
						if (reason.equals(getString(R.string.allocation_cancel_reason_using_another_taxi))) {
							reason = INTENT_VALUE_TAG_CANCEL_USING_ANOTHER_TAXI;
						} else if (reason.equals(getString(R.string.allocation_cancel_reason_etc))) {
							reason = INTENT_VALUE_TAG_CANCEL_ETC;
						} else if (reason.equals(getString(R.string.allocation_cancel_reason_not_available_call))) {
							reason = INTENT_VALUE_TAG_CANCEL_NOT_AVAILABLE_CALL;
						}*/

						mMainViewModel.requestCancelCall(cancelReason);
					}
				}
				break;


			/*//콜 취소
			case Constants.DIALOG_TAG_CANCEL_CALL:
				// TODO: 2019. 3. 4. 탑승실패 서버 전송 처리 추가 필요
				if (intent != null) {
					WavResourcePlayer.getInstance(getActivity()).play(R.raw.voice_151);
					String selectedItem = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);
					LogHelper.e("selectedItem : " + selectedItem);
					showNormalMsgPopup(Constants.DIALOG_TAG_CANCEL_REASON_SELECTION, getString(R.string.alloc_msg_complete));
				}

				break;*/

			//목적지로 자동 길안내

			case Constants.DIALOG_TAG_ROUTING_TO_DEPARTURE:
			case Constants.DIALOG_TAG_ROUTING_TO_DESTINATION:
				WavResourcePlayer.getInstance(getContext()).play(R.raw.voice_128);
				mMainViewModel.executeNavigation(getContext());
				break;

			case Constants.DIALOG_TAG_ALIGHTED:
//				mRepository.resetCallInfo();
				//승차중 배차 정보가 있다면, UI를 완료처리하지 않음.
				LogHelper.e("mMainViewModel.hasNormalCall() : " + mMainViewModel.hasNormalCall());
				if (!mMainViewModel.hasNormalCall()) {
					boolean wasBackground = ((MainApplication)getActivity().getApplication()).wasBackground();
					LogHelper.e("wasBackground : " + wasBackground);
					if(wasBackground) {
						getActivity().moveTaskToBack(true);
					}
					mMainViewModel.resetCallInfoForUi();
				}

				//mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);
				break;

			//배차 메시지 전송
			case DIALOG_TAG_SEND_SMS:
				// TODO: 2019. 3. 8. 서버 SMS 전송 처리 및 실패 성공 여부에 따라 팝업창 표시
				if (intent != null) {
					String selectedItem = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);
					selectedItem = selectedItem.replaceAll("\\<[^>]*>","");

					LogHelper.e("selectedItem : " + selectedItem);
					mMainViewModel.requestToSendSMS(selectedItem);

//
//					String selectedItem = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);
//					LogHelper.e("selectedItem : " + selectedItem);
//					showNormalMsgPopup(DIALOG_TAG_NO_NEED_RESPONSE, getString(R.string.msg_send_succeed));
				}
				//showNormalMsgPopup(DIALOG_TAG_NO_NEED_RESPONSE, getString(R.string.msg_send_failure));
				break;

			//내비 설치
			case DIALOG_TAG_INSTALL_NAVI:
				if (intent != null) {
					String selectedNavi = intent.getStringExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM);

					String appPackageName = mMainViewModel.getNavigationPackageName(getContext(), selectedNavi);
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
					}
				} else {
					showCancelRoutingPopup();
				}
				break;

			default:
				break;
		}
	}
}
