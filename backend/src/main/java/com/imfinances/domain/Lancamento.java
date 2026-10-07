package com.imfinances.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Lancamento(UUID idLancamento, UUID idCompra, UUID idCategoria, String descricao, LocalDateTime dataHora,
                         Money valorParcela, int parcelaAtual, int quantidadeParcelas) {
    public Lancamento {
        Objects.requireNonNull(idLancamento, "O id de Lançamento não pode ser nulo");
        Objects.requireNonNull(idCompra, "O id de Compra não pode ser nulo");
        Objects.requireNonNull(idCategoria, "O id de Categoria não pode ser nulo");
        Objects.requireNonNull(descricao, "A Descrição não pode ser nula");
        Objects.requireNonNull(dataHora, "A Data e Hora não pode ser nula");
        Objects.requireNonNull(valorParcela, "O Valor não pode ser nulo");
        if (!valorParcela.ehPositivo()) {
            throw new IllegalArgumentException("O valor inserido: " + valorParcela + " deve ser maior que 0.");
        }
        if (quantidadeParcelas < 1) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser maior ou igual a 1");
        }
        if (parcelaAtual < 1 || parcelaAtual > quantidadeParcelas) {
            throw new IllegalArgumentException("A parcela atual deve ser maior ou igual a 1 e menor ou igual a quantidade de parcelas");
        }
        if (descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode estar vazia ou apenas espaços");
        }
    }

    //public static Lancamento compraParcelada(PlanoDeParcelamento plano, String descricao, LocalDateTime dataHora, UUID idCategoria) {

    // }
}
