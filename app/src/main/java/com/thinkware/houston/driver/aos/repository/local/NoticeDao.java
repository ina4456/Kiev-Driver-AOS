package com.thinkware.houston.driver.aos.repository.local;


import com.thinkware.houston.driver.aos.model.entity.Notice;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface NoticeDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Notice notice);

	@Query("SELECT * FROM notice_table WHERE notice_is_notice == 1 ORDER BY id DESC LIMIT 1")
	LiveData<Notice> getLatestNotice();

	@Query("SELECT * FROM notice_table WHERE notice_is_notice == :isNotice AND id != 0 ORDER BY id DESC LIMIT 5")
	LiveData<List<Notice>> getNoticeList(boolean isNotice);
}
