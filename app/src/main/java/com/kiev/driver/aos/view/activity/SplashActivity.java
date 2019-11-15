package com.kiev.driver.aos.view.activity;

import android.os.Bundle;
import android.os.Handler;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivitySplashBinding;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.viewmodel.ConfigViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class SplashActivity extends AppCompatActivity {

	private ActivitySplashBinding mBinding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
		mBinding.tvAppName.setText(R.string.app_name);

		ConfigViewModel.Factory factory = new ConfigViewModel.Factory(getApplication());
		ConfigViewModel configViewModel = ViewModelProviders.of(this, factory).get(ConfigViewModel.class);
		subscribeToModel(configViewModel);

		// TODO: 2019. 3. 8. 초기화 실행 필요
		//configViewModel.initConfigData(getApplicationContext());


		new Handler().postDelayed(new Runnable() {
			public void run() {
				finish();
				LoginActivity.startActivity(SplashActivity.this);
			}
		}, 1000);
	}


	private void subscribeToModel(final ConfigViewModel viewModel) {
		viewModel.getConfiguration().observe(this, new Observer<Configuration>() {
			@Override
			public void onChanged(Configuration configuration) {
				if (configuration != null) {
					LogHelper.e("onChanged()");
				}
			}
		});
	}
}
