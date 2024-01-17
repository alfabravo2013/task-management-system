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
                (SELECT count (*) AS taskcommentscount FROM comments AS c WHERE c.task_id = t.id)
            FROM tasks AS t
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
                (SELECT count (*) AS taskcommentscount FROM comments AS c WHERE c.task_id = t.id)
            FROM tasks AS t
            WHERE t.author_id = :authorId
            GROUP BY taskid, tasktitle, taskdescription, taskauthorid, taskstatus,
                     taskpriority, taskassigneeid
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
                (SELECT count (*) AS taskcommentscount FROM comments AS c WHERE c.task_id = t.id)
            FROM tasks AS t
            WHERE t.assignee_id = :assigneeId
            GROUP BY taskid, tasktitle, taskdescription, taskauthorid, taskstatus,
                     taskpriority, taskassigneeid
            """, nativeQuery = true)
    Page<TaskView> findTaskViewsByAssigneeId(Long assigneeId, Pageable pageable);
}
