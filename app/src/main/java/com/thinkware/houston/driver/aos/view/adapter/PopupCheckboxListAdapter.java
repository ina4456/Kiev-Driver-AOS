package com.thinkware.houston.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinkware.houston.driver.aos.R;
import com.thinkware.houston.driver.aos.databinding.ItemFloatingBtnListBinding;
import com.thinkware.houston.driver.aos.model.SelectionItem;
import com.thinkware.houston.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by seok-beomkwon on 2017. 10. 17..
 */

public class PopupCheckboxListAdapter extends RecyclerView.Adapter<PopupCheckboxListAdapter.ViewHolder> {
	private Context mContext;
	private ArrayList<SelectionItem> mList;

	public PopupCheckboxListAdapter(Context context, ArrayList<SelectionItem> list) {
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_floating_btn_list, viewGroup, false);
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

	public ArrayList<SelectionItem> getItems() {
		return mList;
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ItemFloatingBtnListBinding mFloatingBtnListBinding;

		public ViewHolder(View itemView) {
			super(itemView);
			mFloatingBtnListBinding = DataBindingUtil.bind(itemView);
			mFloatingBtnListBinding.clPopupListFloatingBtnItem.setOnClickListener(this);
		}

		public void bindItemData(SelectionItem item) {
			//마지막 아이템 디바이더 제거
			int position = getAdapterPosition();
			if (mList != null && mList.size()-1 == position) {
				mFloatingBtnListBinding.vPopupFloatingBtnDivider.setVisibility(View.GONE);
			}
			mFloatingBtnListBinding.ctvPopupFloatingBtnName.setText(item.getItemContent());
			mFloatingBtnListBinding.ctvPopupFloatingBtnName.setChecked(item.isChecked());
		}

		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			LogHelper.e("onClick()");
			if (position != -1) {
				SelectionItem item = getItemAt(position);
				if (item != null) {
					mFloatingBtnListBinding.ctvPopupFloatingBtnName.setChecked(!item.isChecked());
					item.setChecked(!item.isChecked());
				}
			}
		}
	}
}