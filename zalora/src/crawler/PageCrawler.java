/**
 * 
 */
package crawler;

import java.util.Collection;
import java.util.Queue;

import page.HTMLPage;
import page.HTMLPageRepository;
import policy.ContentScanner;

/**
 * This defines a page crawler, which crawls through the given URL link. Links
 * found on the page are added to its {@link HTMLPageRepository}.
 * 
 * @author ted.kuo
 * 
 */
public class PageCrawler extends Thread {

	/**
	 * Specifies the queue this {@link PageCrawler} interacts with to insert all the links found in a page
	 */
	private HTMLPageRepository htmlPageQueue;
	
	/**
	 * Scanner for scanning the links on a page.
	 */
	private ContentScanner<HTMLPage> linkScanner;
	
	/**
	 * Specifies the page to crawl, null when there is nothing to crawl.
	 */
	private HTMLPage pageToCrawl;
	
	/**
	 * Specifies the queue which this crawler should return once its done.
	 */
	private Queue<PageCrawler> crawlersQueue;
	
	/**
	 * Constructor 
	 * 
	 * @param queue which this {@link PageCrawler} interacts to insert all the links found in a page.
	 * @param crawlersPool the home of these crawlers, it should add itself back to the pool once is done with crawling of a page.
	 */
	public PageCrawler(HTMLPageRepository queue, ContentScanner<HTMLPage> linkScanner, Queue<PageCrawler> crawlersPool) {
		super();
		this.htmlPageQueue = queue;
		this.linkScanner = linkScanner;
		this.crawlersQueue = crawlersPool;
	}

	/**
	 * Starts the crawling and set the busy signal to true.
	 */
	public void startCrawling(HTMLPage page) {
		synchronized (this) {
			this.pageToCrawl = page;
			this.notifyAll();
		}
	}

	/**
	 * Stops the crawling and make itself available in the crawler's queue.
	 */
	private void stopCrawling() {
		synchronized (this) {
			this.pageToCrawl = null;
			this.crawlersQueue.add(this);
		}
			
	}
	
	/**
	 * Run to crawl this{@link #getPageToCrawl()}, and stop when done.
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				if (this.pageToCrawl != null) {
					try {
						Collection<HTMLPage> linksFound = this.linkScanner.scanPage(this.pageToCrawl);
						this.htmlPageQueue.insert(linksFound);
					}
					catch (Exception e) {System.err.println("Error:" + e.getMessage() + " " + e.getStackTrace());}
					finally {
						stopCrawling();			
					}	
				}
				else {
					try {
						this.wait();
					} catch (InterruptedException e) {
						System.err.println("Error" + e.getMessage() + " " + e.getStackTrace());
						// Do nothing and continue.
					}
				}
		  }
		}
	}
}
