package wp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.tool.hbm2x.StringUtils;

import wp.model.Crawler;
import wp.model.CrawlerImpl;
import wp.model.CrawlerStatus;
import wp.model.CrawlingIntensity;

/**
 * @struts.form name="crawlsForm"
 * @author Jeff
 *
 */
public class CrawlsForm extends ActionForm {

	private ArrayList<CrawlerForm>	crawlerRules = new ArrayList<CrawlerForm>();
	private String	doit;
	private String	clearIt;
	
	public String getClearIt() {
		return clearIt;
	}

	public void setClearIt(String clearIt) {
		this.clearIt = clearIt;
	}

	public String getDoit() {
		return doit;
	}

	public void setDoit(String doit) {
		this.doit = doit;
	}
	
	public void	bind (List<CrawlerImpl> crawlers) {
		crawlerRules.clear();
		
		for (Crawler crawler : crawlers) {
			CrawlerForm	af = new CrawlerForm();
			af.setContains(crawler.getDesirableWords());
			af.setUrl(crawler.getSeedUrl());
			af.setPredicts(crawler.getDesirablePredictions());
			af.setMaxLinksToFollow(String.valueOf(crawler.getMaxLinksToFollow()));
			af.setCanLeaveDomain(crawler.getCanLeaveDomainList());
			af.setDisable(!crawler.isEnabled());
			CrawlingIntensity	il = crawler.getIntensityLevel();
			if (il != null)
				af.setIntensity(il.toString());
			
			CrawlerStatus		status = crawler.getStatus();
			if (status != null)
				af.setStatus(status.toString());
			
			crawlerRules.add(af);
		}
		
		// add some blanks in case they want to add more
		crawlerRules.add(new CrawlerForm());
		crawlerRules.add(new CrawlerForm());
		crawlerRules.add(new CrawlerForm());
		
	}
	
	public int	getNumCrawlers () {
		return (crawlerRules.size());
	}
	
	public List<CrawlerForm>	getCrawlerRulesList() {
		return (crawlerRules);
	}

	public CrawlerForm	getCrawlerRulesListElement (int idx) {
		while (crawlerRules.size() <= idx)
			crawlerRules.add(new CrawlerForm());
		return (crawlerRules.get(idx));
	}
	
	public void	validateCrawlers (ActionMessages errors, ActionMapping mapping, HttpServletRequest request) {
		
	}

	public List<Long>	extractCrawlersToDelete (List<CrawlerImpl> existing) {

		ArrayList<Long>	toDelete = new ArrayList<Long>();
		for (int i = 0; i < existing.size(); i++) {
			if (crawlerRules.get(i).isRemove())
				toDelete.add(existing.get(i).getId());
		}
		
		return (toDelete);
	}
	
	public List<CrawlerImpl>	extractCrawlers (List<CrawlerImpl> existing) {
		List<CrawlerImpl>	newList = new ArrayList<CrawlerImpl>();
		HashMap<String, CrawlerImpl>	urlToCrawlerMap = new HashMap<String, CrawlerImpl>();
		if (existing != null) {
			for (CrawlerImpl a : existing) {
				urlToCrawlerMap.put(a.getSeedUrl(), a);
			}
		}
		
		ArrayList<CrawlerForm>	newRules = new ArrayList<CrawlerForm>();
		for (CrawlerForm af : crawlerRules) {
			if (!af.isRemove())
				newRules.add(af);
		}
		crawlerRules = newRules;
		
		for (CrawlerForm af : crawlerRules) {
			
			String	url = af.getUrl();
			if (StringUtils.isEmpty(url))
				continue;
			
			CrawlerImpl	existingCrawler = urlToCrawlerMap.get(url);
			if (existingCrawler == null)
				existingCrawler = new CrawlerImpl();
			
			existingCrawler.setSeedUrl(url);
			existingCrawler.setDesirableWords(af.getContains());
			existingCrawler.setDesirablePredictions(af.getPredicts());
			existingCrawler.setIntensityLevel(CrawlingIntensity.valueOf(af.getIntensity()));
			existingCrawler.setEnabled(!af.isDisable());
			
			try {
				existingCrawler.setMaxLinksToFollow(Integer.parseInt(af.getMaxLinksToFollow()));
			}
			catch (Exception e) {
				
			}
			existingCrawler.setCanLeaveDomainList(af.isCanLeaveDomain());
			newList.add(existingCrawler);
		}
		
		return (newList);
	}
}
