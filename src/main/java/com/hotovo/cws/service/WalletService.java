package com.hotovo.cws.service;

import com.hotovo.cws.controller.dto.WalletRequest;
import com.hotovo.cws.domain.Wallet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = {"wallets"})
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
	@Cacheable
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
	@Cacheable
	public Wallet createWallet(WalletRequest wallet) {
		Optional<Wallet> existingWallet = wallets.stream().filter(w -> isDuplicate(w, wallet)).findAny();

		if (!existingWallet.isPresent()) {
			Wallet createdWallet = Wallet.builder()
					.id(Wallet.getWalletId().getAndIncrement())
					.privateKey(wallet.getPrivateKey())
					.publicKey(wallet.getPublicKey())
					.currencies(wallet.getCurrencies())
					.name(wallet.getName())
					.build();
			wallets.add(createdWallet);
			log.info("Wallet {} was created successfully!", wallet.getName());
			return createdWallet;
		} else {
			throw new RuntimeException("Wallet with requested name, private and public key already present! Won't be created");
		}
	}

	/**
	 * Updates the specified wallet in the wallets collection (possible wallet parameters to be updated are name, privateKey and publicKey
	 *
	 * @param walletUpdate - parameters of the wallet to be updated
	 * @param id - specific id for wallet update
	 * @return updated wallet
	 * @throws RuntimeException in case the wallet is found in the wallets collection
	 */
	@CachePut(key = "#wallet.id")
	public Wallet updateWallet(Long id, WalletRequest walletUpdate) {
		return wallets.stream()
				.filter(w -> w.getId().equals(id))
				.findAny()
				.map(wallet -> {
					wallet.setName(walletUpdate.getName());
					wallet.setPrivateKey(walletUpdate.getPrivateKey());
					wallet.setPublicKey(walletUpdate.getPublicKey());
					log.info("Wallet {} was updated successfully!", wallet);
					return wallet;
				})
				.orElseThrow(() -> new RuntimeException("Wallet for update not found!"));
	}

	/**
	 * Removes the specified wallet from the wallets collection based on wallet id.
	 *
	 * @param id - id of the wallet to be removed
	 * @return removed wallet
	 * @throws RuntimeException in case the wallet is found in the wallets collection
	 */
	@CacheEvict(key = "#wallet.id")
	public Wallet deleteWallet(Long id) {
		return wallets.stream()
				.filter(w -> w.getId().equals(id))
				.findAny()
				.map(w -> {
					wallets.remove(w);
					log.info("Wallet with id {} was deleted successfully!", id);
					return w;
				})
				.orElseThrow(() -> new RuntimeException("Wallet for delete not found!"));
	}

	/**
	 * Actual wallets collection state
	 *
	 * @return wallets collection
	 */
	public List<Wallet> fetchWallets() {
		return new ArrayList<>(wallets);
	}

	private boolean isDuplicate(Wallet wallet, WalletRequest walletRequest) {
		return wallet.getName().equals(walletRequest.getName()) && wallet.getPrivateKey().equals(walletRequest.getPrivateKey()) && wallet.getPublicKey()
				.equals(walletRequest.getPublicKey());
	}

	//	test data initialization
//	@PostConstruct
//	private void initWallets() {
//		Set<Currency> currencies = new LinkedHashSet<>();
//		currencies.add(new Currency(1.0, "BTC"));
//		currencies.add(new Currency(5.0, "ETH"));
//		currencies.add(new Currency(10.0, "LTC"));
//
//		while (wallets.size() < 20) {
//			long id = Wallet.getWalletId().getAndIncrement();
//			wallets.add(Wallet.builder()
//					.id(id)
//					.name("wallet " + id)
//					.privateKey(UUID.randomUUID().toString())
//					.publicKey(UUID.randomUUID().toString())
//					.currencies(currencies)
//					.build());
//		}
//	}
}
