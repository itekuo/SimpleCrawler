package price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import page.HTMLLink;
import policy.PageAnalyser;
import price.Price.Currency;

/**
 * This {@link PriceAnalyser} scans through pages for wrong prices that is outside the given range. 
 * 
 * @author ted.kuo
 */
public class PriceAnalyser implements PageAnalyser {

	/**
	 * Specifies the min and max price that indicates the "correct" range.
	 */
	private double minPrice, maxPrice;
	
	/**
	 * Constructor
	 * 
	 * @param minPrice minimum price range 
	 * @param maxPrice maximum price range
	 */
	public PriceAnalyser(double minPrice, double maxPrice) {
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	/**
	 * Goes through the page content and print out any price that is outside the range.
	 */
	@Override
	public void analyse(HTMLLink link, Document linkContent) {
		if (link != null & linkContent != null) {
			// Analyse
			Collection<Price> pricesFound = getAllPricesFromPage(link, linkContent);
			for (Price price : pricesFound) {
				Double priceAmount = price.getPriceAmount();
				if (priceAmount < this.minPrice || priceAmount > this.maxPrice) {
					System.out.println("Price Error: " + price + ", Link: " + link.getCanonicalPageURLString());
				}
			}
		}		
	}
	
	/**
	 * This scans through a given {@link HTMLLink} for prices. 
	 */
	public List<Price> getAllPricesFromPage(HTMLLink page, Document pageContent) {
		List<Price> pricesFound = new ArrayList<>();
		
		List<Price> productDetailPrices = getPricesFromProductDetailPage(page, pageContent);
		pricesFound.addAll(productDetailPrices);
		
		return pricesFound;
	}

	/**
	 * A specific mechanism to search for prices in a Zalora product detail page.
	 * 
	 * @param page to be scanned for prices.
	 * @param pageContent
	 * @return a list of prices on a product details page.
	 */
	private List<Price> getPricesFromProductDetailPage(HTMLLink page, Document pageContent) {
		List<Price> pricesFound = new ArrayList<>();
		
		if (pageContent == null) {
			return pricesFound;
		}
		
		/* 
		 * Only apply this to "leaf" pages which is identified by pages having html
		 * extension. At Zalora page, all product detail page ends with .html
		 * extension.
		 */
		if (page.getPageURL().getPath().endsWith(".html")) {
			Elements priceElements = pageContent.getElementsByClass("prd-price");
			for (Element element : priceElements) {
				Elements priceAmountElements = element.getElementsByAttributeValue("property", "gr:hasCurrencyValue");
				Elements currencyElements = element.getElementsByAttributeValue("property", "gr:hasCurrency");
				if (!priceAmountElements.isEmpty() && priceAmountElements.get(0).html() != null) {
					try {
						String priceAmountString = priceAmountElements.get(0).html();
						Double priceAmount = Double.parseDouble(priceAmountString.trim().replace(",", ""));

						// convert the currency
						Currency currency = null;
						if (!currencyElements.isEmpty()) {
							String currencyString = currencyElements.get(0).html();
							currency = Currency.convert(currencyString);
						}
						
						Price p = new Price(priceAmount, currency);
						pricesFound.add(p);
					}
					catch (NumberFormatException e) {
						System.err.println(e.getMessage());
					}
					
				}
			}
		}
		return pricesFound;
	}
}
