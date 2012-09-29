package page;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import utils.Timer;
import crawler.PageCrawler;
import crawler.WebCrawler;

/**
 * This specifies a repository of all the {@link HTMLPage}s that have been
 * discovered by the {@link PageCrawler}s. This {@link HTMLPageRepository} only
 * keeps a unique set of URLs. Duplicate URLs are ignored when they are inserted
 * to this repository.
 * 
 * This repository also maintain {@link HTMLPage}s that have not yet been
 * crawled. {@link HTMLPage}s that have not been discovered yet are kept in a
 * simple FIFO queue.
 * 
 * @author ted.kuo
 */
public class HTMLPageRepository {

	/**
	 * A history of the urls that have been inserted to this queue. URLs doesn't get
	 * removed even when an URL has been popped off the queue. However, provide
	 * faster performance at query operations. This is important for avoiding looping.
	 */
	private Set<HTMLPage> htmlPages;
	
	/**
	 * A queue of pages that have not yet been visited by the {@link PageCrawler}.
	 */
	private LinkedList<HTMLPage> unvisitedPageQueue;
	
	private Timer queueTimer;
	
	/**
	 * Constructor. Initialises an empty repository.
	 */
	public HTMLPageRepository() {
		this.htmlPages = new HashSet<HTMLPage>();
		this.unvisitedPageQueue = new LinkedList<>();
		this.queueTimer = new Timer();
	}
	
	/**
	 * Return the first item in the unvisited page queue.
	 * 
	 * @return the first item in the unvisited page queue, null if the queue is empty.
	 */
	public synchronized HTMLPage pollForUnvisitedPage() {
		return this.unvisitedPageQueue.poll();
	}

	/**
	 * Inserts the given URL into the repository, if and only if it is not already in
	 * the repository.
	 * 
	 * @param page to be inserted into this repository.
	 */
	private void privateInsert(HTMLPage page) {
		if (!this.htmlPages.contains(page)) {
			this.unvisitedPageQueue.add(page);
			this.htmlPages.add(page);
		}
	}
	
	/**
	 * Inserts the given page to this repository.
	 * 
	 * @param page to be inserted.
	 */
	public synchronized void insert(HTMLPage page) {
		privateInsert(page);
		notifyAll();
	}

	/**
	 * Inserts the given Collection of {@link HTMLPage}s into this repository.
	 * HTMLPage that already exists in the repository is ignored.
	 * 
	 * @param pages to be inserted into this repository
	 */
	public synchronized void insert(Collection<HTMLPage> pages) {
		this.queueTimer.start();
		for (HTMLPage page : pages) {
			privateInsert(page);
		}
		
		/* Notify all the threads that have been waiting for new information
		 * inserted into this repository.
		 */
		System.out.println("Queue: " + this.unvisitedPageQueue.size());
		this.queueTimer.pause();
	}
	
	/**
	 * @return the queueTimer
	 */
	public Timer getQueueTimer() {
		return queueTimer;
	}

	/**
	 * Returns true if all the pages inserted into this repository have been crawled/visited.
	 * 
	 * @return true if there is no more pages to visit.
	 */
	public synchronized boolean isAllPagesVisited() {
		return this.unvisitedPageQueue.size() == 0;
	}
	
	/**
	 * Returns the number of pages that have been discovered and inserted into this
	 * repository, regardless of whether its currently in the queue.
	 * 
	 * @return the number of pages discovered.
	 */
	public synchronized int getNumberOfPagesDiscovered() {
		return this.htmlPages.size();
	}
	
	/**
	 * Return the number of discovered pages that have not yet been visited.
	 * 
	 * @return the number of discovered pages that have not yet been visited.
	 */
	public synchronized int getNumberOfUnvisitedPages() {
		return this.unvisitedPageQueue.size();
	}
}
