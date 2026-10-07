package com.imfinances.api;

import com.imfinances.domain.*;
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
        Resultado<Money> resultadoTotal = Money.emCentavosEntrada(request.totalCompraEmCentavos());

        switch (resultadoTotal) {
            case Resultado.Sucesso<Money>(Money valor) -> {

                Resultado<PlanoDeParcelamento> parcelamento = PlanoDeParcelamento.parcelar(valor, request.quantidadeParcelas());
                switch (parcelamento) {
                    case Resultado.Sucesso<PlanoDeParcelamento>(PlanoDeParcelamento plano) -> {
                        List<Long> listaNova = new ArrayList<>();
                        for (Money p : plano.parcelas()) {
                            listaNova.add(p.centavos());
                        }
                        PlanoDeParcelamentoResponse response = new PlanoDeParcelamentoResponse(listaNova, plano.total().centavos(), plano.quantidade());
                        return ResponseEntity.ok(response);
                    }
                    case Resultado.Falha<PlanoDeParcelamento>(String erro) -> {
                        ErroResponse erroResponse = new ErroResponse(erro);
                        return ResponseEntity.badRequest().body(erroResponse);
                    }
                }

            }
            case Resultado.Falha<Money>(String erro) -> {
                ErroResponse erroResponse = new ErroResponse(erro);
                return ResponseEntity.badRequest().body(erroResponse);
            }
        }

    }
}
