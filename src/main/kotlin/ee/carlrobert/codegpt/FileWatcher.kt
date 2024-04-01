package ee.carlrobert.codegpt

import com.intellij.openapi.Disposable
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import java.io.File
import java.nio.file.Path

class FileWatcher(private val pathToWatch: Path) : Disposable {

    private val fileMonitor =
        FileAlterationMonitor(500, FileAlterationObserver(pathToWatch.toFile()))

    fun watch(onFileCreated: (File) -> Unit) {
        val observer = FileAlterationObserver(pathToWatch.toFile())
        observer.addListener(object : FileAlterationListenerAdaptor() {
            override fun onFileCreate(file: File) {
                onFileCreated(file)
            }
        })
        fileMonitor.addObserver(observer)
        fileMonitor.start()
    }

    override fun dispose() {
        fileMonitor.stop()
    }
}