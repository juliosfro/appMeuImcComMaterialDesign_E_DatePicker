package br.edu.projetosandroid.meuimc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import br.edu.projetosandroid.meuimc.model.PessoaModel;

/**
 * Created by Julio on 30/08/2018.
 */

public class PessoaDAO extends SQLiteOpenHelper {
    //dados do banco:
    private static final String NOME_BANCO = "dbPessoa.db";
    private static final int VERSAO = 4;
    private static final String TABELA = "pessoa";

    //colunas da tabela:
    private static final String ID = "id";
    private static final String NOME = "nome";
    private static final String DATA_NASCIMENTO = "datanascimento";
    private static final String PESO = "peso";
    private static final String ALTURA = "altura";
    private static final String IMC = "imc";

    //metodo construtor:
    public PessoaDAO(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criando a estrutura do banco de dados.
        String sql = "CREATE TABLE " + TABELA + " ( " +
                " " + ID + " INTEGER PRIMARY KEY, " +
                " " + NOME + " TEXT, " + DATA_NASCIMENTO + " TEXT, " + PESO + " TEXT, "
                + ALTURA + " TEXT, " + IMC + " TEXT );";
        // Executando o SQL.
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Caso haja atualizacao no banco de dados.
        String sql = "DROP TABLE IF EXISTS " + TABELA;
        db.execSQL(sql);
        onCreate(db);
    }

    // Salvando novo registro no banco de dados.
    public long salvarCadastro(PessoaModel p) {
        ContentValues values = new ContentValues(); //vai armazenar os dados do objeto
        long retornoDB;

        values.put(NOME, p.getNome());
        values.put(DATA_NASCIMENTO, p.getDataNascimento());
        values.put(PESO, p.getPeso());
        values.put(ALTURA, p.getAltura());
        values.put(IMC, p.getImc());

        retornoDB = getWritableDatabase().insert(TABELA, null, values); //fazendo o insert
        return retornoDB; //retorna se deu certo ou nao a execucao
    }

    // Atualizando registro no banco de dados.
    public long atualizarCadastro(PessoaModel p) {
        ContentValues values = new ContentValues(); //vai armazenar os dados do objeto
        long retornoDB;

        values.put(NOME, p.getNome());
        values.put(DATA_NASCIMENTO, p.getDataNascimento());
        values.put(PESO, p.getPeso());
        values.put(ALTURA, p.getAltura());
        values.put(IMC, p.getImc());

        String[] argumentos = {String.valueOf(p.getId())}; //coletando o ID em questão
        retornoDB = getWritableDatabase().update(TABELA, values, ID + "=?", argumentos); //fazendo o update onde o id= a alguma coisa
        return retornoDB; //retorna se deu certo ou nao a execucao
    }

    // Removendo registro do banco de dados.
    public long removerRegistro(PessoaModel p) {
        long retornoDB;

        String[] argumentos = {String.valueOf(p.getId())}; //coletando o ID em questão
        retornoDB = getWritableDatabase().delete(TABELA, ID + "=?", argumentos);
        return retornoDB; //retorna se deu certo ou nao a execucao
    }

    // Fazendo o select *
    public ArrayList<PessoaModel> selectAllPessoas() {

        String[] colunas = {ID, NOME, DATA_NASCIMENTO, PESO, ALTURA, IMC};
        Cursor cursor = getWritableDatabase().query(TABELA, colunas, null, null, null, null, "nome ASC");

        // Array que vai armazenar os registros.
        ArrayList<PessoaModel> listPessoa = new ArrayList<PessoaModel>();

        // Enquanto houver registros, adiciona a pessoa a listagem.
        while (cursor.moveToNext()) {
            PessoaModel pessoa = new PessoaModel();
            pessoa.setId(cursor.getInt(0));
            pessoa.setNome(cursor.getString(1));
            pessoa.setDataNascimento(cursor.getString(2));
            pessoa.setPeso(cursor.getString(3));
            pessoa.setAltura(cursor.getString(4));
            pessoa.setImc(cursor.getString(5));

            // Adicionando o elemento completo na listagem.
            listPessoa.add(pessoa);
        }
        return listPessoa; //volta a listagem completa
    }

    public PessoaModel buscarPorId(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ID, NOME, DATANASCIMENTO, PESO, ALTURA, IMC FROM PESSOA WHERE ID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        // Enquanto houver registros...
        while (cursor.moveToNext()) {
            PessoaModel pessoa = new PessoaModel();
            pessoa.setId(cursor.getInt(0));
            pessoa.setNome(cursor.getString(1));
            pessoa.setDataNascimento(cursor.getString(2));
            pessoa.setPeso(cursor.getString(3));
            pessoa.setAltura(cursor.getString(4));
            pessoa.setImc(cursor.getString(5));

            return pessoa;
        }
        return null;
    }
}
