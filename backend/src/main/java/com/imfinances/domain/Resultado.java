package com.imfinances.domain;

public sealed interface Resultado<T> {
    record Sucesso<T>(T conteudoSucesso) implements Resultado<T> {
    }

    record Falha<T>(String erro) implements Resultado<T> {
    }
}
