package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.dao.AdvertiserDao;
import wp.model.Advertiser;

/**
 * @spring.bean id="advertiserService"
 * @spring.property name="advertiserDao" ref="advertiserDao"
 * @author Jeff
 *
 */
public class AdvertiserServiceImpl implements AdvertiserService {

	private AdvertiserDao	advertiserDao;
	
	public AdvertiserDao getAdvertiserDao() {
		return advertiserDao;
	}

	public void setAdvertiserDao(AdvertiserDao advertiserDao) {
		this.advertiserDao = advertiserDao;
	}

	public Advertiser create(String id, String name, String description, String emailAddr, String password)
			throws AlreadyExistsException, SQLException {
		
		return (advertiserDao.create(id, name, description, emailAddr, password));
	}

	public void delete(Advertiser advertiser) throws SQLException {
		advertiserDao.delete(advertiser);
	}

	public Advertiser findById(Long id) throws SQLException {
		return (advertiserDao.findById(id));
	}

	public Advertiser findByUsername (String username) throws SQLException {
		return (advertiserDao.findByUsername(username));
	}
	
	public List<Advertiser>	listAdvertisers (String filter) throws SQLException {
		return (advertiserDao.listAdvertisers(filter));
	}

	public void save(Advertiser advertiser) throws SQLException, AlreadyExistsException {
		advertiserDao.save(advertiser);
	}

}
