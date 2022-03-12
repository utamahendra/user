package com.example.user.common.exception

import com.example.user.common.viewstate.ViewError
import java.lang.Exception

data class ViewErrorException(val viewError: ViewError) : Exception()