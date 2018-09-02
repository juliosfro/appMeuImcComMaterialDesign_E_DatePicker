package br.edu.projetosandroid.meuimc.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import br.edu.projetosandroid.meuimc.R;
import br.edu.projetosandroid.meuimc.dao.PessoaDAO;
import br.edu.projetosandroid.meuimc.model.PessoaModel;

public class TelaListarPessoaController extends AppCompatActivity {
    private ListView _listaVisivel;
    private Integer _idClicado;
    private Button _btnVoltarParaTelaPrincipal;

    PessoaModel pessoa = new PessoaModel();
    PessoaDAO pessoaDAO = new PessoaDAO(TelaListarPessoaController.this);

    ArrayList<PessoaModel> arrayListPessoa = new ArrayList<PessoaModel>();
    ArrayAdapter<PessoaModel> arrayAdapterPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.title_telaListarPessoa);
        setContentView(R.layout.activity_tela_listar_pessoa);

        // Instanciando o objeto.
        _listaVisivel = (ListView) findViewById(R.id.listaPessoas);
        _btnVoltarParaTelaPrincipal = (Button) findViewById(R.id.btn_voltar_para_tela_principal);

        _btnVoltarParaTelaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaListarPessoaController.this, TelaPrincipalController.class);
                startActivity(intent);
            }
        });

        // Habilitando o menu de contexto(clicar e segurar pra abrir opções).
        registerForContextMenu(_listaVisivel);

//        // Ao clicar em algum dos elementos da lista.
//        _listaVisivel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //pegando o elemento clicado e convertendo pra um obj da classe pessoa
//                PessoaModel pessoaEnviada = arrayAdapterPessoa.getItem(position);
//                //chamando a tela do formulario passando esses dados por parametro:
//                Intent i = new Intent(TelaListarPessoaController.this, TelaCadastrarPessoaController.class);
//                //passando os dados do registro clicado
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("pessoa-enviada", pessoaEnviada);
//                i.putExtras(bundle);
//                startActivity(i);
//            }
//        });

        // Ao clicar e segurar em algum dos elementos da lista:
        _listaVisivel.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //vai chamar o menu criado na linha 93
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Pegando o objeto clicado na ListView.
                pessoa = arrayAdapterPessoa.getItem(position);
                _idClicado = arrayAdapterPessoa.getItem(position).getId();
                return false;
            }
        });
    }

    // Fazendo a consulta e populando a lista.
    protected void popularListaPessoa() {
        pessoaDAO = new PessoaDAO(TelaListarPessoaController.this); //instanciando
        arrayListPessoa = pessoaDAO.selectAllPessoas(); //atribuindo à listagem o resultado do select

        if (_listaVisivel != null) {
            // Inserindo a listagem numa tela de exibição padrao.
            arrayAdapterPessoa = new ArrayAdapter<PessoaModel>(TelaListarPessoaController.this, android.R.layout.simple_list_item_1, arrayListPessoa);
            _listaVisivel.setAdapter(arrayAdapterPessoa); //atribuindo o adaptador para exibicao
        }
        pessoaDAO.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        popularListaPessoa();
    }

    // Criando o menu que aparece ao clicar e segurar.
    @Override
    public void onCreateContextMenu(final ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        // Opções de menu que irão aparecer.
        // MenuItem opMenuExibirDetalhes = menu.add("Exibir");
        MenuItem opMenuEditar = menu.add("Editar");
        MenuItem opMenuDeletar = menu.add("Deletar");

        opMenuEditar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                exibirConfirmacaoEditar();
                return false;
            }
        });

        opMenuDeletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                exibirConfirmacaoExcluir();
                return false;
            }
        });
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void alerta(String mensagem) {
        // Funcao pra exibir mensagem básica ao usuario.
        Toast.makeText(TelaListarPessoaController.this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void exibirConfirmacaoExcluir() {
        AlertDialog.Builder msgBoxDeletar = new AlertDialog.Builder(TelaListarPessoaController.this);
        msgBoxDeletar.setTitle("Confirmação de exclusão.");
        msgBoxDeletar.setIcon(R.drawable.ic_trash1);
        msgBoxDeletar.setMessage("Tem certeza que deseja excluir essa pessoa?");

        msgBoxDeletar.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pessoaDAO = new PessoaDAO(TelaListarPessoaController.this);
                long retornoDB = pessoaDAO.removerRegistro(pessoa);
                if (retornoDB == -1) {
                    alerta("Erro ao excluir.");
                } else {
                    alerta("Excluído com sucesso.");
                    // Após excluir o registro, faz o select no banco novamente.
                    popularListaPessoa();
                }
            }
        });

        msgBoxDeletar.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        msgBoxDeletar.show();
    }

    public void exibirConfirmacaoEditar() {
        AlertDialog.Builder msgBoxDeletar = new AlertDialog.Builder(TelaListarPessoaController.this);
        msgBoxDeletar.setTitle("Confirmação de edição.");
        msgBoxDeletar.setIcon(R.drawable.ic_edit1);
        msgBoxDeletar.setMessage("Deseja editar as informações dessa pessoa?");

        msgBoxDeletar.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                pessoaDAO = new PessoaDAO(TelaListarPessoaController.this);
                // Buscando no banco a pessoa selecionada.
                pessoa = pessoaDAO.buscarPorId(_idClicado);

                // Chamando a tela do formulario passando esses dados por parametro.
                Intent intent = new Intent(TelaListarPessoaController.this, TelaCadastrarPessoaController.class);

                // Passando os dados do registro clicado.
                Bundle bundle = new Bundle();
                bundle.putSerializable("pessoa-enviada", pessoa);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        msgBoxDeletar.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        msgBoxDeletar.show();
    }
}