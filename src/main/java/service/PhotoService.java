package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.model.Photo;

public interface PhotoService {

	Photo	findById (Long photoId) throws SQLException;
	
	Photo	create (String name, byte [] data, String tag, String ownerId, int width, int height) 
		throws SQLException;
	
	void	delete (Photo photo) throws SQLException;
	
	void	save (Photo photo) throws SQLException;
	
	List<Photo>	listPhotos (String filter) throws SQLException;
}
