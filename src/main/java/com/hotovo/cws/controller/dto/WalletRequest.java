package com.hotovo.cws.controller.dto;

import com.hotovo.cws.domain.Currency;
import java.util.Set;
import javax.validation.constraints.NotBlank;
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

	@NotBlank(message = "wallet private key can not be blank")
	private String privateKey;
	@NotBlank(message = "wallet public key can not be blank")
	private String publicKey;
	@NotBlank(message = "wallet name can not be blank")
	private String name;
	private Set<Currency> currencies;
}
