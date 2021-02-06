package com.fct.mvvm.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fct.mvvm.data.LaunchEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow


@Dao
abstract class LaunchDao {

    //region SQL methods using Coroutines/Flow

    /**
     * Return the latest [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0
        ORDER BY dateUnix desc
        LIMIT 1
    """
    )
   abstract fun getLatestLaunch(): Flow<LaunchEntity?>

    /**
     * Return a list of all upcoming [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 1  
        ORDER BY dateUnix asc
    """
    )
    abstract fun getUpcomingLaunches(): Flow<List<LaunchEntity>>

    /**
     * Return a list of the past [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0  
        ORDER BY dateUnix desc
    """
    )
    abstract fun getPastLaunches(): Flow<List<LaunchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(launchEntity: LaunchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(launchCollection: List<LaunchEntity>)

    @Query("DELETE FROM launch_entity")
    abstract suspend fun deleteAll()

    //endregion

    //region SQL methods using RxJava

    /**
     * Fetch the latest [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0
        ORDER BY dateUnix desc
        LIMIT 1
    """
    )
    abstract fun fetchLatestLaunch(): Maybe<LaunchEntity>

    /**
     * Return a list of all upcoming [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 1  
        ORDER BY dateUnix asc
    """
    )
    abstract fun fetchUpcomingLaunches(): Maybe<List<LaunchEntity>>

    /**
     * Return a list of the past [LaunchEntity]
     */
    @Query(
        """
        SELECT * FROM launch_entity 
        WHERE upcoming = 0  
        ORDER BY dateUnix desc
    """
    )
    abstract fun fetchPastLaunches(): Maybe<List<LaunchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertStandard(launchEntity: LaunchEntity) : Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllStandard(launchCollection: List<LaunchEntity>)

    //endregion

}