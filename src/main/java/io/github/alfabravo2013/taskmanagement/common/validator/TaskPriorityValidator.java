package io.github.alfabravo2013.taskmanagement.common.validator;

import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskPriorityValidator implements ConstraintValidator<TaskPriorities, TaskPriority> {
    @Override
    public boolean isValid(TaskPriority value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        for (var constant : TaskPriority.values()) {
            if (constant == value) {
                return true;
            }
        }
        return false;
    }
}
