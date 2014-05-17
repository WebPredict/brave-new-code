package wp.web;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.web.struts.MappingDispatchActionSupport;

import wp.core.IdNameProjection;
import wp.core.Rater;
import wp.dao.FeedDao;
import wp.model.Ad;
import wp.model.Advertiser;
import wp.model.CategorySet;
import wp.model.User;
import wp.model.UserStats;
import wp.service.AdService;
import wp.service.AdStatService;
import wp.service.AdvertiserService;
import wp.service.RatingService;
import wp.service.UserService;

/**
 * 
 * @spring.property name="ratingService" ref="ratingService"
 * @spring.property name="userService" ref="userService"
 * @spring.property name="feedDao" ref="feedDao"
 * @spring.property name="adService" ref="adService"
 * @spring.property name="adStatService" ref="adStatService"
 * @spring.property name="advertiserService" ref="advertiserService"
 *
 * @struts.action-forward name="notLoggedIn" path="/Login.jsp"
 * 
 * @author Jeff
 *
 */
public class BaseAction extends MappingDispatchActionSupport {

	protected RatingService ratingService;
	protected UserService	userService;
	protected AdService		adService;
	protected AdStatService	adStatService;
	private AdvertiserService	advertiserService;
	protected FeedDao	feedDao;
	
	
	private static Log log = LogFactory.getLog("wp.web.BaseAction");

	public static final String	INFO_MSG = "Info Message: You need to login to start rating or predicting pages. " + 
		"Go to <a style=\"color: white; font-weight: bold; text-decoration: underline; font-size: 18px\" href='SERVER_URL'>SERVER_URL</a>.";

	public static final String	INFO_MSG_PLAIN = "Info Message: You need to login to start rating or predicting pages. " +
		"Go to SERVER_URL.";

	public static final String	UNSUPPORTED_FORMAT_MSG = "Info Message: No rating or prediction was done for this link, because it's of an unsupported format " + 
		"(i.e., primarily image, video, sound, .pdf).";


	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws Exception 
	{
		StringBuffer	buf = new StringBuffer();
		HttpSession		session = request.getSession();
		String			id = (String)session.getAttribute("userId");
		if (id != null)
			buf.append("USER: " + id);
		else
			buf.append("ANONYMOUS");

		String			requestURI = request.getRequestURI();
		buf.append(" -- " + request.getRequestURI());
		
		if (false && id != null && requestURI.indexOf("ImageAction") == -1) {
			String	historyKey = "history" + id;
		
			Stack<String>	history = (Stack<String>)session.getAttribute(historyKey);
			if (history == null) 
				history = new Stack<String>();
		
			history.push(requestURI);
			session.setAttribute(historyKey, history);
		}
		
		log.info(buf.toString());
		request.setAttribute("addOnVersion", Rater.getTheRater().getAddOnVersion());
		
		Advertiser	adver = getAdvertiserFromSession(request);
		User	user = getUserFromSession(request);
		if (adver != null)
			request.setAttribute("showAdverTabs", true);
		else if (user != null) {
			Map<String, String>	featuresMap = user.getFeaturesMap();
			request.setAttribute("features", featuresMap);
			request.setAttribute("showUserTabs", true);
			request.setAttribute("premium", user.isPremium());
		}
		if (adver == null && user == null)
			request.setAttribute("showUserTabs", true);
		
		ActionForward	superForward = super.execute(mapping, form, request, response);

		return (superForward);
	}
	 
	protected void	loginUser (HttpServletRequest request, User user) throws SQLException {
		request.getSession().setAttribute("userId", user.getUserId());	 
		request.setAttribute("user", user);
		setupAll(request, user);
	}

	public FeedDao getFeedDao() {
		return feedDao;
	}

	public void setFeedDao(FeedDao feedDao) {
		this.feedDao = feedDao;
	}

	protected String	getInfoMessage (HttpServletRequest request) {
		return (INFO_MSG.replaceAll("SERVER_URL", 
				Rater.getTheRater().getServerName() + request.getContextPath()));
	}

	protected String	getUnsupportedFormatMessage (HttpServletRequest request) {
		return (UNSUPPORTED_FORMAT_MSG);
	}

	protected String	getInfoMessagePlain (HttpServletRequest request) {
		return (INFO_MSG_PLAIN.replaceAll("SERVER_URL", 
				Rater.getTheRater().getServerName() + request.getContextPath())); 
	}

	protected ActionForward	previousPage (HttpServletRequest request) {
		HttpSession		session = request.getSession();
		String			id = (String)session.getAttribute("userId");
		if (StringUtils.isEmpty(id))
			return (null);
		
		Stack<String>	history = (Stack<String>)session.getAttribute("history" + id);
	
		if (history == null || history.size() < 2)
			return (null);
		
		history.pop(); // current page
		String	prevPage = history.pop();
		int	lastSlash = prevPage.lastIndexOf('/');
		if (lastSlash == -1)
			return (null);
		
		return (new ActionForward(prevPage.substring(lastSlash)));
	}
	
	public AdvertiserService getAdvertiserService() {
		return advertiserService; 
	}

	public void setAdvertiserService(AdvertiserService advertiserService) {
		this.advertiserService = advertiserService;
	}

	public AdStatService getAdStatService() {
		return adStatService;
	}

	public void setAdStatService(AdStatService adStatService) {
		this.adStatService = adStatService;
	}

	public AdService getAdService() {
		return adService;
	}

 	public void setAdService(AdService adService) {
		this.adService = adService;
	}

	public RatingService getRatingService()
	{ 
		return ratingService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setRatingService(RatingService ratingService)
	{
		this.ratingService = ratingService;
	} 

	protected String	prepUrl (String urlFromForm) throws UnsupportedEncodingException {
		if (urlFromForm == null)
			return (null);
		if (!urlFromForm.startsWith("http://"))
			urlFromForm = "http://" + urlFromForm;

		return (urlFromForm);
		//return (URLDecoder.decode(urlFromForm, "UTF-8"));
	}

	protected User	getUserFromSession (HttpServletRequest request) throws SQLException {
		String	id = (String)request.getSession().getAttribute("userId");
		if (id != null)
			return (userService.findUser(id));
		return (null);
	}

	protected Advertiser	getAdvertiserFromSession (HttpServletRequest request) throws SQLException {
		Long	id = (Long)request.getSession().getAttribute("advertiserId");
		if (id != null)
			return (advertiserService.findById(id));
		return (null);
	}
	
	protected void	setupCollections (HttpSession session, HttpServletRequest request) throws SQLException {
		User	user = getUserFromSession(request);
		request.getSession().setAttribute("user", user);
		if (user != null) 
			request.setAttribute("ratingsOrderable", user.getCategorySet().isOrdered());		

		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		if (user != null) {
			List<String>	categories = user.getCategorySet().getCategories();
			for (String cat : categories) {
				list.add(new IdNameProjection(cat, cat));
			}

		}
		request.getSession().setAttribute("serverName", Rater.getTheRater().getServerName());
		request.setAttribute("ratings", list);
	}

	protected ActionForward	noUser (ActionMapping mapping, HttpServletRequest request, String userId) {
		ActionMessages	errors = getErrors(request);

		errors.add("username", new ActionMessage("error.userDoesntExist", userId));
		saveErrors(request, errors);
		return mapping.getInputForward();
	}

	protected ActionForward	noUserLoggedIn (ActionMapping mapping, HttpServletRequest request) {
		ActionMessages	errors = getErrors(request);

		errors.add("username", new ActionMessage("error.userNotLoggedIn"));
		
		String	pageInfo = request.getRequestURI();
		if (StringUtils.isNotEmpty(pageInfo)) {
			int	idx = pageInfo.lastIndexOf("/");
			pageInfo = pageInfo.substring(idx);
			request.setAttribute("pageTried", pageInfo);
		}
		saveErrors(request, errors);
		
		return (mapping.findForward("notLoggedIn"));
	}

	protected ActionForward	userNotAuthorized (ActionMapping mapping, HttpServletRequest request) {
		ActionMessages	errors = getErrors(request);

		errors.add("username", new ActionMessage("error.userNotAuthorized"));
		saveErrors(request, errors);
		return (mapping.findForward("unauthorized"));
	}
	
	protected void	setupAll (HttpServletRequest request, User user) throws SQLException {
		if (user != null) { 
			CategorySet	set = user.getCategorySet();
			request.setAttribute("ratingsOrderable", set.isOrdered());
			UserStats	stats = ratingService.findUserStats(user.getUserId());
			boolean	hasRatings = stats.getRatedPagesMap().size() > 0;
			
			boolean	hasCategories = set.getCategories() != null && set.getCategories().size() > 0;
			request.setAttribute("hasCategories", hasCategories);
			request.setAttribute("hasRatings", hasRatings);
			List<Ad>	ads = adService.getAdsFor(user, 4);		
			adStatService.impress(ads);
			request.setAttribute("ads", ads);
			request.setAttribute("user", user);
			request.setAttribute("hasMessages", user.getMsgList() != null && user.getMsgList().size() > 0);
		}
		setupCollections(request.getSession(), request);
	}
	
}
