package com.realityexpander.statemanagementmistake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.realityexpander.statemanagementmistake.ui.theme.StateManagementMistakeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateManagementMistakeTheme {

                data class MyState(var count: Int)

                val _state = MutableStateFlow(MyState(0))
                val state = _state.collectAsState()
//                 val coroutineScope = CoroutineScope(Dispatchers.Main)
                val coroutineScope = CoroutineScope(Dispatchers.IO)

                fun increment2TimesBAD() {
                    // BAD - doesn't get the current value before updating,
                    //       will cause a race condition.
                    coroutineScope.launch {// count == 0, same as below ⬇️
                        _state.value = _state.value.copy(count = _state.value.count + 1)
                    }
                    coroutineScope.launch {// count == 0, same as above ⬆️
                        _state.value = _state.value.copy(count = _state.value.count + 1)
                    }
                }

                fun increment2TimesGOOD() {
                    // GOOD - gets the current value before updating,
                    //        no race condition is possible.
                    coroutineScope.launch {
                        _state.update { // gets the current value here before updating 😀
                            it.copy(count = it.count + 1)
                        }
                    }
                    coroutineScope.launch {
                        _state.update { // gets the current value here before updating 😃
                            it.copy(count = it.count + 1)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Count= ${state.value.count}",
                        )

                        Button(
                            onClick = {
                                repeat(100) {
//                                    increment2TimesBAD()
                                     increment2TimesGOOD()
                                }
                            }
                        ) {
                            Text("Increment +200")
                        }
                    }
                }
            }
        }
    }
}
