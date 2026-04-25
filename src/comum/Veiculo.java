package comum;
import java.io.Serializable;

public class Veiculo implements Serializable {
    private final int id;
    private final String cor;
    private final String tipo;
    private final int estacaoId;
    private final int funcionarioId;
    private int posFabrica;
    private int posLoja;

    public Veiculo(int id, String cor, String tipo, int estacaoId, int funcionarioId) {
        this.id = id;
        this.cor = cor;
        this.tipo = tipo;
        this.estacaoId = estacaoId;
        this.funcionarioId = funcionarioId;
    }

    public void setPosFabrica(int pos) { this.posFabrica = pos; }
    public void setPosLoja(int pos) { this.posLoja = pos; }

    @Override
    public String toString() {
        return String.format("ID:%d | %s | %s | Est:%d | Func:%d", id, cor, tipo, estacaoId, funcionarioId);
    }
}