package com.github.pressstart9.pluginlabwork.services

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile


class AddExistingFiles : AnAction() {
    fun fileCopy(project: Project, i: VirtualFile, selectedFolder: VirtualFile) {
        WriteCommandAction.runWriteCommandAction(project) {
            if (i.isDirectory) {
                val folder: VirtualFile = selectedFolder.createChildDirectory(this, i.name);
                for (j in i.children) {
                    fileCopy(project, j, folder);
                }
            } else {
                VfsUtilCore.copyFile(this, i, selectedFolder);
            }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return;

        val application: Application = ApplicationManager.getApplication()
        application.invokeLater() {
            val descriptor = FileChooserDescriptor(true, true, false, false, false, true);
            descriptor.isShowFileSystemRoots = true;

            var selectedFolder: VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
            if (!selectedFolder.isDirectory) {
                selectedFolder = selectedFolder.parent;
            }

            FileChooser.chooseFiles(descriptor, project, null) {
                for (i in it) {
                    fileCopy(project, i, selectedFolder)
                }

                
            }
        }
    }
}
