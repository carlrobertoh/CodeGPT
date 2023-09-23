package ee.carlrobert.codegpt.util;

import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.editor.ChainDiffVirtualFile;
import com.intellij.diff.impl.CacheDiffRequestChainProcessor;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;


public class DiffUtils {


    public static final String DIFF_TITLE = "CodeGPT Diff";

    public static final String DIFF_ID = "CodeGPT_Diff_ID";

    public static Key<String> DIFF_FILENAME = Key.create("Diff.DIFF_FILENAME");

    public static Key<String> DIFF_FILEPATH_LEFT = Key.create("Diff.DIFF_FILEPATH_LEFT");

    public static Key<String> DIFF_FILEPATH_RIGHT = Key.create("Diff.DIFF_FILEPATH_RIGHT");

    public static Key<String> DIFF_FILE_UNIQUE_ID = Key.create("Diff.DIFF_FILE_UNIQUE_ID");

    public static String TEMP_DIR_NAME = "DiffCodeGPT";

    public static final String LEFT_TITLE = "(CodeGPT Suggested Code)";

    public static final String RIGHT_TITLE = "(Original)";

    private static final String EDITOR_FILE_NAME = "Diff";

    public static void closeDiffViewIfAlreadyOpened(@NotNull Project project) {
        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        for (EditorComposite editor : manager.getCurrentWindow().getAllComposites()) {
            try {
                if (EDITOR_FILE_NAME.equals(editor.getFile().getName()) && editor.getFile() instanceof ChainDiffVirtualFile) {
                    SimpleDiffRequest simpleDiffRequest = (SimpleDiffRequest) ((SimpleDiffRequestChain.DiffRequestProducerWrapper) ((CacheDiffRequestChainProcessor) ((ChainDiffVirtualFile) editor.getFile()).createProcessor(project)).getRequestChain().getRequests().get(0)).getRequest();
                    if (DIFF_ID.equals(simpleDiffRequest.getUserData(DiffUtils.DIFF_FILE_UNIQUE_ID))) {
                        editor.dispose();
                        break;
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }
}
