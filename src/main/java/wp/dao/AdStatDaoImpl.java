package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.model.Ad;
import wp.model.AdStat;

/**
 * @spring.bean id="adStatDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class AdStatDaoImpl extends HibernateDaoSupport implements AdStatDao {

	@Transactional(readOnly = false)
	public void click(Ad ad) throws SQLException {
		ad.incrementClicks();
		getHibernateTemplate().save(ad);
		List<AdStat>	stats = ad.getStats();
		for (AdStat stat : stats)
			getHibernateTemplate().save(stat);
	}

	@Transactional(readOnly = false)
	public void impress(List<Ad> ads) throws SQLException {
		for (Ad ad : ads) {
			ad.incrementImpressions();
			getHibernateTemplate().save(ad);
			List<AdStat>	stats = ad.getStats();
			for (AdStat stat : stats)
				getHibernateTemplate().save(stat);
		}
	}

}
