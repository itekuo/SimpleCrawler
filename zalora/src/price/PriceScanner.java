package price;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import page.HTMLLink;
import policy.ContentScanner;
import price.Price.Currency;

/**
 * This 
 * @author ted.kuo
 *
 */
public class PriceScanner implements ContentScanner<Price> {

	/**
	 * Constructor
	 */
	public PriceScanner() {
	}
	
	/**
	 * This scans through a given {@link HTMLLink} for prices. 
	 */
	public List<Price> scanPage(HTMLLink page, Document pageContent) {
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
