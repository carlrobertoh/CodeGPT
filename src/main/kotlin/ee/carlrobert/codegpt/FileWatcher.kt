package ee.carlrobert.codegpt

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.WatchKey
import kotlin.concurrent.thread


@Service(Service.Level.PROJECT)
class FileWatcher : Disposable {

    private var fileMonitor: Thread? = null

    fun watch(pathToWatch: Path, onFileCreated: (Path) -> Unit) {
        val watchService = FileSystems.getDefault().newWatchService()
        pathToWatch.register(watchService, ENTRY_CREATE) // watch for new files
        fileMonitor = thread {
            var key: WatchKey
            while ((watchService.take().also { key = it }) != null) {
                for (event in key.pollEvents()) {
                    onFileCreated(event.context() as Path)
                }
                key.reset()
            }
        }
    }

    override fun dispose() {
        fileMonitor?.interrupt()
    }
}
