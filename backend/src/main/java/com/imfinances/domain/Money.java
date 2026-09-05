package com.imfinances.domain;

public record Money(long centavos) {
    public Money {
        if (centavos < 0) {
            throw new IllegalArgumentException("O valor inserido:" + centavos + " é menor do que 0");
        }
    }

    public static Money emCentavos(long valor) {
        return new Money(valor);
    }

    public static ResultadoMoney emReais(double valor) {
        if (!Double.isFinite(valor)) {
            return new ResultadoMoney.Falha("O valor recebido foi " + valor + " e ele não é finito.");
        } else if (valor < 0) {
            return new ResultadoMoney.Falha("O valor recebido foi " + valor + " e é negativo/menor do que 0.");
        } else {
            long centavos = Math.round(valor * 100);
            return new ResultadoMoney.Sucesso(Money.emCentavos(centavos));
        }
    }
}
