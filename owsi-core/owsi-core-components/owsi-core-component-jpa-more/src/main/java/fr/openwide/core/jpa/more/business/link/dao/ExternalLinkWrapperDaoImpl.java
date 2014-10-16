package fr.openwide.core.jpa.more.business.link.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.StringExpression;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.core.jpa.more.business.link.model.ExternalLinkStatus;
import fr.openwide.core.jpa.more.business.link.model.ExternalLinkWrapper;
import fr.openwide.core.jpa.more.business.link.model.QExternalLinkWrapper;

@Service("externalLinkWrapperDao")
public class ExternalLinkWrapperDaoImpl extends GenericEntityDaoImpl<Long, ExternalLinkWrapper> implements IExternalLinkWrapperDao {
	private static final QExternalLinkWrapper qExternalLinkWrapper = QExternalLinkWrapper.externalLinkWrapper;
	
	@Override
	public List<ExternalLinkWrapper> listByIds(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Lists.newArrayList();
		}
		
		return new JPAQuery(getEntityManager()).from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.id.in(ids))
				.orderBy(qExternalLinkWrapper.id.asc())
				.list(qExternalLinkWrapper);
	}

	@Override
	public List<ExternalLinkWrapper> listActive() {
		JPQLQuery query = new JPAQuery(getEntityManager());
		
		query.from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.status.notIn(ExternalLinkStatus.INACTIVES))
				.orderBy(qExternalLinkWrapper.url.lower().asc());
		
		return query.list(qExternalLinkWrapper);
	}

	@Override
	public List<ExternalLinkWrapper> listNextCheckingBatch(int batchSize) {
		JPQLQuery query = new JPAQuery(getEntityManager());
		
		// Query to list the next <batchsize> URLs
		// Must be a separate query, not a subquery, since JPQL doesn't support limit in subqueries
		List<String> nextUrlsToCheckSubquery = listNextCheckingBatchUrls(batchSize);
		
		// Query to list the matching linkwrappers (may be more than <batchsize>)
		query.from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.url.lower().in(nextUrlsToCheckSubquery))
				// Only links without the following statuses are to be checked
				.where(qExternalLinkWrapper.status.notIn(ExternalLinkStatus.INACTIVES))
				.orderBy(qExternalLinkWrapper.id.asc());
		
		return query.list(qExternalLinkWrapper);
	}
	
	private List<String> listNextCheckingBatchUrls(int batchSize) {
		JPQLQuery query = new JPAQuery(getEntityManager());
		
		StringExpression url = qExternalLinkWrapper.url.lower();
		
		query.from(qExternalLinkWrapper)
				// Only links with the following statuses are to be checked
				.where(qExternalLinkWrapper.status.notIn(ExternalLinkStatus.INACTIVES))
				.groupBy(url)
				.orderBy(
						// NOTE: if, for a given URL, one linkWrapper has a null lastCheckDate and the other has a non-null lastCheckDate, the null one is ignored.
						// This is not really on purpose, but this does not impair functionality and the implementation is simpler that way.
						qExternalLinkWrapper.lastCheckDate.max().asc().nullsFirst(),
						url.asc()
				);
		
		return query.limit(batchSize).list(url);
	}

	@Override
	public List<String> listUrlsFromIds(Collection<Long> ids) {
		return new JPAQuery(getEntityManager())
				.from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.id.in(ids))
				.orderBy(qExternalLinkWrapper.url.asc())
				.distinct()
				.list(qExternalLinkWrapper.url);
	}

	@Override
	public List<String> listUrlsFromStatuses(Collection<ExternalLinkStatus> statuses) {
		return new JPAQuery(getEntityManager())
				.from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.status.in(statuses))
				.orderBy(qExternalLinkWrapper.url.asc())
				.distinct()
				.list(qExternalLinkWrapper.url);
	}

	@Override
	public List<ExternalLinkWrapper> listFromUrls(Collection<String> urls) {
		Set<String> lowerUrls = Sets.newHashSet();
		for (String url : urls) {
			lowerUrls.add(StringUtils.lowerCase(url));
		}
		
		return new JPAQuery(getEntityManager())
				.from(qExternalLinkWrapper)
				.where(qExternalLinkWrapper.url.lower().in(lowerUrls))
				.orderBy(qExternalLinkWrapper.id.asc())
				.list(qExternalLinkWrapper);
	}

}