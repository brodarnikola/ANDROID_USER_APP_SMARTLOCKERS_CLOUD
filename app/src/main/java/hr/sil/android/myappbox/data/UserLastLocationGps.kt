package hr.sil.android.myappbox.data

import android.location.Location
import java.util.*

class UserLastLocationGps {
    var lastGoodLocation = Location("GPS")
    var lastFetchedGpsLocation = Date()
}