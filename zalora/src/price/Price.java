package price;

/**
 * A simple price class to represent a Price in its amount and its currency.
 * 
 * NOTE: No quantity ratio is used because its assumed all the prices are stated
 * as per item. Nothing like "$40.00 for two".
 * 
 * @author ted.kuo
 */
public class Price {
	/**
	 * Specifies the price.
	 */
	private double priceAmount;

	/**
	 * Currency of a price. 
	 */
	private Currency currency;

	/**
	 * Constructor for a {@link Price}.
	 * 
	 * @param priceAmount the price amount of an item
	 * @param currency which the price is in.
	 */
	public Price(double priceAmount, Currency currency) {
		this.priceAmount = priceAmount;
		this.currency = currency;
	}

	/**
	 * @return the priceAmount
	 */
	public double getPriceAmount() {
		return priceAmount;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * String representation of {@link Price}
	 * 
	 * @return toString
	 */
	public String toString() {
		return "$" + this.priceAmount + " " + this.currency.toString();
	}
	
	/**
	 * A simple enum to declare all possible currencies of a price.
	 * 
	 * @author ted.kuo
	 */
	public enum Currency {
		SGD;
		
		/**
		 * Return the {@link Currency} object that represents the given currency
		 * string. If no match can be found, or if the given string is null, then
		 * null is returned.
		 * 
		 * @param currencyString to be converted.
		 * @return the {@link Currency} representation of the given string.
		 */
		public static Currency convert(String currencyString) {
			if (currencyString == null || currencyString.isEmpty()) {
				return null;
			}
			
			Currency correspondingCurrency = null;
			for (Currency currency : Currency.values()) {
				if (currency.toString().equals(currencyString)) {
					return currency;
				}
			}
			return correspondingCurrency;
		}
	}
}
