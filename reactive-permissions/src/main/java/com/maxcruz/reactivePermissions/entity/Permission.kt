package com.maxcruz.reactivePermissions.entity

import android.os.Parcel
import android.os.Parcelable


/**
 * Data class to describe an permission
 *
 * @property permission String constant name for the permission (get from system)
 * @property explanationResource Int string resource that explain the permission
 * @property canContinue Boolean this permission is essential?
 *
 * @author Max Cruz
 */
class Permission : Parcelable {

    val permission: String
    val explanationResource: Int?
    val canContinue: Boolean

    constructor(permission: String, explanationResource: Int?, canContinue: Boolean) {
        this.permission = permission
        this.explanationResource = explanationResource
        this.canContinue = canContinue
    }

    constructor(source: Parcel) {
        this.permission = source.readString()
        val resource = source.readInt()
        this.explanationResource = if (resource != 0) resource else null
        this.canContinue = 1 == source.readInt()
    }

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<Permission> = object : Parcelable.Creator<Permission> {

            override fun createFromParcel(source: Parcel): Permission = Permission(source)

            override fun newArray(size: Int): Array<Permission?> = arrayOfNulls(size)

        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(permission)
        if (explanationResource == null)
            dest?.writeInt(0)
        else
            dest?.writeInt(explanationResource)
        dest?.writeInt((if (canContinue) 1 else 0))
    }

}