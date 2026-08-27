package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CalculatedAsset
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PortfolioViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppThemeMode
import com.example.util.LocalSoundHaptic
import com.example.util.SecurityManager
import com.example.util.SoundHapticHelper
import com.example.util.Strings

enum class NavigationTab(val icon: ImageVector) {
    DASHBOARD(Icons.Default.Dashboard),
    ASSETS(Icons.Default.AccountBalanceWallet),
    REBALANCE(Icons.Default.Balance),
    ANALYTICS(Icons.Default.Analytics),
    CATEGORIES(Icons.Default.Category);

    fun getTitle(strings: Strings): String {
        return when (this) {
            DASHBOARD -> strings.tabDashboard
            ASSETS -> strings.tabAssets
            REBALANCE -> strings.tabRebalance
            ANALYTICS -> strings.tabAnalytics
            CATEGORIES -> strings.tabCategories
        }
    }
}

class MainActivity : FragmentActivity() {

    private val viewModel: PortfolioViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val strings = AppStrings.get(appLanguage)
            val layoutDirection = if (appLanguage == AppLanguage.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr

            val securityManager = remember { SecurityManager(this@MainActivity) }
            val soundHapticHelper = remember { SoundHapticHelper(this@MainActivity) }
            val isSoundEnabled by soundHapticHelper.soundEnabled.collectAsStateWithLifecycle()
            val isHapticEnabled by soundHapticHelper.hapticEnabled.collectAsStateWithLifecycle()
            var isPasscodeEnabled by remember { mutableStateOf(securityManager.isPasscodeEnabled) }
            var isBiometricEnabled by remember { mutableStateOf(securityManager.isBiometricEnabled) }
            var isUnlocked by remember { mutableStateOf(!securityManager.isPasscodeEnabled) }

            MyApplicationTheme(themeMode = themeMode) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    LocalSoundHaptic provides soundHapticHelper
                ) {
                    val summary by viewModel.portfolioSummary.collectAsStateWithLifecycle()
                    val allPortfolios by viewModel.allPortfolios.collectAsStateWithLifecycle()
                    val currentPortfolio by viewModel.currentPortfolio.collectAsStateWithLifecycle()
                    val allAssets by viewModel.allAssetsForActivePortfolio.collectAsStateWithLifecycle()
                    val filteredAssets by viewModel.filteredCalculatedAssets.collectAsStateWithLifecycle()
                    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
                    val snapshots by viewModel.allSnapshotsForActivePortfolio.collectAsStateWithLifecycle()

                    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                    val categoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
                    val actionFilter by viewModel.selectedActionFilter.collectAsStateWithLifecycle()
                    val showFrozenOnly by viewModel.showFrozenOnly.collectAsStateWithLifecycle()
                    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
                    val currency by viewModel.currency.collectAsStateWithLifecycle()
                    val usePersianDigits by viewModel.usePersianDigits.collectAsStateWithLifecycle()
                    val tolerancePercent by viewModel.tolerancePercent.collectAsStateWithLifecycle()
                    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

                    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

                    // Dialog states
                    var showAddEditAssetDialog by remember { mutableStateOf(false) }
                    var editingAsset by remember { mutableStateOf<CalculatedAsset?>(null) }
                    var showQuickUpdateDialog by remember { mutableStateOf(false) }
                    var quickUpdateTarget by remember { mutableStateOf<CalculatedAsset?>(null) }
                    var showCashSimulator by remember { mutableStateOf(false) }
                    var showSettingsDialog by remember { mutableStateOf(false) }
                    var showAddCategoryDialog by remember { mutableStateOf(false) }
                    var showSetPasscodeDialog by remember { mutableStateOf(false) }
                    var showBackupRestoreDialog by remember { mutableStateOf(false) }
                    var showUnlockPrivacyDialog by remember { mutableStateOf(false) }
                    var assetToDelete by remember { mutableStateOf<CalculatedAsset?>(null) }

                    fun triggerBiometric(onSuccess: () -> Unit) {
                        if (securityManager.isBiometricAvailable() && isBiometricEnabled) {
                            securityManager.showBiometricPrompt(
                                activity = this@MainActivity,
                                title = strings.biometricPromptTitle,
                                subtitle = strings.biometricPromptSubtitle,
                                negativeButtonText = strings.cancel,
                                onSuccess = onSuccess
                            )
                        }
                    }

                    fun requestUnhidePrivacy() {
                        if (isPrivacyMode) {
                            // Trying to unhide
                            if (securityManager.isPasscodeEnabled) {
                                if (securityManager.isBiometricAvailable() && isBiometricEnabled) {
                                    triggerBiometric {
                                        viewModel.setPrivacyMode(false)
                                    }
                                } else {
                                    showUnlockPrivacyDialog = true
                                }
                            } else {
                                viewModel.setPrivacyMode(false)
                            }
                        } else {
                            // Hiding requires no auth
                            viewModel.setPrivacyMode(true)
                        }
                    }

                    if (!isUnlocked) {
                        LockScreen(
                            strings = strings,
                            usePersianDigits = usePersianDigits,
                            isBiometricAvailable = securityManager.isBiometricAvailable(),
                            isBiometricEnabled = isBiometricEnabled,
                            onVerifyPin = { pin ->
                                val ok = securityManager.verifyPin(pin)
                                if (ok) isUnlocked = true
                                ok
                            },
                            onTriggerBiometric = {
                                triggerBiometric { isUnlocked = true }
                            },
                            onForgotPasscode = {
                                securityManager.disableLock()
                                isPasscodeEnabled = false
                                viewModel.resetSettingsToDefaults()
                                isUnlocked = true
                            }
                        )
                    } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AccountBalanceWallet,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = currentTab.getTitle(strings),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            if (currentTab == NavigationTab.DASHBOARD) {
                                                Text(
                                                    text = strings.appSubtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            soundHapticHelper.tap()
                                            requestUnhidePrivacy()
                                        },
                                        modifier = Modifier.testTag("privacy_mode_toggle_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isPrivacyMode) strings.showValues else strings.hideValues,
                                            tint = if (isPrivacyMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            soundHapticHelper.tap()
                                            showCashSimulator = true
                                        },
                                        modifier = Modifier.testTag("cash_simulator_button")
                                    ) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = strings.cashInjection,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            soundHapticHelper.tap()
                                            showBackupRestoreDialog = true
                                        },
                                        modifier = Modifier.testTag("topbar_backup_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Backup,
                                            contentDescription = strings.backupSection,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            soundHapticHelper.tap()
                                            showSettingsDialog = true
                                        },
                                        modifier = Modifier.testTag("settings_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = strings.settingsTitle,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        },
                        floatingActionButton = {
                            if (currentTab == NavigationTab.ASSETS) {
                                FloatingActionButton(
                                    onClick = {
                                        soundHapticHelper.tap()
                                        editingAsset = null
                                        showAddEditAssetDialog = true
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.testTag("fab_add_asset")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = strings.addNewAsset)
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .testTag("bottom_navigation_bar"),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                tonalElevation = 2.dp
                            ) {
                                NavigationTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    val title = tab.getTitle(strings)
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            soundHapticHelper.tap()
                                            currentTab = tab
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = title,
                                                tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentTab) {
                                NavigationTab.DASHBOARD -> {
                                    DashboardScreen(
                                        summary = summary,
                                        portfolios = allPortfolios,
                                        activePortfolioId = currentPortfolio?.id ?: 1,
                                        onSelectPortfolio = { viewModel.selectPortfolio(it) },
                                        onCreatePortfolio = { name, desc, icon, color -> viewModel.addPortfolio(name, desc, color) },
                                        onEditPortfolio = { id, name, desc -> viewModel.updatePortfolio(id, name, desc) },
                                        onDeletePortfolio = { viewModel.deletePortfolio(it) },
                                        strings = strings,
                                        currency = currency,
                                        usePersianDigits = usePersianDigits,
                                        onNavigateToAssets = { currentTab = NavigationTab.ASSETS },
                                        onNavigateToRebalance = { currentTab = NavigationTab.REBALANCE },
                                        onOpenCashSimulator = { showCashSimulator = true },
                                        onNormalizeWeights = { viewModel.normalizeTargetWeights() },
                                        onRecordSnapshot = { viewModel.recordCurrentSnapshot("ثبت از داشبورد") },
                                        onQuickEditAsset = { asset ->
                                            quickUpdateTarget = asset
                                            showQuickUpdateDialog = true
                                        },
                                        isPrivacyMode = isPrivacyMode,
                                        onTogglePrivacyMode = { requestUnhidePrivacy() }
                                    )
                                }

                                NavigationTab.ASSETS -> {
                                    AssetsListScreen(
                                        assets = filteredAssets,
                                        categories = allCategories,
                                        strings = strings,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { viewModel.searchQuery.value = it },
                                        selectedCategoryFilter = categoryFilter,
                                        onCategoryFilterChange = { viewModel.selectedCategoryFilter.value = it },
                                        selectedActionFilter = actionFilter,
                                        onActionFilterChange = { viewModel.selectedActionFilter.value = it },
                                        sortOption = sortOption,
                                        onSortOptionChange = { viewModel.sortOption.value = it },
                                        currency = currency,
                                        usePersianDigits = usePersianDigits,
                                        onAddAsset = {
                                            editingAsset = null
                                            showAddEditAssetDialog = true
                                        },
                                        onEditAsset = { asset ->
                                            editingAsset = asset
                                            showAddEditAssetDialog = true
                                        },
                                        onQuickUpdateAsset = { asset ->
                                            quickUpdateTarget = asset
                                            showQuickUpdateDialog = true
                                        },
                                        onDeleteAsset = { asset ->
                                            assetToDelete = asset
                                        },
                                        isPrivacyMode = isPrivacyMode
                                    )
                                }

                                NavigationTab.REBALANCE -> {
                                    RebalanceScreen(
                                        summary = summary,
                                        strings = strings,
                                        currency = currency,
                                        usePersianDigits = usePersianDigits,
                                        onNormalizeWeights = { viewModel.normalizeTargetWeights() },
                                        onOpenCashSimulator = { showCashSimulator = true },
                                        onQuickEditAsset = { asset ->
                                            quickUpdateTarget = asset
                                            showQuickUpdateDialog = true
                                        },
                                        isPrivacyMode = isPrivacyMode
                                    )
                                }

                                NavigationTab.ANALYTICS -> {
                                    AnalyticsScreen(
                                        summary = summary,
                                        snapshots = snapshots,
                                        strings = strings,
                                        currency = currency,
                                        usePersianDigits = usePersianDigits,
                                        onRecordSnapshot = { note -> viewModel.recordCurrentSnapshot(note) },
                                        onDeleteSnapshot = { id -> viewModel.deleteSnapshot(id) }
                                    )
                                }

                                NavigationTab.CATEGORIES -> {
                                    CategoriesScreen(
                                        categories = allCategories,
                                        summary = summary,
                                        strings = strings,
                                        currency = currency,
                                        usePersianDigits = usePersianDigits,
                                        onAddCategory = { name, colorHex, iconName, targetWeight, minWeight, maxWeight, targetTolerance, desc ->
                                            viewModel.addCategory(
                                                name,
                                                colorHex,
                                                iconName,
                                                targetWeight,
                                                minWeight,
                                                maxWeight,
                                                targetTolerance,
                                                desc
                                            )
                                        },
                                        onUpdateCategory = { viewModel.updateCategory(it) },
                                        onDeleteCategory = { viewModel.deleteCategory(it) }
                                    )
                                }
                            }
                        }
                    }
                    }

                    // Add / Edit Asset Dialog
                    if (showAddEditAssetDialog) {
                        AddEditAssetDialog(
                            initialAsset = editingAsset?.asset,
                            categories = allCategories,
                            existingAssets = allAssets,
                            currency = currency,
                            usePersianDigits = usePersianDigits,
                            strings = strings,
                            onDismiss = { showAddEditAssetDialog = false },
                            onSave = { name, symbol, categoryId, quantity, unitPrice, targetWeight, isFrozen, frozenPercentage, notes ->
                                if (editingAsset != null) {
                                    viewModel.updateAsset(
                                        id = editingAsset!!.asset.id,
                                        name = name,
                                        symbol = symbol,
                                        categoryId = categoryId,
                                        quantity = quantity,
                                        unitPrice = unitPrice,
                                        targetWeight = targetWeight,
                                        isFrozen = isFrozen,
                                        frozenPercentage = frozenPercentage,
                                        notes = notes
                                    )
                                } else {
                                    viewModel.addAsset(
                                        name = name,
                                        symbol = symbol,
                                        categoryId = categoryId,
                                        quantity = quantity,
                                        unitPrice = unitPrice,
                                        targetWeight = targetWeight,
                                        isFrozen = isFrozen,
                                        frozenPercentage = frozenPercentage,
                                        notes = notes
                                    )
                                }
                                showAddEditAssetDialog = false
                            },
                            onAddNewCategory = {
                                showAddCategoryDialog = true
                            }
                        )
                    }

                    // Quick Update Modal
                    if (showQuickUpdateDialog && quickUpdateTarget != null) {
                        QuickUpdateDialog(
                            item = quickUpdateTarget!!,
                            currency = currency,
                            usePersianDigits = usePersianDigits,
                            strings = strings,
                            onDismiss = {
                                showQuickUpdateDialog = false
                                quickUpdateTarget = null
                            },
                            onConfirm = { newQuantity, newUnitPrice ->
                                viewModel.quickUpdateHolding(quickUpdateTarget!!.asset.id, newQuantity, newUnitPrice)
                                showQuickUpdateDialog = false
                                quickUpdateTarget = null
                            }
                        )
                    }

                    // Cash Injection Simulator Modal
                    if (showCashSimulator) {
                        CashInjectionModal(
                            currency = currency,
                            usePersianDigits = usePersianDigits,
                            strings = strings,
                            onDismiss = { showCashSimulator = false },
                            onSimulate = { amount -> viewModel.simulateCashInjection(amount) },
                            onApply = { injections -> viewModel.applyCashInjection(injections) }
                        )
                    }

                    // Settings Dialog
                    if (showSettingsDialog) {
                        SettingsDialog(
                            strings = strings,
                            appLanguage = appLanguage,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            themeMode = themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            currentCurrency = currency,
                            onCurrencyChange = { viewModel.setCurrency(it) },
                            usePersianDigits = usePersianDigits,
                            onPersianDigitsChange = { viewModel.setUsePersianDigits(it) },
                            tolerancePercent = tolerancePercent,
                            onToleranceChange = { viewModel.setTolerance(it) },
                            isPasscodeEnabled = isPasscodeEnabled,
                            onPasscodeToggle = { enable ->
                                if (!enable) {
                                    securityManager.disableLock()
                                    isPasscodeEnabled = false
                                }
                            },
                            onOpenSetPasscode = { showSetPasscodeDialog = true },
                            isBiometricAvailable = securityManager.isBiometricAvailable(),
                            isBiometricEnabled = isBiometricEnabled,
                            onBiometricToggle = { enable ->
                                securityManager.isBiometricEnabled = enable
                                isBiometricEnabled = enable
                            },
                            isSoundEnabled = isSoundEnabled,
                            onSoundToggle = { soundHapticHelper.setSoundEnabled(it) },
                            isHapticEnabled = isHapticEnabled,
                            onHapticToggle = { soundHapticHelper.setHapticEnabled(it) },
                            onOpenBackupRestore = { showBackupRestoreDialog = true },
                            onResetSettings = {
                                viewModel.resetSettingsToDefaults()
                                soundHapticHelper.warningAction()
                            },
                            onDismiss = { showSettingsDialog = false }
                        )
                    }

                    // Set Passcode Dialog
                    if (showSetPasscodeDialog) {
                        SetPasscodeDialog(
                            strings = strings,
                            usePersianDigits = usePersianDigits,
                            onPasscodeSet = { pin ->
                                securityManager.setPin(pin)
                                isPasscodeEnabled = true
                                isUnlocked = true
                            },
                            onDismiss = { showSetPasscodeDialog = false }
                        )
                    }

                    // Unlock Privacy Mode Passcode Dialog
                    if (showUnlockPrivacyDialog) {
                        PasscodeUnlockDialog(
                            title = strings.unhideSecurityTitle,
                            subtitle = strings.unhideSecuritySubtitle,
                            strings = strings,
                            usePersianDigits = usePersianDigits,
                            isBiometricAvailable = securityManager.isBiometricAvailable() && isBiometricEnabled,
                            onVerifyPin = { pin ->
                                val ok = securityManager.verifyPin(pin)
                                if (ok) {
                                    viewModel.setPrivacyMode(false)
                                    showUnlockPrivacyDialog = false
                                }
                                ok
                            },
                            onBiometricClick = {
                                triggerBiometric {
                                    viewModel.setPrivacyMode(false)
                                    showUnlockPrivacyDialog = false
                                }
                            },
                            onDismiss = { showUnlockPrivacyDialog = false }
                        )
                    }

                    // Backup & Restore Dialog
                    if (showBackupRestoreDialog) {
                        BackupRestoreDialog(
                            strings = strings,
                            categories = allCategories,
                            assets = allAssets,
                            snapshots = snapshots,
                            portfolios = allPortfolios,
                            currency = currency,
                            tolerancePercent = tolerancePercent,
                            onRestoreData = { payload ->
                                viewModel.restoreBackup(payload)
                            },
                            onDismiss = { showBackupRestoreDialog = false }
                        )
                    }

                    // Add Category Modal (from Asset dialog)
                    if (showAddCategoryDialog) {
                        AddEditCategoryDialog(
                            strings = strings,
                            usePersianDigits = usePersianDigits,
                            onDismiss = { showAddCategoryDialog = false },
                            onSave = { name, colorHex, iconName, targetWeight, minWeight, maxWeight, targetTolerance, desc ->
                                viewModel.addCategory(
                                    name,
                                    colorHex,
                                    iconName,
                                    targetWeight,
                                    minWeight,
                                    maxWeight,
                                    targetTolerance,
                                    desc
                                )
                                showAddCategoryDialog = false
                            }
                        )
                    }

                    // Delete Confirmation Alert
                    if (assetToDelete != null) {
                        AlertDialog(
                            onDismissRequest = { assetToDelete = null },
                            title = { Text(strings.deleteAssetConfirmTitle) },
                            text = { Text(strings.deleteAssetConfirmText(assetToDelete!!.asset.name)) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        soundHapticHelper.deleteAction()
                                        viewModel.deleteAsset(assetToDelete!!.asset)
                                        assetToDelete = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(strings.delete)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    soundHapticHelper.tap()
                                    assetToDelete = null
                                }) {
                                    Text(strings.cancel)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
