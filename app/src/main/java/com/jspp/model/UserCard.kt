package com.jspp.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class UserCard(
    val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val avatarUrl: String,
    val bio: String,
    val tags: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val occupation: String = "",
    val education: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val isOnline: Boolean = false,
    val distance: String = "",
    val lastActiveTime: Long = 0L
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

    }
}