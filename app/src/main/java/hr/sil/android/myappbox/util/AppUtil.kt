package hr.sil.android.myappbox.util


import hr.sil.android.myappbox.store.DeviceStoreRemoteUpdater
import hr.sil.android.myappbox.store.MPLDeviceStore


object AppUtil {
    suspend fun refreshCache() {
        //DatabaseHandler.deliveryKeyDb.clear()
        //DataCache.clearCaches()
        //DataCache.preloadCaches()
        MPLDeviceStore.clear()
        //force update device store
        DeviceStoreRemoteUpdater.forceUpdate()
    }


}

