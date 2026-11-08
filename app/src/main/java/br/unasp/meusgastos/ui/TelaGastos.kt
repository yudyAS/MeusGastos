package br.unasp.meusgastos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.unasp.meusgastos.model.Categoria
import br.unasp.meusgastos.model.Gasto
import br.unasp.meusgastos.ui.theme.Fundo
import br.unasp.meusgastos.ui.theme.Navy
import br.unasp.meusgastos.viewmodel.GastosViewModel

/**
 * CAMADA VIEW.
 * Este é o ÚNICO Composable do app que conhece o ViewModel.
 * Repare: não existe um único "if" de regra aqui dentro.
 */
@Composable
fun TelaGastos(
    modifier: Modifier = Modifier,
    vm: GastosViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        Text("Meus Gastos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy)

        Spacer(Modifier.height(14.dp))

        FormularioGasto(
            descricao = vm.descricao,
            valorTexto = vm.valorTexto,
            categoria = vm.categoriaSelecionada,
            erro = vm.erro,
            aoDigitarDescricao = vm::aoDigitarDescricao,
            aoDigitarValor = vm::aoDigitarValor,
            aoSelecionarCategoria = vm::aoSelecionarCategoria,
            aoAdicionar = vm::adicionar
        )

        Spacer(Modifier.height(16.dp))

        FiltroCategorias(selecionada = vm.filtro, aoFiltrar = vm::aoFiltrar)

        Spacer(Modifier.height(12.dp))

        ResumoTotal(total = vm.total, quantidade = vm.quantidade)

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.gastosVisiveis) { gasto ->
                LinhaGasto(gasto = gasto, aoRemover = { vm.remover(gasto.id) })
            }
        }
    }
}

@Composable
fun FiltroCategorias(
    selecionada: Categoria?,
    aoFiltrar: (Categoria?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Etiqueta("Todas", ativa = selecionada == null, aoClicar = { aoFiltrar(null) })
        }
        items(Categoria.entries.toList()) { c ->
            Etiqueta(c.rotulo, ativa = selecionada == c, aoClicar = { aoFiltrar(c) })
        }
    }
}

@Preview(showBackground = true, heightDp = 780)
@Composable
private fun FiltroPreview() {
    FiltroCategorias(selecionada = Categoria.LAZER, aoFiltrar = {}, modifier = Modifier.padding(16.dp))
}

// Criando os componentes
// 1. Etiqueta (Filtro por Categoria)
@Composable
fun Etiqueta(
    texto: String,
    ativa: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = ativa,
        onClick = aoClicar,
        label = { Text(texto) },
        modifier = modifier
    )
}

// 2. FormularioGasto
@Composable
fun FormularioGasto(
    descricao: String,
    valorTexto: String,
    categoria: Categoria,
    erro: String?,
    aoDigitarDescricao: (String) -> Unit,
    aoDigitarValor: (String) -> Unit,
    aoSelecionarCategoria: (Categoria) -> Unit,
    aoAdicionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = descricao,
            onValueChange = aoDigitarDescricao,
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valorTexto,
            onValueChange = aoDigitarValor,
            label = { Text("Valor (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        
        Text("Categoria:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Categoria.entries.toList()) { c ->
                FilterChip(
                    selected = (categoria == c),
                    onClick = { aoSelecionarCategoria(c) },
                    label = { Text(c.rotulo) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        if (erro != null) {
            Text(text = erro, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        Button(onClick = aoAdicionar, modifier = Modifier.fillMaxWidth()) {
            Text("Adicionar Gasto")
        }
    }
}

// 3. ResumoTotal
@Composable
fun ResumoTotal(total: Double, quantidade: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Total: R$ %.2f".format(total), fontWeight = FontWeight.Bold)
            Text("Quantidade: $quantidade itens")
        }
    }
}

// 4. LinhaGasto
@Composable
fun LinhaGasto(gasto: Gasto, aoRemover: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(gasto.descricao, fontWeight = FontWeight.Bold)
                Text("${gasto.categoria.rotulo} - R$ %.2f".format(gasto.valor))
            }
            IconButton(onClick = aoRemover) {
                Icon(Icons.Default.Delete, contentDescription = "Remover")
            }
        }
    }
}