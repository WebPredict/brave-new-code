package wp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.tool.hbm2x.StringUtils;

import wp.model.Alert;

/**
 * @struts.form name="alertsForm"
 * @author Jeff
 *
 */
public class AlertsForm extends ActionForm {

	private ArrayList<AlertForm>	alertRules = new ArrayList<AlertForm>();
	private String	doit;
	private String	clearIt;

	public String getClearIt() {
		return clearIt;
	}

	public void setClearIt(String clearIt) {
		this.clearIt = clearIt;
	}

	public String getDoit() {
		return doit;
	}

	public void setDoit(String doit) {
		this.doit = doit;
	}
	
	public void	bind (List<Alert> alerts) {
		alertRules.clear();
		
		for (Alert alert : alerts) {
			AlertForm	af = new AlertForm();
			af.setAddon(alert.isAddon());
			af.setEmail(alert.isEmail());
			af.setCrawl(alert.isCrawl());
			af.setContains(alert.getContains());
			af.setUrl(alert.getUrlStr());
			af.setPredicts(alert.getPrediction());
			alertRules.add(af);
		}
		
		// add some blanks in case they want to add more
		alertRules.add(new AlertForm());
		alertRules.add(new AlertForm());
		alertRules.add(new AlertForm());
		
	}
	
	public int	getNumAlerts () {
		return (alertRules.size());
	}
	
	public List<AlertForm>	getAlertRulesList() {
		return (alertRules);
	}

	public AlertForm	getAlertRulesListElement (int idx) {
		while (alertRules.size() <= idx)
			alertRules.add(new AlertForm());
		return (alertRules.get(idx));
	}
	
	public void	validateAlerts (ActionMessages errors, ActionMapping mapping, HttpServletRequest request) {
		
	}

	public List<Long>	extractAlertsToDelete (List<Alert> existing) {

		ArrayList<Long>	toDelete = new ArrayList<Long>();
		for (int i = 0; i < existing.size(); i++) {
			if (alertRules.get(i).isRemove())
				toDelete.add(existing.get(i).getId());
		}
		
		return (toDelete);
	}
	
	public List<Alert>	extractAlerts (List<Alert> existing) {
		List<Alert>	newList = new ArrayList<Alert>();
		HashMap<String, Alert>	urlToAlertMap = new HashMap<String, Alert>();
		if (existing != null) {
			for (Alert a : existing) {
				urlToAlertMap.put(a.getUrlStr(), a);
			}
		}
		
		ArrayList<AlertForm>	newRules = new ArrayList<AlertForm>();
		for (AlertForm af : alertRules) {
			if (!af.isRemove())
				newRules.add(af);
		}
		alertRules = newRules;
		
		for (AlertForm af : alertRules) {
			
			String	url = af.getUrl();
			if (StringUtils.isEmpty(url))
				continue;
			
			Alert	existingAlert = urlToAlertMap.get(url);
			if (existingAlert == null)
				existingAlert = new Alert();
			
			existingAlert.setUrlStr(url);
			existingAlert.setAddon(af.isAddon());
			existingAlert.setEmail(af.isEmail());
			existingAlert.setContains(af.getContains());
			existingAlert.setPrediction(af.getPredicts());
			existingAlert.setCrawl(af.isCrawl());
			newList.add(existingAlert);
		}
		
		return (newList);
	}
}
