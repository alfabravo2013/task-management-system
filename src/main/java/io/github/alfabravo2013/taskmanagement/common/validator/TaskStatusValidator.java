package io.github.alfabravo2013.taskmanagement.common.validator;

import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskStatusValidator implements ConstraintValidator<TaskStatuses, TaskStatus> {
    @Override
    public boolean isValid(TaskStatus value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        for (var constant : TaskStatus.values()) {
            if (constant == value) {
                return true;
            }
        }
        return false;
    }
}
