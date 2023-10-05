package annotations

/**
 * Annotation class for PendingRemoval.
 * Use this annotation to mark elements that are planned to be removed in future versions.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class PendingRemoval

/**
 * Annotation class for QuicklyFixed.
 * Use this annotation to mark elements that were quickly fixed after a bug was discovered.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class QuicklyFixed
