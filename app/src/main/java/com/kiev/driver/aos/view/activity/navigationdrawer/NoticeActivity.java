package com.kiev.driver.aos.view.activity.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ActivityNoticeListBinding;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.view.activity.BaseActivity;
import com.kiev.driver.aos.view.adapter.NoticeAdapter;
import com.kiev.driver.aos.viewmodel.NoticeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class NoticeActivity extends BaseActivity implements View.OnClickListener {

	public static final String EXTRA_IS_NOTIFICATION = "extra_is_notification";

	private boolean isNotice;
	private ActivityNoticeListBinding mBinding;
	private ExpandableListView mExpandableListView;
	private NoticeAdapter mAdapter;
	private ArrayList<Notice> mNoticeList;
	private NoticeViewModel mNoticeViewModel;


	public static void startActivity(Context context, boolean isNotice) {
		final Intent intent = new Intent(context, NoticeActivity.class);
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
//		mBinding.setViewModel(mNoticeViewModel);

		initToolbar(isNotice);
		initializeView(isNotice);
		subscribeViewModel();

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

		initExpandableListView();
		setEmptyMsgView();
		mBinding.elvNotice.setVisibility(View.GONE);
		mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
	}

	// FIXME: 2019-09-03 인솔라인 날짜 데이터 형식 확정 후 수정 필요
	private void subscribeViewModel() {
		mNoticeViewModel.getNoticeList(isNotice).observe(this, new Observer<List<Notice>>() {
			@Override
			public void onChanged(List<Notice> notices) {
				if (notices != null && notices.size() > 0) {
					LogHelper.e("notice list : " + notices.size());
					//setRecyclerView(new ArrayList(notices));
					mAdapter.setGroupList(notices);
					setEmptyMsgView();
				}
			}
		});
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

	private void setEmptyMsgView() {
		if (mAdapter != null) {
			if (mAdapter.getGroupCount() <= 0) {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.VISIBLE);
				mBinding.elvNotice.setVisibility(View.GONE);
			} else {
				mBinding.viewEmptyMsg.clEmptyView.setVisibility(View.GONE);
				mBinding.elvNotice.setVisibility(View.VISIBLE);
			}
		}
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
