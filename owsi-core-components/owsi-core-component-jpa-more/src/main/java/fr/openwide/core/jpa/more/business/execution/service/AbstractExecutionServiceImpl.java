package fr.openwide.core.jpa.more.business.execution.service;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.more.business.execution.dao.IAbstractExecutionDao;
import fr.openwide.core.jpa.more.business.execution.model.AbstractExecution;
import fr.openwide.core.jpa.more.business.execution.model.ExecutionStatus;
import fr.openwide.core.jpa.more.business.execution.model.IExecutionType;

public abstract class AbstractExecutionServiceImpl<E extends AbstractExecution<E, ET>, ET extends IExecutionType>
	extends GenericEntityServiceImpl<Integer, E> implements IAbstractExecutionService<E, ET> {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public AbstractExecutionServiceImpl(IAbstractExecutionDao<E> executionDao) {
		super(executionDao);
	}

	@Override
	public E start(E execution, ET type) throws ServiceException, SecurityServiceException {
		execution.setExecutionStatus(ExecutionStatus.RUNNING);
		execution.setExecutionType(type);
		execution.setStartDate(new Date());
		create(execution);
		return execution;
	}

	@Override
	public void close(E execution, ExecutionStatus executionStatus) throws ServiceException, SecurityServiceException {
		entityManager.merge(execution);
		execution.setEndDate(new Date());
		execution.setExecutionStatus(executionStatus);
		update(execution);
	}
}
