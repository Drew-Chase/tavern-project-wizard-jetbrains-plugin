package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings

open class TAVERNProjectSettings {
    var projectName: String = ""
    var projectDirectory: String = ""
    open fun isValid(): Boolean {
        return projectName.isNotBlank() && projectDirectory.isNotBlank()
    }
}

