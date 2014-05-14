package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import wp.core.SearchResult;

/**
 * @spring.bean id="searchDao"
 * 
 * @spring.property name="userDao" ref="userDao"
 * @spring.property name="pageDao" ref="pageDao"
 * @spring.property name="groupDao" ref="groupDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class SearchDaoImpl extends HibernateDaoSupport implements SearchDao {

	private UserDao	userDao;
	private PageDao	pageDao;
	private GroupDao	groupDao;
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public PageDao getPageDao() {
		return pageDao;
	}
	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	public GroupDao getGroupDao() {
		return groupDao;
	}
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
	public List<SearchResult> find(SearchInfo info) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
