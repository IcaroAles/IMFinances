package com.imfinances.api;

import com.imfinances.domain.Money;
import com.imfinances.domain.PlanoDeParcelamento;
import com.imfinances.domain.ResultadoParcelamento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PlanoDeParcelamentoController {

    @PostMapping("/planos-de-parcelamento")
    public ResponseEntity<?> devolvePlanoDeParcelamento(@RequestBody PlanoDeParcelamentoRequest request) {
        Money total = Money.emCentavos(request.totalCompraEmCentavos());
        ResultadoParcelamento parcelamento = PlanoDeParcelamento.parcelar(total, request.quantidadeParcelas());
        switch (parcelamento) {
            case ResultadoParcelamento.Sucesso(PlanoDeParcelamento plano) -> {
                List<Long> listaNova = new ArrayList<>();
                for (Money p : plano.parcelas()) {
                    listaNova.add(p.centavos());
                }
                PlanoDeParcelamentoResponse response = new PlanoDeParcelamentoResponse(listaNova, plano.total().centavos(), plano.quantidade());
                return ResponseEntity.ok(response);
            }
            case ResultadoParcelamento.Falha(String erro) -> {
                ErroResponse erroResponse = new ErroResponse(erro);
                return ResponseEntity.badRequest().body(erroResponse);
            }
        }
    }
}
