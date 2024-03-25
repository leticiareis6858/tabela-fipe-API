package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.ModelosVeiculos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        //cria o menu que é exibido no console
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
                //retorna todas as marcas de acordo com o tipo de veículo informado
                String json = consumoApi.obterDados(endereco);
                System.out.println(json);

                var marcas = conversor.obterLista(json, Dados.class);
                marcas.stream().sorted(Comparator.comparing(Dados::nome)).forEach(System.out::println);

                //consulta todos os modelos da marca que tiver o código digitado no console
                System.out.println("Digite o código da marca a ser consultada: ");
                var codigoMarca = scanner.nextLine();

                endereco = endereco + "/" + codigoMarca + "/modelos";
                json = consumoApi.obterDados(endereco);
                var modeloLista = conversor.obterDados(json, ModelosVeiculos.class);

                System.out.println("Modelos dessa marca:\n");
                modeloLista.modelosVeiculos().stream().sorted(Comparator.comparing(Dados::nome)).forEach(System.out::println);

                //consulta especificamente um modelo a partir do seu nome ou trecho do nome
                //(retorna todas as variações do modelo)
                System.out.println("Digite o nome ou um trecho do nome do modelo a ser consultado: ");
                var nomeVeiculo = scanner.nextLine();

                List<Dados> modelosFiltrados = modeloLista.modelosVeiculos().stream().filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase())).collect(Collectors.toList());

                System.out.println("\nModelos do veículo filtrados: ");
                modelosFiltrados.forEach(System.out::println);

                //consulta os valores do modelo que tiver seu código digitado
                //(retorna os valores de acordo com ano de lançamento)
                System.out.println("Digite o código do modelo para buscar os valores: ");
                var codigoModelo = scanner.nextLine();

                endereco = endereco + "/" + codigoModelo + "/anos";
                json = consumoApi.obterDados(endereco);
                List<Dados> anosModelo = conversor.obterLista(json, Dados.class);
                List<Veiculo> veiculos = new ArrayList<>();

                for (int i = 0; i < anosModelo.size(); i++) {
                    var enderecoAnos = endereco + "/" + anosModelo.get(i).codigo();
                    json = consumoApi.obterDados(enderecoAnos);
                    Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                    veiculos.add(veiculo);
                }
                System.out.println("\nTodas as avaliações do veículo por ano: ");
                veiculos.forEach(System.out::println);

            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira um número.");
            scanner.nextLine();
            exibeMenu();
        }
    }
}
