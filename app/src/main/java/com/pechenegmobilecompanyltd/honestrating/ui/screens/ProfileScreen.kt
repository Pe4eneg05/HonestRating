package com.pechenegmobilecompanyltd.honestrating.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.R
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import java.io.InputStream

data class ProfileUiState(
    val avatarBase64: String? = null,
    val name: String = "",
    val isHr: Boolean = false,
    val avatarUploading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    onComplete: () -> Unit
) {
    val uid = auth.currentUser?.uid
    val context = LocalContext.current
    var uiState by remember { mutableStateOf(ProfileUiState()) }
    var isSaving by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Загрузка данных Firestore (и активация shimmer)
    LaunchedEffect(uid) {
        if (uid != null) {
            isDataLoaded = false
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { snapshot ->
                    uiState = uiState.copy(
                        name = snapshot.getString("name") ?: "",
                        isHr = snapshot.getBoolean("isHr") ?: false,
                        avatarBase64 = snapshot.getString("avatarBase64")
                    )
                    isDataLoaded = true
                }
                .addOnFailureListener { isDataLoaded = true }
        }
    }

    // Лаунчер выбора фото и кодирования в base64
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uiState = uiState.copy(avatarUploading = true)
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null && uid != null) {
                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                firestore.collection("users").document(uid)
                    .update("avatarBase64", base64)
                    .addOnSuccessListener {
                        uiState = uiState.copy(
                            avatarBase64 = base64,
                            avatarUploading = false
                        )
                    }
                    .addOnFailureListener {
                        uiState = uiState.copy(avatarUploading = false)
                    }
            } else {
                uiState = uiState.copy(avatarUploading = false)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Crossfade(targetState = isDataLoaded, label = "ProfileCrossfade") { loaded ->
                if (!loaded) {
                    ProfileShimmerPlaceholder()
                } else {
                    ProfileContent(
                        uiState = uiState,
                        isSaving = isSaving,
                        onPickAvatar = { pickImageLauncher.launch("image/*") },
                        onNameChange = { uiState = uiState.copy(name = it) },
                        onRoleChange = { uiState = uiState.copy(isHr = it) },
                        onSave = {
                            isSaving = true
                            firestore.collection("users").document(uid!!).set(
                                mapOf(
                                    "name" to uiState.name,
                                    "isHr" to uiState.isHr,
                                    "avatarBase64" to uiState.avatarBase64
                                )
                            ).addOnSuccessListener {
                                isSaving = false
                                onComplete()
                            }.addOnFailureListener {
                                isSaving = false
                            }
                        },
                        avatarUploading = uiState.avatarUploading
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    isSaving: Boolean,
    onPickAvatar: () -> Unit,
    onNameChange: (String) -> Unit,
    onRoleChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    avatarUploading: Boolean
) {
    val bitmapImage: ImageBitmap? = remember(uiState.avatarBase64) {
        uiState.avatarBase64?.let { b64 ->
            try {
                val bytes = Base64.decode(b64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) { null }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(enabled = !avatarUploading) { onPickAvatar() },
            contentAlignment = Alignment.Center
        ) {
            if (bitmapImage == null) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.avatar_content_description),
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            } else {
                Image(
                    bitmap = bitmapImage,
                    contentDescription = stringResource(R.string.avatar_content_description),
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (avatarUploading) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    modifier = Modifier.matchParentSize(),
                    shape = CircleShape
                ) {}
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name_hint)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surfaceContainerLowest,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Divider(
            Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .fillMaxWidth().alpha(0.5f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f))
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(stringResource(R.string.role_label), modifier = Modifier.weight(1f))
            AnimatedContent(targetState = uiState.isHr) { hr ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = hr,
                        onCheckedChange = onRoleChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (hr) stringResource(R.string.role_hr) else stringResource(R.string.role_employee),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        AnimatedContent(
            targetState = isSaving,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxWidth()
        ) { saving ->
            Button(
                onClick = onSave,
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp
                )
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        stringResource(R.string.save_button),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileShimmerPlaceholder() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f))
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(44.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f))
        )
        Divider(
            Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .fillMaxWidth()
                .alpha(0.4f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(34.dp)
                .clip(RoundedCornerShape(14.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.27f))
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(15.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f))
        )
    }
}