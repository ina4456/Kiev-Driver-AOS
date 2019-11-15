/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiev.driver.aos.repository.local;

import android.content.Context;

import com.kiev.driver.aos.AppExecutors;
import com.kiev.driver.aos.Constants;
import com.kiev.driver.aos.model.entity.Configuration;
import com.kiev.driver.aos.model.entity.Notice;
import com.kiev.driver.aos.model.entity.Taxi;
import com.kiev.driver.aos.repository.remote.manager.NetworkManager;
import com.kiev.driver.aos.util.LogHelper;
import com.kiev.driver.aos.util.NavigationExecutor;
import com.kiev.driver.aos.model.entity.Call;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Configuration.class, Taxi.class, Call.class, Notice.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "houston-sp-db";

    public abstract ConfigDao configDao();
    public abstract CallDao callDao();
	public abstract TaxiDao taxiDao();
	public abstract NoticeDao noticeDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database driverInformation and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext, final AppExecutors executors) {
    	LogHelper.i("buildDatabase()");
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            database.setDatabaseCreated();
                            initData(database
		                            , new Configuration()
		                            , new Taxi()
		                            , new Call()
		                            , new Notice());
                        });
                    }
                })
//            .addMigrations(MIGRATION_1_2)
            .build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
	    LogHelper.i("updateDatabaseCreated()");
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
	    LogHelper.i("setDatabaseCreated()");
        mIsDatabaseCreated.postValue(true);
    }

    private static void initData(final AppDatabase appDatabase, Configuration configuration, Taxi taxi, Call call, Notice notice) {
    	LogHelper.i("initData()");
	    appDatabase.runInTransaction(() -> {
	    	//설정 기본값
		    configuration.setNeedAutoLogin(false);
		    configuration.setHasConfirmedPermissionGuide(false);
		    configuration.setUseAutoSendSmsWhenGotCall(true);
		    configuration.setUseAutoRouteToPassenger(true);
		    configuration.setUseAutoRouteToDestination(true);
		    configuration.setUseSpeakerPhone(true);
		    configuration.setNavigation(NavigationExecutor.NAVI_TYPE_TMAP);
		    configuration.setUseMainBtn(true);
		    configuration.setUseCallBtn(true);
		    configuration.setUseBoardingAlightingBtn(true);
		    configuration.setFontSizeInt(Constants.FONT_SIZE_NORMAL);

		    configuration.setCallCenterNumber(Constants.CALL_CENTER_PHONE_NUMBER);
		    configuration.setPassword("0");
		    configuration.setPst(30);
		    configuration.setPsd(3000);
		    configuration.setRc(7);
		    configuration.setRt(7);
		    configuration.setCvt(6);
		    configuration.setConfigurationVersion(0);
		    configuration.setEmergencyPeriodTime(10);

		    configuration.setCallServerIp(NetworkManager.IP_DEV);
		    configuration.setCallServerPort(NetworkManager.PORT_DEV);
		    // FIXME: 2019-09-20 테스트 데이터
		    configuration.setServiceNumber(5);
		    configuration.setCorporation(false);
		    configuration.setCorporationCode(10);

		    appDatabase.configDao().upsert(configuration);


		    //콜 기본값
		    call.setCallStatus(Constants.CALL_STATUS_VACANCY);
		    appDatabase.callDao().insert(call);

		    //택시/기사 기본값
		    // FIXME: 2019-09-03 테스트 데이터
		    taxi.setDriverName("권석범");
		    taxi.setDriverPhoneNumber("010-5055-6980");
		    taxi.setCompanyName("팅크웨어");
		    taxi.setTaxiPlateNumber("37소3150");
		    taxi.setTaxiModel("기아 셀토스");
		    taxi.setTaxiColor("회색");
		    appDatabase.taxiDao().upsert(taxi);

	    });
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    /*private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                + "`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                + "SELECT `id`, `name`, `description` FROM products");

        }
    };*/
}
