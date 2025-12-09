package hr.sil.android.myappbox.activities.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.getColorOrThrow
import androidx.lifecycle.lifecycleScope
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.DataCache
import hr.sil.android.smartlockers.enduser.core.remote.model.RLanguage
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.ActivitySendParcelDeliveryBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivitySettingsUserDetailsBinding
import hr.sil.android.smartlockers.enduser.util.SettingsHelper
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.util.connectivity.NetworkChecker
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.settings.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast

class SettingsUserDetailsActivity : BaseActivity(0, R.id.no_internet_layout) {

    val GROUP_NAME_MAXIMUM_LENGTH: Int = 18
    private val log = logger()

    private var isOnResumeFinished = false

    private var isGroupNameEdittextTouched = false
    private var programaticSetText = false
    private var togetherGroupName = ""

    private val LESS_THAN_TOTAL_ALLOWED_CHARACTERS = 31
    private val MAX_ROW_LENGTH = 15
    private val NO_CHARATER_IN_ROW_IN_EPAPER = 0
    private val ROW_SWITCH_POSITION = MAX_ROW_LENGTH + 1
    private val FIRST_INDEX_IN_LIST_SPLITED_BY_SPACE = 0

    private val STARTING_FROM_FIRST_CHARACTER = 0

    private val SECOND_ROW_LAST_INDEX = MAX_ROW_LENGTH * 2

    private val INCREASE_BY_ONE_BECAUSE_OF_EMPTY_SPACE = 1

    private val ROW_POSITION = 0
    private val INDEX_POSITION = 1
    private var cursorPosition = IntArray(2)

    private var groupNameErrorColor = 0
    private var groupNameCorrectColor = 0

    private lateinit var binding: ActivitySettingsUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAllHintToUpperCase()

        cursorPosition[ROW_POSITION] = 0
        cursorPosition[INDEX_POSITION] = 0

        groupNameErrorColor = getColorAttrValue(R.attr.thmRegisterGroupNameErrorTextColor) ?: 0
        groupNameCorrectColor = getColorAttrValue(R.attr.thmRegisterGroupNameCorrectTextColor) ?: 0

        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        binding.btnUserSettings?.setOnClickListener {

            if (NetworkChecker.isInternetConnectionAvailable()) {
                if (validate()) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnUserSettings.visibility = View.GONE
                    lifecycleScope.launch {
                        val languagesList = DataCache.getLanguages().toList()

                        val user = UserUtil.user

                        val languageName = SettingsHelper.languageName
                        val language: RLanguage? =
                            languagesList.firstOrNull { it.code == languageName }!!

                        var updateResult = false

                        val name = binding.nameEditText.text.toString()
                        val groupName =
                            binding.groupNameFirstRow.text.toString().trim() + " " + binding.groupNameSecondRow.text.toString().trim()
                        val address = binding.addressEditText.text.toString()
                        val phone = binding.phoneEditText.text.toString()

                        if (user != null && language != null) {
                            updateResult = UserUtil.userUpdate(
                                name, address, phone, language,
                                user.isNotifyPush,
                                user.isNotifyEmail, groupName )
                        }
                        withContext(Dispatchers.Main) {

                            if( updateResult ) {

                                App.ref.toast(this@SettingsUserDetailsActivity.getString(R.string.app_generic_success))

                                UserUtil.user?.name = name
                                UserUtil.user?.address = address
                                UserUtil.user?.telephone = phone

                                UserUtil.userGroup?.name = groupName

                                val intent = Intent(
                                    this@SettingsUserDetailsActivity,
                                    SettingsActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                            else {
                                Toast.makeText(
                                    this@SettingsUserDetailsActivity,
                                    R.string.error_while_saving_user,
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.progressBar.visibility = View.GONE
                                binding.btnUserSettings.visibility = View.VISIBLE
                            }
                        }
                    }

                }
            }
            else {
                App.ref.toast(R.string.app_generic_no_network)
            }
        }

        var togetherGroupName = ""

        binding.groupNameFirstRow.run {

            addTextChangedListener(object : TextWatcher {
                var fRInsertedCharLength = 0

                override fun afterTextChanged(p0: Editable?) {
                    if (programaticSetText && binding.groupNameFirstRow.isFocused && binding.groupNameFirstRow.text.isNotEmpty() && cursorPosition[INDEX_POSITION] <= binding.groupNameFirstRow.text.length)
                        binding.groupNameFirstRow.setSelection(cursorPosition[INDEX_POSITION])
                    else if (programaticSetText && binding.groupNameFirstRow.isFocused && binding.groupNameFirstRow.text.isNotEmpty() && cursorPosition[INDEX_POSITION] >= binding.groupNameFirstRow.text.length)
                        binding.groupNameFirstRow.setSelection(binding.groupNameFirstRow.text.length)
                }

                override fun beforeTextChanged(
                    p0: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    fRInsertedCharLength = binding.groupNameFirstRow.text.length
                    log.info("First row.. Size before text changing is $fRInsertedCharLength")
                }

                override fun onTextChanged(
                    firstRowText: CharSequence?,
                    positionIndexChanged: Int,
                    before: Int,
                    count: Int
                ) {

                    if (programaticSetText) return

                    programaticSetText = true
                    var newCharacter = ""
                    if (fRInsertedCharLength > firstRowText.toString().length && firstRowText.toString().isNotEmpty()) {
                        if (positionIndexChanged == 0)
                            newCharacter = firstRowText?.get(0).toString()
                        else
                            newCharacter = firstRowText?.get(positionIndexChanged - 1).toString()
                    } else if (firstRowText.toString().isNotEmpty())
                        newCharacter = firstRowText?.get(positionIndexChanged).toString()
                    log.info("last character is: $newCharacter")

                    if (firstRowText.toString().isEmpty()) {
                        reorderAndMoveAllTextUp()
                    } else if (!handleLastCharacterEmptySpace(
                            binding.groupNameFirstRow,
                            binding.groupNameSecondRow
                        )
                    ) {
                        if (binding.groupNameFirstRow.isFocused) {

                            togetherGroupName = parseGroupName()

                            positionCursorInCurrentEdittext(
                                fRInsertedCharLength,
                                firstRowText.toString(),
                                positionIndexChanged,
                                before,
                                count
                            )

                            displayGroupName(togetherGroupName, newCharacter)

                            if (positionIndexChanged + count - 1 == MAX_ROW_LENGTH) {
                                binding.groupNameSecondRow.isEnabled = true
                                binding.groupNameSecondRow.requestFocus()
                                if (binding.groupNameSecondRow.text.contains(" ")) {
                                    val firstWordInSecondRow = binding.groupNameSecondRow.text.split(" ")
                                    binding.groupNameSecondRow.setSelection(firstWordInSecondRow.first().length)
                                } else {
                                    binding.groupNameSecondRow.setSelection(binding.groupNameSecondRow.text.length)
                                }
                            }
                        }
                    }
                    programaticSetText = false
                }
            })
        }

        binding.groupNameSecondRow.run {

            addTextChangedListener(object : TextWatcher {

                var tRInsertedCharLength = 0

                override fun afterTextChanged(p0: Editable?) {
                    if (programaticSetText && binding.groupNameSecondRow.isFocused && binding.groupNameSecondRow.text.isNotEmpty() && cursorPosition[INDEX_POSITION] <= binding.groupNameSecondRow.text.length) {
                        binding.groupNameSecondRow.setSelection(cursorPosition[INDEX_POSITION])
                    }
                }

                override fun beforeTextChanged(
                    p0: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    tRInsertedCharLength = binding.groupNameSecondRow.text.length
                    log.info("Second row.. Size before text changing is $tRInsertedCharLength")
                }

                override fun onTextChanged(
                    secondRowText: CharSequence?,
                    positionIndexChanged: Int,
                    before: Int,
                    count: Int
                ) {
                    if (programaticSetText) return

                    var newCharacter = ""
                    if (tRInsertedCharLength > secondRowText.toString().length && secondRowText.toString().isNotEmpty()) {
                        if (positionIndexChanged == 0)
                            newCharacter = secondRowText?.get(0).toString()
                        else
                            newCharacter = secondRowText?.get(positionIndexChanged - 1).toString()
                    } else if (secondRowText.toString().isNotEmpty())
                        newCharacter = secondRowText?.get(positionIndexChanged).toString()
                    log.info("last character is: " + newCharacter)

                    programaticSetText = true
                    if (secondRowText.toString().isEmpty()) {
                        jumpFromSecondRowToFirstRow()
                    } else if (binding.groupNameSecondRow.isFocused) {

                        togetherGroupName = parseGroupName()

                        positionCursorInCurrentEdittext(
                            tRInsertedCharLength,
                            secondRowText.toString(),
                            positionIndexChanged,
                            before,
                            count
                        )

                        displayGroupName(togetherGroupName, newCharacter)
                    }
                    programaticSetText = false
                }
            })
        }

    }

    private fun setAllHintToUpperCase() {
        binding.name.hint = resources.getString(R.string.app_generic_name).uppercase()
        binding.email.hint = resources.getString(R.string.edit_user_notification_setting_email).uppercase()
        binding.address.hint = resources.getString(R.string.app_generic_address).uppercase()
        binding.phone.hint = resources.getString(R.string.app_generic_phone).uppercase()
    }

    private fun validate(): Boolean {
        var validated = true
        if (!validateUserName()) {
            validated = false
        }
        if (!validateGroupName()) {
            validated = false
        }
        if (!validateAddress()) {
            validated = false
        }
        return validated
    }

    private fun validateUserName(): Boolean {
        return validateEditText(binding.name, binding.nameEditText) { nameEditText ->
            when {
                nameEditText.isEmpty() -> {
                    binding.tvName.visibility = View.VISIBLE
                    binding.tvName.setText(R.string.edit_user_validation_blank_fields_exist)
                    ValidationResult.INVALID_USERNAME_BLANK
                }
                nameEditText.length < 4 -> {
                    binding.tvName.visibility = View.VISIBLE
                    binding.tvName.setText(R.string.edit_user_validation_username_min_4_characters)
                    ValidationResult.INVALID_USERNAME_MIN_4_CHARACTERS
                }
                else -> {
                    binding.tvName.visibility = View.GONE
                    ValidationResult.VALID
                }
            }
        }
    }

    private fun validateGroupName(): Boolean {
        val groupNameTogetherLength = binding.groupNameFirstRow.text.length + binding.groupNameSecondRow.text.length
        if (binding.groupNameFirstRow.text.isEmpty() && binding.groupNameSecondRow.text.isEmpty()) {
            binding.groupNameWrong.visibility = View.VISIBLE
            binding.groupNameWrong.text = getString(R.string.edit_user_validation_blank_fields_exist)
            binding.groupNameTitle.setTextColor(groupNameErrorColor)
            return false
        } else if (groupNameTogetherLength < 4) {
            binding.groupNameWrong.visibility = View.VISIBLE
            binding.groupNameWrong.text =
                getString(R.string.edit_user_validation_group_name_min_4_characters)
            binding.groupNameTitle.setTextColor(groupNameErrorColor)
            return false
        } else {
            binding.groupNameWrong.visibility = View.GONE
            binding.groupNameTitle.setTextColor(groupNameCorrectColor)
            return true
        }
    }

    private fun validateAddress(): Boolean {
        return validateEditText(binding.address, binding.addressEditText) { addressEditText ->
            when {
                addressEditText.isEmpty() -> {
                    binding.tvAddress.visibility = View.VISIBLE
                    binding.tvAddress.setText(R.string.edit_user_validation_blank_fields_exist)
                    ValidationResult.INVALID_PASSWORD_BLANK
                }
                else -> {
                    binding.tvAddress.visibility = View.GONE
                    ValidationResult.VALID
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        binding.nameEditText.setText(UserUtil.user?.name ?: "")

        displayGroupNameOnResume()
        isOnResumeFinished = true
        binding.emailEditText.setText(UserUtil.user?.email ?: "")
        binding.addressEditText.setText(UserUtil.user?.address ?: "")
        binding.phoneEditText.setText(UserUtil.user?.telephone ?: "")
    }

    fun displayGroupName(groupName: String, newCharacter: String) {
        if (groupName.contains(" ")) {
            validateNameWithSpaces(groupName, newCharacter)
        } else {
            validateNameWithoutSpaces(groupName)
        }
    }

    private fun displayGroupNameOnResume() {
        if (UserUtil.userGroup?.name.toString().contains(" "))
            validateNameWithSpaces(UserUtil.userGroup?.name.toString(), "")
        else
            validateNameWithoutSpaces(UserUtil.userGroup?.name)
    }

    private fun validateNameWithoutSpaces(nameText: String?) {
        if (nameText != null) {
            if (nameText.length < ROW_SWITCH_POSITION) {
                if (nameText != binding.groupNameFirstRow.text.toString())
                    binding.groupNameFirstRow.setText(
                        nameText.substring(
                            STARTING_FROM_FIRST_CHARACTER,
                            nameText.length
                        )
                    )
                binding.groupNameSecondRow.setText("")
                binding.firstRowCharacterLength.text = "" + nameText.substring(
                    STARTING_FROM_FIRST_CHARACTER,
                    nameText.length
                ).length + "/" + MAX_ROW_LENGTH
                binding.secondRowCharacterLength.text =
                    "" + STARTING_FROM_FIRST_CHARACTER + "/" + MAX_ROW_LENGTH
                // disable second  row in epaper
                binding.groupNameSecondRow.isEnabled = false
            } else if (nameText.length > MAX_ROW_LENGTH && nameText.length < LESS_THAN_TOTAL_ALLOWED_CHARACTERS) {
                binding.groupNameFirstRow.setText(
                    nameText.substring(
                        STARTING_FROM_FIRST_CHARACTER,
                        MAX_ROW_LENGTH
                    )
                )
                if (nameText != binding.groupNameSecondRow.text.toString())
                    binding.groupNameSecondRow.setText(nameText.substring(MAX_ROW_LENGTH, nameText.length))
                binding.firstRowCharacterLength.text = "" + nameText.substring(
                    STARTING_FROM_FIRST_CHARACTER,
                    MAX_ROW_LENGTH
                ).length + "/" + MAX_ROW_LENGTH
                binding.secondRowCharacterLength.text = "" + nameText.substring(
                    MAX_ROW_LENGTH,
                    nameText.length
                ).length + "/" + MAX_ROW_LENGTH
                // disable second row in epaper
                binding.groupNameSecondRow.isEnabled = true
            } else if (nameText.length > SECOND_ROW_LAST_INDEX) {
                if (nameText != binding.groupNameFirstRow.text.toString())
                    binding.groupNameFirstRow.setText(
                        nameText.substring(
                            STARTING_FROM_FIRST_CHARACTER,
                            MAX_ROW_LENGTH
                        )
                    )
                if (nameText != binding.groupNameSecondRow.text.toString())
                    binding.groupNameSecondRow.setText(
                        nameText.substring(
                            MAX_ROW_LENGTH,
                            SECOND_ROW_LAST_INDEX
                        )
                    )
                binding.firstRowCharacterLength.text = "" + nameText.substring(
                    STARTING_FROM_FIRST_CHARACTER,
                    MAX_ROW_LENGTH
                ).length + "/" + MAX_ROW_LENGTH
                binding.secondRowCharacterLength.text = "" + nameText.substring(
                    MAX_ROW_LENGTH,
                    SECOND_ROW_LAST_INDEX
                ).length + "/" + MAX_ROW_LENGTH

                // disable second row in epaper
                binding.groupNameSecondRow.isEnabled = true
            }
        }
    }

    private fun validateNameWithSpaces(groupName: String?, newCharacter: String) {

        var firstRowCounterLength = 0
        var secondRowCounterLength = 0

        var firstRowText = ""
        var secondRowText = ""

        if (groupName != null) {
            val emptySpaces = groupName.split(" ")
            //val lastCharacter = nameText.substring(nameText.length - 1, nameText.length)
            //for (index in 0..emptySpaces.size - 1) {
            for (index in 0 until emptySpaces.size) {

                if (emptySpaces[index].length > MAX_ROW_LENGTH) {

                    if (index == FIRST_INDEX_IN_LIST_SPLITED_BY_SPACE) {
                        firstRowText += emptySpaces[index].substring(
                            STARTING_FROM_FIRST_CHARACTER,
                            MAX_ROW_LENGTH
                        )
                        firstRowCounterLength += emptySpaces[index].substring(
                            STARTING_FROM_FIRST_CHARACTER,
                            MAX_ROW_LENGTH
                        ).length

                        if (emptySpaces[index].length > SECOND_ROW_LAST_INDEX) {

                            secondRowText += emptySpaces[index].substring(
                                MAX_ROW_LENGTH,
                                SECOND_ROW_LAST_INDEX
                            )
                            secondRowCounterLength += emptySpaces[index].substring(
                                MAX_ROW_LENGTH,
                                SECOND_ROW_LAST_INDEX
                            ).length

                        } else {
                            secondRowText += emptySpaces[index].substring(
                                MAX_ROW_LENGTH,
                                emptySpaces[index].length
                            ) + " "
                            secondRowCounterLength += emptySpaces[index].substring(
                                MAX_ROW_LENGTH,
                                emptySpaces[index].length
                            ).length
                        }
                    } else if (secondRowCounterLength <= ROW_SWITCH_POSITION) {

                        secondRowText += emptySpaces[index].substring(
                            STARTING_FROM_FIRST_CHARACTER,
                            MAX_ROW_LENGTH
                        )
                        secondRowCounterLength += secondRowText.length + INCREASE_BY_ONE_BECAUSE_OF_EMPTY_SPACE
                    }
                } else if (firstRowCounterLength < ROW_SWITCH_POSITION && (firstRowCounterLength + emptySpaces[index].length) < ROW_SWITCH_POSITION && secondRowCounterLength <= NO_CHARATER_IN_ROW_IN_EPAPER) {
                    if (emptySpaces.size - 1 == index) {
                        firstRowCounterLength += emptySpaces[index].length
                        firstRowText += emptySpaces[index]
                    } else {
                        firstRowText += emptySpaces[index] + " "
                        firstRowCounterLength = firstRowText.length
                    }
                } else if (secondRowCounterLength < ROW_SWITCH_POSITION) {

                    if (emptySpaces.size - 1 == index) {
                        secondRowCounterLength += emptySpaces[index].length
                        secondRowText += emptySpaces[index]
                    } else {
                        secondRowText += emptySpaces[index] + " "
                        secondRowCounterLength = secondRowText.length
                    }
                }
            }
        }

        when {
            binding.groupNameFirstRow.isFocused || !isOnResumeFinished -> {
                firstRowText = currentEdittextRemoveLastCharacterEmptySpace(
                    firstRowText,
                    firstRowCounterLength,
                    newCharacter
                )
                secondRowText = notSelectedEdittextRemoveLastCharacterEmptySpace(
                    secondRowText,
                    secondRowCounterLength
                )
            }
            binding.groupNameSecondRow.isFocused || !isOnResumeFinished -> {
                firstRowText = notSelectedEdittextRemoveLastCharacterEmptySpace(
                    firstRowText,
                    firstRowCounterLength
                )
                secondRowText = currentEdittextRemoveLastCharacterEmptySpace(
                    secondRowText,
                    secondRowCounterLength,
                    newCharacter
                )
            }
        }

        if (firstRowText.length >= MAX_ROW_LENGTH) {
            firstRowText = firstRowText.substring(STARTING_FROM_FIRST_CHARACTER, MAX_ROW_LENGTH)
        }
        if (secondRowText.length >= MAX_ROW_LENGTH) {
            secondRowText = secondRowText.substring(STARTING_FROM_FIRST_CHARACTER, MAX_ROW_LENGTH)
        }

        if (firstRowText != binding.groupNameFirstRow.text.toString()) {
            binding.groupNameFirstRow.setText(firstRowText)
        }
        if (secondRowText != binding.groupNameSecondRow.text.toString()) {
            binding.groupNameSecondRow.setText(secondRowText)
        }


        when {
            binding.groupNameSecondRow.isFocused && secondRowText.isEmpty() -> {
                val isGrouNameThirdFocused = binding.groupNameSecondRow.isFocused
                log.info("da li ce ikad uciiii aaaaa : ${isGrouNameThirdFocused}")
                binding.groupNameFirstRow.requestFocus()
                binding.groupNameFirstRow.setSelection(binding.groupNameFirstRow.text.length)
                binding.groupNameSecondRow.isEnabled = false
            }
        }

        binding.firstRowCharacterLength.text = "" + binding.groupNameFirstRow.text.length + "/" + MAX_ROW_LENGTH
        binding.secondRowCharacterLength.text = "" + binding.groupNameSecondRow.text.length + "/" + MAX_ROW_LENGTH

        // enable or disable second and third row in epaper
        binding.groupNameSecondRow.isEnabled = secondRowText.isNotEmpty()
    }

    private fun currentEdittextRemoveLastCharacterEmptySpace(
        currentText: String,
        currentRowLength: Int,
        newCharacter: String
    ): String {

        if (currentRowLength > NO_CHARATER_IN_ROW_IN_EPAPER && newCharacter != " ") {
            val lastCharacterInRow = currentText.substring(currentText.length - 1)
            if (lastCharacterInRow == " ") {
                val correctRowString = currentText.trim()
                return correctRowString
            } else {
                return currentText
            }
        }
        return currentText
    }

    private fun notSelectedEdittextRemoveLastCharacterEmptySpace(
        notSelectedText: String,
        notSelectedRowLength: Int
    ): String {
        if (notSelectedRowLength > NO_CHARATER_IN_ROW_IN_EPAPER) {
            val lastCharacterInRow = notSelectedText.substring(notSelectedText.length - 1)
            if (lastCharacterInRow == " ") {
                val correctRowString = notSelectedText.trim()
                return correctRowString
            } else
                return notSelectedText
        }
        return notSelectedText
    }

    private fun parseGroupName(): String {

        var groupName = ""

        if (binding.groupNameSecondRow.text.isEmpty()) groupName = binding.groupNameFirstRow.text.toString()
        else if (binding.groupNameSecondRow.text.isNotEmpty()) {
            if (binding.groupNameFirstRow.text.last().toString() == " ")
                groupName = binding.groupNameFirstRow.text.toString() + binding.groupNameSecondRow.text.toString()
            else
                groupName =
                    binding.groupNameFirstRow.text.toString() + " " + binding.groupNameSecondRow.text.toString()
        }
        return groupName
    }

    private fun handleLastCharacterEmptySpace(
        currentEditText: EditText,
        nextEditText: EditText?
    ): Boolean {

        if (currentEditText.selectionEnd == ROW_SWITCH_POSITION && currentEditText.text.length == ROW_SWITCH_POSITION
            && currentEditText.text.last().toString() == " "
        ) {
            nextEditText?.isEnabled = true
            nextEditText?.requestFocus()

            if (cursorPosition[ROW_POSITION] < 3) cursorPosition[ROW_POSITION] = +1
            cursorPosition[INDEX_POSITION] = 0
            currentEditText.setText(currentEditText.text.toString().dropLast(1))
            return true
        }
        return false
    }

    private fun positionCursorInCurrentEdittext(
        currentTextLength: Int,
        currentText: String,
        positionIndexChanged: Int,
        before: Int,
        count: Int
    ) {
        when {
            currentTextLength > currentText.length -> cursorPosition[INDEX_POSITION] =
                positionIndexChanged + count
            else -> cursorPosition[INDEX_POSITION] = positionIndexChanged + before + 1
        }
    }

    private fun jumpFromSecondRowToFirstRow() {

        binding.groupNameFirstRow.requestFocus()
        binding.groupNameFirstRow.setSelection(binding.groupNameFirstRow.text.length)
        binding.groupNameSecondRow.isEnabled = false
        binding.groupNameSecondRow.setText("")
        binding.secondRowCharacterLength.text = "0/" + MAX_ROW_LENGTH
    }

    private fun reorderAndMoveAllTextUp() {
        if (binding.groupNameSecondRow.text.isNotEmpty()) {
            binding.groupNameFirstRow.setText(binding.groupNameSecondRow.text.toString())
            binding.groupNameSecondRow.setText("")
            binding.groupNameSecondRow.isEnabled = false
            binding.firstRowCharacterLength.text = "" + binding.groupNameFirstRow.text.length + "/" + MAX_ROW_LENGTH
            binding.secondRowCharacterLength.text =
                "" + binding.groupNameSecondRow.text.length + "/" + MAX_ROW_LENGTH
        } else {
            binding.firstRowCharacterLength.text = "" + binding.groupNameFirstRow.text.length + "/" + MAX_ROW_LENGTH
        }
    }

    private fun getColorAttrValue(attr: Int): Int? {
        val attrArray = intArrayOf(attr)
        val typedArray = this@SettingsUserDetailsActivity.obtainStyledAttributes(attrArray)
        val result = try {
            typedArray.getColorOrThrow(0)
        } catch (exc: Exception) {
            null
        }
        typedArray.recycle()
        return result
    }

    override fun onNetworkStateUpdated(available: Boolean) {
        super.onNetworkStateUpdated(available)
        networkAvailable = available
        updateUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {

                val intent = Intent(this@SettingsUserDetailsActivity, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@SettingsUserDetailsActivity, SettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        super.onBackPressed()
    }

}
