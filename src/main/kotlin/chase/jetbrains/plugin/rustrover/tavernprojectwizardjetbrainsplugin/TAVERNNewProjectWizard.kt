package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class TAVERNNewProjectWizard : GeneratorNewProjectWizard {
    override val id: @NonNls String = "TavernProjectWizard"
    override val name = "TAVERN Project"
    override val icon: Icon = TAVERNPluginResource.ICON
    override val ordinal: Int = -1000  // Changed from 0 to -1000 to give it even higher priority

    // Add a property to make the wizard more visible
    val isVisible: Boolean = true

    init {
        println("TAVERNNewProjectWizard init block")
        System.err.println("TAVERNNewProjectWizard init block")
    }

    constructor() {
        println("TAVERNNewProjectWizard constructor")
        System.err.println("TAVERNNewProjectWizard constructor")
    }

    override fun createStep(context: WizardContext): NewProjectWizardStep {
        println("TAVERNNewProjectWizard createStep")
        System.err.println("TAVERNNewProjectWizard createStep")
        return RootNewProjectWizardStep(context).nextStep { TAVERNNewProjectWizardStep(context) }
    }

    fun isAvailable(): Boolean {
        println("TAVERNNewProjectWizard isAvailable")
        System.err.println("TAVERNNewProjectWizard isAvailable")
        return true
    }

    fun isAvailableFor(context: WizardContext): Boolean {
        println("TAVERNNewProjectWizard isAvailableFor")
        System.err.println("TAVERNNewProjectWizard isAvailableFor")
        return true
    }

    fun getWeight(): Int {
        println("TAVERNNewProjectWizard getWeight")
        System.err.println("TAVERNNewProjectWizard getWeight")
        return -1000
    }

}
