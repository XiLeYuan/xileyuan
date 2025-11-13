package com.jspp.model

import android.os.Parcel
import android.os.Parcelable

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
    val lastActiveTime: Long = 0L,
    val hometown: String = "",
    val residence: String = ""
) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(age)
        parcel.writeString(location)
        parcel.writeString(avatarUrl)
        parcel.writeString(bio)
        parcel.writeStringList(tags)
        parcel.writeStringList(photos)
        parcel.writeString(occupation)
        parcel.writeString(education)
        parcel.writeInt(height)
        parcel.writeInt(weight)
        parcel.writeByte(if (isOnline) 1 else 0)
        parcel.writeString(distance)
        parcel.writeLong(lastActiveTime)
        parcel.writeString(hometown)
        parcel.writeString(residence)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object CREATOR : Parcelable.Creator<UserCard> {
        override fun createFromParcel(parcel: Parcel): UserCard {
            return UserCard(parcel)
        }
        
        override fun newArray(size: Int): Array<UserCard?> {
            return arrayOfNulls(size)
        }
    }
}