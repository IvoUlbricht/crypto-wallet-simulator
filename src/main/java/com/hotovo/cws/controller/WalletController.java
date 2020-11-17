package com.hotovo.cws.controller;

import com.hotovo.cws.controller.dto.CurrencyBuyRequest;
import com.hotovo.cws.controller.dto.CurrencyTransferRequest;
import com.hotovo.cws.domain.Wallet;
import com.hotovo.cws.service.CurrencyService;
import com.hotovo.cws.service.WalletService;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/wallet")
public class WalletController {

	@Autowired
	private WalletService walletService;

	@Autowired
	private CurrencyService currencyService;


	@GetMapping(value = "/{id}")
	public ResponseEntity getWalletInformation(@PathVariable("id") @NotNull Long id) {
		log.debug("Request for get wallet with id: {}", id);
		return walletService.getWalletInformation(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/create")
	public ResponseEntity createWallet(@RequestBody Wallet wallet) {
		log.debug("Request for CREATE wallet {}", wallet);
		return ResponseEntity.ok(walletService.createWallet(wallet));
	}

	@PutMapping(value = "/update/{id}")
	public ResponseEntity updateWalletInformation(@RequestBody Wallet wallet) {
		log.debug("Request for UPDATE wallet {}", wallet);
		return walletService.updateWallet(wallet)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.badRequest().build());
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity deleteWallet(@PathVariable("id") @NotNull Long id) {
		log.debug("Request for DELETE wallet with id {}", id);
		return walletService.deleteWallet(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/{id}/currency-buy")
	public ResponseEntity buyCurrency(@Valid CurrencyBuyRequest request) {
		log.debug("Request for CURRENCY BUY wallet with id {} currency {} amount {} destCurrency {}",
				request.getId(), request.getCurrency(), request.getAmount(), request.getDestCurrency());

		return walletService.getWalletInformation(request.getId())
				.map(wallet -> currencyService.buyCurrency(request.getCurrency(), request.getAmount(), request.getDestCurrency(), wallet))
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/currency-transfer")
	public ResponseEntity transferCurrency(@RequestBody @Valid CurrencyTransferRequest request) {

		log.debug("Request for CURRENCY TRANSFER from wallet with id {} currency {} amount {} \n"
						+ "to destination wallet with id {} currency {}", request.getSrcId(), request.getSrcCurrency(), request.getSrcAmount(), request.getDestId(),
				request.getDestCurrency());

		Optional<Wallet> sourceWallet = walletService.getWalletInformation(request.getSrcId());
		if (!sourceWallet.isPresent()) {
			log.error("Source wallet with id {} not found!", request.getSrcId());
			return ResponseEntity.unprocessableEntity().body("Source wallet not found!");
		}

		Optional<Wallet> destinationWallet = walletService.getWalletInformation(request.getDestId());
		if (!destinationWallet.isPresent()) {
			log.error("Destination wallet with id {} not found!", request.getDestId());
			return ResponseEntity.unprocessableEntity().body("Destination wallet not found!");
		}
		boolean currencySuccess = currencyService.transferCurrency(sourceWallet.get(), request.getSrcCurrency(), request.getSrcAmount(), destinationWallet.get(),
				request.getDestCurrency());

		if (currencySuccess) {
			return ResponseEntity.ok("Currency transfer successfull");
		} else {
			return ResponseEntity.unprocessableEntity().body("Currency transfer failed, look for the application logs for more information!");
		}
	}

	@GetMapping(value = "/fetch-all")
	public Set<Wallet> getAllWallets() {
		return walletService.fetchWallets();
	}

}
