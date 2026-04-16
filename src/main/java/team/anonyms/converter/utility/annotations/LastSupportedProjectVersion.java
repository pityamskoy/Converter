package team.anonyms.converter.utility.annotations;

import team.anonyms.converter.utility.enums.ProjectVersion;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * <p>
 *     {@code LastSupportedProjectVersion} is used to document what the last project version a target supports.
 * </p>
 * <p>
 *     It is supposed to be used in seldom cases when a target totally relies on a project environment. <br>
 *     For example, it is reasonable to point last supported project version when meaningfulness
 *     of usage a target depends on configuration of the project.
 * </p>
 * <p>
 *     Overall, it is highly recommended to use this annotation only in necessary cases, because
 *     all targets annotated with this annotation should have new values after they are updated.
 * </p>
 */
@Documented
@Target(value = {TYPE, METHOD, CONSTRUCTOR, ANNOTATION_TYPE})
@Retention(SOURCE)
public @interface LastSupportedProjectVersion {
    ProjectVersion value();
}
