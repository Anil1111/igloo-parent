package fr.openwide.core.jpa.more.business.task.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.more.business.task.dao.IQueuedTaskHolderDao;
import fr.openwide.core.jpa.more.business.task.model.QueuedTaskHolder;
import fr.openwide.core.jpa.more.business.task.util.TaskStatus;

public class QueuedTaskHolderServiceImpl extends GenericEntityServiceImpl<Long, QueuedTaskHolder> implements
		IQueuedTaskHolderService {
	
	private IQueuedTaskHolderDao queuedTaskHolderDao;

	@Autowired
	public QueuedTaskHolderServiceImpl(IQueuedTaskHolderDao queuedTaskHolderDao) {
		super(queuedTaskHolderDao);
		this.queuedTaskHolderDao = queuedTaskHolderDao;
	}

	@Override
	protected void createEntity(QueuedTaskHolder queuedTaskHolder) throws ServiceException, SecurityServiceException {
		queuedTaskHolder.setCreationDate(new Date());
		super.createEntity(queuedTaskHolder);
	}

	@Override
	public Long count(Date since, TaskStatus... statuses) {
		return queuedTaskHolderDao.count(since, statuses);
	}
	
	@Override
	public Long count(TaskStatus... statuses) {
		return queuedTaskHolderDao.count(statuses);
	}

	@Override
	public QueuedTaskHolder getNextTaskForExecution(String taskType) {
		return queuedTaskHolderDao.getNextTaskForExecution(taskType);
	}

	@Override
	public QueuedTaskHolder getRandomStalledTask(String taskType, int executionTimeLimitInSeconds) {
		return queuedTaskHolderDao.getStalledTask(taskType, executionTimeLimitInSeconds);
	}
	
	@Override
	public List<Long> initializeTasksAndListConsumable(String queueId) throws ServiceException, SecurityServiceException {
		List<QueuedTaskHolder> queuedTaskHolderList = queuedTaskHolderDao.listConsumable(queueId);
		List<Long> taskIds = Lists.newArrayListWithExpectedSize(queuedTaskHolderList.size());

		for (QueuedTaskHolder queuedTaskHolder : queuedTaskHolderList) {
			queuedTaskHolder.setStatus(TaskStatus.TO_RUN);
			queuedTaskHolder.resetExecutionInformation();
			update(queuedTaskHolder);
			taskIds.add(queuedTaskHolder.getId());
		}

		return taskIds;
	}

	@Override
	public List<String> listTypes() {
		return queuedTaskHolderDao.listTypes();
	}
	
	@Override
	public boolean isReloadable(QueuedTaskHolder task) {
		return TaskStatus.RELOADABLE_TASK_STATUS.contains(task.getStatus());
	}

	@Override
	public boolean isCancellable(QueuedTaskHolder task) {
		return TaskStatus.CANCELLABLE_TASK_STATUS.contains(task.getStatus());
	}
}
