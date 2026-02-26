import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame implements ActionListener {
    private JTextField tela;
    private JButton[] botoesNumeros = new JButton[10];
    private JButton btnSoma, btnSub, btnMult, btnDiv, btnIgual, btnLimpar, btnApagar, btnAbrePar, btnFechaPar, btnPonto;
    private String expressao = "";

    public App() {
        // Configurações Básicas da Janela
        setTitle("Calculadora Nexo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 600);
        setLayout(new BorderLayout(10, 10));

        // --- VISOR ---
        tela = new JTextField();
        tela.setEditable(false);
        tela.setHorizontalAlignment(JTextField.RIGHT);
        tela.setFont(new Font("Monospaced", Font.BOLD, 35));
        tela.setPreferredSize(new Dimension(350, 90));
        tela.setBackground(new Color(240, 240, 240));
        add(tela, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (Grids de Botões) ---
        JPanel painelCorpo = new JPanel(new GridLayout(5, 4, 5, 5));

        // Botões de Operação e Controle
        btnLimpar = new JButton("C");
        btnApagar = new JButton("<-");
        btnAbrePar = new JButton("(");
        btnFechaPar = new JButton(")");
        btnDiv = new JButton("/");
        btnMult = new JButton("*");
        btnSub = new JButton("-");
        btnSoma = new JButton("+");
        btnPonto = new JButton(".");

        // Criando e estilizando botões numéricos
        for (int i = 0; i < 10; i++) {
            botoesNumeros[i] = new JButton(String.valueOf(i));
            botoesNumeros[i].addActionListener(this);
            botoesNumeros[i].setBackground(Color.WHITE);
            botoesNumeros[i].setFont(new Font("Arial", Font.PLAIN, 20));
        }

        // Organizando a Grade (Linhas 1 a 5)
        painelCorpo.add(btnLimpar); painelCorpo.add(btnApagar); painelCorpo.add(btnAbrePar); painelCorpo.add(btnFechaPar);
        painelCorpo.add(botoesNumeros[7]); painelCorpo.add(botoesNumeros[8]); painelCorpo.add(botoesNumeros[9]); painelCorpo.add(btnDiv);
        painelCorpo.add(botoesNumeros[4]); painelCorpo.add(botoesNumeros[5]); painelCorpo.add(botoesNumeros[6]); painelCorpo.add(btnMult);
        painelCorpo.add(botoesNumeros[1]); painelCorpo.add(botoesNumeros[2]); painelCorpo.add(botoesNumeros[3]); painelCorpo.add(btnSub);
        
        // Penúltima linha: Espaço vazio, Zero, Ponto e Soma
        painelCorpo.add(new JButton("")); // Espaço para manter o alinhamento
        painelCorpo.add(botoesNumeros[0]); 
        painelCorpo.add(btnPonto); 
        painelCorpo.add(btnSoma);

        // --- BOTÃO IGUAL (Base) ---
        btnIgual = new JButton("=");
        btnIgual.setFont(new Font("Arial", Font.BOLD, 30));
        btnIgual.setPreferredSize(new Dimension(350, 70)); 
        btnIgual.setBackground(new Color(255, 140, 0)); // Laranja Nexo
        btnIgual.setForeground(Color.WHITE);
        btnIgual.addActionListener(this);

        // Container para juntar o Grid e o Botão Igual
        JPanel containerCentral = new JPanel(new BorderLayout(5, 5));
        containerCentral.add(painelCorpo, BorderLayout.CENTER);
        containerCentral.add(btnIgual, BorderLayout.SOUTH);

        add(containerCentral, BorderLayout.CENTER);

        // Adicionando Listeners aos outros botões
        JButton[] ops = {btnLimpar, btnApagar, btnSoma, btnSub, btnMult, btnDiv, btnAbrePar, btnFechaPar, btnPonto};
        for (JButton b : ops) {
            b.addActionListener(this);
            b.setFont(new Font("Arial", Font.BOLD, 18));
        }

        setLocationRelativeTo(null); // Centraliza na tela do PC
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if ("0123456789.".contains(comando)) {
            expressao += comando;
        } else if (comando.equals("(")) {
            expressao += "(";
        } else if (comando.equals(")")) {
            expressao += ")";
        } else if (comando.equals("C")) {
            expressao = "";
        } else if (comando.equals("<-")) {
            if (!expressao.isEmpty()) {
                // Se o último for um operador com espaços, remove os espaços também
                if (expressao.endsWith(" ")) expressao = expressao.substring(0, expressao.length() - 3);
                else expressao = expressao.substring(0, expressao.length() - 1);
            }
        } else if (comando.equals("=")) {
            calcularExpressaoComplexa();
            return; 
        } else {
            // Operadores (+, -, *, /)
            if (!expressao.isEmpty() && !expressao.endsWith(" ")) {
                expressao += " " + comando + " ";
            }
        }
        tela.setText(expressao);
    }

    private void calcularExpressaoComplexa() {
        try {
            double resultado = avaliar(expressao);
            // Formatação para evitar o ".0" em números inteiros
            expressao = (resultado % 1 == 0) ? String.valueOf((int)resultado) : String.valueOf(resultado);
            tela.setText(expressao);
        } catch (Exception ex) {
            tela.setText("Erro");
            expressao = "";
        }
    }

    // Algoritmo para processar a String matemática respeitando parênteses e precedência
    private double avaliar(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() { nextChar(); double x = parseExpression(); return x; }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else throw new RuntimeException("Inesperado");
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}