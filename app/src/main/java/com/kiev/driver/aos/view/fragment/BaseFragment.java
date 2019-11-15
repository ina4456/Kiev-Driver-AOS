package com.kiev.driver.aos.view.fragment;

import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;

import androidx.fragment.app.Fragment;


/**
 * Created by seok-beomkwon on 2017. 11. 3..
 */

public class BaseFragment extends Fragment {
	public void showPopupDialog(Popup popup) {
		if (popup != null) {
			PopupDialogFragment dialogFragment = PopupDialogFragment.newInstance(popup);
			dialogFragment.show(getChildFragmentManager(), popup.getTag());
		}
	}

	public void startLoadingProgress() {
		startLoadingProgress(null);
	}

	public void startLoadingProgress(String msg) {
		BaseActivity baseActivity = (BaseActivity) getActivity();
		if (baseActivity != null)
			baseActivity.startLoadingProgress(msg);
	}

	public void finishLoadingProgress() {
		LogHelper.e("finishLaodingProgress() 1 : " + getActivity());
		BaseActivity baseActivity = (BaseActivity) getActivity();
		if (baseActivity != null) {
			LogHelper.e("finishLaodingProgress() 2");
			baseActivity.finishLoadingProgress();
		}
	}
}
