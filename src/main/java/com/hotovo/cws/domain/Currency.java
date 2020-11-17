package com.hotovo.cws.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

	@EqualsAndHashCode.Exclude
	private Double amount;
	private String symbol;
}
