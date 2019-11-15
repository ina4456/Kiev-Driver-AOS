package com.kiev.driver.aos.repository.local;


import com.kiev.driver.aos.model.entity.Configuration;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface ConfigDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void upsert(Configuration config);

	@Query("DELETE FROM config_table")
	void deleteConfig();

	@Query("SELECT * FROM config_table")
	LiveData<Configuration> getConfigLive();

	@Query("SELECT * FROM config_table")
	Configuration getConfig();
}
