package com.hotovo.cws.service;

import com.hotovo.cws.controller.dto.WalletRequest;
import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
	public Wallet getWalletInformation(Long id) {
		return wallets.stream()
				.filter(wallet -> wallet.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Wallet with requested id not found!"));
	}

	/**
	 * Adds the wallet to the wallets collection
	 *
	 * @param createWallet - parameters for the wallet to be created
	 * @return created wallet
	 */
	public Wallet createWallet(WalletRequest createWallet) {
		Optional<Wallet> existingWallet = wallets.stream().filter(w -> isDuplicate(w, createWallet)).findFirst();

		if (!existingWallet.isPresent()) {
			Wallet createdWallet = Wallet.builder()
					.id(Wallet.getWalletId().getAndIncrement())
					.privateKey(createWallet.getPrivateKey())
					.publicKey(createWallet.getPublicKey())
					.currencies(createWallet.getCurrencies())
					.name(createWallet.getName())
					.build();
			wallets.add(createdWallet);
			log.info("Wallet {} was created successfully!", createWallet.getName());
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
	public Wallet updateWallet(Long id, WalletRequest walletUpdate) {
		return wallets.stream()
				.filter(w -> w.getId().equals(id))
				.findFirst()
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
	public Wallet deleteWallet(Long id) {
		return wallets.stream()
				.filter(w -> w.getId().equals(id))
				.findFirst()
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
	public Page<Wallet> fetchWallets(Pageable pageable) {
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), wallets.size());
		return new PageImpl<>(new ArrayList<>(wallets).subList(start, end), pageable, wallets.size());
	}

	private boolean isDuplicate(Wallet wallet, WalletRequest walletRequest) {
		return wallet.getName().equals(walletRequest.getName()) && wallet.getPrivateKey().equals(walletRequest.getPrivateKey()) && wallet.getPublicKey()
				.equals(walletRequest.getPublicKey());
	}

	//test data initialization
//	@PostConstruct
	private void initWallets() {
		Set<Currency> currencies = new LinkedHashSet<>();
		currencies.add(new Currency(100.0, "BTC"));
		currencies.add(new Currency(5000.0, "ETH"));
		currencies.add(new Currency(28.0, "LTC"));

		while (wallets.size() < 20) {
			long id = Wallet.getWalletId().getAndIncrement();
			wallets.add(Wallet.builder()
					.id(id)
					.name("wallet " + id)
					.privateKey(UUID.randomUUID().toString())
					.publicKey(UUID.randomUUID().toString())
					.currencies(currencies)
					.build());
		}
	}
}
