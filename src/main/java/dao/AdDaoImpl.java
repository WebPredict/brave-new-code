package wp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.AdEngine;
import wp.core.AlreadyExistsException;
import wp.core.RecentStats;
import wp.core.SearchResult;
import wp.model.Ad;
import wp.model.Advertiser;
import wp.model.RatedPage;
import wp.model.User;
import wp.model.UserStats;

/**
 * @spring.bean id="adDao"
 * @spring.property name="userStatsDao" ref="userStatsDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class AdDaoImpl extends HibernateDaoSupport implements AdDao {

	private UserStatsDao	userStatsDao;
	
	static Collection<String>	GENRIC_GOOD_RATINGS = new HashSet<String>(); // TODO standardize this
	
	public static final Ad	GENERIC_AD = new Ad("Your Ad Here", "http://www.webpredict.net/ForAdvertisers.jsp", "Your ad could go here if you sign up with WebPredict.");
	
	public static List<Ad>	GENERIC_AD_LIST;
	
	static { // TEMP:
		GENRIC_GOOD_RATINGS.add("good");
		GENRIC_GOOD_RATINGS.add("great");
		GENRIC_GOOD_RATINGS.add("interesting");
		GENRIC_GOOD_RATINGS.add("excellent");
		GENRIC_GOOD_RATINGS.add("awesome");
		GENRIC_GOOD_RATINGS.add("cool");
		GENRIC_GOOD_RATINGS.add("helpful");
		GENRIC_GOOD_RATINGS.add("8");
		GENRIC_GOOD_RATINGS.add("9");
		GENRIC_GOOD_RATINGS.add("10");
		GENERIC_AD_LIST = new ArrayList();
		//GENERIC_AD_LIST.add(GENERIC_AD);
	}
	
	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	@Transactional(readOnly = false)
	public Ad create(Long advertiserId, String title, String content)
			throws AlreadyExistsException, SQLException {
		Ad	ad = new Ad(advertiserId);
		ad.setTitle(title);
		ad.setContent(content);
		getHibernateTemplate().save(ad);
		return (ad);
	}

	@Transactional(readOnly = false)
	public void delete(Ad ad) throws SQLException {
		getHibernateTemplate().delete(ad);
	}
	
	public Ad findById(Long advertiserId, Long adId) throws SQLException {
		List<Ad>	ads = getHibernateTemplate().find("from Ad where advertiserId = ? and id = ?", 
				new Object [] {advertiserId, adId});
		return (ads == null || ads.size() == 0) ? null : ads.get(0);
	}

	public List<Ad> getAdsFor(List<RatedPage> context, int num) throws SQLException {
		List<Ad>	ads = listAllAds(null, num);
		
		List<Ad>	filtered = AdEngine.getAdEngine().filter(ads, context);
		if (filtered.size() == 0)
			return (GENERIC_AD_LIST);
		return (filtered);
	}

	public List<Ad> getSearchAds(List<SearchResult> context, int num) throws SQLException {
		List<Ad>	ads = listAllAds(null, num);
		
		List<Ad>	filtered = AdEngine.getAdEngine().filterSearch(ads, context);
		if (filtered.size() == 0)
			return (GENERIC_AD_LIST);
		return (filtered);
	}

	public List<Ad> getAdsFor(RecentStats context, int num) throws SQLException {
		List<Ad>	ads = listAllAds(null, num);
		return (AdEngine.getAdEngine().getAdsFor(context, ads, GENRIC_GOOD_RATINGS, num));
	}

	public List<Ad> getAdsFor(User user, int num) throws SQLException {
		List<Ad>	ads = listAllAds(null, num);
		UserStats	stats = user == null ? null : userStatsDao.findStatsFor(user.getUserId());
		
		return (AdEngine.getAdEngine().getAdsFor(stats, ads, GENRIC_GOOD_RATINGS, num));
	}

	public List<Ad> listAds(Long advertiserId) throws SQLException {
		return (getHibernateTemplate().find("from Ad where advertiserId = ?", advertiserId));
	}

	public List<Ad> listAllAds(String filter, int num) throws SQLException {
		return (getHibernateTemplate().find("from Ad"));
	}

	@Transactional(readOnly = false)
	public void save(Ad ad) throws SQLException {
		Long	id = ad.getId();
		if (id == null) {
			Advertiser	adver = ad.getAdvertiser();
			adver.getAdsList().add(ad);
			getHibernateTemplate().save(ad);
			getHibernateTemplate().save(adver);
		}
		else
			getHibernateTemplate().save(ad);
		
	}

}
