package wp.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.core.AlreadyExistsException;
import wp.model.Ad;
import wp.model.Advertiser;

/**
 * @spring.bean id="advertiserDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class AdvertiserDaoImpl extends HibernateDaoSupport implements AdvertiserDao {

	@Transactional(readOnly = false)
	public Advertiser create(String username, String name, String description,
			String emailAddr, String password) throws AlreadyExistsException,
			SQLException {
		if (findByUsername(username) != null)
			throw new AlreadyExistsException("Advertiser", username);
		Advertiser	adver = new Advertiser();
		adver.setUsername(username);
		adver.setName(name);
		adver.setDescription(description);
		adver.setEmail(emailAddr);
		adver.setPassword(password);
		adver.setCampaignStartDate(new Date());
		adver.setCampaignEndDate(new Date());
		save(adver);
		return (adver);
	}

	@Transactional(readOnly = false)
	public void delete(Advertiser advertiser) throws SQLException {
		getHibernateTemplate().delete(advertiser);
	}

	public Advertiser findById(Long id) throws SQLException {
		return ((Advertiser)getHibernateTemplate().get(Advertiser.class, id));
	}

	public Advertiser findByUsername(String username) throws SQLException {
		List<Advertiser>	ads = getHibernateTemplate().find("from Advertiser where username = ?", username);
		return (ads == null || ads.size() == 0) ? null : ads.get(0);
	}

	public List<Advertiser> listAdvertisers(String filter) throws SQLException {
		if (StringUtils.isEmpty(filter))
			return (getHibernateTemplate().find("from Advertiser"));
		String	filterStr = "%" + filter + "%";
		
		return (getHibernateTemplate().find("from Advertiser where name like ?", filterStr));
	}

	@Transactional(readOnly = false)
	public void save(Advertiser advertiser) throws SQLException, AlreadyExistsException {
		Long	id = advertiser.getId();
		if (id == null && findByUsername(advertiser.getName()) != null)
			throw new AlreadyExistsException("Advertiser", advertiser.getName());
	
		getHibernateTemplate().save(advertiser);
		List<Ad>	ads = advertiser.getAdsList();
		if (ads != null) {
			for (Ad ad : ads) {
				getHibernateTemplate().save(ad);
			}
		}
	}

}