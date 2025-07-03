package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNActixProjectSettings
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import javax.swing.*

class TAVERNActixProjectSettingsPanel : GeneratorPeerImpl<TAVERNActixProjectSettings>() {

    // Basic settings
    private val projectNameTextField = JBTextField()
    
    // API settings
    private val apiPortSpinner = JSpinner(SpinnerNumberModel(8080, 1, 65535, 1))

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
            .addComponentFillVertically(JPanel(), 0)
            .panel
            .apply {
                border = JBUI.Borders.empty(10)
            }
        return panel
    }
}