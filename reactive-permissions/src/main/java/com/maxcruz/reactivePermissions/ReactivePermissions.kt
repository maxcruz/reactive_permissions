package com.maxcruz.reactivePermissions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.maxcruz.reactivePermissions.entity.Permission
import com.maxcruz.reactivePermissions.ui.BlockedDialog
import com.maxcruz.reactivePermissions.ui.ExplainDialog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * This class, manages the permissions request in Android M and sends the answers using an
 * observable object. The basic flow is:
 *   - Evaluate current permissions (granted and pending)
 *   - Request pending user permissions
 *   - Receive the user answer
 *   - Explain why the permission is need if denied
 *   - Block the app if the permission denied is essential
 *
 *   @property activity Activity where the request is performed
 *   @property requestCode Int coda that identify the request
 *
 *   @author Max Cruz
 */
class ReactivePermissions(private val activity: Activity, private val requestCode: Int) {

    private val observable: Observable<Pair<String, Boolean>>
    private var stack: MutableList<Permission>? = null
    private var subscriber: ObservableEmitter<in Pair<String, Boolean>>? = null
    private var fragment: Fragment? = null

    /**
     * Class constructor that initializes the observable object for the results. When send the
     * answer to each permission in the stack, request the next one
     */
    init {
        observable = Observable.create<Pair<String, Boolean>> { subscriber = it  }
    }

    constructor(fragment: Fragment, requestCode: Int):this(fragment.activity, requestCode) {
        this.fragment = fragment
    }

    /**
     * Get the results of the permission requests
     *
     * @return Observable<Pair<String, Boolean>> the observable object that emmit the results
     */
    fun observeResultPermissions(): Observable<Pair<String, Boolean>> {
        return observable
    }

    /**
     * Answer the permissions already granted, add the rest to the stack pending for approval and
     * starts the request process. In versions previous to Android M, all permissions are
     * granted in this step.
     *
     * @param permissionsRequired List<Permission> evaluated to evaluate
     */
    fun evaluate(permissionsRequired: List<Permission>) {
        val (grantedPermissions, notGrantedPermissions) = permissionsRequired.partition {
            ! needPermission(it.permission)
        }
        grantedPermissions.forEach {
            if (subscriber != null) subscriber!!.onNext(Pair(it.permission, true))
        }
        if (notGrantedPermissions.isEmpty()) {
            if (subscriber != null) subscriber!!.onComplete()
        } else {
            stack = notGrantedPermissions.toMutableList()
            request()
        }
    }

    /**
     * Request approval for the next permission, emit onCompleted if there are no
     * permissions on the stack
     */
    private fun request() {
        val permission = stack!!.firstOrNull()
        if (permission == null) {
            if (subscriber != null) subscriber!!.onComplete()
            return
        }
        val permissions = arrayOf(permission.permission)
        if (fragment != null) (fragment as Fragment).requestPermissions(permissions, requestCode)
        else ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * Receive the response from the user. Should be called inside of onRequestPermissionsResult
     * method in the activity
     *
     * @param evaluated Array<String> evaluated list
     * @param response Array<Int> to each permission in the list, corresponds an response code
     */
    fun receive(evaluated: Array<String>, response: IntArray) {
        val permission = stack!!.firstOrNull() ?: return
        val answer = fun (permission: Permission, result: Boolean) {
            stack!!.remove(permission)
            if (subscriber!= null) subscriber!!.onNext(Pair(permission.permission, result))
            request()
        }
        evaluated.zip(response.toTypedArray()).forEach {
            val result = (it.second ==  PackageManager.PERMISSION_GRANTED)
            if (result) {
                answer.invoke(permission, result)
            } else {
                if (shouldShowExplanation(permission.permission)) {
                    if (permission.explanationResource != null) {
                        explain()
                    } else {
                        if (permission.canContinue) answer.invoke(permission, result)
                        else block()
                    }
                } else {
                    if (permission.canContinue) answer.invoke(permission, result)
                    else block()
                }
            }
        }
    }

    /**
     * Explain why the app needs this permission. After the rationale explanation, retrying the
     * permission, the non essentials can be omitted
     */
    private fun explain() {
        val permission = stack!!.firstOrNull() ?: return
        if (permission.explanationResource == null) {
            stack!!.remove(permission)
            if (subscriber!= null) subscriber!!.onNext(Pair(permission.permission, false))
            request()
            return
        }
        val dialogTag = "explainPermissions"
        val fragmentTransaction = activity.fragmentManager.beginTransaction()
        val previous = activity.fragmentManager.findFragmentByTag(dialogTag)
        if (previous != null) {
            fragmentTransaction.remove(previous)
        }
        val dialog = ExplainDialog.newInstance(permission)
        dialog.show(fragmentTransaction, dialogTag)
        dialog.results.subscribe {
            if (it.second) {
                request()
            } else {
                stack!!.remove(permission)
                if (subscriber!= null) subscriber!!.onNext(Pair(it.first, false))
                request()
            }
        }
    }

    /**
     * If the user choose "Never ask again" in an essential permission, block the app and retry
     * the permission with the preferences
     */
    private fun block() {
        val permission = stack!!.firstOrNull() ?: return
        val dialogTag = "blockedPermission"
        val fragmentTransaction = activity.fragmentManager.beginTransaction()
        val previous = activity.fragmentManager.findFragmentByTag(dialogTag)
        if (previous != null) {
            fragmentTransaction.remove(previous)
        }
        val dialog = BlockedDialog.newInstance(permission.permission,
                ! shouldShowExplanation(permission.permission))
        dialog.show(fragmentTransaction, dialogTag)
        dialog.results.subscribe {
            request()
        }
    }

    /**
     * False if is the first time that the permission is requested or if the user choose
     * "Never ask again", in other cases is true
     *
     * @param permission String the constant name for the permission
     * @return Boolean whether you can show permission rationale UI.
     */
    private fun shouldShowExplanation(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Verify if the given permission is granted
     *
     * @param permission String the constant name for the permission
     * @return Boolean true if the permission is granted, false in some else
     */
    private fun needPermission(permission: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(activity, permission)
        return permissionCheck != PackageManager.PERMISSION_GRANTED
    }

}
