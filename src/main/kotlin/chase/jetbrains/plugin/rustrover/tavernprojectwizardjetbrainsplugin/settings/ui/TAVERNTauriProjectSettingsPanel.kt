package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings.PackageManager
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.Desktop
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import javax.swing.*

class TAVERNTauriProjectSettingsPanel : GeneratorPeerImpl<TAVERNTauriProjectSettings>() {

    // Basic settings
    private val projectNameTextField = JBTextField()
    private val appIdentifierTextField = JBTextField("com.tauri.app")

    // Window settings
    private val windowTitleTextField = JBTextField("TAVERN App")
    private val windowWidthSpinner = JSpinner(SpinnerNumberModel(800, 50, 9999, 1))
    private val windowHeightSpinner = JSpinner(SpinnerNumberModel(600, 50, 9999, 1))
    private val customChromeCheckbox = JBCheckBox("Use custom window frame", false)

    // Package manager settings
    private val packageManagerComboBox = ComboBox<String>()

    // Cargo dependency checks
    private val cargoInstallButton = JButton("Install cargo-generate")
    private val rustupLinkLabel = JLabel("<html><a href=''>Install Rust/Cargo</a></html>")
    private val refreshDependenciesButton = JButton("Refresh")
    private var isCargoInstalled = false
    private var isCargoGenerateInstalled = false

    init {
        // Check if cargo and cargo-generate are installed
        checkCargoInstallation()

        // Configure the rustup link to open the website
        rustupLinkLabel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                BrowserUtil.browse("https://rustup.rs/")
            }
        })

        // Configure the cargo-generate install button
        cargoInstallButton.addActionListener {
            installCargoGenerate()
        }

        // Configure the refresh button
        refreshDependenciesButton.addActionListener {
            checkCargoInstallation()
        }

        // Update UI based on installation status
        updateDependencyUI()
    }

    /**
     * Checks if cargo and cargo-generate are installed
     */
    private fun checkCargoInstallation() {
        isCargoInstalled = isCommandAvailable("cargo", "--version")
        isCargoGenerateInstalled = isCommandAvailable("cargo", "generate", "--version")
        updateDependencyUI()
    }

    /**
     * Updates the UI components based on installation status
     */
    private fun updateDependencyUI() {
        cargoInstallButton.isVisible = isCargoInstalled && !isCargoGenerateInstalled
        rustupLinkLabel.isVisible = !isCargoInstalled
    }

    /**
     * Checks if a command is available by running it and checking the exit code
     */
    private fun isCommandAvailable(vararg command: String): Boolean {
        return try {
            val process = ProcessBuilder(*command)
                .redirectErrorStream(true)
                .start()
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Installs cargo-generate using cargo install
     */
    private fun installCargoGenerate() {
        ProgressManager.getInstance().run(object : Task.Backgroundable(null, "Installing cargo-generate", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Installing cargo-generate..."
                indicator.isIndeterminate = true

                try {
                    val process = ProcessBuilder("cargo", "install", "cargo-generate")
                        .redirectErrorStream(true)
                        .start()

                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        indicator.text = "Installing cargo-generate: $line"
                    }

                    val exitCode = process.waitFor()

                    ApplicationManager.getApplication().invokeLater {
                        if (exitCode == 0) {
                            Messages.showInfoMessage(
                                "cargo-generate has been successfully installed.",
                                "Installation Complete"
                            )
                            isCargoGenerateInstalled = true
                            updateDependencyUI()
                        } else {
                            Messages.showErrorDialog(
                                "Failed to install cargo-generate. Please try installing it manually using 'cargo install cargo-generate'.",
                                "Installation Failed"
                            )
                        }
                    }
                } catch (e: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        Messages.showErrorDialog(
                            "An error occurred while installing cargo-generate: ${e.message}",
                            "Installation Error"
                        )
                    }
                }
            }
        })
    }

    override fun validate(): ValidationInfo? {
        if (projectNameTextField.text.isBlank()) {
            return ValidationInfo("Project name cannot be empty", projectNameTextField)
        }

        if (appIdentifierTextField.text.isBlank()) {
            return ValidationInfo("App identifier cannot be empty", appIdentifierTextField)
        } else if (!appIdentifierTextField.text.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]$"))) {
            return ValidationInfo("App identifier must be in reverse-domain format (e.g., com.example.app)", appIdentifierTextField)
        }

        if (windowTitleTextField.text.isBlank()) {
            return ValidationInfo("Window title cannot be empty", windowTitleTextField)
        }

        return null
    }

    override fun getSettings(): TAVERNTauriProjectSettings {
        return TAVERNTauriProjectSettings().apply {
            projectName = projectNameTextField.text
            appIdentifier = appIdentifierTextField.text
            windowTitle = windowTitleTextField.text
            windowWidth = windowWidthSpinner.value as Int
            windowHeight = windowHeightSpinner.value as Int
            customChrome = customChromeCheckbox.isSelected
            packageManager = when (packageManagerComboBox.selectedItem as String) {
                "YARN" -> PackageManager.YARN
                "PNPM" -> PackageManager.PNPM
                else -> PackageManager.NPM
            }
        }
    }

    fun updateDataFromSettings(settings: TAVERNTauriProjectSettings) {
        projectNameTextField.text = settings.projectName
        appIdentifierTextField.text = settings.appIdentifier
        windowTitleTextField.text = settings.windowTitle
        windowWidthSpinner.value = settings.windowWidth
        windowHeightSpinner.value = settings.windowHeight
        customChromeCheckbox.isSelected = settings.customChrome
        packageManagerComboBox.selectedItem = when (settings.packageManager) {
            PackageManager.YARN -> "YARN"
            PackageManager.PNPM -> "PNPM"
            PackageManager.NPM -> "NPM"
        }
    }

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
        // Set up package manager dropdown
        packageManagerComboBox.model = DefaultComboBoxModel(arrayOf("NPM", "YARN", "PNPM"))

        // Check cargo installation status
        checkCargoInstallation()

        // Build form with FormBuilder for better layout management
        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Project Name:"), projectNameTextField.apply {
                document.addDocumentListener(object : javax.swing.event.DocumentListener {
                    private fun updateLocation() {
                        val newName: String = text.lowercase().replace(Regex("[^a-zA-Z0-9]"), "-")
                        val currentLoc: String = myLocationField.text
                        val currentName: String = currentLoc.split('\\').last().trim('\\')
                        myLocationField.text = String.format("%s%s", currentLoc.substring(0, currentLoc.length - currentName.length), newName)
                    }

                    override fun insertUpdate(e: javax.swing.event.DocumentEvent) = updateLocation()
                    override fun removeUpdate(e: javax.swing.event.DocumentEvent) = updateLocation()
                    override fun changedUpdate(e: javax.swing.event.DocumentEvent) = updateLocation()
                })
            }, 1, false)
            .addLabeledComponent(JBLabel("App Identifier:"), appIdentifierTextField, 1, false)
            .addSeparator()
            .addLabeledComponent(JBLabel("Window Title:"), windowTitleTextField, 1, false)
            .addLabeledComponent(JBLabel("Window Width:"), windowWidthSpinner, 1, false)
            .addLabeledComponent(JBLabel("Window Height:"), windowHeightSpinner, 1, false)
            .addComponent(customChromeCheckbox)
            .addSeparator()
            .addLabeledComponent(JBLabel("Package Manager:"), packageManagerComboBox, 1, false)
            .addSeparator()

        // Create a panel for the dependency header with refresh button
        panel.addComponent(JPanel(java.awt.BorderLayout()).apply {
            add(JBLabel("Rust Dependencies").apply { 
                font = font.deriveFont(font.style or java.awt.Font.BOLD) 
            }, java.awt.BorderLayout.WEST)
            add(refreshDependenciesButton, java.awt.BorderLayout.EAST)
        })

        // Add dependency status and installation options
        if (!isCargoInstalled) {
            panel.addComponent(JPanel(java.awt.FlowLayout(java.awt.FlowLayout.LEFT)).apply {
                add(JBLabel("Cargo is not installed. "))
                add(rustupLinkLabel)
            })
        } else if (!isCargoGenerateInstalled) {
            panel.addComponent(JPanel(java.awt.FlowLayout(java.awt.FlowLayout.LEFT)).apply {
                add(JBLabel("cargo-generate is not installed. "))
                add(cargoInstallButton)
            })
        } else {
            panel.addComponent(JBLabel("✓ All required Rust dependencies are installed."))
        }

        // Finish building the panel
        val finalPanel = panel
            .addComponentFillVertically(JPanel(), 0)
            .panel
            .apply {
                border = JBUI.Borders.empty(10)
            }

        return finalPanel
    }
}
