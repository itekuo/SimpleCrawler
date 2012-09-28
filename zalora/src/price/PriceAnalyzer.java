package price;

import page.HTMLPage;
import page.HTMLPageRepository;
import policy.ContentScanner;

/**
 * This {@link PriceAnalyzer} goes through the visited pages found in the page
 * repository and report prices that are outside of the given range.
 * 
 * @author ted.kuo
 */
public class PriceAnalyzer implements Runnable {

	/**
	 * {@link HTMLPage} repository
	 */
	private HTMLPageRepository pageRepository;
	
	/**
	 * Specifies the scanner for parsing the document pages for prices.
	 */
	private ContentScanner<Double> priceScanner;
	
	/**
	 * Price range
	 */
	private Double minPrice, maxPrice;
	
	/**
	 * Constructor
	 * 
	 * @param minPrice
	 * @param maxPrice
	 */
	public PriceAnalyzer(HTMLPageRepository htmlPageRepository, ContentScanner<Double> priceScanner, 
													double minPrice, double maxPrice) {

		this.pageRepository = htmlPageRepository;
		this.priceScanner = priceScanner;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	@Override
	public void run() {
//		while (true) {
//			if (this.pageRepository.get)
//		}
	}
}

