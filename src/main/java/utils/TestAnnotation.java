package wp.utils;

import java.lang.annotation.Documented;

/**
 * Annotation test class javadoc comments.
 * @author Jeff
 *
 */
@Documented
public @interface TestAnnotation {

	String worstCase () default "";
}
