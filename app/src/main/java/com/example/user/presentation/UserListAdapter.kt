package com.example.user.presentation

import android.view.View
import android.view.ViewGroup
import com.example.user.R
import com.example.user.common.base.BaseHolder
import com.example.user.common.base.BaseLoadingAdapter
import com.example.user.common.extension.inflate
import com.example.user.common.extension.loadImageUrl
import com.example.user.domain.model.UserData
import kotlinx.android.synthetic.main.item_user.view.*

class UserListAdapter : BaseLoadingAdapter<UserData?>() {

    override fun bindViewHolder(holder: BaseHolder<UserData?>, data: UserData?) {
        if (holder is UserHolder) holder.bind()
        else super.bindViewHolder(holder, data)
    }

    override fun onCreateMainViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<UserData?> {
        return UserHolder(parent.inflate(R.layout.item_user, false))
    }

    override fun getLoadingView(): Int {
        return R.layout.view_pagination_loading
    }

    inner class UserHolder(view: View) : BaseHolder<UserData?>(listener, view) {
        fun bind() {
            item?.apply {
                itemView.tv_id.text = id.toString()
                itemView.tv_username.text = username
                itemView.tv_repo.text = repoUrl
                itemView.iv_user.loadImageUrl(photo)
            }
        }
    }
}