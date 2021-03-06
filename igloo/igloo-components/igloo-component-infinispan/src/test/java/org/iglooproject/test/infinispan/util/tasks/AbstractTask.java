package org.iglooproject.test.infinispan.util.tasks;

import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.MDC;

import org.iglooproject.test.infinispan.util.TestCacheManagerBuilder;

public abstract class AbstractTask implements Runnable {

	private final EmbeddedCacheManager cacheManager;

	protected AbstractTask(EmbeddedCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public EmbeddedCacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public final void run() {
		MDC.put("processId", TestCacheManagerBuilder.PROCESS_ID);
		try {
			runTask();
		} finally {
			MDC.clear();
		}
	}

	public abstract void runTask();

}
