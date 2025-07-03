package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings.PackageManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
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
            .addComponentFillVertically(JPanel(), 0)
            .panel
            .apply {
                border = JBUI.Borders.empty(10)
            }
        return panel
    }
}