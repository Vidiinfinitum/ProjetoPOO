# Sistema de Biblioteca

Projeto acadêmico completo para cadastro de livros físicos, livros digitais, revistas e usuários, com empréstimos, devoluções, multas, pesquisas e persistência em arquivo. Toda a interação ocorre por caixas de diálogo de `JOptionPane`.

Requer **JDK 17 ou superior** e ambiente gráfico. Usa exclusivamente a biblioteca padrão do Java, sem Maven, Gradle ou dependências externas. O código possui exatamente **20 arquivos Java em dois pacotes**, `modelo` e `sistema`. Os identificadores próprios estão em português sem acentos; textos e comentários usam acentuação.

## Compilar e executar

Abra um terminal na raiz do projeto, com `java` e `javac` do JDK no `PATH`:

```text
javac -encoding UTF-8 -Xlint:all -d out src/modelo/*.java src/sistema/*.java
java -cp out sistema.Main
```

O comando pedido no enunciado também compila o projeto em ambientes com codificação padrão UTF-8:

```text
javac -Xlint:all -d out src/modelo/*.java src/sistema/*.java
```

Para verificar explicitamente a compatibilidade com Java 17, mesmo compilando com um JDK mais novo:

```text
javac -encoding UTF-8 -Xlint:all --release 17 -d out src/modelo/*.java src/sistema/*.java
java -cp out sistema.Main
```

Neste computador o JDK foi encontrado fora do `PATH`, em `C:\Program Files\Java\jdk-27.0.1.0.3`. No PowerShell, é possível ajustar somente a sessão atual antes de executar os comandos acima:

```powershell
$env:Path = 'C:\Program Files\Java\jdk-27.0.1.0.3\bin;' + $env:Path
```

Se o JDK estiver em outro local, substitua esse caminho pelo diretório `bin` correspondente. Salve os fontes em UTF-8 e execute os comandos sempre na raiz para usar o mesmo arquivo de dados.

## Estrutura

```text
README.md
src/
  modelo/
    StatusItem.java
    Genero.java
    Exportavel.java
    Validavel.java
    EmprestimoNegadoException.java
    Pessoa.java
    Usuario.java
    Bibliotecario.java
    Carteirinha.java
    Item.java
    Livro.java
    LivroDigital.java
    Revista.java
    Emprestimo.java
  sistema/
    Biblioteca.java
    Utilitarios.java
    ArquivoBiblioteca.java
    VitrineDigital.java
    MenuPrincipal.java
    Main.java
```

`out/` contém apenas os arquivos compilados, gerados pelo `javac`. `dados_biblioteca.txt` é criado ao salvar, na pasta em que o programa foi executado. Não existem fontes Java no pacote padrão, terceiros pacotes nem `package-info.java`.

## Primeira execução

Quando o arquivo ainda não existe, `Main.main` cadastra os três materiais com uma única chamada a `cadastrarItens(...)`:

| Código | Material | Dados para experimentar |
| --- | --- | --- |
| 1 | Livro físico: Dom Casmurro | Multa de R$ 1,50 por dia, limitada a R$ 50,00 |
| 2 | Livro digital: Introdução à programação Java | Multa sempre igual a R$ 0,00 |
| 3 | Revista: Ciência em Foco | Multa de R$ 0,75 por dia |

Usuária inicial: **Maria Souza**, CPF **12345678900**, carteirinha criada com validade de um ano. Responsável fixo: **Ana Martins**, matrícula **BIB-001**, turno **Diurno**. Os limites são três para usuários e dez para bibliotecários. O responsável é consultado na opção 12; os registros de empréstimo utilizam usuários cadastrados.

O destaque da vitrine é um e-book fixo com código 9000, próprio da apresentação da opção 11. O acervo usado para empréstimos é administrado pela biblioteca.

## Opções do menu

| Opção | Operação |
| --- | --- |
| 1 | Cadastrar livro; confirmar se é digital e, nesse caso, informar tamanho em MB e formato |
| 2 | Cadastrar revista |
| 3 | Cadastrar usuário com carteirinha anual |
| 4 | Registrar empréstimo por CPF e código do item |
| 5 | Registrar devolução; informar data `dd/MM/yyyy` ou deixar vazio para hoje |
| 6 | Buscar parte do título, ignorando maiúsculas e minúsculas |
| 7 | Buscar pelo ano de publicação |
| 8 | Listar acervo |
| 9 | Listar usuários e empréstimos ativos |
| 10 | Simular multa de livro físico ou digital, por código e dias de atraso |
| 11 | Ver destaque digital usando a ficha herdada de Livro |
| 12 | Informar CPF e consultar os limites do usuário e do bibliotecário responsável |
| 13 | Salvar sem encerrar |
| 0 | Escolher entre salvar e sair, sair sem salvar ou cancelar a saída |

Cancelar um campo abandona a operação corrente sem cadastro parcial. Escolher 0 ou cancelar o menu abre a pergunta sobre salvar antes de sair: **Sim** salva e encerra; **Não** encerra sem gravar as alterações pendentes; **Cancelar** ou fechar essa pergunta retorna ao menu. O Main respeita essa escolha e não faz uma gravação automática após o encerramento do menu. Entradas numéricas inválidas e datas inexistentes recebem mensagens amigáveis. Valores decimais aceitam ponto ou vírgula. Campos de texto não aceitam ponto e vírgula nem quebras de linha, pois esses caracteres delimitam os registros do arquivo.

Para experimentar multas, registre um empréstimo e informe na opção 5 uma data posterior à previsão apresentada, que corresponde a hoje mais 14 dias. Três dias depois da previsão produzem R$ 4,50 no livro físico inicial, R$ 2,25 na revista e R$ 0,00 no e-book. Também é possível simular livros diretamente pela opção 10, sem emprestá-los.

## Persistência

`ArquivoBiblioteca` grava texto UTF-8, uma linha por objeto, utilizando as implementações de `Exportavel.paraLinhaArquivo()`. Os valores decimais no arquivo usam ponto; as datas usam ISO (`yyyy-MM-dd`). A apresentação ao usuário usa a moeda brasileira, e os diálogos de data usam `dd/MM/yyyy`.

```text
LIVRO;codigo;titulo;ano;isbn;paginas;valorReposicao;genero;status
LIVRODIGITAL;codigo;titulo;ano;isbn;paginas;valorReposicao;genero;status;tamanhoMb;formato
REVISTA;codigo;titulo;ano;numeroEdicao;periodicidade;status
USUARIO;nome;cpf;validadeISO
EMPRESTIMO;cpf;codigoItem;dataEmprestimoISO;dataPrevistaISO
```

Na abertura, o sistema recupera os valores originais dos materiais, a validade das carteirinhas, os estados dos itens e as datas dos empréstimos. Os vínculos são resolvidos por CPF e código usando os mesmos objetos cadastrados. Os contadores de empréstimos ativos são reconstruídos a partir desses vínculos. A leitura aceita registros de empréstimo antes dos participantes porque os resolve após ler os cadastros.

Antes de aplicar os registros à biblioteca utilizada pelo menu, a carga verifica os dados em uma biblioteca temporária. Registros inválidos, códigos/CPFs duplicados ou inconsistências entre status e empréstimos provocam `IOException`. Na inicialização, a falha é exibida e o programa encerra sem sobrescrever o arquivo original. A gravação usa um arquivo temporário e tenta substituição atômica; se o sistema de arquivos não oferecer essa operação, faz a substituição normal. Falhas ao salvar pelo menu mantêm a aplicação aberta.

## Auto-verificação: Requisito -> classe/arquivo -> justificativa

Todos os pontos abaixo possuem comentários com número, nome e justificativa nos locais de declaração e de uso pertinentes. Cada caminho identifica também a classe de mesmo nome.

| Requisito | Classe/arquivo | Justificativa e evidência | Resultado |
| --- | --- | --- | --- |
| 1.1 — Encapsulamento | Todas as classes com atributos em `src/modelo/` e `src/sistema/` | Todos os atributos de instância são `private`; há getters/setters quando necessários. Biblioteca devolve cópias das listas. A constante pública é `static final`. | OK |
| 1.2 — Atributos String/int/float | `modelo/Livro.java` — Livro | `isbn` é String, `numeroPaginas` é int e `valorReposicao` é float; todos são atributos privados de cada objeto, sem `static`. | OK |
| 1.3 — Enumeração | `modelo/StatusItem.java`, `modelo/Genero.java`, `modelo/Item.java`, `modelo/Livro.java`; usos em `sistema/Biblioteca.java`, `ArquivoBiblioteca.java`, `MenuPrincipal.java`, `VitrineDigital.java`, `Main.java` | StatusItem limita e controla a disponibilidade; Genero classifica livros. A gravação e a carga preservam ambos. | OK |
| 1.4 — ArrayList | `sistema/Biblioteca.java`; usos em `sistema/Utilitarios.java` e `ArquivoBiblioteca.java` | Listas dinâmicas mantêm itens, usuários e empréstimos e fornecem os registros para relatórios e persistência. | OK |
| 1.5 — Constante | `modelo/Emprestimo.java`; leitura em `sistema/Biblioteca.java`, `registrarEmprestimo` | `public static final int PRAZO_PADRAO_DIAS = 14` é usada em outra classe para calcular a previsão com `LocalDate.now().plusDays(...)`. | OK |
| 2.1 — Método com argumentos variados (varargs) | `sistema/Biblioteca.java`, `cadastrarItens(Item... itens)`; chamadas em `Main.java`, `MenuPrincipal.java`, `ArquivoBiblioteca.java` | Um FOR percorre os argumentos, ignora códigos existentes e retorna a quantidade adicionada. A carga inicial usa três materiais em uma única chamada; o menu usa um por chamada. | OK |
| 2.2 — Método estático | `sistema/Utilitarios.java`; usos em `Biblioteca.java` e `MenuPrincipal.java` | Atraso, moeda e apresentação de listas são serviços sem estado, reutilizados na devolução, simulação e relatórios. | OK |
| 3 — Construtor | Todas as classes concretas de `modelo` e `sistema`; instanciações em `Main.java`, `MenuPrincipal.java`, `Usuario.java`, `Biblioteca.java`, `ArquivoBiblioteca.java` e `VitrineDigital.java` | Construtores explícitos preparam as entidades e os serviços. Usuario possui duas versões efetivamente utilizadas. Utilitarios possui construtor privado para impedir instâncias, pois oferece somente métodos estáticos. As interfaces não têm construtores. | OK |
| 4 — Associação por Composição | `modelo/Usuario.java` e `modelo/Carteirinha.java`; usos de Usuario em `sistema/Main.java`, `MenuPrincipal.java`, `ArquivoBiblioteca.java` | Ambos os construtores de Usuario executam `new Carteirinha("CART-" + cpf, validade)`. Não há criação de Carteirinha fora de Usuario nem setter para substituir essa parte. A carga recupera a validade original. | OK |
| 5 — Associação por Agregação | `modelo/Emprestimo.java`; `sistema/Biblioteca.java`; montagem em `Main.java` e restauração em `ArquivoBiblioteca.java` | Emprestimo recebe Usuario e Item independentes. Biblioteca recebe o bibliotecário pronto e associa itens/usuários pelos cadastros. Os atributos dessas associações pertencem a classes concretas. | OK |
| 6.1 — Atributo final em superclasse | `modelo/Livro.java` e sua filha `modelo/LivroDigital.java` | O pai concreto declara `private final String isbn`, atribuído no construtor. É um atributo de instância diferente da constante do prazo. | OK |
| 6.2 — Acoplamento dinâmico | `modelo/Livro.java`, `modelo/LivroDigital.java`, `sistema/VitrineDigital.java`; opção 11 em `MenuPrincipal.java` | Livro declara `montarFicha()`. LivroDigital herda sem declarar/sobrescrever esse método nem chamar `super.montarFicha()`. VitrineDigital declara `LivroDigital destaque`, instancia a filha no construtor e executa `destaque.montarFicha()`. | OK |
| 7.1 — Polimorfismo de sobrecarga | `sistema/Biblioteca.java`, `buscar(String)` e `buscar(int)`; opções 6 e 7 em `MenuPrincipal.java`; também construtores de `modelo/Usuario.java` | Os parâmetros selecionam buscas textuais ou por ano com o mesmo nome. Ambas são usadas pelo menu. Os construtores de Usuario distinguem cadastro novo e restauração com validade. | OK |
| 7.2 — Polimorfismo de coerção | `sistema/Utilitarios.java`, `calcularDiasAtraso`; uso na devolução em `Biblioteca.java` | A instrução literal `int dias = (int) ChronoUnit.DAYS.between(prevista, devolucao);` converte o long em int. `Math.max(dias, 0)` evita multa antes do vencimento. | OK |
| 7.3 — Polimorfismo de subtipagem | `modelo/Livro.java`, `modelo/LivroDigital.java`; `sistema/Biblioteca.java`, `simularMultaLivro`; opção 10 em `MenuPrincipal.java` | O parâmetro concreto Livro recebe Livro ou LivroDigital. A chamada `livro.calcularMulta(dias)` escolhe dinamicamente a multa física ou zero na filha concreta. | OK |
| 7.4 — Polimorfismo paramétrico | `sistema/Utilitarios.java`, `formatarLista`; relatório de `Biblioteca.java` e buscas/listagens de `MenuPrincipal.java` | O método estático `<T>` formata elementos via `toString`. Todas as chamadas informam explicitamente `<Item>`, `<Usuario>` ou `<Emprestimo>`, fora de construtores e de classes abstratas. | OK |
| 8 — Interface | `modelo/Exportavel.java`, `Validavel.java`, `Livro.java`, `Revista.java`, `Usuario.java`, `Emprestimo.java`, `Carteirinha.java`; usos em `sistema/Biblioteca.java` e `ArquivoBiblioteca.java` | Exportavel atende à persistência e Validavel controla a autorização de empréstimo. Usuario implementa as duas interfaces. Os contratos não têm construtores nem get/set. | OK |
| 9 — Pacotes | Todos os 20 arquivos em `src/modelo/` e `src/sistema/` | Existem somente esses dois pacotes. Sistema importa modelo; modelo nunca importa sistema. Não há classe no pacote padrão. | OK |
| 10 — Classe abstrata | `modelo/Pessoa.java`, `modelo/Item.java`; concretas `Usuario.java`, `Bibliotecario.java`, `Livro.java`, `LivroDigital.java`, `Revista.java` | Pessoa exige `calcularLimiteEmprestimos`; Item exige `calcularMulta`. As concretas fornecem os comportamentos e são utilizadas na carga, no menu, na vitrine e no serviço. | OK |
| 11 — Exceção | `modelo/EmprestimoNegadoException.java`; lançamentos em `sistema/Biblioteca.java`; tratamento na opção 4 de `MenuPrincipal.java`; I/O em `ArquivoBiblioteca.java` e `Main.java` | Exceção checada possui mensagem específica para usuário ausente, item ausente, indisponibilidade, carteirinha vencida e limite atingido. O menu a captura em diálogo de erro, além de tratar números/datas inválidos e IOException ao salvar. Main trata falha de carga. | OK |
| 12 — Entrada de dados por caixas de diálogo | `sistema/MenuPrincipal.java`; mensagens iniciais em `Main.java` | As 14 opções, campos, confirmações e resultados utilizam somente JOptionPane. Não há JFrame, JPanel nem janela construída manualmente. Cancelamento é tratado antes de interpretar os campos; a saída permite salvar, descartar alterações pendentes ou retornar ao menu. | OK |
| 13 — Arquivo | `sistema/ArquivoBiblioteca.java`; inicialização em `Main.java`, gravação em `MenuPrincipal.java` | Salva e reconstrói Livro, LivroDigital, Revista, Usuario e Emprestimo, usando os construtores e os serviços de cadastro/restauração. As entidades recuperadas são as que o menu consulta e modifica. | OK |

## Validação realizada

- Compilação com o comando literal `javac -Xlint:all -d out src/modelo/*.java src/sistema/*.java`: **sem erros nem avisos**.
- Compilação com `-encoding UTF-8 -Xlint:all --release 17`: **sem erros nem avisos**.
- **65 verificações funcionais automatizadas** executadas com JShell do JDK, sem bibliotecas externas e sem adicionar classes ao projeto. Cobrem cadastro múltiplo e duplicado, composição e validade, limites, buscas, atraso, teto de multa, multas digitais, ficha herdada, os cinco motivos de recusa, devoluções, cópias de listas, gravação e recuperação integral dos valores, identidade dos participantes restaurados, contadores, leitura fora de ordem e rejeição de arquivos inconsistentes sem carga parcial.
- Auto-verificação estrutural dos 22 pontos do checklist, incluindo os dois pacotes, os 20 arquivos, visibilidade dos atributos e chamadas genéricas explícitas.
- Os caminhos de interação e cancelamento de JOptionPane foram revisados no código. A validação funcional automatizada exercitou o domínio e a persistência; a navegação visual dos diálogos deve ser executada em um ambiente gráfico com o comando indicado acima.

Os arquivos usados nos testes ficaram em uma pasta temporária e foram removidos, sem criar dados demonstrativos persistidos na raiz do projeto.
