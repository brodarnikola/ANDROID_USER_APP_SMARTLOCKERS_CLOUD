//package hr.sil.android.myappbox.view.ui.adapters
//
//import android.annotation.SuppressLint
//import android.graphics.Color
//import android.graphics.drawable.Drawable
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.inputmethod.EditorInfo
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.content.res.getColorOrThrow
//import hr.sil.android.myappbox.App
//import hr.sil.android.myappbox.R
//import hr.sil.android.myappbox.core.remote.WSUser
//import hr.sil.android.myappbox.core.remote.model.RPinManagement
//import hr.sil.android.myappbox.core.remote.model.RPinManagementSavePin
//import hr.sil.android.myappbox.core.util.logger
//import hr.sil.android.myappbox.util.backend.UserUtil
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class PinManagementAdapter (var keys: MutableList<RPinManagement>,
//                            var masterUnitId: Int?,
//                            val activity: AppCompatActivity) : RecyclerView.Adapter<PinManagementAdapter.PinViewHolder>() {
//
//    var previousSelectedRow: Int = -1
//
//    override fun onBindViewHolder(holder: PinViewHolder, position: Int) {
//        holder.bindItem(position, keys[position])
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
//
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.list_pin_managment, parent, false)
//        return PinViewHolder(view)
//    }
//
//    override fun getItemCount() = keys.size
//
//    inner class PinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val log = logger()
//        val number: TextView = itemView.findViewById(R.id.tvPinManagmentNumber)
//        val name: TextView = itemView.findViewById(R.id.tvPinManagmentName)
//        val openToDelete: ImageButton = itemView.findViewById(R.id.ivDeletePin)
//        val insertNewPinLayout: RelativeLayout = itemView.findViewById(R.id.rlPinManagmentNewLayout)
//        val etPinNewName: EditText = itemView.findViewById(R.id.etPinNewName)
//        val savePinButton: ImageButton = itemView.findViewById(R.id.ivSavePin)
//        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.clMainLayout)
//        val deleteLayoutConfirm: RelativeLayout = itemView.findViewById(R.id.rlDeleteLayout)
//        val buttonDelete: Button = itemView.findViewById(R.id.btnDelete)
//        val buttonCancelDeletePin: Button = itemView.findViewById(R.id.btnCancel)
//
//        private val openImage = getDrawableAttrValue(R.attr.thmPinManagmentOpenImage)
//
//        private val selectedEvenBackground = getDrawableAttrValue(R.attr.thmPinManagmentSelectedEvenBackgroundColor)
//        private val selectedOddBackground = getDrawableAttrValue(R.attr.thmPinManagmentSelectedOddBackgroundColor)
//
//        private val unselectedEvenBackground = getColorAttrValue(R.attr.thmPinManagmentUnselectedEvenBackgroundColor) ?: Color.WHITE
//        private val unselectedOddBackground = getColorAttrValue(R.attr.thmPinManagmentUnselectedOddBackgroundColor) ?: Color.WHITE
//        //?attr/
//        private fun getDrawableAttrValue(attr: Int): Drawable? {
//            val attrArray = intArrayOf(attr)
//            val typedArray = activity.obtainStyledAttributes(attrArray)
//            val result = try { typedArray.getDrawable(0) } catch (exc: Exception) { null }
//            typedArray.recycle()
//            return result
//        }
//
//        private fun getColorAttrValue(attr: Int): Int? {
//            val attrArray = intArrayOf(attr)
//            val typedArray = activity.obtainStyledAttributes(attrArray)
//            val result =  try { typedArray.getColorOrThrow(0) } catch (exc: Exception) { null }
//            typedArray.recycle()
//            return result
//        }
//
//        @SuppressLint("ClickableViewAccessibility")
//        fun bindItem(position: Int, pinObject: RPinManagement ) {
//
//            number.text = pinObject.pin
//
//            if (pinObject.isSelected && pinObject.position % 2 == 0)
//                mainLayout.background = selectedEvenBackground
//            else if (pinObject.isSelected && pinObject.position % 2 != 0)
//                mainLayout.background = selectedOddBackground
//            else if (pinObject.isSelected == false && pinObject.position % 2 == 0)
//                mainLayout.setBackgroundColor( unselectedEvenBackground )
//            else
//                mainLayout.setBackgroundColor( unselectedOddBackground )
//
//            if (pinObject.pinGenerated) {
//
//                name.text = itemView.context.getString(R.string.pin_managment_generate)
//                openToDelete.setOnClickListener {
//                    showAdddPinButton(pinObject, position)
//                }
//                openToDelete.setImageDrawable(openImage)
//                savePinButton.setOnClickListener {
//                    savePinFromGroup(pinObject, it, position)
//                }
//
//                etPinNewName.setOnTouchListener { v, event ->
//                    var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//                    if( findSelectedRow != null && pinObject.isSelected != findSelectedRow.isSelected ) {
//
//                        findSelectedRow.isExtendedToDelete = false
//                        findSelectedRow.isSelected = false
//                        notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//
//                        pinObject.isSelected = true
//                        notifyItemChanged(keys.indexOf(pinObject), pinObject);
//                    }
//
//                    previousSelectedRow = position
//                    lastSelectedPin = pinObject
//                    //App.ref.pinManagementSelectedItem = pinObject
//                    false
//                }
//
//                etPinNewName.addTextChangedListener(object : TextWatcher {
//                    override fun afterTextChanged(p0: Editable?) {
//                    }
//
//                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    }
//
//                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                        //App.ref.pinManagementName = p0.toString()
//                        pinManagmentName = p0.toString()
//                        var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//                        if( findSelectedRow != null && pinObject.isSelected != findSelectedRow.isSelected ) {
//
//                            findSelectedRow.isExtendedToDelete = false
//                            findSelectedRow.isSelected = false
//                            notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//
//                            pinObject.isSelected = true
//                            notifyItemChanged(keys.indexOf(pinObject), pinObject);
//                        }
//
//                        previousSelectedRow = position
//                        lastSelectedPin = pinObject
//                        //App.ref.pinManagementSelectedItem = pinObject
//                    }
//                })
//            }
//            else {
//                name.text = pinObject.pinName
//                openToDelete.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_cancel_access))
//
//                if( pinObject.isExtendedToDelete )
//                    deleteLayoutConfirm.visibility = View.VISIBLE
//                else
//                    deleteLayoutConfirm.visibility = View.GONE
//
//                openToDelete.setOnClickListener {
//                    openToDeletePin(pinObject, position)
//                }
//
//                buttonCancelDeletePin.setOnClickListener {
//                    deleteLayoutConfirm.visibility = View.GONE
//                    pinObject.isExtendedToDelete = false
//                    notifyItemChanged(keys.indexOf(pinObject), pinObject);
//                }
//
//                buttonDelete.setOnClickListener {
//                    GlobalScope.launch {
//
//                        val pinSaved = WSUser.deletePinForSendParcel(pinObject.pinId)
//
//                        withContext(Dispatchers.Main) {
//
//                            if (pinSaved) {
//                                log.info("Succesfully deleted pin")
//
//                                keys.remove(pinObject)
//                                val isPinSelected = keys.filter { it.isSelected == true }.firstOrNull()
//
//                                for (items in keys) {
//
//                                    if (isPinSelected == null && (items.position - 1) == pinObject.position)
//                                        items.isSelected = true
//
//                                    if (items.position > pinObject.position)
//                                        items.position = items.position - 1
//                                }
//                                //previousSelectedRow = adapterPosition - 1
//
//                                notifyDataSetChanged()
//                            } else {
//                            }
//                        }
//                    }
//                }
//            }
//
//            mainLayout.setOnClickListener { view ->
//
//                if (previousSelectedRow == -1 && position != 0) {
//
//                    var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//                    if (findSelectedRow != null) {
//                        findSelectedRow.isExtendedToDelete = false
//                        findSelectedRow.isSelected = false
//                        notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//                    }
//
//                    pinObject.isSelected = true
//
//                    previousSelectedRow = position
//                    notifyItemChanged(keys.indexOf(pinObject), pinObject);
//                    //notifyDataSetChanged()
//                } else {
//
//                    if (previousSelectedRow != position && previousSelectedRow != -1) {
//
//                        var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//                        if (findSelectedRow != null) {
//                            findSelectedRow.isExtendedToDelete = false
//                            findSelectedRow.isSelected = false
//                            notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//                        }
//
//                        pinObject.isSelected = true
//                        notifyItemChanged(keys.indexOf(pinObject), pinObject);
//
//                        previousSelectedRow = position
//                        //notifyDataSetChanged()
//                    }
//                }
//                lastSelectedPin = pinObject
//                //App.ref.pinManagementSelectedItem = pinObject
//            }
//        }
//
//        private fun savePinFromGroup(pinObject: RPinManagement, itemView: View, position: Int) {
//
//            if (etPinNewName.text.toString() != "") {
//                GlobalScope.launch {
//
//                    val savePin = RPinManagementSavePin()
//                    savePin.groupId = UserUtil.userGroup?.id
//                    savePin.masterId = masterUnitId
//                    savePin.pin = pinObject.pin
//                    savePin.name = etPinNewName.text.toString()
//
//                    val pinSaved = WSUser.savePinManagementForSendParcel(savePin)
//
//                    if( pinSaved != null ) {
//
//                        val changePin = keys.filter { it.pinGenerated == true }.firstOrNull()
//                        if( changePin != null ) {
//                            changePin.pinGenerated = false
//                            changePin.pinId = pinSaved.id
//                            changePin.pinName = pinSaved.name
//                        }
//                    }
//
//                    withContext(Dispatchers.Main) {
//
//                        if (pinSaved != null) {
//
//                            lastSelectedPin = pinObject
//                            //App.ref.pinManagementSelectedItem = pinObject
//
//                            log.info("Pin successfully saved")
//                            insertNewPinLayout.visibility = View.GONE
//                            name.visibility = View.VISIBLE
//                            name.text = pinObject.pinName
//                            openToDelete.visibility = View.VISIBLE
//                            openToDelete.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_cancel_access))
//                            openToDelete.setOnClickListener { openToDeletePin(pinObject, position) }
//                            notifyDataSetChanged()
//                        } else {
//
//                            log.info("Pin is not successfully saved")
//                        }
//                    }
//                }
//            } else {
//                App.Companion.ref.toast(itemView.context.getString(R.string.pin_managment_name_not_empty))
//            }
//        }
//
//        private fun showAdddPinButton(pinObject: RPinManagement, position: Int) {
//
//            insertNewPinLayout.visibility = View.VISIBLE
//            val params = etPinNewName.getLayoutParams()
//            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT
//            etPinNewName.setLayoutParams(params)
//
//            etPinNewName.isFocusable = true
//            etPinNewName.requestFocusFromTouch()
//            etPinNewName.requestFocus()
//            etPinNewName.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//            openToDelete.visibility = View.GONE
//            name.visibility = View.GONE
//
//            var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//            if( findSelectedRow != null && pinObject.isSelected != findSelectedRow.isSelected ) {
//
//                findSelectedRow.isExtendedToDelete = false
//                findSelectedRow.isSelected = false
//                notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//            }
//            pinObject.isSelected = true
//            notifyItemChanged(keys.indexOf(pinObject), pinObject);
//
//            previousSelectedRow = position
//            lastSelectedPin = pinObject
//            //App.ref.pinManagementSelectedItem = pinObject
//        }
//
//        private fun openToDeletePin(pinObject: RPinManagement, position: Int) {
//
//            deleteLayoutConfirm.visibility = View.VISIBLE
//
//            var findSelectedRow = keys.filter { it.isSelected == true }.firstOrNull()
//
//            if( findSelectedRow != null && pinObject.isSelected != findSelectedRow.isSelected ) {
//
//                findSelectedRow.isExtendedToDelete = false
//                findSelectedRow.isSelected = false
//                notifyItemChanged(keys.indexOf(findSelectedRow), findSelectedRow);
//
//                pinObject.isExtendedToDelete = true
//                pinObject.isSelected = true
//                notifyItemChanged(keys.indexOf(pinObject), pinObject);
//            }
//
//            previousSelectedRow = position
//            lastSelectedPin = pinObject
//            //App.ref.pinManagementSelectedItem = pinObject
//        }
//    }
//
//    companion object {
//
//        private var lastSelectedPin: RPinManagement = RPinManagement()
//        private var pinManagmentName: String = ""
//
//        fun getLastSelectedPin(): RPinManagement {
//            return this.lastSelectedPin
//        }
//
//        fun setLastSelectedPin(lastPin: RPinManagement) {
//            lastSelectedPin = lastPin
//        }
//
//        fun getPinName(): String {
//            return this.pinManagmentName
//        }
//
//        fun setPinName(pinName: String) {
//            pinManagmentName = pinName
//        }
//    }
//
//}