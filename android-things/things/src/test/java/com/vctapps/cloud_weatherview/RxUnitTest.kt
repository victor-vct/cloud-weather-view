package com.vctapps.cloud_weatherview

import org.junit.Rule
import org.mockito.junit.MockitoJUnit

class RxUnitTest {

    @Rule @JvmField
    val rule = MockitoJUnit.rule()!!

    @Rule
    @JvmField var testSchedulerRule = RxSchedulerRule()
}