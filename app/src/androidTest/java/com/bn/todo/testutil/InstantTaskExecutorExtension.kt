package com.bn.todo.testutil

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.jupiter.api.extension.Extension

class InstantTaskExecutorExtension : InstantTaskExecutorRule(), Extension