package com.hotovo.cws.controller;

import com.hotovo.cws.controller.dto.CurrencyBuyRequest;
import com.hotovo.cws.controller.dto.CurrencyTransferRequest;
import com.hotovo.cws.controller.dto.WalletRequest;
import com.hotovo.cws.domain.Currency;
import com.hotovo.cws.domain.Wallet;
import com.hotovo.cws.service.CurrencyService;
import com.hotovo.cws.service.WalletService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/wallet")
public class WalletController {

	@Autowired
	private WalletService walletService;

	@Autowired
	private CurrencyService currencyService;

	@ApiOperation(value = "R-get-wallet",
			notes = "Get a wallet by its id",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Wallet info", response = Wallet.class),
			@ApiResponse(code = 404, message = "Wallet not found"),
			@ApiResponse(code = 400, message = "Request not valid please check requested parameters")})
	@GetMapping(value = "/{id}")
	public ResponseEntity getWalletInformation(@PathVariable("id") @NotNull Long id) {
		log.debug("Request for get wallet with id: {}", id);
		try {
			return ResponseEntity.ok(walletService.getWalletInformation(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-create-wallet",
			notes = "Create new wallet entry",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Created wallet", response = Wallet.class),
			@ApiResponse(code = 400, message = "Request not valid or wallet already existing!")})
	@PostMapping(value = "")
	public ResponseEntity createWallet(@RequestBody @Valid WalletRequest wallet) {
		log.debug("Request for CREATE wallet {}", wallet);
		try {
			Set<Currency> validCurrencies = wallet.getCurrencies().stream()
					.filter(currency -> currencyService.isCurrencyValid(currency.getSymbol()))
					.collect(Collectors.toSet());
			wallet.setCurrencies(validCurrencies);
			log.info("wallet request currencies{}", validCurrencies.toString());
			return ResponseEntity.ok(walletService.createWallet(wallet));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-update-wallet",
			notes = "Update specific wallet entry by id",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Updated wallet", response = Wallet.class),
			@ApiResponse(code = 404, message = "Wallet not found"),
			@ApiResponse(code = 400, message = "Request not valid please check requested parameters")})
	@PutMapping(value = "/{id}")
	public ResponseEntity updateWalletInformation(@PathVariable("id") Long id, @Valid @RequestBody WalletRequest wallet) {
		log.debug("Request for UPDATE wallet with id {}", id);
		try {
			return ResponseEntity.ok(walletService.updateWallet(id, wallet));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-delete-wallet",
			notes = "Delete specific wallet entry by id",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Deleted wallet", response = Wallet.class),
			@ApiResponse(code = 404, message = "Wallet not found"),
			@ApiResponse(code = 400, message = "Request not valid please check requested parameters")})
	@DeleteMapping(value = "/{id}")
	public ResponseEntity deleteWallet(@PathVariable("id") @NotNull Long id) {
		log.debug("Request for DELETE wallet with id {}", id);
		try {
			return ResponseEntity.ok(walletService.deleteWallet(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-currency-buy",
			notes = "Transfer specified amount of particular currency on the wallet specified by id.\n"
					+ "User can also specify destination currency, conversion will be done based on the real-time data from https://www.cryptocompare.com/",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Wallet with the currency changes", response = Wallet.class),
			@ApiResponse(code = 404, message = "Price for currency not found or not valid"),
			@ApiResponse(code = 400, message = "Request not valid please check requested parameters")})
	@PostMapping(value = "/{id}/currency-buy")
	public ResponseEntity buyCurrency(@Valid @RequestBody CurrencyBuyRequest request) {
		log.debug("Request for CURRENCY BUY wallet with id {} currency {} amount {} destCurrency {}",
				request.getId(), request.getCurrency(), request.getAmount(), request.getDestCurrency());
		try {
			Wallet sourceWallet = walletService.getWalletInformation(request.getId());
			return ResponseEntity.ok(currencyService.buyCurrency(request.getCurrency(), request.getAmount(), request.getDestCurrency(), sourceWallet));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-currency-transfer",
			notes = "Transfer specified amount of particular currency between source and destination wallet specified by their id's.\n"
					+ "User can also specify destination currency, conversion will be done based on the real-time data from https://www.cryptocompare.com/",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Currency transfer success"),
			@ApiResponse(code = 404, message = "Source or destination not found, or price for currency not found or not valid"),
			@ApiResponse(code = 400, message = "Request not valid or currency transfer failed")})
	@PostMapping(value = "/currency-transfer")
	public ResponseEntity transferCurrency(@Valid @RequestBody CurrencyTransferRequest request) {

		log.debug("Request for CURRENCY TRANSFER from wallet with id {} currency {} amount {} \n"
						+ "to destination wallet with id {} currency {}", request.getSrcId(), request.getSrcCurrency(), request.getSrcAmount(), request.getDestId(),
				request.getDestCurrency());

		try {
			Wallet sourceWallet = walletService.getWalletInformation(request.getSrcId());
			Wallet destinationWallet = walletService.getWalletInformation(request.getDestId());

			currencyService.transferCurrency(sourceWallet, request.getSrcCurrency(), request.getSrcAmount(), destinationWallet, request.getDestCurrency());
			return ResponseEntity.ok("Currency transfer success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@ApiOperation(value = "R-get-wallets",
			notes = "Get all wallet entries with paging support",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "Paged wallet entries", response = Wallet.class)
	@GetMapping(value = "/list")
	public Page<Wallet> getAllWallets(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		return walletService.fetchWallets(pageable);
	}

}
