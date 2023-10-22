package com.github.pressstart9.pluginlabwork.windows

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class FileCorrectionWindow : DialogWrapper(false) {
    private val panel: JPanel = JPanel(GridBagLayout());
    private val gbc: GridBagConstraints = GridBagConstraints();

    private val textFields: MutableList<JBTextField> = mutableListOf();
    private var filesFields: MutableList<VirtualFile> = mutableListOf();
    private var project: Project? = null;
    private var selectedFolder: VirtualFile? = null;

    private var callBack: ((Project, MutableList<VirtualFile>, MutableList<String>, VirtualFile) -> Unit)? = null;

    init {
        init();
        title = "Names Conflict";

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        setOKButtonText("Apply");
    }

    fun addRows(fileNames: MutableList<String>, files: MutableList<VirtualFile>, proj: Project, fold: VirtualFile,
                bar: (project: Project, itValues: MutableList<VirtualFile>, itNames: MutableList<String>, selectedFolder: VirtualFile) -> Unit) {
        for (i in fileNames) {
            panel.add(JLabel(i), gbc);
            ++gbc.gridx;
            panel.add(JLabel(" -> "), gbc);
            ++gbc.gridx;
            textFields.add(JBTextField());
            panel.add(textFields[textFields.size - 1], gbc);
            ++gbc.gridy;
            --gbc.gridx;
            --gbc.gridx;
        }
        filesFields = files;
        callBack = bar;
        project = proj;
        selectedFolder = fold;
    }

    override fun createCenterPanel(): JComponent {
        return panel;
    }

    override fun doOKAction() {
        super.doOKAction();
        callBack!!(project!!, filesFields, textFields.map { p -> p.text }.toMutableList(), selectedFolder!!);
    }
}