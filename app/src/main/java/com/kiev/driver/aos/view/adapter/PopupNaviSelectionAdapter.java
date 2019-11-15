package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ItemNavigationListBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by seok-beomkwon on 2017. 10. 17..
 */

public class PopupNaviSelectionAdapter extends RecyclerView.Adapter<PopupNaviSelectionAdapter.ViewHolder> {
	private Context mContext;
	private ArrayList<SelectionItem> mList;
	private SelectionCallback mCallback;
	private int popupType;

	public PopupNaviSelectionAdapter(Context context, ArrayList<SelectionItem> list, int popupType, SelectionCallback callback) {
		this.mContext = context;
		this.mList = list;
		this.mCallback = callback;
		this.popupType = popupType;
	}

	public interface SelectionCallback {
		void onListItemSelected(int popupType, SelectionItem item);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_navigation_list, viewGroup, false);
		return new ViewHolder(binding.getRoot());
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.bindItemData(getItemAt(position));
	}

	@Override
	public int getItemCount() {
		return (null != mList ? mList.size() : 0);
	}

	public SelectionItem getItemAt(int position) {
		return mList.get(position);
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ItemNavigationListBinding mNavigationListBinding;

		public ViewHolder(View itemView) {
			super(itemView);
			mNavigationListBinding = DataBindingUtil.bind(itemView);
			mNavigationListBinding.clPopupListItem1.setOnClickListener(this);
		}

		public void bindItemData(SelectionItem item) {
			//마지막 아이템 디바이더 제거
			int position = getAdapterPosition();
			if (mList != null && mList.size()-1 == position) {
				mNavigationListBinding.vPopupNaviDivider.setVisibility(View.GONE);
			}
			mNavigationListBinding.tvPopupNaviName.setText(item.getItemContent());

			//타입 내비 선택
			if (popupType == Popup.TYPE_ONE_BTN_NAVI_SELECTION) {
				mNavigationListBinding.tvPopupNaviName.setSelected(item.isChecked());
				mNavigationListBinding.tvPopupNaviInstalledYn.setVisibility(item.isNaviInstalled() ? View.GONE : View.VISIBLE);

			//타입 내비 설치
			} else {
				String naviName = String.format(mContext.getString(R.string.setting_navigation_app_install), item.getItemContent());
				mNavigationListBinding.tvPopupNaviName.setText(naviName);
				mNavigationListBinding.tvPopupNaviInstalledYn.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			LogHelper.e("adapter position : " + position);
			if (position != -1) {
				SelectionItem item = getItemAt(position);
				mCallback.onListItemSelected(popupType, item);
				item.setChecked(true);
				mNavigationListBinding.tvPopupNaviName.setSelected(true);
			}
		}
	}
}