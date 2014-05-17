package wp.model;

public enum AdProblem {

	allCommonWords ("Ad contains only common words"),
	vulgarities ("Ad contains vulgarities"),
	copyrighted ("Ad contains a copyrighted name");
	
	private final String	pretty;
	
	AdProblem (String pretty) {
		this.pretty = pretty;
	}
	
	public String	getPrettyPrint () {
		return (pretty);
	}
}
