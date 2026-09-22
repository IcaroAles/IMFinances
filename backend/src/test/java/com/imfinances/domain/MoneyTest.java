package com.imfinances.domain;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
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

    @Test
    void somarVerificaValor() {
        Money valora = Money.emCentavos(10);
        Money valorb = Money.emCentavos(15);

        assertThat(valora.somar(valorb)).isEqualTo(Money.emCentavos(25));
    }

    @Test
    void divisaoConfereBaseEResto() {

        Money total = Money.emCentavos(10000);
        Money.Divisao d = total.dividir(7);

        assertThat(d.base().centavos()).isEqualTo(1428);
        assertThat(d.resto().centavos()).isEqualTo(4);
    }

    @Test
    void ehPositivoNegaValor0eAceitaValor1() {
        Money valorzero = Money.emCentavos(0);
        Money valorum = Money.emCentavos(1);

        assertThat(valorzero.ehPositivo()).isFalse();
        assertThat(valorum.ehPositivo()).isTrue();
    }

    @Property
    void provarContaFunciona(
            @ForAll @LongRange(min = 0, max = 100000000) long centavos,
            @ForAll @IntRange(min = 1, max = 100) int n
    ) {
        Money total = Money.emCentavos(centavos);
        Money.Divisao conta = total.dividir(n);
        long reconstrucao = conta.base().centavos() * n + conta.resto().centavos();

        assertThat(total.centavos()).isEqualTo(reconstrucao);
    }

    @Test
    void emCentavosEntradaFalhaNegativo() {
        ResultadoMoney nn = Money.emCentavosEntrada(-1);
        assertThat(nn).isInstanceOf(ResultadoMoney.Falha.class);
    }

    @Test
    void emCentavosEntradaAceita0() {
        ResultadoMoney nn = Money.emCentavosEntrada(0);
        assertThat(nn).isInstanceOf(ResultadoMoney.Sucesso.class);
    }

    @Test
    void emCentavosEntradaAceitaValorNormal() {
        ResultadoMoney r = Money.emCentavosEntrada(1080);
        if (!(r instanceof ResultadoMoney.Sucesso(Money valor))) {
            throw new AssertionError("deveria ter aceitado, veio: " + r);
        }
        assertThat(valor.centavos()).isEqualTo(1080);
    }
}