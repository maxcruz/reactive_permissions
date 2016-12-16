package com.maxcruz.reactivePermissions.ui

import android.app.DialogFragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.maxcruz.reactivePermissions.R
import com.maxcruz.reactivePermissions.entity.Permission
import kotlinx.android.synthetic.main.explain_permissions.*
import kotlinx.android.synthetic.main.permission_description.*
import rx.subjects.PublishSubject

/**
 * Dialog to explain why an permission is required
 *
 * @author Max Cruz
 */
class ExplainDialog() : DialogFragment() {

    /**
     * Subject object to send the button click event in the dialog
     */
    val results: PublishSubject<Pair<String, Boolean>> = PublishSubject.create()

    /**
     * Static stuff
     */
    companion object {

        private val PERMISSION_PARAM: String = "permission"

        /**
         * Returns a new instance of this dialog
         *
         * @param permission Permission object to explain
         * @return Instance for this dialog fragment
         */
        fun newInstance(permission: Permission): ExplainDialog {
            val instance = ExplainDialog()
            val arguments = Bundle()
            arguments.putParcelable(PERMISSION_PARAM, permission)
            instance.arguments = arguments
            instance.isCancelable = false
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
        val fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        view.startAnimation(fadeIn)
        return view
    }

    /**
     * When the view is created, get the permission from the argument and load it
     *
     * @param view View returned by onCreateView
     * @param savedInstanceState Bundle previous saved state as given here.
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val permission = arguments.getParcelable<Permission>(PERMISSION_PARAM)
        val (icon, name) = permissionInfo(permission.permission)
        explainPermission(
                permission.permission,
                permission.explanationResource,
                name,
                icon,
                permission.canContinue
        )
    }

    /**
     * Load the dialog interface
     *
     * @param permission String constant name for the permission (get from the system)
     * @param resource Int the string resource that explains the need
     * @param name String label name for the permission
     * @param icon Int Group icon resource for the permission
     * @param canContinue Boolean defines if the permission is essential
     */
    fun explainPermission(permission: String, resource: Int?, name: String, icon: Int,
                          canContinue: Boolean) {
        val listener = fun (value: Boolean) {
            results.onNext(Pair(permission, value))
            finish()
        }
        permissionIcon.setImageResource(icon)
        permissionTitle.text = name
        if (resource != null) permissionMessage.text = getString(resource)
        if (canContinue) {
            confirmButton.setOnClickListener { listener(false) }
        } else {
            confirmButton.visibility = View.GONE
        }
        retryButton.setOnClickListener { listener(true) }
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
     * Retrieve the permission info from the system
     *
     * @param permission String the constant name for the permission (get from system)
     * @return Pair<Int, String> the first is the icon resource and the second the label name
     */
    private fun permissionInfo(permission: String) : Pair<Int, String> {
        val i = PackageManager.GET_META_DATA
        val permissionInfo = activity.packageManager.getPermissionInfo(permission, i)
        val groupInfo = activity.packageManager.getPermissionGroupInfo(permissionInfo.group, i)
        val permissionName = getString(permissionInfo.labelRes).capitalize()
        val icon = groupInfo.icon
        return Pair(icon, permissionName)
    }

}