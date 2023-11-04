package com.github.pressstart9.pluginlabwork.services

import com.github.pressstart9.pluginlabwork.windows.FileCorrectionWindow
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import java.io.BufferedReader
import java.io.InputStreamReader


class AddExistingFiles : AnAction() {
    private var project: Project? = null

    private var fileExtensions: MutableList<String> = mutableListOf()
    private var skippedFileExtensions: MutableList<String> = mutableListOf()
    private var namingPatterns: MutableList<Pair<Regex, String>> = mutableListOf()
    private var includeFolders: Boolean = true

    private fun transformName(title: String): String {
        var newTitle: String = title
        thisLogger().warn(title)
        for (trans in namingPatterns) {
            val groups = trans.first.find(title)
            if (groups != null) {
                newTitle = trans.second.format(*groups.groupValues.subList(1, groups.groupValues.size).toTypedArray())
                break;
            }
        }

        return newTitle
    }

    private fun fileCopy(i: VirtualFile, selectedFolder: VirtualFile, iName: String = i.name) : Boolean {
        return try {
            WriteCommandAction.runWriteCommandAction(project) {
                if (selectedFolder.findChild(iName) != null) {
                    throw IllegalStateException("There is already file with such name")
                }

                if (i.isDirectory) {
                    val folder: VirtualFile = selectedFolder.createChildDirectory(this, iName)
                    for (j in i.children) {
                        fileCopy(j, folder)
                    }
                } else {
                    VfsUtilCore.copyFile(this, i, selectedFolder, iName)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun startCopy(itValues: MutableList<VirtualFile>, itNames: MutableList<String>, selectedFolder: VirtualFile) {
        var i = 0
        while (i < itValues.size) {
            if (fileCopy(itValues[i], selectedFolder, itNames[i])) {
                itNames.removeAt(i)
                itValues.remove(itValues[i])
                --i
            }
            ++i
        }

        if (itNames.size != 0) {
            val win = FileCorrectionWindow()
            win.addRows(itNames, itValues, selectedFolder, ::startCopy)
            win.show()
        }
    }

    private fun chooseFiles(selectedFolder: VirtualFile) {
        val application: Application = ApplicationManager.getApplication()
        application.invokeLater {
            val descriptor = object: FileChooserDescriptor(true, includeFolders, false,
                false, false, true) {
                override fun getDescription() = "Select a files for copying into this project"
                override fun getTitle() = "Select Copying Files"
            }
            descriptor.isShowFileSystemRoots = true
            descriptor.withFileFilter {
                (fileExtensions.size == 0 || fileExtensions.any { ext -> it.name.endsWith(ext) })
                        && skippedFileExtensions.all { ext -> !it.name.endsWith(ext) }
            }

            var sf = selectedFolder
            if (!selectedFolder.isDirectory) {
                sf = selectedFolder.parent
            }

            FileChooser.chooseFiles(descriptor, project, null) {
                startCopy(it.toMutableList(), (it.map { p -> transformName(p.name) }).toMutableList(), sf)
            }

            fileExtensions.clear()
            skippedFileExtensions.clear()
            namingPatterns.clear()
            includeFolders = true
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project ?: return

        val application: Application = ApplicationManager.getApplication()
        application.invokeLater {
            val descriptor = object: FileChooserDescriptor(true, false, false,
                false, false, false) {
                override fun getDescription() = "Select a template file for copying with \".md\" extension"
                override fun getTitle() = "Select Template"
            }
            descriptor.isShowFileSystemRoots = true
            descriptor.withFileFilter { it.name.endsWith(".md")}

            val sFile = FileChooser.chooseFile(descriptor, project, null)

            if (sFile != null) {
                val inputStream = sFile.inputStream
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String
                    line = reader.readLine().substringAfter(": ")
                    for (ext in line.split(", ")) {
                        if (ext[0] == '"' && ext[ext.length - 1] == '"') {
                            fileExtensions.add(ext.substring(1, ext.length - 1))
                        }
                    }

                    line = reader.readLine().substringAfter(": ")
                    for (ext in line.split(", ")) {
                        if (ext[0] == '"' && ext[ext.length - 1] == '"') {
                            skippedFileExtensions.add(ext.substring(1, ext.length - 1))
                        }
                    }

                    reader.readLine()
                    line = reader.readLine()
                    while (line[0] != '_') {
                        val fromTo = line.split("\" -> \"")
                        namingPatterns.add(
                            Pair(
                                fromTo[0].substring(1).toRegex(),
                                fromTo[1].substring(0, fromTo[1].length - 1)
                            )
                        )
                        line = reader.readLine()
                    }

                    if (line.substringAfter(": ")[0] != 't') {
                        includeFolders = false
                    }
                }
            }

            chooseFiles(e.getRequiredData(CommonDataKeys.VIRTUAL_FILE))
        }
    }
}
