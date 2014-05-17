package wp.web;

import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import wp.core.CachedURL;
import wp.core.RatedPagesComparator;
import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RecentStats;
import wp.core.SearchResult;
import wp.core.UserCloudInfo;
import wp.core.UserMatch;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.core.WordCloudInfo;
import wp.dao.ListInfo;
import wp.dao.SearchInfo;
import wp.model.Ad;
import wp.model.Features;
import wp.model.PageDiff;
import wp.model.ParsedPage;
import wp.model.Prediction;
import wp.model.Privacy;
import wp.model.RatedPage;
import wp.model.Recommendation;
import wp.model.User;
import wp.model.UserStats;
import wp.model.WordFreqPair;
import wp.service.SearchService;


/**
 * Get link rating in plain text format.
 *  
 * @struts.action path="/RateLink" parameter="rate" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 *   @struts.action-forward name="rate" path="/Rated.jsp"
 *   @struts.action-forward name="addRatingFirst" path="/AddRatingFirst.jsp"
 * @spring.bean name="/RateLink"  
 * 
 * @struts.action path="/BulkRateLinks" parameter="bulkRate" validate="false"
 *   name="bulkRatingForm" scope="request" input="/Controls.do"
 * @spring.bean name="/BulkRateLinks"  
 * 
 * @struts.action path="/RatePartial" parameter="ratePartial" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 *   @struts.action-forward name="loginNeeded" path="/Login.jsp"
 * @spring.bean name="/RatePartial"  
 * 
 * @struts.action path="/GetMenuItems" parameter="getMenuItems" validate="false"
 * @spring.bean name="/GetMenuItems"
 * 
 * @struts.action path="/RateContent" parameter="rateContent" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 *   @struts.action-forward name="rate" path="/Rated.jsp"
 * @spring.bean name="/RateContent"
 * 
 * @struts.action path="/AddCategoryAndRate" parameter="addAndRate" validate="false"
 *   name="addCategoryForm" scope="request" input="/RecentRatings.do"
 * @spring.bean name="/AddCategoryAndRate"
 * 
 * @struts.action path="/SubmitFeedback" parameter="submitFeedback" validate="false"
 *   name="feedbackForm" scope="request" input="/Welcome.do"
 *   @struts.action-forward name="feedbackSubmitted" path="/FeedbackSubmitted.jsp"
 * @spring.bean name="/SubmitFeedback"
 *
 * @struts.action path="/Feedback" parameter="feedback" validate="false"
 *   name="feedbackForm" scope="request" input="/Welcome.do"
 *   @struts.action-forward name="feedback" path="/Feedback.jsp"
 * @spring.bean name="/Feedback"
 * 
 * @struts.action path="/Login" parameter="login" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="loginControls" path="/Controls.do"
 * @spring.bean name="/Login"
 * 
 * @struts.action path="/LoginLanding" parameter="loginLanding" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="loginLanding" path="/SimpleLogin.jsp"
 * @spring.bean name="/LoginLanding"
 * 
 * @struts.action path="/SimpleLogin" parameter="simpleLogin" validate="false"
 *   name="ratingForm" scope="request" input="/LoginLanding.do"
 * @struts.action-forward name="simpleLoginSuccess" path="/SimpleLoginSuccess.jsp"
 * @spring.bean name="/SimpleLogin"
 * 
 * @struts.action path="/Logout" parameter="logout" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="logout" path="/Welcome.do"
 * @spring.bean name="/Logout"
 * 
 * @struts.action path="/PredictLink" parameter="predict" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 * @spring.bean name="/PredictLink"
 * 
 * @struts.action path="/ParsePage" parameter="parsePage" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="parsedPage" path="/ParsedPage.jsp"
 * @spring.bean name="/ParsePage"
 * 
 * @struts.action path="/PredictContent" parameter="predictContent" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 * @spring.bean name="/PredictContent"
 * 
 * @struts.action path="/ShowHistory" parameter="history" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="history" path="/UserHistory.jsp"
 * @spring.bean name="/ShowHistory"
 * 
 * @struts.action path="/WordCloud" parameter="wordCloud" validate="false"
 *   name="wordCloudForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="wordCloud" path="/WordCloud.jsp"
 * @spring.bean name="/WordCloud"
 * 
 * @struts.action path="/Search" parameter="search" validate="false"
 *   name="searchForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="searchResults" path="/SearchResults.jsp"
 * @spring.bean name="/Search"
 * 
 * @struts.action path="/RecentRatings" parameter="recentRatings" validate="false"
 *   name="recentRatingsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="recentRatings" path="/RecentPages.jsp"
 * @spring.bean name="/RecentRatings"
 * 
 * @struts.action path="/RecommendPages" parameter="recommend" validate="false"
 *   name="recommendForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="recommend" path="/RecommendedPages.jsp"
 * @spring.bean name="/RecommendPages"
 * 
 * @struts.action path="/FindSimilarUsers" parameter="findSimilarUsers" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="similarUsers" path="/SimilarUsers.jsp"
 * @spring.bean name="/FindSimilarUsers"
 * 
 * @struts.action path="/PageDiffs" parameter="diffs" validate="false"
 *   name="pageDiffsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="diffs" path="/PageDiffs.jsp"
 * @spring.bean name="/PageDiffs"
 * 
 * @struts.action path="/GetPageDiffs" parameter="computeDiffs" validate="false"
 *   name="pageDiffsForm" scope="request" input="/PageDiffs.do"
 * @spring.bean name="/GetPageDiffs"
 * 
 * @struts.action path="/GetGlobalStats" parameter="globalStats" validate="false"
 *   name="globalStatsForm" scope="request" input="/Controls.do"
 * @struts.action-forward name="globalStats" path="/GlobalStats.jsp"
 * @spring.bean name="/GetGlobalStats"
 * 
 * @struts.action path="/ChangeHistory" parameter="changeHistory" validate="false"
 *   name="historyForm" scope="request" input="/ShowHistory.do"
 * @spring.bean name="/ChangeHistory"
 * 
 * @struts.action path="/ShowAllRatings" parameter="showAllRatings" validate="false"
 *   scope="request" input="/Welcome.do"
 *  @struts.action-forward name="showRatings" path="/ShowRatings.jsp"
 * @spring.bean name="/ShowAllRatings"
 * 
 * @struts.action path="/ShowStats" parameter="stats" validate="false"
 *   name="ratingForm" scope="request" input="/Controls.do"
 *  @struts.action-forward name="stats" path="/UserStats.jsp"
 * @spring.bean name="/ShowStats"
 * 
 * @struts.action path="/PredictLinkAjax" parameter="predictAjax" validate="false"
 * @spring.bean name="/PredictLinkAjax"
 * 
 * @struts.action path="/AnonLogin" parameter="anonLogin" validate="false"
 * @spring.bean name="/AnonLogin"
 * 
 * @struts.action path="/AutoLogin" parameter="autoLogin" validate="false"
 * @spring.bean name="/AutoLogin"
 * 
 * @struts.action path="/RateLinkAjax" parameter="rateAjax" validate="false"
 * @spring.bean name="/RateLinkAjax"
 * 
 * @struts.action path="/Welcome" parameter="welcome" validate="false"
 * 	name="ratingForm" scope="request"
 * @struts.action-forward name="welcome" path="/Welcome.jsp"
 * @spring.bean name="/Welcome"
 * 
 * @struts.action path="/Download" parameter="download" validate="false"
 * 	name="ratingForm" scope="request"
 * @struts.action-forward name="download" path="/Download.jsp"
 * @spring.bean name="/Download"
 * 
 * @struts.action path="/Controls" parameter="controls" validate="false"
 * 	name="ratingForm" scope="request"
 * @struts.action-forward name="controls" path="/Controls.jsp"
 * @struts.action-forward name="controlsFull" path="/Controls.do"
 * @spring.bean name="/Controls"
 * 
 * 
 * @spring.property name="searchService" ref="searchService"
 * 
 */
public class RatingsAction extends BaseAction
{
	public static final int	PAGE_SIZE = 30;
	
	private SearchService	searchService;
	
	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)  
		throws Exception
	{	
		return (mapping.findForward("download"));
	}
	
	public ActionForward welcome(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)  
	throws Exception
	{	
		ListInfo	info = new ListInfo();
		info.setMaxPerPage(PAGE_SIZE);
		String		pageNumStr = request.getParameter("page");
		int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
		info.setPageNum(pageNum);
		info.setNotEqualsCols(new String [] {"user.privacy"});
		info.setNotEquals(new Object [] {Privacy.PRIVATE});
		
		RecentStats	 rstats = ratingService.getRecentStats(info);
		HttpSession	session = request.getSession(); 
		session.setAttribute("recentStats", rstats);
		 
		Collection<WordCloudInfo>	wordCloud = ratingService.getWordCloud(100, null, true, false);
		request.setAttribute("wordCloud", wordCloud);
		
		Collection<RatingCloudInfo>	ratingsCloud = ratingService.getRatingsCloud(200);
		request.setAttribute("ratingsCloud", ratingsCloud);

		Collection<UserCloudInfo>	userCloud = ratingService.getMostActiveMembers(50);
		request.setAttribute("userCloud", userCloud);

		User		user = getUserFromSession(request);
		
		int	numResults = ratingService.countRecentStats(info);
		int	numPages = numResults == 0 ? 1 : (int)Math.ceil((double)numResults / (double)PAGE_SIZE);
		request.setAttribute("numPages", numPages);
		request.setAttribute("curPage", pageNum);
		setupAll(request, user);
		if (user == null) {
			List<Ad>	ads = adService.getAdsFor(rstats, 2);		
			adStatService.impress(ads);
			request.setAttribute("ads", ads);
		}
		
		request.setAttribute("noMenus", user == null);
		
		request.setAttribute("selTab", Features.home.toString());

		return (mapping.findForward("welcome")); 
	}

	public ActionForward diffs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		request.setAttribute("selTab", Features.pageDiffs.toString());

		return (mapping.findForward("diffs"));
	}
	
	public ActionForward computeDiffs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		PageDiffsForm	pdf = (PageDiffsForm)form;
		String	reference = prepUrl(pdf.getReference());
		URL		referenceURL = null;
		ActionMessages	errors = getErrors(request);
		if (StringUtils.isEmpty(reference)) {
			errors.add("reference", new ActionMessage("error.emptyLink", reference));
			saveErrors(request, errors);
		}
		try {
			referenceURL = Utils.normalizeUrl(new URL(reference)); 
		}
		catch (Exception e) {
			errors.add("reference", new ActionMessage("error.badURL", reference));
			saveErrors(request, errors);
		}
		
		String	page1 = prepUrl(pdf.getPage1());
		String	page2 = prepUrl(pdf.getPage2());
		String	page3 = prepUrl(pdf.getPage3());
		String	page4 = prepUrl(pdf.getPage4());
		String	page5 = prepUrl(pdf.getPage5());
		
		ArrayList<URL>	pages = new ArrayList<URL>();
	
		addPage(errors, request, page1, "page1", pages);
		addPage(errors, request, page2, "page2", pages);
		addPage(errors, request, page3, "page3", pages);
		addPage(errors, request, page4, "page4", pages);
		addPage(errors, request, page5, "page5", pages);
		if (errors.size() > 0) 
			return (mapping.getInputForward());

		List<PageDiff>	diffs = ratingService.pageDiffs(user.getUserId(), referenceURL, pages);
		
		request.setAttribute("results", diffs);
		return (mapping.findForward("diffs"));
	}

	private void	addPage (ActionMessages errors, HttpServletRequest request, String page1, 
			String name, ArrayList<URL> pages) {
		try {
			if (StringUtils.isNotEmpty(page1)) 
				pages.add(Utils.normalizeUrl(new URL(page1)));
		}
		catch (Exception e) {
			errors.add(name, new ActionMessage("error.badURL", e.toString()));
			saveErrors(request, errors);
		}
		
	}
	
	public ActionForward controls(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)  
		throws Exception
		{	
			User		user = getUserFromSession(request);
			if (user == null)
				return (noUserLoggedIn(mapping, request));
			
			UserStats	stats = ratingService.findUserStats(user.getUserId());
			boolean	hasRatings = stats.getRatedPagesMap().size() > 0;
			
			if (!hasRatings) {
				ListInfo	info = new ListInfo();
				List<String>	cats = user.getCategorySet().getCategories();
				if (cats != null && cats.size() > 0) {
					info.setIns(new Collection [] {user.getCategorySet().getCategories()});
					info.setInsCols(new String [] {"rating"});
				}
				info.setMaxPerPage(PAGE_SIZE);
				
				RecentStats	 rstats = ratingService.getRecentStats(info);
				request.setAttribute("recentStats", rstats);
			}
			
			RatingForm	rf = (RatingForm)form;
			rf.setLink((String)request.getAttribute("link"));
			
			setupAll(request, user);
			request.setAttribute("selTab", Features.controls.toString());

			return (mapping.findForward("controls")); 
		}

	public ActionForward showAllRatings(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) 
		throws Exception
		{	
			String	urlStr = request.getParameter("url");
			urlStr = URLDecoder.decode(urlStr, "UTF-8");
			ListInfo	info = new ListInfo();
			info.setEquals(new String [] {urlStr});
			info.setEqualsCols(new String [] {"urlStr"});
			info.setMaxPerPage(PAGE_SIZE);
			RecentStats	 rstats = ratingService.getRecentStats(info);
			int	numResults = rstats.getRatings() == null ? 0 : rstats.getRatings().size();
			int	numPages = numResults == 0 ? 1 : (int)Math.ceil((double)numResults / (double)PAGE_SIZE);
			request.setAttribute("numPages", numPages);
			String		pageNumStr = request.getParameter("page");
			int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
			
			request.setAttribute("curPage", pageNum);
			HttpSession	session = request.getSession();
			session.setAttribute("recentStats", rstats);
			setupAds(request, rstats);
			request.setAttribute("showAllMsg", urlStr);
			return (mapping.findForward("recentRatings"));
		}

	public ActionForward rateAjax(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String  urlStr = request.getParameter("url");
		String  rating = request.getParameter("rating");
		String	comment = request.getParameter("comment");
		//urlStr = URLDecoder.decode(urlStr, "UTF-8");
		String	cookieData = request.getParameter("cookieData");
		
		PrintWriter out = response.getWriter();

		User	user = getUserFromSession(request);
		
		// TEMPORARY:
		if (user == null) {
			String	testUserId = request.getParameter("testUserId");
			if (testUserId != null)
				user = userService.findUser(testUserId);
		}
		
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
		RatingInfo  result = ratingService.rate(userId, url, rating, comment, cookieData);
		out.print("<response><rating>" + result.toString() + "</rating><userId>" + user.getUserId() + "</userId></response>");
		out.flush();
		return null;
	}



	public ActionForward changeHistory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HistoryForm	hf = (HistoryForm)form;
		HttpSession session = request.getSession();
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else {
			boolean	remove = WebUtils.xequals(hf.getRemove(), "true");
			boolean	refetch = WebUtils.xequals(hf.getRefetch(), "true");
			UserStats	stats = ratingService.findUserStats(user.getUserId());
			HashMap<URL, RatedPage>	pages = stats.getRatedPagesMap();
			String	urlStr = hf.getUrl();
			if (refetch && StringUtils.isEmpty(urlStr)) {
				for (RatedPage rp : pages.values()) {
					rp.setTimestamp(new Date());
					ratingService.rate(user.getUserId(), rp.getUrl(), rp.getRating(), rp.getComment(), null);
				}
			}
			else {
				URL	url = Utils.normalizeUrl(new URL(urlStr));
				RatedPage	rp = pages.get(url); 
				if (rp != null) {
					if (remove)
						ratingService.delete(stats, rp);
					else if (refetch) {
						rp.setTimestamp(new Date());
						ratingService.rate(user.getUserId(), url, rp.getRating(), rp.getComment(), null);
					}
					else
					{
						rp.setTimestamp(new Date());
						String	newRating = hf.getRating();
						rp.setRating(newRating);
					}
				}
				else
					System.out.println ("COULD NOT FIND PAGE: " + urlStr + " for user " + user.getUserId());
			}
			
			ratingService.save(stats);
			
			setupHistory(request, session, user.getUserId());
		}
		return (mapping.findForward("history"));

	}

	public ActionForward recommend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
	
		RecommendForm	rf = (RecommendForm)form;
		String	desirable = rf.getDesirableRatings();
		HashSet<String>	desirableRatingsSet = null;
		if (desirable != null) {
			desirableRatingsSet = new HashSet<String>();
			StringTokenizer	tok = new StringTokenizer(desirable, ",");
			while (tok.hasMoreTokens())
				desirableRatingsSet.add(tok.nextToken().trim());
		}
		else
			rf.setDesirableRatings(user.getDesirableRatings());
		
		if (StringUtils.isEmpty(rf.getMaxPagesToSearch()))
			rf.setMaxPagesToSearch("50");
		if (StringUtils.isEmpty(rf.getMaxResults()))
			rf.setMaxResults("10");
		
		if (StringUtils.isNotEmpty(rf.getDoit())) {
			int		maxPages = 50;
			try {
				maxPages = Integer.parseInt(rf.getMaxPagesToSearch());
			}
			catch (Exception e) {
			}
			if (maxPages > 100)
				maxPages = 100;
			
			int		maxResults = 10;
			try {
				maxResults = Integer.parseInt(rf.getMaxResults());
			}
			catch (Exception e) {
			}
			if (maxResults > 15)
				maxResults = 15;
			
			List<Recommendation>	recs = user.getRecommendationsList();
			Collection<String>	dontShow = new HashSet<String>();
			if (recs != null) {
				for (int i = 0; i < recs.size(); i++) {
					Recommendation	rec = recs.get(i);
					if (rf.getDontShow() [i])
						rec.setDontShow(true);
					
					if (rec.isDontShow())
						System.out.println ("SHOULD NOT SHOW " + rec.getUrlStr() + " INDEX: " + i);
					// in any case, don't let the recommendations call return duplicates:
					dontShow.add(rec.getUrlStr());
				}
				
			}
			userService.saveRecommendations(user, recs);
			
			recs = ratingService.getRecommendations(user.getUserId(), desirableRatingsSet, 
					dontShow, maxPages, maxResults);
			
			userService.saveRecommendations(user, recs);
			
			request.setAttribute("didIt", true);
		}
	
		rf.init();
		List<Recommendation>	filtered = user.getVisibleRecommendationsList();
		request.setAttribute("recs", filtered);
		return (mapping.findForward("recommend"));
	}
	
	public ActionForward wordCloud(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		WordCloudForm	wcf = (WordCloudForm)form;
		int	size;
		if (!StringUtils.isEmpty(wcf.getSize())) 
			size = Integer.parseInt(wcf.getSize());
		else 
			size = 100; 
		String	ratedAs = wcf.getRatedAs();
		boolean	ignoreCommon = wcf.isIgnoreCommon();
		
		Collection<WordCloudInfo>	wordCloud = ratingService.getWordCloud(size, ratedAs, ignoreCommon, true);
		request.setAttribute("wordCloud", wordCloud);
		return (mapping.findForward("wordCloud"));
	}
	
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		SearchForm	sf = (SearchForm)form;
		SearchInfo	searchInfo = new SearchInfo();
		searchInfo.setSearch(sf.getSearch());
		searchInfo.setLowest(sf.getLowest());
		searchInfo.setHighest(sf.getHighest());
		searchInfo.setMaxResults(1000);
		searchInfo.setRatedAs(sf.getRatedAs());
		searchInfo.setRatedBy(sf.getRatedBy());
		searchInfo.setGroupName(sf.getGroupName());
		searchInfo.setLongerThan(sf.getLongerThan());
		searchInfo.setShorterThan(sf.getShorterThan());
		searchInfo.setHasAds(sf.isHasAds());
		searchInfo.setHasPopups(sf.isHasPopups());
		searchInfo.setSortCols(new String [] {"timestamp"});
		searchInfo.setAscendings(new boolean [] {false});
		
		int	times = 1;
		try {
			if (StringUtils.isNotEmpty(sf.getTimes()))
				times = Integer.parseInt(sf.getTimes());
			else
				sf.setTimes("1");
		}
		catch (Exception e) {
			
		}
		searchInfo.setRatedAtLeastTimes(times);
		
		String		pageNumStr = request.getParameter("page");
		int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
		searchInfo.setPageNum(pageNum);
		
		List<SearchResult>	results = searchService.find(searchInfo);
		int	numResults = results == null ? 0 : results.size();
		int	numPages = numResults == 0 ? 1 : (int)Math.ceil((double)numResults / (double)PAGE_SIZE);
		request.setAttribute("curPage", pageNum);
		request.setAttribute("numPages", numPages);
		request.setAttribute("numResults", numResults);
		request.setAttribute("searchResults", results);
		setupAds(request, results);
		return (mapping.findForward("searchResults"));
	}
	
	public ActionForward recentRatings(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RecentRatingsForm	rrf = (RecentRatingsForm)form;
		int	size;
		if (!StringUtils.isEmpty(rrf.getSize())) 
			size = Integer.parseInt(rrf.getSize());
		else {
			Integer	sizeObj = (Integer)request.getSession().getAttribute("listSize");
			if (sizeObj != null)
				size = sizeObj;
			else
				size = 30;
			rrf.setSize(String.valueOf(size));
		}
		String	sortCol = request.getParameter("sort");
		if (StringUtils.isEmpty(sortCol))
			sortCol = (String)request.getSession().getAttribute("sort");
		else
			request.getSession().setAttribute("sort", sortCol);
		
		String	dir = request.getParameter("dir");
		if (StringUtils.isEmpty(dir))
			dir = (String)request.getSession().getAttribute("dir");
		else
			request.getSession().setAttribute("dir", dir);
		request.setAttribute("antiDir", dir == null || dir.equals("desc") ? "asc" : "desc");
		request.getSession().setAttribute("listSize", size);
		
		ListInfo	info = new ListInfo();
		if (StringUtils.isNotEmpty(sortCol)) {
			info.setSortCols(new String [] {sortCol});
			info.setAscendings(new boolean [] {dir != null && dir.equals("asc") ? true : false});
		}
		info.setMaxPerPage(10);
		info.setMaxResults(size);
		String		pageNumStr = request.getParameter("page");
		int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
		info.setPageNum(pageNum);
		RecentStats	 rstats = ratingService.getRecentStats(info);
		request.setAttribute("recentStats", rstats);
		
		int	numResults = ratingService.countRecentStats(info);
		int	numPages = numResults == 0 ? 1 : (int)Math.ceil((double)numResults / (double)PAGE_SIZE);
		request.setAttribute("numPages", numPages);
		request.setAttribute("curPage", pageNum);
		
		setupAds(request, rstats);
		request.setAttribute("selTab", Features.recent.toString());
		return (mapping.findForward("recentRatings"));
	}
	
	private void	setupAds (HttpServletRequest request, RecentStats rstats) throws Exception {
		User	user = getUserFromSession(request);
		List<Ad>	ads = user == null ? adService.getAdsFor(rstats, 4) : adService.getAdsFor(user, 4);		
		adStatService.impress(ads);
		request.setAttribute("ads", ads);
	}

	private void	setupAds (HttpServletRequest request, List<SearchResult> results) throws Exception {
		List<Ad>	ads = adService.getSearchAds(results, 4);		
		adStatService.impress(ads);
		request.setAttribute("ads", ads);
	}

	public ActionForward feedback(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (mapping.findForward("feedback"));
	}
	
	public ActionForward submitFeedback(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		FeedbackForm	ff = (FeedbackForm)form;
		String	subject = ff.getSubject();
		String	msg = ff.getMessage();
		String	emailAddr = ff.getEmailAddr();
		
		if (!StringUtils.isEmpty(msg)) {
			if (msg.length() > 2000) {
				msg = msg.substring(0, 2000);
			}
		
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost("k2smtpout.secureserver.net");
			sender.setPort(25);
			//sender.setUsername("feedback");
			MimeMessage message = sender.createMimeMessage();

			// use the true flag to indicate you need a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setTo("feedback@webpredict.net");

			StringBuffer	buf = new StringBuffer("Feedback from email address: ");
			buf.append(emailAddr);
			buf.append(WebUtils.NL);
			buf.append("Subject: " + subject);
			buf.append(WebUtils.NL);
			buf.append("Message: " + msg);
			buf.append(WebUtils.NL);
			
			helper.setText(buf.toString()); 
		
			try {
				WebUtils.sendMail(subject, msg, emailAddr, "feedback@webpredict.net");
			}
			catch (Exception e) {
				ActionMessages	errors = getErrors(request);
				errors.add("mail", new ActionMessage("error.systemError", e.toString()));
				saveErrors(request, errors);
			}
		}
		return (mapping.findForward("feedbackSubmitted"));
	}

	
	public ActionForward findSimilarUsers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		
		List<UserMatch>	users = userService.findSimilarUsersTo(user.getUserId());
		request.setAttribute("users", users);
		request.setAttribute("selTab", Features.findSimilarUsers.toString());
		return (mapping.findForward("similarUsers"));
	}
	
	public ActionForward globalStats(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
	
		GlobalStatsForm	gsf = (GlobalStatsForm)form;
		Integer	max = 1000;
		if (StringUtils.isEmpty(gsf.getMax()))
			gsf.setMax(String.valueOf(max));
		try {
			max = Integer.parseInt(gsf.getMax());
			
		}
		catch (Exception e) {
			
		}
		String	search = gsf.getSearch();
		List<WordFreqPair>	globalStats = ratingService.getGlobalWordStats(max, search, gsf.isMostFrequent());
		
		if (globalStats != null && globalStats.size() > 0) {
			int	halfSize = (int)Math.ceil((double)globalStats.size() / 2d);
			List<WordFreqPair>	firstHalf = globalStats.subList(0, halfSize);
			List<WordFreqPair>	secondHalf = halfSize < globalStats.size() ? 
					globalStats.subList(halfSize + 1, globalStats.size()) : new ArrayList<WordFreqPair>();
			request.setAttribute("firstHalfStats", firstHalf);
			request.setAttribute("secondHalfStats", secondHalf);

		}
		request.setAttribute("selTab", Features.stats.toString());

		return (mapping.findForward("globalStats"));
	}
	
	public ActionForward history(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));
		else 
			setupHistory(request, session, user.getUserId());
		
		request.setAttribute("selTab", Features.history.toString());
		return (mapping.findForward("history"));
	}

	private void	setupHistory (HttpServletRequest request, HttpSession session, String userId) throws Exception {
		UserStats	stats = ratingService.findUserStats(userId);
		HashMap<URL, RatedPage>	pages = stats.getRatedPagesMap();
		int	numPages = pages == null ? 0 : pages.size();
		session.setAttribute("numPages", numPages);
		String		pageNumStr = request.getParameter("page");
		int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
		session.setAttribute("curPage", pageNum);
		
		if (pages != null){
			String	sortCol = request.getParameter("sort");
			String	dir = request.getParameter("dir");
			request.setAttribute("antiDir", dir == null || dir.equals("asc") ? "desc" : "asc");
			
			RatedPage []	pagesArr = new RatedPage [numPages];
			if (numPages > 0)
				pages.values().toArray(pagesArr);
			Arrays.sort(pagesArr, new RatedPagesComparator(sortCol, dir != null && dir.equals("asc")));
			ArrayList<RatedPage>	arr = new ArrayList<RatedPage>();
			for (RatedPage rp : pagesArr)
				arr.add(rp);
			
			session.setAttribute("history", arr);
		}
	}
	
	public ActionForward logout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		request.setAttribute("loggedOut", true);
		request.getSession().removeAttribute("userId");
		return (mapping.findForward("logout"));
	}

	public ActionForward getMenuItems(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		PrintWriter out = response.getWriter();

		User	user = getUserFromSession(request);
		if (user == null) {
			String	lastLoginId = request.getParameter("lastLoginId");
			if (StringUtils.isNotEmpty(lastLoginId)) 
				user = userService.findUser(URLDecoder.decode(lastLoginId, "UTF-8"));
			
			if (user == null) {
				out.println(getInfoMessagePlain(request));
				return (null);
			}
			else
				loginUser(request, user);
		}
		
		List<String>	categories = user.getCategorySet().getCategories();
		StringBuffer	buf = new StringBuffer("<response><ratings>");
		for (String s : categories) {
			buf.append("<rating>");
			buf.append(s);
			buf.append("</rating>");
		}
			
		buf.append("</ratings>");
		buf.append("<userId>" + user.getUserId() + "</userId>");
		buf.append("</response>");
		out.print(buf.toString());
		out.flush();
		
		return (null);
	}
	
	public ActionForward anonLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String  anonId = request.getParameter("anonid");
		anonId = URLDecoder.decode(anonId, "UTF-8");

		if (StringUtils.isEmpty(anonId) || anonId == null || !anonId.startsWith("anon-")) {
			response.getWriter().println(getInfoMessagePlain(request));
			return (null);
		}
	
		User	anonUser = userService.findOrCreateAnonUser(anonId);
		loginUser(request, anonUser);
		
		return (getMenuItems(mapping, form, request, response));
	}

	public ActionForward autoLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String  userId = URLDecoder.decode(request.getParameter("userId"), "UTF-8");
		
		if (StringUtils.isEmpty(userId)) {
			response.getWriter().println(getInfoMessagePlain(request));
			return (null);
		}
	
		User	anonUser = userService.findUser(userId);
		loginUser(request, anonUser);
		
		return (getMenuItems(mapping, form, request, response));
	}
	
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		String	userId = rf.getUsername();

		if (StringUtils.isEmpty(userId))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("username", new ActionMessage("error.emptyUserId", userId));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		User	user = userService.findUser(userId);

		String	password = rf.getPassword();
		if (user == null)  
			return (noUser(mapping, request, userId));
		else if (StringUtils.isEmpty(password) || !user.getPassword().equals(password))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		loginUser(request, user);
		String	pageTried = rf.getPageTried();
		if (StringUtils.isNotEmpty(pageTried)) 
			return (new ActionForward(pageTried));
		
		return (mapping.findForward("loginControls"));
	}

	public ActionForward loginLanding(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		request.setAttribute("showUserTabs", false);
		return (mapping.findForward("loginLanding"));
	}
	
	public ActionForward simpleLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		String	userId = rf.getUsername();

		if (StringUtils.isEmpty(userId))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("username", new ActionMessage("error.emptyUserId", userId));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		User	user = userService.findUser(userId);

		String	password = rf.getPassword();
		HttpSession session = request.getSession();
		if (user == null)  
			return (noUser(mapping, request, userId));
		else if (StringUtils.isEmpty(password) || !user.getPassword().equals(password))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		else
			session.setAttribute("userId", userId);	 

		request.setAttribute("user", user);
		setupAll(request, user);
		
		request.setAttribute("close", request.getParameter("close"));
		return (mapping.findForward("simpleLoginSuccess"));
	}

	public ActionForward stats(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		User	user = getUserFromSession(request);
		
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		else {
			String	sortCol = request.getParameter("sort");
			String	dir = request.getParameter("dir");
			UserStats	stats = ratingService.findUserStats(user.getUserId());
			HashMap<URL, RatedPage>	pages = stats.getRatedPagesMap();
			Collection<RatedPage>		ratedPages = pages == null ? null : pages.values();
			int	numPages = pages == null ? 0 : pages.size();

			String		pageNumStr = request.getParameter("page");
			int			pageNum = StringUtils.isEmpty(pageNumStr) ? 1 : Integer.parseInt(pageNumStr);
			session.setAttribute("curPage", pageNum);
			
			RatedPage []	pagesArr = new RatedPage [numPages];
			if (numPages > 0)
				ratedPages.toArray(pagesArr);
			
			// ugh all these stupid temp collections! reduce
			Arrays.sort(pagesArr, new RatedPagesComparator(sortCol, dir != null && dir.equals("asc")));
			ArrayList<RatedPage>	arr = new ArrayList<RatedPage>();
			
			int	numNumericRatingPages = 0;			
			int	totalRating = 0;
			if (pages != null)
				for (RatedPage p : pagesArr) {
					if (p.isRatingNumeric()) {
						numNumericRatingPages++;
						totalRating += Integer.parseInt(p.getRating());
					}
					arr.add(p);
				}

			if (user.getCategorySet().isOrdered() && user.getCategorySet().isNumeric()) {
				float	aveRating = numNumericRatingPages == 0 ? 0 : (float)totalRating / (float)numNumericRatingPages;
				request.setAttribute("aveRating", aveRating);
			}
			
			request.setAttribute("antiDir", dir == null || dir.equals("asc") ? "desc" : "asc");
			if (pages != null)
				session.setAttribute("history", arr);
			request.setAttribute("numPages", numPages);
		}
		return (mapping.findForward("stats"));
	}

	public ActionForward ratePartial(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		if (user == null)
			return (mapping.findForward("loginNeeded"));
		
		String	url = URLDecoder.decode(request.getParameter("url"), "UTF-8");
		request.setAttribute("link", url);
		return (mapping.findForward("controlsFull"));
	}
	
	public ActionForward rate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();

		if (StringUtils.isEmpty(rf.getLink()))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("link", new ActionMessage("error.emptyLink", rf.getLink()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		if (StringUtils.isEmpty(rf.getRating()))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("rating", new ActionMessage("error.emptyRating", rf.getRating()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		if (WebUtils.isUnsupportedFormat(rf.getLink())) {
			ActionMessages	errors = getErrors(request);
			errors.add("link", new ActionMessage("error.unsupportedFormat", rf.getLink()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		String	urlStr = prepUrl(rf.getLink());
		String	rating = rf.getRating();
		
		if (!user.getCategorySet().contains(rating)) {
			request.setAttribute("link", urlStr);
			request.setAttribute("encodedLink", URLEncoder.encode(urlStr, "UTF-8"));
			request.setAttribute("rating", rating);
			return (mapping.findForward("addRatingFirst"));
		}
		
		URL     url;
		try {
			url = Utils.normalizeUrl(new URL(urlStr));
			RatingInfo  result = ratingService.rate(userId, url, rating, rf.getComment(), null);
			request.setAttribute("messages", result.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("systemerror", e.getLocalizedMessage());
		}

		setupAll(request, user);
		return (mapping.findForward("controls"));
	}

	public ActionForward addAndRate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();

		AddCategoryForm	acf = (AddCategoryForm)form;
		
		String	rating = acf.getRating();
		String	urlStr = acf.getLink();
		//urlStr = URLDecoder.decode(urlStr, "UTF-8");
		
		user.getCategorySet().getCategories().add(rating);
		
		userService.save(user);
		
		URL     url;
		try {
			url = new URL(urlStr);
			RatingInfo  result = ratingService.rate(userId, url, rating, null, null);
			request.setAttribute("messages", result.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("systemerror", e.getLocalizedMessage());
		}

		setupAll(request, user);
		return (mapping.findForward("controls"));
	}

	public ActionForward bulkRate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		BulkRatingForm	rf = (BulkRatingForm)form;
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (this.noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();


		List<String>	categories = user.getCategorySet().getCategories();
		
		StringBuffer	buf = new StringBuffer();
		if (StringUtils.isNotEmpty(rf.getRating1())) 
			rateIt(request, prepUrl(rf.getRating1()), categories.get(0), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating2())) 
			rateIt(request, prepUrl(rf.getRating2()), categories.get(1), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating3())) 
			rateIt(request, prepUrl(rf.getRating3()), categories.get(2), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating4())) 
			rateIt(request, prepUrl(rf.getRating4()), categories.get(3), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating5())) 
			rateIt(request, prepUrl(rf.getRating5()), categories.get(4), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating6())) 
			rateIt(request, prepUrl(rf.getRating6()), categories.get(5), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating7())) 
			rateIt(request, prepUrl(rf.getRating7()), categories.get(6), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating8())) 
			rateIt(request, prepUrl(rf.getRating8()), categories.get(7), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating9())) 
			rateIt(request, prepUrl(rf.getRating9()), categories.get(8), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating10())) 
			rateIt(request, prepUrl(rf.getRating10()), categories.get(9), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating11())) 
			rateIt(request, prepUrl(rf.getRating11()), categories.get(10), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating12())) 
			rateIt(request, prepUrl(rf.getRating12()), categories.get(11), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating13())) 
			rateIt(request, prepUrl(rf.getRating13()), categories.get(12), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating14())) 
			rateIt(request, prepUrl(rf.getRating14()), categories.get(13), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating15())) 
			rateIt(request, prepUrl(rf.getRating15()), categories.get(14), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating16())) 
			rateIt(request, prepUrl(rf.getRating16()), categories.get(15), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating17())) 
			rateIt(request, prepUrl(rf.getRating17()), categories.get(16), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating18())) 
			rateIt(request, prepUrl(rf.getRating18()), categories.get(17), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating19())) 
			rateIt(request, prepUrl(rf.getRating19()), categories.get(18), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating20())) 
			rateIt(request, prepUrl(rf.getRating20()), categories.get(19), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating21())) 
			rateIt(request, prepUrl(rf.getRating21()), categories.get(20), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating22())) 
			rateIt(request, prepUrl(rf.getRating22()), categories.get(21), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating23())) 
			rateIt(request, prepUrl(rf.getRating23()), categories.get(22), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating24())) 
			rateIt(request, prepUrl(rf.getRating24()), categories.get(23), buf, userId);
		if (StringUtils.isNotEmpty(rf.getRating25())) 
			rateIt(request, prepUrl(rf.getRating25()), categories.get(24), buf, userId);
		
		request.setAttribute("messages", buf.toString());
		
		setupAll(request, user);
		return (mapping.findForward("controls"));
	}

	private void	rateIt (HttpServletRequest request, String urlStr, String rating, StringBuffer buf, String userId) throws Exception {
		URL     url;
		try {
			url = new URL(urlStr);
			RatingInfo  result = ratingService.rate(userId, url, rating, null, null);
			buf.append(result.toString() + "...");
		}
		catch (Exception e) {
			e.printStackTrace();
			buf.append(e.getLocalizedMessage() + "...");
		}
	}
	
	public ActionForward rateContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (this.noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();

		String	rawContent = rf.getRawContent();
		String	rating = rf.getRating();
 
		if (StringUtils.isEmpty(rawContent))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("rawContent", new ActionMessage("error.emptyContent", rf.getLink()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		if (StringUtils.isEmpty(rf.getRating()))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("rating", new ActionMessage("error.emptyRating", rf.getRating()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		try {
			RatingInfo  result = ratingService.rateRawContent(userId, rawContent, rf.getContentId(), rating, rf.getComment());
			request.setAttribute("messages", result.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("systemerror", e.getLocalizedMessage());
		}

		setupAll(request, user);
		return (mapping.findForward("controls"));
	}

	public ActionForward parsePage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		RatingForm	rf = (RatingForm)form;
		User	user = getUserFromSession(request);
		if (user == null)
			return (this.noUserLoggedIn(mapping, request));

		String	urlStr = prepUrl(rf.getLink());

		URL     url;
		try {
			url = new URL(urlStr);
			CachedURL	cachedUrl = new CachedURL(url);
			ParsedPage  result = new WebUtils().newParse(cachedUrl, user.isGenericizeNumbers(), 
					user.isConsiderAds(), user.isUseStrictParser(), DEFAULT_SNIPPET_SIZE, null);
			request.setAttribute("pp", result);
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("systemerror", e.getLocalizedMessage());
		}

		setupCollections(session, request);
		return (mapping.findForward("parsedPage"));
	}

	public ActionForward predict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		String	rawContent = rf.getRawContent();

		if (!StringUtils.isEmpty(rawContent))
			return (predictContent(mapping, form, request, response));
		
		Collection<String> keywords = null;
		
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();

		if (StringUtils.isEmpty(rf.getLink()))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("link", new ActionMessage("error.emptyLink", rf.getLink()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		if (WebUtils.isUnsupportedFormat(rf.getLink())) {
			ActionMessages	errors = getErrors(request);
			errors.add("link", new ActionMessage("error.unsupportedFormat", rf.getLink()));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		
		String	urlStr = prepUrl(rf.getLink());
		
		boolean	summarize = true;
		
		URL url;
		try {
			url = Utils.normalizeUrl(new URL(urlStr));
			Prediction  prediction = ratingService.predict(userId, url, null, keywords, summarize, DEFAULT_SNIPPET_SIZE);
			request.setAttribute("prediction", prediction);   
		}
		catch (Exception e) {
			e.printStackTrace();
			//out.print("PROBLEM: " + e.getLocalizedMessage());
			//errors.add
			request.setAttribute("systemerror", e.toString());      
		}

		setupAll(request, user);
		return (mapping.findForward("controls"));
	}

	public ActionForward predictContent (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		User	user = getUserFromSession(request);
		String	userId;
		if (user == null)
			return (noUserLoggedIn(mapping, request));
		else
			userId = user.getUserId();

		boolean	summarize = false;
		try {
			Prediction  prediction = ratingService.predictRawContent(userId, rf.getRawContent(), summarize, DEFAULT_SNIPPET_SIZE);
			request.setAttribute("prediction", prediction);  
		}
		catch (Exception e) {
			e.printStackTrace();
			//out.print("PROBLEM: " + e.getLocalizedMessage());
			//errors.add
			request.setAttribute("systemerror", e.toString());      
		}

		setupAll(request, user); 
		return (mapping.findForward("controls"));
	}

	public static final int	DEFAULT_SNIPPET_SIZE = 255;
	
	public ActionForward predictAjax(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		PrintWriter out = response.getWriter();
		String  urlStr = request.getParameter("url");
		//urlStr = URLDecoder.decode(urlStr, "UTF-8");
		String	cookieData = request.getParameter("cookieData");
		String	keywordsStr = request.getParameter("keywords");
		String	summarizeStr = request.getParameter("summary");
		String	minerrorsStr = request.getParameter("minerrors");
		boolean	minerrors = minerrorsStr == null ? false : minerrorsStr.equals("true");
		
		Collection<String> keywords = null;
		if (keywordsStr != null) {
			keywordsStr = URLDecoder.decode(keywordsStr, "UTF-8").toLowerCase();
			keywords = Utils.extractCollection(keywordsStr);
		}
		
		User	user = getUserFromSession(request);
		
		// TEMPORARY:
		if (user == null) {
			String	testUserId = request.getParameter("testUserId");
			if (testUserId != null)
				user = userService.findUser(testUserId);
		}
		
		if (user == null) {
			out.println(getInfoMessage(request));
			return (null);
		}
		
		String	userId = user.getUserId();
		System.out.println ("RECEIVED REQUEST TO PREDICT " + urlStr + " for user " + userId + "...");

		StringBuffer	buf = new StringBuffer();
		buf.append("<response><userId>" + user.getUserId() + "</userId><prediction>");
		
		if (WebUtils.isUnsupportedFormat(urlStr)) {
			if (minerrors) {
				buf.append("<predValue>-</predValue></prediction></response>");
				out.println(buf.toString());
			}
			else
				out.println(getUnsupportedFormatMessage(request));
			return (null);
		}
		
		boolean	summarize = false;
		if (StringUtils.isNotEmpty(summarizeStr))
			summarize = true;
		
		URL url;
		try {
			url = new URL(urlStr);
		}
		catch (Exception e) {
			if (minerrors)
				buf.append("<predValue>-</predValue>");
			else
				buf.append("PROBLEM: " + e.getLocalizedMessage());
			buf.append("</prediction></response>");
			out.print(buf.toString());
			System.out.println(buf.toString());
			
			return (null);
		}
					
		Prediction  prediction = ratingService.predict(userId, url, cookieData, keywords, summarize, DEFAULT_SNIPPET_SIZE);

		if (prediction.isSuccess()) {
			Integer		maxRating = prediction.getMaxRating();
		
			buf.append("<predValue>");
			buf.append(prediction.getRating());
			buf.append("</predValue>");
			
			String		nextLikely = prediction.getSecondGuess();
			if (nextLikely != null) {
				buf.append("<nextMostLikely>");
				buf.append(nextLikely);
				buf.append("</nextMostLikely>");
			}

			String		summary = prediction.getSummary();
			if (summary != null) {
				buf.append("<summary>");
				buf.append(WebUtils.stripHtml(new StringBuffer(summary)));
				buf.append("</summary>");
			}

			if (maxRating != null) {
				buf.append("<outOf>");
				buf.append(maxRating);
				buf.append("</outOf>");
			}
			
			if (prediction.hasLittleOrNoContent()) {
				buf.append("<warning>");
				buf.append("This link returned little or no content to our prediction engine.");
				buf.append("</warning>");
			}
			
			String	existing = prediction.getExistingRating();
			if (existing != null) {
				buf.append("<existingRating>");
				buf.append(existing);
				buf.append("</existingRating>");
			}
			
			// TODO: reenable if they can start working well
//			boolean	considerAds = user.isConsiderAds();
//			if (considerAds)
//				buf.append("<hasAds>" + prediction.isHasAds() + "</hasAds>");
			
			boolean	considerImages = user.isConsiderNumImages();
			if (considerImages)
				buf.append("<numImages>" + prediction.getNumImages() + "</numImages>");
			
			boolean	considerLinks = user.isConsiderNumLinks();
			if (considerLinks)
				buf.append("<numLinks>" + prediction.getNumLinks() + "</numLinks>");
			
//			boolean	considerPopups = user.isConsiderPopups();
//			if (considerPopups)
//				buf.append("<hasPopups>" + prediction.isHasPopups() + "</hasPopups>");
	
		}
		else {
			buf.append("<predValue>");
			if (minerrors)
				buf.append("-");
			else
				buf.append(prediction.toString());
			buf.append("</predValue>");
		}
		buf.append("<hasKeywords>");
		buf.append(prediction.isHasKeywords());
		buf.append("</hasKeywords>");
	
		buf.append("</prediction>");
		buf.append("</response>");
		out.print(buf.toString());
		out.flush();
		System.out.println ("SENDING PREDICTION RESPONSE FOR " + urlStr + " for user " + userId + "...");
		System.out.println(buf.toString());
		
		return null;
	}
}
