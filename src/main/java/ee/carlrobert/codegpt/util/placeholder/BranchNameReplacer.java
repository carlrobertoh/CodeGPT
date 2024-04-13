package ee.carlrobert.codegpt.util.placeholder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;

public class BranchNameReplacer implements PlaceholderReplacer {

  @Override
  public String getPlaceholderDescription() {
    return "The name of the current branch";
  }

  @Override
  public String getPlaceholderKey() {
    return "BRANCH_NAME";
  }

  @Override
  public String getReplacementValue() {
    try {
      Project currentProject = ProjectManager.getInstance().getOpenProjects()[0];
      var repositories = GitUtil.getRepositoryManager(currentProject).getRepositories();
      if (repositories.isEmpty()) {
        return "BRANCH-UNKNOWN";
      }

      GitRepository repository = repositories.get(0);
      String currentBranch = GitBranchUtil.getBranchNameOrRev(repository);

      return currentBranch;
    } catch (Exception ignore) {
      // ignore this, as it can happen during testing and other areas
    }

    return "BRANCH-UNKNOWN";
  }
}