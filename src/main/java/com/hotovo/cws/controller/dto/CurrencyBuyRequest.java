package com.hotovo.cws.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyBuyRequest {

	@NotNull(message = "wallet id can not be null")
	private Long id;

	@NotBlank(message = "currency can not be blank")
	private String currency;

	@NotNull
	@Positive(message = "amount must be positive value")
	private Double amount;

	@NotBlank(message = "destination currency can not be blank")
	private String destCurrency;

}
