package com.github.pressstart9.pluginlabwork.windows

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class FileCorrectionWindow : DialogWrapper(false) {
    private val panel: JPanel = JPanel(GridBagLayout())
    private val gbc: GridBagConstraints = GridBagConstraints()

    private val textFields: MutableList<JBTextField> = mutableListOf()
    private var filesFields: MutableList<VirtualFile> = mutableListOf()
    private var selectedFolder: VirtualFile? = null

    private var callBack: ((MutableList<VirtualFile>, MutableList<String>, VirtualFile) -> Unit)? = null

    init {
        init()
        title = "Names Conflict"

        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.gridy = 0

        setOKButtonText("Apply")
    }

    fun addRows(fileNames: MutableList<String>, files: MutableList<VirtualFile>, fold: VirtualFile,
                bar: (itValues: MutableList<VirtualFile>, itNames: MutableList<String>, selectedFolder: VirtualFile) -> Unit) {
        for (i in fileNames) {
            textFields.add(JBTextField(i))
            panel.add(textFields[textFields.size - 1], gbc)
            ++gbc.gridy
        }
        filesFields = files
        callBack = bar
        selectedFolder = fold
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    override fun doOKAction() {
        super.doOKAction()
        callBack!!(filesFields, textFields.map { p -> p.text }.toMutableList(), selectedFolder!!)
    }
}