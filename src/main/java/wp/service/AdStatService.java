package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.model.Ad;

public interface AdStatService {

	void	impress (List<Ad> ads) throws SQLException;
	
	void	click (Ad ad) throws SQLException;

}
