package com.vag.lmsapp.fragments

import android.content.DialogInterface

open class ModalFragment<T> : BaseModalFragment() {
    companion object {
        const val PAYLOAD = "payload"
    }
//    protected var dismissed = true
    var onOk: ((T) -> Unit) ? = null

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        dialog?.let {
//            val sheet = it as BottomSheetDialog
//            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }
//    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
//        dismissed = true
        onOk = null
    }
//    override fun show(manager: FragmentManager, tag: String?) {
//        if(dismissed) {
//            super.show(manager, tag)
//        }
//        dismissed = false
//    }
//    override fun onStart() {
//        super.onStart()
//        val touchOutsideView = dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
//        touchOutsideView?.setOnClickListener {
//            it.hideKeyboard()
//        }
//    }
}