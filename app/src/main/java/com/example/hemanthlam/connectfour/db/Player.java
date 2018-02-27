package com.example.hemanthlam.connectfour.db;

/**
 * Created by sonam on 2/25/18.
 */


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Added a new entity player which contains two columns name and score
 */
@Entity(tableName = "player")
public class Player {

    public void setUid(int uid) {
        this.uid = uid;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "score")
    private int score;

    public int getUid() {
        return uid;
    }

    public Player(){

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
