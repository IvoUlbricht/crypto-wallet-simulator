package com.hotovo.cws.service;

import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestTemplate;

import static com.hotovo.cws.service.TestDataService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CurrencyServiceTest {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CurrencyService sut;

	@Test
	@DisplayName("given existing wallets with enough balance of currency when transfer request then transfer successfull")
	void currency_transfer_success() {
		Wallet sourceWallet = createTestWallet();
		Wallet destinationWallet = createTestWallet();

		boolean txResult = sut.transferCurrency(sourceWallet, "BTC", 0.5, destinationWallet, "ETH");
		assertThat(txResult).isTrue();

		Currency sourceCurrency = sourceWallet.getCurrencies().stream()
				.filter(curr -> curr.getSymbol().equalsIgnoreCase("BTC")).findAny().orElse(null);
		assertThat(sourceCurrency).isNotNull();
		assertThat(sourceCurrency.getSymbol()).isEqualTo("BTC");
		assertThat(sourceCurrency.getAmount()).isEqualTo(0.5);

		Currency destCurrency = destinationWallet.getCurrencies().stream()
				.filter(curr -> curr.getSymbol().equalsIgnoreCase("ETH")).findAny().orElse(null);
		assertThat(destCurrency).isNotNull();
		assertThat(destCurrency.getSymbol()).isEqualTo("ETH");
		assertThat(destCurrency.getAmount()).isGreaterThan(0.0);
	}

	@Test
	@DisplayName("given existing wallets with low balance of currency when transfer request then throw runtime exception")
	void currency_transfer_failed_low_balance() {
		Wallet sourceWallet = createTestWallet();
		sourceWallet.getCurrencies().add(new Currency(25.0, "ETH"));
		Wallet destinationWallet = createTestWallet();

		assertThatThrownBy(() -> sut.transferCurrency(sourceWallet, "ETH", 26.0, destinationWallet, "ETH"))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Source wallet has no such currency or balance is lower then requested amount!");

		Currency sourceCurrency = sourceWallet.getCurrencies().stream()
				.filter(curr -> curr.getSymbol().equalsIgnoreCase("ETH")).findAny().orElse(null);
		assertThat(sourceCurrency).isNotNull();
		assertThat(sourceCurrency.getSymbol()).isEqualTo("ETH");
		assertThat(sourceCurrency.getAmount()).isEqualTo(25.0);

		Currency destCurrency = destinationWallet.getCurrencies().stream()
				.filter(curr -> curr.getSymbol().equalsIgnoreCase("ETH")).findAny().orElse(null);
		assertThat(destCurrency).isNull();
	}

	@Test
	@DisplayName("given existing wallet when request buy currency then success")
	void currency_buy_success() {
		Wallet wallet = createTestWallet();

		wallet = sut.buyCurrency("BTC", 0.38, "BTC", wallet);

		Currency currency = wallet.getCurrencies().stream().filter(curr -> curr.getSymbol().equalsIgnoreCase("BTC")).findAny().orElse(null);
		assertThat(currency).isNotNull();
		assertThat(currency.getSymbol()).isEqualTo("BTC");
		assertThat(currency.getAmount()).isEqualTo(1.38);
	}

	@Test
	@DisplayName("given existing wallet when buy currency with wrong symbol then failed")
	void currency_buy_failed() {
		Wallet wallet = createTestWallet();

		assertThatThrownBy(() -> sut.buyCurrency("ASDF", 0.38, "ASDF", wallet))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Price for currency not found or not valid!");

		Currency currency = wallet.getCurrencies().stream().filter(curr -> curr.getSymbol().equalsIgnoreCase("currency")).findAny().orElse(null);
		assertThat(currency).isNull();
	}
}