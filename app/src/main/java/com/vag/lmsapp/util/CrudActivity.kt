package com.vag.lmsapp.util

import android.content.Intent
import android.os.Bundle
import com.google.android.material.card.MaterialCardView
import com.vag.lmsapp.R
import java.util.*

abstract class CrudActivity : BaseActivity(), CrudActivityInterface {
    companion object {
        const val ENTITY_ID = "entity_id"
        const val ACTION_SAVE = 1
        const val ACTION_DELETE = 2
    }

    private lateinit var authLauncher: AuthLauncherActivity
    private var buttonSave: MaterialCardView? = null
    private var buttonDelete: MaterialCardView? = null
    private var buttonCancel: MaterialCardView? = null

    override fun onStart() {
        super.onStart()

        buttonSave = findViewById(R.id.card_button_confirm)
        buttonDelete = findViewById(R.id.card_button_delete)
        buttonCancel = findViewById(R.id.card_button_close)

        buttonSave?.setOnClickListener {
            onSave()
        }

        buttonDelete?.setOnClickListener {
            onDelete()
        }

        buttonCancel?.setOnClickListener {
            requestExit()
        }

        authLauncher.onOk = { loginCredentials, code ->
//            val loginCredentials = it.data?.getParcelableExtra<LoginCredentials>(
//                AuthActionDialogActivity.RESULT)

            when (code) {
                ACTION_SAVE -> this.confirmSave(loginCredentials)
                ACTION_DELETE ->  this.confirmDelete(loginCredentials)
            }
        }
    }

    override fun onSave() {
        authenticate(ACTION_SAVE)
    }

    override fun onDelete() {
        showDeleteDialog()
    }

    protected open fun authenticate(code: Int) {
//        val intent = Intent(this, AuthActionDialogActivity::class.java).apply {
//            this.action = action
//        }
        authLauncher.launch(emptyList(), code)
    }

    private fun showDeleteDialog() {
        showDeleteConfirmationDialog {
            authenticate(ACTION_DELETE)
        }
//        AlertDialog.Builder(this).apply {
//            setTitle("Delete this item")
//            setMessage("Are you sure you want to proceed?")
//            setPositiveButton("Yes") { _, _ ->
//                authenticate(ACTION_DELETE)
//            }
//            setNegativeButton("Cancel") { _, _ ->
//
//            }
//            create()
//        }.show()
    }

    override fun onStop() {
        super.onStop()
        buttonSave?.setOnClickListener(null)
        buttonDelete?.setOnClickListener(null)
        buttonCancel?.setOnClickListener(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authLauncher = AuthLauncherActivity(this)

        intent.getStringExtra(ENTITY_ID).toUUID().let {
            this.get(it)
        }
    }

    override fun confirmExit(entityId: UUID?) {
        val intent = Intent().apply {
            action = intent.action
            putExtra(ENTITY_ID, entityId.toString())
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}