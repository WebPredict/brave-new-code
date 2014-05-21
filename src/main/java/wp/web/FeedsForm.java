package wp.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.tool.hbm2x.StringUtils;

import wp.model.Feed;
import wp.utils.Utils;

/**
 * @struts.form name="feedsForm"
 * @author Jeff
 *
 */
public class FeedsForm extends ActionForm {

	private ArrayList<FeedForm>	feedRules = new ArrayList<FeedForm>();
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
	
	public void	bind (List<Feed> feeds, String crawlerUrl) {
		feedRules.clear();
		
		for (Feed feed : feeds) {
			FeedForm	af = new FeedForm();
			af.bind(feed, crawlerUrl);
			feedRules.add(af);
		}
		
		Date	today = new Date();
		Date	tomorrow = new Date(today.getTime () + 86400000l);
		Date	lastWeek = new Date(today.getTime() - 86400000l * 7l);
		
		FeedForm	extra = new FeedForm();
		extra.setEarliest(Utils.stringStorage(lastWeek));
		extra.setLatest(Utils.stringStorage(tomorrow));
		feedRules.add(extra);
		extra = new FeedForm();
		extra.setEarliest(Utils.stringStorage(lastWeek));
		extra.setLatest(Utils.stringStorage(tomorrow));
		feedRules.add(extra);
		extra = new FeedForm();
		extra.setEarliest(Utils.stringStorage(lastWeek));
		extra.setLatest(Utils.stringStorage(tomorrow));
		feedRules.add(extra);		
	}
	
	public int	getNumFeeds () {
		return (feedRules.size());
	}
	
	public List<FeedForm>	getFeedRulesList() {
		return (feedRules);
	}

	public FeedForm	getFeedRulesListElement (int idx) {
		while (feedRules.size() <= idx)
			feedRules.add(new FeedForm());
		return (feedRules.get(idx));
	}
	
	public void	validateFeeds (ActionMessages errors, ActionMapping mapping, HttpServletRequest request) {
		
	}

	public List<Long>	extractFeedsToDelete (List<Feed> existing) {

		ArrayList<Long>	toDelete = new ArrayList<Long>();
		for (int i = 0; i < existing.size(); i++) {
			if (feedRules.size() <= i)
				break;
			if (feedRules.get(i).isRemove())
				toDelete.add(existing.get(i).getId());
		}
		
		return (toDelete);
	}
	
	public List<Feed>	extractFeeds (List<Feed> existing) {
		List<Feed>	newList = new ArrayList<Feed>();
		
		ArrayList<FeedForm>	newRules = new ArrayList<FeedForm>();
		for (FeedForm af : feedRules) {
			if (!af.isRemove())
				newRules.add(af);
		}
		feedRules = newRules;

		int	counter = 0;
		for (FeedForm af : feedRules) {
			
			String	name = af.getName();
			if (StringUtils.isEmpty(name))
				continue;
			
			Feed	existingFeed = existing.size() > counter ? existing.get(counter) : null;
			
			counter++;
			existingFeed = af.extract(existingFeed);
			newList.add(existingFeed);
		}
		
		return (newList);
	}
}
