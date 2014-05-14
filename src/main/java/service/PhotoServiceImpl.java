package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.PhotoDao;
import wp.model.Photo;

/**
 * @spring.bean id="photoService"
 * @spring.property name="photoDao" ref="photoDao"
 * @author Jeff
 *
 */
public class PhotoServiceImpl implements PhotoService {

	private PhotoDao	photoDao;
	
	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public Photo create(String name, byte [] data, String tag, String ownerId, int width,
			int height) throws SQLException {
		return (photoDao.create(name, data, tag, ownerId, width, height));
	}

	public void delete(Photo photo) throws SQLException {
		photoDao.delete(photo);
	}
 
	public Photo findById(Long photoId) throws SQLException {
		return (photoDao.findById(photoId));
	}

	public List<Photo> listPhotos(String filter) throws SQLException {
		return (photoDao.listPhotos(filter));
	}
	
	public void save(Photo photo) throws SQLException {
		photoDao.save(photo);
	}

}
