package price;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintStream;
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
import price.PriceAnalyser;

/**
 * Test-case for {@link PriceAnalyser}
 * 
 * @author ted.kuo
 */
public class TestPriceAnalyser {

	@Mock HTMLLink testPage;
	@Mock PrintStream mockOut;
	
	/**
	 * Object in test
	 */
	private PriceAnalyser priceAnalyser;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		priceAnalyser = new PriceAnalyser(20, 2000);
	}
	
	@Test
	public void testgetAllPricesFromPage_aProductDetailPage() throws Exception {
		// Given a page with a product price.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/C5172dd2.html"));
		Document pageContent = Jsoup.parse("<span class=\"rfloat prd-price\">" +
				"<span property=\"gr:hasCurrencyValue\">88.00</span> " +
				"<span property=\"gr:hasCurrency\">SGD</span>" +
				"</span>");
		
		// When
		List<Price> pricesFound = this.priceAnalyser.getAllPricesFromPage(testPage, pageContent);
		
		// Then
		assertEquals(1, pricesFound.size());
		assertEquals(88d, pricesFound.get(0).getPriceAmount(), 0.1d);
		assertEquals(Price.Currency.SGD, pricesFound.get(0).getCurrency());
	}
	
	@Test
	public void testgetAllPricesFromPage_notAProductDetailPage() throws Exception {
		// Given a page that is not a 'leaf' page.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/shoes"));
		Document pageContent = Jsoup.parse("<html>something</html>");

		// When
		List<Price> pricesFound = this.priceAnalyser.getAllPricesFromPage(testPage, pageContent);

		// Then
		assertEquals(0, pricesFound.size());
	}
	
	@Test
	public void testgetAllPricesFromPage_PriceMoreThanAThousand() throws Exception {
		// Given a page with a product price.
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/C5172dd2.html"));
		Document pageContent = Jsoup.parse("<span class=\"rfloat prd-price\">" +
				"<span property=\"gr:hasCurrencyValue\">2,000,900.00</span> " +
				"<span property=\"gr:hasCurrency\">SGD</span>" +
				"</span>");
		
		// When
		List<Price> pricesFound = this.priceAnalyser.getAllPricesFromPage(testPage, pageContent);
		
		// Then
		assertEquals(1, pricesFound.size());
		assertEquals(2000900d, pricesFound.get(0).getPriceAmount(), 0.1d);
		assertEquals(Price.Currency.SGD, pricesFound.get(0).getCurrency());
	}
	
	@Test
	public void testAnalyse_rangeCheck() throws Exception {
		// Given
		System.setOut(mockOut);
		stub(testPage.getPageURL()).toReturn(new URL("http://www.zalora.sg/shoe1.html"));
		stub(testPage.getCanonicalPageURLString()).toReturn("http://www.zalora.sg/shoe1.html");
		Document pageContent = Jsoup.parse("<span class=\"rfloat prd-price\">"
				+ "<span property=\"gr:hasCurrencyValue\">2,000,900.00</span> "
				+ "<span property=\"gr:hasCurrency\">SGD</span>" + "</span>"
				+ "<span class=\"rfloat prd-price\">"
				+ "<span property=\"gr:hasCurrencyValue\">10.00 </span> "
				+ "<span property=\"gr:hasCurrency\">SGD</span>" + "</span>"
				+ "<span class=\"rfloat prd-price\">"
				+ "<span property=\"gr:hasCurrencyValue\">20.00 </span> "
				+ "<span property=\"gr:hasCurrency\">SGD</span>" + "</span>");
		
		// When
		this.priceAnalyser.analyse(this.testPage, pageContent);
		
		// Then
		verify(mockOut).println("Price Error: $2000900.0 SGD Link: http://www.zalora.sg/shoe1.html");
		verify(mockOut).println("Price Error: $10.0 SGD Link: http://www.zalora.sg/shoe1.html");
	}
}
