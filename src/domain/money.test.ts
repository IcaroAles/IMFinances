import { describe, it, expect } from "vitest";
import { Money } from "./money";
import fc, { bigInt } from "fast-check";

describe("Money", () => {
  describe("emCentavos", () => {
    it("guarda o valor em centavos", () => {
      expect(Money.emCentavos(1000n).centavosBrutos()).toBe(1000n);
    });

    it("lança exceção ao invés de retornar o result", () => {
      expect(() => Money.emCentavos(-1n)).toThrow();
    });
  });

  describe("emReais", () => {
    it("recebe 0.29 e precisa retornar 29n inteiro sem virgula", () => {
      const r = Money.emReais(0.29);

      if (!r.ok) {
        throw new Error(`Valor negativo ou 0`);
      }
      expect(r.valor.centavosBrutos()).toBe(29n);
    });
    it("recebe 10 e precisa retornar 1000n inteiro sem virgula", () => {
      const r = Money.emReais(10);

      if (!r.ok) {
        throw new Error(`Valor negativo ou 0`);
      }
      expect(r.valor.centavosBrutos()).toBe(1000n);
    });

    it("Rejeita numeros negativos, recebendo -5 e rejeitando", () => {
      const r = Money.emReais(-5);
      expect(r.ok).toBe(false);
    });

    it("Rejeita NaN, recebendo NaN e rejeitando", () => {
      const r = Money.emReais(NaN);
      expect(r.ok).toBe(false);
    });
  });

  describe("operações", () => {
    it("Rejeita 0, aceita 1 centavo", () => {
      expect(Money.emCentavos(1n).ehPositivo()).toBe(true);
      expect(Money.emCentavos(0n).ehPositivo()).toBe(false);
    });

    it("100.00 dividos em 7 partes tem base 1428 e resto 4  ", () => {
      const m = Money.emCentavos(10000n);
      const { base, resto } = m.dividir(7);
      expect(base.centavosBrutos()).toBe(1428n);
      expect(resto.centavosBrutos()).toBe(4n);
    });
  });

  describe("invariante", () => {
    it("para cada base x n + resto = Valor original  ", () => {
      fc.assert(
        fc.property(
          fc.bigInt({ min: 1n, max: 100_000_000n }), // gera um total qualquer
          fc.integer({ min: 1, max: 60 }), // gera um n qualquer

          (total, n) => {
            const dinheiro = Money.emCentavos(total);
            const { base, resto } = dinheiro.dividir(n);
            const conta =
              base.centavosBrutos() * BigInt(n) + resto.centavosBrutos();
            expect(conta).toBe(total);
          },
        ),
      );
    });
  });
});
