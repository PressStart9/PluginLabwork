# Intellij IDEA plugin. Laboratory work №3.
> [!IMPORTANT]
> This work was made by Postnikov Andrey (ISU id: 409391) from group M3101

This plugin was created to simplify simultaneous copying of multiple files from one project to another.
Plugin supports copying with rules from custom template, which you can create also using this plugin.

## Usage

Let's look at the work of the plugin using the example of a python project with several files.

![New project with several files](/images/EmptyProject.png)

If we use right mouse button on any file, we will see another item in the end Copy/Paste block: "Add existing files".

![Add existing files button](/images/AddButton.png)

When we press this button, dialog window for choosing template of copying appears.

![Template choosing window](/images/TemplateWindow.png)

If you want to copy with specific rules you can use [***templates***](TEMPLATE.md). But for simple copying you can press "Cancel" button.

After this appears another dialog window. It is needed for choosing copied files.

![Files choosing window](/images/FilesWindow.png)

We choose files, but some of them have same names as we already have in project. Plugin copy all files, which have no naming conflicts and show dialog window to resolve these conflicts.

![Naming conflicts window](/images/ConflictsWindow.png)

When edited name is free, plugin add file with such name to project.

## Commit history
```

```