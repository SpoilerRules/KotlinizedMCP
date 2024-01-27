package annotations

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

/**
 * Annotation class for PendingRemoval.
 * Use this annotation to mark elements that are planned to be removed in future versions.
 * @property reason Optional reason for pending removal.
 */
@Target(CLASS, FUNCTION, PROPERTY)
@Retention(BINARY)
annotation class PendingRemoval(val reason: String = "")

/**
 * Annotation class for QuicklyFixed.
 * Use this annotation to mark elements that were quickly fixed after a bug was discovered.
 */
@Target(CLASS, FUNCTION, PROPERTY)
@Retention(BINARY)
annotation class QuicklyFixed