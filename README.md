# IMFinances

Aplicativo de controle de gastos pessoais, focado em uma dor diária: dificuldade de registrar gastos ao longo do dia de maneira rápida. Foco principal: **registrar um gasto em poucos segundos**, no dia a dia.

Projeto pessoal de estudo e portfólio, com foco em back-end: arquitetura, modelagem de domínio e testes.

> Status: em desenvolvimento.

---

## Stack

| Camada | Tecnologia | Status |
|---|---|---|
| Linguagem | Java 21 | Pronto |
| Build | Maven | Pronto |
| Testes | JUnit, AssertJ, jqwik | Pronto |
| API | Spring Boot | Em andamento |
| Banco | PostgreSQL em Docker | Próximo |
| Persistência | JPA / Hibernate (Spring Data JPA) | Próximo |
| Migrations | Flyway | Backlog |
| Front-end | TypeScript (PWA) | Backlog |

---

## Arquitetura

O back-end segue arquitetura hexagonal (ports & adapters). A regra central:

**O pacote `domain` não importa nada relacionado a framework e infraestrutura.** Nenhum `org.springframework`, nenhum JPA, nenhum JSON.

As dependências apontam sempre para dentro: a infraestrutura conhece o domínio, o domínio não conhece a infraestrutura.

Quais os ganhos reais com isso:

- Testes de domínio rodam em milissegundos, sem subir Spring nem banco.
- Trocar framework ou banco mexe só nos adapters; a regra de negócio não "enxerga" isso.
- A regra fica legível: as classes do domínio não têm anotações de infraestrutura misturadas.
- As dependências ficam explícitas: o domínio declara o que precisa numa interface (port), e a infraestrutura implementa.

---

## Decisões de domínio

### Dinheiro em centavos, nunca `double`

`Money` é um `record` que guarda um `long` de centavos.

Pois `double` é ponto flutuante binário, e `0.1` não tem representação exata em binário: `0.1 + 0.2` dá `0.30000000000000004`. Em dinheiro, esse erro acumulado vira centavo perdido, o que leva a conta a não fechar no final.

### Dois tipos de erro, dois tratamentos

| Tipo de erro | Exemplo | Tratamento | Na API |
|---|---|---|---|
| Entrada inválida (esperado, vem do usuário) | parcelar em 1x | retorna `Falha` (*Result pattern* com `sealed interface`) | 4xx |
| Bug de programação (algum problema no código) | construtor chamado com dado inválido | lança exceção | 5xx |

Quem chama uma fábrica que retorna `Resultado...` é obrigado pelo tipo a tratar o caso de falha.

### Parcelamento

`PlanoDeParcelamento.parcelar(total, n)` divide uma compra em parcelas:

- mínimo de 2 parcelas (1x é compra à vista);
- nenhuma parcela pode ser zero (não dá para parcelar 2 centavos em 5x);
- quando a divisão não é exata, a sobra vai para a primeira parcela (comportamento verificado em fatura real do meu cartão);
  - R$ 1,00 parcelado em 3x → R$ 0,34 + R$ 0,33 + R$ 0,33.

**Invariante:** a soma das parcelas é sempre igual ao total da compra.
O plano guarda só as parcelas. O total não é guardado de propósito, porque dá pra calcular a partir delas, e um valor calculado que fica guardado pode ficar desatualizado. Por isso o construtor não conhece o total original, e a invariante só pode ser verificada na fábrica.

### Imutabilidade

Os objetos de domínio são `record`s. O `PlanoDeParcelamento` guarda uma cópia da lista recebida, feita com `List.copyOf`. Isso protege o plano de dois jeitos: por ser uma cópia, alterar a lista original depois não muda o plano; e por ser imutável, ninguém consegue alterar a lista que sai de `plano.parcelas()`.

---

## Testes

```bash
cd backend
mvn test
```

- Testes de exemplo: protegem as regras de design (ex.: a sobra vai na primeira parcela, não em qualquer uma).
- Testes de propriedade (jqwik): geram entradas aleatórias e verificam a invariante (ex.: soma das parcelas == total, para qualquer total e qualquer número de parcelas).

Os dois se complementam: se a sobra fosse para a última parcela (e não para a primeira), o teste de propriedade continuaria verde, e só o teste pegaria.

Os testes de propriedade foram validados com sabotagem: eu quebrei o código de propósito para confirmar que o teste fica vermelho.

---

## Estrutura de pastas

```
backend/
  src/main/java/com/imfinances/
    domain/    regras de negócio, Java puro
    api/       endpoints HTTP (em andamento)
  src/test/java/com/imfinances/
    domain/    testes do domínio
```

---

## Como rodar

Pré-requisitos: JDK 21 e Maven 3.9+.

```bash
git clone https://github.com/IcaroAles/IMFinances.git
cd IMFinances/backend
mvn test
```

---

## Desenvolvedor

**Icaro Nery** - Desenvolvedor Back-End Júnior
**Linkedin** - https://www.linkedin.com/in/icaro-nery-93611a321/
