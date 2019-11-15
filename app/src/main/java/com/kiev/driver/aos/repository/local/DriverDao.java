package com.kiev.driver.aos.repository.local;


import com.kiev.driver.aos.model.entity.Driver;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface DriverDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void upsert(Driver driver);

	@Query("SELECT * FROM driver_table")
	LiveData<Driver> getDriverInformation();
}
