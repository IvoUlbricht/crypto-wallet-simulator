package com.hotovo.cws.service;

import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class CurrencyService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${crypto-compare.base-url}")
	private String ccBaseUrl;

	@Value("${crypto-compare.api-key}")
	private String apiKey;

	/**
	 * Transfer of specified amount and currency between two wallets. If transaction is not successfull return both wallets/currencies to origin state.
	 *
	 * @param sourceWallet - wallet from where currency will be withdrawn
	 * @param sourceSymbol - symbol for the currency that will be withdrawn
	 * @param transferAmount - amount of the currency that will be withdrawn
	 * @param destWallet - destination wallet where currency will be deposited
	 * @param destSymbol - destination currency which will be deposited
	 * @return <tt>true</tt> or <tt>false</tt> based on the transaction success
	 * @throws RuntimeException if the specified currency and its balance is lower then requested amount
	 */
	public boolean transferCurrency(Wallet sourceWallet, String sourceSymbol, Double transferAmount, Wallet destWallet, String destSymbol) {
		Currency sourceCurrency = sourceWallet.getCurrencies().stream()
				.filter(currency -> currency.getSymbol().equalsIgnoreCase(sourceSymbol) && currency.getAmount() >= transferAmount)
				.findAny()
				.orElseThrow(() -> new RuntimeException("Source wallet has no such currency or balance is lower then requested amount!"));

		Currency destinationCurrency = findOrCreateNewCurrency(destWallet, destSymbol);

		Double price = fetchPrice(sourceSymbol, destSymbol);

		try {
			log.info("Money transfer from wallet {} to wallet {} started!", sourceWallet.getId(), destWallet.getId());
			sourceCurrency.setAmount(sourceCurrency.getAmount() - transferAmount);
			addOrReplaceCurrency(sourceWallet, sourceCurrency);

			destinationCurrency.setAmount(destinationCurrency.getAmount() + transferAmount * price);
			addOrReplaceCurrency(destWallet, destinationCurrency);
			log.info("Money transfer from wallet {} to wallet {} successfull!", sourceWallet.getId(), destWallet.getId());
			return true;
		} catch (Exception e) {
			log.error("Error performing transaction.... switching back to original state");
			addOrReplaceCurrency(sourceWallet, sourceCurrency);
			addOrReplaceCurrency(destWallet, destinationCurrency);
			return false;
		}
	}

	/**
	 * Buy specified amount of currency and add it to specified wallet with specified currency. Conversion between currencies is done based on actual data from
	 * CryptoCompare.com API (https://min-api.cryptocompare.com/documentation)
	 *
	 * @param sourceSymbol - symbol for currency user buys
	 * @param amount - amount of currency user buys
	 * @param destSymbol - destination currency symbol
	 * @param wallet - destination wallet
	 * @return wallet with currency changes
	 */
	public Wallet buyCurrency(String sourceSymbol, Double amount, String destSymbol, Wallet wallet) {
		Double price = fetchPrice(sourceSymbol, destSymbol);

		Currency destinationCurrency = findOrCreateNewCurrency(wallet, destSymbol);
		destinationCurrency.setAmount(destinationCurrency.getAmount() + amount * price);
		addOrReplaceCurrency(wallet, destinationCurrency);
		return wallet;
	}

	/**
	 * Fetch the currency conversion value for
	 *
	 * @param fsym cryptocurrency symbol of interest e.g. BTC
	 * @param tsym cryptocurrency symbol to convert into e.g. ETH
	 * @return price for the currency conversion
	 */
	private Double fetchPrice(String fsym, String tsym) {
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("fsym", fsym);
		uriParams.put("tsym", tsym);
		UriComponents uri = UriComponentsBuilder
				.fromHttpUrl(ccBaseUrl.concat("/data/price?fsym={fsym}&tsyms={tsym}"))
				.buildAndExpand(uriParams);

		log.info("External endpoint call with method {} to {} ", HttpMethod.GET, uri.toString());
		ResponseEntity<HashMap> responseEntity = restTemplate.exchange(uri.toUri(), HttpMethod.GET, createEntityWithAuthHeader(), HashMap.class);
		return Optional.ofNullable(responseEntity.getBody())
				.map(response -> {
					log.info("Received response from crypto compare {}", response.toString());
					return response.get(tsym);
				})
				.filter(o -> o instanceof Number)
				.map(o -> (Number) o)
				.map(Number::doubleValue)
				.orElseThrow(() -> new RuntimeException("Price for currency not found or not valid!"));
	}

	private Currency findOrCreateNewCurrency(Wallet destWallet, String destSymbol) {
		return destWallet.getCurrencies().stream()
				.filter(currency -> currency.getSymbol().equalsIgnoreCase(destSymbol))
				.findAny()
				.orElse(new Currency(0.0, destSymbol));
	}

	private void addOrReplaceCurrency(Wallet sourceWallet, Currency sourceCurrency) {
		sourceWallet.getCurrencies().remove(sourceCurrency);
		sourceWallet.getCurrencies().add(sourceCurrency);
	}

	private HttpEntity createEntityWithAuthHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "Apikey " + apiKey);
		return new HttpEntity<>(headers);
	}

}
