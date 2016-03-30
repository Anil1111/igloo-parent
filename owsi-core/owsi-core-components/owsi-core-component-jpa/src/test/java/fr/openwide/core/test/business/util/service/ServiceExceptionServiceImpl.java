package fr.openwide.core.test.business.util.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.test.business.company.model.Company;
import fr.openwide.core.test.business.company.service.ICompanyService;


/**
 * Classe utilitaire permettant de générer des exceptions et de vérifier
 * leur bonne prise en compte par le transaction manager
 * 
 * @author Open Wide
 */
@Service
public class ServiceExceptionServiceImpl implements ServiceExceptionService {
	
	@Autowired
	private ICompanyService companyService;

	@Override
	public void dontThrow() throws ServiceException, SecurityServiceException {
		Company company = new Company("Company Test");
		companyService.create(company);
	}

	@Override
	public void throwServiceException() throws ServiceException, SecurityServiceException {
		Company company = new Company("Company Test");
		companyService.create(company);
		throw new ServiceException();
	}

	@Override
	public void throwServiceInheritedException() throws ServiceException, SecurityServiceException {
		Company company = new Company("Company Test");
		companyService.create(company);
		throw new MyException() ;
	}

	@Override
	public void throwUncheckedException() throws ServiceException, SecurityServiceException {
		Company company = new Company("Company Test");
		companyService.create(company);
		throw new IllegalStateException();
	}

	@Override
	public long size() {
		return companyService.count();
	}
	
	private static class MyException extends ServiceException {
		private static final long serialVersionUID = -6408089837070550022L;
	}
}
