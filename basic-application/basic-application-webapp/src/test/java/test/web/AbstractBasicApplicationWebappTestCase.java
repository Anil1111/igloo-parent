package test.web;

import java.util.Set;

import org.apache.wicket.Localizer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebApplication;
import org.iglooproject.basicapp.core.business.history.service.IHistoryLogService;
import org.iglooproject.basicapp.core.business.user.model.BasicUser;
import org.iglooproject.basicapp.core.business.user.model.User;
import org.iglooproject.basicapp.core.business.user.model.UserGroup;
import org.iglooproject.basicapp.core.business.user.service.IUserGroupService;
import org.iglooproject.basicapp.core.business.user.service.IUserService;
import org.iglooproject.basicapp.core.security.model.BasicApplicationAuthorityConstants;
import org.iglooproject.jpa.exception.SecurityServiceException;
import org.iglooproject.jpa.exception.ServiceException;
import org.iglooproject.jpa.security.business.authority.model.Authority;
import org.iglooproject.jpa.security.business.authority.service.IAuthorityService;
import org.iglooproject.jpa.security.business.authority.util.CoreAuthorityConstants;
import org.iglooproject.jpa.security.service.IAuthenticationService;
import org.iglooproject.spring.property.dao.IMutablePropertyDao;
import org.iglooproject.test.wicket.core.AbstractWicketTestCase;
import org.iglooproject.wicket.more.AbstractCoreSession;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.collect.ImmutableSet;

import test.web.config.spring.BasicApplicationWebappTestCommonConfig;

@ContextConfiguration(classes = BasicApplicationWebappTestCommonConfig.class)
public abstract class AbstractBasicApplicationWebappTestCase extends AbstractWicketTestCase<BasicApplicationWicketTester> {

	protected static final String USER_PASSWORD = "kobalt";

	protected UserGroup users;

	protected UserGroup administrators;

	protected BasicUser utilisateur;

	protected BasicUser administrateur;

	@Autowired
	protected IUserService userService;

	@Autowired
	protected IUserGroupService userGroupService;

	@Autowired
	protected IAuthorityService authorityService;

	@Autowired
	protected IAuthenticationService authenticationService;

	@Autowired
	protected IHistoryLogService historyLogService;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	private IMutablePropertyDao mutablePropertyDao;

	@Autowired
	private WebApplication application;

	@Before
	public void setUp() throws ServiceException, SecurityServiceException {
		initAuthorities();
		initUserGroups();
		initUsers();
		
		setWicketTester(new BasicApplicationWicketTester(application));
	}

	@Override
	protected void cleanAll() throws ServiceException, SecurityServiceException {
		entityManagerClear();
		
		cleanEntities(userService);
		cleanEntities(userGroupService);
		cleanEntities(authorityService);
		cleanEntities(historyLogService);
		
		mutablePropertyDao.cleanInTransaction();
		
		authenticationService.signOut();
		
		emptyIndexes();
	}

	private void initAuthorities() throws ServiceException, SecurityServiceException {
		authorityService.create(new Authority(CoreAuthorityConstants.ROLE_ADMIN));
		authorityService.create(new Authority(CoreAuthorityConstants.ROLE_AUTHENTICATED));
	}

	private void initUserGroups() throws ServiceException, SecurityServiceException {
		users = new UserGroup("Users");
		administrators = new UserGroup("Administrators");
		
		userGroupService.create(users);
		userGroupService.create(administrators);
	}

	private void initUsers() throws ServiceException, SecurityServiceException {
		utilisateur = createBasicUser("utilisateur", ImmutableSet.of(BasicApplicationAuthorityConstants.ROLE_AUTHENTICATED),
			ImmutableSet.of(users));
		administrateur = createBasicUser("administrateur", ImmutableSet.of(BasicApplicationAuthorityConstants.ROLE_ADMIN),
			ImmutableSet.of(administrators));
	}

	private BasicUser createBasicUser(String username, Set<String> authorities, Set<UserGroup> userGroups)
		throws ServiceException, SecurityServiceException {
		
		BasicUser user = new BasicUser();
		user.setUsername(username);
		user.setFirstName(username);
		user.setLastName(username);
		if (authorities != null) {
			for (String authority : authorities) {
				user.addAuthority(authorityService.getByName(authority));
			}
		}
		
		userService.create(user);
		userService.setPasswords(user, USER_PASSWORD);
		
		if (userGroups != null) {
			for (UserGroup userGroup : userGroups) {
				userGroupService.addUser(userGroup, user);
			}
		}
		
		return user;
	}

	protected void authenticateUser(User user) throws ServiceException, SecurityServiceException {
		if (AuthenticatedWebSession.exists()) {
			AuthenticatedWebSession.get().invalidate();
		}
		
		AbstractCoreSession<?> session = AbstractCoreSession.get();
		User loggedInUser = null;
		
		session.signIn(user.getUsername(), USER_PASSWORD);
		loggedInUser = (User) session.getUser();
		userService.onSignIn(loggedInUser);
	}

	// Depends on the tester.getSession.getLocale()
	protected static String localize(String key) {
		return Localizer.get().getString(key, null);
	}

	protected String modalPath(String path) {
		return path + ":container:dialog";
	}

	protected String modalFormPath(String path) {
		return modalPath(path) + ":body:form";
	}

	protected String breadCrumbPath() {
		return "breadCrumb:breadCrumbElementListView";
	}

	protected String breadCrumbElementPath(int element) {
		return "breadCrumb:breadCrumbElementListView:" + element + ":breadCrumbElement";
	}
}
