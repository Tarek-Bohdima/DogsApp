package com.example.android.dogsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DogEntity::class, FavoriteEntity::class], version = 2, exportSchema = false)
abstract class DogsDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
    abstract fun favoriteDao(): FavoriteDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `favorites` " +
                "(`imageUrl` TEXT NOT NULL, PRIMARY KEY(`imageUrl`))"
        )
    }
}
