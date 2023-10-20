package annotations

import kotlin.annotation.AnnotationTarget.*
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Annotation class for PendingRemoval.
 * Use this annotation to mark elements that are planned to be removed in future versions.
 */
@Target(CLASS, FUNCTION, PROPERTY)
@Retention(SOURCE)
annotation class PendingRemoval

/**
 * Annotation class for QuicklyFixed.
 * Use this annotation to mark elements that were quickly fixed after a bug was discovered.
 */
@Target(CLASS, FUNCTION, PROPERTY)
@Retention(SOURCE)
annotation class QuicklyFixed
