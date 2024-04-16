package ee.carlrobert.codegpt.util

import ee.carlrobert.codegpt.util.file.FileUtil.convertFileSize

object DownloadingUtil {
  private const val BYTES_IN_MB = 1024 * 1024

  fun getFormattedDownloadProgress(
    fileNumber: Int, fileCount: Int, startTime: Long,
    fileSize: Long, bytesRead: Long
  ): String {
    val timeElapsed = System.currentTimeMillis() - startTime

    val speed = (bytesRead.toDouble() / timeElapsed) * 1000 / BYTES_IN_MB
    val percent = bytesRead.toDouble() / fileSize * 100
    val downloadedMB = bytesRead.toDouble() / BYTES_IN_MB
    val totalMB = fileSize.toDouble() / BYTES_IN_MB
    val remainingMB = totalMB - downloadedMB

    return String.format(
      "File %d/%d: %s of %s (%.2f%%), Speed: %.2f MB/sec, Time left: %s",
      fileNumber,
      fileCount,
      convertFileSize(downloadedMB.toLong() * BYTES_IN_MB),
      convertFileSize(totalMB.toLong() * BYTES_IN_MB),
      percent,
      speed,
      getTimeLeftFormattedString(speed, remainingMB)
    )
  }

  private fun getTimeLeftFormattedString(speed: Double, remainingMB: Double): String {
    val timeLeftSec = if (speed > 0) remainingMB / speed else 0.0
    val hours = (timeLeftSec / 3600).toLong()
    val minutes = ((timeLeftSec % 3600) / 60).toLong()
    val seconds = (timeLeftSec % 60).toLong()

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
  }
}
