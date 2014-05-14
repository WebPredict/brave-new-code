package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import wp.model.ParsedPage;
import wp.model.RatedPage;

/**
 * @spring.bean id="pageDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class PageDaoImpl extends QueryConstructor implements PageDao {

	
	public List<RatedPage> findPagesContaining(String s) throws SQLException {
		if (StringUtils.isEmpty(s))
			return (getHibernateTemplate().find("from RatedPage"));

		String	filter = "%" + s + "%";
		return (getHibernateTemplate().find("from RatedPage where title like ?", filter));
	}

	public List<RatedPage> findRatedPages(ListInfo info) throws SQLException {
		return ((List<RatedPage>)makeQuery(info, "RatedPage"));
	}

	public int	countRatedPages(ListInfo info) throws SQLException {
		return (makeCountQuery(info, "RatedPage"));
	}

	public List<RatedPage> listRatedPages(int size) throws SQLException {
		return (getHibernateTemplate().find("from RatedPage"));
	}

	public List<RatedPage> findPublicRatedPages(ListInfo info)
			throws SQLException {
		String []	notLikes = new String [] {"file:%"};
		String []	notLikesCols = new String [] {"urlStr"};
		info.setNotLikes(notLikes);
		info.setNotLikesCols(notLikesCols);

		return (findRatedPages(info));
	}

	public ParsedPage findParsedPageBYURL(String url) throws SQLException {
		List<ParsedPage>	pps = getHibernateTemplate().find("from ParsedPage where urlStr = ?", url);
		return (pps == null || pps.size() == 0 ? null : pps.get(0));
	}

	public List<String> findPublicRatedPageURLs(String notBy)
			throws SQLException {
		
		List<String>	urls = getHibernateTemplate().find("select rp.urlStr from RatedPage rp " + 
				"where rp.user.userId != ? and rp.urlStr not like ?", new Object [] {notBy, "file:%"});
		return (urls);
	}
}
