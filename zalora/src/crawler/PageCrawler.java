/**
 * 
 */
package crawler;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import org.jsoup.nodes.Document;

import page.HTMLLink;
import page.HTMLLinkRepository;
import policy.HTMLLinkScanner;
import policy.PageAnalyser;

/**
 * This defines a page crawler, which crawls through the given URL link. Links
 * found on the page are added to its {@link HTMLLinkRepository}.
 * 
 * @author ted.kuo
 * 
 */
public class PageCrawler extends Thread {

	/**
	 * Specifies the number of times this {@link PageCrawler} will retry when encountering connection issues.
	 */
	private static final int MAX_RETRY = 3;
	
	/**
	 * Specifies the queue this {@link PageCrawler} interacts with to insert all the links found in a page
	 */
	private HTMLLinkRepository htmlLinkRepository;
	
	/**
	 * Scanners for scanning the links on a page.
	 */
	private List<HTMLLinkScanner> linkScanners;
	
	/**
	 * Specifies the page to crawl, null when there is nothing to crawl.
	 */
	private HTMLLink linkToCrawl;
	
	/**
	 * Specifies the role that analyses a page for information.
	 */
	private List<PageAnalyser> pageAnalysers;
	
	/**
	 * Specifies the queue which this crawler should return to once its done.
	 */
	private Queue<PageCrawler> crawlersQueue;
	
	/**
	 * Constructor 
	 * 
	 * @param htmlLinkRespository which this {@link PageCrawler} interacts to insert all the links found in a page.
	 * @param linkScanners for scanning links in a page
	 * @param freeCrawlersPool the home of these crawlers, it should add itself back to the pool once is done with crawling of a page.
	 * @param pageAnalysers for analysing information in a page.
	 */
	public PageCrawler(HTMLLinkRepository htmlLinkRespository, List<HTMLLinkScanner> linkScanners, 
			Queue<PageCrawler> freeCrawlersPool, List<PageAnalyser> pageAnalysers) {
		this.htmlLinkRepository = htmlLinkRespository;
		this.linkScanners = new ArrayList<>(linkScanners);
		this.crawlersQueue = freeCrawlersPool;
		this.pageAnalysers = new ArrayList<>(pageAnalysers);
	}

	/**
	 * Return true if this PageCrawler still has a page to crawl.
	 * 
	 * @return true if this {@link PageCrawler} still has something to do.
	 */
	public synchronized boolean hasPageToCrawl() {
		return this.linkToCrawl != null;
	}
	
	/**
	 * Starts the crawling.
	 */
	public synchronized void startCrawling(HTMLLink link) {
		this.linkToCrawl = link;
		
		// To notify this waiting PageCrawler
		this.notifyAll();
	}

	/**
	 * Stops the crawling and make itself available in the free crawler's queue.
	 */
	private synchronized void makeMyselfAvailable() {
		// Add myself to the free crawlers queue.
		this.crawlersQueue.add(this);

		// Wait on itself until the WebCrawler gave it another page to crawl.
		try {
			this.wait();
		} catch (InterruptedException e) {
			// If interrupted, simply continue.
			System.out.println("Interrupted: " + e.getMessage());
		}
	}

	/**
	 * Returns the page content for the page this {@link PageCrawler} should be
	 * crawling. This will retry three times if there is connection issue. Returns
	 * null if the destination URL cannot be found, or if the page cannot be
	 * retrieved for any other reasons.
	 * 
	 * @param linkToCrawl page content of which is returned.
	 * @return the page content of the given {@link HTMLLink}
	 */
	private Document getPageContent(HTMLLink linkToCrawl) {
		boolean isPageRetrieved = false;
		Document pageContent = null;
		
		for (int numberOfAttempts = 0; numberOfAttempts < MAX_RETRY && !isPageRetrieved ; numberOfAttempts++) {
			try {
				pageContent = this.linkToCrawl.getContent();
				isPageRetrieved = true;
			} 
			catch (FileNotFoundException fnfe) {
				// If the link is broken, then just skip this page and return.
				isPageRetrieved = true;
			}
			catch (SocketException | SocketTimeoutException se) {
				isPageRetrieved = false;
			}
			catch (Exception e) {
				isPageRetrieved = true;
				System.err.println("Error:" + e.getMessage() + " for link: " + this.linkToCrawl.getCanonicalPageURLString());
			}
			finally {
				numberOfAttempts++;
			}
		}
		return pageContent;
	}
	
	/**
	 * Run to crawl the given page as set by
	 * {@link PageCrawler#startCrawling(HTMLLink)}. Once its done, it does nothing
	 * until another page is set for it to crawl.
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				if (this.linkToCrawl != null) {
					crawl();
				} 
				else {
					// If there is nothing to crawl, then make myself available.
					makeMyselfAvailable();
				}
		  }
		}
	}

	/**
	 * The method that contains the actual logic to crawl a link assigned to this {@link PageCrawler}.
	 */
	public void crawl() {
			Document pageContent = getPageContent(this.linkToCrawl);
			
			if (pageContent != null) {
				Collection<HTMLLink> linksFound = new ArrayList<>();
				for (HTMLLinkScanner linkScanner : this.linkScanners) {
					linksFound.addAll(linkScanner.scanPage(
							this.linkToCrawl, pageContent));
				}
				this.htmlLinkRepository.insert(linksFound);
				
				for (PageAnalyser pageAnalyser : this.pageAnalysers) {
					pageAnalyser.analyse(this.linkToCrawl, pageContent);
				}
			}
			
			// For whatever reason, mark the page as crawled.
			this.linkToCrawl = null;
	}
}
