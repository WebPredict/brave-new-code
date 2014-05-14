package wp.process;

import java.sql.SQLException;

import org.hibernate.Session;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.scheduling.quartz.QuartzJobBean;

import wp.service.AlertService;

public class AlertsJob extends QuartzJobBean {

	private AlertService	alertService;
	
	public AlertService getAlertService() {
		return alertService;
	}


	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}

	protected void	executeInternal (JobExecutionContext context) {
		
		try {			
			ApplicationContext appContext = (ApplicationContext) context.
				getScheduler().getContext().get("applicationContext");

			HibernateTransactionManager	manager = (HibernateTransactionManager)appContext.getBean("transactionManager");
			Session session = manager.getSessionFactory().openSession();
			
			alertService = (AlertService)appContext.getBean("alertService");			
			alertService.runAlertsForAll();
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
