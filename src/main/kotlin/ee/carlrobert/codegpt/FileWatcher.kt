package ee.carlrobert.codegpt

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import kotlin.concurrent.thread
import kotlin.io.path.exists


@Service(Service.Level.PROJECT)
class FileWatcher : Disposable {

    private var fileMonitor: Thread? = null

    fun watch(pathToWatch: Path, onFileCreated: (Path) -> Unit) {
        fileMonitor = pathToWatch.takeIf { it.exists() }?.let {
            try {
                FileSystems.getDefault().newWatchService().also {
                    pathToWatch.register(it, ENTRY_CREATE) // watch for new files
                }
            } catch (e: Exception) {
                null // WatchService or registration failed
            }
        }?.let { watchService ->
            thread {
                try {
                    generateSequence { watchService.take() }.forEach { key ->
                        key.pollEvents().forEach { onFileCreated(it.context() as Path) }
                        key.reset()
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    override fun dispose() {
        fileMonitor?.interrupt()
    }
}
