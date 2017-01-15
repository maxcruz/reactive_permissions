package com.maxcruz.reactivePermissions.ui

import android.app.DialogFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.maxcruz.reactivePermissions.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.explain_permissions.*
import kotlinx.android.synthetic.main.permission_description.*

/**
 * Dialog to explain that an essential permission is blocked
 *
 * @author Max Cruz
 */
class BlockedDialog : DialogFragment() {

    /**
     * Subject object to send the retry event
     */
    val results: PublishSubject<String> = PublishSubject.create()
    private var retryPermission: String? = null

    /**
     * Static stuff
     */
    companion object {

        private val BLOCKED_PERMISSION_PARAM: String = "blockedPermission"
        private val EXTERNAL_PARAM: String = "external"

        /**
         * Returns a new instance of this dialog
         *
         * @param permission String constant name for the permission (get from the system)
         * @return Instance for this dialog fragment
         */
        fun newInstance(permission: String, external: Boolean): BlockedDialog {
            val instance = BlockedDialog()
            val arguments = Bundle()
            instance.isCancelable = false
            arguments.putString(BLOCKED_PERMISSION_PARAM, permission)
            arguments.putBoolean(EXTERNAL_PARAM, external)
            instance.arguments = arguments
            return instance
        }
    }

    /**
     * Inflate the layout view and attach it to the fragment
     *
     * @param inflater LayoutInflater used to inflate any views in the fragment
     * @param container ViewGroup this is the parent view that the fragment's UI should be attached
     * @param savedInstanceState Bundle previous saved state as given here.
     * @return View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.explain_permissions, container, false)
        return view
    }

    /**
     * When the view is created, get the permission from the argument and load it
     *
     * @param view View returned by onCreateView
     * @param savedInstanceState Bundle previous saved state as given here.
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val permission = arguments.getString(BLOCKED_PERMISSION_PARAM)
        val external = arguments.getBoolean(EXTERNAL_PARAM)
        val permissionInfo = activity.packageManager.getPermissionInfo(permission,
                PackageManager.GET_META_DATA)
        val permissionName = getString(permissionInfo.labelRes)
        val appName = getString(activity.application.applicationInfo.labelRes)
        explainPermission(permission, permissionName, appName, external)
    }

    /**
     * Load the layout that explains that an essential permission is denied permanently.
     * When click in the retry button open the preferences
     *
     * @param permission String permission constant name
     * @param permissionName String the permission label
     * @param appName String the name of the app
     */
    @Suppress("DEPRECATION")
    fun explainPermission(permission: String, permissionName: String, appName: String, external: Boolean) {
        confirmButton.text = getString(R.string.explain_blocked_permission_close)
        val baseMessage = getString(R.string.explain_blocked_permission_dialog)
        val message = String.format(baseMessage, "<b>$permissionName</b>", appName)
        val fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        permissionMessage.startAnimation(fadeIn)
        permissionMessage.text = Html.fromHtml(message)
        confirmButton.setOnClickListener {
            activity.finish()
        }
        retryButton.setOnClickListener {
            if (external) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + activity.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                activity.startActivity(intent)
                retryPermission = permission
            } else {
                finish()
                results.onNext(permission)
            }
        }
    }

    /**
     * Animate when dismiss
     */
    private fun finish() {
        val fadeOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        view.startAnimation(fadeOut)
        dismiss()
    }

    /**
     * Called when the fragment is visible to the user and actively running
     */
    override fun onResume() {
        if (retryPermission != null) {
            finish()
            results.onNext(retryPermission)
        }
        super.onResume()
    }

}