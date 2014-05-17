package wp.process;

import java.sql.SQLException;

import org.hibernate.Session;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.scheduling.quartz.QuartzJobBean;

import wp.service.CrawlerService;

public class CrawlerJob extends QuartzJobBean {

	private CrawlerService	crawlerService;
	

	public CrawlerService getCrawlerService() {
		return crawlerService;
	}


	public void setCrawlerService(CrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}


	protected void	executeInternal (JobExecutionContext context) {
		
		try {			
			ApplicationContext appContext = (ApplicationContext) context.
				getScheduler().getContext().get("applicationContext");

			HibernateTransactionManager	manager = (HibernateTransactionManager)appContext.getBean("transactionManager");
			Session session = manager.getSessionFactory().openSession();
			
			crawlerService = (CrawlerService)appContext.getBean("crawlerService");			
			crawlerService.runCrawlersForAll();
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
