package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNActixProjectSettings
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.*

class TAVERNActixProjectSettingsPanel : GeneratorPeerImpl<TAVERNActixProjectSettings>() {

    // Basic settings
    private val projectNameTextField = JBTextField()

    // API settings
    private val apiPortSpinner = JSpinner(SpinnerNumberModel(8080, 1, 65535, 1))

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

        return null
    }

    override fun getSettings(): TAVERNActixProjectSettings {
        return TAVERNActixProjectSettings().apply {
            projectName = projectNameTextField.text
            apiPort = apiPortSpinner.value as Int
        }
    }

    fun updateDataFromSettings(settings: TAVERNActixProjectSettings) {
        projectNameTextField.text = settings.projectName
        apiPortSpinner.value = settings.apiPort
    }

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
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
            .addSeparator()
            .addLabeledComponent(JBLabel("API Port:"), apiPortSpinner, 1, false)
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
