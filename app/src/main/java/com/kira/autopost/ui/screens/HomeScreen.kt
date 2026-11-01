package com.kira.autopost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.kira.autopost.ui.components.AppTab
import com.kira.autopost.ui.theme.KiraBlue
import com.kira.autopost.ui.theme.KiraBlueCardBg
import com.kira.autopost.ui.theme.KiraBlueCardBorder
import com.kira.autopost.ui.theme.KiraBlueDark
import com.kira.autopost.ui.theme.StatusFailed
import com.kira.autopost.ui.theme.StatusSuccess
import com.kira.autopost.ui.theme.TextMuted
import com.kira.autopost.ui.theme.TextPrimary
import com.kira.autopost.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    appPreferences: AppPreferences,
    onNavigateTab: (AppTab) -> Unit,
    onTogglePosting: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPostingActive by appPreferences.isPostingActive.collectAsState()
    val accounts by appPreferences.accounts.collectAsState()
    val selectedGroupIds by appPreferences.selectedGroupIds.collectAsState()
    val successCount by appPreferences.successCount.collectAsState()
    val failedCount by appPreferences.failedCount.collectAsState()
    val floodWaitCount by appPreferences.floodWaitCount.collectAsState()
    val safeDelay by appPreferences.safeDelaySeconds.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Safe Sending Card (Image 2 style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(KiraBlueCardBg)
                .border(1.dp, KiraBlueCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Blue Shield Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(KiraBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Sending Shield",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "ការការពារគណនី (Safe Sending ${safeDelay}s)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "គម្លាតពេលផុស $safeDelay វិនាទី ដើម្បីការពារការ Ban ឬ Flood Wait ពី Telegram",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Summary Section Header
        Text(
            text = stringResource(id = R.string.summary_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Two Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Connected Accounts Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                    .clickable { onNavigateTab(AppTab.ACCOUNTS) }
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Accounts",
                            tint = KiraBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.summary_accounts),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${accounts.count { it.isActive }}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = stringResource(id = R.string.summary_accounts_connected),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Selected Groups Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                    .clickable { onNavigateTab(AppTab.GROUPS) }
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Groups",
                            tint = KiraBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.summary_groups_selected),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${selectedGroupIds.size}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = stringResource(id = R.string.summary_groups_ready),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Results Card (លទ្ធផលនៃការផុសកន្លងមក)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.results_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = stringResource(id = R.string.results_view_all),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.clickable { onNavigateTab(AppTab.REPORTS) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Success
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$successCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.stat_success),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Failed
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$failedCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.stat_failed),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Skipped / Flood Wait
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$floodWaitCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.stat_skipped),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Quick Actions Header
        Text(
            text = stringResource(id = R.string.quick_actions_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ONE Toggle Button: Start / Stop Posting
        Button(
            onClick = onTogglePosting,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPostingActive) Color(0xFFDC2626) else KiraBlue
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isPostingActive) Icons.Default.Stop else Icons.Default.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPostingActive) {
                        stringResource(id = R.string.btn_stop_posting)
                    } else {
                        stringResource(id = R.string.btn_start_posting)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Outlined Add Account Button
        OutlinedButton(
            onClick = { onNavigateTab(AppTab.ACCOUNTS) },
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, KiraBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = KiraBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.btn_add_telegram_account),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KiraBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
