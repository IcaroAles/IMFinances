package com.imfinances.domain;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void guardaValorCentavos() {
        Money dinheiro = Money.emCentavos(1000);
        assertThat(dinheiro.centavos()).isEqualTo(1000);
    }

    @Test
    void valorNegativoLancaExessao() {
        assertThatThrownBy(() -> Money.emCentavos(-5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emReaisDevolveCentavos() {
        // Java
        ResultadoMoney r = Money.emReais(0.29);
        if (!(r instanceof ResultadoMoney.Sucesso(Money valor))) {
            throw new AssertionError("deveria ter aceitado, veio: " + r);
        }
        assertThat(valor.centavos()).isEqualTo(29L);
    }

    @Test
    void emReaisFalhaNegativo() {
        ResultadoMoney r = Money.emReais(-5);
        assertThat(r).isInstanceOf(ResultadoMoney.Falha.class);
    }

    @Test
    void emReaisFalhaNaN() {
        ResultadoMoney nn = Money.emReais(Double.NaN);
        assertThat(nn).isInstanceOf(ResultadoMoney.Falha.class);
    }
}