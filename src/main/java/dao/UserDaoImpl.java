package wp.dao;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.AlreadyExistsException;
import wp.core.EmailWithOtherUserException;
import wp.core.MaxUsersException;
import wp.core.Rater;
import wp.core.RatingOutOfRangeException;
import wp.core.UserMatch;
import wp.model.Alert;
import wp.model.CategorySet;
import wp.model.CrawlerImpl;
import wp.model.Feed;
import wp.model.Friend;
import wp.model.Group;
import wp.model.ParsedPage;
import wp.model.Photo;
import wp.model.Privacy;
import wp.model.RatedPage;
import wp.model.Recommendation;
import wp.model.User;
import wp.model.UserMessage;
import wp.model.UserStats;
import wp.model.WebStat;

/**
 * @spring.bean id="userDao"
 * @spring.property name="groupDao" ref="groupDao"
 * @spring.property name="alertDao" ref="alertDao"
 * @spring.property name="photoDao" ref="photoDao"
 * @spring.property name="feedDao" ref="feedDao"
 * @spring.property name="crawlerDao" ref="crawlerDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

	public static final int	MAX_USERS = 500; // temp
	public static final int MAX_USERS_PER_EMAIL = 20;
	
	private GroupDao	groupDao;
	private PhotoDao	photoDao;
	private UserStatsDao	userStatsDao;
	private AlertDao	alertDao;
	private CrawlerDao	crawlerDao;
	private FeedDao		feedDao;
	
	public FeedDao getFeedDao() {
		return feedDao;
	}

	public void setFeedDao(FeedDao feedDao) {
		this.feedDao = feedDao;
	}

	public CrawlerDao getCrawlerDao() {
		return crawlerDao;
	}

	public void setCrawlerDao(CrawlerDao crawlerDao) {
		this.crawlerDao = crawlerDao;
	}

	public AlertDao getAlertDao() {
		return alertDao;
	}

	public void setAlertDao(AlertDao alertDao) {
		this.alertDao = alertDao;
	}

	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public GroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	@Transactional(readOnly = false)
	public void addFriend(User user, String userId) throws SQLException {
		Friend	friend = new Friend(findUser(userId), user.getUserId());
		if (!user.getFriends().contains(friend)) {
			Friend	friendUser = new Friend(user, userId);
			
			getHibernateTemplate().save(friend);
			getHibernateTemplate().save(friendUser);
			user.getFriends().add(friend);
			friend.getUser().getFriends().add(friendUser);
			save(user);
			save(friend.getUser());
		}
	}
	
	@Transactional(readOnly = false)
	public void addGroup(User user, Long groupId) throws SQLException {
		Group	group = groupDao.findById(groupId);
		if (!user.getGroups().contains(group)) {
			user.getGroups().add(group);
			//group.getMembers().add(user);
			save(user);
			//getHibernateTemplate().save(group);
		}
	}
	
	@Transactional(readOnly = false)
	public void	saveRecommendations (User user, List<Recommendation> recs) throws SQLException {
		if (recs == null)
			return;
		
		for (Recommendation rec : recs) {			
			Recommendation	existing = user.findRecommendationByUrl(rec.getUrlStr());
			if (existing == null) {
				rec.setUser(user);
				user.getRecommendationsList().add(rec);
			}
			getHibernateTemplate().save(rec);
			
		}
		save(user);
	}
	

	@Transactional(readOnly = false)
	public void	saveAlerts (User user, List<Alert> alerts) throws SQLException {
		if (alerts == null)
			return;
		
		boolean	needToSave = false;
		for (Alert alert : alerts) {
			alert.setUser(user);
			getHibernateTemplate().save(alert);
			if (!user.getAlertsList().contains(alert)) {
				user.getAlertsList().add(alert);
				needToSave = true;
			}
		}
		if (needToSave)
			save(user);
	}

	@Transactional(readOnly = false)
	public void	saveFeeds (User user, List<Feed> feeds) throws SQLException {
		if (feeds == null)
			return;
		
		boolean	needToSave = false;
		for (Feed feed : feeds) {
			feed.setUser(user);
			getHibernateTemplate().save(feed);
			if (!user.getFeedsList().contains(feed)) {
				user.getFeedsList().add(feed);
				needToSave = true;
			}
		}
		if (needToSave)
			save(user);
	}

	@Transactional(readOnly = false)
	public void	saveCrawlers (User user, List<CrawlerImpl> crawlers) throws SQLException {
		if (crawlers == null)
			return;
		
		boolean	needToSave = false;
		for (CrawlerImpl crawler : crawlers) {
			crawler.setUser(user);
			getHibernateTemplate().save(crawler);
			if (!user.getCrawlersList().contains(crawler)) {
				user.getCrawlersList().add(crawler);
				needToSave = true;
			}
		}
		if (needToSave)
			save(user);
	}
	

	@Transactional(readOnly = false)
	public void	addPhoto (User user, byte [] data, String imageId, String tag) throws SQLException {
		int	maxDim = 200;
		int	width = maxDim;
		int	height = maxDim;
		
		try {
			String	fileName = Rater.getTheRater().getDataDir() + user.getUserId() + imageId;
			File	file = new File(fileName);
			FileUtils.writeByteArrayToFile(file, data);
			ImageIcon imageIcon = new ImageIcon(fileName);  
			Image image = imageIcon.getImage();                 
			height = image.getHeight(null);  
			width  = image.getWidth(null);  
			               
			if (height > maxDim) {
				double	percent = (double)maxDim / (double)height;
				width = (int)(percent * (double)width);
				height = maxDim;
			}
			if (width > maxDim) {
				double	percent = (double)maxDim / (double)width;
				height = (int)(percent * (double)height);
				width = maxDim;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Photo	photo = new Photo();
		photo.setFilename(imageId);
		photo.setTag(tag);
		photo.setWidth(width);
		photo.setHeight(height);
		photo.setData(data);
		photo.setUser(user);
		getHibernateTemplate().save(photo);
		if (!user.getPhotoList().contains(photo)) {
			user.getPhotoList().add(photo);
			save(user);
		}
	}
	
	public String	generateNewUserConfirmCode (String userId) {
		return (Rater.getTheRater().generateNewUserConfirmCode(userId));
	}
	
	public boolean	checkUserConfirmCode (String userId, String code) {
		return (Rater.getTheRater().checkUserConfirmCode(userId, code));
	}
	
	@Transactional(readOnly = false)
	public User	findOrCreateAnonUser (String userid) throws SQLException {
		User	existing = findUser(userid);
		if (existing != null)
			return (existing);
		
		User	user = new User();
		user.setRatingsPublic(true);
		user.setPrivacy(Privacy.PRIVATE);
		user.setPassword(String.valueOf(Math.random()));
		user.setUserId(userid);
		user.setPremium(true);
		user.setMaxRating(10);
		
		CategorySet	set = new CategorySet(user, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10", "default categories");
		user.setCategorySet(set);
		getHibernateTemplate().save(set);
		save(user);

		UserStats	stats = new UserStats(user);
		userStatsDao.save(stats);
		return (user); 
	}
	
	@Transactional(readOnly = false)
	public User createUser(String userid, Integer maxRating, String email,
			String password, String ratingsList, boolean premium, boolean admin, String name,
			boolean ratingsPublic) throws AlreadyExistsException, EmailWithOtherUserException,
			RatingOutOfRangeException, MaxUsersException, SQLException {
		
		if (findUser(userid) != null)
			throw new AlreadyExistsException("User", userid);

		List<User>	existing = findUsersByEmail(email);
		
		if (existing != null && existing.size() > MAX_USERS_PER_EMAIL)
			throw new EmailWithOtherUserException(email);
		
		if (listUsers(null).size() >= MAX_USERS)
			throw new MaxUsersException (MAX_USERS);
		
		User	user = new User();
		user.setEmailAddr(email);
		user.setConsiderAds(true);
		user.setConsiderPopups(true);
		user.setRatingsPublic(ratingsPublic);
		user.setPrivacy(ratingsPublic ? Privacy.PUBLIC : Privacy.PRIVATE);
		user.setAdmin(admin);
		user.setName(name);
		user.setPassword(password);
		user.setUserId(userid);
		user.setPremium(premium);
		user.setMaxRating(maxRating);
		
		CategorySet	set = new CategorySet(user, ratingsList, name);
		user.setCategorySet(set);
		save(user);
		UserStats	stats = new UserStats(user);
		userStatsDao.save(stats);
		return (user); 
	}

	@Transactional(readOnly = false)
	public void enable(String userId, boolean enable) throws SQLException {
		User	user = findUser(userId);
		user.setDisabled(!enable);
		save(user);
	}

	/*
	public List<UserMatch> findSimilarUsersToOld(String userId)
			throws SQLException {
		UserStats	stats = userStatsDao.findStatsFor(userId);
		List<User>	users = listUsers(null);	
		ArrayList<UserMatch>	filtered = new ArrayList<UserMatch>();

		HashMap<URL, RatedPage>	rps = stats.getRatedPagesMap();
		ArrayList<URL>	urls = new ArrayList<URL>();
		int	numToTry = 1;
		for (URL url : rps.keySet()) {
			urls.add(url);
			if (urls.size() >= numToTry)
				break;
		}
		Rater	rater = Rater.getTheRater();
		int	maxUsersToTry = 20; // TODO reasonable value here
		int	usersTried = 0;
		for (User usr : users) {
			if (!usr.getUserId().equals(userId)) {
		
				UserStats	usrStats = userStatsDao.findStatsFor(userId);
				if (usrStats.getRatedPages().size() == 0)
					continue;
				
				int	total = 0;
				for (int i = 0; i < urls.size(); i++) {
					URL	url = urls.get(i);
					Prediction	pred = rater.predictRating(usrStats, url, false, null, null);
					RatedPage	rp = rps.get(url);
					if (pred.isSuccess()) {
						//total += Math.abs(rp.getRating() - pred.getRating());
						if (!rp.getRating().equals(pred.getRating()))
							total++;
					}

				}
				
				UserMatch	match = new UserMatch();
				match.setUserId(usr.getUserId());
				match.setName(usr.getName());
				match.setDescription(usr.getDescription());
				match.setMatchQuality(total);
				filtered.add(match);
				usersTried++;
				
				if (usersTried >= maxUsersToTry)
					break;
			}
		}
		
		Collections.sort(filtered, new Comparator () {

			public int compare(Object arg0, Object arg1) {
				UserMatch	r0 = (UserMatch)arg0;
				UserMatch	r1 = (UserMatch)arg1;
				
				return (r0.getMatchQuality() - r1.getMatchQuality());
			}
			
		});
		
		return (filtered);
	}
*/
	
	public List<UserMatch> findSimilarUsersTo(String userId)
	throws SQLException {
		UserStats	stats = userStatsDao.findStatsFor(userId);
		List<User>	users = listUsers(null);	
		ArrayList<UserMatch>	filtered = new ArrayList<UserMatch>();
		HashMap<URL, RatedPage>	rps = stats.getRatedPagesMap();
		
		int	maxUsersToTry = 300; // TODO reasonable value here
		int	usersTried = 0;
				
		for (User usr : users) {
			String		theirUserId = usr.getUserId();
			
			if (!theirUserId.equals(userId)) {
				
				String	query = "select rp.urlStr, rp.rating from RatedPage rp, User user where rp.user.id = " + 
					"user.id and user.userId = ? group by rp.user.id";
			 		
				List	result = getHibernateTemplate().find(query, theirUserId);

				if (result == null)
					continue;
				HashMap<String, String>	theirMap = new HashMap<String, String>();
				
				if (result != null) {
					for (Object entry : result) {
						String	urlStr = (String)((Object [])entry) [0];
						String	rating = (String)((Object [])entry) [1];
						theirMap.put(urlStr, rating);
					}
				}

				double			total = 0;
				Set<URL>	urls = rps.keySet();
				for (URL url : urls) {
					RatedPage	rp = rps.get(url);
					String		myRating = rp.getRating();
					
					String	theirRating = theirMap.get(url.toString());
					if (theirRating != null) {
						if (myRating.equalsIgnoreCase(theirRating))
							total++;
						else
							total += .25; // give a little credit just for having some pages in common
					}
					
				}
				
				UserMatch	match = new UserMatch();
				match.setUserId(usr.getUserId());
				match.setName(usr.getName());
				match.setMainPhotoId(usr.getMainPhotoId());
				match.setDescription(usr.getDescription());
				match.setMatchQuality(total);
				filtered.add(match);
				usersTried++;

				if (usersTried >= maxUsersToTry)
					break;
			}
		}

		Collections.sort(filtered, new Comparator () {

			public int compare(Object arg0, Object arg1) {
				UserMatch	r0 = (UserMatch)arg0;
				UserMatch	r1 = (UserMatch)arg1;

				int		qual0 = (int)(r0.getMatchQuality() * 100d);
				int		qual1 = (int)(r1.getMatchQuality() * 100d);
				
				return (qual1 - qual0);
			}

		});

		return (filtered);
	}


	public User findUser(String userid) throws SQLException {
		List<User>	users = getHibernateTemplate().find("from User where userId=?", userid);
		User	ret = users == null || users.size() == 0 ? null : users.get(0);
		return (ret);
	}

	public User findUserByCode(String code) throws SQLException {
		List<User>	users = getHibernateTemplate().find("from User where code=?", code);
		User	ret = users == null || users.size() == 0 ? null : users.get(0);
		return (ret);
	}

	public List<User> findUsersByEmail(String email) throws SQLException {
		if (StringUtils.isEmpty(email))
			return (null);
		
		List<User>	users = getHibernateTemplate().find("from User where emailAddr=?", email);
		return (users);
	}
	
	public List<User> listUsers(String filter) throws SQLException {
		if (StringUtils.isEmpty(filter))
			return (getHibernateTemplate().find("from User"));

		String	perFilter = "%" + filter + "%";
		return (getHibernateTemplate().find("from User where userId like ? or name like ?", new String [] {perFilter, perFilter}));
	}
	
	@Transactional(readOnly = false)
	public void removeFriend(User user, String userId) throws SQLException {
		if (user.isFriendsWith(userId)) {
			
			User	friendUser = findUser(userId);
			
			Friend	friend = user.getFriend(userId);
			Friend	otherFriend = friendUser.getFriend(user.getUserId());
			
			user.getFriends().remove(friend);
			friendUser.getFriends().remove(otherFriend);
			save(user);
			save(friendUser);
			getHibernateTemplate().delete(friend);
			getHibernateTemplate().delete(otherFriend);
		}
	}

	@Transactional(readOnly = false)
	public void removeGroup(User user, Long groupId) throws SQLException {
		Group	group = groupDao.findById(groupId);
		if (user.getGroups().contains(group)) {			
			
			group.getMembers().remove(user);
			user.getGroups().remove(group);
			save(user);
			getHibernateTemplate().save(group);
		}
	}

	@Transactional(readOnly = false)
	public void removeRecommendations(User user, List<Long> recIds) throws SQLException {
//		for (Long recId : recIds) {
//			Recommendation	rec = recommendationDao.findById(recId);
//			if (user.getRecommendationsList().contains(rec)) {				
//				user.getRecommendationsList().remove(rec);
//				save(user);
//				getHibernateTemplate().delete(rec);
//			}
//		}
	}


	@Transactional(readOnly = false)
	public void removeAlerts(User user, List<Long> alertIds) throws SQLException {
		for (Long alertId : alertIds) {
			Alert	alert = alertDao.findById(alertId); 
			if (user.getAlertsList().contains(alert)) {				
				user.getAlertsList().remove(alert);
				getHibernateTemplate().delete(alert);
			}
		}
		save(user);
		
	}

	@Transactional(readOnly = false)
	public void removeFeeds(User user, List<Long> feedIds) throws SQLException {
		for (Long feedId : feedIds) {
			Feed	feed = feedDao.findById(feedId); 
			if (user.getFeedsList().contains(feed)) {				
				user.getFeedsList().remove(feed);
				getHibernateTemplate().delete(feed);
			}
		}
		save(user);
		
	}

	@Transactional(readOnly = false)
	public void removeCrawlers(User user, List<Long> crawlerIds) throws SQLException {
		for (Long crawlerId : crawlerIds) {
			CrawlerImpl crawler = crawlerDao.findById(crawlerId); 
			if (user.getCrawlersList().contains(crawler)) {				
				user.getCrawlersList().remove(crawler);
				getHibernateTemplate().delete(crawler);
			}
		}
		save(user);
		
	}

	@Transactional(readOnly = false)
	public void removePhoto(User user, Long imageId) throws SQLException {
		List<Photo>	photos = user.getPhotoList();
		ArrayList<Photo>	filtered = new ArrayList<Photo>();
		Photo	toRemove = null;
		for (Photo p : photos) {
			if (!p.getId().equals(imageId)) 
				filtered.add(p);
			else
				toRemove = p;
		}
		
		user.setPhotoList(filtered);
		save(user);
		
		if (toRemove != null)
			photoDao.delete(toRemove); 
	}

	@Transactional(readOnly = false)
	public void save(User user) throws SQLException {
		getHibernateTemplate().save(user.getCategorySet());
		getHibernateTemplate().save(user);
	}

	@Transactional(readOnly = false)
	public void sendMessage(String fromUserId, String toUserId, String subject,
			String message) throws SQLException {
		User	user = findUser(toUserId);
		
		List<UserMessage>	msgs = user.getMsgList();
		UserMessage	msg = new UserMessage();
		msg.setSubject(subject);
		msg.setMsg(message);
		msg.setUserIdFrom(fromUserId);
		msgs.add(msg);
		user.setMsgList(msgs);
		msg.setUser(user);
		user.getMsgList().add(msg);
		getHibernateTemplate().save(msg);
		save(user);
	}

	@Transactional(readOnly = false)
	public void inheritRatingsFrom(User fromUser, User toUser)
			throws SQLException {
		UserStats	stats = userStatsDao.findStatsFor(fromUser.getUserId());
		
		if (stats != null) {
			Collection<RatedPage>	rps = stats.getRatedPages();
			if (rps != null) {
				UserStats	toStats = userStatsDao.findStatsFor(toUser.getUserId());

				for (RatedPage rp : rps) {
					URL	url = rp.getUrl();
					String	comment = rp.getComment();
					String	rating = rp.getRating();
					ParsedPage	pp = rp.getParsedPage();

					toStats.pageRated(rating, url, pp, comment);					
				}
				userStatsDao.save(toStats);
			}
			
		}
	}
	
	@Transactional(readOnly = false)
	public void	delete (User user) throws SQLException {
		UserStats	stats = userStatsDao.findStatsFor(user.getUserId());
		getHibernateTemplate().delete(user);
		getHibernateTemplate().delete(stats);
	}

	@Transactional(readOnly = false)
	public void updateWebStats(User user, String url, int numKeystrokes, int time) throws SQLException {
		List<WebStat>	stats = getHibernateTemplate().find("from WebStat where user.id = ? and url = ?", new Object [] {user.getId(), url});
		if (stats == null || stats.size() == 0) {
			WebStat	stat = new WebStat();
			stat.setUser(user);
			stat.setUrlStr(url);
			stat.setNumKeystrokes(numKeystrokes);
			stat.setSecondsSpent(time);
			getHibernateTemplate().save(stat);
		}
		else {
			WebStat	stat = stats.get(0);
			stat.setNumKeystrokes(stat.getNumKeystrokes() + numKeystrokes);
			stat.setSecondsSpent(stat.getSecondsSpent() + time);
			getHibernateTemplate().save(stat);
		}
	}
}
