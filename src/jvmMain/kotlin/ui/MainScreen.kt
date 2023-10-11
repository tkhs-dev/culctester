package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun MainScreen(screenModel: MainScreenModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top,
    ) {
        Row(Modifier.weight(1f)) {
            Box(Modifier.weight(1f))
            Column(modifier = Modifier.width(900.dp).padding(0.dp, 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    ElevatedButton(onClick = {  },
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
                        Button(onClick = {  },
                            modifier = Modifier.width(200.dp).height(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
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
                        Button(onClick = {  },
                            modifier = Modifier.width(200.dp).height(35.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
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
                    Column(modifier = Modifier.background(Color.Black, shape = RoundedCornerShape(8.dp)).fillMaxWidth().fillMaxHeight().padding(20.dp)) {
                        Text("失敗ケースがありません",
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
            settingBoolElem("減算を含む", true, {})
            settingBoolElem("乗算を含む", true, {})
            settingBoolElem("二桁以上の整数を含む", true, {})
            settingBoolElem("負の数を含む", true, {})
            settingIntElem("ネストの深さ", 10, {})
            settingIntElem("数式の最大の長さ(文字)", 200, {})
            Box(Modifier.weight(1f))
            ElevatedButton(onClick = {  },
                modifier = Modifier.width(300.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A5D9)),
                shape = RoundedCornerShape(size = 5.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(5.dp)
            ) {
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
        OutlinedTextField(value = value.toString(), onValueChange = { onValueChange(it.toInt()) }, modifier = Modifier.width(70.dp).height(45.dp))
    }
}