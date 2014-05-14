package wp.model;

import java.util.Date;
import java.util.HashSet;

public class CrawlResultsExportConfig {

	private int	snippetSize;
	
	// filters:
	private Date	earliest;
	private Date	latest;
	private HashSet<String>	predictions;
	
	private String	feedname;
	private User	user;
	
}
