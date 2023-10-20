package annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Retention(RUNTIME)
@Target(CLASS, FUNCTION)
annotation class Preview

@Retention(RUNTIME)
@Target(CLASS, FUNCTION)
annotation class ExperimentalState