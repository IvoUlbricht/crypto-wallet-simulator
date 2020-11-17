package com.hotovo.cws.controller.dto;

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

	@NotNull
	private Long id;

	@NotNull
	private String currency;

	@Positive
	private Double amount;

	@NotNull
	private String destCurrency;

}
