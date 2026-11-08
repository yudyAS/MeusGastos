# Respostas sobre a Arquitetura MVVM - Meus Gastos

## 1. Qual é o papel do `GastoRepository` de ser uma interface e não uma classe concreta?

O papel de definir o `GastoRepository` como uma interface é aplicar o **Princípio da Inversão de Dependência (DIP - Dependency Inversion Principle)** e garantir o **desacoplamento** entre as camadas.

Ao depender de uma abstração (a interface `GastoRepository`), a camada de negócio (`GastosViewModel`) não precisa conhecer os detalhes de onde ou como os dados são armazenados (seja em memória com `GastoRepositoryMemoria`, dados fictícios com `GastoRepositoryDemo`, ou futuramente um banco de dados local Room ou uma API REST na nuvem). 

Isso traz os seguintes benefícios:
- **Flexibilidade:** É possível alterar a fonte de dados do app trocando apenas a instância repassada à interface, sem alterar nenhuma linha de código na ViewModel ou na View.
- **Testabilidade:** Facilita a criação de Mocks/Stubs em testes unitários automatizados sem depender de banco de dados ou rede.

---

## 2. Por que o `total` é calculado e não guardado em uma variável atualizada manualmente?

O `total` é implementado como uma **propriedade derivada / calculada** (`val total: Double get() = gastosVisiveis.sumOf { it.valor }`).

Se o `total` fosse armazenado em uma variável mutável separada (ex: `var total = 0.0`), teríamos a obrigação de atualizar manualmente o seu valor em todos os métodos que alteram o estado da lista, como no `adicionar()`, `remover()`, e ao mudar o `filtro` de categoria.

Isso geraria um alto risco de **Dessincronização de Estado** (bugs em que o valor exibido não corresponde à soma real dos itens visíveis). Sendo uma propriedade calculada dinamicamente, garantimos que o `total` estará **sempre 100% correto e sincronizado** com o estado atual da lista `gastosVisiveis`, eliminando a necessidade de gerenciamento manual.

---

## 3. O `FormularioGasto` não recebe o `GastosViewModel`. O que isso te permite fazer que não seria possível se ele recebesse?

Ao não receber o `GastosViewModel` diretamente, o componente `FormularioGasto` torna-se **Stateless** (livre de estado interno acoplado), recebendo apenas dados primitivos (como `descricao: String`, `valorTexto: String`, etc.) e callbacks de eventos (como `aoAdicionar: () -> Unit`).

Isso nos permite:
- **Renderização em Previews do Android Studio (@Preview):** O componente pode ser visualizado e testado visualmente no IDE sem a necessidade de instanciar ou mockar um `ViewModel` inteiro.
- **Reutilização de Código:** O formulário pode ser facilmente reaproveitado em outras telas ou outros fluxos da aplicação que exijam entrada de dados semelhantes.
- **Testes de UI Isolados:** Facilita a criação de testes de interface (`ComposeTestRule`) focados puramente na renderização e na resposta do formulário aos cliques do usuário.
