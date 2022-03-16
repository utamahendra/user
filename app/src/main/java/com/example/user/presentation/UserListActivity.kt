package com.example.user.presentation

import android.os.Bundle
import android.widget.Toast
import com.example.user.R
import com.example.user.common.ErrorCode
import com.example.user.common.PaginationListener
import com.example.user.common.base.BaseActivity
import com.example.user.common.extension.gone
import com.example.user.common.extension.setSingleClickListener
import com.example.user.common.extension.visible
import com.example.user.common.viewbinding.viewBinding
import com.example.user.common.viewstate.PaginationViewState
import com.example.user.databinding.ActivityUserListBinding
import com.example.user.domain.model.UserData
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
        adapter.setItemClickListener { _, _, _ ->

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
                        is PaginationViewState.Error -> showError(state.viewError?.errorCode.toString())
                        is PaginationViewState.PaginationError -> showPaginationError()
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

    private fun showError(errorCode: String) {
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
        binding.viewError.btnRetry.setSingleClickListener { viewModel.getUsers() }
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

    private fun showPaginationError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
    }
}