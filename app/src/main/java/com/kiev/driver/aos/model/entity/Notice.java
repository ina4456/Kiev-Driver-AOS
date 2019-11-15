package com.kiev.driver.aos.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "notice_table")
public class Notice {

	@PrimaryKey(autoGenerate = true)
	private int id;
	@ColumnInfo(name = "notice_title")
	private String title;
	@ColumnInfo(name = "notice_content")
	private String content;
	@ColumnInfo(name = "notice_date")
	private long date;
	@ColumnInfo(name = "notice_is_notice")
	private boolean isNotice;
	@ColumnInfo(name = "notice_is_read")
	private boolean isRead;


	public Notice() {
	}

	@Ignore
	public Notice(String title, String content, long date, boolean isNotice) {
		this.title = title;
		this.content = content;
		this.date = date;
		this.isNotice = isNotice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public boolean isNotice() {
		return isNotice;
	}

	public void setNotice(boolean notice) {
		isNotice = notice;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean read) {
		isRead = read;
	}

	@Override
	public String toString() {
		return "Notice{" +
				"id=" + id +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", date=" + date +
				", isNotice=" + isNotice +
				", isRead=" + isRead +
				'}';
	}
}
