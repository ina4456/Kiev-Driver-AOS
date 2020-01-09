package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityNoticeListBinding;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseNoticeListPacket;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.adapter.NoticeAdapter;
import com.kiev.driver.aos.viewmodel.NoticeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class NoticeActivity extends BaseActivity implements View.OnClickListener {

	public static final String EXTRA_IS_NOTIFICATION = "extra_is_notification";

	private boolean isNotice;
	private ActivityNoticeListBinding mBinding;
	private ExpandableListView mExpandableListView;
	private NoticeAdapter mAdapter;
	private NoticeViewModel mNoticeViewModel;


	public static void startActivity(Context context, boolean isNotice) {
		final Intent intent = new Intent(context, NoticeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRA_IS_NOTIFICATION, isNotice);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isNotice = getIntent().getBooleanExtra(EXTRA_IS_NOTIFICATION, false);
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notice_list);
		mBinding.setLifecycleOwner(this);
		mNoticeViewModel = new ViewModelProvider(this, new NoticeViewModel.Factory(getApplication(), isNotice))
				.get(NoticeViewModel.class);

		initToolbar(isNotice);
		initializeView(isNotice);
		subscribeViewModel(isNotice);

	}

	private void initToolbar(boolean isNotice) {
		setSupportActionBar(mBinding.noticeToolbar.toolbar);
		mBinding.noticeToolbar.ibtnActionButton.setVisibility(View.GONE);
		mBinding.noticeToolbar.toolbar.setOnClickListener(this);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setTitle(isNotice ? getString(R.string.d_menu_notice): getString(R.string.d_menu_call_center_msg) );
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowHomeEnabled(true);
			ab.setDisplayShowTitleEnabled(true);
		}
	}

	private void initializeView(boolean isNotice){
		if (isNotice) {
			mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(R.string.notice_no_notice));
		} else {
			mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(R.string.notice_no_message));
		}

		mBinding.elvNotice.setVisibility(View.GONE);
		mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);

		initExpandableListView();
		showListOrEmptyMsgView(isNotice);

	}

	private void subscribeViewModel(boolean isNotice) {
		if (isNotice) {
			MutableLiveData<ResponseNoticeListPacket> liveData = mNoticeViewModel.getNoticeListFromServer();
			liveData.observe(this, new Observer<ResponseNoticeListPacket>() {
				@Override
				public void onChanged(ResponseNoticeListPacket responseNoticeListPacket) {
					if (responseNoticeListPacket != null) {
						liveData.removeObserver(this);
						LogHelper.e("responseNoticeListPacket : " + responseNoticeListPacket);

						ArrayList<Notice> noticeArrayList = new ArrayList<>();
						noticeArrayList.add(getNoticeObjectFromPacket(
								responseNoticeListPacket.getNoticeCode1(),
								responseNoticeListPacket.getNoticeDate1(),
								responseNoticeListPacket.getNoticeTitle1(),
								responseNoticeListPacket.getNoticeContent1()
						));
						noticeArrayList.add(getNoticeObjectFromPacket(
								responseNoticeListPacket.getNoticeCode2(),
								responseNoticeListPacket.getNoticeDate2(),
								responseNoticeListPacket.getNoticeTitle2(),
								responseNoticeListPacket.getNoticeContent2()
						));
						noticeArrayList.add(getNoticeObjectFromPacket(
								responseNoticeListPacket.getNoticeCode3(),
								responseNoticeListPacket.getNoticeDate3(),
								responseNoticeListPacket.getNoticeTitle3(),
								responseNoticeListPacket.getNoticeContent3()
						));
						noticeArrayList.add(getNoticeObjectFromPacket(
								responseNoticeListPacket.getNoticeCode4(),
								responseNoticeListPacket.getNoticeDate4(),
								responseNoticeListPacket.getNoticeTitle4(),
								responseNoticeListPacket.getNoticeContent4()
						));
						noticeArrayList.add(getNoticeObjectFromPacket(
								responseNoticeListPacket.getNoticeCode5(),
								responseNoticeListPacket.getNoticeDate5(),
								responseNoticeListPacket.getNoticeTitle5(),
								responseNoticeListPacket.getNoticeContent5()
						));

						mAdapter.setGroupList(noticeArrayList);
						showListOrEmptyMsgView(isNotice);
					}
				}
			});
		} else {
			LiveData<List<Notice>> liveData = mNoticeViewModel.getNoticeList(false);
			liveData.observe(this, new Observer<List<Notice>>() {
				@Override
				public void onChanged(List<Notice> notices) {
					if (notices != null && notices.size() > 0) {
						//liveData.removeObserver(this);
						LogHelper.e("notice list : " + notices.size());
						//setRecyclerView(new ArrayList(notices));
						mAdapter.setGroupList(notices);
						showListOrEmptyMsgView(false);
					}
				}
			});
		}
	}


	private void initExpandableListView() {
		mExpandableListView = mBinding.elvNotice;
		mAdapter = new NoticeAdapter(this, new ArrayList<Notice>());
		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				//parent.smoothScrollToPosition(groupPosition);
				return false;
			}
		});
	}



	private void showListOrEmptyMsgView(boolean isNotice) {
		if (mAdapter != null) {
			if (mAdapter.getGroupCount() <= 0) {
				mBinding.viewEmptyMsg.tvEmptyMsg.setText(getString(isNotice ? R.string.notice_no_notice : R.string.notice_no_message));
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.elvNotice.setVisibility(View.GONE);
			} else {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.GONE);
				mBinding.elvNotice.setVisibility(View.VISIBLE);
			}
		}
	}


	private Notice getNoticeObjectFromPacket(int code, String date, String title, String content) {
		Notice notice = new Notice();
		notice.setNotice(true);
		notice.setId(code);
		notice.setDate(date);
		notice.setTitle(title);
		notice.setContent(content);


		return notice;
	}

	@Override
	public void onClick(View view) {
		LogHelper.e("onclick : " +view.getId());
		LogHelper.e("onclick : " + R.id.toolbar);
		LogHelper.e("onclick : " + R.id.notice_toolbar);

//		if (view.getId() == R.id.toolbar) {
//			LogHelper.e("onclick toolbar");
//			finish();
//		}
	}
}
