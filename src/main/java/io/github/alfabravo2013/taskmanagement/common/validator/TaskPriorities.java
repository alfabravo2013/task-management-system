package io.github.alfabravo2013.taskmanagement.common.validator;

import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TaskPriorityValidator.class)
public @interface TaskPriorities {
    String message() default "Значение приоритета задачи должно быть одним из допустимых";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    TaskPriority[] value() default {};
}
