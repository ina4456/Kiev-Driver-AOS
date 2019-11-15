package com.kiev.driver.aos.repository.local;


import com.kiev.driver.aos.model.entity.Taxi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface TaxiDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void upsert(Taxi taxi);

	@Query("SELECT * FROM taxi_table")
	LiveData<Taxi> getTaxiInfoLive();
}
