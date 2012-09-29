package policy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import page.HTMLPage;
import page.HTMLPageRepository;
import page.URLParams;
import utils.Timer;

/**
 * 
 */

/**
 * This scanner scans for HTML links.
 * 
 * @author ted.kuo
 *
 */
public class LinkScanner implements ContentScanner<HTMLPage> {

	/**
	 * Specifies the host, under which all URLs found will be scanned and reported.
	 */
	private String host;
	
	private Timer scanningTimer;
	
	private Timer pageFetchingTimer;
	
	/**
	 * Constructor
	 * 
	 * @param host, only links under the given host will be scanned and reported.
	 */
	public LinkScanner(String host, HTMLPageRepository queue) {
		if (host == null) {
			throw new IllegalArgumentException("host cannot be null");
		}
		this.host = host;
		this.scanningTimer = new Timer();
		this.pageFetchingTimer = new Timer();
	}
	
	/**
   * Scans through the page and return all the anchor reference in a list.
   * 
   * return a collection of URLs
   */
	@Override
	public List<HTMLPage> scanPage(HTMLPage page, Document doc) {
		List<HTMLPage> linksFound = new ArrayList<>();
		if (doc == null) {
			return linksFound;
		}
		
		// Retrieve the content of the page
		this.pageFetchingTimer.start();
		this.pageFetchingTimer.pause();
		
		// If the document cannot be fetched, then return empty list.
		
		this.scanningTimer.start();
		// Look for a href element values.
		Elements anchorTags = doc.getElementsByTag("a");
		for (Element element : anchorTags) {
			String hrefValue = element.attr("href");
			try {
				URL link = new URL(page.getPageURL(), hrefValue);
				
				// Only add the link if the host is correct.
				if (!isSimplyProductFilterQuery(link) && this.host.equals(link.getHost()) && !isZaloraAccountSpecificLink(link)) {
					linksFound.add(new HTMLPage(link));
				}
			} catch (MalformedURLException e) {
				// If a malformed link is found, log and continue.
				System.err.println("Malformed link found: " + hrefValue);
			}
		}
		this.scanningTimer.pause();
		return linksFound;
	}
	
	/**
	 * @return the scanningTimer
	 */
	public Timer getScanningTimer() {
		return scanningTimer;
	}

	/**
	 * @return the pageFetchingTimer
	 */
	public Timer getPageFetchingTimer() {
		return pageFetchingTimer;
	}

	/**
	 * This returns true if the link found to be "filter" link which won't help us to find more product pricing.
	 * 
	 * Any link that is found to have either <sort, dir, size> , <sort, dir, color>, <sort, dir, price>, <sort, dir, rating>
	 * 
	 * This can be later be turned into flexible policies, if special rule like
	 * this increases, or if we like to stay flexible. This is currently kept as
	 * is for simplicity and readability.
	 * 
	 * @param url to check.
	 * @return true if the URL contains query parameters that are only to filter the existing page.
	 */
	private boolean isSimplyProductFilterQuery(URL url) {
		URLParams param = new URLParams(url);
		if (param.getKeySet().size() == 0) {
			return false;
		}
		// A list of known product filtering keys and not necessary keys
		Set<String> filterKeys = new HashSet<>(Arrays.asList("occasion", "sort", "dir", "size", "color", "price", "rating", "gender", "page"));
		if (filterKeys.containsAll(param.getKeySet())) {
			return true;
		}
		
		return false;
	}
	
	private boolean isZaloraAccountSpecificLink(URL url) {
		return (url.getPath().startsWith("/sendfriend") || url.getPath().startsWith("/customer/wishlist/add/p/"));
	}
}
