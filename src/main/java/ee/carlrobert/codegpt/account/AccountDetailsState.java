package ee.carlrobert.codegpt.account;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.account.AccountDetailsState",
    storages = @Storage("CodeGPTAccountDetails.xml")
)
public class AccountDetailsState implements PersistentStateComponent<AccountDetailsState> {

  public String accountName = "User";
  public Double totalAmountGranted;
  public Double totalAmountUsed;

  public static AccountDetailsState getInstance() {
    return ApplicationManager.getApplication().getService(AccountDetailsState.class);
  }

  @Nullable
  @Override
  public AccountDetailsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AccountDetailsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
