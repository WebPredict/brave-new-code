package wp.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.CachedURL;
import wp.core.Rater;
import wp.core.UserDisabledException;
import wp.core.UserQuotaException;
import wp.core.Utils;
import wp.core.WebUtils;
import wp.model.Alert;
import wp.model.AlertResult;
import wp.model.Prediction;
import wp.model.User;
import wp.model.UserStats;

/**
 * @spring.bean id="alertDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @spring.property name="userDao" ref="userDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class AlertDaoImpl extends HibernateDaoSupport implements AlertDao {

	private UserStatsDao	userStatsDao;
	private UserDao			userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	
	@Transactional(readOnly=false)
	public void	runAlertsForAll () throws SQLException {
		try {
		List<User>	users = userDao.listUsers(null);
		
		if (users == null)
			return;
		
		for (User user : users) {
			List<AlertResult>	results = runAlertsFor(user);
			
			String	emailAddr = user.getEmailAddr();
			if (results == null)
				continue;
			
			StringBuffer	buf = new StringBuffer();
			for (AlertResult result : results) {
				
				if (result.isEmail()) {
					if (buf.length() == 0) {
						buf.append("<html><body>Here is your list of alert results (created " + new Date().toString() + "):");
						buf.append("<br /><br />");	
						buf.append("<table>");
						buf.append("<tr><th>Page</th><th>Title</th><th>Snippet</th><th>Contains</th><th>Predicts As</th></tr>");
					}
					
					buf.append("<tr>");
					buf.append("<td>" + result.getUrlStr() + "</td>");
					buf.append("<td>" + result.getTitle() + "</td>");
					buf.append("<td>" + result.getSnippet() + "</td>");
					buf.append("<td>" + result.getContains() + "</td>");
					buf.append("<td>" + result.getPrediction() + "</td>");
					buf.append("</tr>");
				}
			}
			
			if (buf.length() > 0) {
				buf.append("</table>" +
						"<br /><br />Click <a href='http://www.webpredict.net/wp2/GetAlerts.do'>here</a> to configure/turn off your alert emails." +
						"<br /><br /></body></html>");
				try {
					WebUtils.sendMail("Your WebPredict Page Alerts", buf.toString(), "feedback@webpredict.net", emailAddr);
				}
				catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	}

	@Transactional(readOnly=false)
	public List<AlertResult> runAlertsFor(User user) throws SQLException {
		
		List<Alert>	alerts = user.getAlertsList();
		
		if (alerts == null)
			return (null);
		
		UserStats	stats = userStatsDao.findStatsFor(user.getUserId());

		ArrayList<AlertResult>	results = new ArrayList<AlertResult>();
		for (Alert alert : alerts) {
			
			if (alert.isCrawl() && alert.needToCrawl())
				continue; // different process
			
			String	urlStr = alert.getUrlStr();
			String	contains = alert.getContains();
			String	predictsStr = alert.getPrediction();

			Collection<String>	predicts = null;
			if (StringUtils.isNotEmpty(predictsStr))
				predicts = Utils.extractCollection(predictsStr);
			
			if (user.findAlertResult(urlStr, predicts, contains) != null)
				continue; // don't create duplicates

			Collection<String>	keywords = null;
			if (StringUtils.isNotEmpty(contains))
				keywords = Utils.extractCollection(contains);

			if (alert.isCrawl()) {
				Set<CachedURL>	crawledUrls = alert.getCrawledURLs();
				for (CachedURL url : crawledUrls) {
					Prediction pred;
					try {
						pred = Rater.getTheRater().predictRating(stats, url, false, keywords, false, 255);
						addAlertResult(pred, results, alert, predicts, contains, url.getURL().toString());
					} catch (UserQuotaException e) {
						break;
					} catch (UserDisabledException e) {
						break;
					}
					
				}		
				continue;
			}
			URL url;
			try {
				url = new URL(Utils.normalizeUrlStr(urlStr));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Prediction	pred = Rater.getTheRater().predictRating(stats, url, null, keywords, false, 255);			
			addAlertResult(pred, results, alert, predicts, contains, urlStr);
		}
		
		for (AlertResult result : results) {
			result.setUser(user);	
			result.setTimestamp(new Date());
			getHibernateTemplate().save(result);
			user.getAlertResultsList().add(result);
		}
		if (results.size() > 0)
			getHibernateTemplate().save(user);
		return (results);
	}
	
	public static void	addAlertResult (Prediction pred, ArrayList<AlertResult> results, Alert alert, 
			Collection<String> predicts, String contains, String urlStr) {
		String	first = pred.getRating();
		String	second = pred.getSecondGuess();
		if ((pred.isHasKeywords() || StringUtils.isEmpty(contains)) && 
				(predicts != null && (predicts.contains(first) || 
				(second != null && predicts.contains(second))))) {
			AlertResult	ar = new AlertResult();
			if (pred.isHasKeywords())
				ar.setContains(contains);
			if (predicts.contains(first))
				ar.setPrediction(first);
			else if (predicts.contains(second))
				ar.setPrediction(second);
			
			ar.setUrlStr(urlStr);
			ar.setTitle(pred.getTitle());
			ar.setSnippet(pred.getSnippet());
			ar.setEmail(alert.isEmail());
			ar.setAddon(alert.isAddon());
			results.add(ar);
		}
		else if (pred.isHasKeywords()) {
			// TODO which keywords???
			AlertResult	ar = new AlertResult();
			ar.setContains(contains);
			ar.setEmail(alert.isEmail());
			ar.setAddon(alert.isAddon());
			ar.setUrlStr(urlStr);
			ar.setTitle(pred.getTitle());
			ar.setSnippet(pred.getSnippet());
			results.add(ar);
		}
	}
	
	@Transactional(readOnly=false)
	public void	deleteResults (User user, List<AlertResult> results) throws SQLException {
		ArrayList<AlertResult>	toRemove = new ArrayList<AlertResult>();
		for (AlertResult result : results) 
			toRemove.add(result);
		
		for (AlertResult result : toRemove) {
			user.getAlertResultsList().remove(result);
			getHibernateTemplate().delete(result);
		}
		getHibernateTemplate().save(user);
	}
	
	@Transactional(readOnly=false)
	public void	deleteAlerts (User user, List<Alert> alerts) throws SQLException {
		ArrayList<Alert>	toRemove = new ArrayList<Alert>();
		for (Alert alert : alerts) 
			toRemove.add(alert);
		
		for (Alert alert : toRemove) {
			user.getAlertsList().remove(alert);
			getHibernateTemplate().delete(alert);
		}
		getHibernateTemplate().save(user);
	}
	
	public Alert	findById (Long id) throws SQLException {
		return (Alert)getHibernateTemplate().get(Alert.class, id);
	}
}
