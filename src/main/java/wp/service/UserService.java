package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.core.EmailWithOtherUserException;
import wp.core.MaxUsersException;
import wp.core.RatingOutOfRangeException;
import wp.core.UserMatch;
import wp.model.Alert;
import wp.model.CrawlerImpl;
import wp.model.Feed;
import wp.model.Recommendation;
import wp.model.User;

public interface UserService {

	User	findUser (String userid) throws SQLException;
	  
	User	findUserByCode (String code) throws SQLException;

	User	findOrCreateAnonUser (String userid) throws SQLException;
	
	public String	generateNewUserConfirmCode (String userId);
	
	public boolean	checkUserConfirmCode (String userId, String code);
	
	User	createUser (String userid, Integer maxRating, String email, String password, 
			String ratingsList, boolean premium, boolean admin, String name, boolean ratingsPublic) 
		throws AlreadyExistsException, EmailWithOtherUserException, RatingOutOfRangeException, MaxUsersException, SQLException;
	
	void	save (User user) throws SQLException;
	
	void	delete (User user) throws SQLException;
	
	List<User>	listUsers (String filter) throws SQLException;
	
	void	addFriend (User user, String userId) throws SQLException;
	
	void	removeFriend (User user, String userId) throws SQLException;
	
	void	addGroup (User user, Long groupId) throws SQLException;
	
	void	removeGroup (User user, Long groupId) throws SQLException;
	
	void	saveRecommendations (User user, List<Recommendation> recs) throws SQLException;
	
	void	removeRecommendations (User user, List<Long> recIds) throws SQLException;
	
	void	saveAlerts (User user, List<Alert> alerts) throws SQLException;
	
	void	removeAlerts (User user, List<Long> alertIds) throws SQLException;

	void	saveFeeds (User user, List<Feed> feeds) throws SQLException;
	
	void	removeFeeds (User user, List<Long> feedIds) throws SQLException;

	void	saveCrawlers (User user, List<CrawlerImpl> crawlers) throws SQLException;
	
	void	removeCrawlers (User user, List<Long> crawlerIds) throws SQLException;

	void	enable (String userId, boolean enable) throws SQLException;
	
	void	sendMessage (String fromUserId, String toUserId, String subject, String message) throws SQLException;
	
	void	addPhoto (User user, byte [] data, String imageId, String tag) throws SQLException;
	
	void	removePhoto (User user, Long imageId) throws SQLException;
	
	List<UserMatch>	findSimilarUsersTo (String userId) throws SQLException;
	
	void	inheritRatingsFrom (String inheritFromUserId, User toUser) throws SQLException;
	
	void	updateWebStats (User user, String url, int numKeystrokesInt, int timeInt) throws SQLException;
}
