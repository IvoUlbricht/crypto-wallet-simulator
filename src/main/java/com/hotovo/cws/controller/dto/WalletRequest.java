package com.hotovo.cws.controller.dto;

import com.hotovo.cws.domain.Currency;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest {

	@NotNull
	private String privateKey;
	@NotNull
	private String publicKey;
	@NotNull
	private String name;
	private Set<Currency> currencies;
}
