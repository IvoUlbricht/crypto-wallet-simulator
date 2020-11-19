package com.hotovo.cws.domain;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

	private static AtomicLong walletId = new AtomicLong(1);

	private Long id;
	private String privateKey;
	private String publicKey;
	private String name;
	private Set<Currency> currencies = new LinkedHashSet<>();

	public static AtomicLong getWalletId() {
		return walletId;
	}

}
