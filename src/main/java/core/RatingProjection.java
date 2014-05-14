package wp.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

public class RatingProjection {

	private String	url;
	private String	rating;
	private String	title;
	private String	snippet;
	private String	userId;
	private Date	date;
	private String	mainPhotoId;
	private Integer	maxRating;
	
	public Integer getMaxRating() {
		return maxRating;
	}

	public void setMaxRating(Integer maxRating) {
		this.maxRating = maxRating;
	}

	public String	getEncodedUrl () throws UnsupportedEncodingException {
		return (url == null ? "" : URLEncoder.encode(url, "UTF-8"));
	}
	
	public String getMainPhotoId() {
		return mainPhotoId;
	}
	public void setMainPhotoId(String mainPhotoId) {
		this.mainPhotoId = mainPhotoId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String	getUnicodeSnippet () {
		StringBuffer	buf = new StringBuffer();
		
		if (snippet != null) {
			for (int i = 0; i < snippet.length(); i++)
				buf.append(("&#" + snippet.codePointAt(i) + ";"));
		}
		return (buf.toString());
	}
	
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	
}
