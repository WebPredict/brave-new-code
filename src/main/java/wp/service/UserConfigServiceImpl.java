package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.UserConfigDao;
import wp.model.UserConfig;

/**
 * @spring.bean id="userConfigService"
 * @spring.property name="userConfigDao" ref="userConfigDao"
 * @author Jeff
 *
 */
public class UserConfigServiceImpl implements UserConfigService {

	private UserConfigDao	userConfigDao;
	
	public UserConfigDao getUserConfigDao() {
		return userConfigDao;
	}

	public void setUserConfigDao(UserConfigDao userConfigDao) {
		this.userConfigDao = userConfigDao;
	}

	public void delete(UserConfig config) throws SQLException {
		userConfigDao.delete(config);
	}

	public UserConfig findByName(String name) throws SQLException {
		return userConfigDao.findByName(name);
	}

	public List<UserConfig> list(String filter) throws SQLException {
		return userConfigDao.list(filter);
	}

	public void save(UserConfig config) throws SQLException {
		userConfigDao.save(config);
	}

}
