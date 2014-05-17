package wp.model;



public interface IAlert {

	void	setUrlStr (String urlStr);
	String	getUrlStr ();
	
	void	setPrediction (String predictionList);
	String	getPrediction ();
	
	void	setContains (String keywordList);
	String	getContains ();

	void	setRepeatTimePeriod (int repeatSeconds);
	int		getRepeatTimePeriod ();

	void	setSnippetSize (int maxChars);
	int		getSnippetSize ();
}
