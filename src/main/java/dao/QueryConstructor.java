package wp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class QueryConstructor extends HibernateDaoSupport {

	private String	makeClause (ArrayList<Object> filters, Object [] vals, String [] cols, 
			String clause, String operator, boolean useOr) {
		if (vals != null && vals.length > 0) {
			if (clause.equals(""))
				clause = " where (";
			else
				clause += " and (";
			for (int i = 0; i < vals.length; i++) {
				clause += cols [i];
				clause += " ";
				clause += operator;
				
				if (operator.equals("in"))
					clause += " (:inParam" + i + ") ";
				else if (operator.equals("not in"))
					clause += " (:notInParam" + i + ") ";
				else
					clause += " ? ";
				
				if (operator.equals("like") || operator.equals("not like"))
					filters.add("%" + vals [i] + "%");
				//else if (operator.equals("in") || operator.equals("not in"))
					//filters.add("(" + vals [i] + ")");
				else if (!operator.equals("in") && !operator.equals("not in"))
					filters.add(vals [i]);
				
				if (i < vals.length - 1)
					clause += useOr ? "or " : "and ";
			}
			clause += ") ";
		}
		return (clause);
	}

	protected int	makeCountQuery (ListInfo info, String className) {
		Long	count = (Long)makeQueryInt(info, className, true);
		return (count == null ? 0 : (int)count.longValue());
	}

	protected List	makeQuery (ListInfo info, String className) {
		return ((List)makeQueryInt(info, className, false));
	}
	
	private Object	makeQueryInt (ListInfo info, String className, boolean count) {
		String	query = count ? "select count(*) from " + className : "from " + className;
		
		String []	equalsVals = info.getEquals();
		Object []	notEqualsVals = info.getNotEquals();
		String []	likesVals = info.getLikes();
		String []	notLikesVals = info.getNotLikes();
		final Collection []	insVals = info.getIns();
		final Collection []	notInsVals = info.getNotIns();
		
		final ArrayList<Object>	filters = new ArrayList<Object>();
		final ArrayList<String>	paramList = new ArrayList<String>();
		String	whereClause = "";
		
		whereClause = makeClause (filters, equalsVals, info.getEqualsCols(), whereClause, "=", info.isEqualsOr());
		whereClause = makeClause (filters, notEqualsVals, info.getNotEqualsCols(), whereClause, "!=", info.isNotEqualsOr());
		whereClause = makeClause (filters, likesVals, info.getLikesCols(), whereClause, "like", info.isLikesOr());
		whereClause = makeClause (filters, notLikesVals, info.getNotLikesCols(), whereClause, "not like", info.isNotLikesOr());
		whereClause = makeClause (filters, insVals, info.getInsCols(), whereClause, "in", info.isInsOr());
		whereClause = makeClause (filters, notInsVals, info.getNotInsCols(), whereClause, "not in", info.isNotInsOr());
		
		String []	sortCols = info.getSortCols();
		boolean []	ascs = info.getAscendings();		
		String orderBy = "";
		if (sortCols != null && sortCols.length > 0) {
			orderBy = " order by ";
			for (int i = 0; i < sortCols.length; i++) {
				orderBy += sortCols [i];
				orderBy += ascs [i] ? " asc" : " desc";
				if (i < sortCols.length -1)
					orderBy += ",";
			}
		}
		if (orderBy.equals(""))
			orderBy = " order by id asc";
				
		final int	maxResults = info.getMaxResults();
		final int	pageNum = info.getPageNum();
		final int	pageSize = info.getMaxPerPage();
		final String	queryStr = filters.size() > 0  || (insVals != null && insVals.length > 0) ? 
				query + whereClause + orderBy : query + orderBy;
		
		HibernateTemplate template = getHibernateTemplate();
		if (count) {	
			return template.execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Query query = session.createQuery(queryStr); 
	                for (int i = 0; i < filters.size(); i++) {
	                	query.setParameter(i, filters.get(i));

	                }
	                
	                if (insVals != null)
	                	for (int i = 0; i < insVals.length; i++) {
	                		query.setParameterList("inParam" + i, insVals [i]);
	                	}
	                
	                if (notInsVals != null)
	                	for (int i = 0; i < notInsVals.length; i++) {
	                		query.setParameterList("notInParam" + i, notInsVals [i]);
	                	}
	                
	                return ((Long)query.iterate().next());
	            }
	        });
		}
		else {
	        return template.executeFind(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Query query = session.createQuery(queryStr); 
	                for (int i = 0; i < filters.size(); i++)
	                	query.setParameter(i, filters.get(i));
	                
	                if (insVals != null)
	                	for (int i = 0; i < insVals.length; i++) {
	                		query.setParameterList("inParam" + i, insVals [i]);
	                	}
	                if (notInsVals != null)
	                	for (int i = 0; i < notInsVals.length; i++) {
	                		query.setParameterList("notInParam" + i, notInsVals [i]);
	                	}
	                
	                query.setMaxResults(pageSize);
	                query.setFirstResult(pageSize * (pageNum - 1));
	                return query.list();
	            }
	        });
		}
	}
}
