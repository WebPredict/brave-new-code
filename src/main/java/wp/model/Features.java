package wp.model;

public enum Features {

	home ("Home"),
	controls ("Controls"),
	myAccount ("My Account"),
	stats ("Stats"),
	history ("History"),
	recent ("Recent Ratings"),
	recommend ("Recommend"),
	findSimilarUsers ("Find Similar Users"),
	alerts ("Alerts"),
	feeds ("Feeds"),
	crawls ("Crawls"),
	pageDiffs ("Page Diffs"),
	groups ("Groups");
	
	private final String	pretty;
	
	Features (String pretty) {
		this.pretty = pretty;
	}
	
	public String	getPrettyPrint () {
		return (pretty);
	}
	
	public String	toString () {
		return (pretty);
	}

}
