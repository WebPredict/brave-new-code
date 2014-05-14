package wp.dao;

import java.sql.SQLException;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.core.RecentStats;
import wp.core.SearchResult;
import wp.model.Ad;
import wp.model.RatedPage;
import wp.model.User;

public interface AdDao {

	Ad	findById (Long advertiserId, Long adId) throws SQLException;
	
	Ad	create (Long advertiserId, String title, String description) 
		throws AlreadyExistsException, SQLException;
	
	void	delete (Ad ad) throws SQLException;
	
	void	save (Ad ad) throws SQLException;
	
	List<Ad>	listAds (Long advertiserId) throws SQLException;
	
	List<Ad>	listAllAds (String filter, int num) throws SQLException;
	
	List<Ad>	getAdsFor (RecentStats context, int num) throws SQLException;
	
	List<Ad>	getAdsFor (List<RatedPage> context, int num) throws SQLException;
		
	public List<Ad> getSearchAds(List<SearchResult> results, int num) throws SQLException;
	
	List<Ad>	getAdsFor (User user, int num) throws SQLException;
}
