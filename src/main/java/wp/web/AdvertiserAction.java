package wp.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import wp.core.AlreadyExistsException;
import wp.core.Constants;
import wp.model.Ad;
import wp.model.AdStat;
import wp.model.Advertiser;
import wp.model.Prediction;
import wp.model.User;
import wp.utils.WebUtils;

/**
 * @struts.action path="/FinishSignupAdvertiser" parameter="finishSignup" validate="false"
 *   name="advertiserSignupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="finishSignup" path="/SignupAdvertiserInfo.jsp"
 * @spring.bean name="/FinishSignupAdvertiser"
 * 
 * @struts.action path="/PreAdvertiserSignup" parameter="preSignup" validate="false"
 *   name="advertiserSignupForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="preSignup" path="/PreAdverSignup.jsp"
 * @spring.bean name="/PreAdvertiserSignup"
 * 
 * @struts.action path="/LoginAdvertiser" parameter="login" validate="false"
 *   name="advertiserLoginForm" scope="request" input="/ForAdvertisers.jsp"
 * @struts.action-forward name="adWelcome" path="/AdvertiserControls.jsp"
 * @spring.bean name="/LoginAdvertiser"
 * 
 * @struts.action path="/AdWelcome" parameter="adWelcome" validate="false"
 *   scope="request" input="/ForAdvertisers.jsp" 
 * @spring.bean name="/AdWelcome"
 * 
 * @struts.action path="/LogoutAdvertiser" parameter="logout" validate="false"
 *   name="advertiserLoginForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="logout" path="/Welcome.do"
 * @spring.bean name="/LogoutAdvertiser"
 * 
 * @struts.action path="/ListAdvertisers" parameter="listAdvertisers" validate="false"
 *   name="advertisersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listAdvertisers" path="/ListAdvertisers.jsp"
 * @spring.bean name="/ListAdvertisers"
 * @struts.action-forward name="unauthorized" path="/Unauthorized.jsp"
 * 
 * @struts.action path="/AddAds" parameter="addAds" validate="false"
 *   name="addAdsForm" scope="request" input="/EditUser.do"
 * @struts.action-forward name="addAds" path="/AdList.jsp"
 * @struts.action-forward name="doneAddAds" path="/EditAdvertiser.do"
 * @spring.bean name="/AddAds"
 * 
 * @struts.action path="/EvalLink" parameter="eval" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="showPredictions" path="/EvalControls.do"
 * @spring.bean name="/EvalLink"
 * 
 * @struts.action path="/EvalContent" parameter="evalContent" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="evalControls" path="/EvalContent.jsp"
 * @spring.bean name="/EvalContent"
 * 
 * @struts.action path="/EvalControls" parameter="evalControls" validate="false"
 *   name="ratingForm" scope="request" input="/Welcome.do"
 * @spring.bean name="/EvalControls"
 * 
 * @struts.action path="/ShowAdStats" parameter="stats" validate="false"
 *   name="advertiserForm" scope="request" input="/Welcome.do"
 *  @struts.action-forward name="stats" path="/AdStats.jsp"
 * @spring.bean name="/ShowAdStats"
 * 
 * @struts.action path="/CountClick" parameter="countClick" validate="false"
 *   name="advertiserForm" scope="request" input="/Welcome.do"
 * @spring.bean name="/CountClick"
 * 
 * @struts.action path="/ShowAdHistory" parameter="history" validate="false"
 *   name="advertiserForm" scope="request" input="/Welcome.do"
 *  @struts.action-forward name="history" path="/AdHistory.jsp"
 * @spring.bean name="/ShowAdHistory"
 * 
 * @struts.action path="/DeleteAdvertiser" parameter="deleteAdvertiser" validate="false"
 *   name="advertisersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listAdvertisers" path="/ListAdvertisers.jsp"
 * @spring.bean name="/DeleteAdvertiser"
 * 
 * @struts.action path="/EditAdvertiser" parameter="editAdvertiser" validate="false"
 *   name="advertiserForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="editAdvertiser" path="/AdvertiserEdit.jsp"
 * @spring.bean name="/EditAdvertiser"
 * 
 * @struts.action path="/ViewAdvertiser" parameter="viewAdvertiser" validate="false"
 *   name="advertiserForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewAdvertiser" path="/AdvertiserView.jsp"
 * @spring.bean name="/ViewAdvertiser"
 * 
 * @struts.action path="/SaveAdvertiser" parameter="saveAdvertiser" validate="true"
 *   name="advertiserForm" scope="request" input="/EditAdvertiser.do"
 * @struts.action-forward name="saveAdvertiser" path="/AdvertiserEdit.jsp"
 * @spring.bean name="/SaveAdvertiser"
 * 
 * 
 * @author Jeff
 *
 */
public class AdvertiserAction extends BaseAction {

	public ActionForward preSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		return (mapping.findForward("preSignup"));
	}
	
	public ActionForward finishSignup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		AdvertiserSignupForm	psf = (AdvertiserSignupForm)form;
		
		ActionMessages	errors = getErrors(request);
		String	newUserId = psf.getNewusername();
		String	emailAddr = psf.getEmailAddr();
		String	password = psf.getPassword();
		String	name = newUserId;
		
		if (StringUtils.isEmpty(password)) {
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
		}
		if (StringUtils.isEmpty(emailAddr)) {
			errors.add("emailAddr", new ActionMessage("error.invalidEmail", emailAddr));
			saveErrors(request, errors);
		}
		if (errors.size() > 0) {
			return (mapping.findForward("preSignup"));
		}
		
		try {
			Advertiser	adver = getAdvertiserService().create(newUserId, name, null, emailAddr, password);
		}
		catch (AlreadyExistsException e) {
			errors.add("newusername", new ActionMessage("error.advertiserAlreadyExists", newUserId));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		return (mapping.findForward("finishSignup"));
	}
	
	public ActionForward countClick(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	adId = request.getParameter("adId");
		String	advertiserId = request.getParameter("adverId");
		Ad	ad = adService.findById(new Long(advertiserId), new Long(adId));
		String	link = ad.getLink();
		if (!link.startsWith("http://"))
			link = "http://" + link;
		adStatService.click(ad);
		response.sendRedirect(link);
		return (null);
	}
	
	public ActionForward addAds(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		AddAdsForm	agf = (AddAdsForm)form;
		Advertiser	advertiser = getAdvertiserFromSession(request);
		List<Ad>	ads = advertiser.getAdsList();
		
		List<AddAdInfo>	adInfo = new ArrayList<AddAdInfo>();
		if (ads != null)
			for (Ad a : ads) {
				AddAdInfo	info = new AddAdInfo();
				info.setId(a.getId().toString());
				info.setContent(a.getContent());
				info.setTitle(a.getTitle());
				info.setEnabled(a.isEnabled());
				adInfo.add(info);
			}
		
		if (!StringUtils.isEmpty(agf.getAdId())) {
			String	adId = agf.getAdId();
			Ad		ad = advertiser.findById(new Long(adId));
			ad.setEnabled(!ad.isEnabled());
			adService.save(ad);
			return (mapping.findForward("doneAddAds"));
		}
	
		request.setAttribute("ads", adInfo);
		request.setAttribute("advertiser", advertiser);
		return (mapping.findForward("addAds"));
	}
	
	public ActionForward stats(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		HttpSession session = request.getSession();
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null)
			return (noUserLoggedIn(mapping, request));
		else {
			
			double	totalCost = 0;
			double	remainingBudget = adver.getBudget();
			int		numImpressions = 0;
			int		numClicks = 0;
			
			Date	startDate = adver.getCampaignStartDate();
			Date	endDate = adver.getCampaignEndDate();
			List<Ad>	ads = adver.getAdsList(); 
			
			List<AdStat>	adStats = new ArrayList<AdStat>();
			for (Ad ad : ads) {				
				List<AdStat>	stats = ad.getStats();
				for (AdStat stat : stats) {
					numImpressions += stat.getNumImpressions();
					numClicks += stat.getNumClicks();
					totalCost += stat.getCost();
					adStats.add(stat);
				}
			}
			remainingBudget -= totalCost;
			
			String	costStr = Constants.COST_FORMAT.format(totalCost);
			String	remainingBudgetStr = Constants.COST_FORMAT.format(remainingBudget);
			session.setAttribute("totalCost", costStr);
			session.setAttribute("remainingBudget", remainingBudgetStr);
			session.setAttribute("adStats", adStats);
			
			session.setAttribute("numImpressions", numImpressions);
			session.setAttribute("numClicks", numClicks);
		}
		return (mapping.findForward("stats"));
	}

	public ActionForward evalControls(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null)
			return (noUserLoggedIn(mapping, request));

		return (mapping.findForward("evalControls"));
	}
	
	public ActionForward eval(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		String	rawContent = rf.getRawContent();

		if (!StringUtils.isEmpty(rawContent))
			return (evalContent(mapping, form, request, response));
		
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null)
			return (noUserLoggedIn(mapping, request));
		
		String	urlStr = prepUrl(rf.getLink());

		int	numUsers = 100;
		try {
			numUsers = Integer.parseInt(rf.getNumUsers());
		}
		catch (Exception e) {			
		}
		
		int	minHistory = 5;
		try {
			minHistory = Integer.parseInt(rf.getMinHistorySize());
		}
		catch (Exception e) {			
		}
		URL url;
		try {
			url = new URL(urlStr);
			Prediction  [] predictions = ratingService.predict(url, numUsers, minHistory, 255);
			request.setAttribute("predictions", predictions);   
		}
		catch (Exception e) {
			e.printStackTrace();
			//out.print("PROBLEM: " + e.getLocalizedMessage());
			//errors.add
			request.setAttribute("systemerror", e.toString());      
		}

		return (mapping.findForward("showPredictions"));
	}

	public ActionForward evalContent (ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		RatingForm	rf = (RatingForm)form;
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null)
			return (noUserLoggedIn(mapping, request));

		int	numUsers = 100;
		try {
			numUsers = Integer.parseInt(rf.getNumUsers());
		}
		catch (Exception e) {			
		}
		
		int	minHistory = 5;
		try {
			minHistory = Integer.parseInt(rf.getMinHistorySize());
		}
		catch (Exception e) {			
		}
		
		try {
			Prediction  [] predictions = ratingService.predictRawContent(rf.getRawContent(), numUsers, minHistory, 255);
			request.setAttribute("predictions", predictions);  
		}
		catch (Exception e) {
			e.printStackTrace();
			//out.print("PROBLEM: " + e.getLocalizedMessage());
			//errors.add
			request.setAttribute("systemerror", e.toString());      
		}

		return (mapping.findForward("showPredictions"));
	}


	public ActionForward logout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		request.getSession().removeAttribute("advertiserId");
		request.setAttribute("showAdverTabs", false);
		return (mapping.findForward("logout"));
	}

	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		AdvertiserLoginForm	rf = (AdvertiserLoginForm)form;
		String	userId = rf.getUsername();
		Advertiser	adver = getAdvertiserService().findByUsername(userId);
		
		String	password = rf.getPassword();
		HttpSession session = request.getSession();
		if (adver == null) 
			return (noUser(mapping, request, userId));
		else if (StringUtils.isEmpty(password) || !adver.getPassword().equals(password))
		{
			ActionMessages	errors = getErrors(request);
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		else
			session.setAttribute("advertiserId", adver.getId());	 

		request.setAttribute("showUserTabs", false);
		request.setAttribute("showAdverTabs", true);
		request.setAttribute("advertiser", adver);
		return (mapping.findForward("adWelcome"));
	}

	public ActionForward adWelcome(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));

		request.setAttribute("advertiser", adver);
		return (mapping.findForward("adWelcome"));
	}
	
	public ActionForward listAdvertisers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		User	user = getUserFromSession(request);

		if (user == null) 
			return (noUserLoggedIn(mapping, request));

		List<Advertiser>	advertisers = getAdvertiserService().listAdvertisers(null);

		request.setAttribute("advertisers", advertisers);
		return (mapping.findForward("listAdvertisers"));
	}
	
	public ActionForward editAdvertiser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));
		
		AdvertiserForm	af = (AdvertiserForm)form;
		af.bind(adver);
		
		request.setAttribute("adsList", adver.getAdsList());
		request.setAttribute("advertiser", adver);
		return (mapping.findForward("editAdvertiser"));
	}

	public ActionForward viewAdvertiser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		String	advertiserId = request.getParameter("advertiserId");
		Advertiser	advertiser = getAdvertiserService().findById(new Long(advertiserId));
		
		request.setAttribute("advertiser", advertiser);
		return (mapping.findForward("viewAdvertiser"));
	}
	
	public ActionForward saveAdvertiser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));
		
		AdvertiserForm	gf = (AdvertiserForm)form;
		String	password = gf.getPassword();
		String	email = gf.getEmail();
		String	password2 = gf.getPassword2();
		
		Advertiser	advertiser = gf.extract(getAdvertiserService());
		Long	id = advertiser.getId();
		String	name = advertiser.getName();
		boolean	isNew = false;
		String	username = advertiser.getUsername();
		
		if (id == null) {
			isNew = true;
		}
		
		ActionMessages	errors = getErrors(request);
		if (StringUtils.isEmpty(password)) {
			errors.add("password", new ActionMessage("error.invalidPassword", password));
			saveErrors(request, errors);
		}
		if (!WebUtils.xequals(password, password2)) {
			errors.add("password", new ActionMessage("error.passwordMismatch", password));
			saveErrors(request, errors);
		}
		if (StringUtils.isEmpty(username)) {
			errors.add("username", new ActionMessage("error.invalidId", id));
			saveErrors(request, errors);
		}
		if (StringUtils.isEmpty(email)) {
			errors.add("email", new ActionMessage("error.invalidEmail", email));
			saveErrors(request, errors);
		}
		if (errors.size() > 0) {
			return (mapping.getInputForward());
		}
		
		try {
			getAdvertiserService().save(advertiser);
		}
		catch (AlreadyExistsException e) {
			errors.add("name", new ActionMessage("error.advertiserAlreadyExists", advertiser.getName()));
			saveErrors(request, errors);
			return mapping.getInputForward();
		}
		
		request.setAttribute("advertiser", adver);
		return (mapping.findForward("adWelcome"));
	}
}
