package policy;
import java.util.Collection;

import org.jsoup.nodes.Document;

import page.HTMLLink;

/**
 * Interface for scanning through a HTMLPage for information
 * 
 * @author ted.kuo
 */
public interface ContentScanner<E> {

	/**
	 * Returns a list of element E as a result of a scan. Each implementation
	 * decides how/what its scanning the content for.
	 * 
	 * @param page content to be scanned.
	 * @return a collection of items as a result of a scan.
	 */
	Collection<E> scanPage(HTMLLink htmlPage, Document pageContent) ;
}
