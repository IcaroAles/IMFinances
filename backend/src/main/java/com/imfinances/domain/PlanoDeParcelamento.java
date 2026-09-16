package com.imfinances.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PlanoDeParcelamento(List<Money> parcelas) {
    public PlanoDeParcelamento {
        parcelas = List.copyOf(parcelas);
        if (parcelas.size() < 2) {
            throw new IllegalArgumentException("O numero de parcelas inserido: " + parcelas.size() + " é menor do que 2");
        }
        for (int i = 0; i < parcelas.size(); i++) {
            Money parcelaAtual = parcelas.get(i);
            if (!parcelaAtual.ehPositivo()) {
                throw new IllegalArgumentException("O valor inserido: " + parcelaAtual + " da posição " + (i + 1) + " deve ser maior que 0.");
            }
        }
    }

    public int quantidade() {
        return parcelas.size();
    }

    public Money total() {
        Money totalDinheiro = Money.emCentavos(0);
        for (Money p : parcelas) {
            totalDinheiro = totalDinheiro.somar(p);
        }
        return totalDinheiro;
    }

    public static ResultadoParcelamento parcelar(Money total, int n) {
        if (n <= 1) {
            return new ResultadoParcelamento.Falha("O valor recebido " + n + " deve ser maior ou igual a 2");
        }
        Money.Divisao divisaoParcelar = total.dividir(n);
        if (!divisaoParcelar.base().ehPositivo()) {
            return new ResultadoParcelamento.Falha("Divisão não pode ser feita pois o total é menor do que o numero de parcelas");
        }
        List<Money> listaParcelas = new ArrayList<>();
        Money primeiraParcela = divisaoParcelar.base().somar(divisaoParcelar.resto());
        listaParcelas.add(primeiraParcela);
        for (int i = 0; i < n - 1; i++) {
            listaParcelas.add(divisaoParcelar.base());
        }
        PlanoDeParcelamento planoParcelar = new PlanoDeParcelamento(listaParcelas);
        return new ResultadoParcelamento.Sucesso(planoParcelar);
    }
}
