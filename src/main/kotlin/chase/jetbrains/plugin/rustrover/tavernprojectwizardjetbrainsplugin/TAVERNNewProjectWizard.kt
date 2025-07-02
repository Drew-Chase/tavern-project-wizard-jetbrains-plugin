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
    override val ordinal: Int = 1000
    init {
        println("TAVERNNewProjectWizard init")
    }
    override fun createStep(context: WizardContext): NewProjectWizardStep {
        println("TAVERNNewProjectWizard createStep")
        return RootNewProjectWizardStep(context).nextStep { TAVERNNewProjectWizardStep(context) }
    }

}