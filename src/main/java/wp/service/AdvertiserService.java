package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.core.AlreadyExistsException;
import wp.model.Advertiser;

public interface AdvertiserService {

	Advertiser	findById (Long id) throws SQLException;
	
	Advertiser	findByUsername (String username) throws SQLException;
	
	Advertiser	create (String id, String name, String description, String emailAddr, String password) 
		throws AlreadyExistsException, SQLException;
	
	void	delete (Advertiser advertiser) throws SQLException;
	
	void	save (Advertiser advertiser) throws SQLException, AlreadyExistsException;
	
	List<Advertiser>	listAdvertisers (String filter) throws SQLException;
}
