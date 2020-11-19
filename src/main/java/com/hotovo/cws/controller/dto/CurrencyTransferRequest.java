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
public class CurrencyTransferRequest {

	@NotNull(message = "source wallet id can not be null")
	private Long srcId;

	@NotBlank(message = "source currency can not be blank")
	private String srcCurrency;

	@Positive(message = "amount must be positive value")
	private Double srcAmount;

	@NotNull(message = "destination wallet id can not be null")
	private Long destId;

	@NotBlank(message = "destination currency can not be blank")
	private String destCurrency;

}
