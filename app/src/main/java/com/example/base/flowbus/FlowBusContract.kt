package com.example.base.flowbus

interface BusEvent

data class MainScreenToastEvent(val msg: String) : BusEvent