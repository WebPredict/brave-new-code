package wp.dao;

import java.util.Collection;

public class ListInfo {

	private int		maxResults = 1000;
	private int		maxPerPage = 50;
	private int		pageNum = 1;
	private String []	sortCols;
	private boolean []	ascendings;
	private String []	likes;
	private String []	notLikes;
	private String []	equals;
	private Object []	notEquals;
	private String []	likesCols;
	private String []	notLikesCols;
	private String []	equalsCols;
	private String []	notEqualsCols;
	private Collection []	ins;
	private String []	insCols;
	private Collection []	notIns;
	private String []	notInsCols;
	
	private boolean		likesOr;
	private boolean		notLikesOr;
	private boolean		equalsOr;
	private boolean		notEqualsOr;
	private boolean		insOr;
	private boolean		notInsOr;
	
	public boolean isInsOr() {
		return insOr;
	}
	public void setInsOr(boolean insOr) {
		this.insOr = insOr;
	}
	public boolean isNotInsOr() {
		return notInsOr;
	}
	public void setNotInsOr(boolean notInsOr) {
		this.notInsOr = notInsOr;
	}
	public String[] getInsCols() {
		return insCols;
	}
	public void setInsCols(String[] insCols) {
		this.insCols = insCols;
	}
	public String[] getNotInsCols() {
		return notInsCols;
	}
	public void setNotInsCols(String[] notInsCols) {
		this.notInsCols = notInsCols;
	}
	public boolean isLikesOr() {
		return likesOr;
	}
	public void setLikesOr(boolean likesOr) {
		this.likesOr = likesOr;
	}
	public boolean isNotLikesOr() {
		return notLikesOr;
	}
	public void setNotLikesOr(boolean notLikesOr) {
		this.notLikesOr = notLikesOr;
	}
	public boolean isEqualsOr() {
		return equalsOr;
	}
	public void setEqualsOr(boolean equalsOr) {
		this.equalsOr = equalsOr;
	}
	public boolean isNotEqualsOr() {
		return notEqualsOr;
	}
	public void setNotEqualsOr(boolean notEqualsOr) {
		this.notEqualsOr = notEqualsOr;
	}
	public Collection[] getIns() {
		return ins;
	}
	public void setIns(Collection[] ins) {
		this.ins = ins;
	}
	public Collection[] getNotIns() {
		return notIns;
	}
	public void setNotIns(Collection[] notIns) {
		this.notIns = notIns;
	}
	public String[] getLikesCols() {
		return likesCols;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public void setLikesCols(String[] likesCols) {
		this.likesCols = likesCols;
	}
	public String[] getNotLikesCols() {
		return notLikesCols;
	}
	public void setNotLikesCols(String[] notLikesCols) {
		this.notLikesCols = notLikesCols;
	}
	public String[] getEqualsCols() {
		return equalsCols;
	}
	public void setEqualsCols(String[] equalsCols) {
		this.equalsCols = equalsCols;
	}
	public String[] getNotEqualsCols() {
		return notEqualsCols;
	}
	public void setNotEqualsCols(String[] notEqualsCols) {
		this.notEqualsCols = notEqualsCols;
	}
	public String[] getSortCols() {
		return sortCols;
	}
	public void setSortCols(String[] sortCol) {
		this.sortCols = sortCol;
	}
	public boolean[] getAscendings() {
		return ascendings;
	}
	public void setAscendings(boolean[] ascending) {
		this.ascendings = ascending;
	}
	public String[] getLikes() {
		return likes;
	}
	public void setLikes(String[] likes) {
		this.likes = likes;
	}
	public String[] getNotLikes() {
		return notLikes;
	}
	public void setNotLikes(String[] notLikes) {
		this.notLikes = notLikes;
	}
	public String[] getEquals() {
		return equals;
	}
	public void setEquals(String[] equals) {
		this.equals = equals;
	}
	public Object[] getNotEquals() {
		return notEquals;
	}
	public void setNotEquals(Object[] notEquals) {
		this.notEquals = notEquals;
	}
	public int getMaxPerPage() {
		return maxPerPage;
	}
	public void setMaxPerPage(int maxPerPage) {
		this.maxPerPage = maxPerPage;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
}
