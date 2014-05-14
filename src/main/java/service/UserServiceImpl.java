package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.core.EmailWithOtherUserException;
import wp.core.MaxUsersException;
import wp.core.RatingOutOfRangeException;
import wp.core.UserMatch;
import wp.dao.GroupDao;
import wp.dao.UserDao;
import wp.model.Alert;
import wp.model.CrawlerImpl;
import wp.model.Feed;
import wp.model.Recommendation;
import wp.model.User;

/**
 * @spring.bean id="userService"
 * @spring.property name="userDao" ref="userDao"
 * @spring.property name="groupDao" ref="groupDao"
 * @author Jeff
 *
 */
public class UserServiceImpl implements UserService {

	private UserDao	userDao;
	private GroupDao	groupDao;
	
	public GroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void	save (User user) throws SQLException {
		userDao.save(user);
	}
	
	public void	sendMessage (String fromUserId, String toUserId, String subject, String message) throws SQLException {
		userDao.sendMessage(fromUserId, toUserId, subject, message);
	}
	
	public void removePhoto(User user, Long imageId) throws SQLException {
		userDao.removePhoto(user, imageId);
	}
	
	public void	addPhoto (User user, byte [] data, String imageId, String tag) throws SQLException {
		userDao.addPhoto(user, data, imageId, tag);
	}
	
	public void	addFriend (User user, String userId) throws SQLException {
		userDao.addFriend(user, userId);
	}
	
	public void	saveRecommendations (User user, List<Recommendation> recs) throws SQLException {
		userDao.saveRecommendations(user, recs);
	}

	public void	removeRecommendations (User user, List<Long> recIds) throws SQLException {
		userDao.removeRecommendations(user, recIds);
	}

	public void	saveAlerts (User user, List<Alert> alerts) throws SQLException {
		userDao.saveAlerts(user, alerts); 
	}

	public void	removeAlerts (User user, List<Long> alertIds) throws SQLException {
		userDao.removeAlerts(user, alertIds);
	}

	public void	saveFeeds (User user, List<Feed> feeds) throws SQLException {
		userDao.saveFeeds(user, feeds); 
	}

	public void	removeFeeds (User user, List<Long> feedIds) throws SQLException {
		userDao.removeFeeds(user, feedIds);
	}

	public void	saveCrawlers (User user, List<CrawlerImpl> crawlers) throws SQLException {
		userDao.saveCrawlers(user, crawlers);
	}

	public void	removeCrawlers (User user, List<Long> crawlerIds) throws SQLException {
		userDao.removeCrawlers(user, crawlerIds);
	}

	public void	removeFriend (User user, String userId) throws SQLException {
		userDao.removeFriend(user, userId);
	}
	
	public void	addGroup (User user, Long groupId) throws SQLException {
		userDao.addGroup(user, groupId);
	}
	
	public void	removeGroup (User user, Long groupId) throws SQLException {
		userDao.removeGroup(user, groupId);
	}
	
	public User	findOrCreateAnonUser (String userid) throws SQLException {
		return (userDao.findOrCreateAnonUser(userid));
	}
	
	public User	createUser (String userId, Integer maxRating, String email, String password, String ratingsList, boolean premium, 
			boolean admin, String name, boolean ratingsPublic) 
		throws AlreadyExistsException, EmailWithOtherUserException, RatingOutOfRangeException, MaxUsersException, SQLException {

		return (userDao.createUser(userId, maxRating, email, password, ratingsList, premium, admin, name, ratingsPublic));
	}

	public User	findUser (String userId) throws SQLException {
		return (userDao.findUser(userId));
	}

	public User	findUserByCode (String code) throws SQLException {
		return (userDao.findUserByCode(code));
	}

	public void enable(String userId, boolean enable) throws SQLException {
		userDao.enable(userId, enable);
	}

	public List<User> listUsers(String filter) throws SQLException {
		return (userDao.listUsers(filter));
	}
	
	public String	generateNewUserConfirmCode (String userId) {
		return (userDao.generateNewUserConfirmCode(userId));
	}
	
	public boolean	checkUserConfirmCode (String userId, String code) {
		return (userDao.checkUserConfirmCode(userId, code));
	}
	
	/**
	 * WARNING: very computationally intensive
	 */
	public List<UserMatch>	findSimilarUsersTo (String userId) throws SQLException
	{
		return (userDao.findSimilarUsersTo(userId));
	}

	public void inheritRatingsFrom(String inheritFromUserId, User toUser)
			throws SQLException {
		User	fromUser = findUser(inheritFromUserId);
		userDao.inheritRatingsFrom(fromUser, toUser);
	}

	public void	delete (User user) throws SQLException {
		userDao.delete(user);
	}

	public void updateWebStats(User user, String url, int numKeystrokes,
			int time) throws SQLException {
		userDao.updateWebStats(user, url, numKeystrokes, time);
	}
}
