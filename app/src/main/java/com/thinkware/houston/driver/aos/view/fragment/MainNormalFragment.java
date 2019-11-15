package com.thinkware.houston.driver.aos.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinkware.houston.driver.aos.Constants;
import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.FragmentMainBinding;
import com.thinkware.houston.driver.aos.model.Popup;
import com.thinkware.houston.driver.aos.model.entity.Call;
import com.thinkware.houston.driver.aos.model.entity.Configuration;
import com.thinkware.houston.driver.aos.util.LogHelper;
import com.thinkware.houston.driver.aos.util.WavResourcePlayer;
import com.thinkware.houston.driver.aos.view.activity.WaitingCallListActivity;
import com.thinkware.houston.driver.aos.view.activity.WaitingZoneListActivity;
import com.thinkware.houston.driver.aos.viewmodel.MainViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class MainNormalFragment extends BaseFragment implements View.OnClickListener, PopupDialogFragment.PopupDialogListener {

	private MainViewModel mMainViewModel;
	private FragmentMainBinding mBinding;

	public static MainNormalFragment newInstance() {
		return new MainNormalFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {

		mMainViewModel = new ViewModelProvider(this, new MainViewModel.Factory(getActivity().getApplication()))
				.get(MainViewModel.class);
		mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_main, container, false);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mMainViewModel);

		subscribeMainViewModel(mMainViewModel);
		setListeners();

		return mBinding.getRoot();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void subscribeMainViewModel(MainViewModel mainViewModel) {
		mainViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				if (call != null) {
					LogHelper.e("onChanged()-call : " + call.getCallStatus());
					int callStatus = call.getCallStatus();
					switch (callStatus) {
						case Constants.CALL_STATUS_WORKING:
						case Constants.CALL_STATUS_VACANCY:
						case Constants.CALL_STATUS_VACANCY_IN_WAITING_ZONE:
							mBinding.groupVacancy.setVisibility(View.VISIBLE);
							//mBinding.groupBoarding.setVisibility(View.GONE);
							mBinding.groupResting.setVisibility(View.GONE);

							if (callStatus == Constants.CALL_STATUS_VACANCY_IN_WAITING_ZONE) {
								mBinding.btnWaitingZone.setText(call.getWaitingZoneName());
								mBinding.btnWaitingZone.setTextColor(getResources().getColorStateList(R.color.selector_tc_waiting_zone_btn));
							} else {
								mBinding.btnWaitingZone.setText(getString(R.string.main_btn_waiting_zone));
								mBinding.btnWaitingZone.setTextColor(getResources().getColorStateList(R.color.selector_tc_main_middle_btn));
							}
							break;

						/*case Constants.CALL_STATUS_BOARDED:
							mBinding.groupVacancy.setVisibility(View.GONE);
							mBinding.groupBoarding.setVisibility(View.VISIBLE);
							mBinding.groupResting.setVisibility(View.GONE);
							break;*/

						case Constants.CALL_STATUS_RESTING:
							mBinding.groupVacancy.setVisibility(View.GONE);
							//mBinding.groupBoarding.setVisibility(View.GONE);
							mBinding.groupResting.setVisibility(View.VISIBLE);
							break;

						default:
							break;
					}
				}
			}
		});

		mainViewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				LogHelper.e("mainNormal : config : " + configuration.toString());
				float vacancyTextSize = getResources().getDimension(R.dimen.main_status_vacancy_text_size);
				float boardingRestingTextSize = getResources().getDimension(R.dimen.main_status_boarding_resting_text_size);
				mBinding.tvVacancy.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(vacancyTextSize));
				//mBinding.tvBoarding.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(boardingRestingTextSize));
				mBinding.tvResting.setTextSize(COMPLEX_UNIT_DIP, configuration.getFontSizeFromSetting(boardingRestingTextSize));
			}
		});
	}

	private void setListeners() {
		mBinding.btnResting.setOnClickListener(this);
		mBinding.btnReceiveCall.setOnClickListener(this);
		mBinding.btnBoarding.setOnClickListener(this);
		//mBinding.btnAlighting.setOnClickListener(this);

		mBinding.btnWaitingZone.setOnClickListener(this);
		mBinding.btnWaitingCallList.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_resting:
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_RESTING);
				WavResourcePlayer.getInstance(getContext()).play(R.raw.voice_112);
				break;

			case R.id.btn_receive_call:
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_WORKING);
				WavResourcePlayer.getInstance(getContext()).play(R.raw.voice_114);
				break;

			case R.id.btn_boarding:
				mMainViewModel.changeCallStatus(Constants.CALL_STATUS_BOARDED);
				break;

			case R.id.btn_alighting:
				Popup completePopup = new Popup
						.Builder(Popup.TYPE_NO_BTN, Constants.DIALOG_TAG_COMPLETE_CALL)
						.setContent(getString(R.string.alloc_msg_complete))
						.setDismissSecond(3)
						.build();
				showPopupDialog(completePopup);
				break;

			case R.id.btn_waiting_zone:
				WaitingZoneListActivity.startActivity(getActivity());
				break;

			case R.id.btn_waiting_call_list:
				WaitingCallListActivity.startActivity(getActivity());
				break;
		}
	}

	@Override
	public void onDismissPopupDialog(String tag, Intent intent) {
		if (tag.equals(Constants.DIALOG_TAG_COMPLETE_CALL)) {
			mMainViewModel.changeCallStatus(Constants.CALL_STATUS_VACANCY);
		}
	}
}
