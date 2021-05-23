package com.minuminu.haruu.wheremyhome.db.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomeInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String? = "",
    var address: String? = "", // 주소
    var deposit: Int = 0, // 보증금
    var rental: Int = 0, // 월세
    var expense: Float = 0f, // 관리비
    @ColumnInfo(name = "start_date")
    var startDate: String? = "", // 계약가능일(시작)
    @ColumnInfo(name = "end_date")
    var endDate: String? = "", // 계약가능일(종료)
    var score: Int = 0, // 점수
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun toString(): String {
        return "HomeInfo(id='$id', name='$name', address='$address', deposit=$deposit, rental=$rental, expense=$expense, startDate='$startDate', endDate='$endDate', score=$score')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeInt(deposit)
        parcel.writeInt(rental)
        parcel.writeFloat(expense)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeInt(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeInfo> {
        override fun createFromParcel(parcel: Parcel): HomeInfo {
            return HomeInfo(parcel)
        }

        override fun newArray(size: Int): Array<HomeInfo?> {
            return arrayOfNulls(size)
        }
    }
}