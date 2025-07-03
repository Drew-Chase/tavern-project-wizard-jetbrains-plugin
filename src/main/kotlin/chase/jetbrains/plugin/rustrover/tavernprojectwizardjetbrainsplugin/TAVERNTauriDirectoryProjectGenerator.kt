package chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin

import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.TAVERNTauriProjectSettings
import chase.jetbrains.plugin.rustrover.tavernprojectwizardjetbrainsplugin.settings.ui.TAVERNProjectSettingsPanel
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.LayeredIcon
import com.intellij.util.PlatformIcons
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.Icon

class TAVERNTauriDirectoryProjectGenerator : DirectoryProjectGenerator<TAVERNTauriProjectSettings> {
    private val LOG = Logger.getInstance(TAVERNTauriDirectoryProjectGenerator::class.java)
    private var projectSettingsPanel: TAVERNProjectSettingsPanel? = null

    /**
     * Checks if a command is available by running it and checking the exit code
     */
    private fun isCommandAvailable(vararg command: String): Boolean {
        return try {
            val process = ProcessBuilder(*command)
                .redirectErrorStream(true)
                .start()
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    override fun getName() = "TAVERN Project"

    override fun getLogo(): Icon? {
        return LayeredIcon.create(PlatformIcons.PROJECT_ICON, TAVERNPluginResource.ICON)
    }

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: TAVERNTauriProjectSettings, module: Module) {
        // Determine which template to use based on the selected tab
        val isActixSelected = projectSettingsPanel?.isActixSelected ?: false
        val templateType = if (isActixSelected) "actix" else "tauri"

        // Run the cargo generate command in a background task
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Generating TAVERN Project", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Generating TAVERN $templateType project..."
                indicator.isIndeterminate = true

                try {
                    // Check if cargo and cargo-generate are installed
                    if (!isCommandAvailable("cargo", "--version")) {
                        ApplicationManager.getApplication().invokeLater {
                            Messages.showErrorDialog(
                                "Cargo is not installed. Please install Rust and Cargo from https://rustup.rs/ and try again.",
                                "Cargo Not Found"
                            )
                        }
                        LOG.error("Cargo is not installed")
                        return
                    }

                    if (!isCommandAvailable("cargo", "generate", "--version")) {
                        ApplicationManager.getApplication().invokeLater {
                            Messages.showErrorDialog(
                                "cargo-generate is not installed. Please install it using 'cargo install cargo-generate' and try again.",
                                "cargo-generate Not Found"
                            )
                        }
                        LOG.error("cargo-generate is not installed")
                        return
                    }

                    // Create the project directory if it doesn't exist
                    val projectDir = File(baseDir.path)
                    if (!projectDir.exists()) {
                        projectDir.mkdirs()
                    }

                    // Build the command with template-specific parameters
                    val commandList = mutableListOf(
                        "cargo", "generate", 
                        "Drew-Chase/tavern-cargo-template", 
                        templateType, 
                        "--init", 
                        "-o",
                        "--name", project.name,
                    )

                    // Add template-specific parameters
                    if (isActixSelected) {
                        // For actix template, add the API port parameter
                        val apiPort = projectSettingsPanel?.actixSettings?.apiPort ?: 8080
                        commandList.add("-d")
                        commandList.add("api_port=$apiPort")
                    } else {
                        // For tauri template, add all the required parameters
                        val appIdentifier = settings.appIdentifier
                        val windowTitle = settings.windowTitle
                        val windowWidth = settings.windowWidth
                        val windowHeight = settings.windowHeight
                        val customChrome = settings.customChrome
                        val packageManager = settings.packageManager.toString().lowercase()

                        commandList.add("-d")
                        commandList.add("app_identifier=$appIdentifier")
                        commandList.add("-d")
                        commandList.add("window_title=$windowTitle")
                        commandList.add("-d")
                        commandList.add("window_width=$windowWidth")
                        commandList.add("-d")
                        commandList.add("window_height=$windowHeight")
                        commandList.add("-d")
                        commandList.add("custom_chrome=$customChrome")
                        commandList.add("-d")
                        commandList.add("package_manager=$packageManager")
                    }

                    val command = commandList

                    // Run the command in the project directory
                    val processBuilder = ProcessBuilder(command)
                    processBuilder.directory(projectDir)
                    processBuilder.redirectErrorStream(true)

                    val process = processBuilder.start()

                    // Read the output
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    val output = StringBuilder()

                    while (reader.readLine().also { line = it } != null) {
                        output.append(line).append("\n")
                        indicator.text = "Generating TAVERN $templateType project: $line"
                    }

                    val exitCode = process.waitFor()

                    if (exitCode != 0) {
                        // Show error message if the command failed
                        ApplicationManager.getApplication().invokeLater {
                            Messages.showErrorDialog(
                                "Failed to generate TAVERN $templateType project. Exit code: $exitCode\n\n$output",
                                "Project Generation Failed"
                            )
                        }
                        LOG.error("Failed to generate TAVERN $templateType project. Exit code: $exitCode\nOutput: $output")
                    } else {
                        LOG.info("Successfully generated TAVERN $templateType project")
                    }
                } catch (e: Exception) {
                    // Show error message if an exception occurred
                    ApplicationManager.getApplication().invokeLater {
                        Messages.showErrorDialog(
                            "An error occurred while generating TAVERN $templateType project: ${e.message}",
                            "Project Generation Error"
                        )
                    }
                    LOG.error("Error generating TAVERN $templateType project", e)
                }
            }
        })
    }

    override fun createPeer(): ProjectGeneratorPeer<TAVERNTauriProjectSettings?> {
        return TAVERNProjectSettingsPanel().also { projectSettingsPanel = it }
    }

    override fun validate(baseDirPath: String): ValidationResult {
//        val baseDir = File(baseDirPath)
//        if (!baseDir.exists()) {
//            return ValidationResult("The specified base directory does not exist.")
//        }
//        if (!baseDir.isDirectory) {
//            return ValidationResult("The specified base directory is not a valid directory.")
//        }
//        if (baseDir.listFiles()?.isEmpty() == false) {
//            return ValidationResult("The specified base directory is not empty.")
//        }

        return ValidationResult.OK
    }
}
