package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.R;
import com.kiev.driver.aos.databinding.ItemSingleSelectionListBinding;
import com.kiev.driver.aos.databinding.ItemTitleContentListBinding;
import com.kiev.driver.aos.model.Popup;
import com.kiev.driver.aos.model.SelectionItem;
import com.kiev.driver.aos.util.DeviceUtil;
import com.kiev.driver.aos.util.LogHelper;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by seok-beomkwon on 2017. 10. 17..
 */

public class PopupSingleSelectionAdapter extends RecyclerView.Adapter<PopupSingleSelectionAdapter.ViewHolder> {
	private Context mContext;
	private ArrayList<SelectionItem> mList;
	private SelectionCallback mCallback;
	private int popupType;

	public PopupSingleSelectionAdapter(Context context, ArrayList<SelectionItem> list, int popupType, SelectionCallback callback) {
		this.mContext = context;
		this.mList = list;
		this.mCallback = callback;
		this.popupType = popupType;
	}

	public interface SelectionCallback {
		void onListItemSelected(int popupType, String itemName, int itemId);
	}

	@Override
	public int getItemViewType(int position) {
		return popupType;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		ViewHolder viewHolder;
		ViewDataBinding binding;
		if (viewType == Popup.TYPE_ONE_BTN_TITLE_CONTENT) {
			binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_title_content_list, viewGroup, false);
			viewHolder =  new TitleContentViewHolder(binding.getRoot());
		} else {
			binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_single_selection_list, viewGroup, false);
			viewHolder = new SingleSelectionViewHolder(binding.getRoot());
		}
		return viewHolder;
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

	class SingleSelectionViewHolder extends ViewHolder implements View.OnClickListener {
		ItemSingleSelectionListBinding mBinding;

		public SingleSelectionViewHolder(View itemView) {
			super(itemView);
			mBinding = DataBindingUtil.bind(itemView);
			mBinding.clPopupItem.setOnClickListener(this);
		}

		public void bindItemData(SelectionItem item) {
			//마지막 아이템 디바이더 제거
			int position = getAdapterPosition();
			if (mList != null && mList.size()-1 == position) {
				mBinding.viewPopupItemDivider.setVisibility(View.GONE);
			}

			String itemName = item.getItemContent();
			LogHelper.e("item name : " +itemName);

			//개행 존재할 경우 height 늘림
			if (itemName.contains("\n")) {
				RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mBinding.clPopupItem.getLayoutParams();
				float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, mContext.getResources().getDisplayMetrics());
				params.height = (int)pixels;
				mBinding.clPopupItem.setLayoutParams(params);

				item.setItemContent(itemName.replaceAll("(?:\n|\r\n)","<br>"));
			}

			//색 포함 텍스트 설정
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				mBinding.tvPopupItemName.setText(Html.fromHtml(itemName, Html.FROM_HTML_MODE_LEGACY));
			} else {
				mBinding.tvPopupItemName.setText(Html.fromHtml(itemName));
			}

			//font size 설정 팝업
			float normalFontSize = mContext.getResources().getDimension(R.dimen.main_poi_name_text_size);
			normalFontSize = DeviceUtil.convertPxToDp((int)normalFontSize);

			if (itemName.equals(mContext.getString(R.string.setting_font_size_small))) {
				mBinding.tvPopupItemName
						.setTextSize(COMPLEX_UNIT_DIP, (normalFontSize - Constants.FONT_SIZE_INC_DEC_VALUE));

			} else if (itemName.equals(mContext.getString(R.string.setting_font_size_normal))) {
				mBinding.tvPopupItemName
						.setTextSize(COMPLEX_UNIT_DIP, normalFontSize);

			} else if (itemName.equals(mContext.getString(R.string.setting_font_size_large))) {
				mBinding.tvPopupItemName
						.setTextSize(COMPLEX_UNIT_DIP, (normalFontSize + Constants.FONT_SIZE_INC_DEC_VALUE));
			}

			mBinding.tvPopupItemName.setSelected(item.isChecked());

		}

		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			LogHelper.e("position : " + position);
			if (position != -1) {
				SelectionItem item = getItemAt(position);
				mCallback.onListItemSelected(popupType, item.getItemContent(), item.getItemId());
				// TODO: 2019-10-24 AOS SP는 팝업에서 항목을 선택하고 확인을 누르는 시나리오가 없음
				//item.setChecked(true);
				mBinding.tvPopupItemName.setSelected(true);
			}
		}
	}


	class TitleContentViewHolder extends ViewHolder {
		private ItemTitleContentListBinding mItemTitleContentListBinding;
		public TitleContentViewHolder(View itemView) {
			super(itemView);
			mItemTitleContentListBinding = DataBindingUtil.bind(itemView);
		}

		public void bindItemData(SelectionItem item) {
			mItemTitleContentListBinding.tvPopupTitle.setText(item.getItemTitle());
			mItemTitleContentListBinding.tvPopupContent.setText(item.getItemContent());
		}
	}


	public abstract class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) { super(itemView); }
		abstract void bindItemData(SelectionItem item);
	}
}