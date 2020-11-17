package com.hotovo.cws.service;

import com.hotovo.cws.domain.Wallet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WalletService {

	/**
	 * Collection of existing wallets
	 */
	private Set<Wallet> wallets = new LinkedHashSet<>();

	/**
	 * Retrieve wallet based on the requested id
	 *
	 * @param id - identificator of the wallet to be returned
	 * @return wallet information
	 */
	public Optional<Wallet> getWalletInformation(Long id) {
		return wallets.stream()
				.filter(wallet -> wallet.getId().equals(id))
				.findAny();
	}

	/**
	 * Adds the wallet to the wallets collection
	 *
	 * @param wallet - parameters for the wallet to be created
	 * @return created wallet
	 */
	public Wallet createWallet(Wallet wallet) {
		//maybe validation for unique combination of name, PRIV and PUB keys
		wallet.assignId();
		wallets.add(wallet);
		log.info("Wallet {} was created successfully!", wallet);
		return wallet;
	}

	/**
	 * Updates the specified wallet in the wallets collection (possible wallet parameters to be updated are name, privateKey and publicKey
	 *
	 * @param walletUpdate - parameters of the wallet to be updated
	 * @return updated wallet
	 * @throws RuntimeException in case the wallet is found in the wallets collection
	 */
	public Optional<Wallet> updateWallet(Wallet walletUpdate) {
		return Optional.of(wallets.stream()
				.filter(w -> w.getId().equals(walletUpdate.getId()))
				.findAny()
				.map(wallet -> {
					wallet.setName(walletUpdate.getName());
					wallet.setPrivateKey(walletUpdate.getPrivateKey());
					wallet.setPublicKey(walletUpdate.getPublicKey());
					log.info("Wallet {} was updated successfully!", wallet);
					return wallet;
				})
				.orElseThrow(() -> new RuntimeException("Wallet for update not found!")));
	}

	/**
	 * Removes the specified wallet from the wallets collection based on wallet id.
	 *
	 * @param id - id of the wallet to be removed
	 * @return removed wallet
	 * @throws RuntimeException in case the wallet is found in the wallets collection
	 */
	public Optional<Wallet> deleteWallet(Long id) {
		return Optional.of(wallets.stream()
				.filter(w -> w.getId().equals(id))
				.findAny()
				.map(w -> {
					wallets.remove(w);
					log.info("Wallet with id {} was deleted successfully!", id);
					return w;
				})
				.orElseThrow(() -> new RuntimeException("Wallet for delete not found!")));
	}

	/**
	 * Actual wallets collection state
	 *
	 * @return wallets collection
	 */
	public Set<Wallet> fetchWallets() {
		return wallets;
	}

	//test data initialization
//	@PostConstruct
//	private void initWallets() {
//		Set<Currency> currencies = new LinkedHashSet<>();
//		currencies.add(new Currency(1.0, "BTC"));
//		currencies.add(new Currency(5.0, "ETH"));
//		currencies.add(new Currency(10.0, "LTC"));
//
//		while (wallets.size() < 5) {
//			Wallet wallet = Wallet.builder()
//					.name("wallet " + wallets.size() + 1)
//					.privateKey(UUID.randomUUID().toString())
//					.publicKey(UUID.randomUUID().toString())
//					.currencies(currencies)
//					.build();
//			wallet.assignId();
//			wallets.add(wallet);
//			log.info("wallet {} initialized", wallet);
//		}
//	}
}
