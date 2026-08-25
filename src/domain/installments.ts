/**
 * Divide uma compra parcelada em N parcelas.
 *
 * Especificação (definida no design, antes do código):
 *
 *   pré-condições:
 *     n >= 2           1x é compra à vista, não é parcelamento
 *     n <= total       senão gera parcelas de R$ 0,00
 *
 *   base  = total / n           divisão inteira
 *   resto = total % n
 *
 *   parcela 1     = base + resto      a primeira absorve a sobra
 *   parcelas 2..n = base
 *
 *   invariante:
 *     soma(parcelas) === total, para qualquer total e qualquer n válidos
 *
 * @param total valor da compra EM CENTAVOS
 * @param n     quantidade de parcelas
 */

  type ResultadoParcelamento =
  | { ok: true;  parcelas: bigint[] }
  | { ok: false; erro: string };

export function parcelar(total: bigint, n: number): ResultadoParcelamento {


  if (n < 2) {
    return { ok: false, erro: `Parcelamento exige pelo menos duas parcelas, recebeu ${n}` };
  }
  if (n > total) {
    return { ok: false, erro: `Não é possivel dividir ${total} centavos em ${n} parcelas` };
  }


  const base = total / BigInt(n);
  const resto = total % BigInt(n);

  const parcelas: bigint[] = [];

  for (let i = 0; i < n; i++) {
    if (i === 0) {
      parcelas.push(base + resto);
    } else {
      parcelas.push(base);
    }
  }

  return { ok: true, parcelas };
}
