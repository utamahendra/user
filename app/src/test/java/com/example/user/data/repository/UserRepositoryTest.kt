package com.example.user.data.repository

import com.example.user.data.remote.UserApiService
import com.example.user.data.remote.response.UserDetailResponse
import com.example.user.data.remote.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserRepositoryTest : KoinTest {

    @Mock
    lateinit var apiService: UserApiService

    private val repository by inject<UserRepository>()

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(module {
                single { UserRepository(apiService) }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun `on get user list success`() {
        runBlockingTest {
            val expectedResult = listOf(UserResponse(1, "username", "photo", "repo"))
            BDDMockito.given(apiService.getUsers(10, 0)).willReturn(expectedResult)
            val actual = repository.getUsers(10, 0)
            assertEquals(expectedResult, actual)
        }
    }


    @Test
    fun `on get user detail success`() {
        runBlockingTest {
            val expectedResult = UserDetailResponse("name", "email", "createdAt")
            BDDMockito.given(apiService.getUserDetail("username")).willReturn(expectedResult)
            val actual = repository.getUserDetail("username")
            assertEquals(expectedResult, actual)
        }
    }

}