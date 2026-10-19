# Roteiro de treino — sem respostas

Antes de alterar, execute o aplicativo e faça um commit. Trabalhe um exercício de cada vez; envie sua tentativa para revisão.

1. **Lambda:** em `AtividadeService.ordenar`, substitua apenas o Comparator anônimo de título por lambda. Preserve a comparação ignorando maiúsculas.
2. **Comparator:** reescreva a ordenação por prazo com Comparator.comparing. Treine a referência de método depois da lambda.
3. **Filter:** em `pesquisar`, transforme o laço em um pipeline. Preserve o tratamento do texto, todos os filtros e o fato de pendentes incluírem atrasadas.
4. **Count:** em `contarConcluidas`, use filter e count. Observe que count retorna long; decida se deve atualizar a assinatura e seus consumidores.
5. **Soma:** em `somarMinutosPendentes`, use filter, mapToLong e sum. Preserve o retorno long.
6. **Grouping:** em `contarPorDisciplina`, use groupingBy e um coletor de contagem. Preserve a ordem alfabética do TreeMap e ajuste os tipos se usar counting.
7. **Map:** adicione ao serviço uma operação que devolva somente os títulos, sem modificar a lista original.
8. **AnyMatch:** adicione uma verificação para informar se há atividades atrasadas ainda não concluídas.
9. **Optional:** encontre a atividade pendente de menor prazo usando min. Trate lista vazia e empate sem usar get sem verificação.
10. **Integração:** acrescente um botão no FXML para exibir a próxima atividade usando o método anterior.

## Dados para comparação manual
Cadastre estes registros manualmente (prazos relativos ao dia do teste):

| Título | Disciplina | Minutos | Prazo | Situação |
|---|---|---:|---|---|
| Revisar POO | Java | 60 | Amanhã | Pendente |
| Resolver SQL | Banco de Dados | 40 | Ontem | Pendente |
| Ler sobre herança | Java | 30 | Hoje | Concluída |

Esperado: 3 atividades; 1 concluída; 100 minutos pendentes; progresso de aproximadamente 33%; Java = 2 e Banco de Dados = 1; filtro Atrasadas = Resolver SQL.
Busca JAVA = 2 linhas; filtro Pendentes = 2 linhas. Feche e reabra: os dados devem continuar iguais.

## Cuidados na refatoração
- Não modifique a coleção enquanto percorre um Stream dela.
- Stream.toList produz lista não modificável; considere o que o consumidor precisa.
- Não transforme validações e gravação em pipelines só para diminuir linhas.
- Preserve os resultados antes de partir para o próximo exercício.
