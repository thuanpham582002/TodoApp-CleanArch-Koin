package com.example.todoappcleanarchwithkoin.di

import androidx.lifecycle.SavedStateHandle
import com.example.core.domain.use_case.TodoUseCase
import com.example.core.domain.use_case.TodoUseCaseImpl
import com.example.todoappcleanarchwithkoin.ui.notification.TodoScheduler
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.AddEditViewModel
import com.example.todoappcleanarchwithkoin.ui.todo.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val useCaseModule = module {
    factory<TodoUseCase> { TodoUseCaseImpl(get()) }
    single {
        SavedStateHandle()
    }
}

val notificationModule = module {
    single { TodoScheduler(androidContext()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { AddEditViewModel(get(), get()) }
}