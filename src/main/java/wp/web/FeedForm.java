package wp.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.struts.validator.ValidatorForm;
import org.hibernate.tool.hbm2x.StringUtils;

import wp.core.Utils;
import wp.model.Feed;

/**
 * @struts.form name="feedForm"
 * @author Jeff
 *
 */
public class FeedForm extends ValidatorForm {

	private String	id;
	private String	name;
	private String	ratings;
	private String	earliest;
	private String	latest;
	private String	snippetSize;
	private boolean	remove;
	private String	feedUrl;
	private String	frequency;
	
	
	
	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public void	bind (Feed feed, String feedUserUrlPiece) {
		id = feed.getId() == null ? "" : feed.getId().toString();
		name = feed.getName();
		ratings = feed.getRatings();
		if (feed.getEarliest() != null)
			earliest = Utils.stringStorage(feed.getEarliest());
		else if (StringUtils.isEmpty(id))
			earliest = Utils.stringStorage(new Date());
		
		if (feed.getLatest() != null)
			latest = Utils.stringStorage(feed.getLatest());
		else if (StringUtils.isEmpty(id))
			latest = Utils.stringStorage(new Date());
	
		frequency = String.valueOf(feed.getFrequency());
		snippetSize = String.valueOf(feed.getSnippetSize());
		
		try {
			feedUrl = feedUserUrlPiece + "&feed=" + URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Feed	extract (Feed existing) {
		if (existing == null)
			existing = new Feed();
		
		if (StringUtils.isNotEmpty(earliest))
			existing.setEarliest(Utils.getTimestamp(earliest));
		
		if (StringUtils.isNotEmpty(latest))
			existing.setLatest(Utils.getTimestamp(latest));
		existing.setName(name);
		
		existing.setRatings(ratings);
		
		if (StringUtils.isNotEmpty(snippetSize)) {
			try {
				existing.setSnippetSize(Integer.parseInt(snippetSize));
			}
			catch (NumberFormatException e) { }
		}
		if (StringUtils.isNotEmpty(frequency)) {
			try {
				existing.setFrequency(Integer.parseInt(frequency));
			}
			catch (NumberFormatException e) { }
		}
		return (existing);
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @struts.validator type="required"
	 * @struts.validator-args arg0resource="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
	public String getRatings() {
		return ratings;
	}
	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	public String getEarliest() {
		return earliest;
	}
	public void setEarliest(String earliest) {
		this.earliest = earliest;
	}
	public String getLatest() {
		return latest;
	}
	public void setLatest(String latest) {
		this.latest = latest;
	}
	
	
	public String getSnippetSize() {
		return snippetSize;
	}
	
	/**
	 * @struts.validator type="integer"
	 * @struts.validator-args arg0resource="snippetSize"
	 */
	public void setSnippetSize(String snippetSize) {
		this.snippetSize = snippetSize;
	}
	
}
