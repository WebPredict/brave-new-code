package wp.core;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import wp.model.RatedPage;

public class RatedPagesComparator implements Comparator {

	private String	sort;
	private boolean	asc;
	
	public RatedPagesComparator (String sort, boolean asc) {
		this.sort = sort;
		this.asc = asc;
	}
	
	public int compare(Object arg0, Object arg1) {
		RatedPage	r0 = (RatedPage)arg0;
		RatedPage	r1 = (RatedPage)arg1;
		
		if (StringUtils.isEmpty(sort) || (sort.equals("rating"))) {
			String	rr0 = r0.getRating();
			String	rr1 = r1.getRating();
			
			return (asc ? rr0.compareTo(rr1) : rr1.compareTo(rr0));
		}
		else if (sort.equals("comment")) {
			String	u0 = r0.getComment();
			String	u1 = r1.getComment();
			
			if (u0 == null || u1 == null)
				return (0);
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("ratio")) {
			Double	u0 = r0.getParsedPage().getLinkToContentRatio();
			Double	u1 = r1.getParsedPage().getLinkToContentRatio();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("images")) {
			Integer	u0 = r0.getParsedPage().getNumImages();
			Integer	u1 = r1.getParsedPage().getNumImages();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("content")) {
			Integer	u0 = r0.getParsedPage().getContentSize();
			Integer	u1 = r1.getParsedPage().getContentSize();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("words")) {
			Integer	u0 = r0.getStats().getNumWords();
			Integer	u1 = r1.getStats().getNumWords();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("distinct")) {
			Integer	u0 = r0.getStats().getNumDistinctWords();
			Integer	u1 = r1.getStats().getNumDistinctWords();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("ads")) {
			Boolean	u0 = r0.getParsedPage().isHasAds();
			Boolean	u1 = r1.getParsedPage().isHasAds();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("popups")) {
			Boolean	u0 = r0.getParsedPage().isHasPopups();
			Boolean	u1 = r1.getParsedPage().isHasPopups();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("stripped")) {
			Integer	u0 = r0.getParsedPage().getStrippedContentSize();
			Integer	u1 = r1.getParsedPage().getStrippedContentSize();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("links")) {
			Integer	u0 = r0.getParsedPage().getNumLinks();
			Integer	u1 = r1.getParsedPage().getNumLinks();
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("url")) {
			String	u0 = r0.getUrl().toString();
			String	u1 = r1.getUrl().toString();
			
			if (u0 == null || u1 == null)
				return (0);
			
			return (asc ? u0.compareTo(u1) : u1.compareTo(u0));
		}
		else if (sort.equals("date")) {
			Date	d0 = r0.getTimestamp();
			Date	d1 = r1.getTimestamp();
			
			if (d0 == null || d1 == null)
				return (0);
			
			if (d0.getTime() > d1.getTime())
				return (asc ? 1 : -1);
			else if (d0.getTime() < d1.getTime())
				return (asc ? -1 : 1);
			return 0;
		}
		
		return (0);
	}
	
}
