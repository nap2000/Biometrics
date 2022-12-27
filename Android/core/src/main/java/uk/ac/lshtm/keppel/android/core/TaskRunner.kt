package uk.ac.lshtm.keppel.android.core

interface TaskRunner {

    fun execute(runnable: () -> Unit)
}