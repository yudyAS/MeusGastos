# Relatório de Experimentos de Arquitetura (Estragos) - Meus Gastos

Este documento registra as observações práticas ao tentar violar propositalmente as regras da arquitetura MVVM recomendada no projeto.

---

## Estrago 1: Trocar a referência da interface pelo tipo concreto no ViewModel
- **Ação realizada:** Alterar o tipo da propriedade `repositorio` no `GastosViewModel` de `GastoRepository` para `GastoRepositoryMemoria`.
- **Previsão:** Ao acoplar a classe concreta diretamente no ViewModel, perde-se a capacidade de alternar a fonte de dados sem modificar o código do ViewModel.
- **Resultado/Mensagem:** Para trocar a fonte de dados de dados em memória para dados demo (`GastoRepositoryDemo`) ou banco de dados, é necessário modificar diretamente a classe do ViewModel. O desacoplamento é quebrado.

---

## Estrago 2: Usar uma lista comum `List<Gasto>` em vez de `mutableStateListOf`
- **Ação realizada:** Declarar a lista de gastos como uma `List<Gasto>` padrão do Kotlin dentro da ViewModel.
- **Previsão:** O Jetpack Compose não conseguirá observar as alterações na lista quando novos itens forem adicionados ou removidos.
- **Resultado/Mensagem:** A interface do aplicativo congela e não atualiza automaticamente ao clicar em "Adicionar Gasto" ou "Remover". A lista exibida na tela fica estática, exigindo um mecanismo manual de recompotamento.

---

## Estrago 3: Armazenar o `total` em uma variável mutável comum
- **Ação realizada:** Definir `var total = 0.0` e tentar atualizar a variável em pontos específicos.
- **Previsão:** Ao aplicar filtros de categoria ou remover um gasto, o valor total do resumo ficará dessincronizado com os itens visíveis na tela.
- **Resultado/Mensagem:** Ao filtrar gastos por categoria (ex: "Lazer"), o total continua exibindo a soma de todos os gastos ou um valor desatualizado, introduzindo inconsistência na exibição de dados.

---

## Estrago 4: Passar a ViewModel ou permitir estado no FormularioGasto
- **Ação realizada:** Passar a instância de `GastosViewModel` para o componente `FormularioGasto` ou gerenciar o estado do formulário diretamente dentro da `TelaGastos`.
- **Previsão:** A visualização do componente no Preview do Android Studio deixará de funcionar e o componente ficará engessado.
- **Resultado/Mensagem:** O Android Studio exibe erro de renderização no `@Preview`, pois não consegue instanciar a ViewModel no ambiente de preview sem fornecimento de fábrica/mocks complexos.

---

## Estrago 5: Fazer a View acessar o Repositório diretamente sem passar pelo ViewModel
- **Ação realizada:** Instanciar o `GastoRepository` direto no Composable `TelaGastos`.
- **Previsão:** Violação da arquitetura MVVM, misturando a camada de exibição com a camada de dados e causando perda de estado ao rotacionar a tela.
- **Resultado/Mensagem:** Quando o usuário gira o celular (mudança de configuração da Activity), todos os dados digitados ou carregados na tela são perdidos e a tela é recriada do zero.
