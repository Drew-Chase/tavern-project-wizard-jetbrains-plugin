package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.ui.JBUI
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class TAVERNTauriProjectSettingsPanel : GeneratorPeerImpl<TAVERNTauriProjectSettings>() {
    private val panel: JPanel = JPanel(GridLayoutManager(3, 2, JBUI.emptyInsets(), -1, -1))

    init {
        // Add your UI components here
        panel.add(JLabel("Project Name:"))
    }

    override fun getComponent(): JComponent = panel
}