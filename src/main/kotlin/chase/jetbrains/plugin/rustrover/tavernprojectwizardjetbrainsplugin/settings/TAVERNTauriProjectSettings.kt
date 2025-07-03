package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings

class TAVERNTauriProjectSettings : TAVERNProjectSettings() {
    var appIdentifier: String = "com.tauri.app"
    var windowTitle: String = "TAVERN App"
    var windowWidth: Int = 800
    var windowHeight: Int = 600
    var customChrome: Boolean = false
    var packageManager: PackageManager = PackageManager.NPM

    enum class PackageManager { NPM, YARN, PNPM }

    override fun isValid(): Boolean {
        return super.isValid() &&
                appIdentifier.isNotBlank() &&
                windowTitle.isNotBlank() &&
                windowWidth > 0 &&
                windowHeight > 0 &&
                (packageManager == PackageManager.NPM || packageManager == PackageManager.YARN || packageManager == PackageManager.PNPM)
    }
}