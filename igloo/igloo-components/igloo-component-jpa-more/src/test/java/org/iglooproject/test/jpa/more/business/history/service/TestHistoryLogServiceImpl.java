package org.iglooproject.test.jpa.more.business.history.service;

import java.util.Date;
import java.util.List;

import org.iglooproject.functional.Supplier2;
import org.iglooproject.jpa.more.business.history.service.AbstractHistoryLogServiceImpl;
import org.iglooproject.test.jpa.more.business.history.dao.ITestHistoryLogDao;
import org.iglooproject.test.jpa.more.business.history.model.TestHistoryDifference;
import org.iglooproject.test.jpa.more.business.history.model.TestHistoryLog;
import org.iglooproject.test.jpa.more.business.history.model.atomic.TestHistoryEventType;
import org.iglooproject.test.jpa.more.business.history.model.bean.TestHistoryLogAdditionalInformationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestHistoryLogServiceImpl extends AbstractHistoryLogServiceImpl<TestHistoryLog, TestHistoryEventType,
		TestHistoryDifference, TestHistoryLogAdditionalInformationBean>
		implements ITestHistoryLogService {

	private static final Supplier2<TestHistoryDifference> HISTORY_DIFFERENCE_SUPPLIER = () -> new TestHistoryDifference();
	
	@Autowired
	public TestHistoryLogServiceImpl(ITestHistoryLogDao dao) {
		super(dao);
	}
	
	@Override
	protected <T> TestHistoryLog newHistoryLog(Date date, TestHistoryEventType eventType, List<TestHistoryDifference> differences,
			T mainObject, TestHistoryLogAdditionalInformationBean additionalInformation) {
		TestHistoryLog log = new TestHistoryLog(date, eventType, valueService.create(mainObject));
		
		// Don't set any subject here (we don't have a IUserService)
		
		if (additionalInformation != null) {
			setAdditionalInformation(log, additionalInformation);
		}
		
		return log;
	}

	@Override
	protected Supplier2<TestHistoryDifference> newHistoryDifferenceSupplier() {
		return HISTORY_DIFFERENCE_SUPPLIER;
	}

}
