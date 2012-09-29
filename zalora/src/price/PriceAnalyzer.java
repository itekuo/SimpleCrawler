package price;

import java.util.Collection;

import org.jsoup.nodes.Document;

import page.HTMLPage;
import page.HTMLPageRepository;
import policy.ContentScanner;

/**
 * This {@link PriceAnalyzer} goes through the visited pages found in the page
 * repository and report prices that are outside of the given range.
 * 
 * @author ted.kuo
 */
public class PriceAnalyzer {

	/**
	 * Specifies the scanner for parsing the document pages for prices.
	 */
	private ContentScanner<Price> priceScanner;
	
	/**
	 * Price range
	 */
	private Double minPrice, maxPrice;
	
	/**
	 * Constructor
	 */
	public PriceAnalyzer(ContentScanner<Price> priceScanner, Double minPrice, Double maxPrice) {
		this.priceScanner = priceScanner;
		
		// By default, set the min and max to zero.
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	/**
	 * This {@link PriceAnalyzer} talks to the {@link HTMLPageRepository} for
	 * pages that are still to be scanned. It scans through the pages and prints
	 * out any prices it finds using the {@link PriceScanner}.
	 * 
	 * This thread doesn't finish until its told to, and will on the
	 * {@link HTMLPageRepository} for more pages, if there is currently no
	 * page to analyse.
	 */
	public void analyse(HTMLPage pageToAnalyse, Document content) {
		if (pageToAnalyse != null) {

			// Analyse
			Collection<Price> pricesFound = this.priceScanner.scanPage(pageToAnalyse, content);
			if (pricesFound.isEmpty()) {
				System.out.println("No Price Found Link: " + pageToAnalyse.getCanonicalPageURLString());
			}
			else {
				for (Price price : pricesFound) {
					Double priceAmount = price.getPriceAmount();
					if (priceAmount < minPrice || priceAmount > maxPrice) {
						System.out.println("Price Error: " + price + " Link: " + pageToAnalyse.getCanonicalPageURLString());
					}
					else {
						System.out.println("Price Found: " + price + " Link: " + pageToAnalyse.getCanonicalPageURLString());
					}
				}
			}
		}
	}
}

