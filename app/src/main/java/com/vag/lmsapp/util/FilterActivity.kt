package com.vag.lmsapp.util

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.vag.lmsapp.R

abstract class FilterActivity : AppCompatActivity(), FilterActivityInterface {
    protected var searchBar: SearchView? = null
    override var enableAdvancedFilter: Boolean = true
//    override var enableAddButton: Boolean = false

    private lateinit var toolbar: Toolbar
    protected lateinit var addEditLauncher: ActivityLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        window.statusBarColor = resources.getColor(toolbarBackground, null)

        addEditLauncher = ActivityLauncher(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        menu?.findItem(R.id.menu_advanced_option)?.isVisible = enableAdvancedFilter
//        menu?.findItem(R.id.menu_add)?.isVisible = enableAddButton

        searchBar = menu?.findItem(R.id.menu_search)?.actionView as SearchView
        searchBar?.apply {
            maxWidth = Integer.MAX_VALUE
            queryHint = filterHint
//            setOnQueryTextFocusChangeListener { _, b ->
//                if(b) {
//                    toolbar.setBackgroundColor(applicationContext.getColor(R.color.white))
//                } else {
//                    toolbar.setBackgroundColor(applicationContext.getColor(toolbarBackground))
//                }
//            }
        }
        searchBar?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchBar?.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
            return true
        } else if(item.itemId == R.id.menu_advanced_option) {
            onAdvancedSearchClick()
            return true
//        } else if(item.itemId == R.id.menu_add) {
//            onAddButtonClick()
//            return true
        }
        return super.onOptionsItemSelected(item)
    }
}