package org.dhis2.usescases.teiDashboard.dashboardfragments.relationships

import io.reactivex.Single
import org.dhis2.Bindings.profilePicturePath
import org.dhis2.Bindings.userFriendlyValue
import org.dhis2.R
import org.dhis2.utils.resources.ResourceManager
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

class RelationshipRepositoryImpl(
    private val d2: D2,
    private val config: RelationshipConfiguration,
    private val resources: ResourceManager
) : RelationshipRepository {

    override fun relationshipTypes(): Single<List<Pair<RelationshipType, String>>> {
        return when (config) {
            is EventRelationshipConfiguration -> stageRelationshipTypes()
            is TrackerRelationshipConfiguration -> trackerRelationshipTypes()
        }
    }

    override fun relationships(): Single<List<RelationshipViewModel>> {
        return when (config) {
            is EventRelationshipConfiguration -> eventRelationships()
            is TrackerRelationshipConfiguration -> enrollmentRelationships()
        }
    }

    private fun trackerRelationshipTypes(): Single<List<Pair<RelationshipType, String>>> {
        // TODO: Limit link to only TEI
        val teTypeUid = d2.trackedEntityModule().trackedEntityInstances()
            .uid((config as TrackerRelationshipConfiguration).teiUid)
            .blockingGet().trackedEntityType() ?: return Single.just(emptyList())

        return d2.relationshipModule().relationshipTypes()
            .withConstraints()
            .byConstraint(RelationshipEntityType.TRACKED_ENTITY_INSTANCE, teTypeUid)
            .get().map { relationshipTypes ->
                relationshipTypes.mapNotNull { relationshipType ->
                    val secondaryTeTypeUid = when {
                        relationshipType.fromConstraint()?.trackedEntityType()
                            ?.uid() == teTypeUid ->
                            relationshipType.toConstraint()?.trackedEntityType()?.uid()
                        relationshipType.bidirectional() == true && relationshipType.toConstraint()
                            ?.trackedEntityType()?.uid() == teTypeUid ->
                            relationshipType.fromConstraint()?.trackedEntityType()?.uid()
                        else -> null
                    }
                    secondaryTeTypeUid?.let {
                        Pair(relationshipType, secondaryTeTypeUid)
                    }
                }
            }
    }

    private fun stageRelationshipTypes(): Single<List<Pair<RelationshipType, String>>> {
        // TODO: Limit links to TEI
        val event = d2.eventModule().events().uid(
            (config as EventRelationshipConfiguration).eventUid
        ).blockingGet()
        val programStageUid = event.programStage() ?: ""
        val programUid = event.program() ?: ""
        return d2.relationshipModule().relationshipTypes()
            .withConstraints()
            .byAvailableForEvent(event.uid())
            .get().map { relationshipTypes ->
                relationshipTypes.mapNotNull { relationshipType ->
                    val secondaryUid = when {
                        relationshipType.fromConstraint()?.programStage()
                            ?.uid() == programStageUid ->
                            relationshipType.toConstraint()?.trackedEntityType()?.uid()
                        relationshipType.fromConstraint()?.program()?.uid() == programUid ->
                            relationshipType.toConstraint()?.trackedEntityType()?.uid()
                        relationshipType.bidirectional() == true && relationshipType.toConstraint()
                            ?.programStage()?.uid() == programStageUid ->
                            relationshipType.fromConstraint()?.trackedEntityType()?.uid()
                        relationshipType.bidirectional() == true && relationshipType.toConstraint()
                            ?.program()?.uid() == programUid ->
                            relationshipType.fromConstraint()?.trackedEntityType()?.uid()
                        else -> null
                    }
                    secondaryUid?.let { Pair(relationshipType, secondaryUid) }
                }
            }
    }

    fun eventRelationships(): Single<List<RelationshipViewModel>> {
        val eventUid = (config as EventRelationshipConfiguration).eventUid
        return Single.fromCallable {
            d2.relationshipModule().relationships().getByItem(
                RelationshipItem.builder().event(
                    RelationshipItemEvent.builder().event(eventUid).build()
                ).build()
            ).mapNotNull { relationship ->
                val relationshipType =
                    d2.relationshipModule().relationshipTypes().uid(relationship.relationshipType())
                        .blockingGet()

                val relationshipOwnerUid: String?
                val direction: RelationshipDirection
                if (eventUid != relationship.from()?.event()?.event()) {
                    relationshipOwnerUid =
                        relationship.from()?.trackedEntityInstance()?.trackedEntityInstance()
                    direction = RelationshipDirection.FROM
                } else {
                    relationshipOwnerUid =
                        relationship.to()?.trackedEntityInstance()?.trackedEntityInstance()
                    direction = RelationshipDirection.TO
                }
                if (relationshipOwnerUid == null) return@mapNotNull null

                val event = d2.eventModule().events()
                    .withTrackedEntityDataValues().uid(eventUid).blockingGet()
                val tei = d2.trackedEntityModule().trackedEntityInstances()
                    .withTrackedEntityAttributeValues().uid(relationshipOwnerUid).blockingGet()

                val (fromGeometry, toGeometry) =
                    if (direction == RelationshipDirection.FROM) {
                        Pair(
                            tei.geometry(),
                            event.geometry()
                        )
                    } else {
                        Pair(
                            event.geometry(),
                            tei.geometry()
                        )
                    }
                val (fromValues, toValues) =
                    if (direction == RelationshipDirection.FROM) {
                        Pair(
                            getTeiAttributesForRelationship(relationshipOwnerUid),
                            getEventValuesForRelationship(eventUid)
                        )
                    } else {
                        Pair(
                            getEventValuesForRelationship(eventUid),
                            getTeiAttributesForRelationship(relationshipOwnerUid)
                        )
                    }

                val (fromProfilePic, toProfilePic) =
                    if (direction == RelationshipDirection.FROM) {
                        Pair(
                            tei.profilePicturePath(d2, null),
                            null
                        )
                    } else {
                        Pair(
                            null,
                            tei.profilePicturePath(d2, null)
                        )
                    }

                val (fromDefaultPic, toDefaultPic) =
                    if (direction == RelationshipDirection.FROM) {
                        Pair(
                            getTeiDefaultRes(tei),
                            getEventDefaultRes(event)
                        )
                    } else {
                        Pair(
                            getEventDefaultRes(event),
                            getTeiDefaultRes(tei)
                        )
                    }

                RelationshipViewModel(
                    relationship,
                    fromGeometry,
                    toGeometry,
                    relationshipType,
                    direction,
                    relationshipOwnerUid,
                    RelationshipOwnerType.TEI,
                    fromValues,
                    toValues,
                    fromProfilePic,
                    toProfilePic,
                    fromDefaultPic,
                    toDefaultPic,
                    getOwnerColor(relationshipOwnerUid, RelationshipOwnerType.TEI)
                )
            }
        }
    }

    fun enrollmentRelationships(): Single<List<RelationshipViewModel>> {
        val teiUid = (config as TrackerRelationshipConfiguration).teiUid
        val tei = d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid).blockingGet()
        val programUid = d2.enrollmentModule().enrollments()
            .uid(config.enrollmentUid).blockingGet().program()
        return Single.fromCallable {
            d2.relationshipModule().relationships().getByItem(
                RelationshipItem.builder().trackedEntityInstance(
                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(teiUid)
                        .build()
                ).build()
            ).mapNotNull { relationship ->
                val relationshipType =
                    d2.relationshipModule().relationshipTypes().uid(relationship.relationshipType())
                        .blockingGet()
                val direction: RelationshipDirection
                val relationshipOwnerUid: String?
                val relationshipOwnerType: RelationshipOwnerType?
                val fromGeometry: Geometry?
                val toGeometry: Geometry?
                val fromValues: List<Pair<String, String>>
                val toValues: List<Pair<String, String>>
                val fromProfilePic: String?
                val toProfilePic: String?
                val fromDefaultPicRes: Int
                val toDefaultPicRes: Int

                when (teiUid) {
                    relationship.from()?.trackedEntityInstance()?.trackedEntityInstance() -> {
                        direction = RelationshipDirection.TO
                        fromGeometry = tei.geometry()
                        fromValues = getTeiAttributesForRelationship(teiUid)
                        fromProfilePic = tei.profilePicturePath(d2, programUid)
                        fromDefaultPicRes = getTeiDefaultRes(tei)
                        if (relationship.to()?.trackedEntityInstance() != null) {
                            relationshipOwnerType = RelationshipOwnerType.TEI
                            relationshipOwnerUid =
                                relationship.to()?.trackedEntityInstance()?.trackedEntityInstance()
                            val toTei = d2.trackedEntityModule().trackedEntityInstances()
                                .uid(relationshipOwnerUid).blockingGet()
                            toGeometry = toTei.geometry()
                            toValues = getTeiAttributesForRelationship(toTei.uid())
                            toProfilePic = toTei.profilePicturePath(d2, programUid)
                            toDefaultPicRes = getTeiDefaultRes(toTei)
                        } else {
                            relationshipOwnerType = RelationshipOwnerType.EVENT
                            relationshipOwnerUid =
                                relationship.to()?.event()?.event()
                            val toEvent = d2.eventModule().events()
                                .uid(relationshipOwnerUid).blockingGet()
                            toGeometry = toEvent.geometry()
                            toValues = getEventValuesForRelationship(toEvent.uid())
                            toProfilePic = null
                            toDefaultPicRes = getEventDefaultRes(toEvent)
                        }
                    }
                    relationship.to()?.trackedEntityInstance()?.trackedEntityInstance() -> {
                        direction = RelationshipDirection.FROM
                        toGeometry = tei.geometry()
                        toValues = getTeiAttributesForRelationship(teiUid)
                        toProfilePic = tei.profilePicturePath(d2, programUid)
                        toDefaultPicRes = getTeiDefaultRes(tei)
                        if (relationship.from()?.trackedEntityInstance() != null) {
                            relationshipOwnerType = RelationshipOwnerType.TEI
                            relationshipOwnerUid =
                                relationship.from()?.trackedEntityInstance()
                                    ?.trackedEntityInstance()
                            val fromTei = d2.trackedEntityModule().trackedEntityInstances()
                                .uid(relationshipOwnerUid).blockingGet()
                            fromGeometry = fromTei.geometry()
                            fromValues = getTeiAttributesForRelationship(fromTei.uid())
                            fromProfilePic = fromTei.profilePicturePath(d2, programUid)
                            fromDefaultPicRes = getTeiDefaultRes(fromTei)
                        } else {
                            relationshipOwnerType = RelationshipOwnerType.EVENT
                            relationshipOwnerUid =
                                relationship.from()?.event()?.event()
                            val fromEvent = d2.eventModule().events()
                                .uid(relationshipOwnerUid).blockingGet()
                            fromGeometry = fromEvent.geometry()
                            fromValues = getEventValuesForRelationship(fromEvent.uid())
                            fromProfilePic = null
                            fromDefaultPicRes = getEventDefaultRes(fromEvent)
                        }
                    }
                    else -> return@mapNotNull null
                }

                if (relationshipOwnerUid == null) return@mapNotNull null

                RelationshipViewModel(
                    relationship,
                    fromGeometry,
                    toGeometry,
                    relationshipType,
                    direction,
                    relationshipOwnerUid,
                    relationshipOwnerType,
                    fromValues,
                    toValues,
                    fromProfilePic,
                    toProfilePic,
                    fromDefaultPicRes,
                    toDefaultPicRes,
                    getOwnerColor(relationshipOwnerUid, relationshipOwnerType)
                )
            }
        }
    }

    private fun getOwnerColor(uid: String, relationshipOwnerType: RelationshipOwnerType): Int {
        return when (relationshipOwnerType) {
            RelationshipOwnerType.EVENT -> {
                val event = d2.eventModule().events().uid(uid).blockingGet()
                val program = d2.programModule().programs().uid(event.program()).blockingGet()
                if (program.programType() == ProgramType.WITHOUT_REGISTRATION) {
                    resources.getColorFrom(program.style().color())
                } else {
                    val programStage =
                        d2.programModule().programStages().uid(event.programStage()).blockingGet()
                    resources.getColorFrom(programStage.style().color() ?: program.style().color())
                }
            }
            RelationshipOwnerType.TEI -> {
                val tei = d2.trackedEntityModule().trackedEntityInstances()
                    .uid(uid).blockingGet()
                val teType = d2.trackedEntityModule().trackedEntityTypes()
                    .uid(tei.trackedEntityType()).blockingGet()
                return resources.getColorFrom(teType.style().color())
            }
        }
    }

    private fun getTeiAttributesForRelationship(teiUid: String): List<Pair<String, String>> {
        val attrFromType = d2.trackedEntityModule().trackedEntityTypeAttributes()
            .byDisplayInList().isTrue.blockingGet().mapNotNull {
                it.trackedEntityAttribute()?.uid() to it
            }.toMap()

        val attrValuesFromType = d2.trackedEntityModule().trackedEntityAttributeValues()
            .byTrackedEntityInstance().eq(teiUid)
            .byTrackedEntityAttribute().`in`(attrFromType.keys.toList())
            .blockingGet().mapNotNull { attributeValue ->
                val fieldName = d2.trackedEntityModule().trackedEntityAttributes()
                    .uid(attributeValue.trackedEntityAttribute()).blockingGet().displayFormName()
                val value = attributeValue.userFriendlyValue(d2)
                if (fieldName != null && value != null) {
                    Pair(fieldName, value)
                } else {
                    null
                }
            }

        return if (attrValuesFromType.isNotEmpty()) {
            attrValuesFromType
        } else {
            listOf(Pair("uid", teiUid))
        }
    }

    private fun getEventValuesForRelationship(eventUid: String): List<Pair<String, String>> {
        val event =
            d2.eventModule().events().withTrackedEntityDataValues().uid(eventUid).blockingGet()
        val deFromEvent = d2.programModule().programStageDataElements()
            .byProgramStage().eq(event.programStage())
            .byDisplayInReports().isTrue.blockingGetUids()

        val valuesFromEvent = event.trackedEntityDataValues()?.mapNotNull {
            if (!deFromEvent.contains(it.dataElement())) return@mapNotNull null
            val formName = d2.dataElementModule().dataElements().uid(it.dataElement()).blockingGet()
                .displayName()
            val value = it.userFriendlyValue(d2)
            if (formName != null && value != null) {
                Pair(formName, value)
            } else {
                null
            }
        } ?: emptyList()

        return if (valuesFromEvent.isNotEmpty()) {
            valuesFromEvent
        } else {
            val stage = d2.programModule().programStages().uid(event.programStage()).blockingGet()
            listOf(Pair("displayName", stage.displayName() ?: event.uid()))
        }
    }

    private fun getTeiDefaultRes(tei: TrackedEntityInstance): Int {
        val teiType =
            d2.trackedEntityModule().trackedEntityTypes().uid(tei.trackedEntityType()).blockingGet()
        return getTeiTypeDefaultRes(teiType.uid())
    }

    override fun getTeiTypeDefaultRes(teiTypeUid: String): Int {
        val teiType =
            d2.trackedEntityModule().trackedEntityTypes().uid(teiTypeUid).blockingGet()
        return resources.getObjectStyleDrawableResource(
            teiType.style().icon(),
            R.drawable.photo_temp_gray
        )
    }

    override fun getEventProgram(eventUid: String): String {
        return d2.eventModule().events().uid(eventUid).blockingGet().program() ?: ""
    }

    private fun getEventDefaultRes(event: Event): Int {
        val stage = d2.programModule().programStages().uid(event.programStage()).blockingGet()
        val program = d2.programModule().programs().uid(event.program()).blockingGet()
        return resources.getObjectStyleDrawableResource(
            stage.style().icon() ?: program.style().icon(),
            R.drawable.photo_temp_gray
        )
    }
}

sealed class RelationshipConfiguration
data class TrackerRelationshipConfiguration(
    val enrollmentUid: String,
    val teiUid: String
) : RelationshipConfiguration()

data class EventRelationshipConfiguration(
    val eventUid: String
) : RelationshipConfiguration()