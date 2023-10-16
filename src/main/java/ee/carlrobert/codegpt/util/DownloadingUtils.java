package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

public class DownloadingUtils {

  private static final int BYTES_IN_MB = 1024 * 1024;

  public static String getFormattedDownloadProgress(long startTime, long fileSize, long bytesRead) {
    long timeElapsed = System.currentTimeMillis() - startTime;

    double speed = ((double) bytesRead / timeElapsed) * 1000 / BYTES_IN_MB;
    double percent = (double) bytesRead / fileSize * 100;
    double downloadedMB = (double) bytesRead / BYTES_IN_MB;
    double totalMB = (double) fileSize / BYTES_IN_MB;
    double remainingMB = totalMB - downloadedMB;

    return format(
        "%.2fMB of %.2fMB (%.2f%%), Speed: %.2fMB/sec, Time left: %s",
        downloadedMB,
        totalMB,
        percent,
        speed,
        getTimeLeftFormattedString(speed, remainingMB));
  }

  private static String getTimeLeftFormattedString(double speed, double remainingMB) {
    double timeLeftSec = speed > 0 ? remainingMB / speed : 0;
    long hours = (long) (timeLeftSec / 3600);
    long minutes = (long) ((timeLeftSec % 3600) / 60);
    long seconds = (long) (timeLeftSec % 60);

    return format("%02d:%02d:%02d", hours, minutes, seconds);
  }
}
