package fr.openwide.core.spring.notification.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.javatuples.LabelValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.config.CoreConfigurer;
import fr.openwide.core.spring.notification.exception.NotificationContentRenderingException;
import fr.openwide.core.spring.notification.model.INotificationContentDescriptor;
import fr.openwide.core.spring.notification.model.INotificationRecipient;
import fr.openwide.core.spring.notification.service.impl.ExplicitelyDefinedNotificationContentDescriptorImpl;
import fr.openwide.core.spring.notification.service.impl.FirstNotNullNotificationContentDescriptorImpl;
import fr.openwide.core.spring.notification.service.impl.FreemarkerTemplateNotificationContentDescriptorImpl;
import fr.openwide.core.spring.notification.util.NotificationUtils;
import fr.openwide.core.spring.util.StringUtils;
import freemarker.template.Configuration;

public class NotificationBuilder implements INotificationBuilderBaseState, INotificationBuilderBuildState,
		INotificationBuilderBodyState, INotificationBuilderContentState, INotificationBuilderTemplateState, INotificationBuilderSendState {
	
	private static final String DEFAULT_MAIL_ENCODING = "utf-8";
	
	private static final String NEW_LINE_TEXT_PLAIN = StringUtils.NEW_LINE_ANTISLASH_N;
	private static final String NEW_LINE_HTML = "<br />";
	
	private static final String DEV_SUBJECT_PREFIX = "[Dev]";
	
	private static final Function<INotificationRecipient, String> I_NOTIFICATION_RECIPIENT_TO_ADDRESS_FUNCTION = new Function<INotificationRecipient, String>() {
		@Override
		public String apply(INotificationRecipient recipient) {
			if (recipient == null) {
				return null;
			}
			return recipient.getEmail();
		}
	};
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private CoreConfigurer configurer;
	
	@Autowired
	@Qualifier(value = "freemarkerMailConfiguration")
	private Configuration templateConfiguration;
	
	private String from;
	
	private final Multimap<Locale, String> toByLocale = LinkedHashMultimap.create();
	
	private final Multimap<Locale, String> ccByLocale = LinkedHashMultimap.create();
	
	private final Multimap<Locale, String> bccByLocale = LinkedHashMultimap.create();
	
	private final Set<String> recipientsToIgnore = Sets.newHashSet();
	
	private INotificationContentDescriptor userContentDescriptor;
	
	private FreemarkerTemplateNotificationContentDescriptorImpl templateContentDescriptor;
	
	private ExplicitelyDefinedNotificationContentDescriptorImpl explicitelyDefinedContentDescriptor =
			new ExplicitelyDefinedNotificationContentDescriptorImpl();

	/**
	 * This is not a map, since duplicate filenames are allowed. This is not a multimap either, since mutliple
	 * attachements with the same name are not related to each other.
	 */
	private final Collection<LabelValue<String, File>> attachments = Lists.newArrayList();
	
	private final Map<String, File> inlines = Maps.newHashMap();
	
	private final Multimap<String, String> headers = LinkedHashMultimap.create();
	
	private int priority;
	
	protected NotificationBuilder() {
	}
	
	public static INotificationBuilderBaseState create() {
		return new NotificationBuilder();
	}
	
	@Override
	public INotificationBuilderBaseState from(String from) {
		Assert.hasText(from, "Sender's email address must contain text");
		this.from = from;
		return this;
	}
	
	@Override
	@Deprecated
	public INotificationBuilderBuildState to(String... to) {
		if (!ObjectUtils.isEmpty(to)) {
			toAddress(Lists.newArrayList(to));
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState toAddress(String toFirst, String... toOthers) {
		return toAddress(Lists.asList(toFirst, toOthers));
	}
	
	@Override
	public INotificationBuilderBuildState toAddress(Collection<String> to) {
		if (to != null) {
			for (String email : to) {
				if (StringUtils.hasText(email)) {
					addRecipient(toByLocale, getDefaultLocale(), email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState to(INotificationRecipient toFirst, INotificationRecipient ... toOthers) {
		return to(Lists.asList(toFirst, toOthers));
	}
	
	@Override
	public INotificationBuilderBuildState to(Collection<? extends INotificationRecipient> to) {
		if (to != null) {
			for (INotificationRecipient receiver : to) {
				if (receiver != null && StringUtils.hasText(receiver.getEmail())) {
					addRecipient(toByLocale, getLocale(receiver), receiver.getEmail());
				}
			}
		}
		return this;
	}
	
	@Override
	@Deprecated
	public INotificationBuilderBuildState cc(String... cc) {
		if (!ObjectUtils.isEmpty(cc)) {
			ccAddress(Lists.newArrayList(cc));
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState ccAddress(String ccFirst, String... ccOthers) {
		return ccAddress(Lists.asList(ccFirst, ccOthers));
	}
	
	@Override
	public INotificationBuilderBuildState ccAddress(Collection<String> cc) {
		if (cc != null) {
			for (String email : cc) {
				if (StringUtils.hasText(email)) {
					addRecipient(ccByLocale, getDefaultLocale(), email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState cc(INotificationRecipient ccFirst, INotificationRecipient ... ccOthers) {
		return cc(Lists.asList(ccFirst, ccOthers));
	}
	
	@Override
	public INotificationBuilderBuildState cc(Collection<? extends INotificationRecipient> cc) {
		if (cc != null) {
			for (INotificationRecipient receiver : cc) {
				if (receiver != null && StringUtils.hasText(receiver.getEmail())) {
					addRecipient(ccByLocale, getLocale(receiver), receiver.getEmail());
				}
			}
		}
		return this;
	}

	
	@Override
	@Deprecated
	public INotificationBuilderBuildState bcc(String... bcc) {
		if (!ObjectUtils.isEmpty(bcc)) {
			bccAddress(Lists.newArrayList(bcc));
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState bccAddress(String bccFirst, String... bccOthers) {
		return bccAddress(Lists.asList(bccFirst, bccOthers));
	}
	
	@Override
	public INotificationBuilderBuildState bccAddress(Collection<String> bcc) {
		if (bcc != null) {
			for (String email : bcc) {
				if (StringUtils.hasText(email)) {
					addRecipient(bccByLocale, getDefaultLocale(), email);
				}
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState bcc(INotificationRecipient bccFirst, INotificationRecipient ... bccOthers) {
		return bcc(Lists.asList(bccFirst, bccOthers));
	}
	
	@Override
	public INotificationBuilderBuildState bcc(Collection<? extends INotificationRecipient> bcc) {
		if (bcc != null) {
			for (INotificationRecipient receiver : bcc) {
				if (receiver != null && StringUtils.hasText(receiver.getEmail())) {
					addRecipient(bccByLocale, getLocale(receiver), receiver.getEmail());
				}
			}
		}
		return this;
	}
	
	
	@Override
	public INotificationBuilderBuildState exceptAddress(Collection<String> except) {
		recipientsToIgnore.addAll(except);
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState exceptAddress(String exceptFirst, String... exceptOthers) {
		recipientsToIgnore.addAll(Lists.asList(exceptFirst, exceptOthers));
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState except(INotificationRecipient exceptFirst, INotificationRecipient... exceptOthers) {
		recipientsToIgnore.addAll(Lists.transform(Lists.asList(exceptFirst, exceptOthers), I_NOTIFICATION_RECIPIENT_TO_ADDRESS_FUNCTION));
		return this;
	}
	
	@Override
	public INotificationBuilderBuildState except(Collection<? extends INotificationRecipient> except) {
		recipientsToIgnore.addAll(Lists.transform(Lists.newArrayList(except), I_NOTIFICATION_RECIPIENT_TO_ADDRESS_FUNCTION));
		return this;
	}
	
	@Override
	public INotificationBuilderTemplateState template(String templateKey) {
		Assert.hasText(templateKey, "Template key must contain text");
		this.templateContentDescriptor = new FreemarkerTemplateNotificationContentDescriptorImpl(templateConfiguration, templateKey, Collections.unmodifiableCollection(attachments));
		return this;
	}
	
	@Override
	public INotificationBuilderTemplateState variable(String name, Object value) {
		return variable(name, value, null);
	}
	
	/**
	 * If locale == null, the variable will be considered as not locale-sensitive and will be available for all locales
	 */
	@Override
	public INotificationBuilderTemplateState variable(String name, Object value, Locale locale) {
		Assert.hasText(name, "Variable name must contain text");
		templateContentDescriptor.setVariable(locale, name, value);
		return this;
	}
	
	@Override
	public INotificationBuilderTemplateState variables(Map<String, ?> variables) {
		return variables(variables, null);
	}

	@Override
	public INotificationBuilderTemplateState variables(Map<String, ?> variables, Locale locale) {
		templateContentDescriptor.setVariables(locale, variables);
		return this;
	}
	
	@Override
	public INotificationBuilderContentState content(INotificationContentDescriptor contentDescriptor) {
		this.userContentDescriptor = checkNotNull(contentDescriptor);
		return this;
	}
	
	@Override
	public INotificationBuilderBodyState subject(String subject) {
		return subject(getSubjectPrefix(), subject);
	}
	
	@Override
	public INotificationBuilderBodyState subject(String prefix, String subject) {
		Assert.hasText(subject, "Email subject must contain text");
		if (StringUtils.hasText(prefix)) {
			this.explicitelyDefinedContentDescriptor.setSubject(null, prefix + " " + subject);
		} else {
			this.explicitelyDefinedContentDescriptor.setSubject(null, subject);
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState textBody(String textBody) {
		return textBody(textBody, null);
	}
	
	@Override
	public INotificationBuilderSendState textBody(String textBody, Locale locale) {
		this.explicitelyDefinedContentDescriptor.setTextBody(locale, textBody);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState htmlBody(String htmlBody) {
		return htmlBody(htmlBody, null);
	}
	
	// If locale == null, the associated htmlBody will be used for any locale that do not have one
	@Override
	public INotificationBuilderSendState htmlBody(String htmlBody, Locale locale) {
		this.explicitelyDefinedContentDescriptor.setHtmlBody(locale, htmlBody);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState attach(String attachmentFilename, File file) {
		Assert.hasText(attachmentFilename, "Attachment filename must contain text");
		Assert.notNull(file, "Attached file must not be null");
		this.attachments.add(LabelValue.with(attachmentFilename, file));
		return this;
	}
	
	@Override
	public INotificationBuilderSendState attach(Collection<LabelValue<String, File>> attachments) {
		Assert.notNull(attachments, "Attachment map must not be null");
		this.attachments.addAll(attachments);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState attach(Map<String, File> attachments) {
		Assert.notNull(attachments, "Attachment map must not be null");
		for (Map.Entry<String, File> attachment : attachments.entrySet()) {
			attach(attachment.getKey(), attachment.getValue());
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState inline(String contentId, File file) {
		Assert.hasText(contentId, "Content ID must contain text");
		Assert.notNull(file, "Inline file must not be null");
		this.inlines.put(contentId, file);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState header(String name, String value) {
		Assert.hasText(name, "Header name must contain text");
		Assert.hasText(value, "Header value must contain text");
		this.headers.put(name, value);
		return this;
	}
	
	@Override
	public INotificationBuilderSendState headers(MultiValueMap<String, String> headers) {
		Assert.notNull(headers, "Header map must not be null");
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String value : header.getValue()) {
				header(header.getKey(), value);
			}
		}
		return this;
	}
	
	@Override
	public INotificationBuilderSendState priority(int priority) {
		if (priority < 1 || priority > 5) {
			throw new IllegalArgumentException("Priority must be between 1 (highest) and 5 (lowest)");
		}
		this.priority = priority;
		return this;
	}
	
	@Override
	public void send() throws ServiceException {
		send(DEFAULT_MAIL_ENCODING);
	}

	@Override
	public void send(String encoding) throws ServiceException {
		if (!NotificationUtils.isNotificationsEnabled()) {
			return;
		}
		
		removeDuplicatesAndRecipientsToIgnoreBeforeSendingMessage();
		
		Set<Locale> allLocales = ImmutableSet.<Locale> builder()
				.addAll(toByLocale.keySet())
				.addAll(ccByLocale.keySet())
				.addAll(bccByLocale.keySet())
				.build();
		for (Locale locale : allLocales) {
			Collection<String> to = toByLocale.get(locale);
			Collection<String> cc = ccByLocale.get(locale);
			Collection<String> bcc = bccByLocale.get(locale);
			
			if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) { // Multimap.get(unknown key) returns an empty collection
				continue;
			}
			
			try {
				MimeMessage message = buildMessage(to, cc, bcc, encoding, locale);
				
				if (message == null) {
					continue;
				}
				
				mailSender.send(message);
			} catch (NotificationContentRenderingException e) {
				throw new ServiceException("Error while rendering email notification", e);
			} catch (MessagingException e) {
				throw new ServiceException(
						String.format("Error building the MIME message (to:%s, cc:%s, bcc:%s)", to, cc, bcc),
						e
				);
			} catch (MailException e) {
				throw new ServiceException(
						String.format("Error sending email notification (to:%s, cc:%s, bcc:%s)", to, cc, bcc),
						e
				);
			}
		}
	}
	
	private void addRecipient(Multimap<Locale, String> recipients, Locale locale, String recipient) {
		if (!recipients.containsValue(recipient)) {
			recipients.put(locale, recipient);
		}
	}
	
	private void removeDuplicatesAndRecipientsToIgnoreBeforeSendingMessage() {
		Collection<String> tos = ImmutableSet.copyOf(toByLocale.values());
		Collection<String> ccs = ImmutableSet.copyOf(ccByLocale.values());
		
		doRemoveDuplicatesAndRecipientsToIgnoreBeforeSendingMessageFromList(toByLocale, recipientsToIgnore);
		doRemoveDuplicatesAndRecipientsToIgnoreBeforeSendingMessageFromList(ccByLocale, recipientsToIgnore, tos);
		doRemoveDuplicatesAndRecipientsToIgnoreBeforeSendingMessageFromList(bccByLocale, recipientsToIgnore, tos, ccs);
	}
	
	@SafeVarargs
	private final void doRemoveDuplicatesAndRecipientsToIgnoreBeforeSendingMessageFromList(Multimap<Locale, String> candidates, Collection<String>... recipientListsToRemove) {
		if (ObjectUtils.isEmpty(recipientListsToRemove)) {
			return;
		}
		
		for (Locale locale : candidates.keySet()) {
			for (Collection<String> recipientsToRemove : recipientListsToRemove) {
				candidates.get(locale).removeAll(recipientsToRemove);
			}
		}
	}
	
	private INotificationContentDescriptor chooseNotificationContentDescriptor() {
		List<INotificationContentDescriptor> descriptors = Lists.newArrayList();
		if (userContentDescriptor != null) { // Maximum priority
			descriptors.add(userContentDescriptor);
		}
		if (templateContentDescriptor != null) {
			descriptors.add(templateContentDescriptor);
		}
		descriptors.add(explicitelyDefinedContentDescriptor); // Minimum priority
		return new FirstNotNullNotificationContentDescriptorImpl(descriptors);
	}
	
	private MimeMessage buildMessage(Collection<String> to, Collection<String> cc, Collection<String> bcc, String encoding, Locale locale) throws NotificationContentRenderingException, MessagingException {
		INotificationContentDescriptor contentDescriptor = chooseNotificationContentDescriptor();
		String subject = contentDescriptor.renderSubject(locale);
		String textBody = contentDescriptor.renderTextBody(locale);
		String htmlBody = contentDescriptor.renderHtmlBody(locale);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, isMultipartNeeded(textBody, htmlBody), encoding);
		
		if (from == null) {
			from = getDefaultFrom();
		}
		Collection<String> filteredTo = filterTo(to);
		Collection<String> filteredCc = filterCcBcc(cc);
		Collection<String> filteredBcc = filterCcBcc(bcc);
		if (filteredTo.isEmpty() && filteredCc.isEmpty() && filteredBcc.isEmpty()) {
			return null;
		}
		
		helper.setFrom(from);
		helper.setTo(Iterables.toArray(filteredTo, String.class));
		helper.setCc(Iterables.toArray(filteredCc, String.class));
		helper.setBcc(Iterables.toArray(filteredBcc, String.class));
		
		helper.setSubject(subject);

		String textBodyPrefix = getBodyPrefix(to, cc, bcc, encoding, locale, MailFormat.TEXT);
		String htmlBodyPrefix = getBodyPrefix(to, cc, bcc, encoding, locale, MailFormat.HTML);
		
		if (StringUtils.hasText(textBody) && StringUtils.hasText(htmlBody)) {
			helper.setText(textBodyPrefix + textBody, htmlBodyPrefix + htmlBody);
		} else if (StringUtils.hasText(htmlBody)) {
			helper.setText(htmlBodyPrefix + htmlBody, true);
		} else {
			helper.setText(textBodyPrefix + textBody);
		}
	
		for (LabelValue<String, File> attachment : attachments) {
			helper.addAttachment(attachment.getLabel(), attachment.getValue());
		}
		for (Map.Entry<String, File> inline : inlines.entrySet()) {
			helper.addInline(inline.getKey(), inline.getValue());
		}
		for (Entry<String, Collection<String>> header : headers.asMap().entrySet()) {
			for (String value : header.getValue()) {
				message.addHeader(header.getKey(), value);
			}
		}
		if (priority != 0) {
			helper.setPriority(priority);
		}
		return message;
	}
	
	private boolean isMultipartNeeded(String textBody, String htmlBody) {
		boolean multipleBodies = StringUtils.hasText(textBody) && StringUtils.hasText(htmlBody);
		return multipleBodies || !attachments.isEmpty() || !inlines.isEmpty();
	}
	
	private String getDefaultFrom() {
		return configurer.getNotificationMailFrom();
	}
	
	private Locale getDefaultLocale() {
		return configurer.getDefaultLocale();
	}
	
	private Locale getLocale(INotificationRecipient recipient) {
		return configurer.toAvailableLocale(recipient.getLocale());
	}
	
	protected boolean isMailRecipientsFiltered() {
		return configurer.isNotificationMailRecipientsFiltered();
	}
	
	private Collection<String> filterTo(Collection<String> emails) {
		if (isMailRecipientsFiltered()) {
			return getNotificationTestEmails();
		}
		return emails;
	}
	
	protected Collection<String> getNotificationTestEmails() {
		return Arrays.asList(configurer.getNotificationTestEmails());
	}
	
	private Collection<String> filterCcBcc(Collection<String> emails) {
		if (isMailRecipientsFiltered()) {
			return Sets.newHashSet();
		}
		return emails;
	}
	
	private String getSubjectPrefix() {
		StringBuilder subjectPrefix = new StringBuilder();
		subjectPrefix.append(configurer.getNotificationMailSubjectPrefix());
		if (configurer.isConfigurationTypeDevelopment()) {
			subjectPrefix.append(DEV_SUBJECT_PREFIX);
		}
		return subjectPrefix.toString();
	}
	
	private String getBodyPrefix(Collection<String> to, Collection<String> cc, Collection<String> bcc, String encoding, Locale locale, MailFormat mailFormat) {
		if (isMailRecipientsFiltered()) {
			String newLine = MailFormat.HTML.equals(mailFormat) ? NEW_LINE_HTML : NEW_LINE_TEXT_PLAIN;
			
			StringBuffer newBody = new StringBuffer();
			newBody.append("#############").append(newLine);
			newBody.append("#").append(newLine);
			newBody.append("# To: ").append(renderAddressesForDebug(to, mailFormat)).append(newLine);
			newBody.append("# Cc: ").append(renderAddressesForDebug(cc, mailFormat)).append(newLine);
			newBody.append("# Bcc: ").append(renderAddressesForDebug(bcc, mailFormat)).append(newLine);
			newBody.append("#").append(newLine);
			newBody.append("# Encoding: ").append(encoding).append(newLine);
			newBody.append("# Locale: ").append(locale).append(newLine);
			newBody.append("#").append(newLine);
			newBody.append("#############").append(newLine).append(newLine).append(newLine);
			
			return newBody.toString();
		}
		return "";
	}
	
	private Object renderAddressesForDebug(Collection<String> addresses, MailFormat mailFormat) {
		final String prefix, suffix;
		if (MailFormat.HTML.equals(mailFormat)) {
			prefix = "&lt;";
			suffix = "&gt;";
		} else {
			prefix = "<";
			suffix = ">";
		}
		if (addresses != null && !addresses.isEmpty()) {
			return prefix + StringUtils.join(addresses, suffix + ", " + prefix) + suffix;
		} else {
			return prefix + "none" + suffix;
		}
	}
	
	private enum MailFormat {
		HTML,
		TEXT
	}
	
}
