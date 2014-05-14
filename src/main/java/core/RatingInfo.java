package wp.core;

import java.net.URL;

public class RatingInfo {

	private RatingStatus	status;
	private String	problem;
	private URL		url;
	private String		rating;
	
	public RatingInfo () { }
	
	public RatingInfo (RatingStatus status, String problem, URL url, String rating) {
		this.status = status;
		this.problem = problem;
		this.url = url;
		this.rating = rating;
	}
	
	public String	toString () {
		if (problem != null || status == RatingStatus.failed)
			return ("Rating failed for url + " + url + " because: " + problem + ".");
				
		return ("Rating of " + rating + " succeeded for " + url + ". (" + status + ")");
	}

	public String	getRating () {
		return (rating);
	}
	
	public RatingStatus getStatus() {
		return status;
	}

	public void setStatus(RatingStatus status) {
		this.status = status;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

}
