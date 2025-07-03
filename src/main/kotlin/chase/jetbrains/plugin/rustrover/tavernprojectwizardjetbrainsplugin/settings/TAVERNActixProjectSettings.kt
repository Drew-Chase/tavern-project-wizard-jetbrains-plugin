package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings

class TAVERNActixProjectSettings : TAVERNProjectSettings() {
    var apiPort: Int = 8080

    override fun isValid(): Boolean {
        return super.isValid() && apiPort in 1..65535
    }
}