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

    public boolean ehPositivo() {
        return this.centavos > 0;
    }

    public Money somar(Money outro) {
        return Money.emCentavos(outro.centavos() + this.centavos());
    }

    public record Divisao(Money base, Money resto) {
    }

    public Divisao dividir(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("O valor inserido:" + n + " precisa ser no mínimo 1");
        }
        long base = this.centavos / n;
        long resto = this.centavos % n;

        return new Divisao(Money.emCentavos(base), Money.emCentavos(resto));
    }
}
