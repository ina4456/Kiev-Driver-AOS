package com.kiev.driver.aos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seok-beomkwon on 2017. 10. 23..
 */

public class SelectionItem implements Parcelable {
	public static final int ITEM_TYPE_TITLE = 1000;
	public static final int ITEM_TYPE_CONTENT = 2000;

	private int itemId;
	private int itemType;
	private String itemTitle;
	private String itemContent;
	private boolean isChecked;
	private boolean isNaviInstalled;    //내비게이션 설치 여부 팝업에서 사용


	public SelectionItem() {
	}

	public SelectionItem(Parcel in) {
		readFromParcel(in);
	}

	public static final Creator<SelectionItem> CREATOR = new Creator<SelectionItem>() {
		@Override
		public SelectionItem createFromParcel(Parcel in) {
			return new SelectionItem(in);
		}

		@Override
		public SelectionItem[] newArray(int size) {
			return new SelectionItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(itemId);
		parcel.writeInt(itemType);
		parcel.writeString(itemTitle);
		parcel.writeString(itemContent);
		parcel.writeByte((byte)(isChecked ? 1 : 0));
		parcel.writeByte((byte)(isNaviInstalled ? 1 : 0));
	}

	private void readFromParcel(Parcel in) {
		itemId = in.readInt();
		itemType = in.readInt();
		itemTitle = in.readString();
		itemContent = in.readString();
		isChecked = in.readByte() != 0;
		isNaviInstalled = in.readByte() != 0;
	}


	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getItemContent() {
		return itemContent;
	}

	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public boolean isNaviInstalled() {
		return isNaviInstalled;
	}

	public void setNaviInstalled(boolean naviInstalled) {
		isNaviInstalled = naviInstalled;
	}

	@Override
	public String toString() {
		return "SelectionItem{" +
				"itemId=" + itemId +
				", itemType=" + itemType +
				", itemTitle='" + itemTitle + '\'' +
				", itemContent='" + itemContent + '\'' +
				", isChecked=" + isChecked +
				", isNaviInstalled=" + isNaviInstalled +
				'}';
	}
}
