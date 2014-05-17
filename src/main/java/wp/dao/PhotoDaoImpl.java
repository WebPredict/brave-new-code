package wp.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import wp.model.Photo;

/**
 * @spring.bean id="photoDao"
 * @spring.property name="sessionFactory" ref="sessionFactory"
 * @author Jeff
 *
 */
public class PhotoDaoImpl extends HibernateDaoSupport implements PhotoDao {

	@Transactional(readOnly = false)
	public Photo create(String name, byte [] data, String tag, String ownerId, int width,
			int height) throws SQLException {
		Photo	photo = new Photo();
		photo.setFilename(name);
		photo.setHeight(height);
		photo.setWidth(width);
		photo.setOwnerId(ownerId);
		photo.setData(data);
		photo.setTag(tag);
		save(photo);
		return (photo);
	}

	@Transactional(readOnly = false)
	public void delete(Photo photo) throws SQLException {
		getHibernateTemplate().delete(photo);
	}

	public Photo findById(Long photoId) throws SQLException {
		return ((Photo)getHibernateTemplate().get(Photo.class, photoId));
	}

	public List<Photo> listPhotos(String filter) throws SQLException {
		if (StringUtils.isEmpty(filter))
			return (getHibernateTemplate().find("from Photo"));
		String	filterStr = "%" + filter + "%";
		
		return (getHibernateTemplate().find("from Photo where name like ?", filterStr));
	}

	@Transactional(readOnly = false)
	public void save(Photo photo) throws SQLException {
		getHibernateTemplate().save(photo);
	}

}
