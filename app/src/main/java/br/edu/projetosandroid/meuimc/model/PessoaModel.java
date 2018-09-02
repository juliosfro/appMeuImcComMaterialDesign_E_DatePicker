package br.edu.projetosandroid.meuimc.model;

import java.io.Serializable;

/**
 * Created by Julio on 30/08/2018.
 */

public class PessoaModel implements Serializable {
    private Integer _id;
    private String _nome;
    private String _dataNascimento;
    private String _peso;
    private String _altura;
    private String _imc;

    public String getNome() {
        return this._nome;
    }

    public void setNome(String nome) {
        this._nome = nome.toUpperCase();
    }

    public String getDataNascimento() {
        return this._dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this._dataNascimento = dataNascimento;
    }

    public String getPeso() {
        return this._peso;
    }

    public void setPeso(String peso) {
        this._peso = peso;
    }

    public String getAltura() {
        return this._altura;
    }

    public void setAltura(String altura) {
        this._altura = altura;
    }

    public Integer getId() {
        return this._id;
    }

    public void setId(Integer id) {
        this._id = id;
    }

    public String getImc() {
        return this._imc;
    }

    public void setImc(String imc) {
        this._imc = imc;
    }

    public Double calcularImc(PessoaModel pessoa) {
        Double double_altura = Double.parseDouble(pessoa.getAltura());
        Double double_peso = Double.parseDouble(pessoa.getPeso());
        Double _resultadoImcDouble = double_peso / (double_altura / 100 * double_altura / 100);
        return _resultadoImcDouble;
    }

    // Sobrescrevendo o toString pra retornar somente o campo nome.
    @Override
    public String toString() {
        return "ID - " + _id.toString() + (" | Nome: ") + _nome.toString();
    }
}