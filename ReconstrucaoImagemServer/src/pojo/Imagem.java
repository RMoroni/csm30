package pojo;

public class Imagem {
    
    private Long id;
    private Long cliente_id;
    private String data_hora_inicio;
    private String data_hora_fim;
    private short altura;
    private short largura;
    private short iteracoes;
    private char status;
    private String nome;

    /*
    Status:
        'a' -> aguardando
        'r' -> em reconstrução
        's' -> reconstruido com sucesso
        'i' -> impossível de reconstruir (loop)
    */
    public Imagem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(Long cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getData_hora_inicio() {
        return data_hora_inicio;
    }

    public void setData_hora_inicio(String data_hora_inicio) {
        this.data_hora_inicio = data_hora_inicio;
    }

    public String getData_hora_fim() {
        return data_hora_fim;
    }

    public void setData_hora_fim(String data_hora_fim) {
        this.data_hora_fim = data_hora_fim;
    }

    public short getAltura() {
        return altura;
    }

    public void setAltura(short altura) {
        this.altura = altura;
    }

    public short getLargura() {
        return largura;
    }

    public void setLargura(short largura) {
        this.largura = largura;
    }

    public short getIteracoes() {
        return iteracoes;
    }

    public void setIteracoes(short iteracoes) {
        this.iteracoes = iteracoes;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
