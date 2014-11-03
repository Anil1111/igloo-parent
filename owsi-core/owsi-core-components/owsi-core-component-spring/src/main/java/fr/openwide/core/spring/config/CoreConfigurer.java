package fr.openwide.core.spring.config;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import fr.openwide.core.spring.config.util.TaskQueueStartMode;
import fr.openwide.core.spring.util.StringUtils;

public class CoreConfigurer extends CorePropertyPlaceholderConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CoreConfigurer.class);

	private static final String CONFIGURATION_TYPE_DEVELOPMENT = "development";
	private static final String CONFIGURATION_TYPE_DEPLOYMENT = "deployment";
	
	private static final int TASK_STOP_TIMEOUT_DEFAULT = 70000;
	private static final int TASK_QUEUE_NUMBER_OF_THREADS_DEFAULT = 1;
	
	public String getVersion() {
		return getPropertyAsString("version");
	}

	public String getConfigurationType() {
		String configurationType = getPropertyAsString("configurationType");
		if (CONFIGURATION_TYPE_DEVELOPMENT.equals(configurationType) || CONFIGURATION_TYPE_DEPLOYMENT.equals(configurationType)) {
			return configurationType;
		} else {
			throw new IllegalStateException("Configuration type should be either development or deployment");
		}
	}
	
	public boolean isConfigurationTypeDevelopment() {
		return CONFIGURATION_TYPE_DEVELOPMENT.equals(getConfigurationType());
	}
	
	public File getTmpDirectory() {
		return getPropertyAsWritableDirectory("tmp.path");
	}
	
	public int getLuceneBooleanQueryMaxClauseCount() {
		return getPropertyAsInteger("lucene.booleanQuery.maxClauseCount", BooleanQuery.getMaxClauseCount());
	}
	
	public File getImageMagickConvertBinary() {
		String imageMagickConvertBinary = getPropertyAsString("imageMagick.convertBinary.path", "/usr/bin/convert");
		
		if (StringUtils.hasText(imageMagickConvertBinary)) {
			return new File(imageMagickConvertBinary);
		} else {
			return null;
		}
	}
	
	public Set<Locale> getAvailableLocales() {
		List<String> localesAsString = getPropertyAsStringList("locale.availableLocales");
		Set<Locale> locales = new HashSet<Locale>(localesAsString.size());
		
		for (String localeAsString : localesAsString) {
			try {
				locales.add(LocaleUtils.toLocale(localeAsString));
			} catch (Exception e) {
				LOGGER.error(String.format(
						"%1$s string from locale.availableLocales cannot be mapped to Locale, ignored",
						localeAsString
				));
			}
		}
		return locales;
	}
	
	public Locale getDefaultLocale() {
		Locale defaultLocale = LocaleUtils.toLocale(getPropertyAsString("locale.default"));
		if (defaultLocale == null) {
			defaultLocale = Locale.US;
		}
		return defaultLocale;
	}
	
	/**
	 * <p> Le but est de partir d'une locale
	 * quelconque et d'aboutir obligatoirement à une locale provenant de la liste
	 * <i>locale.availableLocales</i>.</p>
	 * 
	 * <p>Le mapping se fait ainsi :
	 * <ul>
	 * <li>si la locale est dans locale.availableLocales, alors on utilise la locale</li>
	 * <li>sinon on vérifié si le <i>Language</i> de la locale correspond à un <i>Language</i>
	 * dans locale.availableLocales ; alors on utilise la locale correspondante
	 * </li>
	 * <li>sinon on utilise <i>locale.default</i></li>
	 * </ul>
	 * </p>
	 * 
	 * <p>Exemple :<br/>
	 * <code>locale.availableLocales=fr en</code><br/>
	 * <code>locale.default=fr</code><br/>
	 * <br/>
	 * Les résultats seront les suivants
	 * <ul>
	 * <li>fr -> fr (correspondance exacte)</li>
	 * <li>fr_FR -> fr (correspondance sur Language)</li>
	 * <li>en -> en (correspondance exacte)</li>
	 * <li>en_US -> en (correspondance sur Language)</li>
	 * <li>ar_SA -> fr (défaut)</li>
	 * </ul>
	 * </p>
	 *
	 * @param locale
	 * @return locale, not null, from locale.availableLocales
	 */
	public Locale toAvailableLocale(Locale locale) {
		if (locale != null) {
			Set<Locale> availableLocales = getAvailableLocales();
			if (availableLocales.contains(locale)) {
				return locale;
			} else {
				for (Locale availableLocale : availableLocales) {
					if (availableLocale.getLanguage().equals(locale.getLanguage())) {
						return availableLocale;
					}
				}
			}
		}
		
		// default locale from configuration
		return getDefaultLocale();
	}
	
	public int getHibernateSearchReindexBatchSize() {
		return getPropertyAsInteger("hibernate.search.reindex.batchSize", 25);
	}
	
	public int getHibernateSearchReindexLoadThreads() {
		return getPropertyAsInteger("hibernate.search.reindex.loadThreads", 8);
	}
	
	public String getNotificationMailFrom() {
		return getPropertyAsString("notification.mail.from");
	}
	
	public String getNotificationMailSubjectPrefix() {
		return getPropertyAsString("notification.mail.subjectPrefix");
	}
	
	public boolean isNotificationMailRecipientsFiltered() {
		return isConfigurationTypeDevelopment() || getPropertyAsBoolean("notification.mail.recipientsFiltered");
	}
	
	public List<String> getNotificationTestEmails() {
		return getPropertyAsStringList("notification.test.emails");
	}

	public int getTaskStopTimeout() {
		return getPropertyAsInteger("task.stop.timeout", TASK_STOP_TIMEOUT_DEFAULT);
	}
	
	public TaskQueueStartMode getTaskQueueStartMode() {
		return getPropertyAsEnum("task.startMode", TaskQueueStartMode.class, TaskQueueStartMode.manual);
	}
	
	public int getTaskQueueNumberOfThreads(String queueId) {
		Assert.notNull(queueId);
		return getPropertyAsInteger("task.queues.config." + queueId + ".threads", TASK_QUEUE_NUMBER_OF_THREADS_DEFAULT);
	}
	
	public String getSecurityPasswordSalt() {
		return getPropertyAsString("security.passwordSalt");
	}
	
	public Integer getSecurityPasswordExpirationDays() {
		return getPropertyAsInteger("security.password.expiration.days", 90);
	}
	
	public Integer getSecurityPasswordHistoryCount() {
		return getPropertyAsInteger("security.password.history.count", 4);
	}
	
	public String getSecurityPasswordRecoveryRequestTokenSalt() {
		return getPropertyAsString("security.password.recovery.request.token.salt");
	}
	
	public Integer getSecurityPasswordRecoveryRequestExpicationMinutes() {
		return getPropertyAsInteger("security.password.recovery.request.expiration.minutes", 15);
	}
	
	/**
	 * Configuration of the link checker tool
	 */
	public String getExternalLinkCheckerUserAgent() {
		return getPropertyAsString("externalLinkChecker.userAgent", "Core External Link Checker");
	}
	
	public int getExternalLinkCheckerMaxRedirects() {
		return getPropertyAsInteger("externalLinkChecker.maxRedirects", 5);
	}
	
	public int getExternalLinkCheckerTimeout() {
		return getPropertyAsInteger("externalLinkChecker.timeout", 10000);
	}
	
	public int getExternalLinkCheckerRetryAttemptsLimit() {
		return getPropertyAsInteger("externalLinkChecker.retryAttemptsNumber", 5);
	}
	
	public int getExternalLinkCheckerThreadPoolSize() {
		return getPropertyAsInteger("externalLinkChecker.threadPoolSize", Runtime.getRuntime().availableProcessors());
	}
	
	public int getExternalLinkCheckerBatchSize() {
		return getPropertyAsInteger("externalLinkChecker.batchSize", 500);
	}
	
	public int getExternalLinkCheckerMinDelayBetweenTwoChecksInDays() {
		return getPropertyAsInteger("externalLinkChecker.minDelayBetweenTwoChecksInDays", 3, 0, null);
	}
	
	/**
	 * Configuration of the RequestCycle builder for background threads
	 */
	public String getWicketBackgroundRequestCycleBuilderUrlScheme() {
		return getPropertyAsString("wicket.backgroundThreadContextBuilder.url.scheme", "http");
	}
	
	public String getWicketBackgroundRequestCycleBuilderUrlServerName() {
		return getPropertyAsString("wicket.backgroundThreadContextBuilder.url.serverName", "localhost");
	}
	
	public int getWicketBackgroundRequestCycleBuilderUrlServerPort() {
		return getPropertyAsInteger("wicket.backgroundThreadContextBuilder.url.serverPort", 8080);
	}
	
	public String getWicketDiskDataStorePath() {
		return getPropertyAsString("wicket.diskDataStore.path", null);
	}
	
	public int getWicketDiskDataStoreInMemoryCacheSize() {
		return getPropertyAsInteger("wicket.diskDataStore.inMemoryCacheSize", 40);
	}
	
	public int getWicketDiskDataStoreMaxSizePerSession() {
		return getPropertyAsInteger("wicket.diskDataStore.maxSizePerSession", 10);
	}
	
}