package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.ModelosVeiculos;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConverteDados conversor=new ConverteDados();

    public void exibeMenu() {
        int opcao = 0;
        String endereco = null;

        var menu = """
                --- OPÇÕES ---
                Digite uma das opções para consultar:
                1. Carro
                2. Moto
                3. Caminhão
                0. Sair
                """;

        System.out.println(menu);

        try {
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    endereco = URL_BASE + "carros/marcas";
                    break;
                case 2:
                    endereco = URL_BASE + "motos/marcas";
                    break;
                case 3:
                    endereco = URL_BASE + "caminhoes/marcas";
                    break;
                case 0:
                    System.out.println("Finalizando a aplicação.");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    exibeMenu();
            }

            if (endereco != null) {
                String json = consumoApi.obterDados(endereco);
                System.out.println(json);

                var marcas=conversor.obterLista(json, Dados.class);
                marcas.stream()
                        .sorted(Comparator.comparing(Dados::nome))
                        .forEach(System.out::println);

                System.out.println("Digite o código da marca a ser consultada: ");
                var codigoMarca=scanner.nextLine();

                endereco=endereco+"/"+codigoMarca+"/modelos";
                json=consumoApi.obterDados(endereco);
                var modeloLista=conversor.obterDados(json, ModelosVeiculos.class);

                System.out.println("Modelos dessa marca:\n");
                modeloLista.modelosVeiculos().stream()
                        .sorted(Comparator.comparing(Dados::nome))
                        .forEach(System.out::println);
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira um número.");
            scanner.nextLine();
            exibeMenu();
        }
    }
}
