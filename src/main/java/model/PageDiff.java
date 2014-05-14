package wp.model;

public class PageDiff {

	private String	urlStr;
	private String	title;
	private String	snippet;
	private double	diffScore;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	public double getDiffScore() {
		return diffScore;
	}
	public void setDiffScore(double diffScore) {
		this.diffScore = diffScore;
	}
	
	
}
