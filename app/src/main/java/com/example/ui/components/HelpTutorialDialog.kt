package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.CurrencyFormatter
import com.example.util.HelpTopic
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpTutorialDialog(
    strings: Strings,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var expandedTopicId by remember { mutableStateOf<String?>("portfolios") }

    val filteredTopics = remember(searchQuery, selectedTopicId, strings.helpTopics) {
        strings.helpTopics.filter { topic ->
            val matchesSearch = searchQuery.isBlank() ||
                topic.title.contains(searchQuery, ignoreCase = true) ||
                topic.subtitle.contains(searchQuery, ignoreCase = true) ||
                topic.description.contains(searchQuery, ignoreCase = true) ||
                topic.steps.any { it.contains(searchQuery, ignoreCase = true) } ||
                topic.tip.contains(searchQuery, ignoreCase = true)

            val matchesFilter = selectedTopicId == null || topic.id == selectedTopicId
            matchesSearch && matchesFilter
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("help_tutorial_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = strings.helpSectionTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.helpSectionSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(strings.helpSearchPlaceholder, style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("help_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Topic Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTopicId == null,
                            onClick = {
                                soundHaptic.tap()
                                selectedTopicId = null
                            },
                            label = { Text(strings.filterAll, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                    items(strings.helpTopics) { topic ->
                        FilterChip(
                            selected = selectedTopicId == topic.id,
                            onClick = {
                                soundHaptic.tap()
                                selectedTopicId = if (selectedTopicId == topic.id) null else topic.id
                                expandedTopicId = topic.id
                            },
                            label = { Text(topic.title, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Icon(
                                    getCategoryIconVector(topic.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tutorials List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(filteredTopics, key = { it.id }) { topic ->
                        val isExpanded = expandedTopicId == topic.id
                        val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow")

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isExpanded)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    soundHaptic.tap()
                                    expandedTopicId = if (isExpanded) null else topic.id
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIconVector(topic.iconName),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = topic.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = topic.subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .rotate(arrowRotation)
                                    )
                                }

                                // Expanded Content
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier.padding(top = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                                        // Overview description
                                        Text(
                                            text = topic.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 20.sp
                                        )

                                        // Step by Step Section
                                        if (topic.steps.isNotEmpty()) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.CheckCircleOutline,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = strings.helpStepByStep,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }

                                                    topic.steps.forEachIndexed { index, stepText ->
                                                        val stepNumStr = if (usePersianDigits) {
                                                            CurrencyFormatter.toPersianDigits((index + 1).toString())
                                                        } else {
                                                            (index + 1).toString()
                                                        }

                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            verticalAlignment = Alignment.Top,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Surface(
                                                                shape = CircleShape,
                                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                                modifier = Modifier.size(20.dp)
                                                            ) {
                                                                Box(
                                                                    contentAlignment = Alignment.Center,
                                                                    modifier = Modifier.fillMaxSize()
                                                                ) {
                                                                    Text(
                                                                        text = stepNumStr,
                                                                        style = MaterialTheme.typography.labelSmall,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = MaterialTheme.colorScheme.primary,
                                                                        fontSize = 11.sp
                                                                    )
                                                                }
                                                            }
                                                            Text(
                                                                text = stepText,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                lineHeight = 18.sp,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Tip Card (if present)
                                        if (topic.tip.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Icon(
                                                        Icons.Default.Lightbulb,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.tertiary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = strings.helpQuickTips,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.tertiary
                                                        )
                                                        Text(
                                                            text = topic.tip,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            lineHeight = 17.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Close Button
                Button(
                    onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("help_close_button")
                ) {
                    Text(strings.close)
                }
            }
        }
    }
}
