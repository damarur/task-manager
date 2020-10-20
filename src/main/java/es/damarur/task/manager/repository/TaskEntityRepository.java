package es.damarur.task.manager.repository;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.damarur.task.manager.model.TaskEntity;

@Repository
public interface TaskEntityRepository extends JpaRepository<TaskEntity, Integer>, JpaSpecificationExecutor<TaskEntity>  {

	@Transactional
	@Modifying
	@Query(value = "UPDATE TaskEntity t SET t.finished = :finished, t.finishDate = :finishDate WHERE t.id = :id")
	public int updateFinishedTask(boolean finished, Date finishDate, Integer id);

	public int countTaskEntityByIdAndFinished(int id, boolean finished);

}
