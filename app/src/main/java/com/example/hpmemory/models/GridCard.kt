package com.example.hpmemory.models

data class GridCard(val card :Card, val isFirst:Boolean, var isOpened:Boolean, var isMatched:Boolean = false)
