package com.hotovo.cws.service;

import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class TestDataService {

	public static final String PRIVATE_KEY = UUID.randomUUID().toString();
	public static final String PUBLIC_KEY = UUID.randomUUID().toString();
	public static final String NAME = "test wallet";

	public static Wallet createTestWallet() {
		Set<Currency> currencies = new LinkedHashSet<>();
		currencies.add(new Currency(1.0, "BTC"));
		Wallet wallet = Wallet.builder()
				.name(NAME)
				.privateKey(PRIVATE_KEY)
				.publicKey(PUBLIC_KEY)
				.currencies(currencies)
				.build();
		wallet.assignId();
		return wallet;
	}
}
