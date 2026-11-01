package com.kira.autopost.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kira.autopost.R
import com.kira.autopost.data.AppPreferences
import com.kira.autopost.model.PostTemplate
import com.kira.autopost.ui.theme.KiraBlue
import com.kira.autopost.ui.theme.KiraBlueCardBg
import com.kira.autopost.ui.theme.KiraBlueLight
import com.kira.autopost.ui.theme.TextPrimary
import com.kira.autopost.ui.theme.TextSecondary

@Composable
fun PostEditorScreen(
    appPreferences: AppPreferences,
    modifier: Modifier = Modifier
) {
    val currentTemplate by appPreferences.postTemplate.collectAsState()
    val context = LocalContext.current

    var messageText by remember(currentTemplate) { mutableStateOf(currentTemplate.messageText) }
    var mentionUsername by remember(currentTemplate) { mutableStateOf(currentTemplate.mentionUsername) }
    var mediaUri by remember(currentTemplate) { mutableStateOf(currentTemplate.mediaUri ?: "") }
    var mediaType by remember(currentTemplate) { mutableStateOf(currentTemplate.mediaType ?: "image") }
    var linksText by remember(currentTemplate) { mutableStateOf(currentTemplate.links.joinToString("\n")) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            mediaUri = uri.toString()
            mediaType = if (uri.toString().contains("video")) "video" else "image"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.post_header),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Checkbox "Mention username"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(KiraBlueLight)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = mentionUsername,
                onCheckedChange = { mentionUsername = it },
                colors = CheckboxDefaults.colors(checkedColor = KiraBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.mention_username_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(id = R.string.mention_username_hint),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Message text input
        Text(
            text = stringResource(id = R.string.post_text_label),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            placeholder = { Text(stringResource(id = R.string.post_text_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Media attachment picker
        Text(
            text = stringResource(id = R.string.media_attachment_label),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { filePicker.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = KiraBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "រូបភាព (Photo)", color = KiraBlue)
            }

            OutlinedButton(
                onClick = { filePicker.launch("video/*") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Attachment,
                    contentDescription = null,
                    tint = KiraBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "វីដេអូ (Video)", color = KiraBlue)
            }
        }

        if (mediaUri.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ភ្ជាប់ឯកសារ: $mediaUri",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { mediaUri = "" }) {
                    Text("លុប", color = Color.Red, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Links box
        Text(
            text = stringResource(id = R.string.links_label),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = linksText,
            onValueChange = { linksText = it },
            placeholder = { Text(stringResource(id = R.string.links_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Preview Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = KiraBlueCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "ទិដ្ឋភាពបង្ហាញសារគំរូ (Live Preview):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KiraBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (mentionUsername) {
                    Text(
                        text = "@new_member",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KiraBlue
                    )
                }
                Text(
                    text = messageText.ifEmpty { "(គ្មានសារ)" },
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                if (linksText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    linksText.lines().filter { it.isNotBlank() }.forEach { link ->
                        Text(
                            text = link,
                            fontSize = 13.sp,
                            color = KiraBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
            onClick = {
                val linksList = linksText.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val newTemplate = PostTemplate(
                    messageText = messageText,
                    mentionUsername = mentionUsername,
                    mediaUri = mediaUri.takeIf { it.isNotEmpty() },
                    mediaType = mediaType,
                    links = linksList
                )
                appPreferences.savePostTemplate(newTemplate)
                Toast.makeText(context, context.getString(R.string.post_saved_toast), Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = KiraBlue),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.btn_save_post),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TextButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) { content() }
}
