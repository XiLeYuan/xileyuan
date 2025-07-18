package com.xly.business.user

import android.os.Parcel
import android.os.Parcelable

data class UserInfo(
    val id: String,
    val name: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val occupation: String,
    val education: String,
    val bio: String? = null,
    val hobbies: List<String>? = null,
    val wechat: String? = null,
    val qq: String? = null,
    val avatarUrl: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(age)
        parcel.writeInt(height)
        parcel.writeInt(weight)
        parcel.writeString(occupation)
        parcel.writeString(education)
        parcel.writeString(bio)
        parcel.writeStringList(hobbies)
        parcel.writeString(wechat)
        parcel.writeString(qq)
        parcel.writeString(avatarUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}