package br.edu.projetosandroid.meuimc.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.DecimalFormat;
import java.util.Calendar;

import br.edu.projetosandroid.meuimc.R;
import br.edu.projetosandroid.meuimc.dao.PessoaDAO;
import br.edu.projetosandroid.meuimc.model.PessoaModel;

public class TelaCadastrarPessoaController extends AppCompatActivity {
    private ImageButton _imgButtonDataNascimento;
    private Button _btnImc, _btnLimpar, _btnSalvarAlterar;
    private boolean _isVerificarDatePicker;
    private DatePickerDialog.OnDateSetListener _DateSetListner;
    private TextInputLayout _txtInputLayoutNome, _txtInputLayoutDataNascimento, _txtInputLayoutAltura, _txtInputLayoutPeso;
    private EditText _edtDataNascimento, _edtNome, _edtAltura, _edtPeso, _edtImc;
    PessoaModel pessoa = new PessoaModel();
    PessoaModel _pessoaEditar;
    PessoaDAO pessoaDao = new PessoaDAO(TelaCadastrarPessoaController.this);
    private long _retorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.title_telaCadastrarPessoa);
        setContentView(R.layout.activity_tela_cadastrar_pessoa);

        Intent i = getIntent();

        // Buscando alguma entidade que ja foi utilizada (pessoaEditar).
        // Atribuindo ao objeto de edicao com uma string de identificacao.

        _pessoaEditar = (PessoaModel) i.getSerializableExtra("pessoa-enviada");

        _edtNome = (EditText) findViewById(R.id.edt_nome);
        _edtDataNascimento = (EditText) findViewById(R.id.edt_dataNascimento);
        _edtAltura = (EditText) findViewById(R.id.edt_altura);
        _edtPeso = (EditText) findViewById(R.id.edt_peso);
        _edtImc = (EditText) findViewById(R.id.edt_imc);
        _txtInputLayoutNome = (TextInputLayout) findViewById(R.id.txt_layout_nome);
        _txtInputLayoutDataNascimento = (TextInputLayout) findViewById(R.id.txt_layout_dataNascimento);
        _txtInputLayoutAltura = (TextInputLayout) findViewById(R.id.txt_layout_altura);
        _txtInputLayoutPeso = (TextInputLayout) findViewById(R.id.txt_layout_peso);
        _imgButtonDataNascimento = (ImageButton) findViewById(R.id.img_button_data_nascimento);
        _btnImc = (Button) findViewById(R.id.btn_imc);
        _btnSalvarAlterar = (Button) findViewById(R.id.btn_salvar_alterar);
        _btnLimpar = (Button) findViewById(R.id.btn_limpar_campos);

        verificarOperacao();

        _btnImc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_edtAltura.getText().toString().isEmpty() && !_edtPeso.getText().toString().isEmpty()) {
                    pessoa.setAltura(_edtAltura.getText().toString());
                    pessoa.setPeso(_edtPeso.getText().toString());
                    _edtImc.setText(calcularImc(pessoa));
                } else if (_edtAltura.getText().toString().isEmpty()) {
                    _txtInputLayoutAltura.setError("O campo altura é obrigatório.");
                    _edtAltura.requestFocus();
                } else if (!_edtAltura.getText().toString().isEmpty()) {
                    desativarAlertaErroCampoAltura();
                }

                if (_edtPeso.getText().toString().isEmpty() && !_edtAltura.getText().toString().isEmpty()) {
                    _txtInputLayoutPeso.setError("O campo peso é obrigatório.");
                    _edtPeso.requestFocus();
                } else if (!_edtPeso.getText().toString().isEmpty()) {
                    desativarAlertaErroCampoPeso();
                }
            }
        });

        _imgButtonDataNascimento.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TelaCadastrarPessoaController.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        _DateSetListner, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        _DateSetListner = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;

                _edtDataNascimento.setText(date);
                _edtDataNascimento.setEnabled(false);
                _isVerificarDatePicker = true;
                desativarAlertaErroCampoDataNascimento();

                if (_edtNome.getText().toString().isEmpty())
                    _edtNome.requestFocus();

                else if (_edtAltura.getText().toString().isEmpty())
                    _edtAltura.requestFocus();

                else if (_edtPeso.getText().toString().isEmpty()) {
                    _edtPeso.requestFocus();
                }

            }
        }

        ;

        _btnSalvarAlterar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                if (isVerificarTodosOsCampos()) {
                    pessoa.setNome(_edtNome.getText().toString());
                    pessoa.setDataNascimento(_edtDataNascimento.getText().toString());
                    pessoa.setAltura(_edtAltura.getText().toString());
                    pessoa.setPeso(_edtPeso.getText().toString());
                    calcularImc(pessoa);
                    pessoa.setImc(_edtImc.getText().toString());
                    mostrarDiagnostico();

                    // Verificando se é novo contato ou atualização de contato.
                    if (_pessoaEditar != null) { //atualizar contato
                        _isVerificarDatePicker = true;
                        mostrarDiagnostico();
                        _retorno = pessoaDao.atualizarCadastro(pessoa);
                        if (_retorno == -1) {
                            alerta("Erro ao interagir com o banco de dados.");
                        } else {
                            alerta("Dados alterados com sucesso.");
                        }
                    } else { // Salvar novo contato.
                        // Chamando a funcao que realiza o cadastro.
                        _retorno = pessoaDao.salvarCadastro(pessoa);
                        if (_retorno == -1) {
                            alerta("Erro ao interagir com o banco de dados.");
                        } else {
                            alerta("Dados cadastrados com sucesso.");
                        }
                    }
                    // Analisando o resultado do insert ou do update.

                    pessoaDao.close(); // Fechando a conexão com o banco de dados.
                    // Matando a activy atual.
                    // finish();
                }
            }
        });

        _btnLimpar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                limpar();
            }
        });
    }

    private void verificarOperacao() {
        // Verificando se o objeto pessoaEditar possui valores, se sim, é edição, se nao, é novo contato.
        if (_pessoaEditar != null) { // É edição.
            _edtDataNascimento.setEnabled(false);
            _btnSalvarAlterar.setText("Editar");

            // Preenchendo os dados do formulario com os registros oriundos do item clicado na listagem.
            _edtNome.setText(_pessoaEditar.getNome());
            _edtDataNascimento.setText(_pessoaEditar.getDataNascimento());
            _edtAltura.setText(_pessoaEditar.getAltura());
            _edtPeso.setText(_pessoaEditar.getPeso());
            _edtImc.setText(_pessoaEditar.getImc());
            mostrarDiagnostico();
            pessoa.setId(_pessoaEditar.getId()); //passando o ID já existente

        } else { // È novo registro.
            _btnSalvarAlterar.setText("Salvar");
        }
    }

    private String calcularImc(PessoaModel pessoa) {
        DecimalFormat formato = new DecimalFormat("#.##");
        String x = formato.format(pessoa.calcularImc(pessoa));
        _edtImc.setText(x.replace(",", "."));
        return x.replace(",", ".");
    }

    private void alerta(String mensagem) {
        // Funcao pra exibir mensagem básica ao usuario.
        Toast.makeText(TelaCadastrarPessoaController.this, mensagem, Toast.LENGTH_LONG).show();
    }

    private void limpar() {
        _edtNome.requestFocus();
        _edtNome.setText(null);
        _edtDataNascimento.setText(null);
        _edtAltura.setText(null);
        _edtPeso.setText(null);
        _edtImc.setText(null);
        _edtDataNascimento.setEnabled(true);
        _pessoaEditar = null;
        _isVerificarDatePicker = false;
        _txtInputLayoutNome.setErrorEnabled(false);
        _txtInputLayoutDataNascimento.setErrorEnabled(false);
        _txtInputLayoutAltura.setErrorEnabled(false);
        _txtInputLayoutPeso.setErrorEnabled(false);
        // _txtV_Diagnostico.setText(null);

        verificarOperacao();
        desativarAlertaErroCampoNome();
        desativarAlertaErroCampoDataNascimento();
        desativarAlertaErroCampoAltura();
        desativarAlertaErroCampoPeso();
    }

    private boolean isValidarCampoNome() {
        if (_edtNome.getText().toString().trim().isEmpty()) {
            _txtInputLayoutNome.setError("O campo nome é obrigatório.");
            _edtNome.requestFocus();
            return false;
        } else {
            desativarAlertaErroCampoNome();
            return true;
        }
    }

    private boolean isValidarCampoDataNascimento() {
        if (!_edtDataNascimento.getText().toString().isEmpty()) {
            if (isDataNascimento() || _pessoaEditar != null) {
                desativarAlertaErroCampoDataNascimento();
                return true;
            } else {
                _txtInputLayoutDataNascimento.setError("Toque no calendário e selecione sua data de nascimento.");
                _edtDataNascimento.requestFocus();
                return false;
            }

        } else {
            _txtInputLayoutDataNascimento.setError("Toque no calendário e selecione sua data de nascimento.");
            _edtDataNascimento.requestFocus();
            return false;
        }
    }

    private boolean isValidarCampoPeso() {
        if (_edtPeso.getText().toString().isEmpty()) {
            _txtInputLayoutPeso.setError("O campo peso é obrigatório.");
            _edtPeso.requestFocus();
            return false;
        } else {
            desativarAlertaErroCampoPeso();
            return true;
        }
    }

    private boolean isValidarCampoAltura() {
        if (_edtAltura.getText().toString().isEmpty()) {
            _txtInputLayoutAltura.setError("O campo altura é obrigatório.");
            _edtAltura.requestFocus();
            return false;
        } else {
            desativarAlertaErroCampoAltura();
            return true;
        }
    }

    private boolean isDataNascimento() {
        if (_isVerificarDatePicker && !_edtDataNascimento.getText().toString().isEmpty() && anoDataNascimento() <= anoAtual()) {
            return true;
        }
        return false;
    }

    private void desativarAlertaErroCampoNome() {
        _txtInputLayoutNome.setErrorEnabled(false);
    }

    private void desativarAlertaErroCampoDataNascimento() {
        _txtInputLayoutDataNascimento.setErrorEnabled(false);

    }

    private void desativarAlertaErroCampoAltura() {
        _txtInputLayoutAltura.setErrorEnabled(false);
    }

    private void desativarAlertaErroCampoPeso() {
        _txtInputLayoutPeso.setErrorEnabled(false);
    }

    private Integer anoDataNascimento() {
        String dataCadastrada = new String(_edtDataNascimento.getText().toString().replace("/", ""));
        Integer anoCadastrado = Integer.valueOf(dataCadastrada.substring(dataCadastrada.length() - 4));
        return anoCadastrado;
    }

    private Integer anoAtual() {
        Calendar cal = Calendar.getInstance();
        Integer anoAtual = cal.get(Calendar.YEAR);
        return anoAtual;
    }

    private boolean isVerificarTodosOsCampos() {
        if (isValidarCampoNome()
                && isValidarCampoDataNascimento()
                && isValidarCampoAltura()
                && isValidarCampoPeso()) {
            return true;
        } else {
            return false;
        }
    }

    private void mostrarDiagnostico() {
        if (!_edtImc.toString().isEmpty()) {
            Double imc = Double.valueOf(_edtImc.getText().toString());
            if (imc < 18.5) {
                //_txtV_Diagnostico.setText("ABAIXO DO PESO");
            } else if (imc >= 18.5 && imc <= 24.9) {
                // _txtV_Diagnostico.setText("SAUDÁVEL");
            } else if (imc >= 25 && imc <= 29.9) {
                // _txtV_Diagnostico.setText("EXCESSO DE PESO");
            } else if (imc >= 30) {
                // _txtV_Diagnostico.setText("OBESIDADE");
            }
        }
    }
}

