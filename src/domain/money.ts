/**
 * Value object que representa uma quantia em dinheiro.
 *
 * Existe por um motivo concreto: `bigint` não carrega unidade. Ler `10n` como
 * R$ 10,00 quando eram 10 centavos é um erro que o compilador não pega — e que
 * já aconteceu neste projeto. Aqui a unidade fica no tipo, não no comentário.
 *
 * INVARIANTE: nunca negativo. Em v1 todo lançamento é gasto, e gasto é sempre
 * "quanto", nunca "pra que lado". Quando o estorno chegar (v2), quem carrega a
 * direção é a Transaction, não o dinheiro.
 *
 * Por dentro guarda SEMPRE centavos, em bigint.
 */

import { bigInt } from "fast-check";

export type ResultadoMoney =
  | { ok: true; valor: Money }
  | { ok: false; erro: string };

export class Money {
  private readonly centavos: bigint;

  // Construtor privado: ninguém cria um Money direto com `new`.
  // A única porta de entrada são as fábricas estáticas abaixo — e é isso que
  // obriga quem chama a declarar a unidade.
  private constructor(centavos: bigint) {
    this.centavos = centavos;
  }

  // ───────────────────────────────────────────────────────────────────
  // CONSTRUÇÃO
  // ───────────────────────────────────────────────────────────────────

  /**
   * Porta de entrada do INTERIOR. Recebe dado já validado (banco, outro Money).
   *
   * LANÇA se vier negativo — e lançar aqui é o certo: um negativo neste ponto
   * significa que TEM CÓDIGO SEU ERRADO, não usuário digitando bobagem. É o
   * caso em que você quer que exploda alto.
   */
  static emCentavos(valor: bigint): Money {
    if (valor < 0) {
      throw new Error("Não são aceitos valores negativos");
    }
    return new Money(valor);
  }

  /**
   * Porta de entrada da BORDA. Recebe número vindo do mundo de fora.
   *
   * Devolve Result porque entrada inválida aqui é fluxo normal, não bug.
   *
   * Lembra do que você viu no terminal: 0.29 * 100 = 28.999999999999996.
   * E BigInt() recusa qualquer coisa que não seja inteiro exato.
   *
   * Cuidados: Number.isFinite() barra NaN e Infinity de uma vez só.
   *           Math.round() resolve a sujeira do ponto flutuante.
   */
  static emReais(valor: number): ResultadoMoney {
    if (!Number.isFinite(valor)) {
      return { ok: false, erro: `Digite um número real` };
    }
    if (valor < 0) {
      return { ok: false, erro: `Digite um número positivo` };
    }
    const centavos = valor * 100;
    const arredondado = Math.round(centavos);
    const emBigint = BigInt(arredondado);
    const dinheiro = Money.emCentavos(emBigint);
    return { ok: true, valor: dinheiro };
  }

  // ───────────────────────────────────────────────────────────────────
  // OPERAÇÕES
  // ───────────────────────────────────────────────────────────────────

  /**
   * Devolve um Money NOVO. Não altera nem this nem outro.
   * Value object é imutável — somar dois valores não muda nenhum dos dois,
   * do mesmo jeito que 2 + 3 não transforma o 2 em outra coisa.
   */
  somar(outro: Money): Money {
    const somados = Money.emCentavos(outro.centavos + this.centavos);
    return somados;
  }

  /**
   * Divisão inteira em n partes, devolvendo a base e o que sobrou.
   * É exatamente a conta que hoje está solta dentro do parcelar().
   *
   * DECISÃO PENDENTE: o que fazer se n for 0 ou negativo? Divisão por zero
   * com bigint lança RangeError. Bug de programação ou entrada de usuário?
   * Você já tem o critério pra decidir.
   */
  dividir(n: number): { base: Money; resto: Money } {
    if(n <= 0){
      throw new Error("Valor precisa ser no mínimo 1");
    }
    const basem = Money.emCentavos(this.centavos / BigInt(n))
    const restom = Money.emCentavos(this.centavos % BigInt(n))
    return { base: basem, resto: restom };
  }

  ehPositivo(): boolean {
    const npos = 0n;
    return this.centavos > npos;
  }

  // ───────────────────────────────────────────────────────────────────
  // SAÍDA — só para as BORDAS
  //
  // Repositório (salvar no Postgres) e tela (formatar "R$ 1.000,00") precisam
  // do número cru. Regra de negócio NÃO deve chamar isto — se aparecer no meio
  // do domínio, tem coisa modelada no lugar errado.
  // ───────────────────────────────────────────────────────────────────

  centavosBrutos(): bigint {
    return this.centavos;
  }
}
