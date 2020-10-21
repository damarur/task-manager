package es.damarur.task.manager.repository;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import es.damarur.task.manager.model.TaskEntity;

public class TaskEntitySpecifications {

	private static final String START_DATE = "startDate";
	private static final String TARGET_DATE = "targetDate";
	private static final String FINISH_DATE = "finishDate";

	private TaskEntitySpecifications() {
	}

	public static Specification<TaskEntity> startDateGreaterThan(Date startDate) {
		return (root, query, builder) -> startDate == null ? builder.conjunction()
				: builder.greaterThanOrEqualTo(root.get(START_DATE), startDate);
	}

	public static Specification<TaskEntity> targetDateGreaterThan(Date targetDate) {
		return (root, query, builder) -> targetDate == null ? builder.conjunction()
				: builder.greaterThanOrEqualTo(root.get(TARGET_DATE), targetDate);
	}

	public static Specification<TaskEntity> finishDateGreaterThan(Date finishDate) {
		return (root, query, builder) -> finishDate == null ? builder.conjunction()
				: builder.greaterThanOrEqualTo(root.get(FINISH_DATE), finishDate);
	}

	public static Specification<TaskEntity> startDateLessThan(Date startDate) {
		return (root, query, builder) -> startDate == null ? builder.conjunction()
				: builder.lessThanOrEqualTo(root.get(START_DATE), startDate);
	}

	public static Specification<TaskEntity> targetDateLessThan(Date targetDate) {
		return (root, query, builder) -> targetDate == null ? builder.conjunction()
				: builder.lessThanOrEqualTo(root.get(TARGET_DATE), targetDate);
	}

	public static Specification<TaskEntity> finishDateLessThan(Date finishDate) {
		return (root, query, builder) -> finishDate == null ? builder.conjunction()
				: builder.lessThanOrEqualTo(root.get(FINISH_DATE), finishDate);
	}

	public static Specification<TaskEntity> finishedEquals(Boolean finished) {
		return (root, query, builder) -> finished == null ? builder.conjunction()
				: builder.equal(root.get("finished"), finished);
	}

	public static Specification<TaskEntity> titleLike(String title) {
		return (root, query, builder) -> StringUtils.isBlank(title) ? builder.conjunction()
				: builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
	}
}
