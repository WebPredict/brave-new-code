package wp.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import wp.core.RatingCloudInfo;
import wp.core.RatingInfo;
import wp.core.RatingOutOfRangeException;
import wp.core.RecentStats;
import wp.core.UserCloudInfo;
import wp.core.UserDisabledException;
import wp.core.UserDoesntExistException;
import wp.core.UserQuotaException;
import wp.core.WordCloudInfo;
import wp.dao.ListInfo;
import wp.model.PageDiff;
import wp.model.Prediction;
import wp.model.RatedPage;
import wp.model.Recommendation;
import wp.model.UserStats;
import wp.model.WordFreqPair;
import wp.dao.UserStatsDao;
import wp.dao.PageDao;
import wp.dao.UserDao;
import wp.service.RatingService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Rating + prediction RESTful resource
 * @author Jeff
 *
 */
@Component
@Path("/wp/rating")
public class RatingResourceImpl implements RatingResource
{

	@Autowired
	private RatingService	ratingService;

	@Autowired
	private UserStatsDao	userStatsDao;
	
	@Autowired
	private PageDao			pageDao;

	public RatingService getRatingService() {
		return ratingService;
	}

	public void setRatingService(RatingService ratingService) {
		this.ratingService = ratingService;
	}

	public UserStatsDao getUserStatsDao() {
		return userStatsDao;
	}

	public void setUserStatsDao(UserStatsDao userStatsDao) {
		this.userStatsDao = userStatsDao;
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	@DELETE
	@Path("/{userId}")
	public void		delete (@PathParam("userId") Long userId, @QueryParam("pageUrl") String pageUrl) throws ResourceException {

		try {
		UserStats us = userStatsDao.findStatsFor(String.valueOf(userId));
		RatedPage rp = us.getRatedPagesMap().get(new URL(pageUrl));

		ratingService.delete(us, rp);
		}
		catch (Exception e) {
			throw new ResourceException(e.getMessage());
		}
	}

	@POST	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/rate/{userId}")
	public RatingInfo  rate (@PathParam("userId") Long userId, @QueryParam("url") String url, @QueryParam("rating") String rating, 
						@QueryParam("comment") String comment, @QueryParam("cookieData") String cookieData) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException {
				
		try {
			return (ratingService.rate(String.valueOf(userId), new URL(url), rating, comment, cookieData));
		} catch (MalformedURLException e) {
			throw new ResourceException(e.getMessage());
		} catch (SQLException e) {
			throw new ResourceException(e.getMessage());
		}
		
	}

	@POST	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/rateraw/{userId}")
	public RatingInfo	rateRawContent (@PathParam("userId") Long userId, String rawContent, String rawContentId, String rating, String comment) 
		throws UserQuotaException, UserDoesntExistException, RatingOutOfRangeException, UserDisabledException, ResourceException {
		return (null);
	}

	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/predict/{userId}")
	public Prediction  predict (@PathParam("userId") Long userId, @PathParam("url") String url, @PathParam("cookieData") String cookieData, 
		Collection<String> keywords, boolean summarize, int snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException {
		return (null);
	}

	@PUT	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/predictraw/{userId}")
	public Prediction  predictRawContent (@PathParam("userId") Long userId, @FormParam("rawContent") String rawContent, @QueryParam("summarize") Boolean summarize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException {
		return (null);
	}
	  
	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/predict/{userId}")
	public Prediction  [] predict (@QueryParam("url") String url, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, @QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException {
		return (null);
	}

	@POST
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/predictraw/{userId}")
	public Prediction  [] predictRawContent (@FormParam("rawContent") String rawContent, @QueryParam("maxUsers") Integer maxUsers, @QueryParam("minHistSize") Integer minHistSize, 
		@QueryParam("snippetSize") Integer snippetSize) 
		throws UserQuotaException, UserDoesntExistException, ResourceException {
		return (null);
	}
  
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userId}")
	public UserStats	findUserStats (@PathParam("userId") Long userId) throws ResourceException {
		return (null);
	}

	@PUT	
	@Path("/save/{userId}")
	public void	save (@PathParam("userId") Long userId, @FormParam("stats") String stats) throws ResourceException {
	}

	@GET	
	@Path("/recentstats/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
	public RecentStats	getRecentStats (@QueryParam("info") String info) throws ResourceException {
		return (null);
	}
	
	@GET	
	@Path("/countstats/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public int		countRecentStats (@QueryParam("info") String info) throws ResourceException {
		return (0);
	}
	
	@GET	
	@Path("/wordcloud/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	public Collection<WordCloudInfo>	getWordCloud(@QueryParam("size") Integer size, @QueryParam("ratedAs") String ratedAs, 
		@QueryParam("ignoreCommon") Boolean ignoreCommon, @QueryParam("dontUseCache") Boolean dontUseCache) throws ResourceException {
		return (null);
	}

	@GET	
	@Path("/ratingscloud/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	public Collection<RatingCloudInfo>	getRatingsCloud(@QueryParam("size") Integer size) throws ResourceException {
		return (null);
	}

	@GET	
	@Path("/mostactive")
    @Produces({ MediaType.APPLICATION_JSON })
	public Collection<UserCloudInfo>	getMostActiveMembers(@QueryParam("size") Integer size) throws ResourceException {
		return (null);
	}

	@GET	
    @Produces({ MediaType.APPLICATION_JSON })
	@Path("/recommendations/{userId}")
	public List<Recommendation>	getRecommendations (@PathParam("userId") String userId, @QueryParam("overrideList") String overrideList, 
								@QueryParam("dontShowList") String dontShowList, @QueryParam("searchSize") Integer searchSize, @QueryParam("resultSize") Integer resultSize) 
		throws ResourceException, UserQuotaException, UserDisabledException {
		return (null);
	}
	
	@GET	
	@Path("/wordstats")
    @Produces({ MediaType.APPLICATION_JSON })
	public List<WordFreqPair> getGlobalWordStats(@QueryParam("max") Integer max, @QueryParam("search") String search, @QueryParam("mostFrequent") Boolean mostFrequent) {
		return (null);
	}
	
	@GET	
	@Path("/pagediff/{userId}")
    @Produces({ MediaType.APPLICATION_JSON })
	public List<PageDiff>		pageDiffs (@PathParam("userId") Long userId, @QueryParam("reference") String reference, @QueryParam("diffsList") String diffsList) 
		throws ResourceException {
		return (null);
	}
	
}
