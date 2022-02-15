package top.ntutn.sparkcompose

import androidx.compose.runtime.mutableStateOf
import top.ntutn.sparkcompose.arch.IViewModel

class MainViewModel: IViewModel {
    val currentTab = mutableStateOf(0)
}