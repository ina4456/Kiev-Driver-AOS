package com.kiev.driver.aos.repository.local;


import com.kiev.driver.aos.model.entity.Call;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface CallDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Call call);

	@Update(onConflict = OnConflictStrategy.REPLACE)
	void update(Call call);


	@Query("SELECT * FROM call_table LIMIT 1")
	LiveData<Call> getCallInfoForUiLive();

	@Query("SELECT * FROM call_table LIMIT 1")
	Call getCallInfoForUi();

	@Query("DELETE FROM call_table")
	void deleteCallInfo();
}
