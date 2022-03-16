package com.example.user.presentation

import android.os.Bundle
import android.widget.Toast
import com.example.user.R
import com.example.user.common.DateFormat
import com.example.user.common.ErrorCode
import com.example.user.common.PaginationListener
import com.example.user.common.base.BaseActivity
import com.example.user.common.extension.gone
import com.example.user.common.extension.setSingleClickListener
import com.example.user.common.extension.toDateFormat
import com.example.user.common.extension.visible
import com.example.user.common.viewbinding.viewBinding
import com.example.user.common.viewstate.PaginationViewState
import com.example.user.common.viewstate.ViewState
import com.example.user.databinding.ActivityUserListBinding
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserDetailData
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListActivity : BaseActivity() {

    private val binding by viewBinding(ActivityUserListBinding::inflate)

    private val viewModel by viewModel<UserListViewModel>()

    private val adapter by lazy { UserListAdapter() }

    private val paginationListener by lazy {
        PaginationListener(10) { itemCount ->
            viewModel.getUsers(itemCount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initListener()
        startObservingData()
        viewModel.getUsers()
    }

    private fun initView() {
        binding.rvUsers.adapter = adapter
    }

    private fun initListener() {
        adapter.setItemClickListener { _, data, _ ->
            viewModel.getUserDetail(data?.username.orEmpty())
        }

        binding.rvUsers.addOnScrollListener(paginationListener)

        binding.srlUsers.apply {
            setOnRefreshListener {
                isRefreshing = false
                adapter.clear()
                viewModel.getUsers()
            }
        }
    }

    private fun startObservingData() {
        viewModel.userDetailState.observe(this) { state ->
            when (state) {
                is ViewState.Loading -> {
                    hideError()
                    showLoadingUserDetail()
                }

                is ViewState.Error -> {
                    hideLoadingUserDetail()
                    showErrorToast()
                }

                is ViewState.Success -> {
                    hideLoadingUserDetail()
                    showUserDetail(state.data)
                }
            }
        }

        viewModel.userState.observe(this) { state ->
            when (state) {
                is PaginationViewState.Loading -> {
                    hideError()
                    showLoading()
                }

                is PaginationViewState.LoadMoreLoading -> showPaginationLoading()

                else -> {
                    hideLoading()
                    hidePaginationLoading()
                    when (state) {
                        is PaginationViewState.Success -> bindView(state.data, state.isLastPage)
                        is PaginationViewState.Error -> showError(state.viewError?.errorCode.toString()) { viewModel.getUsers() }
                        is PaginationViewState.PaginationError -> showErrorToast()
                        is PaginationViewState.EmptyData -> showEmpty()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.srlUsers.gone()
        binding.sflUsers.apply {
            visible()
            startShimmer()
        }
    }

    private fun hideLoading() {
        binding.sflUsers.apply {
            gone()
            stopShimmer()
        }
        binding.srlUsers.visible()
    }

    private fun showPaginationLoading() {
        adapter.showLoadingFooter()
    }

    private fun hidePaginationLoading() {
        adapter.hideLoadingFooter()
    }

    private fun showError(errorCode: String, action: () -> Unit) {
        binding.srlUsers.gone()
        when(errorCode) {
            ErrorCode.GLOBAL_INTERNET_ERROR -> {
                binding.viewError.tvTitle.text = getString(R.string.connection_error)
            }

            else -> {
                binding.viewError.tvTitle.text = getString(R.string.internal_server_error)
            }
        }
        binding.viewError.btnRetry.visible()
        binding.viewError.btnRetry.setSingleClickListener { action.invoke() }
        binding.viewError.viewErrorContainer.visible()
    }

    private fun showEmpty() {
        binding.srlUsers.gone()
        binding.viewError.tvTitle.text = getString(R.string.empty_data)
        binding.viewError.btnRetry.gone()
        binding.viewError.viewErrorContainer.visible()
    }

    private fun hideError() {
        binding.srlUsers.visible()
        binding.viewError.viewErrorContainer.gone()
    }

    private fun bindView(data: List<UserData>, isLastPages: Boolean) {
        hideError()
        paginationListener.apply {
            isLastPage = isLastPages
            itemCount = data.size
        }
        adapter.resetData(data)
        if (isLastPages) adapter.hideLoadingFooter()
    }

    private fun showErrorToast() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingUserDetail() {
        binding.pbLoading.visible()
        binding.srlUsers.gone()
    }

    private fun hideLoadingUserDetail() {
        binding.pbLoading.gone()
        binding.srlUsers.visible()
    }

    private fun showUserDetail(data: UserDetailData) {
        Toast.makeText(this, "name: ${data.name}, email: ${data.email}, createdAt: ${data.createdAt.toDateFormat(
            DateFormat.SERVER_TIME, DateFormat.DATE_FORMAT_WITHOUT_TIME)}", Toast.LENGTH_LONG).show()
    }
}