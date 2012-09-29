package price;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import price.Price.Currency;

/**
 * 
 * @author ted.kuo  
 *
 */
public class TestPrice {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCurrency_convert() {
		Currency currency = Price.Currency.convert("SGD");
		Currency currency3 = Price.Currency.convert("SGDD");
		Currency currency2 = Price.Currency.convert("");
		Currency currency4 = Price.Currency.convert(null);
		
		assertEquals(Currency.SGD, currency);
		assertEquals(null, currency2);
		assertEquals(null, currency3);
		assertEquals(null, currency4);
	}

}
