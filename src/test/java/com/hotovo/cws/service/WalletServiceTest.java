package com.hotovo.cws.service;

import static com.hotovo.cws.service.TestDataService.createWalletRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotovo.cws.controller.dto.WalletRequest;
import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class WalletServiceTest {

	@Autowired
	private WalletService sut;

	@Test
	@DisplayName("when_wallet_create_then_wallet_created")
	void create_wallet() {
		WalletRequest walletRequest = createWalletRequest();
		Wallet wallet = sut.createWallet(walletRequest);

		assertWallet(wallet, walletRequest.getName(), walletRequest.getPrivateKey(), walletRequest.getPublicKey(), Wallet.getWalletId().decrementAndGet());
	}

	@Test
	@DisplayName("given_existing_wallet_then_exception_thrown")
	void create_wallet_duplicate() {
		//create first
		WalletRequest walletRequest = createWalletRequest();
		sut.createWallet(walletRequest);

		assertThatThrownBy(() ->
				sut.createWallet(walletRequest))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Wallet with requested name, private and public key already present! Won't be created");
	}


	@Test
	@DisplayName("given_existing_wallet_when_get_then_wallet_returned")
	void get_wallet_info_success() {
		WalletRequest walletRequest = createWalletRequest();
		Wallet wallet = sut.createWallet(walletRequest);

		Optional<Wallet> walletInfo = sut.getWalletInformation(wallet.getId());
		assertThat(walletInfo).isPresent();
		assertWallet(walletInfo.get(), walletRequest.getName(), walletRequest.getPrivateKey(), walletRequest.getPublicKey(), wallet.getId());
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
		Wallet wallet = sut.createWallet(createWalletRequest());

		Wallet walletInfo =
				sut.updateWallet(wallet.getId(), TestDataService.updateWalletRequest("test wallet updated", "private key changed", "public key changed"));
		assertThat(walletInfo).isNotNull();
		assertWallet(walletInfo, "test wallet updated", "private key changed", "public key changed", wallet.getId());
	}

	@Test
	@DisplayName("given_not_existing_wallet_when_wallet_update_then_exception")
	void update_wallet_fail() {
		assertThatThrownBy(() ->
				sut.updateWallet(123L, TestDataService.updateWalletRequest("test wallet updated", "private key changed", "public key changed")))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Wallet for update not found!");
	}

	@Test
	@DisplayName("given_existing_wallet_when_delete_then_wallet_deleted")
	void delete_wallet_success() {
		Wallet wallet = sut.createWallet(createWalletRequest());

		Wallet deletedWallet = sut.deleteWallet(wallet.getId());
		assertThat(deletedWallet).isNotNull();
		assertWallet(deletedWallet, wallet.getName(), wallet.getPrivateKey(), wallet.getPublicKey(), wallet.getId());
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