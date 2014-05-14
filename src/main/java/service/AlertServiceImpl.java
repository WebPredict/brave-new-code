package wp.service;

import java.sql.SQLException;
import java.util.List;

import wp.dao.AlertDao;
import wp.model.Alert;
import wp.model.AlertResult;
import wp.model.User;

/**
 * @spring.bean id="alertService"
 * @spring.property name="alertDao" ref="alertDao"
 * @author Jeff
 *
 */
public class AlertServiceImpl implements AlertService {

	private AlertDao	alertDao;
	
	public AlertDao getAlertDao() {
		return alertDao;
	}

	public void	runAlertsForAll () throws SQLException {
		this.alertDao.runAlertsForAll();
	} 
	
	public void setAlertDao(AlertDao alertDao) {
		this.alertDao = alertDao;
	}

	public List<AlertResult> runAlertsFor(User user) throws SQLException {
		return (alertDao.runAlertsFor(user));
	}

	public void	deleteResults (User user, List<AlertResult> results) throws SQLException {
		alertDao.deleteResults(user, results);
	}
	
	public void	deleteAlerts (User user, List<Alert> alerts) throws SQLException {
		alertDao.deleteAlerts(user, alerts);
	}
}
