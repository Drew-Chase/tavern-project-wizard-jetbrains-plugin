package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNActixProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.components.JBTabbedPane
import javax.swing.JComponent
import javax.swing.JTabbedPane

class TAVERNProjectSettingsPanel : GeneratorPeerImpl<TAVERNTauriProjectSettings>() {

    private val tabbedPane = JBTabbedPane(JTabbedPane.TOP)
    private val tauriPanel = TAVERNTauriProjectSettingsPanel()
    private val actixPanel = TAVERNActixProjectSettingsPanel()

    // Track the currently selected tab
    private var selectedTabIndex = 0

    // Expose the selected project type
    val isActixSelected: Boolean
        get() = selectedTabIndex == 1

    // Expose the actix settings
    val actixSettings: TAVERNActixProjectSettings
        get() = actixPanel.settings

    override fun validate(): ValidationInfo? {
        // Validate based on the selected tab
        return when (selectedTabIndex) {
            0 -> tauriPanel.validate()
            1 -> actixPanel.validate()
            else -> null
        }
    }

    override fun getSettings(): TAVERNTauriProjectSettings {
        // Return settings based on the selected tab
        return when (selectedTabIndex) {
            0 -> tauriPanel.settings
            else -> TAVERNTauriProjectSettings() // Default to Tauri settings
        }
    }

    fun updateDataFromSettings(settings: TAVERNTauriProjectSettings) {
        tauriPanel.updateDataFromSettings(settings)
        selectedTabIndex = 0
        tabbedPane.selectedIndex = 0
    }

    // Additional method to handle Actix settings
    fun updateDataFromActixSettings(settings: TAVERNActixProjectSettings) {
        actixPanel.updateDataFromSettings(settings)
        selectedTabIndex = 1
        tabbedPane.selectedIndex = 1
    }

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
        // Create the tabbed pane with Tauri and Actix panels
        val tauriComponent = tauriPanel.getComponent(myLocationField, checkValid)
        val actixComponent = actixPanel.getComponent(myLocationField, checkValid)

        tabbedPane.addTab("Tauri", tauriComponent)
        tabbedPane.addTab("Actix", actixComponent)

        // Add a listener to track the selected tab
        tabbedPane.addChangeListener { 
            selectedTabIndex = tabbedPane.selectedIndex
            checkValid.run()
        }

        return tabbedPane
    }
}
