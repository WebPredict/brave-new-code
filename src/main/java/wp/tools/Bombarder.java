package wp.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import wp.utils.WebUtils;

/**
 * Sends a combination of rate/predict requests to server at a certain rate and for a certain duration, and
 * collects stats.
 * @author Jeff
 *
 */
public class Bombarder {

	private String	urlFilename = "C:/WebPredictData/testurls.txt";
	private String	serverURL = "http://www.webpredict.net/wp2/";
	private double	actionsPerMinutePerUser = 6;
	private double	probActionIsPrediction = .9;
	private int		testDurationSeconds = 30;
	private boolean	semiRandom = true;
	private ArrayList<String>	users = new ArrayList<String>();
	private ArrayList<String>	ratings = new ArrayList<String>();
	private ArrayList<String>	links = new ArrayList<String>();
	private long	millisBetweenActions;
	private long	startTime;
	
	private int		totalRequestsMade = 0;
	private long	totalTimeWaited = 0;
	
	public synchronized void	updateStats (long moreTime) {
		totalRequestsMade++;
		totalTimeWaited += moreTime;
	}
	
	private int		nextPageIdx = 0;
	
	public synchronized int		getNextPageIndex () {
		int	idx = nextPageIdx;
		
		nextPageIdx++;
		
		if (nextPageIdx >= links.size())
			nextPageIdx = 0;
		return (idx);
	}
	
	public static void main (String [] args) throws Exception {
		
		String	urlFilename = args [0];
		String	serverURL = args [1];
		Double	actionsPerMinutePerUser = Double.parseDouble(args [2]);
		Double	probActionIsPrediction = Double.parseDouble(args [3]);
		Integer	testDurationSeconds = Integer.parseInt(args [4]);
		boolean	semiRandom = true;
				
		new Bombarder(urlFilename, serverURL, actionsPerMinutePerUser, probActionIsPrediction, 
				testDurationSeconds).run();
	}
	
	public Bombarder (String urlFilename, String serverURL, Double actionsPerMinutePerUser, 
			Double probActionIsPrediction, Integer testDurationSeconds) throws Exception {
		this.urlFilename = urlFilename;
		this.serverURL = serverURL;
		this.actionsPerMinutePerUser = actionsPerMinutePerUser;
		this.testDurationSeconds = testDurationSeconds;
		this.probActionIsPrediction = probActionIsPrediction;

		millisBetweenActions = actionsPerMinutePerUser == 0 ? Long.MAX_VALUE : (long)(60000d / actionsPerMinutePerUser);
		
		BufferedReader	reader = null;
		try {
			reader = new BufferedReader(new FileReader(urlFilename));
			String	line;
			while ((line = reader.readLine()) != null)
				links.add(line);
			
		}
		finally {
			if (reader != null)
				reader.close();
		}
	
		
		// Users list:
		try {
			reader = new BufferedReader(new FileReader("C:/WebPredictData/userslist.txt"));
			String	line;
			while ((line = reader.readLine()) != null)
				users.add(line);
			
		}
		finally {
			if (reader != null)
				reader.close();
		}
		
		// Ratings list:
		try {
			reader = new BufferedReader(new FileReader("C:/WebPredictData/ratingslist.txt"));
			String	line;
			while ((line = reader.readLine()) != null)
				ratings.add(line);
			
		}
		finally {
			if (reader != null)
				reader.close();
		}
	}
	
	public void	run () {
		startTime = System.currentTimeMillis();
		for (int i = 0; i < users.size(); i++) {
			new Thread (new BombarderRunner(users.get(i))).start();
		}
		
	}
	
	
	class BombarderRunner implements Runnable {
	
		private String	userId;
		public BombarderRunner (String userId) {
			this.userId = userId;
		}
		
		public void	run () {
			WebUtils	wu = new WebUtils();
			while (startTime + testDurationSeconds * 1000l > System.currentTimeMillis()) {
				
				long	requestStart = System.currentTimeMillis();
				
				int		nextIdx = getNextPageIndex();
				String	curLink = links.get(nextIdx);
				
//				if (semiRandom)
//					nextIdx = new Random().nextInt(links.size());
//				else
//					if (nextLinkIdx >= links.size())
//						nextLinkIdx = 0;
				
				try {
					
					boolean	doingPrediction = Math.random() < probActionIsPrediction;
					
					URL	requestURL;
					if (doingPrediction)
						requestURL = new URL(serverURL + "PredictLinkAjax.do?testUserId=" + userId + "&url=" + URLEncoder.encode(curLink, "UTF-8"));
					else {
						String	rating = ratings.get(new Random().nextInt(ratings.size()));
						requestURL = new URL(serverURL + "RateLinkAjax.do?testUserId=" + userId + 
								"&rating=" + rating + "&url=" + URLEncoder.encode(curLink, "UTF-8"));
					}
					// TODO: make rating configurable
					WebUtils.RawContent	rc = wu.getRawContent(requestURL, null);
					
					long	requestEnd = System.currentTimeMillis();
					long	requestTime = requestEnd - requestStart;
					System.out.println("REQUEST: " + requestURL + " TOOK: " + requestTime + " MILLISECONDS.");
					
					updateStats(requestTime);
					
					long	sleepTime = millisBetweenActions - requestTime;
					if (sleepTime < 10)
						sleepTime = 10;
					Thread.sleep(sleepTime);
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			double	average = (double)totalTimeWaited / (double)totalRequestsMade;
			
			System.out.println("\nTOTAL TIME SO FAR: " + totalTimeWaited + " TOTAL REQUESTS SO FAR: " + 
					totalRequestsMade + " AVERAGE TIME PER REQUEST SO FAR: " + average + "\n");
		}
	}
}
