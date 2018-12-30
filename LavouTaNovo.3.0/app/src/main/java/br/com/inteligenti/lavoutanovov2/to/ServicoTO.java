package br.com.inteligenti.lavoutanovov2.to;

import java.util.Date;

/**
 * Created by fernando on 30/12/17.
 */

public class ServicoTO {

    private Integer id;
    private Integer idCarro;
    private Integer idProfissional;
    
    private String codg_servico;
    private Double valr_valor;
    private Date data_data_servico;
    private String time_hora_servico;
    private Integer id_cliente;
    private String desc_endereco;
    private String codg_localizacao;
    private String codg_situacao;
    private Integer numr_estrelas_cliente;
    private String desc_comentario_cliente;
    private Integer numr_estrelas_profissional;
    private String desc_comentario_profissional;


    // Endereço
    String codgEstado;
    String descCidade;
    String descBairro;
    String descCep;
    String descRua;
    String descNumero;
    String descEdificio;
    String descAp;
    String descComplemento;

    String codgPagamento;
    String descPagamento;

    CreditCardTO cc;
    ClienteTO cliente;
    ProfissionalTO profissional;
    CarroTO carro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime_hora_servico() {
        return time_hora_servico;
    }

    public void setTime_hora_servico(String time_hora_servico) {
        this.time_hora_servico = time_hora_servico;
    }

    public String getCodg_servico() {
        return codg_servico;
    }

    public void setCodg_servico(String codg_servico) {
        this.codg_servico = codg_servico;
    }

    public Double getValr_valor() {
        return valr_valor;
    }

    public void setValr_valor(Double valr_valor) {
        this.valr_valor = valr_valor;
    }

    public Date getData_data_servico() {
        return data_data_servico;
    }

    public void setData_data_servico(Date data_data_servico) {
        this.data_data_servico = data_data_servico;
    }

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getDesc_endereco() {
        return desc_endereco;
    }

    public void setDesc_endereco(String desc_endereco) {
        this.desc_endereco = desc_endereco;
    }

    public String getCodg_localizacao() {
        return codg_localizacao;
    }

    public void setCodg_localizacao(String codg_localizacao) {
        this.codg_localizacao = codg_localizacao;
    }

    public String getCodg_situacao() {
        return codg_situacao;
    }

    public void setCodg_situacao(String codg_situacao) {
        this.codg_situacao = codg_situacao;
    }

    public Integer getNumr_estrelas_cliente() {
        return numr_estrelas_cliente;
    }

    public void setNumr_estrelas_cliente(Integer numr_estrelas_cliente) {
        this.numr_estrelas_cliente = numr_estrelas_cliente;
    }

    public Integer getNumr_estrelas_profissional() {
        return numr_estrelas_profissional;
    }

    public void setNumr_estrelas_profissional(Integer numr_estrelas_profissional) {
        this.numr_estrelas_profissional = numr_estrelas_profissional;
    }

    public String getCodgEstado() {
        return codgEstado;
    }

    public void setCodgEstado(String codgEstado) {
        this.codgEstado = codgEstado;
    }

    public String getDescCidade() {
        return descCidade;
    }

    public void setDescCidade(String descCidade) {
        this.descCidade = descCidade;
    }

    public String getDescBairro() {
        return descBairro;
    }

    public void setDescBairro(String descBairro) {
        this.descBairro = descBairro;
    }

    public String getDescCep() {
        return descCep;
    }

    public void setDescCep(String descCep) {
        this.descCep = descCep;
    }

    public String getDescRua() {
        return descRua;
    }

    public void setDescRua(String descRua) {
        this.descRua = descRua;
    }

    public String getDescNumero() {
        return descNumero;
    }

    public void setDescNumero(String descNumero) {
        this.descNumero = descNumero;
    }

    public String getDescEdificio() {
        return descEdificio;
    }

    public void setDescEdificio(String descEdificio) {
        this.descEdificio = descEdificio;
    }

    public String getDescAp() {
        return descAp;
    }

    public void setDescAp(String descAp) {
        this.descAp = descAp;
    }

    public String getDescComplemento() {
        return descComplemento;
    }

    public void setDescComplemento(String descComplemento) {
        this.descComplemento = descComplemento;
    }

    public String getCodgPagamento() {
        return codgPagamento;
    }

    public void setCodgPagamento(String codgPagamento) {
        this.codgPagamento = codgPagamento;
    }

    public String getDescPagamento() {
        return descPagamento;
    }

    public void setDescPagamento(String descPagamento) {
        this.descPagamento = descPagamento;
    }

    public CreditCardTO getCc() {
        return cc;
    }

    public void setCc(CreditCardTO cc) {
        this.cc = cc;
    }

    public ClienteTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteTO cliente) {
        this.cliente = cliente;
    }

    public Integer getIdCarro() {
        return idCarro;
    }

    public void setIdCarro(Integer idCarro) {
        this.idCarro = idCarro;
    }

    public Integer getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(Integer idProfissional) {
        this.idProfissional = idProfissional;
    }

    public CarroTO getCarro() {
        return carro;
    }

    public void setCarro(CarroTO carro) {
        this.carro = carro;
    }

    public ProfissionalTO getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalTO profissional) {
        this.profissional = profissional;
    }

    public String getDesc_comentario_cliente() {
        return desc_comentario_cliente;
    }

    public void setDesc_comentario_cliente(String desc_comentario_cliente) {
        this.desc_comentario_cliente = desc_comentario_cliente;
    }

    public String getDesc_comentario_profissional() {
        return desc_comentario_profissional;
    }

    public void setDesc_comentario_profissional(String desc_comentario_profissional) {
        this.desc_comentario_profissional = desc_comentario_profissional;
    }
}
