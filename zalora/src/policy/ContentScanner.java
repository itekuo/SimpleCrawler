package policy;
import java.util.Collection;

import page.HTMLPage;

/**
 * Interface for scannning through a HTMLPage for information
 * 
 * @author ted.kuo
 */
public interface ContentScanner<E> {

	/**
	 * Returns a list of element E as a result of a scan. Each implementation
	 * decides how/what its scanning the content for.
	 * 
	 * @param page to be scanned.
	 * @return a collection of items as a result of a scan.
	 */
	Collection<E> scanPage(HTMLPage page);
}
