package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityMyinfoBinding;
import com.kiev.driver.aos.model.entity.Call;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.viewmodel.MyInfoViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

	private MyInfoViewModel mViewModel;
	private ActivityMyinfoBinding mBinding;

	public static void startActivity(Context context) {
		final Intent intent = new Intent(context, MyInfoActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_myinfo);
		mViewModel = new ViewModelProvider(this, new MyInfoViewModel.Factory(getApplication()))
				.get(MyInfoViewModel.class);
		mBinding.setLifecycleOwner(this);
		mBinding.setViewModel(mViewModel);
		mBinding.ibtnMyInfoVehicleNumberChange.setOnClickListener(this);

		subscribeViewModel(mViewModel);
		initToolbar();
	}

	private void subscribeViewModel(MyInfoViewModel myInfoViewModel) {
		myInfoViewModel.getCallInfo().observe(this, new Observer<Call>() {
			@Override
			public void onChanged(Call call) {
				boolean enable = call.getCallStatus() == Constants.CALL_STATUS_VACANCY;
				mBinding.ibtnMyInfoVehicleNumberChange.setEnabled(enable);
			}
		});

		MutableLiveData<ResponseMyInfoPacket> responseMyInfoData = myInfoViewModel.requestMyInfo();
		responseMyInfoData.observe(this, new Observer<ResponseMyInfoPacket>() {
			@Override
			public void onChanged(ResponseMyInfoPacket responseMyInfoPacket) {
				responseMyInfoData.removeObserver(this);

				LogHelper.e("reponseMyInfo : " + responseMyInfoPacket);

			}
		});
	}

	private void initToolbar() {
		setSupportActionBar(mBinding.myInfoToolbar.toolbar);
		mBinding.myInfoToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.myInfoToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(getString(R.string.d_menu_my_info));
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ibtn_my_info_vehicle_number_change) {
			MyInfoChangeVehicleNumberActivity.startActivity(this);
		}
	}
}
