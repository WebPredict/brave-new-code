package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.model.UserConfig;

/**
 * @spring.bean id="userConfigDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 */
public class UserConfigDaoImpl extends HibernateDaoSupport implements UserConfigDao {

	@Transactional(readOnly=false)
	public void delete(UserConfig config) throws SQLException {

		getHibernateTemplate().delete(config);
	}

	public UserConfig findByName(String name) throws SQLException {
		List<UserConfig>	users = getHibernateTemplate().find("from UserConfig where name=?", name);
		UserConfig ret = users == null || users.size() == 0 ? null : users.get(0);
		return (ret);
	}

	public List<UserConfig> list(String filter) throws SQLException {
		return getHibernateTemplate().find("from UserConfig");
	}

	@Transactional(readOnly=false)
	public void save(UserConfig config) throws SQLException {

		getHibernateTemplate().save(config.getCategorySet());
		getHibernateTemplate().save(config);
	}

}
