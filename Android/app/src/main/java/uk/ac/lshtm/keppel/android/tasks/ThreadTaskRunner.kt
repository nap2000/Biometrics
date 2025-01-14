package uk.ac.lshtm.keppel.android.tasks

import uk.ac.lshtm.keppel.android.core.TaskRunner

class ThreadTaskRunner : TaskRunner {

    override fun execute(runnable: () -> Unit) {
        Thread(runnable).start()
    }
}