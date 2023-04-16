package ee.carlrobert.codegpt.client;

import ee.carlrobert.openai.client.completion.ErrorDetails;

interface ActionListener {
  default void handleComplete() {
  }

  default void handleError(ErrorDetails error) {
  }

  default void handleMessage(String message, String fullMessage) {
  }

  default void handleTokensExceeded() {
  }
}
