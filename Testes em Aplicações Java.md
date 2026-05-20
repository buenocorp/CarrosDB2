# Testes em Aplicações Java Desktop

- Teste funcional
- Teste unitário
- Teste de banco de dados (com Mockito)
- Teste automatizado de interface gráfica

---

## O que são testes de software?

Os testes de software são processos utilizados para verificar se o sistema está funcionando corretamente.

**Objetivos:**

- Encontrar erros antes do usuário
- Garantir qualidade
- Validar funcionalidades
- Evitar falhas futuras
- Melhorar manutenção do sistema

**Ferramentas utilizadas neste projeto:**

- Eclipse IDE
- JUnit 5 (Jupiter)
- Mockito
- Swing / SwingUtilities

---

## Teste Funcional

### O que é?

Verifica se as funcionalidades do sistema estão funcionando conforme esperado, do ponto de vista do usuário.

### O que pode ser testado?

- Cadastro de registros
- Botões e menus
- Cálculos e validações
- Navegação entre telas

### Exemplo no projeto CarrosDB

**Fluxo de cadastro de marca:**

1. Usuário abre a tela de Cadastro de Marca
2. Preenche os campos Nome e País
3. Clica em "Salvar"
4. Sistema exibe mensagem de confirmação
5. Marca aparece na tabela

**Resultado esperado:** a nova marca deve aparecer listada na tabela da tela.

### Exemplo prático — verificar que salvar uma marca chama o DAO

O teste `testeSalvarMarca()` em [MarcaControllerTest.java](src/test/controller/MarcaControllerTest.java) verifica que o controller delega o salvamento ao DAO:

```java
@Test
public void testeSalvarMarca() {
    controller.salvarMarca("Toyota", "Japão");

    verify(marcaDAO, times(1))
        .inserir(any(Marca.class));
}
```

**O que está sendo testado:**
- Regra de negócio: ao salvar, o controller chama o DAO exatamente 1 vez
- Comportamento do método `salvarMarca()`
- Que o DAO recebe um objeto `Marca` com os dados corretos

---

## Teste Unitário

### O que é?

Testa pequenas partes do sistema separadamente — normalmente métodos e classes individualmente.

### Objetivo

Garantir que cada função funcione corretamente de forma isolada, sem depender de banco de dados ou interface gráfica.

### Ferramenta utilizada

- **JUnit 5** com a anotação `@Test`
- **Mockito** para simular dependências (`mock`, `when`, `verify`)

### Vantagens

- Detecta erros rapidamente
- Facilita manutenção
- Ajuda na refatoração

---

### Entendendo o Mockito: `mock`, `when` e `verify`

O **Mockito** é uma biblioteca que cria objetos falsos (mocks) para substituir dependências reais durante os testes. Em vez de conectar ao banco de dados ou abrir uma tela, o mock finge ser o objeto e responde como configurado.

#### `mock` — criar um objeto falso

```java
MarcaDAO marcaDAO = mock(MarcaDAO.class);
```

Cria um objeto que se parece com `MarcaDAO`, mas **não executa nada de verdade**. Todos os métodos retornam valores padrão (`null`, `0`, lista vazia) até serem configurados com `when`.

No projeto, isso permite testar o `MarcaController` sem precisar de banco de dados:

```java
// setup do MarcaControllerTest
marcaDAO   = mock(MarcaDAO.class);          // DAO falso
controller = new MarcaController(marcaDAO); // controller real com DAO falso
```

---

#### `when` — definir o que o mock deve retornar

```java
when(marcaDAO.buscarPorId(1)).thenReturn(marca);
```

Configura o mock para que, **quando** `buscarPorId(1)` for chamado, ele **retorne** o objeto `marca`. Sem isso, o mock retornaria `null`.

Exemplo real do [MarcaDAOTest.java](src/test/dao/MarcaDAOTest.java):

```java
// simula o ResultSet retornando uma linha com dados da Ford
when(resultSet.next()).thenReturn(true, false); // true na 1ª chamada, false na 2ª
when(resultSet.getString("nome")).thenReturn("Ford");
when(resultSet.getString("pais")).thenReturn("EUA");
```

O `true, false` no `next()` simula um `ResultSet` com exatamente uma linha: o loop do DAO entra uma vez e sai na segunda iteração.

---

#### `verify` — confirmar que o método foi chamado

```java
verify(marcaDAO, times(1)).inserir(any(Marca.class));
```

Verifica se o método `inserir()` foi chamado **exatamente 1 vez** com qualquer objeto `Marca`. Se não foi chamado, o teste falha.

Variações usadas no projeto:

```java
verify(marcaDAO, times(1)).excluir(1);               // chamado 1 vez com o id 1
verify(marcaDAO, times(1)).inserir(any(Marca.class)); // chamado com qualquer Marca
verify(preparedStatement).setString(1, "Toyota");     // parâmetro SQL verificado
verify(preparedStatement).executeUpdate();             // query foi executada
```

> **Resumo:** `mock` cria o objeto falso, `when` ensina o que ele deve responder, `verify` confere se foi usado corretamente.

---

### Exemplo 1 — Testando o Controller (sem banco de dados)

O [MarcaControllerTest.java](src/test/controller/MarcaControllerTest.java) usa um `mock` do DAO para testar o controller isoladamente:

```java
public class MarcaControllerTest {

    private MarcaDAO marcaDAO;
    private MarcaController controller;

    @BeforeEach
    public void setup() {
        marcaDAO = mock(MarcaDAO.class);        // simula o DAO
        controller = new MarcaController(marcaDAO);
    }

    @Test
    public void testeSalvarMarca() {
        controller.salvarMarca("Toyota", "Japão");

        verify(marcaDAO, times(1))
            .inserir(any(Marca.class));         // verifica que o DAO foi chamado
    }
}
```

**O que está sendo testado:**
- O controller recebe os dados e repassa ao DAO
- O método `inserir()` do DAO é chamado exatamente 1 vez
- Não acessa banco de dados real

---

### Exemplo 2 — Testando a listagem com retorno simulado

```java
@Test
public void testeListarMarcas() {

    List<Marca> listaFake = new ArrayList<>();
    Marca marca = new Marca();
    marca.setId(1);
    marca.setNome("Honda");
    marca.setPais("Japão");
    listaFake.add(marca);

    when(marcaDAO.listar()).thenReturn(listaFake);  // define o retorno do mock

    List<Marca> resultado = controller.listarMarcas();

    assertEquals(1, resultado.size());
    assertEquals("Honda", resultado.get(0).getNome());
}
```

**O que está sendo testado:**
- O controller retorna exatamente o que o DAO fornece
- O tamanho e os dados da lista estão corretos

---

### Exemplo 3 — Testando busca por ID

```java
@Test
public void testeBuscarMarca() {

    Marca marca = new Marca();
    marca.setId(1);
    marca.setNome("BMW");
    marca.setPais("Alemanha");

    when(marcaDAO.buscarPorId(1)).thenReturn(marca);

    Marca resultado = controller.buscarMarca(1);

    assertNotNull(resultado);
    assertEquals("BMW", resultado.getNome());
}
```

**O que está sendo testado:**
- O controller delega a busca ao DAO corretamente
- O objeto retornado não é nulo
- Os dados batem com o que foi configurado no mock

---

## Teste de Banco de Dados (com Mockito)

### O que é?

Verifica se a camada de acesso ao banco (DAO) executa as operações SQL corretamente — sem precisar de um banco real, usando `mock` de `Connection` e `PreparedStatement`.

### Tecnologia utilizada

- **JDBC** (via mocks)
- **SQLite** (`jdbc:sqlite:banco.db`) no ambiente real
- **Mockito** para simular a conexão

---

### Exemplo 1 — Testando o INSERT

No [MarcaDAOTest.java](src/test/dao/MarcaDAOTest.java), a conexão JDBC é simulada:

```java
@BeforeEach
public void setup() throws Exception {
    connection        = mock(Connection.class);
    preparedStatement = mock(PreparedStatement.class);
    statement         = mock(Statement.class);
    resultSet         = mock(ResultSet.class);

    marcaDAO = new MarcaDAO(connection);  // injeta a conexão mockada
}

@Test
public void testeInserirMarca() throws Exception {

    when(connection.prepareStatement(anyString()))
        .thenReturn(preparedStatement);

    Marca marca = new Marca();
    marca.setNome("Toyota");
    marca.setPais("Japão");

    marcaDAO.inserir(marca);

    verify(preparedStatement).setString(1, "Toyota");  // 1º parâmetro SQL
    verify(preparedStatement).setString(2, "Japão");   // 2º parâmetro SQL
    verify(preparedStatement).executeUpdate();          // SQL foi executado
}
```

**O que está sendo testado:**
- Os parâmetros corretos são passados ao `PreparedStatement`
- O `executeUpdate()` é chamado (a query é executada)
- Não abre nenhuma conexão real com o banco

---

### Exemplo 2 — Testando o SELECT (listar)

```java
@Test
public void testeListarMarcas() throws Exception {

    when(connection.createStatement()).thenReturn(statement);
    when(statement.executeQuery(anyString())).thenReturn(resultSet);

    // simula duas chamadas ao next(): primeira retorna true, segunda false
    when(resultSet.next()).thenReturn(true, false);

    when(resultSet.getInt("id")).thenReturn(1);
    when(resultSet.getString("nome")).thenReturn("Ford");
    when(resultSet.getString("pais")).thenReturn("EUA");

    List<Marca> lista = marcaDAO.listar();

    assertEquals(1, lista.size());
    assertEquals("Ford", lista.get(0).getNome());
}
```

**O que está sendo testado:**
- O DAO lê os dados do `ResultSet` corretamente
- A lista tem o tamanho certo
- Os valores são mapeados para o objeto `Marca`

---

### Exemplo 3 — Testando o UPDATE

```java
@Test
public void testeAtualizarMarca() throws Exception {

    when(connection.prepareStatement(anyString()))
        .thenReturn(preparedStatement);

    Marca marca = new Marca();
    marca.setNome("Honda");
    marca.setPais("Japão");

    marcaDAO.atualizar(1, marca);

    verify(preparedStatement).setString(1, "Honda");
    verify(preparedStatement).setString(2, "Japão");
    verify(preparedStatement).setInt(3, 1);          // id no WHERE
    verify(preparedStatement).executeUpdate();
}
```

---

### Exemplo 4 — Testando o DELETE

```java
@Test
public void testeExcluirMarca() throws Exception {

    when(connection.prepareStatement(anyString()))
        .thenReturn(preparedStatement);

    marcaDAO.excluir(1);

    verify(preparedStatement).setInt(1, 1);   // id passado ao DELETE
    verify(preparedStatement).executeUpdate();
}
```

---

## Teste de Conexão com o Banco Real

### O que é?

Verifica se a `ConnectionFactory` consegue abrir, isolar e fechar conexões com o banco SQLite de forma correta. Diferente dos testes DAO, **não usa mock** — conecta ao arquivo `banco.db` de verdade.

### Por que não usar mock aqui?

O método `DriverManager.getConnection()` é **estático**. O Mockito padrão não consegue interceptar métodos estáticos — ele só cria subclasses (via ByteBuddy), e métodos estáticos não participam de herança. Para mockar estáticos seria necessário `mockito-inline`, que não está no projeto.

A solução é testar com o banco real: o SQLite é um arquivo local (`banco.db`) que já existe no projeto, então a conexão funciona sem configuração extra.

### Por que essa abordagem é chamada de Teste de Integração?

Porque envolve **duas partes reais juntas**: o código Java e o banco de dados SQLite. Não há nenhuma simulação — a conexão é aberta, usada e fechada de verdade.

---

### O arquivo [ConnectionFactoryTest.java](src/test/database/ConnectionFactoryTest.java)

```java
@Test
public void testeGetConnectionRetornaConexaoAberta() throws Exception {
    Connection conn = ConnectionFactory.getConnection();

    assertNotNull(conn);
    assertFalse(conn.isClosed());

    conn.close();
}
```

**O que está sendo testado:**
- `assertNotNull(conn)` — garante que o banco foi encontrado e a conexão foi criada
- `assertFalse(conn.isClosed())` — garante que a conexão está ativa, não fechada
- `conn.close()` no final — boa prática: libera o recurso após o teste

---

```java
@Test
public void testeGetConnectionRetornaNovaInstanciaACadaChamada() throws Exception {
    Connection conn1 = ConnectionFactory.getConnection();
    Connection conn2 = ConnectionFactory.getConnection();

    assertNotSame(conn1, conn2);

    conn1.close();
    conn2.close();
}
```

**O que está sendo testado:**
- `assertNotSame` verifica que `conn1` e `conn2` são **objetos diferentes na memória** — cada chamada a `getConnection()` cria uma nova conexão independente
- Isso garante que o `ConnectionFactory` não reutiliza (nem vaza) conexões entre chamadas

---

```java
@Test
public void testeFecharConexaoFunciona() throws Exception {
    Connection conn = ConnectionFactory.getConnection();

    conn.close();

    assertTrue(conn.isClosed());
}
```

**O que está sendo testado:**
- Após chamar `close()`, a propriedade `isClosed()` deve ser `true`
- Confirma que o ciclo de vida da conexão funciona corretamente: abrir → fechar → marcada como fechada

---

```java
@Test
public void testeInitNaoLancaExcecao() {
    assertDoesNotThrow(() -> ConnectionFactory.init());
}
```

**O que está sendo testado:**
- `assertDoesNotThrow` garante que `init()` **não lança nenhuma exceção**
- Se o banco não existir ou a URL estiver errada, `init()` imprimiria o erro mas engole a exceção internamente — este teste confirma que o caminho feliz funciona

---

### Assertions usadas neste teste

| Assertion | O que verifica |
|---|---|
| `assertNotNull(obj)` | O objeto não é `null` |
| `assertFalse(cond)` | A condição é `false` |
| `assertTrue(cond)` | A condição é `true` |
| `assertNotSame(a, b)` | `a` e `b` são objetos diferentes (não o mesmo endereço de memória) |
| `assertDoesNotThrow(lambda)` | O código dentro do lambda não lança nenhuma exceção |

---

## Teste Automatizado de Interface Gráfica

### O que é?

Automatiza ações do usuário na interface Swing do sistema, simulando cliques em botões, digitação e navegação entre telas.

### O teste pode:

- Clicar em botões
- Digitar textos campo a campo
- Abrir janelas e diálogos
- Validar estado da tabela
- Simular o fluxo completo de um usuário

### Ferramenta utilizada

- **SwingUtilities.invokeAndWait()** — executa ações na Event Dispatch Thread (EDT), que é a thread responsável pela interface gráfica no Swing

---

### Exemplo 1 — Abrir a tela e garantir que fecha após o teste

O `@BeforeEach` e `@AfterEach` em [DemoTelaHomeTest.java](src/test/view/DemoTelaHomeTest.java) garantem que a tela está pronta antes de cada teste e fechada depois:

```java
@BeforeEach
void setup() throws Exception {
    database.ConnectionFactory.init();

    SwingUtilities.invokeAndWait(() -> {
        telaHome = new TelaHome("Admin");
        telaHome.setVisible(true);
    });
}

@AfterEach
void tearDown() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
        if (telaHome != null) telaHome.dispose();
    });
}
```

**O que está sendo feito:**
- `ConnectionFactory.init()` inicializa a conexão com o banco antes do teste
- `invokeAndWait()` garante que a janela seja criada na EDT antes do teste prosseguir
- `dispose()` fecha a janela após cada teste, evitando que janelas abertas interfiram nos testes seguintes

---

### Exemplo 2 — Navegar pelo sidebar

```java
SwingUtilities.invokeAndWait(() ->
    buscarBotaoTexto(telaHome.getContentPane(), "Marcas").doClick()
);
pausa(1000);
```

**O que está sendo feito:**
- `buscarBotaoTexto()` percorre a árvore de componentes procurando o botão com o texto "Marcas"
- `doClick()` simula o clique do usuário na EDT
- `pausa(1000)` aguarda o painel de Marcas renderizar antes de continuar

---

### Exemplo 3 — Preencher campos e fechar o diálogo de confirmação

```java
List<JTextField> campos = buscarTodosVisiveis(
    telaHome.getContentPane(), JTextField.class
);

digitarDevagar(campos.get(0), "Ferrari");
pausa(400);
digitarDevagar(campos.get(1), "Itália");
pausa(800);

agendarFechamentoDosDialogos();
```

**O que está sendo feito:**
- `buscarTodosVisiveis()` ignora componentes ocultos pelo `CardLayout`, evitando pegar campos de painéis inativos
- `digitarDevagar()` insere um caractere por vez, simulando a digitação real do usuário
- `agendarFechamentoDosDialogos()` abre uma thread que aguarda e fecha o `JOptionPane` que aparecerá após salvar

---

### Exemplo 4 — Simular cadastro completo (fluxo do usuário)

O teste `abrirSelecionarMarcasDigitarESalvar()` em [DemoTelaHomeTest.java](src/test/view/DemoTelaHomeTest.java) simula todo o fluxo de um usuário real:

```java
@Test
@Timeout(30)
void abrirSelecionarMarcasDigitarESalvar() throws Exception {

    // 1. Usuário vê o dashboard
    pausa(1500);

    // 2. Clica em "Marcas" no sidebar
    SwingUtilities.invokeAndWait(() ->
        buscarBotaoTexto(telaHome.getContentPane(), "Marcas").doClick()
    );
    pausa(1000);

    // 3. Preenche o campo Nome letra por letra
    List<JTextField> campos = buscarTodosVisiveis(
        telaHome.getContentPane(), JTextField.class
    );
    digitarDevagar(campos.get(0), "Ferrari");
    pausa(400);

    // 4. Preenche o campo País
    digitarDevagar(campos.get(1), "Itália");
    pausa(800);

    // 5. Agenda fechar o JOptionPane "Marca salva!" que vai aparecer
    agendarFechamentoDosDialogos();

    // 6. Clica em Salvar
    SwingUtilities.invokeAndWait(() ->
        buscarBotaoExato(telaHome.getContentPane(), "Salvar").doClick()
    );

    // 7. Usuário vê a tabela atualizada com a nova marca
    pausa(2000);
}
```

**O que está sendo testado:**
- Todo o fluxo de cadastro do ponto de vista do usuário
- A navegação pelo sidebar até o painel de Marcas
- A digitação campo a campo simula a interação real com banco de dados real

---

### Auxiliar: digitarDevagar()

Este método simula a digitação humana, inserindo um caractere por vez com um intervalo de 120ms:

```java
private void digitarDevagar(JTextField campo, String texto) throws Exception {
    for (char c : texto.toCharArray()) {
        SwingUtilities.invokeAndWait(() ->
            campo.setText(campo.getText() + c)
        );
        Thread.sleep(120);
    }
}
```

---

## Comparação dos Tipos de Teste

| Tipo de Teste     | Objetivo                          | Ferramenta           | Acessa banco? |
|-------------------|-----------------------------------|----------------------|---------------|
| Funcional         | Validar regras de negócio         | JUnit + Mockito      | Não           |
| Unitário          | Testar métodos isolados           | JUnit + Mockito      | Não           |
| Banco de Dados    | Validar queries e mapeamento      | JUnit + Mockito JDBC | Não (mock)    |
| Conexão (integração) | Verificar abertura e fechamento de conexão | JUnit + SQLite real | **Sim**  |
| Interface Gráfica | Automatizar interação do usuário  | JUnit + SwingUtilities | Sim (real)  |

---

## Estrutura de Testes do Projeto

```
src/test/
├── controller/
│   ├── MarcaControllerTest.java    ← testa MarcaController isolado
│   └── ModeloControllerTest.java   ← testa ModeloController isolado
├── dao/
│   └── MarcaDAOTest.java           ← testa queries SQL via mocks JDBC
├── database/
│   └── ConnectionFactoryTest.java  ← testa conexão real com o banco SQLite
└── view/
    └── DemoTelaHomeTest.java       ← demo visual da TelaHome
```

---

## Conceitos Cobrados em Entrevistas de Emprego

Além de saber escrever testes, as empresas esperam que o candidato conheça as metodologias e práticas que estruturam o desenvolvimento de software de qualidade. Os tópicos abaixo aparecem com frequência em processos seletivos para vagas Java.

---

### TDD — Test-Driven Development *(Desenvolvimento Orientado a Testes)*

**O que é:** metodologia em que os testes são escritos **antes** do código de produção.

**Ciclo Red / Green / Refactor:**

```
1. RED    → escreva um teste que falha (o código ainda não existe)
2. GREEN  → escreva o código mínimo para o teste passar
3. REFACTOR → melhore o código sem quebrar os testes
```

**Exemplo prático:**

```java
// 1. RED — teste escrito primeiro, ainda sem implementação
@Test
void deveCalcularDescontoDeVinte() {
    Pedido pedido = new Pedido(100.0);
    assertEquals(80.0, pedido.calcularDesconto(20)); // falha: método não existe
}

// 2. GREEN — implementação mínima
public double calcularDesconto(int percentual) {
    return valor - (valor * percentual / 100.0);
}

// 3. REFACTOR — reorganiza sem alterar comportamento
```

**Por que as empresas valorizam:**
- Garante que todo código tem teste desde o início
- Força design simples e desacoplado
- Reduz bugs em produção
- Serve como documentação viva do sistema

**Pergunta típica na entrevista:** *"Você já trabalhou com TDD? Explique o ciclo Red/Green/Refactor."*

---

### BDD — Behavior-Driven Development *(Desenvolvimento Orientado a Comportamento)*

**O que é:** extensão do TDD focada no **comportamento do sistema** do ponto de vista do usuário ou negócio. Os testes são escritos em linguagem próxima do natural.

**Estrutura Given / When / Then:**

```
GIVEN  (Dado)    → contexto inicial / pré-condição
WHEN   (Quando)  → ação que o usuário ou sistema executa
THEN   (Então)   → resultado esperado / verificação
```

**Exemplo em Java com JUnit 5 (sem framework externo):**

```java
@Test
void deveInserirMarcaAoSalvar() {
    // Given — controller configurado com DAO mock
    MarcaDAO dao = mock(MarcaDAO.class);
    MarcaController controller = new MarcaController(dao);

    // When — usuário salva uma nova marca
    controller.salvarMarca("Toyota", "Japão");

    // Then — o DAO recebe exatamente uma instrução de inserir
    verify(dao, times(1)).inserir(any(Marca.class));
}
```

**Ferramentas BDD para Java:**
- **Cucumber** — escreve cenários em Gherkin (linguagem natural) e os associa a código Java
- **JBehave** — alternativa ao Cucumber

**Diferença TDD vs BDD:**

| | TDD | BDD |
|---|---|---|
| Foco | Código e unidades | Comportamento e negócio |
| Linguagem | Java puro | Natural (Given/When/Then) |
| Público | Desenvolvedores | Dev + QA + PO |

---

### DDD — Domain-Driven Design *(Design Orientado ao Domínio)*

**O que é:** abordagem de design de software onde o código é organizado em torno do **domínio de negócio**. Não é uma metodologia de testes, mas é muito cobrado em entrevistas junto com TDD/BDD.

**Conceitos principais:**

| Conceito | O que é | Exemplo no CarrosDB |
|---|---|---|
| **Entity** (Entidade) | Objeto com identidade única | `Marca`, `Modelo` |
| **Value Object** | Objeto sem identidade, definido pelos atributos | `"Japão"` como País |
| **Repository** | Interface de acesso a dados do domínio | `MarcaDAO` |
| **Service** | Lógica de negócio que não cabe numa entidade | `MarcaController` |
| **Aggregate** | Grupo de entidades tratadas como unidade | `Modelo` pertence a `Marca` |
| **Bounded Context** | Fronteira onde um modelo tem significado | Módulo de Veículos |

**Por que as empresas valorizam:**
- Código alinhado com o vocabulário do negócio
- Facilita comunicação entre dev e stakeholders
- Base para arquiteturas como Microserviços e Clean Architecture

**Pergunta típica:** *"O que é uma Entity vs um Value Object no DDD?"*

---

### Pirâmide de Testes

Conceito criado por Mike Cohn que define a proporção ideal de cada tipo de teste:

```
        /\
       /  \
      / E2E \        ← poucos (lentos e caros)
     /--------\
    /Integration\    ← médio número
   /--------------\
  /   Unit Tests   \ ← maioria (rápidos e baratos)
 /------------------\
```

| Camada | O que testa | Velocidade | Custo |
|---|---|---|---|
| **Unit** | Métodos isolados | Muito rápido | Baixo |
| **Integration** | Integração entre camadas | Médio | Médio |
| **E2E / UI** | Fluxo completo do usuário | Lento | Alto |

**Antipadrão — Sorvete Invertido:** muitos testes E2E e poucos unitários. Resulta em suite lenta e frágil.

---

### Tipos de Teste que Aparecem em Entrevistas

#### Teste de Integração
Testa a **comunicação entre camadas reais** — controller + DAO + banco de dados — sem mocks:

```java
@Test
void deveInserirEListarMarcaNoбанкеReal() {
    MarcaDAO dao = new MarcaDAO(conexaoReal);
    Marca marca = new Marca("Fiat", "Itália");

    dao.inserir(marca);
    List<Marca> lista = dao.listar();

    assertTrue(lista.stream().anyMatch(m -> m.getNome().equals("Fiat")));
}
```

#### Teste de Regressão
Garante que uma correção de bug **não quebrou** funcionalidades que já funcionavam. É executado após cada mudança no sistema.

#### Teste de Carga / Performance
Verifica se o sistema aguenta grande volume de requisições. Ferramentas: **JMeter**, **Gatling**.

#### Teste de Mutação
Ferramenta (ex: **PIT / Pitest**) introduz bugs artificiais no código e verifica se os testes os detectam. Mede a **qualidade** dos testes, não apenas a cobertura.

---

### Cobertura de Código (Code Coverage)

Métrica que indica qual percentual do código é executado pelos testes.

| Tipo | O que mede |
|---|---|
| **Line Coverage** | % de linhas executadas |
| **Branch Coverage** | % de caminhos (if/else) cobertos |
| **Method Coverage** | % de métodos chamados |

**Atenção:** 100% de cobertura **não garante** testes bons. É possível cobrir todas as linhas sem fazer nenhuma asserção.

---

### SOLID e sua relação com Testes

Os princípios SOLID facilitam a escrita de testes, especialmente o Mockito. Abaixo cada letra com exemplos reais do projeto CarrosDB.

---

#### S — Single Responsibility Principle *(Princípio da Responsabilidade Única)*

> Uma classe deve ter **um único motivo para mudar**.

**Como está no CarrosDB — correto:**

Cada classe tem uma única responsabilidade bem definida:

```
Marca.java           → só representa os dados de uma marca (modelo)
MarcaDAO.java        → só faz operações SQL no banco
MarcaController.java → só coordena a lógica de negócio
TelaMarca.java       → só exibe e captura dados na tela
ValidadorDocumento   → só valida CPF e CNPJ
```

**Como seria a violação:**

```java
// ERRADO — MarcaDAO fazendo validação de negócio (responsabilidade do controller)
public void inserir(Marca marca) {
    if (marca.getNome().isEmpty()) {          // validação aqui viola o S
        JOptionPane.showMessageDialog(...);   // e interação com UI também viola
        return;
    }
    // SQL...
}
```

**Por que importa nos testes:** quando cada classe tem uma responsabilidade, o teste é focado e pequeno. O `MarcaControllerTest` testa só regras de negócio, o `MarcaDAOTest` testa só o SQL.

---

#### O — Open/Closed Principle *(Princípio Aberto/Fechado)*

> Uma classe deve estar **aberta para extensão**, mas **fechada para modificação**.

**Situação:** o sistema precisa exportar marcas em CSV e depois em PDF. Sem o princípio O, você modificaria `MarcaController` a cada novo formato.

**Como aplicar no CarrosDB:**

```java
// Interface define o contrato — nunca é modificada
public interface ExportadorMarca {
    void exportar(List<Marca> marcas);
}

// Extensão 1 — sem tocar no controller
public class ExportadorCSV implements ExportadorMarca {
    @Override
    public void exportar(List<Marca> marcas) {
        // gera arquivo .csv
    }
}

// Extensão 2 — sem tocar no controller
public class ExportadorPDF implements ExportadorMarca {
    @Override
    public void exportar(List<Marca> marcas) {
        // gera arquivo .pdf
    }
}

// MarcaController recebe qualquer exportador sem ser modificado
public class MarcaController {
    private final MarcaDAO marcaDAO;
    private final ExportadorMarca exportador;

    public MarcaController(MarcaDAO marcaDAO, ExportadorMarca exportador) {
        this.marcaDAO   = marcaDAO;
        this.exportador = exportador;
    }

    public void exportarMarcas() {
        exportador.exportar(marcaDAO.listar());
    }
}
```

**No teste — fácil de mockar:**

```java
ExportadorMarca exportadorMock = mock(ExportadorMarca.class);
MarcaController controller = new MarcaController(dao, exportadorMock);

controller.exportarMarcas();

verify(exportadorMock, times(1)).exportar(anyList());
```

---

#### L — Liskov Substitution Principle *(Princípio da Substituição de Liskov)*

> Um objeto de uma **subclasse** deve poder substituir o da **classe pai** sem quebrar o sistema.

**No CarrosDB — o mock já aplica esse princípio:**

Quando o teste substitui o `MarcaDAO` real por um mock, o `MarcaController` não percebe a diferença. Isso só funciona porque o mock respeita o mesmo contrato da classe original.

```java
// MarcaController foi escrito esperando um MarcaDAO
MarcaController controller = new MarcaController(marcaDAO);

// No teste, substituímos por um mock — o controller não sabe e não precisa saber
MarcaDAO mock = mock(MarcaDAO.class);                // substituto de MarcaDAO
MarcaController controller = new MarcaController(mock); // funciona igual
```

**Exemplo de violação:**

```java
// Se uma subclasse mudasse o comportamento de um método herdado...
public class MarcaDAOSomenteLeitura extends MarcaDAO {
    @Override
    public void inserir(Marca marca) {
        throw new UnsupportedOperationException("não pode inserir"); // viola o L
    }
}
// Qualquer código que usa MarcaDAO quebra se receber esse subtipo
```

---

#### I — Interface Segregation Principle *(Princípio da Segregação de Interfaces)*

> Uma classe **não deve ser obrigada** a implementar métodos que não usa.

**Situação:** se criarmos uma interface única para todos os DAOs do projeto com todos os métodos possíveis, uma tela somente leitura seria forçada a implementar `inserir`, `atualizar` e `excluir` sem precisar deles.

**Como aplicar no CarrosDB:**

```java
// ERRADO — interface única e genérica demais
public interface DAO {
    void inserir(Object obj);
    void atualizar(int id, Object obj);
    void excluir(int id);
    List<Object> listar();
    Object buscarPorId(int id);
    void exportarCSV();       // nem todo DAO precisa disso
    void gerarRelatorio();    // nem todo DAO precisa disso
}

// CORRETO — interfaces menores e específicas
public interface Gravavel<T> {
    void inserir(T obj);
    void atualizar(int id, T obj);
    void excluir(int id);
}

public interface Consultavel<T> {
    List<T> listar();
    T buscarPorId(int id);
}

// MarcaDAO implementa ambas porque precisa das duas
public class MarcaDAO implements Gravavel<Marca>, Consultavel<Marca> { ... }

// Uma tela de listagem só precisa consultar — usa só Consultavel<Marca>
public class TelaMarcasListagem {
    private final Consultavel<Marca> dao;

    public TelaMarcasListagem(Consultavel<Marca> dao) {
        this.dao = dao;
    }
}
```

**No teste — mock apenas do que a classe usa:**

```java
Consultavel<Marca> daoMock = mock(Consultavel.class);
when(daoMock.listar()).thenReturn(List.of(new Marca("Fiat", "Itália")));

TelaMarcasListagem tela = new TelaMarcasListagem(daoMock);
// tela não consegue chamar inserir/excluir — interface pequena protege isso
```

---

#### D — Dependency Inversion Principle *(Princípio da Inversão de Dependência)*

> Módulos de **alto nível** (controller) não devem depender de módulos de **baixo nível** (DAO concreto). Ambos devem depender de **abstrações** (interfaces).

**Como está no CarrosDB — o construtor já aplica o D:**

```java
// MarcaController recebe o DAO via construtor — não cria o próprio DAO
public class MarcaController {

    private final MarcaDAO marcaDAO;

    public MarcaController(MarcaDAO marcaDAO) {  // dependência injetada de fora
        this.marcaDAO = marcaDAO;
    }
}
```

Quem cria e injeta o DAO é quem chama o controller (a view ou o teste), não o próprio controller.

**Isso é o que torna os testes possíveis:**

```java
// No teste — injeta um mock no lugar do DAO real
MarcaDAO daoMock = mock(MarcaDAO.class);
MarcaController controller = new MarcaController(daoMock); // D em ação

// No app real — injeta o DAO real
MarcaDAO daoReal = new MarcaDAO();
MarcaController controller = new MarcaController(daoReal);
```

**Como seria a violação:**

```java
// ERRADO — controller cria o próprio DAO, tornando impossível injetar mock
public class MarcaController {
    private final MarcaDAO marcaDAO = new MarcaDAO(); // acoplamento direto

    // agora o teste não consegue substituir por mock
}
```

**Resumo SOLID no CarrosDB:**

| Letra | Princípio | Onde aparece no projeto |
|---|---|---|
| **S** | Uma responsabilidade por classe | `Marca` só é modelo, `MarcaDAO` só é SQL, `MarcaController` só é negócio |
| **O** | Extensão sem modificação | Novo exportador sem alterar o controller |
| **L** | Mock substitui o DAO real sem quebrar nada | `mock(MarcaDAO.class)` no `MarcaControllerTest` |
| **I** | Interfaces pequenas e focadas | `Gravavel<T>` e `Consultavel<T>` separados |
| **D** | Dependência injetada via construtor | `new MarcaController(marcaDAO)` — DAO vem de fora |

---

### Termos Rápidos para Entrevista

#### Mock
Objeto falso que **substitui completamente** uma dependência real. Todos os métodos retornam valores padrão até serem configurados. Permite também verificar se foram chamados.

```java
MarcaDAO dao = mock(MarcaDAO.class);
// dao.inserir() não faz nada de verdade — é um mock
```

---

#### Stub
É um mock configurado para **retornar um valor fixo**. O foco é no retorno, não em verificar se foi chamado.

```java
MarcaDAO dao = mock(MarcaDAO.class);
when(dao.buscarPorId(1)).thenReturn(new Marca("Ford", "EUA")); // isso é um stub
```

---

#### Spy
Wrapper sobre um **objeto real**. Executa o código verdadeiro, mas permite monitorar chamadas e sobrescrever métodos específicos.

```java
MarcaDAO daoReal = new MarcaDAO(connection);
MarcaDAO spy = spy(daoReal);

// chama o método real, mas monitora a chamada
spy.listar();
verify(spy, times(1)).listar();

// sobrescreve apenas um método, mantendo os outros reais
doReturn(new ArrayList<>()).when(spy).listar();
```

---

#### Fixture
Dados de teste preparados **antes de cada cenário** para que os testes partam de um estado conhecido e consistente.

```java
@BeforeEach
void setup() {
    // fixture: objetos prontos para uso nos testes
    marca = new Marca();
    marca.setId(1);
    marca.setNome("Toyota");
    marca.setPais("Japão");

    dao = mock(MarcaDAO.class);
    controller = new MarcaController(dao);
}
```

---

#### Assert
Verificação do **resultado esperado vs obtido**. Se a condição for falsa, o teste falha com uma mensagem de erro.

```java
// verifica igualdade
assertEquals("Toyota", resultado.getNome());

// verifica que não é nulo
assertNotNull(resultado);

// verifica condição booleana
assertTrue(lista.size() > 0);

// verifica que lança exceção
assertThrows(IllegalArgumentException.class, () -> controller.salvarMarca("", ""));
```

---

#### Setup / Teardown
Métodos que **preparam e limpam** o ambiente antes e depois de cada teste.

```java
@BeforeEach   // Setup — executa antes de cada @Test
void setup() {
    dao        = mock(MarcaDAO.class);
    controller = new MarcaController(dao);
}

@AfterEach    // Teardown — executa após cada @Test
void teardown() {
    // fecha conexões, limpa arquivos temporários, etc.
}
```

---

#### Flaky Test
Teste **instável** — passa em algumas execuções e falha em outras sem nenhuma mudança no código. Causa comum: dependência de tempo, threads ou ordem de execução.

```java
// RUIM — flaky: depende do tempo real do sistema
@Test
void testeComTempo() throws InterruptedException {
    Thread.sleep(500); // às vezes o sistema está lento e o timeout estoura
    assertEquals("processado", tarefa.getStatus());
}

// BOM — use @Timeout e mocks para controlar o tempo
@Test
@Timeout(5)
void testeComTimeout() {
    when(dao.buscarPorId(1)).thenReturn(marca); // sem dependência de tempo real
    assertNotNull(controller.buscarMarca(1));
}
```

---

#### Test Double
Termo **genérico** para qualquer objeto de teste que substitui uma dependência real. Mock, Stub e Spy são tipos específicos de Test Double.

```java
// Fake — implementação alternativa simplificada (sem framework)
public class MarcaDAOFake implements MarcaDAO {
    private List<Marca> banco = new ArrayList<>();

    @Override
    public void inserir(Marca m) { banco.add(m); }

    @Override
    public List<Marca> listar() { return banco; }
}

// Uso no teste — sem Mockito, sem banco real
MarcaDAO fake = new MarcaDAOFake();
MarcaController controller = new MarcaController(fake);
controller.salvarMarca("Honda", "Japão");
assertEquals(1, fake.listar().size());
```

---

#### CI/CD
**CI (Continuous Integration):** a cada commit, o servidor roda automaticamente todos os testes. Se algum falhar, o time é notificado antes de ir para produção.

**CD (Continuous Delivery/Deployment):** após os testes passarem, o sistema é entregue/publicado automaticamente.

```
Commit → GitHub Actions / Jenkins → mvn test → ✅ todos passaram → Deploy
                                              → ❌ falhou → bloqueia o merge
```

Ferramentas comuns com Java: **GitHub Actions**, **Jenkins**, **GitLab CI**, **CircleCI**.

---

## Conclusão

Os testes são fundamentais para garantir qualidade, segurança e funcionamento correto das aplicações Java Desktop.

**Benefícios observados no projeto CarrosDB:**

- O uso de **Mockito** permite testar controller e DAO sem banco de dados real
- O uso de **SwingUtilities.invokeAndWait()** garante que os testes de interface rodem na thread correta do Swing
- Testes de validação (campos vazios, seleção obrigatória) evitam regressões nas regras de negócio
- Menos erros chegam ao usuário final
- Maior confiabilidade e facilidade de manutenção

---

## Configuração

### Por que esses JARs são necessários?

O Mockito não trabalha sozinho — ele depende de três bibliotecas externas para funcionar:

#### `mockito-core-5.23.0.jar`

É o núcleo do Mockito. Fornece as anotações e métodos principais: `mock()`, `when()`, `verify()`, `spy()`, `any()`, `times()`, `never()`, etc.

#### `byte-buddy-1.18.8.jar` e `byte-buddy-agent-1.18.8.jar`

O Mockito precisa **criar subclasses em tempo de execução** para interceptar chamadas de métodos. Isso é feito pela biblioteca **ByteBuddy**, que gera bytecode Java dinamicamente.

Quando você escreve `mock(MarcaDAO.class)`, o ByteBuddy cria uma nova classe que herda de `MarcaDAO` e sobrescreve todos os métodos para que o Mockito possa controlá-los.

- `byte-buddy-1.18.8.jar` — faz a geração de bytecode
- `byte-buddy-agent-1.18.8.jar` — necessário quando o Mockito precisa usar o Java Agent para instrumentar classes `final` ou já carregadas pela JVM

#### `objenesis-3.5.jar`

Cria instâncias de objetos **sem chamar o construtor**. Isso é essencial porque muitas classes têm construtores com parâmetros obrigatórios ou lógica que causaria erros. O Mockito usa o Objenesis para instanciar o mock sem precisar passar argumentos.

Por exemplo, `MarcaDAO` exige uma `Connection` no construtor — o Objenesis cria o objeto mock ignorando essa exigência.

**Resumo dos JARs:**

| JAR                          | Função                                              |
|------------------------------|-----------------------------------------------------|
| `mockito-core-5.23.0.jar`    | API principal: `mock`, `when`, `verify`             |
| `byte-buddy-1.18.8.jar`      | Gera subclasses e bytecode em tempo de execução     |
| `byte-buddy-agent-1.18.8.jar`| Java Agent para instrumentar classes `final`/loaded |
| `objenesis-3.5.jar`          | Instancia objetos sem chamar o construtor           |

---

### Como criar e rodar testes no Eclipse

#### Criando um novo teste

**1. Crie a classe de teste** na pasta `src/test/` seguindo o padrão do projeto:

```
src/test/controller/ModeloControllerTest.java  ← para controllers
src/test/dao/ModeloDAOTest.java                ← para DAOs
src/test/view/TelaModeloTest.java              ← para telas
```

**2. Estrutura mínima de uma classe de teste:**

```java
package test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ModeloController;
import dao.MarcaDAO;
import dao.ModeloDAO;

public class ModeloControllerTest {

    private ModeloDAO modeloDAO;
    private MarcaDAO marcaDAO;
    private ModeloController controller;

    @BeforeEach
    public void setup() {
        modeloDAO  = mock(ModeloDAO.class);
        marcaDAO   = mock(MarcaDAO.class);
        controller = new ModeloController(modeloDAO, marcaDAO);
    }

    @Test
    public void testeExcluirModelo() {
        controller.excluirModelo(1);

        verify(modeloDAO, times(1)).excluir(1);
    }
}
```

**Regras do JUnit 5:**

| Anotação          | Quando usar                                      |
|-------------------|--------------------------------------------------|
| `@BeforeEach`     | Executa antes de **cada** teste — use para setup |
| `@AfterEach`      | Executa após cada teste — use para limpeza       |
| `@Test`           | Marca o método como um teste                     |
| `@Timeout(30)`    | Falha se o teste demorar mais que 30 segundos    |
| `@Disabled`       | Pula o teste — ele não será executado            |

#### Pulando um teste com `@Disabled`

Coloque `@Disabled` logo acima do `@Test`. O teste aparece na aba JUnit como **ignorado** (amarelo), sem falhar nem passar:

```java
@Test
@Disabled("aguardando correção do bug #42")
void testeBuscarMarcaPorNome() {
    // este teste não será executado
}
```

Também é possível desabilitar a **classe inteira**, pulando todos os testes dela de uma vez:

```java
@Disabled("funcionalidade ainda não implementada")
public class TelaModeloTest {
    // nenhum teste desta classe será executado
}
```

O texto dentro de `@Disabled("...")` é opcional, mas é boa prática explicar o motivo.

---

#### Rodando os testes no Eclipse

**Rodar todos os testes de uma classe (forma mais simples):**
1. No **Package Explorer**, clique com o botão direito no arquivo `.java` do teste
2. Selecione **Run As → JUnit Test**
3. A aba **JUnit** abrirá com todos os resultados

**Rodar um único teste (método específico):**

O clique dentro do corpo do método nem sempre funciona no Eclipse. A forma mais confiável é:

1. Execute a classe inteira primeiro (**Run As → JUnit Test**)
2. Na aba **JUnit** que abrirá, localize o método desejado
3. Clique com o botão direito sobre ele → **Run**

Outra opção: posicione o cursor **no nome do método** (na linha do `void`) e pressione:

```
Alt + Shift + X   →   T
```

**Rodar todos os testes do projeto:**
1. Clique com o botão direito na pasta `src/test/` no **Package Explorer**
2. Selecione **Run As → JUnit Test**

**Interpretando o resultado na aba JUnit:**

```
✅ verde  → todos os testes passaram
❌ vermelho → pelo menos um teste falhou
```

Ao clicar em um teste com falha, o Eclipse mostra:
- A linha onde ocorreu o erro
- O valor **esperado** (`expected`) vs o valor **recebido** (`actual`)

---

#### Exemplo de falha e como ler a mensagem

Se o teste abaixo falhar:

```java
assertEquals("BMW", resultado.getNome());
```

A mensagem de erro será:

```
expected: <BMW> but was: <null>
```

Isso indica que `resultado.getNome()` retornou `null` — provavelmente o mock não foi configurado com `when` para esse caso.

---

### Como medir cobertura no projeto CarrosDB (Eclipse)

O projeto usa Eclipse puro, sem Maven ou Gradle. A ferramenta indicada é o **EclEmma** — plugin gratuito que integra o JaCoCo diretamente no Eclipse e destaca as linhas cobertas diretamente no editor.

#### Passo 1 — Instalar o EclEmma

1. No Eclipse, vá em **Help → Eclipse Marketplace**
2. No campo de busca, digite `EclEmma`
3. Clique em **Install** no resultado "EclEmma Java Code Coverage"
4. Aceite os termos e reinicie o Eclipse quando solicitado

> O EclEmma já vem instalado por padrão no Eclipse IDE for Java Developers. Verifique antes de instalar: **Help → About Eclipse → Installation Details** e procure por "EclEmma".

#### Passo 2 — Rodar os testes com cobertura

1. No **Package Explorer**, clique com o botão direito na pasta `src/test/` (ou em uma classe de teste específica)
2. Selecione **Coverage As → JUnit Test**
3. Os testes serão executados normalmente, e o Eclipse abrirá a aba **Coverage**

#### Passo 3 — Interpretar o resultado

**No editor de código**, as linhas ficam coloridas automaticamente:

```
Verde  → linha executada pelos testes (coberta)
Vermelho → linha nunca executada (não coberta)
Amarelo → branch parcialmente coberto (ex: só o "if", mas não o "else")
```

**Na aba Coverage** (parte inferior do Eclipse), aparece um relatório por classe:

```
Classe                  | Cobertura de linha | Cobertura de branch
MarcaController         | 95,2%              | 87,5%
MarcaDAO                | 78,4%              | 66,7%
TelaMarca               | 42,1%              | 30,0%
```

#### Exemplo prático no CarrosDB

Ao rodar o `MarcaControllerTest` com Coverage As:

- `salvarMarca()` → verde (testado em `testeSalvarMarca`)
- `listarMarcas()` → verde (testado em `testeListarMarcas`)
- `buscarMarca()` → verde (testado em `testeBuscarMarca`)
- `atualizarMarca()` → vermelho se não houver teste para ela

#### Passo 4 — Limpar o destaque

Para remover as cores do editor após a análise:

**Edit → Remove Active Coverage Session**  
ou clique no ícone de vassoura na aba **Coverage**
