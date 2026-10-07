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
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(4), 3);
        if (!(contateste instanceof Resultado.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }
        assertThat(plano.parcelas()).containsExactly(Money.emCentavos(2), Money.emCentavos(1), Money.emCentavos(1));
    }

    @Test
    void parcelaExataQuandoNIgualTotal() {
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(3), 3);
        if (!(contateste instanceof Resultado.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }
        assertThat(plano.parcelas()).containsExactly(Money.emCentavos(1), Money.emCentavos(1), Money.emCentavos(1));
    }

    @Test
    void falhaParcelasDemais() {
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(5), 6);
        assertThat(contateste).isInstanceOf(Resultado.Falha.class);
    }

    @Test
    void falhaUmaParcela() {
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(150), 1);
        assertThat(contateste).isInstanceOf(Resultado.Falha.class);
    }

    @Property
    void somaParcelasIgualTotal(
            @ForAll @LongRange(min = 25, max = 100000000) long total,
            @ForAll @IntRange(min = 2, max = 24) int n
    ) {
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(total), n);
        if (!(contateste instanceof Resultado.Sucesso(PlanoDeParcelamento plano))) {
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

    @Test
    void sobraMaiorQue1VaiParaPrimeiraParcela() {
        Resultado<PlanoDeParcelamento> contateste = PlanoDeParcelamento.parcelar(Money.emCentavos(10000), 7);
        if (!(contateste instanceof Resultado.Sucesso(PlanoDeParcelamento plano))) {
            throw new AssertionError("deveria ter aceitado, veio: " + contateste);
        }
        assertThat(plano.parcelas()).containsExactly(Money.emCentavos(1432), Money.emCentavos(1428), Money.emCentavos(1428), Money.emCentavos(1428), Money.emCentavos(1428), Money.emCentavos(1428), Money.emCentavos(1428));
    }
}
