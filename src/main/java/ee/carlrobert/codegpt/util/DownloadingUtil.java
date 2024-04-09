package ee.carlrobert.codegpt.util;

import static java.lang.String.format;

import ee.carlrobert.codegpt.util.file.FileUtil;

public class DownloadingUtil {

  private DownloadingUtil() {
  }

  private static final int BYTES_IN_MB = 1024 * 1024;

  public static String getFormattedDownloadProgress(long startTime, long fileSize, long bytesRead) {
    long timeElapsed = System.currentTimeMillis() - startTime;

    double speed = ((double) bytesRead / timeElapsed) * 1000 / BYTES_IN_MB;
    double percent = (double) bytesRead / fileSize * 100;
    double downloadedMB = (double) bytesRead / BYTES_IN_MB;
    double totalMB = (double) fileSize / BYTES_IN_MB;
    double remainingMB = totalMB - downloadedMB;

    return format(
        "%s of %s (%.2f%%), Speed: %.2f MB/sec, Time left: %s",
        FileUtil.convertFileSize((long) downloadedMB * BYTES_IN_MB),
        FileUtil.convertFileSize((long) totalMB * BYTES_IN_MB),
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
