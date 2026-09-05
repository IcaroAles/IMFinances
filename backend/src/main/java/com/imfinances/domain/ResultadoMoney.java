package com.imfinances.domain;

public sealed interface ResultadoMoney {
    record Sucesso(Money valor) implements ResultadoMoney {
    }

    record Falha(String erro) implements ResultadoMoney {
    }
}
