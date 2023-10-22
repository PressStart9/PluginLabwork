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


class AddExistingFiles : AnAction() {
    private fun fileCopy(project: Project, i: VirtualFile, selectedFolder: VirtualFile, iName: String = i.name) : Boolean {
        return try {
            WriteCommandAction.runWriteCommandAction(project) {
                if (selectedFolder.findChild(iName) != null) {
                    throw IllegalStateException("There is already file with such name");
                }

                if (i.isDirectory) {
                    val folder: VirtualFile = selectedFolder.createChildDirectory(this, iName);
                    for (j in i.children) {
                        fileCopy(project, j, folder);
                    }
                } else {
                    VfsUtilCore.copyFile(this, i, selectedFolder, iName);
                }
            }
            true
        } catch (e: Exception) {
            false
        };
    }

    fun startCopy(project: Project, itValues: MutableList<VirtualFile>, itNames: MutableList<String>, selectedFolder: VirtualFile) {
        var i = 0;
        while (i < itValues.size) {
            if (fileCopy(project, itValues[i], selectedFolder, itNames[i])) {
                itNames.removeAt(i);
                itValues.remove(itValues[i]);
                --i;
            }
            ++i;
        }

        if (itNames.size != 0) {
            val win = FileCorrectionWindow();
            win.addRows(itNames, itValues, project, selectedFolder, ::startCopy);
            win.show();
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return;

        val application: Application = ApplicationManager.getApplication()
        application.invokeLater() {
            val descriptor = FileChooserDescriptor(true, true, false,
                false, false, true);
            descriptor.isShowFileSystemRoots = true;

            var selectedFolder: VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
            if (!selectedFolder.isDirectory) {
                selectedFolder = selectedFolder.parent;
            }

            FileChooser.chooseFiles(descriptor, project, null) { it ->
                startCopy(project, it.toMutableList(), (it.map { p -> p.name }).toMutableList(), selectedFolder);
            }
        }
    }
}
