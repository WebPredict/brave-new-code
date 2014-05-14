package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.AdStatDao;
import wp.model.Ad;

/**
 * @spring.bean id="adStatService"
 * @spring.property name="adStatDao" ref="adStatDao"
 * @author Jeff
 *
 */
public class AdStatServiceImpl implements AdStatService {

	private AdStatDao	adStatDao;
	
	public AdStatDao getAdStatDao() {
		return adStatDao;
	}

	public void setAdStatDao(AdStatDao adStatDao) {
		this.adStatDao = adStatDao;
	}

	public void click(Ad ad) throws SQLException {
		adStatDao.click(ad);
	}

	public void impress(List<Ad> ads) throws SQLException {
		adStatDao.impress(ads);
	}

}
