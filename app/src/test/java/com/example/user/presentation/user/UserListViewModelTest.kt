package com.example.user.presentation.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.user.common.ErrorCode
import com.example.user.common.viewstate.PaginationViewState
import com.example.user.common.viewstate.ViewError
import com.example.user.common.viewstate.ViewState
import com.example.user.domain.Either
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserDetailData
import com.example.user.domain.model.UserParam
import com.example.user.domain.usecase.UserDetailUseCase
import com.example.user.domain.usecase.UserUseCase
import com.example.user.getTestObserver
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

class UserListViewModelTest : KoinTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val viewModel by inject<UserListViewModel>()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var userUseCase: UserUseCase

    @Mock
    private lateinit var userDetailUseCase: UserDetailUseCase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(module {
                single { UserListViewModel(userUseCase, userDetailUseCase) }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `on get users success`() {
        runBlockingTest {
            BDDMockito.given(userUseCase.invoke(UserParam(10, 0))).willReturn(
                Either.Success(listOf(UserData(1, "username", "photo", "url")))
            )
            val expected = listOf(
                PaginationViewState.Loading(),
                PaginationViewState.Success(listOf(UserData(1, "username", "photo", "url")), true)
            )
            val actual = viewModel.userState.getTestObserver().observedValues
            viewModel.getUsers(0)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `on get users error`() {
        runBlockingTest {
            val error = ViewError(ErrorCode.GLOBAL_UNKNOWN_ERROR)
            BDDMockito.given(userUseCase.invoke(UserParam(10, 0))).willReturn(Either.Fail(error))
            val expected = listOf<PaginationViewState<List<UserData>>>(
                PaginationViewState.Loading(),
                PaginationViewState.Error(error)
            )
            val actual = viewModel.userState.getTestObserver().observedValues
            viewModel.getUsers(0)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `on get users empty`() {
        runBlockingTest {
            BDDMockito.given(userUseCase.invoke(UserParam(10, 0)))
                .willReturn(Either.Success(listOf<UserData>()))
            val expected = listOf<PaginationViewState<List<UserData>>>(
                PaginationViewState.Loading(),
                PaginationViewState.EmptyData()
            )
            val actual = viewModel.userState.getTestObserver().observedValues
            viewModel.getUsers(0)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `on get users pagination error`() {
        runBlockingTest {
            val error = ViewError(ErrorCode.GLOBAL_UNKNOWN_ERROR)
            BDDMockito.given(userUseCase.invoke(UserParam(10, 10))).willReturn(Either.Fail(error))
            val expected = listOf<PaginationViewState<List<UserData>>>(
                PaginationViewState.LoadMoreLoading(),
                PaginationViewState.PaginationError(error)
            )
            val actual = viewModel.userState.getTestObserver().observedValues
            viewModel.getUsers(10)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `on get users detail error`() {
        runBlockingTest {
            val error = ViewError(ErrorCode.GLOBAL_UNKNOWN_ERROR)
            BDDMockito.given(userDetailUseCase.invoke("username")).willReturn(Either.Fail(error))
            val expected = listOf<ViewState<UserDetailData>>(
                ViewState.Loading(),
                ViewState.Error(error)
            )
            val actual = viewModel.userDetailState.getTestObserver().observedValues
            viewModel.getUserDetail("username")
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `on get users detail success`() {
        runBlockingTest {
            BDDMockito.given(userDetailUseCase.invoke("username")).willReturn(
                Either.Success(
                    UserDetailData("name", "email", "createdAt")
                )
            )
            val expected = listOf(
                ViewState.Loading(),
                ViewState.Success(UserDetailData("name", "email", "createdAt"))
            )
            val actual = viewModel.userDetailState.getTestObserver().observedValues
            viewModel.getUserDetail("username")
            assertEquals(expected, actual)
        }
    }
}