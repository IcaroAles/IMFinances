package com.imfinances.domain;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanoDeParcelamentoTest {

    @Test
    void sobraVaiParaPrimeiraParcela() {
        ResultadoParcelamento contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(4), 3);
        if (!(contateste instanceof ResultadoParcelamento.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }
        assertThat(plano.parcelas()).containsExactly(Money.emCentavos(2), Money.emCentavos(1), Money.emCentavos(1));
    }

    @Test
    void parcelaExataQuandoNIgualTotal() {
        ResultadoParcelamento contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(3), 3);
        if (!(contateste instanceof ResultadoParcelamento.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }
        assertThat(plano.parcelas()).containsExactly(Money.emCentavos(1), Money.emCentavos(1), Money.emCentavos(1));
    }

    @Test
    void falhaParcelasDemais() {
        ResultadoParcelamento contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(5), 6);
        assertThat(contateste).isInstanceOf(ResultadoParcelamento.Falha.class);
    }

    @Test
    void falhaUmaParcela() {
        ResultadoParcelamento contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(150), 1);
        assertThat(contateste).isInstanceOf(ResultadoParcelamento.Falha.class);
    }

    @Property
    void somaParcelasIgualTotal(
            @ForAll @LongRange(min = 25, max = 100000000) long total,
            @ForAll @IntRange(min = 2, max = 24) int n
    ) {
        ResultadoParcelamento contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(total), n);
        if (!(contateste instanceof ResultadoParcelamento.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }

        assertThat(plano.total()).isEqualTo(Money.emCentavos(total));
    }

    @Test
    void copiaDefensiva() {
        List<Money> listaTeste = new ArrayList<>();
        listaTeste.add(Money.emCentavos(5));
        listaTeste.add(Money.emCentavos(5));
        listaTeste.add(Money.emCentavos(5));
        PlanoDeParcelamento planoTeste = new PlanoDeParcelamento(listaTeste);
        listaTeste.add(Money.emCentavos(4));

        assertThat(planoTeste.parcelas()).containsExactly(Money.emCentavos(5), Money.emCentavos(5), Money.emCentavos(5));
    }
}
