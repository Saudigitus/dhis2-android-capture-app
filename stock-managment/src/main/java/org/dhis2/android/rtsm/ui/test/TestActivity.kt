package org.dhis2.android.rtsm.ui.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import org.dhis2.android.rtsm.R
import org.dhis2.commons.filters.FilterManager
import org.dhis2.commons.orgunitselector.OUTreeFragment

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        openOrgUnitTreeSelector()

    }

    fun openOrgUnitTreeSelector() {
        OUTreeFragment.newInstance(
            true,
            FilterManager.getInstance().orgUnitFilters.map { it.uid() }.toMutableList()
        ).apply {
//            selectionCallback = this@TestActivity
        }.show(supportFragmentManager, "OUTreeFragment")
    }
}