package com.imfinances.api;

import java.util.List;

public record PlanoDeParcelamentoResponse(List<Long> parcelasEmCentavos, long totalCompraEmCentavos,
                                          int quantidadeParcelas) {
}
