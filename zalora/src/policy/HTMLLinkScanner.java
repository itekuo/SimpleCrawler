package policy;
import java.util.Collection;

import org.jsoup.nodes.Document;

import page.HTMLLink;

/**
 * Interface for scanning through a {@link HTMLLink} for links
 * 
 * @author ted.kuo
 */
public interface HTMLLinkScanner {

	/**
	 * Returns a list of {@link HTMLLink}s as a result of a scan. Each implementation
	 * decides how it scans for the links.
	 * 
	 * @param link content to be scanned.
	 * @return a collection of links as a result of a scan.
	 */
	Collection<HTMLLink> scanPage(HTMLLink htmlLink, Document pageContent) ;
}
