package wp.model;

public enum ItemType {

	user ("User"),
	ratedPage ("Rated Page"),
	group ("Group");
	
	private final String	pretty;
	
	ItemType (String pretty) {
		this.pretty = pretty;
	}
	
	public String	getPrettyPrint () {
		return (pretty);
	}
}
