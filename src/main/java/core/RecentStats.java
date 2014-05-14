package wp.core;

import java.io.Serializable;
import java.util.List;

public class RecentStats implements Serializable {

	private int totalPagesRated;
	private int	totalPagesPredicted;
	
	public int getTotalPagesPredicted() {
		return totalPagesPredicted;
	}
	public void setTotalPagesPredicted(int totalPagesPredicted) {
		this.totalPagesPredicted = totalPagesPredicted;
	}
	private List<RatingProjection>	ratings;
	
	public int getTotalPagesRated() {
		return totalPagesRated;
	}
	public void setTotalPagesRated(int totalPagesRated) {
		this.totalPagesRated = totalPagesRated;
	}
	public List<RatingProjection> getRatings() {
		return ratings;
	}
	
	public void setRatings(List<RatingProjection> ratings) {
		this.ratings = ratings;
		
		// This is now done in DB:
		
		/*
		Collections.sort(ratings, new Comparator () {

			public int compare(Object arg0, Object arg1) {
				RatingProjection	r0 = (RatingProjection)arg0;
				RatingProjection	r1 = (RatingProjection)arg1;
				
				if (StringUtils.isEmpty(sortCol)) {
					Date	d0 = r0.getDate();
					Date	d1 = r1.getDate();
					
					if (d0 == null || d1 == null)
						return (0);
					
					if (d0.getTime() > d1.getTime())
						return (asc ? 1 : -1);
					else if (d0.getTime() < d1.getTime())
						return (asc ? -1 : 1);
					return 0;
				}
				else if (sortCol.equals("user")) {
					String	u0 = r0.getUserId();
					String	u1 = r1.getUserId();
					
					if (u0 == null || u1 == null)
						return (0);
					
					return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
				}
				else if (sortCol.equals("page")) {
					String	u0 = r0.getUrl();
					String	u1 = r1.getUrl();
					
					if (u0 == null || u1 == null)
						return (0);
					
					return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
				}
				else if (sortCol.equals("rating")) {
					String	rr0 = r0.getRating();
					String	rr1 = r1.getRating();
					
					return (asc ? rr0.compareTo(rr1) : rr1.compareTo(rr0));
				}
				return (0);
			}
			
		});
		*/
	}
	
}
