package com.github.pressstart9.pluginlabwork.services

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil


class AddTemplateFile : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        var fileName = Messages.showInputDialog(
            project,
            "",
            "New Copying Template",
            Messages.getQuestionIcon()
        )

        if (fileName == "") {
            fileName = "NewCopyingTemplate.md"
        }
        if (!fileName!!.endsWith(".md")) {
            fileName += ".md"
        }

        var selectedFolder = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        if (!selectedFolder.isDirectory) {
            selectedFolder = selectedFolder.parent
        }

        WriteCommandAction.runWriteCommandAction(project) {
            val file = selectedFolder.createChildData(this, fileName)
            VfsUtil.saveText(file, """_fileExtensions:
_skippedFileExtensions: ".kt"
_namingPatterns:
"(.*?).py" -> "%sCopy.py"
_includeFolders: true
""")
        }
    }
}
