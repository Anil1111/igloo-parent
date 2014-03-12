package fr.openwide.core.test.jpa.more.business.audit.service;

import fr.openwide.core.jpa.more.business.audit.service.IAbstractAuditService;
import fr.openwide.core.test.jpa.more.business.audit.model.MockAudit;
import fr.openwide.core.test.jpa.more.business.audit.model.MockAuditAction;
import fr.openwide.core.test.jpa.more.business.audit.model.MockAuditActionEnum;
import fr.openwide.core.test.jpa.more.business.audit.model.MockAuditFeature;
import fr.openwide.core.test.jpa.more.business.audit.model.MockAuditFeatureEnum;

public interface IMockAuditService extends IAbstractAuditService<MockAudit> {

	MockAuditAction getAuditActionByEnum(MockAuditActionEnum auditActionEnum);

	MockAuditFeature getAuditFeatureByEnum(MockAuditFeatureEnum auditFeatureEnum);
}
