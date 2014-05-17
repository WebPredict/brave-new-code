package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.ListInfo;
import wp.dao.PageDao;
import wp.model.RatedPage;

/**
 * @spring.bean id="pageService"
 * @spring.property name="pageDao" ref="pageDao"
 * @author Jeff
 *
 */
public class PageServiceImpl implements PageService {

	private PageDao	pageDao;
	
	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public List<RatedPage> findPublicRatedPages(ListInfo info)
			throws SQLException {
		return (pageDao.findPublicRatedPages(info));
	}

	public List<RatedPage> findRatedPages(ListInfo info) throws SQLException {
		return (pageDao.findRatedPages(info));
	}
}
