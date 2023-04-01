package ee.carlrobert.codegpt.client;

interface ActionListener {
  default void handleComplete() {
  }

  default void handleError(String errorMessage) {
  }

  default void handleMessage(String message, String fullMessage) {
  }
}
