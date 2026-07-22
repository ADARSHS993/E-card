import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import com.example.myapplication.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.domain.di.model.USerDataParent
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.presentation.Navigation.SubNavigation
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel
import com.example.myapplication.presentation.utils.LogOutAlertDialog
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    firebaseAuth: FirebaseAuth,
    viewModel: ShoppingAppViewModel = hiltViewModel(),
) {


    LaunchedEffect(key1 = true) {
        viewModel.getUserById(firebaseAuth.currentUser!!.uid)
    }

    val profileScreenState = viewModel.prodfileScreenState.collectAsStateWithLifecycle()
    val upDateScreenState = viewModel.prodfileScreenState.collectAsStateWithLifecycle()
    val userProfileimageScreenState =
        viewModel.uploadUserProfileImageState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val isEditing = remember { mutableStateOf(false) }
    //   val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val imageUri = remember { mutableStateOf("") }

    val firstname =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.firstName ?: "") }
    val lastname =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.lastName ?: "") }
    val email =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.email ?: "") }
    val phoneno =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.phoneNumber ?: "") }
    val address =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.address ?: "") }

    LaunchedEffect(userProfileimageScreenState.value.UserData) {

        profileScreenState.value.userData?.userData?.let { userData ->

            firstname.value = userData.firstName ?: ""
            lastname.value = userData.lastName ?: ""
            email.value = userData.email ?: ""
            phoneno.value = userData.phoneNumber ?: ""
            address.value = userData.address ?: ""
            imageUri.value = userData.profileImage ?: ""
        }
    }

    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->

            if (uri != null) {
                viewModel.uploadUSerProfileImage(uri)
                imageUri.value = uri.toString() //it is converting into string
            }
        }

    if (upDateScreenState.value.userData != null) {
        Toast.makeText(context, upDateScreenState.value.userData.toString(), Toast.LENGTH_SHORT)
            .show()
    } else if (upDateScreenState.value.errorMessage != null) {
        Toast.makeText(context, upDateScreenState.value.errorMessage, Toast.LENGTH_SHORT).show()
    } else if (upDateScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (userProfileimageScreenState.value.UserData != null) {
        imageUri.value = userProfileimageScreenState.value.UserData.toString()
    } else if (userProfileimageScreenState.value.errorMessage != null) {
        Toast.makeText(context, userProfileimageScreenState.value.errorMessage, Toast.LENGTH_SHORT)
            .show()
    } else if (userProfileimageScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }


    if (profileScreenState.value.userData != null) {

        Scaffold(

        )
        { innerpadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerpadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Start)
                )
                {
                    SubcomposeAsyncImage(

                        model = if (isEditing.value) imageUri.value else imageUri.value,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, color = colorResource(id = R.color.orange), CircleShape)
                    ) {

                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                            is AsyncImagePainter.State.Error -> Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )

                            else -> SubcomposeAsyncImageContent()
                        }
                    }

                    if (isEditing.value) {
                        IconButton(
                            onClick = {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .background(
                                    androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Change Picture",
                                tint = Color.White
                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Row {
                    OutlinedTextField(
                        value = firstname.value,
                        onValueChange = { firstname.value = it },
                        label = { Text("First Name") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.orange),
                            focusedBorderColor = colorResource(id = R.color.orange)
                        ),
                        readOnly = if (isEditing.value) false else true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = lastname.value,
                        onValueChange = { lastname.value = it },
                        label = { Text("First Name") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.orange),
                            focusedBorderColor = colorResource(id = R.color.orange)
                        ),
                        readOnly = if (isEditing.value) false else true,
                        shape = RoundedCornerShape(10.dp)
                    )


                }

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.orange),
                        focusedBorderColor = colorResource(id = R.color.orange)
                    ),
                    readOnly = if (isEditing.value) false else true,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text("Email") }
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = phoneno.value,
                    onValueChange = { phoneno.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.orange),
                        focusedBorderColor = colorResource(id = R.color.orange)
                    ),
                    readOnly = if (isEditing.value) false else true,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text("Phone Number") }
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.orange),
                        focusedBorderColor = colorResource(id = R.color.orange)
                    ),
                    readOnly = if (isEditing.value) false else true,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text("Address") }
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        colorResource(id = R.color.orange)
                    )
                ) {
                    Text("Log Out")
                }

                if (showDialog.value) {
                    LogOutAlertDialog(
                        onDismiss = {
                            showDialog.value = false
                        },
                        onConfirm = {
                            firebaseAuth.signOut()
                            navController.navigate(SubNavigation.LoginSignUpScreen)
                        }
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                if (isEditing.value == false) {

                    OutlinedButton(
                        onClick = { isEditing.value = !isEditing.value },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Edit Profile")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            val updateUserData = UserData(
                                firstName = firstname.value,
                                lastName = lastname.value,
                                email = email.value,
                                phoneNumber = phoneno.value,
                                address = address.value,
                                profileImage = imageUri.value,
                            )

                            val userDATApARENT = USerDataParent(

                                nodeID = profileScreenState.value.userData!!.nodeID,
                                userData = updateUserData
                            )

                            viewModel.upDateUserData(userDATApARENT)
                            isEditing.value = !isEditing.value
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        Text("Save Profile")
                    }
                }
            }
        }
    } else if (profileScreenState.value.errorMessage != null) {
        Text(profileScreenState.value.errorMessage!!)
    } else if (profileScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

