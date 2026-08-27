package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PortfolioProfile
import com.example.util.LocalSoundHaptic
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioManageDialog(
    portfolios: List<PortfolioProfile>,
    activePortfolioId: Int,
    strings: Strings,
    onDismiss: () -> Unit,
    onSelectPortfolio: (Int) -> Unit,
    onCreatePortfolio: (name: String, description: String, icon: String, colorHex: String) -> Unit,
    onEditPortfolio: (id: Int, name: String, description: String) -> Unit,
    onDeletePortfolio: (Int) -> Unit
) {
    val soundHaptic = LocalSoundHaptic.current
    var isCreatingNew by remember { mutableStateOf(false) }
    var editingPortfolio by remember { mutableStateOf<PortfolioProfile?>(null) }

    var newName by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#3B82F6") }

    val presetColors = listOf("#3B82F6", "#10B981", "#8B5CF6", "#F59E0B", "#EC4899", "#6366F1", "#06B6D4")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isCreatingNew) strings.createPortfolio else if (editingPortfolio != null) strings.editPortfolio else strings.managePortfolios,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        soundHaptic.tap()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                HorizontalDivider()

                if (isCreatingNew || editingPortfolio != null) {
                    // Create or Edit Form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text(strings.portfolioName + " *") },
                            placeholder = { Text("مثال: سبد سرمایه‌گذاری بازنشستگی، سبد ریسک بالا") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = newDesc,
                            onValueChange = { newDesc = it },
                            label = { Text(strings.portfolioDesc) },
                            placeholder = { Text("توضیحات یا استراتژی این سبد...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (isCreatingNew) {
                            Text(
                                text = strings.categoryColor,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                presetColors.forEach { hex ->
                                    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Gray }
                                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .clickable {
                                                soundHaptic.tap()
                                                selectedColorHex = hex
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    soundHaptic.tap()
                                    isCreatingNew = false
                                    editingPortfolio = null
                                    newName = ""
                                    newDesc = ""
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(strings.cancel)
                            }

                            Button(
                                onClick = {
                                    if (newName.isNotBlank()) {
                                        soundHaptic.successAction()
                                        if (isCreatingNew) {
                                            onCreatePortfolio(newName.trim(), newDesc.trim(), "wallet", selectedColorHex)
                                        } else if (editingPortfolio != null) {
                                            onEditPortfolio(editingPortfolio!!.id, newName.trim(), newDesc.trim())
                                        }
                                        isCreatingNew = false
                                        editingPortfolio = null
                                        newName = ""
                                        newDesc = ""
                                    }
                                },
                                enabled = newName.isNotBlank(),
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(strings.save)
                            }
                        }
                    }
                } else {
                    // List of Portfolios
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(portfolios, key = { it.id }) { portfolio ->
                            val isActive = portfolio.id == activePortfolioId
                            val portColor = try {
                                Color(android.graphics.Color.parseColor(portfolio.colorHex))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        soundHaptic.tap()
                                        onSelectPortfolio(portfolio.id)
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(portColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (portfolio.isDefault) Icons.Default.Star else Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = portColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = portfolio.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (isActive) {
                                                    Surface(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = strings.activePortfolioBadge,
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (portfolio.description.isNotBlank()) {
                                                Text(
                                                    text = portfolio.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                soundHaptic.tap()
                                                editingPortfolio = portfolio
                                                newName = portfolio.name
                                                newDesc = portfolio.description
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = strings.edit, modifier = Modifier.size(16.dp))
                                        }

                                        if (portfolios.size > 1 && !portfolio.isDefault) {
                                            IconButton(
                                                onClick = {
                                                    soundHaptic.deleteAction()
                                                    onDeletePortfolio(portfolio.id)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = strings.delete,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            soundHaptic.tap()
                            isCreatingNew = true
                            newName = ""
                            newDesc = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.createNewPortfolio)
                    }
                }
            }
        }
    }
}
