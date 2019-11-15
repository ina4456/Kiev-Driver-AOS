package com.kiev.driver.aos.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ViewPopupCheckboxListBinding;
import com.kiev.driver.aos.databinding.ViewPopupNavigationBinding;
import com.kiev.driver.aos.databinding.ViewPopupNoBtnBinding;
import com.kiev.driver.aos.databinding.ViewPopupNoticeBinding;
import com.kiev.driver.aos.databinding.ViewPopupOneBtnBinding;
import com.kiev.driver.aos.databinding.ViewPopupSelectionListBinding;
import com.kiev.driver.aos.databinding.ViewPopupSendMsgBinding;
import com.kiev.driver.aos.databinding.ViewPopupTwoBtnBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.adapter.PopupCheckboxListAdapter;
import com.kiev.driver.aos.view.adapter.PopupNaviSelectionAdapter;
import com.kiev.driver.aos.view.adapter.PopupSingleSelectionAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by seok-beomkwon on 2017. 10. 17..
 */


public class PopupDialogFragment extends DialogFragment implements View.OnClickListener
		, PopupSingleSelectionAdapter.SelectionCallback
		, PopupNaviSelectionAdapter.SelectionCallback {

    private static final String ARG_DIALOG_POPUP = "arg_dialog_popup";
    private static final String ARG_LISTENER = "arg_listener";
	private static final int COUNT_DOWN_INTERVAL = 1000;

	private ViewPopupNoBtnBinding noBtnBinding;
	private ViewPopupOneBtnBinding oneBtnBinding;
	private ViewPopupTwoBtnBinding twoBtnBinding;
	private ViewPopupSendMsgBinding sendMsgBinding;
	private ViewPopupSelectionListBinding mSelectionListBinding;
	private ViewPopupNavigationBinding navigationBinding;
	private ViewPopupNoticeBinding noticeBinding;
	private ViewPopupCheckboxListBinding floatingBtnBinding;

	private Popup popup;
	private PopupDialogListener listener;

	public interface PopupDialogListener {
		void onDismissPopupDialog(String tag, Intent intent);
	}

     /**
	 * 팝업 다이얼로그 프레그먼트 생성
	 * @param popup 팝업 객체
	 */
	public static PopupDialogFragment newInstance(Popup popup) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ARG_DIALOG_POPUP, popup);

        PopupDialogFragment fragment = new PopupDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

	@Override
	public void onAttach(Context context) {
		LogHelper.e("onAttach()");
		super.onAttach(context);
		try {
			if (getParentFragment() != null) {
				listener = (PopupDialogListener) getParentFragment();
			} else {
				listener = (PopupDialogListener) context;
			}
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement PopupDialogListener for callback");
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
		//LogHelper.e("onCreate()");
		if (getArguments() != null) {
			popup = (Popup)getArguments().getSerializable(ARG_DIALOG_POPUP);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//LogHelper.e("onCreateDialog()");

		Dialog dialog = new Dialog(getActivity(), getTheme()){
			@Override
			public void onBackPressed() {
				//실시간 메시지의 경우 처리
				if (popup != null && popup.getTag().equals(Constants.DIALOG_TAG_MESSAGE)) {
					LogHelper.e("onBackPressed() && popup dialog from PopupActivity");
					dismissDialog(null);
					getActivity().finish();
				} else {
					dismissDialog(null);
				}
			}
		};
		return dialog;
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
	    //LogHelper.e("onCreateView()");

	    Dialog dialog = getDialog();
	    Window window = dialog.getWindow();
	    window.requestFeature(Window.FEATURE_NO_TITLE); //타이틀 영역 제거
	    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //기본 배경색 투명 적용
	    dialog.setCanceledOnTouchOutside(true); //외부 클릭시 다이얼로그 종료되지 않게 설정
	    setCancelable(false);

	    ViewDataBinding viewDataBinding = null;
	    if (popup == null) {
		    dismiss();
	    } else {
		    switch (popup.getType()) {
			    case Popup.TYPE_ONE_BTN_NORMAL:
				    viewDataBinding = getOneBtnDialog(popup);
				    break;

			    case Popup.TYPE_TWO_BTN_NORMAL:
				    viewDataBinding = getTwoBtnDialog(popup);
				    break;

			    case Popup.TYPE_SMS:
			        viewDataBinding = getSendSmsDialog(popup);
			        break;

			    case Popup.TYPE_ONE_BTN_NAVI_SELECTION:
			    case Popup.TYPE_ONE_BTN_NAVI_INSTALL:
				    viewDataBinding = getNavigationDialog(popup);
				    break;

			    case Popup.TYPE_ONE_BTN_SINGLE_SELECTION:
			    case Popup.TYPE_ONE_BTN_TITLE_CONTENT:
				    viewDataBinding = getSingleSelectionDialog(popup);
				    break;

			    case Popup.TYPE_NO_BTN:
				    viewDataBinding = getNoBtnDialog(popup);
				    break;

			    case Popup.TYPE_TWO_BTN_WITH_TITLE:
			    	viewDataBinding = getNoticeDialog(popup);
			    	break;

			    case Popup.TYPE_ONE_BTN_CHECK_SELECTION:
				    viewDataBinding = getFloatingBtnDialog(popup);
				    break;
		    }
	    }

	    if (viewDataBinding != null) {
		    return viewDataBinding.getRoot();
	    } else {
		    //throw new NullPointerException("something wrong with dialog view");
		    return null;
	    }
    }

    @Override
    public void onResume() {
	    LogHelper.e("onResume()");
	    super.onResume();

	    Window window = getDialog().getWindow();
	    if (window != null && popup != null) {
		    int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_normal_width);
		    if (popup.getType() == Popup.TYPE_SMS) {
			    window.setGravity(Gravity.BOTTOM);
		    } else if (popup.getType() == Popup.TYPE_TWO_BTN_WITH_TITLE){
			    dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_notice_width);
		    }
		    window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);

		    if (popup != null) {
			    if (popup.getDismissSecond() != 0) { //자동 소거
				    startCountDown(popup.getDismissSecond());
			    }
		    }
	    }
    }

	@Override
	public void onDetach() {
		LogHelper.e("onDetach()");
		super.onDetach();
	}


	/**
	 *==================================================================================
	 * 팝업 타입
	 *==================================================================================
	 */

	//원버튼(취소 또는 확인) 팝업
	private ViewDataBinding getOneBtnDialog(Popup popup) {
		oneBtnBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_one_btn, null, true);
		oneBtnBinding.tvPopupBody.setText(popup.getContent());
		oneBtnBinding.btnOnebtnPopup.setOnClickListener(this);

		String labelPositiveBtn = popup.getLabelPositiveBtn();
		if (popup.getDismissSecond() != 0) {
			labelPositiveBtn = getBtnLabelWithDismissSecond(labelPositiveBtn, popup.getDismissSecond());
		}
		oneBtnBinding.btnOnebtnPopup.setText(getSpanned(labelPositiveBtn));
		return oneBtnBinding;
	}

	//투버튼(취소, 확인) 팝업
	private ViewDataBinding getTwoBtnDialog(Popup popup) {
		twoBtnBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_two_btn, null, true);
		twoBtnBinding.tvPopupBody.setText(popup.getContent());
		twoBtnBinding.btnTwobtnPopupNegative.setText(popup.getLabelNegativeBtn());
		twoBtnBinding.btnTwobtnPopupPositive.setText(popup.getLabelPositiveBtn());
		twoBtnBinding.btnTwobtnPopupNegative.setOnClickListener(this);
		twoBtnBinding.btnTwobtnPopupPositive.setOnClickListener(this);

		return twoBtnBinding;
	}

	//SMS 팝업
	private ViewDataBinding getSendSmsDialog(Popup popup) {
		sendMsgBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_send_msg, null, true);
		sendMsgBinding.ibtnSendMsgClose.setOnClickListener(this);

		final ArrayList<SelectionItem> list = popup.getSelectionItems();
		RecyclerView recyclerView = sendMsgBinding.rvSendMsgList;
		PopupSingleSelectionAdapter adapter = new PopupSingleSelectionAdapter(getContext(), list, popup.getType(), this);
		recyclerView.setAdapter(adapter);

		return sendMsgBinding;
	}

	private ViewDataBinding getSingleSelectionDialog(Popup popup) {
		mSelectionListBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_selection_list, null, true);
		mSelectionListBinding.btnPopupNegative.setOnClickListener(this);

		if (popup.getLabelNegativeBtn() != null) {
			mSelectionListBinding.btnPopupNegative.setText(popup.getLabelNegativeBtn());
		}

		final ArrayList<SelectionItem> list = popup.getSelectionItems();
		RecyclerView recyclerView = mSelectionListBinding.rvSelectionList;
		PopupSingleSelectionAdapter adapter = new PopupSingleSelectionAdapter(getContext(), list, popup.getType(), this);
		recyclerView.setAdapter(adapter);

		return mSelectionListBinding;
	}

	//내비선택, 설치 팝업
	private ViewDataBinding getNavigationDialog(Popup popup) {
		navigationBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_navigation, null, true);
		navigationBinding.btnNaviPopupNegative.setOnClickListener(this);

		if (popup.getType() == Popup.TYPE_ONE_BTN_NAVI_SELECTION) {
			navigationBinding.tvPopupListHeader.setVisibility(View.GONE);
		} else {
			navigationBinding.tvPopupListHeader.setVisibility(View.VISIBLE);
		}

		final ArrayList<SelectionItem> list = popup.getSelectionItems();
		RecyclerView recyclerView = navigationBinding.rvNavigationList;
		PopupNaviSelectionAdapter adapter = new PopupNaviSelectionAdapter(getContext(), list, popup.getType(), this);
		recyclerView.setAdapter(adapter);

		return navigationBinding;
	}

	//플로팅 버튼 사용 선택 팝업
	private ViewDataBinding getFloatingBtnDialog(Popup popup) {
		floatingBtnBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_checkbox_list, null, true);
		floatingBtnBinding.btnPopupFloatingBtnPositive.setOnClickListener(this);

		final ArrayList<SelectionItem> list = popup.getSelectionItems();
		RecyclerView recyclerView = floatingBtnBinding.rvFloatingBtnList;
		PopupCheckboxListAdapter adapter = new PopupCheckboxListAdapter(getContext(), list);
		recyclerView.setAdapter(adapter);

		return floatingBtnBinding;
	}

	private ViewDataBinding getNoBtnDialog(Popup popup) {
		noBtnBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_no_btn, null, true);
		noBtnBinding.tvPopupBody.setText(popup.getContent());

		return noBtnBinding;
	}

	private ViewDataBinding getNoticeDialog(Popup popup) {
		noticeBinding = DataBindingUtil
				.inflate(LayoutInflater.from(getContext()), R.layout.view_popup_notice, null, true);
		noticeBinding.tvPopupNoticeBody.setText(popup.getContent());
		noticeBinding.btnPopupNoticeNegative.setOnClickListener(this);
		noticeBinding.btnPopupNoticePositive.setOnClickListener(this);

		if (popup.getTag().equals(Constants.DIALOG_TAG_MESSAGE)) {
			noticeBinding.tvPopupNoticeHeader.setVisibility(View.GONE);
			noticeBinding.vPopupNoticeDivider.setVisibility(View.GONE);
		}
		return noticeBinding;
	}


	/**
	 *==================================================================================
	 */


	private void startCountDown(int dismissSecond){
		CountDownTimer countDownTimer = new CountDownTimer((dismissSecond + 1) * COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
			@Override
			public void onTick(long l) {
				String label = getBtnLabelWithDismissSecond(popup.getLabelPositiveBtn(), (int)(l/1000)); //0초 카운트를 보여주기 위함
				//oneBtn 다이얼로그 이외에는 카운트 표시 안하고 dismiss 함.
				if (oneBtnBinding != null) {
					oneBtnBinding.btnOnebtnPopup.setText(label);
				}
			}
			@Override
			public void onFinish() {
				if(getDialog() != null && getDialog().isShowing()){
					dismissDialog(null);
				}
			}
		};
		countDownTimer.start();
	}

	private String getBtnLabelWithDismissSecond(String labelPositiveBtn, final int dismissSecond) {
		if (isAdded() && getActivity() != null) {
			labelPositiveBtn = labelPositiveBtn + "(%s초)";
			labelPositiveBtn = String.format(labelPositiveBtn, String.valueOf(dismissSecond));
		}
		return labelPositiveBtn;
	}

	private Spanned getSpanned(String labelPositiveBtn) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return Html.fromHtml(labelPositiveBtn, Html.FROM_HTML_MODE_LEGACY);
		} else {
			return Html.fromHtml(labelPositiveBtn);
		}
	}

	//statusBar 위에 그리기 (포지션이 아닌 허용 여부 설정)
	private void setDialogAboveStatusBar(){
		//LogHelper.e("setDialogAboveStatusBar() : " + getDialog().getWindow().getAttributes().toString());
		WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT);

		getDialog().getWindow().setAttributes(windowLayoutParams);
		//LogHelper.e("setDialogAboveStatusBar() : " + getDialog().getWindow().getAttributes().toString());
	}

	private void setFullScreen(Window window){
		//LogHelper.e("setFullScreen()");
		window.getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}


	@Override
    public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()){
		    case R.id.btn_onebtn_popup:
			case R.id.btn_twobtn_popup_positive:
			case R.id.btn_popup_notice_positive:
				intent.putExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, true);
				intent.putExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM, popup.getTitle());
				dismissDialog(intent);
				break;

			case R.id.btn_popup_floating_btn_positive:
				ArrayList<SelectionItem> items = ((PopupCheckboxListAdapter)floatingBtnBinding.rvFloatingBtnList.getAdapter()).getItems();
				intent.putExtra(Constants.DIALOG_INTENT_KEY_PRESSED_POSITIVE_BTN, true);
				intent.putParcelableArrayListExtra(Constants.DIALOG_INTENT_KEY_ITEMS, items);
				dismissDialog(intent);
				break;

			default:
				dismissDialog(null);
				break;
	    }
	}

	@Override
	public void onListItemSelected(int popupType, String itemName, int itemId) {
		LogHelper.e("onListItemSelected() : " + itemName + " / " + itemId);
		Intent intent = new Intent();
		intent.putExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM_ID, itemId);
		intent.putExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM, itemName);

		dismissDialog(intent);
	}

	@Override
	public void onListItemSelected(int popupType, SelectionItem item) {
		LogHelper.e("type : " + popupType);
		Intent intent = new Intent();
		intent.putExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM, item.getItemContent());
		intent.putExtra(Constants.DIALOG_INTENT_KEY_SELECTED_ITEM_INSTALLED, item.isNaviInstalled());

		dismissDialog(intent);
	}

	private void dismissDialog(Intent intent) {
		LogHelper.e("dismissDialog : " + popup.getTag());
		listener.onDismissPopupDialog(popup.getTag(), intent);
		//this.dismiss();
		this.dismissAllowingStateLoss();
	}




	/*
	@Override
	public void networkResponse(Intent intent) {
		LogHelper.d("popupDialogFragment : " + intent.getStringExtra(SV.RESPONSE_CODE));

		String responseCode = intent.getStringExtra(SV.RESPONSE_CODE);
		//메시지 열람 중에 새로운 메시지가 수신될 경우 기존 창을 닫게 처리
		if (responseCode != null) {
			if (responseCode.equals(SV.RESPONSE_CODE_REAL_MESSAGE)
					&& popup.getTag().equals(Constants.DIALOG_TAG_MESSAGE_FROM_LIST)) {
				this.dismissAllowingStateLoss();

				//statusBar위에 그려지는 메서드를 호출한 팝업의 경우(setDialogAboveStatusBar()) 기존 창을 닫게 처리
				//기존 창을 닫지 않을 경우 투명하게 위에 그려지는 증상이 있음.
			} else if (responseCode.equals(SV.RESPONSE_CODE_REAL_MESSAGE)
					&& (popup.getType() == Popup.TYPE_RADIO_BTN_LIST)
					|| popup.getType() == Popup.TYPE_RADIO_TWO_BTN_LIST
					|| popup.getType() == Popup.TYPE_TWO_BTN_EDIT_TEXT){
				this.dismissDialog(null);
			}
		}
	}*/
}
