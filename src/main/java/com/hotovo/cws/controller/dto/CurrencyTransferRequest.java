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
public class CurrencyTransferRequest {

	@NotNull
	private Long srcId;

	@NotNull
	private String srcCurrency;

	@Positive
	private Double srcAmount;

	@NotNull
	private Long destId;

	@NotNull
	private String destCurrency;

}
