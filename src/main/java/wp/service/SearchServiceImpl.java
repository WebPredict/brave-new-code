package wp.service;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import wp.core.Constants;
import wp.core.Rater;
import wp.core.SearchResult;
import wp.core.Utils;
import wp.dao.SearchDao;
import wp.dao.SearchInfo;
import wp.model.Group;
import wp.model.ItemType;
import wp.model.ParsedPage;
import wp.model.RatedPage;
import wp.model.User;

/**
 * @spring.bean id="searchService"
 *
 * @spring.property name="searchDao" ref="searchDao"
 * @spring.property name="userService" ref="userService"
 * @spring.property name="pageService" ref="pageService"
 * @spring.property name="groupService" ref="groupService"
 * 
 * @author Jeff
 *
 */
public class SearchServiceImpl implements SearchService {

	private SearchDao	searchDao;
	private UserService	userService;
	private PageService	pageService;
	private GroupService groupService;
	
	public SearchDao getSearchDao() {
		return searchDao;
	}

	public void setSearchDao(SearchDao searchDao) {
		this.searchDao = searchDao;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PageService getPageService() {
		return pageService;
	}

	public void setPageService(PageService pageService) {
		this.pageService = pageService;
	}

	public static String safe (String s) {
		return (s == null ? "" : s);
	}
	
	public static String [] safeLower (String [] arr) {
		int	size = arr == null ? 0 : arr.length;
		String []	ret = new String [size];
		
		for (int i = 0; i < ret.length; i++)
			ret [i] = safe(arr [i]).toLowerCase();
		return (ret);
	}
	
	public static String [] safeLower (HashSet<String> arr) {
		int	size = arr == null ? 0 : arr.size();
		String []	ret = new String [size];
		int	counter = 0;
		for (String s : arr) {
			ret [counter++] = safe(s).toLowerCase();
		}
		return (ret);
	}
	
	public List<SearchResult> find(SearchInfo info) throws SQLException {
		// TODO index this stuff!!!!
		
		String		search = safe(info.getSearch());
		String		lower = search.toLowerCase();
		
		info.setLikes(new String [] {search, search, search});
		info.setLikesCols(new String [] {"urlStr", "parsedPage.title", "parsedPage.firstLine"});
		info.setLikesOr(true);
		
		Collection<String>	vulgarities = Rater.getTheRater().getVulgarities();

		//info.setNotIns(new Collection [] {vulgarities, vulgarities, vulgarities});
		//info.setNotInsCols(new String [] {"urlStr", "parsedPage.title", "parsedPage.firstLine"});
		//info.setNotInsOr(false);
		
		List<RatedPage>	pages = pageService.findRatedPages(info);
		
		ArrayList<SearchResult>	results = new ArrayList<SearchResult>();
		String	ratedBy = info.getRatedBy();
		String	ratedAs = info.getRatedAs();
		int		atLeast = info.getRatedAtLeastTimes();
		String	longerThan = info.getLongerThan();
		String	shorterThan = info.getShorterThan();
		boolean	hasPopups = info.isHasPopups();
		boolean	hasAds = info.isHasAds();
		int		longerThanInt = 0;
		try {
			longerThanInt = Integer.parseInt(longerThan);
		}
		catch (Exception e) { }
		
		int		shorterThanInt = Integer.MAX_VALUE;
		try {
			shorterThanInt = Integer.parseInt(shorterThan);
		}
		catch (Exception e) { }
		
		if (pages != null) {
			HashSet<String>	lookFor = new HashSet<String>();
			if (ratedAs != null) {
				StringTokenizer	tok = new StringTokenizer(ratedAs, ", ");
				while (tok.hasMoreTokens())
					lookFor.add(tok.nextToken().trim().toLowerCase());
			}

			Pattern	searchPattern = Pattern.compile(search + "\\b", Pattern.CASE_INSENSITIVE);
			HashMap<String, Integer>	urlToNumMap = new HashMap<String, Integer>();
			HashSet<String>		seenUrls = new HashSet<String>();
			
			for (RatedPage rp : pages) {
				String urlStr;
				try {
					urlStr = Utils.normalizeUrl(rp.getUrl()).toString();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				String	urlStrLower = urlStr.toLowerCase();
				ParsedPage	pp = rp.getParsedPage();
				String	firstLine = safe(pp.getFirstLine());
				String	title = safe(pp.getTitle()).toLowerCase();
				String	desc = safe(pp.getDescription()).toLowerCase();
				String []	keywords = safeLower(pp.getKeywordSet());
				String 	headlines = safe(pp.getHeadlines());
				String	firstLineLower = firstLine.toLowerCase();
				String	rating = safe(rp.getRating()).toLowerCase();
				
				int		strippedSize = pp.getStrippedContentSize();
				if (strippedSize < longerThanInt || strippedSize > shorterThanInt)
					continue;
				
				if (lookFor.size() > 0 && !lookFor.contains(rating))
					continue;
				
				if (StringUtils.isNotEmpty(ratedBy)) {
					String	userId = rp.getUser().getUserId();
					if (!userId.equals(ratedBy))
						continue;
				}
				
				if (!hasPopups && pp.isHasPopups())
					continue;
					
				if (!hasAds && pp.isHasAds())
					continue;
			
				if (atLeast > 1) {
					Integer	num = urlToNumMap.get(urlStr);
					if (num == null) {
						urlToNumMap.put(urlStr, 1);
						continue;
					}
					
					if (num < atLeast) {
						urlToNumMap.put(urlStr, ++num);
						continue;
					}
				}

				if (seenUrls.contains(urlStrLower))
					continue;
				//else
					//System.out.println ("URL IS: " + urlStrLower);
				
				if (urlStrLower.equals(lower)) {
					results.add(ratedPageResult(rp, SearchResult.GREAT_SCORE));
					seenUrls.add(urlStrLower);
				}
				else if (urlStrLower.indexOf(lower) != -1) {
					results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
					seenUrls.add(urlStrLower);
				}
				else if (rating.indexOf(lower) != -1) {
					results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
					seenUrls.add(urlStrLower);
				}
				else if (firstLineLower.indexOf(lower) != -1) {
					results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
					seenUrls.add(urlStrLower);
				}
				else if (title.indexOf(lower) != -1) {
					results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
					seenUrls.add(urlStrLower);
				}
				else if (desc.indexOf(lower) != -1) {
					results.add(ratedPageResult(rp, SearchResult.OK_SCORE));
					seenUrls.add(urlStrLower);
				}
				else {
					boolean	matchedKeyword = false;
					for (String keyword : keywords) {
						if (keyword.indexOf(lower) != -1) {
							results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
							seenUrls.add(urlStrLower);
							matchedKeyword = true;
							break;
						}
					}
					if (!matchedKeyword) {
						if (headlines.indexOf(lower) != -1) {
							results.add(ratedPageResult(rp, SearchResult.GOOD_SCORE));
							seenUrls.add(urlStrLower);
							continue;
						}
					}
					
					String	rawContent = pp.getRawContent();
				
					if (StringUtils.isNotEmpty(rawContent)) {
						Matcher	matcher = searchPattern.matcher(rawContent);
						if (StringUtils.isEmpty(search)) { 
							results.add(ratedPageResult(rp, SearchResult.OK_SCORE));
							seenUrls.add(urlStrLower);
						}
						else {
							int		groupCount = matcher.groupCount();
							if (groupCount > 0) {
								int		score = SearchResult.GOOD_SCORE + groupCount;
								results.add(ratedPageResult(rp, score));
								seenUrls.add(urlStrLower);
							}
						}
					}
				}
			}
		}
		
		if (StringUtils.isEmpty(ratedBy) && StringUtils.isEmpty(shorterThan) && StringUtils.isEmpty(longerThan)) {
			// they're not looking for anything besides pages
			info.setLikes(new String [] {search, search});
			info.setLikesCols(new String [] {"userId", "description"});
			List<User>	users = userService.listUsers(search);
				
			if (users != null && StringUtils.isNotEmpty(lower)) {
				for (User user : users) { 
					String	userId = user.getUserId();
					String	userIdLower = userId.toLowerCase();
					String	name = user.getName() == null ? "" : user.getName();
					String	nameLower = name.toLowerCase();
					if (userIdLower.equals(lower)) {
						results.add(new SearchResult(SearchResult.GREAT_SCORE, getUserInfo(user), ItemType.user, userId));
					}
					else if (userIdLower.indexOf(lower) != -1) {
						results.add(new SearchResult(SearchResult.GOOD_SCORE, getUserInfo(user), ItemType.user, userId));
					}
					else if (StringUtils.isNotEmpty(nameLower) && nameLower.equals(lower)) {
						results.add(new SearchResult(SearchResult.GREAT_SCORE, getUserInfo(user), ItemType.user, userId));
					}
					else if (StringUtils.isNotEmpty(nameLower) && nameLower.indexOf(lower) != -1) {
						results.add(new SearchResult(SearchResult.GOOD_SCORE, getUserInfo(user), ItemType.user, userId));
					}
				}
			}
			
			info.setLikes(new String [] {search, search, search});
			info.setLikesCols(new String [] {"groupId", "name", "description"});
			info.setSortCols(null);
			
			List<Group>	groups = groupService.listGroups(info);
			
			if (groups != null && StringUtils.isNotEmpty(lower)) {
				for (Group group : groups) { 
					String	groupId = group.getGroupId();
					String	id = group.getId().toString();
					String	groupIdLower = groupId.toLowerCase();
					String	name = group.getName() == null ? "" : group.getName();
					String	nameLower = name.toLowerCase();
					if (groupIdLower.equals(lower)) {
						results.add(new SearchResult(SearchResult.GREAT_SCORE, groupId, ItemType.group, id));
					}
					else if (groupIdLower.indexOf(lower) != -1) {
						results.add(new SearchResult(SearchResult.GOOD_SCORE, groupId, ItemType.group, id));
					}
					else if (nameLower.equals(lower)) {
						results.add(new SearchResult(SearchResult.GREAT_SCORE, groupId, ItemType.group, id));
					}
					else if (nameLower.indexOf(lower) != -1) {
						results.add(new SearchResult(SearchResult.GOOD_SCORE, groupId, ItemType.group, id));
					}
				}
			}
		}
		Collections.sort(results, new Comparator () {
			public int compare(Object arg0, Object arg1) {
				SearchResult	s0 = (SearchResult)arg0;
				SearchResult	s1 = (SearchResult)arg1;
				
				return (s1.getScore() - s0.getScore());
			}
			
		});
		
		return results;
	}

	private String		getUserInfo (User user) {
		StringBuffer	buf = new StringBuffer(user.getUserId());
		String	name = user.getName();
		if (StringUtils.isNotEmpty(name))
			buf.append (" (" + name + ") ");
		String	desc = user.getDescription();
		if (StringUtils.isNotEmpty(desc))
			buf.append(" -- " + desc);
		return (buf.toString());
	}
	
	private SearchResult	ratedPageResult (RatedPage rp, int score) {
		String			url = rp.getUrl().toString();
		SearchResult	result = new SearchResult(score, safe(rp.getParsedPage().getTitle()), ItemType.ratedPage, url);
		result.setUrl(safe(url));
		String	snapshotDate = Constants.PRECISE_DATE_FORMAT.format(rp.getTimestamp());
		result.setSnippet(rp.getParsedPage().getFirstLine() + " (Recent Rating: " + rp.getRating() + " -- Page Snapshot Taken On: " + 
				snapshotDate + ")");
		return (result);
	}
}
