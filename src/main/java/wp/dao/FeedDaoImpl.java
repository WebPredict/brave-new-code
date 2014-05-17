package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.model.Feed;

/**
 * @spring.bean id="feedDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class FeedDaoImpl extends HibernateDaoSupport implements FeedDao {

	public Feed	findById (Long id) throws SQLException {
		return (Feed)getHibernateTemplate().get(Feed.class, id);
	}

	public Feed	findByUserAndName (Long userId, String name) throws SQLException {
		List<Feed>	feeds = getHibernateTemplate().find("from Feed where user.id = ? and name = ?", 
				new Object [] {userId, name});
		
		Feed	ret = feeds == null || feeds.size() == 0 ? null : feeds.get(0);
		return (ret);
	}

	@Transactional(readOnly=false)
	public void	save (Feed feed) throws SQLException {
		getHibernateTemplate().save(feed);
	}
}
