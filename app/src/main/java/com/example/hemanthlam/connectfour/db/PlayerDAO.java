package com.example.hemanthlam.connectfour.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by sonam on 2/25/18.
 */
//This class provides all the insert, update, select and delete queries related to the player table in database
@Dao
 public interface PlayerDAO {

    @Query("SELECT * FROM player")
    List<Player> getAll();

    @Query("SELECT * from player where name LIKE :name")
    Player getPlayer(String name);

    @Insert
    void insertAll(Player... players);

    @Delete
    void delete(Player player);

   @Query("SELECT * FROM player ORDER BY score DESC LIMIT 5")
   List<Player> getTop5Scores();

   @Update
    void update(Player player);
}
