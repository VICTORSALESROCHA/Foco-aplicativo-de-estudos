# Foco — Controle de Estudos

Projeto didático completo em Java 17, JavaFX 21.0.6 e Maven. Sem lambdas, referências de método ou API Stream. Não há dados pessoais nem exemplos previamente cadastrados.

## Abrir e executar no IntelliJ IDEA
1. Extraia o ZIP inteiro. Não execute dentro do arquivo compactado.
2. No IntelliJ, selecione **File > Open** e abra a pasta `foco-estudos` (ou seu `pom.xml` como projeto).
3. Configure um **JDK 17 ou 21** em **File > Project Structure > Project SDK**. Use o mesmo JDK no Maven Runner.
4. Aguarde a importação do Maven. A primeira execução precisa de internet para baixar as dependências.
5. Na janela **Maven**, clique em **Execute Maven Goal** e execute `javafx:run`. Alternativamente, expanda Plugins > javafx > javafx:run e dê dois cliques.
6. Após importar as dependências, também é possível executar o `main` de **Launcher.java** pelo triângulo verde. Use Launcher como ponto de entrada.

No terminal, com Maven instalado e JAVA_HOME configurado: `mvn clean javafx:run`.
O projeto não inclui Maven Wrapper nem instalador .exe. O IntelliJ pode utilizar seu Maven integrado. Não é necessário baixar o JavaFX SDK manualmente ou adicionar opções --module-path.

## Editar no Scene Builder
Abra `src/main/resources/br/com/foco/principal.fxml` no Scene Builder. O CSS está ao lado e é referenciado por `@estilo.css`.
Preserve os fx:id, o Controller `br.com.foco.controller.PrincipalController` e os eventos On Action, como `#salvar`. Os botões executam métodos @FXML; nenhuma lambda é necessária.
O Scene Builder mostra a estrutura, não executa os eventos ou carrega os dados. Para testar funcionalidade, execute o projeto.

## Funcionalidades
- Criar, editar e excluir atividades com confirmação.
- Título, disciplina, prazo e minutos previstos (1 a 10080).
- Concluir ou reabrir atividades.
- Buscar título/disciplina sem diferenciar maiúsculas e minúsculas.
- Filtrar pendentes, concluídas e atrasadas. Pendentes incluem atrasadas.
- Ordenar por prazo ou título. Clique **Aplicar filtros** após escolher; Enter na busca também aplica.
- Totais gerais, minutos pendentes, progresso e resumo por disciplina.
- Selecione uma linha antes de editar, concluir ou excluir. Limpar cancela a edição.
- Os totais e o resumo sempre consideram todos os registros, independentemente do filtro da tabela.
- Prazo passado é permitido e sinaliza atraso. Atraso é recalculado ao atualizar a tela.

## Persistência
Cada alteração é salva automaticamente em `System.getProperty("user.home")/.foco-estudos/atividades.xml`.
No Windows normalmente: `C:\Users\SEU_USUARIO\.foco-estudos\atividades.xml`.
O caminho real aparece no rodapé ao iniciar. Para backup, feche o aplicativo e copie esse arquivo. Use uma única instância do aplicativo por vez.
O XML padrão de Properties preserva acentos e caracteres especiais. A gravação usa arquivo temporário e substituição atômica quando suportada. Se a gravação falhar, a lista em memória permanece intacta. Um arquivo inválido bloqueia edição para evitar sobrescrevê-lo: faça uma cópia, corrija ou mova o arquivo e reinicie.

## Organização e decisões
- `model/Atividade`: objeto imutável com validação; alteração gera outro objeto com o mesmo UUID.
- `repository/AtividadeRepository`: leitura e escrita, sem dependência da interface.
- `service/AtividadeService`: regras de pesquisa, ordenação e cálculos; devolve cópias das listas.
- `controller/PrincipalController`: interação da tela, mensagens e coordenação do serviço.
- `principal.fxml` e `estilo.css`: estrutura e aparência separadas.
- `Launcher` e `App`: inicialização.

Laços for, condicionais, Comparator com classe anônima e StringBuilder foram mantidos intencionalmente. InputStream/OutputStream são classes de entrada/saída, não a API java.util.stream que será estudada.
Este é um aplicativo local didático: não possui login, nuvem ou sincronização. A persistência ocorre na thread da interface e é adequada a pequenas listas; operações grandes deverão migrar para tarefas em segundo plano.

## Treinamento
Leia `docs/EXERCICIOS.md`. O aplicativo já funciona; o treino consiste em refatorar um método por vez preservando o comportamento. As respostas não estão incluídas.

## Validação desta entrega
Todas as classes foram compiladas com alvo Java 17. Foram verificados persistência XML, caracteres especiais, filtros, ordenação, contagem, edição, exclusão e preservação dos dados em falhas. O FXML e CSS foram carregados em JavaFX sem monitor (headless), com teste dos botões Salvar e Concluir. A aparência e a execução no Windows/Scene Builder devem ser conferidas no seu computador; não foram testadas nesse sistema.
