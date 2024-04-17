package ee.carlrobert.codegpt

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import java.io.File

@Service(Service.Level.PROJECT)
class FileWatcher : Disposable {

    private var fileMonitor: FileAlterationMonitor? = null

    fun watch(pathToWatch: File, onFileCreated: (File) -> Unit) {
        val observer = FileAlterationObserver(pathToWatch)
        observer.addListener(object : FileAlterationListenerAdaptor() {
            override fun onFileCreate(file: File) {
                onFileCreated(file)
            }
        })
        fileMonitor = FileAlterationMonitor(500, observer)
        fileMonitor?.start()
    }

    override fun dispose() {
        fileMonitor?.stop()
    }
}