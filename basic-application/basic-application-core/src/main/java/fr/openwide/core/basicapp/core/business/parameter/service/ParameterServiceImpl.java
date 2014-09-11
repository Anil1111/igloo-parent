package fr.openwide.core.basicapp.core.business.parameter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import fr.openwide.core.jpa.more.business.parameter.dao.IParameterDao;
import fr.openwide.core.jpa.more.business.parameter.service.AbstractParameterServiceImpl;

@Service("parameterService")
public class ParameterServiceImpl extends AbstractParameterServiceImpl implements IParameterService {

	@Autowired
	public ParameterServiceImpl(IParameterDao dao, PlatformTransactionManager transactionManager) {
		super(dao, transactionManager);
	}
}
