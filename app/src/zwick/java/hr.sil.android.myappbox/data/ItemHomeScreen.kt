package hr.sil.android.myappbox.data


sealed class ItemHomeScreen {
    abstract fun getRecyclerviewItemType(): Int
    abstract fun getRecvyclerviewItemTypeData(): Any

    class Header : ItemHomeScreen() {
        private val ITEM_HEADER_HOME_SCREEN = 0

        override fun getRecyclerviewItemType(): Int {
            return ITEM_HEADER_HOME_SCREEN
        }

        override fun getRecvyclerviewItemTypeData(): String {
            return headerTitle
        }

        var headerTitle: String = ""
        var numberOfItems: Int = 0
        var positionToExpandCollapseInAdapterList: Int = 0
        var isExpandedList: Boolean = true
        var indexOfHeader: Int = 0
    }

    class Child( var mplOrSplDevice: DeviceData  /*var mplOrSplDevice: MPLDevice? = null*/  ) : ItemHomeScreen() {
        val ITEM_CHILD_HOME_SCREEN = 1

        override fun getRecyclerviewItemType(): Int {
            return ITEM_CHILD_HOME_SCREEN
        }

        override fun getRecvyclerviewItemTypeData(): DeviceData {
            return mplOrSplDevice
        }

    }
}

