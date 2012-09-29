/**
 * 
 */
package crawler;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Queue;

import org.jsoup.nodes.Document;

import page.HTMLLink;
import page.HTMLLinkRepository;
import policy.ContentScanner;
import price.PriceAnalyzer;

/**
 * This defines a page crawler, which crawls through the given URL link. Links
 * found on the page are added to its {@link HTMLLinkRepository}.
 * 
 * @author ted.kuo
 * 
 */
public class PageCrawler extends Thread {

	/**
	 * Specifies the queue this {@link PageCrawler} interacts with to insert all the links found in a page
	 */
	private HTMLLinkRepository htmlPageQueue;
	
	/**
	 * Scanner for scanning the links on a page.
	 */
	private ContentScanner<HTMLLink> linkScanner;
	
	/**
	 * Specifies the page to crawl, null when there is nothing to crawl.
	 */
	private HTMLLink pageToCrawl;
	
	/**
	 * Specifies the role that analyses a page for prices.
	 */
	private PriceAnalyzer priceAnalyzer;
	
	/**
	 * Specifies the queue which this crawler should return once its done.
	 */
	private Queue<PageCrawler> crawlersQueue;
	
	/**
	 * Constructor 
	 * 
	 * @param queue which this {@link PageCrawler} interacts to insert all the links found in a page.
	 * @param linkScanner for scanning links in a page
	 * @param freeCrawlersPool the home of these crawlers, it should add itself back to the pool once is done with crawling of a page.
	 * @param priceAnalyzer for analysing prices in a page.
	 */
	public PageCrawler(HTMLLinkRepository queue, ContentScanner<HTMLLink> linkScanner, 
			Queue<PageCrawler> freeCrawlersPool, PriceAnalyzer priceAnalyzer) {
		this.htmlPageQueue = queue;
		this.linkScanner = linkScanner;
		this.crawlersQueue = freeCrawlersPool;
		this.priceAnalyzer = priceAnalyzer;
	}

	/**
	 * Return true if this PageCrawler is still crawling a page that was given to it.
	 * 
	 * @return true if this {@link PageCrawler} still has something to do.
	 */
	public boolean isCrawling() {
		synchronized (this) {
			return this.pageToCrawl != null;
		}
	}
	
	/**
	 * Starts the crawling and set the busy signal to true.
	 */
	public void startCrawling(HTMLLink page) {
		synchronized (this) {
			this.pageToCrawl = page;
			this.notifyAll();
		}
	}

	/**
	 * Stops the crawling and make itself available in the free crawler's queue.
	 */
	private void makeMyselfAvailable() {
		synchronized (this) {
			// Add myself to the free crawlers queue.
			this.crawlersQueue.add(this);
			
			// Wait on itself until the WebCrawler gave it another page to crawl.
			try {
				this.wait();
			} catch (InterruptedException e) {
				System.err.println("Interrupted: " + e.getMessage());
				// If interrupted, simply continue.
			}
		}
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
				if (this.pageToCrawl != null) {
					try {
						Document pageContent = this.pageToCrawl.getContent();
						Collection<HTMLLink> linksFound = this.linkScanner.scanPage(this.pageToCrawl, pageContent);
						this.htmlPageQueue.insert(linksFound);

						this.priceAnalyzer.analyse(this.pageToCrawl, pageContent);
					}
					catch (FileNotFoundException fnfe) {
						// If the link is broken, then just continue.
					}
					catch (Exception e) {
						/*
						 * If for any reason the link cannot be connected, then do nothing,
						 * Further extension can be made to log these error for further
						 * investigation
						 */
						System.err.println("Error:" + e.getMessage());
					}
					finally {
						// For whatever reason, mark the page as crawled.
						this.pageToCrawl = null;
					}
				}
				else {
					// If there is nothing to crawl, then make myself available.
					makeMyselfAvailable();
				}
		  }
		}
	}
}
