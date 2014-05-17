package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.model.Alert;
import wp.model.AlertResult;
import wp.model.User;

public interface AlertService {

	void	runAlertsForAll () throws SQLException;
 
	List<AlertResult>	runAlertsFor (User user) throws SQLException;

	void	deleteResults (User user, List<AlertResult> results) throws SQLException;
	
	void	deleteAlerts (User user, List<Alert> alerts) throws SQLException;
}
