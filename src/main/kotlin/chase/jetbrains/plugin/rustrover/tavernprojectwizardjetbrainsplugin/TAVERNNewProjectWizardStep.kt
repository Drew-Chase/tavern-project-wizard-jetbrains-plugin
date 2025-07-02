package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolder
import com.intellij.ui.dsl.builder.Panel

class TAVERNNewProjectWizardStep(
    override val context: WizardContext
) : NewProjectWizardStep {
    override val propertyGraph: PropertyGraph = PropertyGraph()
    override val keywords: NewProjectWizardStep.Keywords = NewProjectWizardStep.Keywords()
    override val data: UserDataHolder = context

    override fun setupUI(builder: Panel) {
        super.setupUI(builder)
    }

    override fun setupProject(project: Project) {
        super.setupProject(project)
    }
}