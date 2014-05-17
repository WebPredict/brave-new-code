package wp.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.model.RatedPage;
import wp.model.UserStats;

/**
 * @spring.bean id="userStatsDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class UserStatsDaoImpl extends HibernateDaoSupport implements UserStatsDao {

	public UserStats findStatsFor(String userId) throws SQLException {
		List<UserStats>	stats = getHibernateTemplate().find("from UserStats where user.userId=?", userId);
		if (stats == null || stats.size() == 0)
			return (null);
		return (stats.get(0));
	}

	@Transactional(readOnly = false)
	public void	save (UserStats stats) throws SQLException {
		
		Collection<RatedPage>	rps = stats.getRatedPages();
		if (rps != null) {
			for (RatedPage rp : rps) {
				rp.setUser(stats.getUser());
			}
		}
		
		getHibernateTemplate().save(stats);
		
	}

	@Transactional(readOnly = false)
	public void delete(UserStats stats, RatedPage rp) throws SQLException {
		stats.removeRating(rp.getUrl());
		getHibernateTemplate().delete(rp);
		getHibernateTemplate().save(stats);
	}
}
