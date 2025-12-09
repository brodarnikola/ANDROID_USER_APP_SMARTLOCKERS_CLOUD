///* SWISS INNOVATION LAB CONFIDENTIAL
//*
//* www.swissinnolab.com
//* __________________________________________________________________________
//*
//* [2016] - [2018] Swiss Innovation Lab AG
//* All Rights Reserved.
//*
//* @author mfatiga
//*
//* NOTICE:  All information contained herein is, and remains
//* the property of Swiss Innovation Lab AG and its suppliers,
//* if any.  The intellectual and technical concepts contained
//* herein are proprietary to Swiss Innovation Lab AG
//* and its suppliers and may be covered by E.U. and Foreign Patents,
//* patents in process, and are protected by trade secret or copyright law.
//* Dissemination of this information or reproduction of this material
//* is strictly forbidden unless prior written permission is obtained
//* from Swiss Innovation Lab AG.
//*/
//
//package hr.sil.android.myappbox.cache
//
//import android.content.Context
//import hr.sil.android.datacache.AutoCache
//import hr.sil.android.datacache.TwoLevelCache
//import hr.sil.android.datacache.updatable.CacheSource
//import hr.sil.android.datacache.util.PersistenceClassTracker
//import hr.sil.android.myappbox.App
//import hr.sil.android.myappbox.cache.dto.RAvailableLockerSizesDTO
//import hr.sil.android.myappbox.store.MPLDeviceStore
//import hr.sil.android.myappbox.R
//import hr.sil.android.myappbox.core.remote.WSUser
//import hr.sil.android.myappbox.core.remote.model.*
//import hr.sil.android.myappbox.core.util.logger
//import hr.sil.android.myappbox.core.util.macRealToClean
//import java.util.concurrent.TimeUnit
//
///**
// * @author mfatiga
// */
//object DataCache {
//    val log = logger()
//    fun checkClasses(context: Context) {
//        PersistenceClassTracker.checkClass(context, RAvailableLockerSize::class)
//        PersistenceClassTracker.checkClass(context, RAdminGroup::class)
//    }
//
//    private val masterUnitCache by lazy {
//       /* PRIMJER KAK NAPRAVITI ID, KEY, KLJUC, KOJI SE SASTOJI OD DVA PODATKA
//       AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RMasterUnit::class, {MasterUnitCacheKey(id, accessType)} )
//                        .memoryLruMaxSize(100)
//                        .build(App.ref))
//                .enableNetworkChecking(App.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.MINUTES) { _ ->
//                    WSUser.getMasterUnits() ?: listOf()
//                })
//                .build()*/
//
//        AutoCache.Builder(
//                TwoLevelCache
//                        // EXAMPLE HOW TO MAKE ID, KEY FOR DATA CACHE WHICH CONSIST OF TWO DATA
//                        //.Builder(RMasterUnit::class, {MasterUnitCacheKey(id, accessType)} )
//                        .Builder(RMasterUnit::class, RMasterUnit::id )
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.MINUTES) { _ ->
//                    WSUser.getMasterUnits() ?: listOf()
//                })
//                .build()
//    }
//
//
//    private val availableLockerSizesCache by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RAvailableLockerSizesDTO::class, RAvailableLockerSizesDTO::masterUnitId)
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setSingleElementSource(CacheSource.ForKey.Suspendable(1, TimeUnit.MINUTES) { masterUnitId, _ ->
//                    val availableLockerSizes = WSUser.getAvailableLockerSizes(masterUnitId)
//                            ?: listOf()
//                    RAvailableLockerSizesDTO(masterUnitId, availableLockerSizes)
//                })
//                .build()
//    }
//
//    private val lockerKeyCache by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RLockerKey::class, RLockerKey::id)
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.MINUTES) { _ ->
//                    WSUser.getActiveKeys() ?: listOf()
//                })
//                .build()
//    }
//
//    private val languagesCache by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RLanguage::class, RLanguage::id)
//                        .memoryLruMaxSize(10)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.HOURS) { _ ->
//                    WSUser.getLanguages()
//                })
//                .build()
//    }
//
//    private val lockersInfoCache by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RLockerInfo::class, RLockerInfo::mac)
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.HOURS) {
//                    getDevices()
//                })
//                .build()
//    }
//
//    private val groupMembers by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(REndUserGroupMember::class, REndUserGroupMember::id)
//                        .memoryLruMaxSize(40)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.MINUTES) { _ ->
//                    WSUser.getGroupMembers() ?: mutableListOf()
//                })
//                .build()
//    }
//
//
//    private val adminUserGroupIdCache by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RGroupInfo::class, RGroupInfo::id)
//                        .memoryLruMaxSize(50)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.MINUTES) { _ ->
//                    WSUser.getGroupMemberships()?: mutableListOf()
//                })
//                .build()
//    }
//
//    private val groupMemberships by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RAdminGroup::class, RAdminGroup::groupId)
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setSingleElementSource(CacheSource.ForKey.Suspendable(1, TimeUnit.MINUTES) {groupId, _ ->
//
//                    val adminGroup = WSUser.getGroupMembershipsById(groupId) ?: mutableListOf()
//                    adminGroup.sortBy { it.master_name }
//                    log.info("Data output:" + adminGroup)
//                    RAdminGroup(groupId, adminGroup)
//                })
//                .build()
//    }
//
//    private val activeRequestAccessMpl by lazy {
//        AutoCache.Builder(
//                TwoLevelCache
//                        .Builder(RAccessRequest::class, RAccessRequest::id)
//                        .memoryLruMaxSize(100)
//                        .build(App.Companion.ref))
//                .enableNetworkChecking(App.Companion.ref)
//                .setFullSource(CacheSource.ForCache.Suspendable(1, TimeUnit.HOURS) { _ ->
//                    WSUser.getActiveRequests() ?: listOf()
//                })
//                .build()
//    }
//
//    private val pickAtHomeKeys by lazy {
//        AutoCache.Builder(
//            TwoLevelCache
//                .Builder(RCreatedLockerKey::class, RCreatedLockerKey::id)
//                .memoryLruMaxSize(100)
//                .build(App.Companion.ref))
//            .enableNetworkChecking(App.Companion.ref)
//            .setFullSource(CacheSource.ForCache.Suspendable(20, TimeUnit.MINUTES) {
//                WSUser.getActivePaHCreatedKeys() ?: mutableListOf()
//            })
//            .build()
//    }
//
//    suspend  fun getDevices(): List<RLockerInfo>? {
//        log.info("Ble entires ${MPLDeviceStore.uniqueDevices.values.joinToString { it.macAddress }}")
//        val entries = MPLDeviceStore.uniqueDevices.values.map { it.macAddress.macRealToClean() }
//        return if (entries.isNotEmpty()) {
//            log.info("Filtered Ble entires ${entries}")
//            WSUser.getDevicesInfo(entries)
//        } else
//            listOf()
//    }
//
//    fun deleteOwnerGroupElement( id: Int  ) {
//         groupMembers.del(id)
//    }
//
//    fun clearCaches() {
//        masterUnitCache.clear()
//        availableLockerSizesCache.clear()
//        lockerKeyCache.clear()
//        languagesCache.clear()
//        lockersInfoCache.clear()
//        groupMembers.clear()
//        adminUserGroupIdCache.clear()
//        groupMemberships.clear()
//        activeRequestAccessMpl.clear()
//    }
//
//    fun clearLockerInfoCache(){
//        lockersInfoCache.clear()
//    }
//
//    fun clearMasterUnitCache(){
//        masterUnitCache.clear()
//    }
//
//    suspend fun preloadKeysCache() {
//        getActiveKeys(true)
//    }
//
//    suspend fun preloadMasterUnitsCache() {
//        getMasterUnits(true)
//    }
//
//    suspend fun preloadCaches() {
//        getMasterUnits(true)
//        getActiveKeys(true)
//        getLanguages(true)
//        getDevicesInfo(true)
//
//        getActiveRequestAccessMpl(true)
//
//        if( !App.Companion.ref.resources.getBoolean(R.bool.scan_advertise_only_tablet) ) {
//            getGroupMembers(true)
//            getGroupMembershipById(true)
//
//            for (items in getGroupMembershipById()) {
//                groupMemberships(items.groupId.toLong(), true)
//            }
//        }
//
//    }
//
//    suspend fun getMasterUnits(awaitUpdate: Boolean = false): Collection<RMasterUnit> =
//            masterUnitCache.getAll(awaitUpdate)
//
//    suspend fun getAvailableLockerSizes(masterUnitId: Int, awaitUpdate: Boolean = false): Collection<RAvailableLockerSize> =
//            availableLockerSizesCache.get(masterUnitId, awaitUpdate)
//                    ?.availableLockerSizes ?: listOf()
//
//    suspend fun getActiveKeys(awaitUpdate: Boolean = false): Collection<RLockerKey> =
//            lockerKeyCache.getAll(awaitUpdate)
//
//    suspend fun getLanguages(awaitUpdate: Boolean = false): Collection<RLanguage> =
//            languagesCache.getAll(awaitUpdate)
//
//    suspend fun getDevicesInfo(awaitUpdate: Boolean = false): Collection<RLockerInfo> =
//            lockersInfoCache.getAll(awaitUpdate)
//
//    suspend fun getGroupMembers(awaitUpdate: Boolean = false): Collection<REndUserGroupMember> =
//            groupMembers.getAll( awaitUpdate)
//
//    suspend fun getGroupMembershipById(awaitUpdate: Boolean = false): Collection<RGroupInfo> =
//            adminUserGroupIdCache.getAll( awaitUpdate)
//
//    suspend fun groupMemberships(groupId: Long, awaitUpdate: Boolean = false):  Collection<RGroupInfo> =
//            groupMemberships.get(groupId, awaitUpdate)
//                    ?.groupsInfo ?: listOf()
//
//    suspend fun getActiveRequestAccessMpl(awaitUpdate: Boolean = false): Collection<RAccessRequest> =
//            activeRequestAccessMpl.getAll( awaitUpdate)
//
//    suspend fun getPickAtHomeKeys(awaitUpdate: Boolean = false): Collection<RCreatedLockerKey> =
//            pickAtHomeKeys.getAll( awaitUpdate)
//}