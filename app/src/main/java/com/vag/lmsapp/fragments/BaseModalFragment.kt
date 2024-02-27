package com.vag.lmsapp.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.vag.lmsapp.util.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseModalFragment: BottomSheetDialogFragment() {
    protected var closeOnTouchOutside = true
    protected var dismissed = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            val sheet = it as BottomSheetDialog
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissed = true
    }
    override fun show(manager: FragmentManager, tag: String?) {
        dialog?.let {
            val sheet = it as BottomSheetDialog
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        if(dismissed) {
            super.show(manager, tag)
        }
        dismissed = false
    }

    override fun onStart() {
        super.onStart()
        val touchOutsideView = dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
        touchOutsideView?.setOnClickListener {
            if(!it.hideKeyboard() && closeOnTouchOutside) {
                dismiss()
            }
        }
    }
}