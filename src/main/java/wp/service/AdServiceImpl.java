package wp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import wp.core.AlreadyExistsException;
import wp.core.Rater;
import wp.core.RecentStats;
import wp.core.SearchResult;
import wp.dao.AdDao;
import wp.model.Ad;
import wp.model.AdProblem;
import wp.model.RatedPage;
import wp.model.User;
import wp.utils.Utils;

/**
 * @spring.bean id="adService"
 * @spring.property name="adDao" ref="adDao"
 * @author Jeff
 *
 */
public class AdServiceImpl implements AdService {

	private AdDao	adDao;

	public AdDao getAdDao() {
		return adDao;
	}

	public void setAdDao(AdDao adDao) {
		this.adDao = adDao;
	}

	public Ad create(Long id, String title, String description)
			throws AlreadyExistsException, SQLException {
		return (adDao.create(id, title, description));
	}

	public void delete(Ad ad) throws SQLException {
		adDao.delete(ad);
	}

	public Ad findById(Long advertiserId, Long adId) throws SQLException {
		return (adDao.findById(advertiserId, adId));
	}

	public List<Ad> listAds(Long advertiserId) throws SQLException {
		return (adDao.listAds(advertiserId));
	}

	public List<Ad> listAllAds(String filter, int max) throws SQLException {
		return (adDao.listAllAds(filter, max));
	}

	public void save(Ad ad) throws SQLException {
		adDao.save(ad); 
	}

	public List<Ad> getAdsFor(List<RatedPage> context, int num)
			throws SQLException {
		return (adDao.getAdsFor(context, num));
	}

	public List<Ad> getSearchAds(List<SearchResult> results, int num)
		throws SQLException {
		return (adDao.getSearchAds(results, num));
	}

	public List<Ad> getAdsFor(RecentStats context, int num)
		throws SQLException {
		return (adDao.getAdsFor(context, num));
	}
	
	public List<Ad> getAdsFor(User user, int num) throws SQLException {
		return (adDao.getAdsFor(user, num));
	}

	public List<AdProblem> validateAd(Ad ad) {
		String	title = ad.getTitle().trim();
		String	content = ad.getContent().trim();
		
		Set<String>	uncommonWords = Utils.getUncommonWords(title);
		uncommonWords.addAll(Utils.getUncommonWords(content));
		
		List<AdProblem>	problems = new ArrayList<AdProblem>();
		
		if (uncommonWords.size() == 0)
			problems.add(AdProblem.allCommonWords);
		
		HashSet<String>	vulgarities = Rater.getTheRater().getVulgarities();
		Collection	intersection = CollectionUtils.intersection(uncommonWords, vulgarities);
		if (intersection != null && intersection.size() > 0)
			problems.add(AdProblem.vulgarities);
		
		return problems;
	}

}