package wp.web;


/**
 * @struts.action path="/ListMessages" parameter="listMessages" validate="false"
 *   name="groupsForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listMessages" path="/ListMessages.jsp"
 * @spring.bean name="/ListMessages"
 * @struts.action-forward name="unauthorized" path="/Unauthorized.jsp"
 * 
 * @struts.action path="/DeleteMessage" parameter="deleteMessage" validate="false"
 *   name="usersForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="listMessages" path="/ListMessages.jsp"
 * @spring.bean name="/DeleteMessage"
 * 
 * @struts.action path="/EditMessage" parameter="editMessage" validate="false"
 *   name="userForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="editMessage" path="/MessageEdit.jsp"
 * @spring.bean name="/EditMessage"
 * 
 * @struts.action path="/ViewMessage" parameter="viewMessage" validate="false"
 *   name="userForm" scope="request" input="/Welcome.do"
 * @struts.action-forward name="viewMessage" path="/MessageView.jsp"
 * @spring.bean name="/ViewMessage"
 * 
 * @struts.action path="/SaveMessage" parameter="saveMessage" validate="false"
 *   name="userForm" scope="request" input="/EditMessage.do"
 * @struts.action-forward name="saveMessage" path="/MessageEdit.jsp"
 * @spring.bean name="/SaveMessage"
 * @author Jeff
 *
 */
public class MessageAction extends BaseAction {

}
