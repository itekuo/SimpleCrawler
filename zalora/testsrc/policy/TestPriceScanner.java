package policy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.stub;

import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import page.HTMLLink;
import price.Price;
import price.PriceScanner;

/**
 * Test-case for {@link PriceScanner}
 * 
 * @author ted.kuo
 */
public class TestPriceScanner {

	@Mock HTMLLink testPage;
	
	private PriceScanner priceScanner;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		priceScanner = new PriceScanner();
	}
	
	@Test
	public void testScanPage_aProductDetailPage() throws Exception {
		// Given a page with a product price.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/C5172dd2.html"));
		Document pageContent = Jsoup.parse("<span class=\"rfloat prd-price\">" +
				"<span property=\"gr:hasCurrencyValue\">88.00</span> " +
				"<span property=\"gr:hasCurrency\">SGD</span>" +
				"</span>");
		
		// When
		List<Price> pricesFound = this.priceScanner.scanPage(testPage, pageContent);
		
		// Then
		assertEquals(1, pricesFound.size());
		assertEquals(88d, pricesFound.get(0).getPriceAmount(), 0.1d);
		assertEquals(Price.Currency.SGD, pricesFound.get(0).getCurrency());
	}
	
	@Test
	public void testScanPage_notAProductDetailPage() throws Exception {
		// Given a page that is not a 'leaf' page.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/shoes"));
		Document pageContent = Jsoup.parse("<html>something</html>");

		// When
		List<Price> pricesFound = this.priceScanner.scanPage(testPage, pageContent);

		// Then
		assertEquals(0, pricesFound.size());
	}
	
	@Test
	public void testScanPage_PriceMoreThanAThousand() throws Exception {
		// Given a page with a product price.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/C5172dd2.html"));
		Document pageContent = Jsoup.parse("<span class=\"rfloat prd-price\">" +
				"<span property=\"gr:hasCurrencyValue\">2,000,900.00</span> " +
				"<span property=\"gr:hasCurrency\">SGD</span>" +
				"</span>");
		
		// When
		List<Price> pricesFound = this.priceScanner.scanPage(testPage, pageContent);
		
		// Then
		assertEquals(1, pricesFound.size());
		assertEquals(2000900d, pricesFound.get(0).getPriceAmount(), 0.1d);
		assertEquals(Price.Currency.SGD, pricesFound.get(0).getCurrency());
	}
	
	@Test
	public void testScanPage_aDetailPageWithSpecialPrice() throws Exception {
		// Given a fragment of a page that is a product detail page with a special price
	}
}
