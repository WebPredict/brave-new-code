package wp.web;

import java.util.ArrayList;
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

import wp.core.IdNameProjection;
import wp.model.Ad;
import wp.model.AdProblem;
import wp.model.Advertiser;

/**
 * @struts.action path="/ListAds" parameter="listAds" validate="false"
 *   name="adsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listAds" path="/ListAds.jsp"
 * @spring.bean name="/ListAds"
 * @struts.action-forward name="unauthorized" path="/Unauthorized.jsp"
 * 
 * @struts.action path="/DeleteAd" parameter="deleteAd" validate="false"
 *   name="adsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listAds" path="/ListAds.jsp"
 * @spring.bean name="/DeleteAd"
 * 
 * @struts.action path="/EditAd" parameter="editAd" validate="false"
 *   name="adForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="editAd" path="/AdEdit.jsp"
 * @spring.bean name="/EditAd"
 * 
 * @struts.action path="/ViewAd" parameter="viewAd" validate="false"
 *   name="adForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewAd" path="/AdView.jsp"
 * @spring.bean name="/ViewAd"
 * 
 * @struts.action path="/SaveAd" parameter="saveAd" validate="true"
 *   name="adForm" scope="request" input="/EditAd.do"
 * @struts.action-forward name="saveAd" path="/AdEdit.jsp"
 * @struts.action-forward name="savedAd" path="/EditAdvertiser.do"
 * @spring.bean name="/SaveAd"
 * 
 * @author Jeff
 *
 */
public class AdAction extends BaseAction {

	public ActionForward listAds(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));
		
		List<Ad>	ads = adService.listAds(adver.getId());

		request.setAttribute("ads", ads);
		return (mapping.findForward("listAds"));
	}
	
	public ActionForward editAd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));
		
		String	adId = request.getParameter("adId");
		AdForm	af = (AdForm)form;
		if (!StringUtils.isEmpty(adId)) {
			Ad	ad = adver.findById(new Long(adId));
			af.bind(ad);
		}
		
		ArrayList<IdNameProjection>	list = new ArrayList<IdNameProjection>();
		for (int i = 0; i < 100; i++) 
			list.add(new IdNameProjection(String.valueOf(i), String.valueOf(i)));
		request.setAttribute("medNumList", list);

		return (mapping.findForward("editAd"));
	}

	public ActionForward viewAd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));

		String	adId = request.getParameter("adId");
		Ad		ad = adver.findById(new Long(adId));
		
		request.setAttribute("ad", ad);
		return (mapping.findForward("viewAd"));
	}
	
	public ActionForward saveAd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		
		HttpSession session = request.getSession();
		Advertiser	adver = getAdvertiserFromSession(request);

		if (adver == null) 
			return (noUserLoggedIn(mapping, request));
		
		AdForm	af = (AdForm)form;
		Ad		ad = af.extract(adService, adver); 
		Long	id = ad.getId();
		String	title = ad.getTitle();
		ActionMessages	errors = getErrors(request);
		
		if (StringUtils.isEmpty(title)) {
			errors.add("title", new ActionMessage("error.invalidTitle", id));
			saveErrors(request, errors);
		}
		
		List<AdProblem>	problems = adService.validateAd(ad);
		
		if (problems.size() > 0) {
			for (AdProblem prob : problems) {
				errors.add("title", new ActionMessage("error.invalidAd", prob.getPrettyPrint()));
				saveErrors(request, errors);
			}
		}
		if (errors.size() > 0) {
			return (mapping.getInputForward());
		}
		
		adService.save(ad);
		session.setAttribute("advertiser", getAdvertiserService().findById(adver.getId()));
		return (mapping.findForward("savedAd"));
	}
}
