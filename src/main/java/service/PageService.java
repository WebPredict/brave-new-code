package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.ListInfo;
import wp.model.RatedPage;

public interface PageService {

//	List<RatedPage>	findPagesContaining (String s) throws SQLException;
//	
//	List<RatedPage>	listRatedPages (int size, String userId, final String sortCol, boolean asc, 
//			boolean skipUglies, String urlStr) throws SQLException;
//	
//	List<RatedPage>	listRatedPages (int max) throws SQLException;
	
	List<RatedPage>	findRatedPages (ListInfo info) throws SQLException;
	
	List<RatedPage>	findPublicRatedPages (ListInfo info) throws SQLException;
}
