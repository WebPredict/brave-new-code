package wp.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import wp.model.ItemType;

public class SearchResult {

	public static final int	GREAT_SCORE = 100;
	public static final int	GOOD_SCORE = 75;
	public static final int	OK_SCORE = 50;
	public static final int	BAD_SCORE = 25;
	
	private int	score;
	private String	details;
	private ItemType	type;
	private String	id;
	private String	snippet;
	private String	url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public SearchResult (int score, String details, ItemType type, String id) {
		this.score = score;
		this.details = details;
		this.type = type;
		this.id = id;
	}
	
	public String	getEncodedUrl () throws UnsupportedEncodingException {
		return (URLEncoder.encode(id, "UTF-8"));
	}
	
	public String	getId () {
		return (id);
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public ItemType getType() {
		return type;
	}
	public void setType(ItemType type) {
		this.type = type;
	}
}
