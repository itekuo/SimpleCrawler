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

import page.HTMLLink;
import page.HTMLLinkRepository;
import page.URLParams;
import utils.Timer;

/**
 * This scanner scans for HTML links.
 * 
 * @author ted.kuo
 */
public class LinkScanner implements HTMLLinkScanner {

	/**
	 * Specifies the host, under which all URLs found will be scanned and reported.
	 */
	private String host;
	
	/**
	 * Constructor
	 * 
	 * 
	 * @param host, only links under the given host will be scanned and reported.
	 */
	public LinkScanner(String host, HTMLLinkRepository queue) {
		if (host == null) {
			throw new IllegalArgumentException("host cannot be null");
		}
		this.host = host;
	}
	
	/**
   * Scans through the page and return all the anchor reference in a list.
   * 
   * return a collection of URLs
   */
	@Override
	public List<HTMLLink> scanPage(HTMLLink page, Document doc) {
		List<HTMLLink> linksFound = new ArrayList<>();
		// If the document is null, then return empty list.
		if (doc == null) {
			return linksFound;
		}
		
		// Look for a href element values.
		Elements anchorTags = doc.getElementsByTag("a");
		for (Element element : anchorTags) {
			String hrefValue = element.attr("href");
			try {
				URL link = new URL(page.getPageURL(), hrefValue);
				
				// Only add the link if the host is correct.
				if (this.host.equals(link.getHost()) && !isZaloraAccountSpecificLink(link)) {
					linksFound.add(new HTMLLink(link));
				}
			} catch (MalformedURLException e) {
				// If a malformed link is found, log and continue.
				System.err.println("Malformed link found: " + hrefValue);
			}
		}
		return linksFound;
	}
	
	/**
	 * Return true if this is an account specific link. It should be ignored.
	 * 
	 * @param url to be checked.
	 * @return true if the given URL is recognised as a link to unhelpful pages.
	 */
	private boolean isZaloraAccountSpecificLink(URL url) {
		return (url.getPath().startsWith("/sendfriend") || url.getPath().startsWith("/customer/wishlist/add/p/"));
	}
}
