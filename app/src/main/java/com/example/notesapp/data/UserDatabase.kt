package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notesapp.model.User

@Database(entities = [User::class], version = 5, exportSchema = false)
@TypeConverters(ImageBitmapString::class)
abstract class UserDatabase :RoomDatabase(){

    abstract fun userDao():UserDao

    //created singleton object for RoomDatabase class
    companion object{
        @Volatile
        private var INSTANCE:UserDatabase?=null

        fun getDatabase(context: Context):UserDatabase{
            val tempInstance= INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE=instance
                return instance
            }
        }
    }

}