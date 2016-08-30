package com.maxcruz.reactivePermissions.entity

/**
 * Data class to describe an permission
 *
 * @param permission String constant name for the permission (get from system)
 * @param explanationResource Int string resource that explain the permission
 * @param canContinue Boolean this permission is essential?
 *
 * @author Max Cruz
 */
data class Permission(
        val permission: String,
        val explanationResource: Int?,
        val canContinue: Boolean)