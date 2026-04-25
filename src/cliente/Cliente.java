package cliente;

import comum.LogService;
import comum.Veiculo;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cliente extends Thread {
    private final int clienteId;
    // REQUISITO VII: Cada cliente possui sua própria garagem (buffer)
    private final List<Veiculo> garagem = new ArrayList<>();
    private final Random random = new Random();

    public Cliente(int id) {
        this.clienteId = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Escolhe aleatoriamente uma das 3 lojas (Portas 20001, 20002 ou 20003)
                int lojaAlvo = random.nextInt(3) + 1;
                int portaLoja = 20000 + lojaAlvo;

                // Tenta realizar a compra conectando na loja via Socket
                try (Socket socket = new Socket("localhost", portaLoja)) {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // O cliente tenta receber o veículo.
                    // Se a loja não tiver estoque, a thread ficará bloqueada no stream ou
                    // a conexão será encerrada, fazendo o cliente esperar.
                    Veiculo veiculoComprado = (Veiculo) in.readObject();

                    if (veiculoComprado != null) {
                        garagem.add(veiculoComprado);

                        // REQUISITO VII: Log de Compra do Cliente
                        String msgLog = String.format("COMPRA_CLIENTE - Cliente ID: %d | %s | Loja: %d | Total na Garagem: %d",
                                clienteId, veiculoComprado.toString(), lojaAlvo, garagem.size());

                        LogService.gravar("log_cliente_" + clienteId + ".txt", msgLog);
                    }
                } catch (Exception e) {
                    // Se a loja estiver vazia ou offline, o cliente aguarda um pouco antes de tentar novamente
                }

                // O cliente aguarda um tempo aleatório entre 1 a 4 segundos para a próxima tentativa de compra
                Thread.sleep(random.nextInt(3000) + 1000);

            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando 20 threads de clientes...");

        // REQUISITO IV: Criação das 20 threads de clientes
        for (int i = 1; i <= 20; i++) {
            new Cliente(i).start();
        }
    }
}