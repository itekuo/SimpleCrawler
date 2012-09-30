package page;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import crawler.PageCrawler;

/**
 * This specifies a repository of all the {@link HTMLLink}s that have been
 * discovered by the {@link PageCrawler}s. This {@link HTMLLinkRepository} only
 * keeps a unique set of URLs. Duplicate URLs are ignored when they are inserted
 * to this repository.
 * 
 * This repository also maintain {@link HTMLLink}s that have been found but not yet
 * crawled. {@link HTMLLink}s that have not been discovered yet are kept in a
 * simple FIFO queue.
 * 
 * @author ted.kuo
 */
public class HTMLLinkRepository {

	/**
	 * A history of the urls that have been inserted to this queue. URLs doesn't get
	 * removed even when an URL has been popped off the queue. However, provide
	 * faster performance at query operations. This is important for avoiding looping.
	 */
	private Set<HTMLLink> htmlLinks;
	
	/**
	 * A queue of pages that have not yet been visited by the {@link PageCrawler}.
	 */
	private LinkedList<HTMLLink> unvisitedLinkQueue;
	
	/**
	 * Constructor. Initialises an empty repository.
	 */
	public HTMLLinkRepository() {
		this.htmlLinks = new HashSet<HTMLLink>();
		this.unvisitedLinkQueue = new LinkedList<>();
	}
	
	/**
	 * Return the first item in the unvisited page queue.
	 * 
	 * @return the first item in the unvisited page queue, null if the queue is empty.
	 */
	public synchronized HTMLLink pollUnvisitedPageQueue() {
		return this.unvisitedLinkQueue.poll();
	}

	/**
	 * Inserts the given page to this repository.
	 * 
	 * @param page to be inserted.
	 */
	public synchronized void insert(HTMLLink page) {
		if (!this.htmlLinks.contains(page)) {
			this.unvisitedLinkQueue.add(page);
			this.htmlLinks.add(page);
		}
	}

	/**
	 * Inserts the given Collection of {@link HTMLLink}s into this repository.
	 * HTMLPage that already exists in the repository is ignored.
	 * 
	 * @param pages to be inserted into this repository
	 */
	public synchronized void insert(Collection<HTMLLink> pages) {
		for (HTMLLink page : pages) {
			insert(page);
		}
	}
	
	/**
	 * Returns true if all the pages inserted into this repository have been crawled/visited.
	 * 
	 * @return true if there is no more pages to visit.
	 */
	public synchronized boolean isAllLinksVisited() {
		return this.unvisitedLinkQueue.size() == 0;
	}
	
	/**
	 * Returns the number of pages that have been discovered and inserted into this
	 * repository, regardless of whether its currently in the queue.
	 * 
	 * @return the number of pages discovered.
	 */
	public synchronized int getNumberOfLinksDiscovered() {
		return this.htmlLinks.size();
	}
	
	/**
	 * Return the number of discovered pages that have not yet been visited.
	 * 
	 * @return the number of discovered pages that have not yet been visited.
	 */
	public synchronized int getNumberOfUnvisitedLinks() {
		return this.unvisitedLinkQueue.size();
	}
}
