package loja;

import comum.Veiculo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LojaMain {
    private final int lojaId;
    private final EstoqueLoja estoque;

    public LojaMain(int id) {
        this.lojaId = id;
        this.estoque = new EstoqueLoja(); // Cada loja tem sua própria esteira de 20 posições
    }

    // Parte CLIENTE: Recebe da Fábrica
    public void conectarNaFabrica() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 12345)) {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    Veiculo v = (Veiculo) in.readObject();

                    // Coloca na esteira local e pega a posição
                    int posLoja = estoque.adicionarVeiculo(v);
                    v.setPosLoja(posLoja);

                    // REQUISITO VI.1: Log de Recebimento
                    System.out.printf("RECEBIMENTO - Loja: %d | %s | Pos Esteira Loja: %d%n",
                            lojaId, v.toString(), posLoja);
                }
            } catch (Exception e) {
                System.out.printf("Loja " + lojaId + " desconectada da fábrica.");
            }
        }).start();
    }

    // Parte SERVIDOR: Vende para o Cliente
    public void iniciarServidorDeVendas() {
        try (ServerSocket server = new ServerSocket(20000 + lojaId)) {
            while (true) {
                Socket clienteSocket = server.accept();
                new Thread(() -> processarVenda(clienteSocket)).start();
            }
        } catch (IOException e) {
            System.out.printf("Erro no servidor da Loja " + lojaId);
        }
    }

    private void processarVenda(Socket clienteSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream())) {
            // Retira do estoque (bloqueia se estiver vazio)
            Veiculo v = estoque.venderVeiculo();

            // Envia para o cliente
            out.writeObject(v);

            // REQUISITO VI.2: Log de Venda ao Cliente
            System.out.println("VENDA_CLIENTE - Loja: " + lojaId + "| Status: Entregue%n");

        } catch (Exception e) {
            // Erro silencioso ou log de erro de rede
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.printf("Por favor, informe o ID da loja (1, 2 ou 3) nos argumentos.%n");
            return;
        }
        int id = Integer.parseInt(args[0]);
        LojaMain loja = new LojaMain(id);

        System.out.printf("Iniciando Loja " + id + "...%n");
        loja.conectarNaFabrica();
        loja.iniciarServidorDeVendas();
    }
}