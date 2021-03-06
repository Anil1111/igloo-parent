package org.iglooproject.jpa.security.config.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.parameters.AnnotationParameterNameDiscoverer;
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer;

import com.google.common.collect.ImmutableList;

import org.iglooproject.commons.util.security.PermissionObject;
import org.iglooproject.jpa.security.access.expression.method.CoreMethodSecurityExpressionHandler;
import org.iglooproject.jpa.security.service.ICorePermissionEvaluator;

/**
 * Par rapport à son parent, cette classe active la protection via les
 * annotations de sécurité spring.
 * 
 * @see Secured
 */
@Configuration
@ImportResource("classpath:spring/igloo-component-jpa-security-context.xml")
// définition des proxys Secured
public abstract class AbstractJpaSecuritySecuredConfig extends AbstractJpaSecurityConfig {
	
	@Override
	public MethodSecurityExpressionHandler expressionHandler(ICorePermissionEvaluator corePermissionEvaluator) {
		CoreMethodSecurityExpressionHandler methodSecurityExpressionHandler = new CoreMethodSecurityExpressionHandler();
		methodSecurityExpressionHandler.setCorePermissionEvaluator(corePermissionEvaluator);
		
		// Discover parameter name using the @PermissionObject annotation, too
		methodSecurityExpressionHandler.setParameterNameDiscoverer(new DefaultSecurityParameterNameDiscoverer(
				ImmutableList.of(
						new AnnotationParameterNameDiscoverer(PermissionObject.class.getName())
				)
		));
		
		return methodSecurityExpressionHandler;
	}

}
