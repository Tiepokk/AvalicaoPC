package loja;

import comum.LogService;
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
                    String msgLog = String.format("RECEBIMENTO - Loja: %d | %s | Pos Esteira Loja: %d",
                            lojaId, v.toString(), posLoja);
                    LogService.gravar("log_loja_" + lojaId + "_recebimento.txt", msgLog);
                }
            } catch (Exception e) {
                LogService.gravar("erros_loja.txt", "Loja " + lojaId + " desconectada da fábrica.");
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
            LogService.gravar("erros_loja.txt", "Erro no servidor da Loja " + lojaId);
        }
    }

    private void processarVenda(Socket clienteSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream())) {
            // Retira do estoque (bloqueia se estiver vazio)
            Veiculo v = estoque.venderVeiculo();

            // Envia para o cliente
            out.writeObject(v);

            // REQUISITO VI.2: Log de Venda ao Cliente
            String msgLog = String.format("VENDA_CLIENTE - Loja: %d | %s | Status: Entregue",
                    lojaId, v.toString());
            LogService.gravar("log_loja_" + lojaId + "_vendas.txt", msgLog);

        } catch (Exception e) {
            // Erro silencioso ou log de erro de rede
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Por favor, informe o ID da loja (1, 2 ou 3) nos argumentos.");
            return;
        }
        int id = Integer.parseInt(args[0]);
        LojaMain loja = new LojaMain(id);

        System.out.println("Iniciando Loja " + id + "...");
        loja.conectarNaFabrica();
        loja.iniciarServidorDeVendas();
    }
}