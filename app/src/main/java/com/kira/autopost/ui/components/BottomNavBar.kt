package com.kira.autopost.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kira.autopost.R
import com.kira.autopost.ui.theme.KiraBlue
import com.kira.autopost.ui.theme.KiraBlueLight
import com.kira.autopost.ui.theme.TextMuted
import com.kira.autopost.ui.theme.TextPrimary

enum class AppTab(val labelRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Default.Home),
    ACCOUNTS(R.string.nav_accounts, Icons.Default.Person),
    GROUPS(R.string.nav_groups, Icons.Default.Group),
    POST(R.string.nav_post, Icons.Default.Send),
    REPORTS(R.string.nav_reports, Icons.Default.BarChart),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val iconColor = if (isSelected) KiraBlue else Color(0xFF64748B)
                val textColor = if (isSelected) KiraBlue else Color(0xFF475569)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSelected && tab == AppTab.HOME) {
                        // Highlighted pill background like screenshot
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(KiraBlueLight)
                                .padding(horizontal = 14.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(id = tab.labelRes),
                                tint = KiraBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(id = tab.labelRes),
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = tab.labelRes),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = textColor,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
