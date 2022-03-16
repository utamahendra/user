package com.example.user.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.user.common.ErrorCode
import com.example.user.common.viewstate.ViewError
import com.example.user.data.remote.response.UserResponse
import com.example.user.domain.Either
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserParam
import com.example.user.domain.source.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.IOException

class UserUseCaseTest : KoinTest {
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val useCase by inject<UserUseCase>()

    @Mock
    private lateinit var repository: UserDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(module {
                single {
                    UserUseCaseImpl(repository) as UserUseCase
                }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `success get users`() {
        runBlockingTest {
            val expected = Either.Success(listOf(UserData(1, "username", "photo", "repo")))
            BDDMockito.given(repository.getUsers(10, 0))
                .willReturn(listOf(UserResponse(1, "username", "photo", "repo")))

            val actual = useCase.invoke(UserParam(10, 0))

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `internal server error get users`() {
        runBlockingTest {
            val expected = Either.Fail(ViewError(ErrorCode.GLOBAL_UNKNOWN_ERROR))
            BDDMockito.given(repository.getUsers(10, 0)).willAnswer { throw RuntimeException() }

            val actual = useCase.invoke(UserParam(10, 0))

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `IO error get users`() {
        runBlockingTest {
            val expected = Either.Fail(ViewError(ErrorCode.GLOBAL_INTERNET_ERROR))
            BDDMockito.given(repository.getUsers(10, 0)).willAnswer { throw IOException() }

            val actual = useCase.invoke(UserParam(10, 0))

            assertEquals(expected, actual)
        }
    }
}