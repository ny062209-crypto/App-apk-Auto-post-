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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.kira.autopost.model.PostStatus
import com.kira.autopost.ui.theme.KiraBlue
import com.kira.autopost.ui.theme.StatusFailed
import com.kira.autopost.ui.theme.StatusFlood
import com.kira.autopost.ui.theme.StatusSuccess
import com.kira.autopost.ui.theme.TextPrimary
import com.kira.autopost.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    appPreferences: AppPreferences,
    modifier: Modifier = Modifier
) {
    val reports by appPreferences.reports.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.reports_header),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "កំណត់ត្រាសរុប: ${reports.size}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            IconButton(onClick = { appPreferences.clearReports() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear logs",
                    tint = Color(0xFFEF4444)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.log_empty),
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reports) { report ->
                    val statusColor = when (report.status) {
                        PostStatus.SUCCESS -> StatusSuccess
                        PostStatus.FAILED -> StatusFailed
                        PostStatus.FLOOD_WAIT -> StatusFlood
                    }
                    val statusText = when (report.status) {
                        PostStatus.SUCCESS -> stringResource(id = R.string.status_success)
                        PostStatus.FAILED -> stringResource(id = R.string.status_failed)
                        PostStatus.FLOOD_WAIT -> stringResource(
                            id = R.string.status_flood_wait,
                            report.floodWaitSeconds
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = report.groupTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusColor.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "សមាជិក: ${report.memberName} " +
                                        if (report.memberUsername.isNotEmpty()) "(@${report.memberUsername})" else "",
                                fontSize = 13.sp,
                                color = TextPrimary
                            )

                            if (report.messagePreview.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${report.messagePreview}\"",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }

                            if (report.errorMessage != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Error: ${report.errorMessage}",
                                    fontSize = 12.sp,
                                    color = StatusFailed
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = dateFormat.format(Date(report.timestamp)),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
