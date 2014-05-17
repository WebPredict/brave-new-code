package wp.web;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.validator.ValidatorForm;

/**
 * @struts.form name="preSignupForm"
 * @author Jeff
 *
 */
public class PreSignupForm extends ValidatorForm {

	private String	newusername;
	private String  password;
	private String  emailAddr;
	private String	captcha;
	private String	subjectiveText = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10";
	private String	option2Text = "bad, ok, good, great";
	private String	option3Text = "stupid, smart, average, other";
	private String	option4Text = "baseball, football, tennis, basketball, other";
	private String	objectiveText = "science, business, entertainment, news, tech";	
	private String	choice;
	private String	jcaptcha_response;
	private String	userConfigId;
	private String	anonLoginId;
	private boolean	ratingsPublic;
	
	
	public String getAnonLoginId() {
		return anonLoginId;
	}
	public void setAnonLoginId(String anonLoginId) {
		this.anonLoginId = anonLoginId;
	}
	public boolean isRatingsPublic() {
		return ratingsPublic;
	}
	public void setRatingsPublic(boolean ratingsPublic) {
		this.ratingsPublic = ratingsPublic;
	}
	public String getUserConfigId() {
		return userConfigId;
	}
	public void setUserConfigId(String userConfigId) {
		this.userConfigId = userConfigId;
	}
	public String getJcaptcha_response() {
		return jcaptcha_response;
	}
	public void setJcaptcha_response(String jcaptcha_response) {
		this.jcaptcha_response = jcaptcha_response;
	}
	public String getChoice() {
		return choice;
	}
	public void setChoice(String choice) {
		this.choice = choice;
	}
	public String getOption2Text() {
		return option2Text;
	}
	public void setOption2Text(String option2Text) {
		this.option2Text = option2Text;
	}
	public String getOption3Text() {
		return option3Text;
	}
	public void setOption3Text(String option3Text) {
		this.option3Text = option3Text;
	}
	public String getOption4Text() {
		return option4Text;
	}
	public void setOption4Text(String option4Text) {
		this.option4Text = option4Text;
	}
	public String getNewusername() {
		return newusername;
	}
	public void setNewusername(String newusername) {
		this.newusername = newusername;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailAddr() {
		return emailAddr;
	}

	/**
	 * @struts.validator type="email"
	 * @struts.validator-args arg0resource="email"
	 */
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	
	public String getSubjectiveText() {
		return subjectiveText;
	}
	public void setSubjectiveText(String subjectiveText) {
		this.subjectiveText = subjectiveText;
	}
	public String getObjectiveText() {
		return objectiveText;
	}
	public void setObjectiveText(String objectiveText) {
		this.objectiveText = objectiveText;
	}
	public String	getRatingsCategories () {
		if (StringUtils.isEmpty(choice) || choice.equals("optionSubjective"))
			return (subjectiveText);
		else if (choice.equals("optionUserConfig"))
			return (null);
		else if (choice.equals("option3"))
			return (option3Text);
		else if (choice.equals("option4"))
			return (option4Text);
		else if (choice.equals("optionObjective"))
			return (objectiveText);
		return ("");
	}
}
