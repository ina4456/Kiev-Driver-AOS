package com.kiev.driver.aos.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by seok-beomkwon on 2017. 10. 18..
 */


public class Popup implements Serializable {
	public static final int TYPE_ONE_BTN_NORMAL = 10;
	public static final int TYPE_ONE_BTN_TITLE_CONTENT = 11;
	public static final int TYPE_TWO_BTN_NORMAL = 20;
	public static final int TYPE_TWO_BTN_WITH_TITLE = 21;
	public static final int TYPE_NO_BTN = 30;

	public static final int TYPE_ONE_BTN_SINGLE_SELECTION = 40;
	public static final int TYPE_ONE_BTN_NAVI_SELECTION = 41;
	public static final int TYPE_ONE_BTN_NAVI_INSTALL = 42;
	public static final int TYPE_SMS = 50;

	public static final int TYPE_ONE_BTN_CHECK_SELECTION = 60;

	private String tag;

	private int type;
	private int width;
	private int height;
	private int dismissSecond;

	private String title;
	private String contentTitle;
	private String content;
	private String labelPositiveBtn;
	private String labelNegativeBtn;
	private ArrayList<SelectionItem> mSelectionItems;
	private boolean isHiddenStatusBar;
	private boolean isIntegerForEditText;

	public String getTag() {
		return tag;
	}

	public int getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public String getContent() {
		return content;
	}

	public String getLabelPositiveBtn() {
		if (labelPositiveBtn == null) {
			return "확인";
		} else {
			return labelPositiveBtn;
		}
	}

	public String getLabelNegativeBtn() {
		if (labelNegativeBtn == null) {
			return "취소";
		} else {
			return labelNegativeBtn;
		}
	}

	public int getDismissSecond() {
		return dismissSecond;
	}

	public ArrayList<SelectionItem> getSelectionItems() {
		return mSelectionItems;
	}

	public boolean isHiddenStatusBar() {
		return isHiddenStatusBar;
	}

	public boolean isIntegerForEditText() {
		return isIntegerForEditText;
	}

	@Override
	public String toString() {
		return "Popup{" +
				"tag='" + tag + '\'' +
				", type=" + type +
				", width=" + width +
				", height=" + height +
				", dismissSecond=" + dismissSecond +
				", title='" + title + '\'' +
				", contentTitle='" + contentTitle + '\'' +
				", content='" + content + '\'' +
				", labelPositiveBtn='" + labelPositiveBtn + '\'' +
				", labelNegativeBtn='" + labelNegativeBtn + '\'' +
				", mSelectionItems=" + mSelectionItems +
				", isHiddenStatusBar=" + isHiddenStatusBar +
				", isIntegerForEditText=" + isIntegerForEditText +
				'}';
	}

	private Popup(Builder builder) {
		this.tag = builder.tag;
		this.type = builder.type;
		this.width = builder.width;
		this.height = builder.height;
		this.title = builder.title;
		this.contentTitle = builder.contentTitle;
		this.content = builder.content;
		this.labelPositiveBtn = builder.labelPositiveBtn;
		this.labelNegativeBtn = builder.labelNegativeBtn;
		this.dismissSecond = builder.dismissSecond;
		this.mSelectionItems = builder.mSelectionItems;
		this.isHiddenStatusBar = builder.isHiddenStatusBar;
		this.isIntegerForEditText = builder.isIntegerForEditText;
	}

	public static class Builder {
		private String tag;
		private int type;
		private int width;
		private int height;
		private int dismissSecond;
		private String title;
		private String contentTitle;
		private String content;
		private String labelPositiveBtn;
		private String labelNegativeBtn;
		private ArrayList<SelectionItem> mSelectionItems;
		private boolean isHiddenStatusBar;
		private boolean isIntegerForEditText;

		public Builder(int type, String tag) {
			this.type = type;
			this.tag = tag;
		}
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContent(String content) {
			this.content = content;
			return this;
		}

		public Builder setContentTitle(String contentTitle) {
			this.contentTitle = contentTitle;
			return this;
		}

		public Builder setBtnLabel(String labelPositiveBtn, String labelNegativeBtn) {
			this.labelPositiveBtn = labelPositiveBtn;
			this.labelNegativeBtn = labelNegativeBtn;
			return this;
		}

		public Builder setDismissSecond(int dismissSecond){
			this.dismissSecond = dismissSecond;
			return this;
		}

		public Builder setSelectionItems(ArrayList<SelectionItem> selectionItems){
			this.mSelectionItems = selectionItems;
			return this;
		}

		public Builder setIsHiddenStatusBar(boolean isHiddenStatusBar){
			this.isHiddenStatusBar = isHiddenStatusBar;
			return this;
		}

		public Builder setIsIntegerForEditText(boolean isIntegerForEditText){
			this.isIntegerForEditText = isIntegerForEditText;
			return this;
		}

		public Popup build() {
			return new Popup(this);
		}
	}
}
