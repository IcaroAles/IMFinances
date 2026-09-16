package com.imfinances.domain;

public sealed interface ResultadoParcelamento {
    record Sucesso(PlanoDeParcelamento plano) implements ResultadoParcelamento {
    }

    record Falha(String erro) implements ResultadoParcelamento {
    }
}
