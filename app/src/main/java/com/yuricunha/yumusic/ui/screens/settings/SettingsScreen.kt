package com.yuricunha.yumusic.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.ui.screens.settings.viewmodel.SettingsViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.BackgroundElevated
import com.yuricunha.yumusic.ui.theme.HoverRipple
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onConnected: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onConnected()
        }
    }

    SettingsScreenContent(
        serverUrl = uiState.serverUrl,
        username = uiState.username,
        password = uiState.password,
        isSaving = uiState.isSaving,
        errorMessage = uiState.errorMessage,
        onServerUrlChange = viewModel::onServerUrlChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSave = viewModel::save,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    serverUrl: String,
    username: String,
    password: String,
    isSaving: Boolean,
    errorMessage: String?,
    onServerUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.screen_settings),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextTertiary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = HoverRipple,
                focusedContainerColor = BackgroundElevated,
                unfocusedContainerColor = BackgroundElevated,
                cursorColor = PrimaryAccent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = TextSecondary,
                unfocusedLabelColor = TextTertiary,
            )

            OutlinedTextField(
                value = serverUrl,
                onValueChange = onServerUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.settings_server_url)) },
                placeholder = { Text(stringResource(R.string.settings_server_url_hint), color = TextTertiary) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.settings_username)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.settings_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
            )

            // Error message
            if (!errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryAccent,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = TextPrimary,
                ),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = TextPrimary,
                        modifier = Modifier.height(20.dp),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.settings_save),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}
