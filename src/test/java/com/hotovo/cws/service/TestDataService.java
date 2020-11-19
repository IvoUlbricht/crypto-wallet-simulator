package com.hotovo.cws.service;

import com.hotovo.cws.controller.dto.WalletRequest;
import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class TestDataService {

	public static Wallet createWallet() {
		Set<Currency> currencies = new LinkedHashSet<>();
		currencies.add(new Currency(1.0, "BTC"));
		return Wallet.builder()
				.id(Wallet.getWalletId().getAndIncrement())
				.name("name " + Wallet.getWalletId())
				.privateKey(UUID.randomUUID().toString())
				.publicKey(UUID.randomUUID().toString())
				.currencies(currencies)
				.build();
	}

	public static WalletRequest createWalletRequest() {
		Set<Currency> currencies = new LinkedHashSet<>();
		currencies.add(new Currency(1.0, "BTC"));
		return WalletRequest.builder()
				.name("name " + Wallet.getWalletId())
				.privateKey(UUID.randomUUID().toString())
				.publicKey(UUID.randomUUID().toString())
				.currencies(currencies)
				.build();
	}

	public static WalletRequest updateWalletRequest(String name, String privateKey, String publicKey) {
		Set<Currency> currencies = new LinkedHashSet<>();
		currencies.add(new Currency(1.0, "BTC"));
		return WalletRequest.builder()
				.name(name)
				.privateKey(privateKey)
				.publicKey(publicKey)
				.currencies(currencies)
				.build();
	}
}
