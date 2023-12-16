package io.github.alfabravo2013.taskmanagement.task.repository;

import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
            SELECT
                t.id AS taskid,
                t.title AS tasktitle,
                t.description AS taskdescription,
                t.status AS taskstatus,
                t.priority AS taskpriority,
                t.author_id AS taskauthorid,
                t.assignee_id AS taskassigneeid,
                count (c.id) AS taskcommentscount
            FROM tasks AS t
            LEFT JOIN comments AS c ON c.task_id = t.id
            GROUP BY taskid, tasktitle, taskdescription, taskauthorid, taskstatus,
                     taskpriority, taskassigneeid
            """, nativeQuery = true)
    Page<TaskView> findAllTaskViews(Pageable pageable);

    @Query(value = """
            SELECT
                t.id AS taskid,
                t.title AS tasktitle,
                t.description AS taskdescription,
                t.status AS taskstatus,
                t.priority AS taskpriority,
                t.author_id AS taskauthorid,
                t.assignee_id AS taskassigneeid,
                count (c.id) AS taskcommentscount
            FROM tasks AS t
            LEFT JOIN comments AS c ON c.task_id = t.id
            WHERE t.author_id = :authorId
            GROUP BY taskid, tasktitle, taskdescription, taskauthorid, taskstatus,
                     taskpriority, taskassigneeid, t.created_at
            """, nativeQuery = true)
    Page<TaskView> findTaskViewsByAuthorId(Long authorId, Pageable pageable);

    @Query(value = """
            SELECT
                t.id AS taskid,
                t.title AS tasktitle,
                t.description AS taskdescription,
                t.status AS taskstatus,
                t.priority AS taskpriority,
                t.author_id AS taskauthorid,
                t.assignee_id AS taskassigneeid,
                count (c.id) AS taskcommentscount
            FROM tasks AS t
            LEFT JOIN comments AS c ON c.task_id = t.id
            WHERE t.assignee_id = :assigneeId
            GROUP BY taskid, tasktitle, taskdescription, taskauthorid, taskstatus,
                     taskpriority, taskassigneeid
            """, nativeQuery = true)
    Page<TaskView> findTaskViewsByAssigneeId(Long assigneeId, Pageable pageable);
}
