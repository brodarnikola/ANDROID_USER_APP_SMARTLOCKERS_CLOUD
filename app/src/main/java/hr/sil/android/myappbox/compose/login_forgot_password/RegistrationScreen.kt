package hr.sil.android.myappbox.compose.login_forgot_password

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.RotatingRingIndicator
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmEdittextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextHintColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.components.groupNameBackground
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.compose.theme.DarkModeTransparent
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.utils.UiEvent
import hr.sil.android.myappbox.view.ui.activities.MainActivity
import androidx.compose.material3.MaterialTheme as Material3

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel,
    nextScreen: (route: String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    val imageName = painterResource(id = R.drawable.ic_register_name) // ?attr/thmRegisterName
    val imagePhone = painterResource(id = R.drawable.ic_register_phone) // ?attr/thmRegisterPhone
    val imageAddress =
        painterResource(id = R.drawable.ic_register_address) // ?attr/thmRegisterAddress
    val imageEmail = painterResource(id = R.drawable.ic_register_email) // ?attr/thmRegisterEmail
    val imagePassword =
        painterResource(id = R.drawable.ic_password_zwick) // ?attr/thmRegisterPassword

    var name by remember { mutableStateOf("") }
    var groupNameFirstRow by remember { mutableStateOf("") }
    var groupNameSecondRow by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var showPasswords by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(true) }

    var errorMessageName by remember { mutableStateOf<String?>(null) }
    var errorMessageGroupName by remember { mutableStateOf<String?>(null) }
    var errorMessagePhone by remember { mutableStateOf<String?>(null) }
    var errorMessageAddress by remember { mutableStateOf<String?>(null) }
    var errorMessageEmail by remember { mutableStateOf<String?>(null) }
    var errorMessagePassword by remember { mutableStateOf<String?>(null) }
    var errorMessageRepeatPassword by remember { mutableStateOf<String?>(null) }

    val nameLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }
    val phoneLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }
    val addressLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }
    val emailLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }
    val passwordLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }
    val repeatPasswordLabelStyle = remember { mutableStateOf(AppTypography.labelLarge) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = Unit) {
        val log = logger()
        log.info("collecting events: start ${viewModel.uiEvents}")
        viewModel.uiEvents.collect { event ->
            log.info("collecting event: ${event}")
            when (event) {
                is RegistrationScreenUiEvent.NavigateBack -> {
                    navigateUp()
                }

                is RegistrationScreenUiEvent.NavigateToMainActivityScreen -> {
                    val startIntent = Intent(context, MainActivity::class.java)
                    context.startActivity(startIntent)
                    activity.finish()
                }

                is UiEvent.ShowToast -> {
                    isButtonEnabled = true
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
            }
        }
    }

    GradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Main container is ConstraintLayout to replicate XML's behavior
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (appBar, scrollContent) = createRefs()

                // 1. AppBarLayout (appBarLayout)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(appBar) {
                            top.linkTo(parent.top)
                        }
                ) {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max)
                                    .padding(end = 30.dp)
                            ) {
                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.logo_header_zwick), // ?attr/thmToolbarHeader
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.align(Alignment.Center) // layout_centerInParent="true"
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigateUp() }) {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = ""
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            //containerColor = ThmLoginBackground,
                            //containerColor = colorResource(R.color.colorPrimary),
                            //titleContentColor = colorResource(R.color.colorWhite),
                            navigationIconContentColor = colorResource(R.color.colorBlack)
                        )
                    )
                }

                // 2. ScrollView with content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(scrollContent) {
                            top.linkTo(appBar.bottom)
                        }
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        val (tvRegister, clName, groupNameTitle, registerLayoutGroupName,
                            clPhone, clAddress, clEmail, clPassword, rlRepeatPassword, progressBar, btnRegister) = createRefs()

                        // Title Text (tvRegister)
                        TextViewWithFont(
                            text = stringResource(id = R.string.intro_register_your_account).uppercase(),
                            color = ThmTitleTextColor,
                            fontSize = ThmTitleTextSize,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            letterSpacing = ThmTitleLetterSpacing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(tvRegister) {
                                    top.linkTo(parent.top)
                                }
                        )

                        // Name Input (clName)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(clName) {
                                    top.linkTo(tvRegister.bottom)
                                }
                        ) {
                            TextField(
                                value = name,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.registration_name),
                                        color = ThmEdittextHintColor,
                                        style = nameLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    name = it
                                    //val checkError = viewModel.getNameError(it, context)
                                    //errorMessageName = if (checkError !== "") checkError else null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "nameTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        nameLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imageName,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 3.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessageName != null
                            )
                            if (errorMessageName != null) {
                                Text(
                                    text = errorMessageName ?: "",
                                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                )
                            }
                        }

                        // Group Name Title
                        TextViewWithFont(
                            text = stringResource(id = R.string.registration_group_name).uppercase(),
                            color = ThmLoginDescriptionTextColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp)
                                .constrainAs(groupNameTitle) {
                                    top.linkTo(clName.bottom)
                                }
                        )

                        // Group Name Layout (register_layout_group_name)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .groupNameBackground()
                                .constrainAs(registerLayoutGroupName) {
                                    top.linkTo(groupNameTitle.bottom)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp)
                            ) {
                                // First Row
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    TextField(
                                        value = groupNameFirstRow,
                                        onValueChange = {
                                            if (it.length <= 15) {
                                                groupNameFirstRow = it
                                            }
                                        },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = ThmEdittextColor,
                                            focusedBorderColor = colorResource(R.color.colorPrimary),
                                            unfocusedBorderColor = ThmEdittextHintColor,
                                            cursorColor = colorResource(R.color.colorPrimary),
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                    )

                                    Text(
                                        text = "${groupNameFirstRow.length}/15",
                                        fontSize = 13.sp,
                                        color = ThmLoginDescriptionTextColor,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 2.dp, end = 10.dp)
                                    )
                                }

                                // Second Row
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ) {
                                    TextField(
                                        value = groupNameSecondRow,
                                        onValueChange = {
                                            if (it.length <= 15) {
                                                groupNameSecondRow = it
                                            }
                                        },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = ThmEdittextColor,
                                            focusedBorderColor = colorResource(R.color.colorPrimary),
                                            unfocusedBorderColor = ThmEdittextHintColor,
                                            cursorColor = colorResource(R.color.colorPrimary),
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                    )

                                    Text(
                                        text = "${groupNameSecondRow.length}/15",
                                        fontSize = 13.sp,
                                        color = ThmLoginDescriptionTextColor,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 2.dp, end = 10.dp)
                                    )
                                }
                            }

                            // Group Name Error (groupNameWrong)
                            if (errorMessageGroupName != null) {
                                Text(
                                    text = errorMessageGroupName ?: "",
                                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                )
                            }
                        }

                        // Phone Input (clPhone)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(clPhone) {
                                    top.linkTo(registerLayoutGroupName.bottom)
                                }
                        ) {
                            TextField(
                                value = phone,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.app_generic_phone),
                                        color = ThmEdittextHintColor,
                                        style = phoneLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    phone = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "phoneTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        phoneLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imagePhone,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessagePhone != null
                            )
                        }

                        // Address Input (clAddress)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(clAddress) {
                                    top.linkTo(clPhone.bottom)
                                }
                        ) {
                            TextField(
                                value = address,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.registration_address),
                                        color = ThmEdittextHintColor,
                                        style = addressLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    address = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "addressTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        addressLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imageAddress,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessageAddress != null
                            )

                            // Group Name Error (groupNameWrong)
                            if (errorMessageAddress != null) {
                                Text(
                                    text = errorMessageAddress ?: "",
                                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                )
                            }
                        }

                        // Email Input (clEmail)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(clEmail) {
                                    top.linkTo(clAddress.bottom)
                                }
                        ) {
                            TextField(
                                value = email,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.registration_email),
                                        color = ThmEdittextHintColor,
                                        style = emailLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    email = it
                                    //val checkError = viewModel.getEmailError(it, context)
                                    //errorMessageEmail = if (checkError !== "") checkError else null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "emailTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        emailLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imageEmail,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessageEmail != null
                            )
                            if (errorMessageEmail != null) {
                                Text(
                                    text = errorMessageEmail ?: "",
                                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                )
                            }
                        }

                        // Password Input (clPassword)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(clPassword) {
                                    top.linkTo(clEmail.bottom)
                                }
                        ) {
                            TextField(
                                value = password,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.registration_password),
                                        color = ThmEdittextHintColor,
                                        style = passwordLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    password = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "passwordTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        passwordLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imagePassword,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessagePassword != null
                            )
                        }

                        // Repeat Password Layout (rlRepeatPassword)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                .constrainAs(rlRepeatPassword) {
                                    top.linkTo(clPassword.bottom)
                                }
                        ) {
                            TextField(
                                value = repeatPassword,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.registration_repeat_password),
                                        color = ThmEdittextHintColor,
                                        style = repeatPasswordLabelStyle.value
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = ThmEdittextColor,
                                    focusedBorderColor = colorResource(R.color.colorPrimary),
                                    unfocusedBorderColor = Material3.colorScheme.outline,
                                    cursorColor = colorResource(R.color.colorPrimary),
                                    backgroundColor = DarkModeTransparent
                                ),
                                onValueChange = {
                                    repeatPassword = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription =
                                            "repeatPasswordTextFieldRegistrationScreen"
                                    }
                                    .onFocusChanged {
                                        repeatPasswordLabelStyle.value =
                                            if (it.isFocused) AppTypography.bodySmall else AppTypography.bodyLarge
                                    },
                                maxLines = 1,
                                singleLine = true,
                                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                ),
                                trailingIcon = {
                                    Icon(
                                        painter = imagePassword,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 5.dp)
                                            .width(25.dp)
                                    )
                                },
                                isError = errorMessageRepeatPassword != null
                            )
                            if (errorMessageRepeatPassword != null) {
                                Text(
                                    text = errorMessageRepeatPassword ?: "",
                                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                                )
                            }

                            // Show Passwords Text
                            TextViewWithFont(
                                text = stringResource(id = R.string.intro_register_show_password),
                                color = ThmLoginDescriptionTextColor,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 10.dp)
                                    .clickable {
                                        showPasswords = !showPasswords
                                    }
                            )
                        }

                        // Progress Bar (progressBar)
                        if (state.loading) {
                            RotatingRingIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(top = 10.dp)
                                    .constrainAs(progressBar) {
                                        top.linkTo(rlRepeatPassword.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                            )
                        }

                        // Register Button (btnRegister)
                        ButtonWithFont(
                            text = stringResource(id = R.string.register_submit_title).uppercase(),
                            onClick = {
                                val nameValidation = viewModel.getNameError(name, context)
                                val groupNameValidation = viewModel.getGroupNameError(
                                    groupNameFirstRow,
                                    groupNameSecondRow,
                                    context
                                )
                                val addressValidation = viewModel.getAddressError(address, context)
                                val emailValidation = viewModel.getEmailError(email, context)
                                val passwordValidation =
                                    viewModel.getPasswordError(password, context)
                                val repeatPasswordValidation =
                                    viewModel.getRepeatPasswordError(
                                        password,
                                        repeatPassword,
                                        context
                                    )

                                if (nameValidation.isNotBlank() || groupNameValidation.isNotBlank() ||
                                    addressValidation.isNotBlank() || emailValidation.isNotBlank() ||
                                    passwordValidation.isNotBlank() || repeatPasswordValidation.isNotBlank()
                                ) {
                                    errorMessageName = nameValidation.ifBlank { null }
                                    errorMessageGroupName = groupNameValidation.ifBlank { null }
                                    errorMessageAddress = addressValidation.ifBlank { null }
                                    errorMessageEmail = emailValidation.ifBlank { null }
                                    errorMessagePassword = passwordValidation.ifBlank { null }
                                    errorMessageRepeatPassword =
                                        repeatPasswordValidation.ifBlank { null }
                                } else {
                                    isButtonEnabled = false
                                    viewModel.onEvent(
                                        RegistrationScreenEvent.OnRegister(
                                            name = name,
                                            groupNameFirstRow = groupNameFirstRow,
                                            groupNameSecondRow = groupNameSecondRow,
                                            phone = phone,
                                            address = address,
                                            email = email,
                                            password = password,
                                            context = context
                                        )
                                    )
                                }
                            },
                            backgroundColor = ThmMainButtonBackgroundColor,
                            textColor = ThmLoginButtonTextColor,
                            fontSize = ThmButtonTextSize,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = ThmButtonLetterSpacing,
                            modifier = Modifier
                                .width(250.dp)
                                .height(40.dp)
                                .constrainAs(btnRegister) {
                                    top.linkTo(if (state.loading) progressBar.bottom else rlRepeatPassword.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                },
                            enabled = isButtonEnabled
                        )
                    }
                }
            }
        }
    }
}