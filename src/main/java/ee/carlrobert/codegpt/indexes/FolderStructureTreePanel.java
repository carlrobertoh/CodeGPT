package ee.carlrobert.codegpt.indexes;

import static java.util.stream.Collectors.toList;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.VcsIgnoreManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileSystemEntry;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.util.file.FileUtils;
import ee.carlrobert.embedding.CheckedFile;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class FolderStructureTreePanel {

  private CheckboxTree checkboxTree;
  private final AsyncProcessIcon loadingFilesSpinner;
  private final JPanel rootPanelContainer;
  private final VcsIgnoreManager ignoreManager;
  private final ChangeListManager changeListManager;
  private long totalSize = 0;

  public FolderStructureTreePanel(@NotNull Project project) {
    this.ignoreManager = VcsIgnoreManager.getInstance(project);
    this.loadingFilesSpinner = new AsyncProcessIcon("loading_files");
    this.changeListManager = ChangeListManager.getInstance(project);

    var projectDirectory = ProjectUtil.guessProjectDir(project);
    if (projectDirectory == null) {
      throw new RuntimeException("Couldn't find project directory");
    }

    var rootNode = new CheckedTreeNode(projectDirectory);
    rootPanelContainer = new JPanel(new BorderLayout());

    CompletableFuture
        .runAsync(() -> traverseDirectory(rootNode, projectDirectory))
        .thenRun(() -> SwingUtilities.invokeLater(() -> {
          checkboxTree = createCheckboxTree(rootNode, true);
          loadingFilesSpinner.setVisible(false);
          updatePanel();
        }));

    checkboxTree = createCheckboxTree(rootNode, false);

    rootPanelContainer.add(createRootPanel());
    rootPanelContainer.add(createFooterPanel(), BorderLayout.SOUTH);
  }

  private CheckboxTree createCheckboxTree(CheckedTreeNode rootNode, boolean enabled) {
    checkboxTree = new CheckboxTree(createFileTypesRenderer(), rootNode);
    checkboxTree.setEditable(enabled);
    checkboxTree.setEnabled(enabled);
    checkboxTree.addCheckboxTreeListener(new CheckboxTreeListener() {
      @Override
      public void nodeStateChanged(@NotNull CheckedTreeNode node) {
        try {
          var length = ((VirtualFileImpl) node.getUserObject()).getLength();
          if (node.isChecked()) {
            totalSize += length;
          } else {
            totalSize -= length;
          }
        } catch (Throwable ignored) {
          // ignore
        }
      }
    });
    checkboxTree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        updatePanel();
      }
    });
    return checkboxTree;
  }

  private void updatePanel() {
    rootPanelContainer.removeAll();
    rootPanelContainer.add(createRootPanel());
    rootPanelContainer.add(createFooterPanel(), BorderLayout.SOUTH);
    rootPanelContainer.revalidate();
    rootPanelContainer.repaint();
  }

  public JPanel getPanel() {
    return rootPanelContainer;
  }

  private JPanel createRootPanel() {
    var scrollPane = ScrollPaneFactory.createScrollPane(checkboxTree);
    scrollPane.setPreferredSize(JBUI.size(250, 375));
    return JBUI.Panels.simplePanel().addToCenter(scrollPane);
  }

  private JPanel createFooterPanel() {
    var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
    if (loadingFilesSpinner.isVisible()) {
      panel.add(new JBLabel("Total size:"));
      panel.add(loadingFilesSpinner);
    } else {
      panel.add(new JBLabel("Total size: "
          + FileUtils.convertFileSize(totalSize) + " ~ "
          + (convertLongValue(totalSize / 4)) + " tokens " + " ~ "
          + new DecimalFormat("#.##")
          .format(((double) (totalSize / 4) / 1000) * 0.0001) + " $"));
    }
    return panel;
  }

  public List<CheckedFile> getCheckedFiles() {
    return getCheckedVirtualFiles().stream()
        .map(item -> new CheckedFile(new File(item.getPath())))
        .collect(toList());
  }

  private List<VirtualFileImpl> getCheckedVirtualFiles() {
    return Arrays.stream(checkboxTree.getCheckedNodes(
            VirtualFileSystemEntry.class,
            node -> node instanceof VirtualFileImpl))
        .map(entry -> (VirtualFileImpl) entry)
        .collect(toList());
  }

  private void processChildFile(@NotNull CheckedTreeNode node, @NotNull VirtualFile file) {
    long fileSize = file.getLength();

    try {
      if (node.isChecked()) {
        node.setChecked(!changeListManager.isIgnoredFile(file)
            && !ignoreManager.isPotentiallyIgnoredFile(file)
            && FileUtils.isUtf8File(file.getPath())
            && fileSize < Math.pow(1024, 2));
      }

      if (node.isChecked()) {
        totalSize += fileSize;
      }
    } catch (RuntimeException ignored) {
      // ignore
    }
  }

  private void traverseDirectory(@NotNull CheckedTreeNode parentNode,
      @NotNull VirtualFile projectDirectory) {
    for (VirtualFile childFile : projectDirectory.getChildren()) {
      var node = new CheckedTreeNode(childFile);
      parentNode.add(node);

      var potentiallyIgnored = ignoredFileDirectories.parallelStream()
          .anyMatch(it -> it.equalsIgnoreCase(childFile.getName()));
      if (!parentNode.isChecked() || potentiallyIgnored) {
        node.setChecked(false);
      }

      if (childFile.isDirectory()) {
        traverseDirectory(node, childFile);
      } else {
        processChildFile(node, childFile);
      }
    }
  }

  private @NotNull CheckboxTree.CheckboxTreeCellRenderer createFileTypesRenderer() {
    return new CheckboxTree.CheckboxTreeCellRenderer() {
      @Override
      public void customizeRenderer(JTree t,
          Object value,
          boolean selected,
          boolean expanded,
          boolean leaf,
          int row,
          boolean focus) {
        if (!(value instanceof CheckedTreeNode)) {
          return;
        }

        var node = (CheckedTreeNode) value;
        var userObject = node.getUserObject();

        if (userObject instanceof VirtualFileSystemEntry) {
          getTextRenderer().append(((VirtualFileSystemEntry) userObject).getName());

          if (userObject instanceof VirtualDirectoryImpl) {
            getTextRenderer().setIcon(AllIcons.Nodes.Folder);
          } else {
            var fileType = FileTypeManager.getInstance()
                .getFileTypeByFile((VirtualFileSystemEntry) userObject);
            getTextRenderer().setIcon(fileType.getIcon());
            getTextRenderer().append(
                " - " + FileUtils.convertFileSize(
                    ((VirtualFileSystemEntry) userObject).getLength()));
          }
        }
      }
    };
  }

  private static String convertLongValue(long value) {
    if (value >= 1_000_000) {
      return value / 1_000_000 + "M";
    }
    if (value >= 1_000) {
      return value / 1_000 + "K";
    }

    return String.valueOf(value);
  }

  private static final List<String> ignoredFileDirectories = List.of(
      "node_modules",
      ".git",
      ".svn",
      ".bzr",
      ".cvs",
      ".m2",
      ".idea",
      ".vscode",
      ".project",
      ".settings",
      "node_modules",
      "vendor",
      "lib",
      "build",
      "target",
      "media",
      "logs",
      "uploads");
}
