package org.dhis2.android.rtsm.ui.home.model

import org.dhis2.android.rtsm.data.TransactionType
import org.dhis2.android.rtsm.data.TransactionType.DISTRIBUTION
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit

data class SettingsUiState(
    val transactionType: TransactionType = DISTRIBUTION,
    val facility: OrganisationUnit? = null,
    val destination: Option? = null
) {
    fun isDistribution() = transactionType == DISTRIBUTION

    fun hasFacilitySelected() = facility != null

    fun hasDestinationSelected() = destination != null
}