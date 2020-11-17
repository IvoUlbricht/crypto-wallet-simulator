package com.hotovo.cws.service;

import static com.hotovo.cws.service.TestDataService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class WalletServiceTest {

	@BeforeEach
	void setUp() {
		sut.fetchWallets().clear();
	}

	@Autowired
	private WalletService sut;

	@Test
	@DisplayName("when_wallet_create_then_wallet_created")
	void create_wallet() {
		Wallet wallet = sut.createWallet(createTestWallet());

		assertWallet(wallet, NAME, PRIVATE_KEY, PUBLIC_KEY, Wallet.getWalletId().decrementAndGet());
	}

	@Test
	@DisplayName("given_existing_wallet_when_get_then_wallet_returned")
	void get_wallet_info_success() {
		Wallet wallet = sut.createWallet(createTestWallet());

		Optional<Wallet> walletInfo = sut.getWalletInformation(wallet.getId());
		assertThat(walletInfo).isPresent();
		assertWallet(walletInfo.get(), NAME, PRIVATE_KEY, PUBLIC_KEY, wallet.getId());
	}

	@Test
	@DisplayName("given_not_existing_wallet_when_get_then_wallet_return_failed")
	void get_wallet_info_fail() {
		Optional<Wallet> walletInfo = sut.getWalletInformation(123L);
		assertThat(walletInfo).isNotPresent();
	}

	@Test
	@DisplayName("given_existing_wallet_when_wallet_update_then_wallet_updated")
	void update_wallet_success() {
		Wallet wallet = sut.createWallet(createTestWallet());

		Optional<Wallet> walletInfo =
				sut.updateWallet(
						Wallet.builder().id(wallet.getId()).name("test wallet updated").publicKey("public key changed").privateKey("private key changed").build());
		assertThat(walletInfo).isPresent();
		assertWallet(walletInfo.get(), "test wallet updated", "private key changed", "public key changed", wallet.getId());
	}

	@Test
	@DisplayName("given_not_existing_wallet_when_wallet_update_then_exception")
	void update_wallet_fail() {
		assertThatThrownBy(() ->
				sut.updateWallet(Wallet.builder().name("test wallet updated").publicKey("public key changed").privateKey("private key changed").build()))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Wallet for update not found!");
	}

	@Test
	@DisplayName("given_existing_wallet_when_delete_then_wallet_deleted")
	void delete_wallet_success() {
		Wallet wallet = sut.createWallet(createTestWallet());

		Optional<Wallet> deletedWallet = sut.deleteWallet(wallet.getId());
		assertThat(deletedWallet).isPresent();
		assertWallet(deletedWallet.get(), NAME, PRIVATE_KEY, PUBLIC_KEY, wallet.getId());
		assertThat(sut.fetchWallets()).isEmpty();
	}

	@Test
	@DisplayName("given_not_existing_wallet_when_delete_then_exception")
	void delete_wallet_fail() {
		assertThatThrownBy(() -> sut.deleteWallet(123L))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Wallet for delete not found!");
	}

	private void assertWallet(Wallet testedWallet, String name, String privateKey, String publicKey, Long id) {
		assertThat(testedWallet.getId()).isEqualTo(id);
		assertThat(testedWallet.getName()).isEqualTo(name);
		assertThat(testedWallet.getPrivateKey()).isEqualTo(privateKey);
		assertThat(testedWallet.getPublicKey()).isEqualTo(publicKey);
		assertThat(testedWallet.getCurrencies()).extracting(Currency::getSymbol).contains("BTC");
		assertThat(testedWallet.getCurrencies()).extracting(Currency::getAmount).contains(1.0);
	}

}