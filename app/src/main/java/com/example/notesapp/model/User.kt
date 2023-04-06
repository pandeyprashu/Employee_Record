package com.example.notesapp.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)

    val id:Int,
    val image: String,
    val birth:String,
    val Name:String,
    val OfficialEmail:String,
    val PersonalEmail:String,
    val Phone:String,
    val Address:String,
    val Designation:String,
    val Experience:String,
    val Years:String,
    val CurrentOrganisation:String
    ):Parcelable