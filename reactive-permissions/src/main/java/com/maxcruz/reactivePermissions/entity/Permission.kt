package com.maxcruz.reactivePermissions.entity

/**
 * Data class to describe an permission
 *
 * @property permission String constant name for the permission (get from system)
 * @property explanationResource Int string resource that explain the permission
 * @property canContinue Boolean this permission is essential?
 *
 * @author Max Cruz
 */
data class Permission(val permission: String, val explanationResource: Int?, val canContinue: Boolean)