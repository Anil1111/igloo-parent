package org.iglooproject.jpa.externallinkchecker.business.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import org.iglooproject.jpa.exception.SecurityServiceException;
import org.iglooproject.jpa.exception.ServiceException;
import org.iglooproject.jpa.externallinkchecker.business.model.ExternalLinkWrapper;
import org.iglooproject.jpa.util.EntityManagerUtils;
import org.iglooproject.spring.util.SpringBeanUtils;

public class ExternalLinkCheckByDomainTask implements Callable<Void> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalLinkCheckByDomainTask.class);

	private static final int SESSION_LIMIT = 1000;
	
	@Autowired
	private IExternalLinkCheckerService linkCheckerService;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private EntityManagerUtils entityManagerUtils;
	
	@Autowired
	private IExternalLinkWrapperService externalLinkWrapperService;
	
	private Map<io.mola.galimatias.URL, Collection<Long>> urlToIdsMap;
	
	public ExternalLinkCheckByDomainTask(ApplicationContext applicationContext, Map<io.mola.galimatias.URL, Collection<Long>> urlToIdsMap) {
		this.urlToIdsMap = urlToIdsMap;
		SpringBeanUtils.autowireBean(applicationContext, this);
	}

	@Override
	public Void call() throws Exception {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		
		return template.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				Session session = entityManagerUtils.getEntityManager().unwrap(Session.class);
				session.setHibernateFlushMode(FlushMode.COMMIT);
				
				int count = 0;
				for (Map.Entry<io.mola.galimatias.URL, Collection<Long>> urlToIdsEntry : urlToIdsMap.entrySet()) {
					// We flush the session to avoid a memory overhead if there is a huge amount of links within the same domain
					if (count >= SESSION_LIMIT) {
						session.flush();
						session.clear();
						count = 0;
					}
					io.mola.galimatias.URL url = urlToIdsEntry.getKey();
					try {
						Collection<ExternalLinkWrapper> links = externalLinkWrapperService.listByIds(urlToIdsEntry.getValue());
						linkCheckerService.checkLinksWithSameUrl(url, links);
						count += links.size();
					} catch (RuntimeException | ServiceException | SecurityServiceException e) {
						LOGGER.error("An error occurred while checking links", e);
					}
				}
				
				return null;
			}
		});
	}
}
