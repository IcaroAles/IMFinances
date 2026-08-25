import { describe, it, expect } from "vitest";
import { parcelar } from "./installments";
import fc from "fast-check";

describe("parcelar", () => {
  // Exemplo resolvido — use como molde para os outros dois.
  it("R$ 1.000,00 em 3x: a primeira absorve o centavo que sobra", () => {
    expect(parcelar(100000n, 3)).toEqual({
      ok: true,
      parcelas: [33334n, 33333n, 33333n],
    });
  });

  // TODO: R$ 100,00 em 7x  →  total 10000, sobram 4 centavos
  //       esperado: 1432, e depois seis parcelas de 1428
  it("R$ 100,00 em 7x: a primeira absorve os centavo que sobram", () => {
    expect(parcelar(10000n, 7)).toEqual({
      ok: true,
      parcelas: [1432n, 1428n, 1428n, 1428n, 1428n, 1428n, 1428n],
    });
  });

  // TODO: R$ 0,10 em 10x   →  total 10, não sobra nada
  //       esperado: dez parcelas de 1
  it("R$ 0,10 em 10x: nao sobram centavos", () => {
    expect(parcelar(10n, 10)).toEqual({
      ok: true,
      parcelas: [1n, 1n, 1n, 1n, 1n, 1n, 1n, 1n, 1n, 1n],
    });
  });

  // TODO: a invariante. Este é o teste mais importante dos quatro.
  //       Para cada caso acima, a soma das parcelas tem que ser
  //       exatamente o total. Dica de sintaxe para somar uma lista
  //       de bigint:
  it("a soma das parcelas é sempre igual ao total", () => {
    fc.assert(
      fc.property(
        fc.bigInt({ min: 2n, max: 100_000_000n }), // gera um total qualquer
        fc.integer({ min: 2, max: 60 }), // gera um n qualquer

        (total, n) => {
          const r = parcelar(total, n);

          if (n > total) {
            // entrada inválida → a função DEVE rejeitar
            expect(r.ok).toBe(false);
            return;
          }

          // entrada válida → a função DEVE aceitar
          if (!r.ok) {
            throw new Error(
              `deveria aceitar ${total} em ${n}x, mas rejeitou: ${r.erro}`,
            );
          }

          expect(r.parcelas.reduce((acc, p) => acc + p, 0n)).toBe(total);
          expect(r.parcelas.every((p) => p > 0n)).toBe(true);
        },
      ),
    );
  });
});

it("recusa 1x: à vista não é parcelamento", () => {
  expect(parcelar(100000n, 1).ok).toBe(false);
});

it("recusa quando não há centavos suficientes para n parcelas", () => {
  expect(parcelar(2n, 3).ok).toBe(false);
});
