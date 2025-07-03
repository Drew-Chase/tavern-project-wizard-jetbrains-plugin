package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui.TAVERNTauriProjectSettingsPanel
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.LayeredIcon
import com.intellij.util.PlatformIcons
import java.io.File
import javax.swing.Icon

class TAVERNTauriDirectoryProjectGenerator : DirectoryProjectGenerator<TAVERNTauriProjectSettings> {
    override fun getName() = "TAVERN Project"

    override fun getLogo(): Icon? {
        return LayeredIcon.create(PlatformIcons.PROJECT_ICON, TAVERNPluginResource.ICON)
    }

    override fun generateProject(p0: Project, p1: VirtualFile, p2: TAVERNTauriProjectSettings, p3: Module) {

    }

    override fun createPeer(): ProjectGeneratorPeer<TAVERNTauriProjectSettings?> {
        return TAVERNTauriProjectSettingsPanel()
    }

    override fun validate(baseDirPath: String): ValidationResult {
        val baseDir = File(baseDirPath)
        if (!baseDir.exists()) {
            return ValidationResult("The specified base directory does not exist.")
        }
        if (!baseDir.isDirectory) {
            return ValidationResult("The specified base directory is not a valid directory.")
        }
        if (baseDir.listFiles()?.isEmpty() == false) {
            return ValidationResult("The specified base directory is not empty.")
        }

        return ValidationResult.OK
    }
}