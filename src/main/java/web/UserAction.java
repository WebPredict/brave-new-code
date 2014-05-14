package wp.web;

import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.taglibs.standard.lang.jpath.encoding.HtmlEncoder;

import wp.core.AlreadyExistsException;
import wp.core.EmailWithOtherUserException;
import wp.core.IdNameProjection;
import wp.core.Rater;
import wp.core.RatingInfo;
import wp.core.RecentStats;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.dao.ListInfo;
import wp.model.Alert;
import wp.model.AlertResult;
import wp.model.CategorySet;
import wp.model.CrawlerImpl;
import wp.model.CrawlerResult;
import wp.model.CrawlingIntensity;
import wp.model.Features;
import wp.model.Feed;
import wp.model.Group;
import wp.model.Photo;
import wp.model.Privacy;
import wp.model.RatedPage;
import wp.model.User;
import wp.model.UserConfig;
import wp.model.UserStats;
import wp.service.AlertService;
import wp.service.CrawlerService;
import wp.service.GroupService;
import wp.service.PhotoService;
import wp.service.UserConfigService;

/**
 * @struts.action path="/EditUser" parameter="editUser" validate="false"
 *   name="userForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="editUser" path="/UserInfo.jsp"
 * @spring.bean name="/EditUser"
 * 
 * @struts.action path="/EditUserConfig" parameter="editUserConfig" validate="false"
 *   name="userConfigForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="editUserConfig" path="/EditUserConfig.jsp"
 * @spring.bean name="/EditUserConfig"
 * 
 * @struts.action path="/SaveUserConfig" parameter="saveUserConfig" validate="true" 
 *   name="userConfigForm" scope="request" input="/EditUserConfig.do"
 * @struts.action-forward name="saveUserConfig" path="/ListUserConfigs.do"
 * @struts.action-forward name="cancelSaveConfig" path="/Controls.do"
 * @spring.bean name="/SaveUserConfig"
 * 
 * @struts.action path="/ViewUser" parameter="viewUser" validate="false"
 *   name="userForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewUser" path="/UserView.jsp"
 * @spring.bean name="/ViewUser"
 * 
 * @struts.action path="/ViewUserConfig" parameter="viewUserConfig" validate="false"
 *   name="userConfigForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewUserConfig" path="/UserConfigView.jsp"
 * @spring.bean name="/ViewUserConfig"
 * 
 * @struts.action path="/CheckMail" parameter="checkMail" validate="false"
 *   name="messageForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="checkMail" path="/Messages.jsp"
 * @spring.bean name="/CheckMail"
 *  
 * @struts.action path="/SaveUser" parameter="saveUser" validate="true" 
 *   name="userForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="saveUser" path="/ViewUser.do"
 * @struts.action-forward name="cancelSave" path="/Controls.do"
 * @spring.bean name="/SaveUser"
 * 
 * @struts.action path="/AlertsRSS" parameter="alertsFeed" validate="false"
 * @spring.bean name="/AlertsRSS"
 * 
 * @struts.action path="/CrawlersRSS" parameter="crawlersFeed" validate="false"
 * @spring.bean name="/CrawlersRSS"
 * 
 * @struts.action path="/AddFriends" parameter="addFriends" validate="false"
 *   name="addFriendsForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="addFriends" path="/AddFriends.jsp"
 * @struts.action-forward name="doneAddFriends" path="/EditUser.do"
 * @spring.bean name="/AddFriends"
 * 
 * @struts.action path="/AddGroups" parameter="addGroups" validate="false"
 *   name="addGroupsForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="addGroups" path="/AddGroups.jsp"
 * @struts.action-forward name="doneAddGroups" path="/EditUser.do"
 * @spring.bean name="/AddGroups"
 * 
 * @struts.action path="/AddPhoto" parameter="addPhoto" validate="false"
 *   name="userForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="addPhoto" path="/ImageUpload.jsp"
 * @spring.bean name="/AddPhoto"
 * 
 * @struts.action path="/AddFriend" parameter="addFriend" validate="false"
 *   scope="request" input="/ViewUser.do"
 * @struts.action-forward name="doneAddFriend" path="/ViewUser.do"
 * @spring.bean name="/AddFriend"
 * 
 * @struts.action path="/PreSignup" parameter="preSignup" validate="false"
 *   name="preSignupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="preSignup" path="/PreSignup.jsp"
 * @spring.bean name="/PreSignup"
 * 
 * @struts.action path="/SimpleSignup" parameter="simpleSignup" validate="false"
 *   name="preSignupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="simpleSignup" path="/SimpleSignup.jsp"
 * @spring.bean name="/SimpleSignup"
 * 
 * @struts.action path="/SetupMessage" parameter="setupMessage" validate="false"
 *   name="messageForm" scope="request" input="/ViewUser.do"
 * @struts.action-forward name="sendMessage" path="/SendMessage.jsp"
 * @spring.bean name="/SetupMessage"
 * 
 * @struts.action path="/InheritRatings" parameter="inheritRatings" validate="false"
 *   name="userForm" scope="request" input="/ViewUser.do"
 * @struts.action-forward name="doneInheritRatings" path="/Controls.do"
 * @spring.bean name="/InheritRatings"
 * 
 * @struts.action path="/CopyRatings" parameter="copyRatings" validate="false"
 *   name="userForm" scope="request" input="/ViewUser.do"
 * @spring.bean name="/CopyRatings"
 * 
 * @struts.action path="/SendMessage" parameter="sendMessage" validate="false"
 *   name="messageForm" scope="request" input="/ViewUser.do"
 * @struts.action-forward name="doneSendMessage" path="/UserView.jsp"
 * @spring.bean name="/SendMessage"
 * 
 * @struts.action path="/FinishSignup" parameter="finishSignup" validate="false"
 *   name="preSignupForm" scope="request" input="/SimpleSignup.do"
 * @struts.action-forward name="finishSignup" path="/SignupInfo.jsp"
 * @spring.bean name="/FinishSignup"
 * 
 * @struts.action path="/FinishSimpleSignup" parameter="finishSimpleSignup" validate="false"
 *   name="preSignupForm" scope="request" input="/Welcome.do"
 * @spring.bean name="/FinishSimpleSignup"
 * 
 * @struts.action path="/ConfirmSignup" parameter="confirmSignup" validate="false"
 *   scope="request" input="/Welcome.do"
 * @struts.action-forward name="signupError" path="/SignupError.jsp"
 * @struts.action-forward name="confirmed" path="/SignupConfirmed.jsp"
 * @spring.bean name="/ConfirmSignup"
 * 
 * @struts.action path="/Cancel" parameter="cancel" validate="false"
 *   scope="request" input="/Welcome.do"
 * @struts.action-forward name="cancel" path="/CancelSubscription.jsp"
 * @spring.bean name="/Cancel"
 * 
 * @struts.action path="/GetAlerts" parameter="alerts" validate="false"
 *   name="alertsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="alerts" path="/Alerts.jsp"
 * @spring.bean name="/GetAlerts"
 * 
 * @struts.action path="/GetFeeds" parameter="feeds" validate="false"
 *   name="feedsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="feeds" path="/Feeds.jsp"
 * @spring.bean name="/GetFeeds"
 * 
 * @struts.action path="/ShowCrawls" parameter="crawls" validate="false"
 *   name="crawlsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="crawls" path="/Crawls.jsp"
 * @spring.bean name="/ShowCrawls"
 * 
 * @struts.action path="/SaveAlerts" parameter="saveAlerts" validate="false"
 *   name="alertsForm" scope="request" input="/GetAlerts.do"
 * @spring.bean name="/SaveAlerts"
 * 
 * @struts.action path="/SaveFeeds" parameter="saveFeeds" validate="false"
 *   name="feedsForm" scope="request" input="/GetFeeds.do"
 * @spring.bean name="/SaveFeeds"
 * 
 * @struts.action path="/SaveCrawls" parameter="saveCrawls" validate="false"
 *   name="crawlsForm" scope="request" input="/ShowCrawls.do"
 * @spring.bean name="/SaveCrawls"
 * 
 * @struts.action path="/Subscribe" parameter="subscribe" validate="false"
 *   scope="request" input="/Welcome.do"
 * @struts.action-forward name="subscribe" path="/Subscribe.jsp"
 * @spring.bean name="/Subscribe"
 *   
 *  @struts.action path="/ForgotPassword" parameter="forgotPassword" validate="false"
 *   scope="request" input="/Welcome.do"
 * @struts.action-forward name="forgotPassword" path="/ForgotPassword.jsp"
 * @spring.bean name="/ForgotPassword"
 * 
 * @struts.action path="/LookupPassword" parameter="lookupPassword" validate="false"
 *   name="lookupPasswordForm" scope="request" input="/ForgotPassword.do"
 * @struts.action-forward name="passwordSent" path="/PasswordSent.jsp"
 * @spring.bean name="/LookupPassword"
 * 
 * @struts.action path="/SignupUser" parameter="signup" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="signup" path="/PreSignup.do"
 * @struts.action-forward name="welcome" path="/Welcome.jsp"
 * @spring.bean name="/SignupUser"
 *
 * @struts.action path="/EnableUser" parameter="enableUser" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUsers" path="/ListUsers.jsp"
 * @spring.bean name="/EnableUser"
 * 
 * @struts.action path="/RemoveUser" parameter="removeUser" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUsers" path="/ListUsers.jsp"
 * @spring.bean name="/RemoveUser"
 * 
 * @struts.action path="/AdminUser" parameter="adminUser" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUsers" path="/ListUsers.jsp"
 * @spring.bean name="/AdminUser"
 * 
 * @struts.action path="/PremiumUser" parameter="premiumUser" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUsers" path="/ListUsers.jsp"
 * @spring.bean name="/PremiumUser"
 * 
 * @struts.action path="/DuplicateUser" parameter="duplicateUser" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @spring.bean name="/DuplicateUser"
 * 
 * @struts.action path="/RefetchPages" parameter="refetchPages" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @spring.bean name="/RefetchPages"
 * 
 * @struts.action path="/ImageAction" parameter="imageAction" validate="false"
 *   scope="request" input="/EditUser.do"
 * @spring.bean name="/ImageAction"
 * 
 * @struts.action path="/RemoveImage" parameter="removeImage" validate="false"
 *   scope="request" input="/ImageUpload.do"
 * @struts.action-forward name="showImages" path="/ImageUpload.jsp"
 * @spring.bean name="/RemoveImage"
 * 
 * @struts.action path="/SendWebStats" parameter="sendWebStats" validate="false"
 * @spring.bean name="/SendWebStats"
 * 
 * @struts.action path="/MakeFirst" parameter="makeFirst" validate="false"
 *   scope="request" input="/ImageUpload.do"
 * @spring.bean name="/MakeFirst"
 * 
 * @struts.action path="/ListUsers" parameter="listUsers" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUsers" path="/ListUsers.jsp"
 * @spring.bean name="/ListUsers"
 * 
 * @struts.action path="/ListUserConfigs" parameter="listUserConfigs" validate="false"
 *   name="userConfigsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listUserConfigs" path="/ListUserConfigs.jsp"
 * @spring.bean name="/ListUserConfigs"
 * 
 * @struts.action-forward name="notLoggedIn" path="/Login.jsp"
 * @struts.action-forward name="unauthorized" path="/Unauthorized.jsp"
 * @struts.action-forward name="controls" path="/Controls.jsp"
 * 
 * @spring.property name="groupService" ref="groupService"
 * @spring.property name="userConfigService" ref="userConfigService"
 * @spring.property name="photoService" ref="photoService"
 * @spring.property name="alertService" ref="alertService"
 * @spring.property name="crawlerService" ref="crawlerService"
 * 
 * @author Jeff
 *
 */
public class UserAction extends BaseAction {

	private GroupService	groupService;
	private PhotoService	photoService;
	private AlertService	alertService;
	private CrawlerService	crawlerService;
	private UserConfigService userConfigService;
	
	public CrawlerService getCrawlerService() {
		return crawlerService;
	}

	public void setCrawlerService(CrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}

	public UserConfigService getUserConfigService() {
		return userConfigService;
	}

	public void setUserConfigService(UserConfigService userConfigService) {
		this.userConfigService = userConfigService;
	}

	public AlertService getAlertService() {
		return alertService;
	}

	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}

	public PhotoService getPhotoService() {
		return photoService;
	}

	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ActionForward listUsers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));

		List<User>	users = userService.listUsers(null);

		request.setAttribute("users", users);
		return (mapping.findForward("listUsers"));
	}

	public ActionForward listUserConfigs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));
		
		List<UserConfig>	configs = userConfigService.list(null);

		request.setAttribute("userConfigs", configs);
		return (mapping.findForward("listUserConfigs"));
	}

	public ActionForward editUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		UserForm	uf = (UserForm)form;
		User	user = getUserFromSession(request);
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		
		String	dnb = (String)request.getAttribute("doNotBind");
		if (dnb == null)
			uf.bind(user);
		
		request.setAttribute("friendsList", user.getFriends());
		request.setAttribute("groupList", user.getGroups());
		request.setAttribute("photoList", user.getPhotoList());
		
		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		for (int i = 0; i < 10; i++) 
			list.add(new IdNameProjection(String.valueOf(i), String.valueOf(i)));
		request.setAttribute("smallNumList", list);

		request.setAttribute("selTab", Features.myAccount);
		return (mapping.findForward("editUser"));
	}

	public ActionForward sendWebStats(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String  urlStr = request.getParameter("url");
		String  numKeystrokes = request.getParameter("numKeystrokes");
		String	time = request.getParameter("time");
		
		// TODO: 
		//String	cookieData = request.getParameter("cookieData");
		
		PrintWriter out = response.getWriter();

		User	user = getUserFromSession(request);
		
		if (user == null) {
			out.println(getInfoMessage(request));
			return (null);
		}

		if (WebUtils.isUnsupportedFormat(urlStr)) {
			out.println(getUnsupportedFormatMessage(request));
			return (null);
		}

		String	userId = user.getUserId();
		
		URL     url;
		try {
			url = Utils.normalizeUrl(new URL(urlStr));
		}
		catch (Exception e) { 
			out.print("<response><rating>PROBLEM: " + e.getLocalizedMessage() + 
					"</rating><userId>" + user.getUserId() + "</userId></response>");
			return (null);
		}
		
		int	numKeystrokesInt = 0;
		try {
			numKeystrokesInt = Integer.parseInt(numKeystrokes);
		}
		catch (Exception e) {
			out.print("<response><status>PROBLEM: " + e.getLocalizedMessage() + 
					"</status><userId>" + user.getUserId() + "</userId></response>");
			return (null);			
		}

		int	timeInt = 0;
		try {
			timeInt = Integer.parseInt(time);
		}
		catch (Exception e) {
			out.print("<response><status>PROBLEM: " + e.getLocalizedMessage() + 
					"</status><userId>" + user.getUserId() + "</userId></response>");
			return (null);			
		}

		userService.updateWebStats(user, url.toString(), numKeystrokesInt, timeInt);
		out.print("<response><status>Web stats updated.</status><userId>" + user.getUserId() + "</userId></response>");
		out.flush();
		return null;
	}

	public ActionForward editUserConfig(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		UserConfigForm	uf = (UserConfigForm)form;
		User	user = getUserFromSession(request);
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		
		String	dnb = (String)request.getAttribute("doNotBind");
		if (dnb == null) {
			String	name = request.getParameter("name");
			UserConfig	uc;
			if (StringUtils.isNotEmpty(name)) 
				uc = userConfigService.findByName(name);
			else
				uc = user.getUserConfig();
			uf.bind(uc);
		}
		
		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		for (int i = 0; i < 10; i++) 
			list.add(new IdNameProjection(String.valueOf(i), String.valueOf(i)));
		request.setAttribute("smallNumList", list);

		return (mapping.findForward("editUserConfig"));
	}

	public ActionForward imageAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	userId = request.getParameter("userId");
		Long	imageId = new Long(request.getParameter("imageId"));
		User	user = userService.findUser(userId);
		List<Photo>	photos = user.getPhotoList();
		for (Photo p : photos) {
			if (p.getId().equals(imageId)) {
				Photo	photo = photoService.findById(imageId);
				byte []	imgBytes = photo == null ? null : photo.getData();
				response.getOutputStream().write(imgBytes);
				response.getOutputStream().flush();
				break;
			}
		}
		return (null);
	}
	
	public ActionForward viewUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	userId = request.getParameter("userId");
		User	loggedIn = getUserFromSession(request);
		User	user;
		if (StringUtils.isEmpty(userId) && loggedIn != null) {
			userId = loggedIn.getUserId();
			user = loggedIn;
		}
		else
			user = userService.findUser(userId);
		
		if (user.getPrivacy() == Privacy.PUBLIC) {
			int		size = 20;
			String	sortCol = null;
			String	dir = null;
			ListInfo	info = new ListInfo();
			if (StringUtils.isNotEmpty(sortCol)) {
				info.setSortCols(new String [] {sortCol});
				info.setAscendings(new boolean [] {dir != null && dir.equals("asc") ? true : false});
			}
			info.setEquals(new String [] {userId});
			info.setEqualsCols(new String [] {"user.userId"});
			info.setMaxResults(size);
			
			RecentStats	 rstats = ratingService.getRecentStats(info);
			request.setAttribute("recentStats", rstats); 
		}
		
		boolean	diffUser = loggedIn != null && !loggedIn.getId().equals(user.getId());
		boolean	canMakeFriends = diffUser && !user.isFriendsWith(loggedIn.getUserId());
		String	rawCategories = user.getCategorySet().getRawCategories();
		
		boolean	canInherit;
		
		if (diffUser) {
			canInherit = true;
			CategorySet	userSet = user.getCategorySet();
			CategorySet	loggedInUserSet = loggedIn.getCategorySet();
			for (String userCat : userSet.getCategories()) {
				if (!loggedInUserSet.contains(userCat)) {
					canInherit = false;
					break;
				}
			}
		}
		else
			canInherit = false;
		
		request.setAttribute("viewUserCategories", rawCategories);
		request.setAttribute("canSendMessage", diffUser);
		request.setAttribute("canInheritRatings", canInherit);
		request.setAttribute("canMakeFriends", canMakeFriends);
		request.getSession().setAttribute("viewUser", user);
		request.setAttribute("friendsList", user.getFriends());
		request.setAttribute("groupList", user.getGroups());
		request.setAttribute("photoList", user.getPhotoList());
		return (mapping.findForward("viewUser"));
	}

	public ActionForward addPhoto(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		request.setAttribute("photoList", user.getPhotoList());
		return (mapping.findForward("addPhoto"));
	}
	
	public ActionForward checkMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		request.setAttribute("msgs", user.getMsgList());
		request.setAttribute("viewUser", user);
		return (mapping.findForward("checkMail"));
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		user.setPremium(false);
		userService.save(user);
		return (mapping.findForward("cancel"));
	}

	public ActionForward subscribe(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		user.setPremium(true);
		userService.save(user);
		return (mapping.findForward("subscribe"));
	}

	public ActionForward alerts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		AlertsForm	af = (AlertsForm)form;
		
		List<Alert>	alerts = user.getAlertsList();
		af.bind(alerts);
		int	numAlerts = af.getNumAlerts();
		
		List<AlertResult>	results = user.getAlertResultsList();
		
		request.setAttribute("alertsFeedUrl", getAlertsFeedUrl(request, user));
		
		request.setAttribute("selTab", Features.alerts);
		request.setAttribute("numAlerts", numAlerts);
		request.setAttribute("results", results);
		return (mapping.findForward("alerts"));
	}

	public ActionForward feeds(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		FeedsForm	af = (FeedsForm)form;
		af.bind(user.getFeedsList(), getCrawlersFeedUrl(request, user));
		
		request.setAttribute("selTab", Features.feeds);

		return (mapping.findForward("feeds"));
	}

	public ActionForward crawls(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		CrawlsForm	af = (CrawlsForm)form;
		
		List<CrawlerImpl>	crawlers = user.getCrawlersList();
		af.bind(crawlers);
		int	numCrawlers = af.getNumCrawlers();
		
		List<CrawlerResult>	results = new ArrayList<CrawlerResult>();
		
		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		for (CrawlingIntensity i : CrawlingIntensity.values()) {
			list.add(new IdNameProjection(i.toString(), i.toString()));
		}
		request.setAttribute("intensities", list);
		
		request.setAttribute("selTab", Features.crawls);
		request.setAttribute("numCrawlers", numCrawlers);
		
		if (crawlers != null) {
			for (CrawlerImpl ci : crawlers) {
				results.addAll(ci.getCrawledResults());
			}
		}
		
		request.setAttribute("results", results);
		return (mapping.findForward("crawls"));
	}

	public ActionForward saveCrawls(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		CrawlsForm	af = (CrawlsForm)form;

		ActionMessages	errors = getErrors(request);
		af.validateCrawlers(errors, mapping, request);
		saveErrors(request, errors);
		
		if (errors.size() > 0) 
			return (mapping.getInputForward());

		List<Long>	toDelete = af.extractCrawlersToDelete(user.getCrawlersList());
		userService.removeCrawlers(user, toDelete);
		List<CrawlerImpl>	crawlers = af.extractCrawlers(user.getCrawlersList());
		userService.saveCrawlers(user, crawlers); 

		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		for (CrawlingIntensity i : CrawlingIntensity.values()) {
			list.add(new IdNameProjection(i.toString(), i.toString()));
		}
		request.setAttribute("intensities", list);
		
		request.setAttribute("selTab", Features.crawls);

		if (crawlers != null) {
			for (CrawlerImpl ci : crawlers) {
				if (StringUtils.isNotEmpty(af.getClearIt())) {
					crawlerService.deleteResults(ci, ci.getCrawledResults());
				}
			}
		}

		if (StringUtils.isNotEmpty(af.getDoit())) {
			crawlerService.runCrawlersFor(user);
			request.setAttribute("didIt", true);
		}
		List<CrawlerResult>	results = new ArrayList<CrawlerResult>();
		
		if (crawlers != null) {
			for (CrawlerImpl ci : crawlers) {
				results.addAll(ci.getCrawledResults());
			}
		}
		request.setAttribute("results", results);
		return (mapping.findForward("crawls"));
	}

	public ActionForward alertsFeed(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = null;

		String	userId = request.getParameter("userId");
		String	userCode = request.getParameter("userCode");
		if (userId != null)
			userId = URLDecoder.decode(userId, "UTF-8");
		if (userCode != null)
			userCode = URLDecoder.decode(userCode, "UTF-8");
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/rss+xml");

		String	errorFeedMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel><title>WebPredict RSS Feed - invalid user code</title></channel></rss>" + WebUtils.NL;
		if (StringUtils.isNotEmpty(userId)) {
			user = userService.findUser(userId);
			if (user != null && user.isObfuscateFeed()) {
				out.println(errorFeedMsg);
				out.flush();
				return (null);
			}
		}
		else if (StringUtils.isNotEmpty(userCode))
			user = userService.findUserByCode(userCode);
		
		StringBuffer	buf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel>" + WebUtils.NL);

		if (user != null) {
			String	feedUrl = getAlertsFeedUrl(request, user);
			
			if (user.isObfuscateFeed()) 
				buf.append("<title>WebPredict RSS Feed</title>" + WebUtils.NL);
			else 
				buf.append("<title>WebPredict RSS Feed for " + userId + "</title>" + WebUtils.NL);		
			
			buf.append("<link>" + feedUrl + "</link>" + WebUtils.NL);
			buf.append("<description>Daily RSS feed of interesting links.</description>" + WebUtils.NL);

			List<AlertResult>	results = user.getAlertResultsList();
			if (results != null) {
				for (AlertResult result : results) {
					buf.append("<item>" + WebUtils.NL);
					buf.append("<title>" + StringEscapeUtils.escapeHtml(result.getTitle()) + "</title>" + WebUtils.NL);
					buf.append("<link>" + result.getUrlStr() + "</link>" + WebUtils.NL);
					
					//Sun, 19 May 2002 15:21:36 GMT
					//"EEE, d MMM yyyy HH:mm:ss Z"
					
					Date	timestamp = result.getTimestamp();
					if (timestamp != null) {
						buf.append("<pubDate>" + RSS_DATE.format(timestamp) + "</pubDate>" + WebUtils.NL);
					}
					String	keywords = result.getContains();
					String	prediction = result.getPrediction();
					//if (prediction != null)
						//buf.append("<comments>This page predicted as " + prediction + ".</comments>" + WebUtils.NL);
					StringBuffer	buf2 = new StringBuffer("[");
					if (keywords != null)
						buf2.append("contains: " + keywords + "; ");
					if (prediction != null)
						buf2.append("predicts as: " + prediction);
						
					buf2.append("] ");
					
					buf.append("<description>" + StringEscapeUtils.escapeHtml(buf2.toString() + result.getSnippet()) + "</description>" + WebUtils.NL);
					buf.append("</item>" + WebUtils.NL);			
				}
			}
		}
		else {
			buf.append("<title>WebPredict RSS Feed - invalid user code</title>" + WebUtils.NL);
		}
		buf.append("</channel></rss>");
		out.println(buf.toString());
		out.flush();
		
		return (null);
	}

	public ActionForward crawlersFeed(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = null;

		String	userId = request.getParameter("userId");
		String	userCode = request.getParameter("userCode");
		if (userId != null)
			userId = URLDecoder.decode(userId, "UTF-8");
		if (userCode != null)
			userCode = URLDecoder.decode(userCode, "UTF-8");
		String	feedName = request.getParameter("feed");
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/rss+xml");

		String	errorFeedMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel><title>WebPredict RSS Feed - invalid user code</title></channel></rss>" + WebUtils.NL;
		if (StringUtils.isNotEmpty(userId)) {
			user = userService.findUser(userId);
			if (user != null && user.isObfuscateFeed()) {
				out.println(errorFeedMsg);
				out.flush();
				return (null);
			}
		}
		else if (StringUtils.isNotEmpty(userCode))
			user = userService.findUserByCode(userCode);
		
		Feed	feed = null;
		if (StringUtils.isNotEmpty(feedName) && user != null) 
			feed = feedDao.findByUserAndName(user.getId(), feedName);

		if (feed == null) {
			out.println(errorFeedMsg);
			out.flush();
			return (null);
		}
		
		StringBuffer	buf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel>" + WebUtils.NL);

		if (user != null) {
			String	feedUrl = getAlertsFeedUrl(request, user);
			
			if (user.isObfuscateFeed()) 
				buf.append("<title>WebPredict RSS Feed " + feed.getName() + "</title>" + WebUtils.NL);
			else 
				buf.append("<title>WebPredict RSS Feed " + feed.getName() + " for " + userId + "</title>" + WebUtils.NL);		
			
			buf.append("<link>" + feedUrl + "</link>" + WebUtils.NL);
			buf.append("<description>Daily RSS feed of interesting links.</description>" + WebUtils.NL);

			int	frequency = feed.getFrequency();
			Date	lastEmitted = feed.getLastEmittedTime();
			boolean	doAnother = (lastEmitted == null || (System.currentTimeMillis() - lastEmitted.getTime()) > frequency * 1000l);
				
			ArrayList<String>	items = new ArrayList<String>();
			List<CrawlerImpl>	crawlers = user.getCrawlersList();
			boolean	stop = false;
			if (crawlers != null && !stop) {
				for (CrawlerImpl ci : crawlers) {
					List<CrawlerResult>	results = ci.getCrawledResults();
					if (stop)
						break;
					
					for (CrawlerResult result : results) {
						Date	timestamp = result.getTimestamp();
						
						if (feed.getLatest() != null && timestamp != null && timestamp.after(feed.getLatest()))
							continue;
						if (feed.getEarliest() != null && timestamp != null && timestamp.before(feed.getEarliest()))
							continue;

						StringBuffer	tmpBuf = new StringBuffer();
						if (result.isPosted() || doAnother) {
							tmpBuf.append("<item>" + WebUtils.NL);
							String	title = result.getTitle();
							if (StringUtils.isNotEmpty(title))
								title = StringEscapeUtils.escapeHtml(title);
							else
								title = "";
							tmpBuf.append("<title>" + title + "</title>" + WebUtils.NL);
							tmpBuf.append("<link>" + result.getUrlStr() + "</link>" + WebUtils.NL);
							
							//Sun, 19 May 2002 15:21:36 GMT
							//"EEE, d MMM yyyy HH:mm:ss Z"
							
							if (timestamp != null) {
								tmpBuf.append("<pubDate>" + RSS_DATE.format(timestamp) + "</pubDate>" + WebUtils.NL);
							}
							String	keywords = result.getContains();
							String	prediction = result.getPrediction();
							StringBuffer	buf2 = new StringBuffer("[");
							if (StringUtils.isNotEmpty(keywords))
								buf2.append("contains: " + keywords + "; ");
							if (prediction != null)
								buf2.append("predicts as: " + prediction);
								
							buf2.append("] ");
							
							String	snippet = result.getSnippet();
							if (snippet != null && snippet.length() > feed.getSnippetSize())
								snippet = snippet.substring(0, feed.getSnippetSize()) + "...";
							
							tmpBuf.append("<description>" + StringEscapeUtils.escapeHtml(buf2.toString() + snippet) + "</description>" + WebUtils.NL);
							tmpBuf.append("</item>" + WebUtils.NL);
							
							items.add(tmpBuf.toString());
						}
						
						if (!result.isPosted()) {
							if (doAnother) {
								feed.setLastEmittedTime(new Date());
								feedDao.save(feed);
								
								result.setPosted(true);
								crawlerService.save(result);
							}
							stop = true;
							break;
						}
						
					}
				}
			}
			
			for (int i = items.size() - 1; i >= 0; i--)
				buf.append(items.get(i));
		}
		else {
			buf.append("<title>WebPredict RSS Feed - invalid user code</title>" + WebUtils.NL);
		}
		buf.append("</channel></rss>");
		out.println(buf.toString());
		out.flush();
		
		return (null);
	}

	public static SimpleDateFormat	RSS_DATE = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
	
	public ActionForward saveAlerts(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		AlertsForm	af = (AlertsForm)form;

		ActionMessages	errors = getErrors(request);
		af.validateAlerts(errors, mapping, request);
		saveErrors(request, errors);
		
		if (errors.size() > 0) 
			return (mapping.getInputForward());

		List<Long>	toDelete = af.extractAlertsToDelete(user.getAlertsList());
		userService.removeAlerts(user, toDelete);
		List<Alert>	alerts = af.extractAlerts(user.getAlertsList());
		userService.saveAlerts(user, alerts);
		
		if (StringUtils.isNotEmpty(af.getDoit())) {
			List<AlertResult>	alertResults = alertService.runAlertsFor(user);
			request.setAttribute("didIt", true);
		}
		
		if (StringUtils.isNotEmpty(af.getClearIt())) {
			alertService.deleteResults(user, user.getAlertResultsList());
		}
		List<AlertResult>	results = user.getAlertResultsList();
		
		request.setAttribute("alertsFeedUrl", getAlertsFeedUrl(request, user));
		
		request.setAttribute("results", results);
		return (mapping.findForward("alerts"));
	}

	public ActionForward saveFeeds(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		FeedsForm	af = (FeedsForm)form;

		ActionMessages	errors = getErrors(request);
		af.validateFeeds(errors, mapping, request);
		saveErrors(request, errors);
		
		if (errors.size() > 0) 
			return (mapping.getInputForward());

		List<Long>	toDelete = af.extractFeedsToDelete(user.getFeedsList());
		List<Feed>	feeds = af.extractFeeds(user.getFeedsList());
		userService.removeFeeds(user, toDelete);
		userService.saveFeeds(user, feeds);
		
		af.bind(user.getFeedsList(), getCrawlersFeedUrl(request, user));
		
		request.setAttribute("feedsFeedUrl", getCrawlersFeedUrl(request, user));
		return (mapping.findForward("feeds"));
	}

	private String	getAlertsFeedUrl (HttpServletRequest request, User user) throws Exception {
		String	path = Rater.getTheRater().getServerName() + request.getContextPath();
		path = path.replaceAll("https", "http");
		
		if (user.isObfuscateFeed())
			return (path + "/AlertsRSS.do?userCode=" + user.getCode());
		else
			return (path + "/AlertsRSS.do?userId=" + URLEncoder.encode(user.getUserId(), "UTF-8"));
	}

	private String	getCrawlersFeedUrl (HttpServletRequest request, User user) throws Exception {
		String	path = Rater.getTheRater().getServerName() + request.getContextPath();
		path = path.replaceAll("https", "http");
		
		if (user.isObfuscateFeed())
			return (path + "/CrawlersRSS.do?userCode=" + user.getCode());
		else
			return (path + "/CrawlersRSS.do?userId=" + URLEncoder.encode(user.getUserId(), "UTF-8"));
	}

	public ActionForward forgotPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (mapping.findForward("forgotPassword"));
	}

	public ActionForward lookupPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		LookupPasswordForm	lpf = (LookupPasswordForm)form;
		String	username = lpf.getUsername();
		String	emailAddr = lpf.getEmailAddr();
		ActionMessages	errors = getErrors(request);
		if (StringUtils.isEmpty(emailAddr)) {
			errors.add("emailAddr", new ActionMessage("error.invalidEmail", emailAddr));
			saveErrors(request, errors);
		}

		if (StringUtils.isEmpty(username)) {
			errors.add("userId", new ActionMessage("error.invalidUserId", username));
			saveErrors(request, errors);
		}

		if (errors.size() > 0) 
			return (mapping.getInputForward());
		
		User	user = userService.findUser(username);
		if (user == null || !user.getEmailAddr().equals(emailAddr)) {
			errors.add("userId", new ActionMessage("error.invalidUserEmailCombo", username));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		StringBuffer	buf = new StringBuffer("<html><body><br />Here is your WebPredict password for username ");
		buf.append(username);
		buf.append(": ");
		buf.append(user.getPassword());
		buf.append(".<br /><br />");
		buf.append("</body></html>");
		
		try {
			WebUtils.sendMail("Your WebPredict Password", buf.toString(), "feedback@webpredict.net", emailAddr);
		}
		catch (Exception e) {
			errors.add("mail", new ActionMessage("error.systemError", e.toString()));
			saveErrors(request, errors);
		}
		return (mapping.findForward("passwordSent"));
	}

	public ActionForward confirmSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	code = request.getParameter("code");
		String	userId = URLDecoder.decode(request.getParameter("userId"), "UTF-8");
//		if (!StringUtils.isEmpty(userId) && userService.checkUserConfirmCode(userId, code)) {
//			request.getSession().setAttribute("userId", userId);
//			setupCollections(request.getSession(), request);
//			return (mapping.findForward("signup"));
//		}
//		else 
//			return (mapping.findForward("signupError"));
		
		// TEMP: Don't verify conf. code for now:
		request.setAttribute("userId", userId);
		return (mapping.findForward("confirmed"));
	}

	public ActionForward simpleSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (preSignup(mapping, form, request, response, true));
	}

	public ActionForward preSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (preSignup(mapping, form, request, response, false));
	}
	
	public ActionForward preSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, boolean simple)
	throws Exception
	{
		PreSignupForm	psf = (PreSignupForm)form;
		
		ActionMessages	errors = getErrors(request);
		String	newUserId = psf.getNewusername();
		String	emailAddr = psf.getEmailAddr();
		String	password = psf.getPassword();
	
		String	userConfig = request.getParameter("userConfigId");
		boolean	errorCheck = StringUtils.isEmpty(userConfig) && !simple;
		
		if (StringUtils.isNotEmpty(userConfig)) {
			psf.setUserConfigId(userConfig);
			psf.setChoice("optionUserConfig");
		}
		
		psf.setPassword("");
		
		if (errorCheck) {
			if (userService.findUser(newUserId) != null) {
				errors.add("newusername", new ActionMessage("error.userAlreadyExists", newUserId));
				saveErrors(request, errors);
			}
			if (StringUtils.isEmpty(password)) {
				errors.add("password", new ActionMessage("error.invalidPassword", password));
				saveErrors(request, errors);
			}
			if (StringUtils.isEmpty(emailAddr)) {
				errors.add("emailAddr", new ActionMessage("error.invalidEmail", emailAddr));
				saveErrors(request, errors);
			}
		}
		request.setAttribute("noMenus", simple);
		return (mapping.findForward(simple ? "simpleSignup" : "preSignup"));
	}

	public ActionForward finishSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (finishSignup(mapping, form, request, response, false));
	}

	public ActionForward finishSimpleSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (finishSignup(mapping, form, request, response, true));
	}
	
	private ActionForward finishSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, boolean simple)
	throws Exception
	{
		PreSignupForm	psf = (PreSignupForm)form;
		
		ActionMessages	errors = getErrors(request);
		String	newUserId = psf.getNewusername();
		String	emailAddr = psf.getEmailAddr();
		String	password = psf.getPassword();
		String	ratingsCategories = psf.getRatingsCategories();
		Integer	maxRating = null;
		
		if (StringUtils.isEmpty(password)) {
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
		}
		if (!simple && StringUtils.isEmpty(emailAddr)) {
			errors.add("emailAddr", new ActionMessage("error.invalidEmail", emailAddr));
			saveErrors(request, errors);
		}
		
		if (Utils.hasVulgarities(newUserId)) {
			errors.add("newusername", new ActionMessage("error.vulgarities", newUserId));
			saveErrors(request, errors);
		}
		if (Utils.hasVulgarities(ratingsCategories)) {
			errors.add("ratingsCategories", new ActionMessage("error.vulgarities", ratingsCategories));
			saveErrors(request, errors);
		}
		
		UserConfig	uc = null;
		String	userConfigId = psf.getUserConfigId();
		if (StringUtils.isNotEmpty(userConfigId)) {
			uc = userConfigService.findByName(userConfigId);
			if (uc == null) {
				errors.add("userConfigId", new ActionMessage("error.unknownUserConfig", userConfigId));
				saveErrors(request, errors);
			}
		}
		
		String	forwardPageName = simple ? "simpleSignup" : "preSignup";
		if (errors.size() > 0) {
			return (mapping.findForward(forwardPageName));
		}
		
		boolean	ratingsPublic = psf.isRatingsPublic();
		
		User	user = null;
		try {
			user = userService.createUser(newUserId, maxRating, emailAddr, password, ratingsCategories, false, false, null, ratingsPublic);		
			user.setDisabled(false);
			
			/**
			 * TEMPORARY: for now, let's make all new users premium:
			 */
			user.setPremium(true);
			
			if (uc == null) {
				// TODO: decide what should be the defaults here after some more testing:
				user.setIgnoreCapitalization(true);
				user.setCountRepeats(true);
				user.setWeightLinks(true);
				user.setHeadlinesWeight(3);
				user.setSmoothProbs(true);
				user.setUsePreciseWordProb(true);
			}
			else {
				user.initWithUserConfig(uc);
			}
			userService.save(user);
		}
		catch (AlreadyExistsException e) {
			errors.add("newusername", new ActionMessage("error.userAlreadyExists", newUserId));
			saveErrors(request, errors);
			return (mapping.findForward(forwardPageName));
		}
		catch (EmailWithOtherUserException e) {
			errors.add("emailAddr", new ActionMessage("error.emailAddrInUse", emailAddr));
			saveErrors(request, errors);
			return (mapping.findForward(forwardPageName));
		}
		
		String	inheritFromUserId = psf.getAnonLoginId();
		if (!StringUtils.isEmpty(inheritFromUserId)) 
			userService.inheritRatingsFrom(inheritFromUserId, user);
		
		String	code = userService.generateNewUserConfirmCode(newUserId);
		StringBuffer	buf = new StringBuffer("<html><body><br />This is your WebPredict signup email for your user name: ");
		buf.append(newUserId);
		
		String	path = Rater.getTheRater().getServerName() + request.getContextPath();
		buf.append(".<br /><br />To confirm your new membership, click <a href='" + path + "/ConfirmSignup.do?code=");
		buf.append(code);
		buf.append("&userId=");
		buf.append(URLEncoder.encode(newUserId, "UTF-8"));
		buf.append("'>here</a>.<br /><br />");
		buf.append("Once you've confirmed your membership, you can immediately log in and start rating and predicting links, " + 
				"either via the control panel or via the Firefox add-on.<br /><br />");
		buf.append("You can download the Firefox Rater and Predictor add-on <a href='" + path + "/Download.do'>here</a>.<br />");
		buf.append("</body></html>");
		//helper.setText(buf.toString(), true); 
		
		// use the true flag to indicate the text included is HTML
		//helper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);

		// let's include the infamous windows Sample file (this time copied to c:/)
		//FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
		//helper.addInline("identifier1234", res);

		//sender.send(message);
		
		try {
			if (StringUtils.isNotEmpty(emailAddr))
				WebUtils.sendMail("Your WebPredict Signup Confirmation", buf.toString(), "feedback@webpredict.net", emailAddr);
		}
		catch (Exception e) {
			errors.add("mail", new ActionMessage("error.systemError", e.toString()));
			saveErrors(request, errors);
		}
		if (StringUtils.isNotEmpty(emailAddr))
			request.setAttribute("newUserEmail", user.getEmailAddr());
		
		if (simple)
			loginUser(request, user);
		return (mapping.findForward("finishSignup"));
	}
	
	public ActionForward signup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		String	newUserId = rf.getNewusername();

		Integer	maxRating = null;
		try {
			User	user = userService.createUser(newUserId, maxRating, rf.getEmailAddr(), rf.getPassword(), "", false, false, null, true);
			request.getSession().setAttribute("userId", newUserId);
			setupCollections(request.getSession(), request);
		}
		catch (AlreadyExistsException e) { 
			ActionMessages	errors = getErrors(request);

			errors.add("username", new ActionMessage("error.userAlreadyExists", newUserId));
			saveErrors(request, errors);
			return mapping.getInputForward();
		}		  	 

		return (mapping.findForward("signup"));
	}

	public ActionForward addFriends(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		AddFriendsForm	uf = (AddFriendsForm)form;
		List<User>	users = userService.listUsers(uf.getFilter());
		User	user = getUserFromSession(request);
		uf.setUserId(user.getUserId());
		
		List<AddUserInfo>	userInfo = new ArrayList<AddUserInfo>();
		if (users != null)
			for (User u : users) {
				if (u.getUserId().equals(user.getUserId()))
					continue; // skip self!
				AddUserInfo	info = new AddUserInfo();
				info.setUserId(u.getUserId());
				info.setName(u.getName());
				boolean	added = user.isFriendsWith(u.getUserId());
				info.setAlreadyAdded(added);
				userInfo.add(info);
			}
		
		if (!StringUtils.isEmpty(uf.getAddUserId())) {
			String	userId = uf.getAddUserId();
			userService.addFriend(user, userId);
			return (mapping.findForward("doneAddFriends"));
		}
		else if (!StringUtils.isEmpty(uf.getRemoveUserId())) {
			String	userId = uf.getRemoveUserId();
			userService.removeFriend(user, userId);
			return (mapping.findForward("doneAddFriends"));
		}
		request.setAttribute("friends", userInfo);
		return (mapping.findForward("addFriends"));
	}
	
	public ActionForward addFriend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		String	addUserId = request.getParameter("userId");
		
		if (!StringUtils.isEmpty(addUserId)) {
			userService.addFriend(user, addUserId);
			request.setAttribute("messages", "Friend added.");
			return (mapping.findForward("doneAddFriend"));
		}
		else
			return (mapping.getInputForward());
	}
	
	public ActionForward sendMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		MessageForm	mf = (MessageForm)form;
		String	subject = mf.getSubject();
		String	message = mf.getMessage();
		if (message != null)
			message = message.replaceAll(WebUtils.NL, " ").trim();
		
		String	toUserId = mf.getToUserId();
		if (!StringUtils.isEmpty(message)) {
			userService.sendMessage(user.getUserId(), toUserId, subject, message);
			request.setAttribute("messages", "Message sent.");
			return (mapping.findForward("doneSendMessage"));
		}
		else
			return (mapping.getInputForward());
	}
	
	public ActionForward copyRatings(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		String	inheritFromUserId = request.getParameter("toUserId");
		if (!StringUtils.isEmpty(inheritFromUserId)) {
			userService.inheritRatingsFrom(inheritFromUserId, user);
			request.setAttribute("messages", "You have successfully copied the ratings history of user " + inheritFromUserId + ".");
			return (mapping.findForward("doneInheritRatings"));
		}
		else 
			return (mapping.getInputForward());
	}

	public ActionForward inheritRatings(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		String	inheritFromUserId = request.getParameter("toUserId");
		if (!StringUtils.isEmpty(inheritFromUserId)) {
			user.setInheritedFrom(inheritFromUserId);
			userService.save(user);
			request.setAttribute("messages", "You have successfully inherited the ratings of user " + inheritFromUserId + ".");
			return (mapping.findForward("doneInheritRatings"));
		}
		else 
			return (mapping.getInputForward());
	}
	
	public ActionForward setupMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		MessageForm	mf = (MessageForm)form;
		mf.setToUserId(request.getParameter("toUserId"));
		return (mapping.findForward("sendMessage"));
	}
	
	public ActionForward addGroups(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		AddGroupsForm	agf = (AddGroupsForm)form;
		
		String	sortCol = request.getParameter("sort");
		String	dir = request.getParameter("dir");
		ListInfo	listInfo = new ListInfo();
		if (StringUtils.isNotEmpty(sortCol)) {
			listInfo.setSortCols(new String [] {sortCol});
			listInfo.setAscendings(new boolean [] {dir != null && dir.equals("asc") ? true : false});
		}
		String	filter = agf.getFilter();
		if (StringUtils.isNotEmpty(filter)) {
			listInfo.setLikes(new String [] {filter, filter});
			listInfo.setLikesOr(true);
			listInfo.setLikesCols(new String [] {"name", "groupId"});
		}
	
		request.setAttribute("antiDir", dir == null || dir.equals("desc") ? "asc" : "desc");
		
		List<Group>		groups = groupService.listGroups(listInfo);
		User	user = getUserFromSession(request);

		List<AddGroupInfo>	groupInfo = new ArrayList<AddGroupInfo>();
		if (groups != null)
			for (Group g : groups) {
				AddGroupInfo	info = new AddGroupInfo();
				info.setId(g.getId().toString());
				info.setGroupId(g.getGroupId());
				info.setDescription(g.getDescription());
				info.setName(g.getName());
				boolean	added = user.getGroups().contains(g);
				info.setAlreadyAdded(added);
				groupInfo.add(info);
			}
		
		if (!StringUtils.isEmpty(agf.getAddGroupId())) {
			String	groupId = agf.getAddGroupId();
			userService.addGroup(user, new Long(groupId));
			return (mapping.findForward("doneAddGroups"));
		}
		else if (!StringUtils.isEmpty(agf.getRemoveGroupId())) {
			String groupId = agf.getRemoveGroupId();
			userService.removeGroup(user, new Long(groupId));
			return (mapping.findForward("doneAddGroups"));
		}
		request.setAttribute("groups", groupInfo);
		return (mapping.findForward("addGroups"));
	}
	
	public ActionForward removeImage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		Long	imageId = new Long(request.getParameter("imageId"));
		
		userService.removePhoto(user, imageId);
		user = userService.findUser(user.getUserId()); // refresh
		request.setAttribute("photoList", user.getPhotoList());
		return (mapping.findForward("showImages"));
	}

	public ActionForward makeFirst(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		Long	imageId = new Long(request.getParameter("imageId"));
		
		List<Photo>	photos = user.getPhotoList();
		for (Photo p : photos) {
			if (p.getId().equals(imageId)) {
				p.setStamp(new Date());
				photoService.save(p);
				break;
			}
		}
		
		userService.save(user);
		user = userService.findUser(user.getUserId()); // refresh
		request.setAttribute("photoList", user.getPhotoList());
		return (mapping.findForward("showImages"));
	}

	public ActionForward saveUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	cancel = request.getParameter("cancel");
		if (StringUtils.isNotEmpty(cancel) && cancel.equals("true"))
			return (mapping.findForward("cancelSave"));
		
		UserForm	uf = (UserForm)form;
		String	password = uf.getPassword();
		String	emailAddr = uf.getEmailAddr();
		String	password2 = uf.getPassword2();
		
		ActionMessages	errors = getErrors(request);
		if (StringUtils.isEmpty(password)) {
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
		}
		if (!WebUtils.xequals(password, password2)) {
			errors.add("password", new ActionMessage("error.passwordMismatch", password));
			saveErrors(request, errors);
		}
		if (StringUtils.isEmpty(emailAddr)) {
			errors.add("emailAddr", new ActionMessage("error.invalidEmail", emailAddr));
			saveErrors(request, errors);
		}
		
		if (errors.size() > 0) {
			request.setAttribute("doNotBind", "true");
			return (mapping.getInputForward());
		}
		
		User	user = uf.extract(userService);
		userService.save(user);
		return (mapping.findForward("saveUser"));
	}


	public ActionForward saveUserConfig(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	cancel = request.getParameter("cancel");
		if (StringUtils.isNotEmpty(cancel) && cancel.equals("true"))
			return (mapping.findForward("cancelSaveConfig"));
		
		UserConfigForm	uf = (UserConfigForm)form;
		
		ActionMessages	errors = getErrors(request);
		if (errors.size() > 0) {
			request.setAttribute("doNotBind", "true");
			return (mapping.getInputForward());
		}
		
		UserConfig	userConfig = uf.extract(userConfigService);
		userConfigService.save(userConfig);
		return (mapping.findForward("saveUserConfig"));
	}


	public ActionForward enableUser (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (adminUserInt(mapping, form, request, response, 2));
	}
	
	public ActionForward refetchPages(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));
		else {
			String	userId = ((UsersForm)form).getUserId();
			UserStats	stats = ratingService.findUserStats(userId);
			HashMap<URL, RatedPage>	pages = stats.getRatedPagesMap();
			
			for (RatedPage rp : pages.values()) {
				rp.setTimestamp(new Date());
				ratingService.rate(userId, rp.getUrl(), rp.getRating(), rp.getComment(), null);
			}
		
			ratingService.save(stats);
		}
		List<User>	users = userService.listUsers(null);		  	
		request.setAttribute("users", users);
		return (mapping.findForward("listUsers"));
	}

	public ActionForward removeUser (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));
		else {
			String	userId = ((UsersForm)form).getUserId();
			User	userToRemove = userService.findUser(userId);
			userService.delete(userToRemove);
			List<User>	users = userService.listUsers(null);		  	
			request.setAttribute("users", users);
			return (mapping.findForward("listUsers"));
		}
	}
	
	public ActionForward duplicateUser (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));
		else {
			String	userId = ((UsersForm)form).getUserId();
			User	userToDup = userService.findUser(userId);
			
			
			String	newUserId = userToDup.getUserId();
			int		i = newUserId.length() - 1;
			while (i >= 0) {
				if (newUserId.charAt(i) < '0' || newUserId.charAt(i) > '9')
					break;
				i--;
			}
			
			String	num = "1"; 
			String	existingNum = i == newUserId.length() - 1 ? null : newUserId.substring(i + 1);
			if (StringUtils.isNotEmpty(existingNum)) {
				num = String.valueOf(Integer.parseInt(existingNum) + 1);
				newUserId = newUserId.substring(0, i + 1) + num;
			}
			else
				newUserId += num;
			
			String	ratingsCategories = userToDup.getCategorySet().getRawCategories();
			User	dupUser = userService.createUser(newUserId, userToDup.getMaxRating(), 
					newUserId, userToDup.getPassword(), ratingsCategories, false, false, null, true);		
			dupUser.setDisabled(false);
			dupUser.setName(userToDup.getName());
			dupUser.setPremium(userToDup.isPremium());
			dupUser.setIgnoreCapitalization(userToDup.isIgnoreCapitalization());
			dupUser.setCountRepeats(userToDup.isCountRepeats());
			dupUser.setWeightLinks(userToDup.isWeightLinks());
			dupUser.setHeadlinesWeight(userToDup.getHeadlinesWeight());
			dupUser.setSmoothProbs(userToDup.isSmoothProbs());
			userService.save(dupUser);
			
			List<User>	users = userService.listUsers(null);		  	
			request.setAttribute("users", users);
			return (mapping.findForward("listUsers"));
		}
	}
	
	public ActionForward adminUser (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (adminUserInt(mapping, form, request, response, 0));
	}

	private ActionForward adminUserInt (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, int propFlag)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = (User)session.getAttribute("user");

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else if (!user.isAdmin())
			return (userNotAuthorized(mapping, request));
		else{
			String	userId = ((UsersForm)form).getUserId();
			User	userToChange = userService.findUser(userId);
			switch (propFlag) {
			case 0:
				userToChange.setAdmin(!userToChange.isAdmin());
				break;
				
			case 1:
				userToChange.setPremium(!userToChange.isPremium());
				break;
				
			case 2:
				userToChange.setDisabled(!userToChange.isDisabled());
				break;
			
			}
			userService.save(userToChange);
			List<User>	users = userService.listUsers(null);		  	
			request.setAttribute("users", users);
			return (mapping.findForward("listUsers"));
		}
	}
	
	public ActionForward premiumUser (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (adminUserInt(mapping, form, request, response, 1));
	}

}
