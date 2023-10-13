package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logic.TestResult

@Composable
@Preview
fun MainScreen(screenModel: MainScreenModel){
    val uiState by screenModel.uiState.collectAsState()
    val results by screenModel.testResult.collectAsState()

    val scope = rememberCoroutineScope()
    val lazyState = rememberLazyListState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top,
    ) {
        Row(Modifier.weight(1f)) {
            Box(Modifier.weight(1f))
            Column(modifier = Modifier.width(900.dp).padding(10.dp, 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.background(Color.Black,shape = RoundedCornerShape(size = 5.dp)).height(90.dp).width(600.dp).align(Alignment.CenterHorizontally))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp,Alignment.End), modifier = Modifier.width(600.dp).height(50.dp).align(Alignment.CenterHorizontally)) {
                    ElevatedButton(onClick = {  },
                        modifier = Modifier.width(90.dp).height(35.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A5D9)),
                        shape = RoundedCornerShape(size = 5.dp),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(5.dp)
                    ) {
                        Text("書き出し",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(700),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                    ElevatedButton(onClick = { scope.launch{screenModel.onRetryClicked()} },
                        modifier = Modifier.width(160.dp).height(35.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A5D9)),
                        shape = RoundedCornerShape(size = 5.dp),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(5.dp)
                        ) {
                        Text("失敗ケースを再実行",
                            style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(700),
                            textAlign = TextAlign.Center,
                        )
                        )
                    }
                }
                Column(Modifier.background(color = Color(0xFF383838), shape = RoundedCornerShape(size = 10.dp)).fillMaxWidth().fillMaxHeight()) {
                    Row(Modifier.padding(15.dp,8.dp,0.dp,0.dp)) {
                        Button(onClick = { screenModel.onUiStateChanged(uiState.copy(tab = MainScreenModel.UiState.Tab.ERROR)) },
                            modifier = Modifier.width(200.dp).height(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if(uiState.tab == MainScreenModel.UiState.Tab.ERROR)Color.Black else Color.Transparent),
                            shape = RoundedCornerShape(10.dp,10.dp,0.dp,0.dp),
                            contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.buttonElevation(5.dp)
                            ) {
                            Text("失敗ケース",
                                style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(700),
                                textAlign = TextAlign.Center,
                            )
                            )
                        }
                        Button(onClick = { screenModel.onUiStateChanged(uiState.copy(tab = MainScreenModel.UiState.Tab.SUCCESS)) },
                            modifier = Modifier.width(200.dp).height(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if(uiState.tab == MainScreenModel.UiState.Tab.SUCCESS)Color.Black else Color.Transparent),
                            shape = RoundedCornerShape(10.dp,10.dp,0.dp,0.dp),
                            contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.buttonElevation(5.dp)
                        ) {
                            Text("成功ケース",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(700),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                    SelectionContainer {
                        LazyColumn(state = lazyState, modifier = Modifier.background(Color.Black, shape = RoundedCornerShape(8.dp)).fillMaxWidth().fillMaxHeight().padding(20.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            items(
                                if(uiState.tab == MainScreenModel.UiState.Tab.ERROR)
                                    results.filterIsInstance<TestResult.Failure>().map { "${it.formula.toString()} expect=${it.expect} actual=${it.actual}" }
                                else
                                    results.filterIsInstance<TestResult.Success>().map { "${it.formula.toString()} result=${it.result}"}
                            ){
                                Text(it.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight(400),
                                        textAlign = TextAlign.Center,
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            Box(Modifier.weight(1f))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.border(width = 1.dp, color = Color(0x1A000000), shape = RoundedCornerShape(size = 2.dp))
                .width(395.dp)
                .fillMaxHeight()
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 2.dp))
                .padding(start = 38.dp, top = 40.dp, end = 14.dp, bottom = 35.dp)
        ) {
            settingBoolElem("減算を含む", uiState.containSub, {screenModel.onUiStateChanged(uiState.copy(containSub = it))})
            settingBoolElem("乗算を含む", uiState.containMul, {screenModel.onUiStateChanged(uiState.copy(containMul = it))})
            settingBoolElem("二桁以上の整数を含む", uiState.containMoreDigits, {screenModel.onUiStateChanged(uiState.copy(containMoreDigits = it))})
            settingBoolElem("負の数を含む", uiState.containNegative, {screenModel.onUiStateChanged(uiState.copy(containNegative = it))})
            settingBoolElem("全体を囲む括弧を許可する", uiState.allowOuterBracket, {screenModel.onUiStateChanged(uiState.copy(allowOuterBracket = it))})
            settingIntElem("ネストの深さ", uiState.depth, {screenModel.onUiStateChanged(uiState.copy(depth = it)) })
            settingIntElem("数式の最大の長さ(文字)", uiState.maxLength, {screenModel.onUiStateChanged(uiState.copy(maxLength = it))})
            settingIntElem( "試行回数", uiState.trialCount, {screenModel.onUiStateChanged(uiState.copy(trialCount = it))})
            Box(Modifier.weight(1f))
            ElevatedButton(onClick = { scope.launch{withContext(Dispatchers.Default){screenModel.onExecuteClicked()}} },
                modifier = Modifier.width(300.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A5D9)),
                shape = RoundedCornerShape(size = 5.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(5.dp),
                enabled = !uiState.isLoading
            ) {
                if(uiState.isLoading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
                else
                    Text("実行",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight(700),
                            textAlign = TextAlign.Center,
                        )
                    )
            }
        }
    }
}

@Composable
private fun settingBoolElem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.CenterVertically)){
            Text(text,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(400),
            ))
        }
        Box(Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.scale(0.7f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun settingIntElem(text: String, value: Int, onValueChange: (Int) -> Unit) {
    Row {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.CenterVertically)){
            Text(text,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(400),
                ))
        }
        Box(Modifier.weight(1f))
        OutlinedTextField(value = value.toString(), onValueChange = { it.toIntOrNull()?.let{onValueChange(it)} }, modifier = Modifier.width(70.dp).height(45.dp))
    }
}