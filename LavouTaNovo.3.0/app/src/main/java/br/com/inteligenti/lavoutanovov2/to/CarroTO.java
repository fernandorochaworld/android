package br.com.inteligenti.lavoutanovov2.to;

/**
 * Created by fernando on 30/12/17.
 */

public class CarroTO {
    private Integer id;
    private String codg_placa;
    private String desc_marca;
    private String desc_modelo;
    private String codg_cor;
    private String desc_outra_cor;
    private String codg_tamanho;
    private String file_foto;
    private Integer id_cliente;

    private String descImg;

    public String getFile_foto() {
        return file_foto;
    }

    public void setFile_foto(String file_foto) {
        this.file_foto = file_foto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodg_placa() {
        return codg_placa;
    }

    public void setCodg_placa(String codg_placa) {
        this.codg_placa = codg_placa;
    }

    public String getCodg_cor() {
        return codg_cor;
    }

    public void setCodg_cor(String codg_cor) {
        this.codg_cor = codg_cor;
    }

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getDesc_marca() {
        return desc_marca;
    }

    public void setDesc_marca(String desc_marca) {
        this.desc_marca = desc_marca;
    }

    public String getDesc_modelo() {
        return desc_modelo;
    }

    public void setDesc_modelo(String desc_modelo) {
        this.desc_modelo = desc_modelo;
    }

    public String getDesc_outra_cor() {
        return desc_outra_cor;
    }

    public void setDesc_outra_cor(String desc_outra_cor) {
        this.desc_outra_cor = desc_outra_cor;
    }

    public String getCodg_tamanho() {
        return codg_tamanho;
    }

    public void setCodg_tamanho(String codg_tamanho) {
        this.codg_tamanho = codg_tamanho;
    }

    public String getDescImg() {
        return descImg;
    }

    public void setDescImg(String descImg) {
        this.descImg = descImg;
    }
}
