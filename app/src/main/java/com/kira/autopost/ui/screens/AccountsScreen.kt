package com.kira.autopost.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kira.autopost.R
import com.kira.autopost.data.AppPreferences
import com.kira.autopost.model.TelegramAccount
import com.kira.autopost.tdlib.AuthState
import com.kira.autopost.tdlib.TdLibManager
import com.kira.autopost.ui.theme.KiraBlue
import com.kira.autopost.ui.theme.KiraBlueCardBg
import com.kira.autopost.ui.theme.KiraBlueLight
import com.kira.autopost.ui.theme.StatusSuccess
import com.kira.autopost.ui.theme.TextPrimary
import com.kira.autopost.ui.theme.TextSecondary

@Composable
fun AccountsScreen(
    appPreferences: AppPreferences,
    tdLibManager: TdLibManager,
    modifier: Modifier = Modifier
) {
    val accounts by appPreferences.accounts.collectAsState()
    val authState by tdLibManager.authState.collectAsState()

    var apiIdInput by remember { mutableStateOf("29841234") }
    var apiHashInput by remember { mutableStateOf("a1b2c3d4e5f60718293a4b5c6d7e8f90") }
    var phoneInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.accounts_header),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.accounts_desc),
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Login Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KiraBlueCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ចូលប្រើ Telegram Account (User TDLib)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiIdInput,
                        onValueChange = { apiIdInput = it },
                        label = { Text(stringResource(id = R.string.api_id_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = apiHashInput,
                        onValueChange = { apiHashInput = it },
                        label = { Text(stringResource(id = R.string.api_hash_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text(stringResource(id = R.string.phone_label)) },
                        placeholder = { Text("+85512345678") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (authState is AuthState.WaitCode || codeInput.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it },
                            label = { Text(stringResource(id = R.string.code_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (authState is AuthState.WaitPassword) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(stringResource(id = R.string.password_2fa_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val apiId = apiIdInput.toIntOrNull() ?: 0
                            if (authState is AuthState.WaitCode) {
                                tdLibManager.checkCode(codeInput)
                            } else if (authState is AuthState.WaitPassword) {
                                tdLibManager.checkPassword(passwordInput)
                            } else {
                                if (phoneInput.isNotEmpty()) {
                                    tdLibManager.startClient(apiId, apiHashInput)
                                    tdLibManager.sendPhoneNumber(phoneInput, apiId, apiHashInput)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KiraBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = when (authState) {
                                is AuthState.WaitCode -> stringResource(id = R.string.btn_confirm_login)
                                is AuthState.WaitPassword -> "ផ្ទៀងផ្ទាត់ពាក្យសម្ងាត់ 2FA"
                                else -> stringResource(id = R.string.btn_send_code)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "គណនីដែលបានភ្ជាប់ (${accounts.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(accounts) { acc ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(KiraBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = KiraBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${acc.firstName} ${acc.lastName}".trim().ifEmpty { "Telegram User" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (acc.username.isNotEmpty()) "@${acc.username}" else acc.phone,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (acc.isActive) StatusSuccess else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (acc.isActive) stringResource(id = R.string.account_status_active) else stringResource(id = R.string.account_status_inactive),
                                fontSize = 12.sp,
                                color = if (acc.isActive) StatusSuccess else Color.Gray
                            )
                        }
                    }

                    Switch(
                        checked = acc.isActive,
                        onCheckedChange = { active ->
                            val updated = accounts.map {
                                if (it.id == acc.id) it.copy(isActive = active) else it
                            }
                            appPreferences.saveAccounts(updated)
                        }
                    )

                    IconButton(
                        onClick = {
                            val updated = accounts.filter { it.id != acc.id }
                            appPreferences.saveAccounts(updated)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}
