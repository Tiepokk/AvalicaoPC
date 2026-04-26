package veiculo;
import java.io.Serializable;

public class Veiculo implements Serializable {
    private final int id;
    private final String cor;
    private final String tipo;
    private final int estacaoId;
    private final int funcionarioId;
    private int lojaId;

    public Veiculo(int id, String cor, String tipo, int estacaoId, int funcionarioId) {
        this.id = id;
        this.cor = cor;
        this.tipo = tipo;
        this.estacaoId = estacaoId;
        this.funcionarioId = funcionarioId;
    }

    public int getLojaId() { return lojaId; }
    public void setLojaId(int id) { this.lojaId = id; }
    public void setPosFabrica(int pos) {}
    public void setPosLoja(int pos) {}

    @Override
    public String toString() {
        return String.format("ID:%d | %s | %s | Est:%d | Func:%d", id, cor, tipo, estacaoId, funcionarioId);
    }
}